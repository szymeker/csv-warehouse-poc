package pl.szymeker.csvwarehouse.storage

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import pl.szymeker.csvwarehouse.dimension.DimensionSchema
import pl.szymeker.csvwarehouse.dimension.GroupDimension
import pl.szymeker.csvwarehouse.dimension.TimeDimension
import java.io.File
import java.time.LocalDateTime
import java.util.*

class DataFile(val id: UUID, private val schema: DimensionSchema, private val url: String, private val path: File) {

    private val startDateTime: LocalDateTime

    private val endDateTime: LocalDateTime

    init {
        val dateRanges = csvReader().open(path) {
            readAllWithHeaderAsSequence().map { dateTime(schema.timeDimension, it) }.toSortedSet()
        }

        startDateTime = dateRanges.first()
        endDateTime = dateRanges.last()
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataFile

        if (url != other.url) return false

        return true
    }

    fun isInRange(startDateTime: LocalDateTime?, endDateTime: LocalDateTime?): Boolean {
        val fileOlderThanQueryStartTime = endDateTime?.let { this.startDateTime.isAfter(it) } ?: false
        val fileYoungerThanQueryEndTime = startDateTime?.let { this.endDateTime.isBefore(it) } ?: false
        return !(fileOlderThanQueryStartTime || fileYoungerThanQueryEndTime)
    }

    fun allRows(
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        groups: List<GroupDimension>?,
        filters: Map<GroupDimension, String>?
    ): Sequence<DataRow> {
        val timeDimension = schema.timeDimension

        if (groups != null && groups.none { schema.containsGroupDimension(it) }) {
            return emptySequence()
        }

        return csvReader().readAllWithHeader(path)
            .asSequence()
            .filter { row -> rowDateTimeInRange(dateTime(timeDimension, row), startDateTime, endDateTime) }
            .filter { row -> rowInFilters(filters, row) }
            .map { row -> DataRow(row, schema.timeDimension, schema.metrics) }
    }

    private fun dateTime(timeDimension: TimeDimension, row: Map<String, String>) =
        timeDimension.parse(row[timeDimension.columnName])

    private fun rowDateTimeInRange(
        time: LocalDateTime,
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?
    ): Boolean {
        val start = if (startDateTime != null) time.isEqual(startDateTime) || time.isAfter(startDateTime) else true
        val end = if (endDateTime != null) time.isBefore(endDateTime) else true
        return start && end
    }

    private fun rowInFilters(
        filters: Map<GroupDimension, String>?,
        row: Map<String, String>
    ) = filters?.all { filter -> row[filter.key.columnName].equals(filter.value) } ?: true
}
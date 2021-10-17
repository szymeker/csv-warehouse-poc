package pl.szymeker.csvwarehouse.query

import pl.szymeker.csvwarehouse.dimension.GroupDimension
import pl.szymeker.csvwarehouse.dimension.MetricDimension
import pl.szymeker.csvwarehouse.dimension.MetricDimensionType
import pl.szymeker.csvwarehouse.storage.DataRow
import pl.szymeker.csvwarehouse.storage.holder.DataHolder
import java.time.LocalDateTime

class QueryEngine(private val dataHolder: DataHolder) {

    fun query(
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        groupByTime: Boolean?,
        groups: List<GroupDimension>?,
        filters: Map<GroupDimension, String>?,
        metricColumns: Set<String>?
    ): Iterable<Response> {
        return dataHolder.allFilesMatching(startDateTime, endDateTime)
            .flatMap { file -> file.allRows(startDateTime, endDateTime, groups, filters) }
            .groupingBy { row ->
                AggregationKey(
                    if (groupByTime == true) row.timeValue() else null,
                    groups?.associateWith { row.dimensionValue(it) } ?: mapOf())
            }
            .fold(
                { _: AggregationKey, _: DataRow -> AggregationValue(mutableMapOf()) },
                { _, accumulator, element -> element.aggregateAllTo(accumulator) })
            .map { Response(it.key.time, it.key.dimensions, enhanceMetrics(it, metricColumns)) }
    }

    data class Response(
        val timeDimension: LocalDateTime?,
        val dimensions: Map<GroupDimension, String>,
        val metrics: Map<MetricDimension, Number>
    )


    private fun enhanceMetrics(
        it: Map.Entry<AggregationKey, AggregationValue>,
        metricColumns: Set<String>?
    ): Map<MetricDimension, Number> {
        if (metricColumns.isNullOrEmpty()) {
            return it.value.metrics
        }

        if (metricColumns.contains("CTR")) {
            val clicks = it.value.metrics[MetricDimension("Clicks", MetricDimensionType.LONG)]?.toLong()
            val impressions = it.value.metrics[MetricDimension("Impressions", MetricDimensionType.LONG)]?.toLong()

            it.value.metrics[MetricDimension("CTR", MetricDimensionType.DOUBLE)] = ctr(clicks, impressions)
        }

        return it.value.metrics.filterKeys { metricColumns.contains(it.columnName) }
    }

    private fun ctr(clicks: Long?, impressions: Long?): Double {
        if (clicks == null || impressions == null) {
            return Double.NaN
        }

        if (impressions == 0L) {
            return 0.0
        }

        return clicks.toDouble() / impressions.toDouble()
    }
}
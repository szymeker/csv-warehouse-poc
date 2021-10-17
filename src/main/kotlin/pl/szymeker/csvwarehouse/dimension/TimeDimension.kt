package pl.szymeker.csvwarehouse.dimension

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TimeDimension(val columnName: String, private val timeFormat: String) {

    private val formatter = DateTimeFormatter.ofPattern(timeFormat)

    fun parse(value: String?): LocalDateTime =
        runCatching { LocalDateTime.parse(value, formatter) }
            .getOrElse { LocalDate.parse(value, formatter).atTime(0, 0) }
}
package pl.szymeker.csvwarehouse.storage

import pl.szymeker.csvwarehouse.dimension.GroupDimension
import pl.szymeker.csvwarehouse.dimension.MetricDimension
import pl.szymeker.csvwarehouse.dimension.TimeDimension
import pl.szymeker.csvwarehouse.query.AggregationValue

data class DataRow(
    private val source: Map<String, String>,
    private val timeDimension: TimeDimension,
    private val metricDimensions: List<MetricDimension>
) {

    fun dimensionValue(column: GroupDimension) = source[column.columnName] ?: ""

    fun timeValue() = timeDimension.parse(source[timeDimension.columnName])

    fun aggregateAllTo(accumulator: AggregationValue): AggregationValue {
        metricDimensions.forEach { metricDimension ->
            accumulator.merge(metricDimension, source[metricDimension.columnName])
        }
        return accumulator
    }
}
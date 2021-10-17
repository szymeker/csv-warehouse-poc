package pl.szymeker.csvwarehouse.query

import pl.szymeker.csvwarehouse.dimension.MetricDimension

data class AggregationValue(val metrics: MutableMap<MetricDimension, Number>) {

    fun merge(metricDimension: MetricDimension, value: String?) {
        metrics.merge(metricDimension, metricDimension.value(value)) { n1, n2 -> metricDimension.merge(n1, n2) }
    }
}

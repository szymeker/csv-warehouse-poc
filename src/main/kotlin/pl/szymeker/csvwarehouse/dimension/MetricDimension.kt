package pl.szymeker.csvwarehouse.dimension

data class MetricDimension(val columnName: String, private val type: MetricDimensionType) {

    fun value(s: String?): Number {
        return when (type) {
            MetricDimensionType.DOUBLE -> s?.toDouble() ?: 0.0
            MetricDimensionType.LONG -> s?.toLong() ?: 0.0
        }
    }

    fun merge(n1: Number, n2: Number): Number {
        return when (type) {
            MetricDimensionType.DOUBLE -> n1.toDouble() + n2.toDouble()
            MetricDimensionType.LONG -> n1.toLong() + n2.toLong()
        }
    }

    override fun toString(): String {
        return columnName
    }
}
package pl.szymeker.csvwarehouse.dimension

data class DimensionSchema(
    val timeDimension: TimeDimension,
    val groupDimensions: List<GroupDimension>,
    val metrics: List<MetricDimension>
) {
    fun containsGroupDimension(dimension: GroupDimension) =
        groupDimensions.any { it.columnName == dimension.columnName }
}
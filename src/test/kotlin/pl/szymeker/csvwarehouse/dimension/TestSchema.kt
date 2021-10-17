package pl.szymeker.csvwarehouse.dimension

fun workingPath() = "src/test/resources/junit"

fun testFilePath() = "src/test/resources/testFile.csv"

fun testFile2Path() = "src/test/resources/testFile2.csv"


fun testSchema() = DimensionSchema(
    TimeDimension("Daily", "M/d/yy"),
    listOf(
        datasourceDimension(),
        campaignDimension()
    ),
    listOf(
        clicksDimension(),
        impressionsDimension()
    )
)

fun impressionsDimension() = MetricDimension("Impressions", MetricDimensionType.LONG)

fun clicksDimension() = MetricDimension("Clicks", MetricDimensionType.LONG)

fun ctrDimension() = MetricDimension("CTR", MetricDimensionType.DOUBLE)

fun campaignDimension() = GroupDimension("Campaign")

fun datasourceDimension() = GroupDimension("Datasource")
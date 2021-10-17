package pl.szymeker.csvwarehouse.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.szymeker.csvwarehouse.dimension.GroupDimension
import pl.szymeker.csvwarehouse.dimension.MetricDimension
import pl.szymeker.csvwarehouse.dimension.MetricDimensionType
import pl.szymeker.csvwarehouse.dimension.TimeDimension
import pl.szymeker.csvwarehouse.query.AggregationValue
import java.time.LocalDateTime

internal class DataRowTest {

    private val dataRow = DataRow(
        mapOf(
            "time" to "12/08/19",
            "1" to "10",
            "2" to "15"
        ),
        TimeDimension("time", "M/d/yy"),
        listOf(
            MetricDimension("1", MetricDimensionType.LONG),
            MetricDimension("2", MetricDimensionType.LONG),
            MetricDimension("3", MetricDimensionType.LONG)
        )
    )

    @Test
    internal fun `should provide dimension value`() {
        assertThat(dataRow.dimensionValue(GroupDimension("1"))).isEqualTo("10")
        assertThat(dataRow.dimensionValue(GroupDimension("2"))).isEqualTo("15")
        assertThat(dataRow.dimensionValue(GroupDimension("3"))).isEmpty()
    }

    @Test
    internal fun `should provide time value`() {
        assertThat(dataRow.timeValue()).isEqualTo(LocalDateTime.of(2019, 12, 8, 0, 0))
    }

    @Test
    internal fun `should aggregate all metrics`() {
        //given
        val accumulator = AggregationValue(mutableMapOf())

        //when
        dataRow.aggregateAllTo(accumulator)

        //then
        assertThat(accumulator.metrics[MetricDimension("1", MetricDimensionType.LONG)]).isEqualTo(10L)
        assertThat(accumulator.metrics[MetricDimension("2", MetricDimensionType.LONG)]).isEqualTo(15L)
    }
}
package pl.szymeker.csvwarehouse.dimension

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MetricDimensionTest {

    @Test
    internal fun `should return value for DOUBLE column`() {
        //given
        val dimension = MetricDimension("Test column", MetricDimensionType.DOUBLE)

        //when-then
        assertThat(dimension.value("2.543")).isEqualTo(2.543)
    }

    @Test
    internal fun `should return value for LONG column`() {
        //given
        val dimension = MetricDimension("Test column", MetricDimensionType.LONG)

        //when-then
        assertThat(dimension.value("${Long.MAX_VALUE}")).isEqualTo(Long.MAX_VALUE)
    }

    @Test
    internal fun `should merge two DOUBLE numbers`() {
        //given
        val dimension = MetricDimension("Test column", MetricDimensionType.DOUBLE)

        //when
        assertThat(dimension.merge(0.5, 0.5)).isEqualTo(1.0)
    }

    @Test
    internal fun `should merge two LONG numbers`() {
        //given
        val dimension = MetricDimension("Test column", MetricDimensionType.LONG)

        //when
        assertThat(dimension.merge(100L, Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE + 100L)
    }
}
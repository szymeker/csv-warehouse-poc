package pl.szymeker.csvwarehouse.dimension

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Month

internal class TimeDimensionTest {

    @Test
    internal fun `should parse local date`() {
        //given
        val dimension = TimeDimension("Test column", "M/d/yy")

        //when
        val dateTime = dimension.parse("12/05/19")

        //then
        assertThat(dateTime.month).isEqualTo(Month.DECEMBER)
        assertThat(dateTime.dayOfMonth).isEqualTo(5)
        assertThat(dateTime.year).isEqualTo(2019)
    }

    @Test
    internal fun `should parse local datetime`() {
        //given
        val dimension = TimeDimension("Test column", "yyyy-MM-dd'T'HH:mm:ss")

        //when
        val dateTime = dimension.parse("1991-03-14T15:30:06")

        //then
        assertThat(dateTime.month).isEqualTo(Month.MARCH)
        assertThat(dateTime.dayOfMonth).isEqualTo(14)
        assertThat(dateTime.year).isEqualTo(1991)
        assertThat(dateTime.hour).isEqualTo(15)
        assertThat(dateTime.minute).isEqualTo(30)
        assertThat(dateTime.second).isEqualTo(6)
    }
}
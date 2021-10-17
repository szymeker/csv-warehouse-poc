package pl.szymeker.csvwarehouse.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.szymeker.csvwarehouse.dimension.*
import java.io.File
import java.time.LocalDateTime
import java.util.*

internal class DataFileTest {

    private val testfile = DataFile(UUID.randomUUID(), testSchema(), "", File(testFilePath()))

    @Test
    internal fun `should not be in range when file older than query range`() {
        //given
        val startTime = LocalDateTime.of(2019, 11, 10, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 11, 0, 0)

        //when-then
        assertThat(testfile.isInRange(startTime, endTime)).isFalse
    }

    @Test
    internal fun `should not be in range when file newer than query range`() {
        //given
        val startTime = LocalDateTime.of(2019, 11, 22, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 23, 0, 0)

        //when-then
        assertThat(testfile.isInRange(startTime, endTime)).isFalse
    }

    @Test
    internal fun `should be in range when file wider than query range`() {
        //given
        val startTime = LocalDateTime.of(2019, 11, 13, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 14, 0, 0)

        //when-then
        assertThat(testfile.isInRange(startTime, endTime)).isTrue
    }

    @Test
    internal fun `should be in range when file narrower than query range`() {
        //given
        val startTime = LocalDateTime.of(2019, 11, 1, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 30, 0, 0)

        //when-then
        assertThat(testfile.isInRange(startTime, endTime)).isTrue
    }

    @Test
    internal fun `should return all rows in date range`() {
        //given
        val startTime = LocalDateTime.of(2019, 11, 13, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 15, 0, 0)

        //when
        val allRows = testfile.allRows(startTime, endTime, null, null).toList()

        //then
        assertThat(allRows).hasSize(2)
        with(allRows[0]) {
            assertThat(this.timeValue()).isEqualTo(LocalDateTime.of(2019, 11, 13, 0, 0))
            assertThat(this.dimensionValue(datasourceDimension())).isEqualTo("Google Ads")
            assertThat(this.dimensionValue(campaignDimension())).isEqualTo("Adventmarkt Touristik")
        }
        with(allRows[1]) {
            assertThat(this.timeValue()).isEqualTo(LocalDateTime.of(2019, 11, 14, 0, 0))
            assertThat(this.dimensionValue(datasourceDimension())).isEqualTo("Google Ads")
            assertThat(this.dimensionValue(campaignDimension())).isEqualTo("Adventmarkt")
        }
    }

    @Test
    internal fun `should return empty if unknown groups`() {
        //given
        val startTime = LocalDateTime.of(2019, 11, 13, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 15, 0, 0)
        val groups = listOf(GroupDimension("Some new column"))

        //when
        val allRows = testfile.allRows(startTime, endTime, groups, null).toList()

        //then
        assertThat(allRows).isEmpty()
    }

    @Test
    internal fun `should filter rows`() {
        //given
        val startTime = LocalDateTime.of(2019, 11, 13, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 15, 0, 0)
        val filters = mapOf(campaignDimension() to "Adventmarkt")

        //when
        val allRows = testfile.allRows(startTime, endTime, null, filters).toList()

        //then
        assertThat(allRows).hasSize(1)
        with(allRows[0]) {
            assertThat(this.timeValue()).isEqualTo(LocalDateTime.of(2019, 11, 14, 0, 0))
            assertThat(this.dimensionValue(datasourceDimension())).isEqualTo("Google Ads")
            assertThat(this.dimensionValue(campaignDimension())).isEqualTo("Adventmarkt")
        }
    }
}
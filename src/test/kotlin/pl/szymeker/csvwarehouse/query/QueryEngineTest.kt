package pl.szymeker.csvwarehouse.query

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import pl.szymeker.csvwarehouse.dimension.*
import pl.szymeker.csvwarehouse.storage.DataFile
import pl.szymeker.csvwarehouse.storage.holder.DataHolder
import java.io.File
import java.time.LocalDateTime
import java.util.*

internal class QueryEngineTest {

    private val holder = mock<DataHolder>()

    private val testfile = DataFile(UUID.randomUUID(), testSchema(), "", File(testFilePath()))

    private val testfile2 = DataFile(UUID.randomUUID(), testSchema(), "", File(testFile2Path()))


    @Test
    internal fun `should respond with empty when no matching files`() {
        //given
        whenever(holder.allFilesMatching(any(), any())).thenReturn(sequenceOf())
        val engine = QueryEngine(holder)

        //and query
        val startTime = LocalDateTime.of(2019, 11, 10, 0, 0)
        val endTime = LocalDateTime.of(2019, 11, 11, 0, 0)
        val groupByTime = false
        val groups = null
        val filters = null
        val metricColumns = null

        //when
        val response = engine.query(startTime, endTime, groupByTime, groups, filters, metricColumns)

        //then
        assertThat(response).isEmpty()
    }

    @Test
    internal fun `should respond grouping by dimension`() {
        //given
        whenever(holder.allFilesMatching(any(), any())).thenReturn(sequenceOf(testfile, testfile2))
        val engine = QueryEngine(holder)

        //and query
        val startTime = LocalDateTime.of(2019, 11, 1, 0, 0)
        val endTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val groupByTime = false
        val groups = listOf(datasourceDimension())
        val filters = null
        val metricColumns = null

        //when
        val response = engine.query(startTime, endTime, groupByTime, groups, filters, metricColumns).toList()

        //then
        assertThat(response).hasSize(3)
        with(response[0]) {
            assertThat(this.timeDimension).isNull()
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 120L), entry(impressionsDimension(), 1200L))
        }
        with(response[1]) {
            assertThat(this.timeDimension).isNull()
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Facebook"))
            assertThat(this.metrics).containsOnly(
                entry(clicksDimension(), 1200L),
                entry(impressionsDimension(), 12000L)
            )
        }
        with(response[2]) {
            assertThat(this.timeDimension).isNull()
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Instagram"))
            assertThat(this.metrics).containsOnly(
                entry(clicksDimension(), 20000L),
                entry(impressionsDimension(), 200000L)
            )
        }
    }

    @Test
    internal fun `should respond grouping and filtering by dimension`() {
        //given
        whenever(holder.allFilesMatching(any(), any())).thenReturn(sequenceOf(testfile, testfile2))
        val engine = QueryEngine(holder)

        //and query
        val startTime = LocalDateTime.of(2019, 11, 1, 0, 0)
        val endTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val groupByTime = false
        val groups = listOf(datasourceDimension())
        val filters = mapOf(datasourceDimension() to "Google Ads")
        val metricColumns = null

        //when
        val response = engine.query(startTime, endTime, groupByTime, groups, filters, metricColumns).toList()

        //then
        assertThat(response).hasSize(1)
        with(response[0]) {
            assertThat(this.timeDimension).isNull()
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 120L), entry(impressionsDimension(), 1200L))
        }
    }

    @Test
    internal fun `should respond grouping and filtering by dimension and grouping by time`() {
        //given
        whenever(holder.allFilesMatching(any(), any())).thenReturn(sequenceOf(testfile, testfile2))
        val engine = QueryEngine(holder)

        //and query
        val startTime = LocalDateTime.of(2019, 11, 1, 0, 0)
        val endTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val groupByTime = true
        val groups = listOf(datasourceDimension())
        val filters = mapOf(datasourceDimension() to "Google Ads")
        val metricColumns = null

        //when
        val response = engine.query(startTime, endTime, groupByTime, groups, filters, metricColumns).toList()

        //then
        assertThat(response).hasSize(6)
        with(response[0]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 11, 12, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 10L), entry(impressionsDimension(), 100L))
        }
        with(response[1]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 11, 13, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 20L), entry(impressionsDimension(), 200L))
        }
        with(response[2]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 11, 14, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 30L), entry(impressionsDimension(), 300L))
        }
        with(response[3]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 12, 12, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 10L), entry(impressionsDimension(), 100L))
        }
        with(response[4]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 12, 13, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 20L), entry(impressionsDimension(), 200L))
        }
        with(response[5]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 12, 14, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(clicksDimension(), 30L), entry(impressionsDimension(), 300L))
        }
    }

    @Test
    internal fun `should respond with CTR if requested`() {
        //given
        whenever(holder.allFilesMatching(any(), any())).thenReturn(sequenceOf(testfile, testfile2))
        val engine = QueryEngine(holder)

        //and query
        val startTime = LocalDateTime.of(2019, 11, 1, 0, 0)
        val endTime = LocalDateTime.of(2019, 12, 1, 0, 0)
        val groupByTime = true
        val groups = listOf(datasourceDimension())
        val filters = mapOf(datasourceDimension() to "Google Ads")
        val metricColumns = setOf("CTR")

        //when
        val response = engine.query(startTime, endTime, groupByTime, groups, filters, metricColumns).toList()

        //then
        assertThat(response).hasSize(3)
        with(response[0]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 11, 12, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(ctrDimension(), 0.1))
        }
        with(response[1]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 11, 13, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(ctrDimension(), 0.1))
        }
        with(response[2]) {
            assertThat(this.timeDimension).isEqualTo(LocalDateTime.of(2019, 11, 14, 0, 0))
            assertThat(this.dimensions).containsOnly(entry(datasourceDimension(), "Google Ads"))
            assertThat(this.metrics).containsOnly(entry(ctrDimension(), 0.1))
        }

    }
}
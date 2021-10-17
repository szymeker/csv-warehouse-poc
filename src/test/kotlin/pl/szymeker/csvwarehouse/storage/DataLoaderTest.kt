package pl.szymeker.csvwarehouse.storage

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import pl.szymeker.csvwarehouse.dimension.testFilePath
import pl.szymeker.csvwarehouse.dimension.testSchema
import pl.szymeker.csvwarehouse.dimension.workingPath
import pl.szymeker.csvwarehouse.storage.holder.DataHolder
import java.io.File
import java.io.FileInputStream

internal class DataLoaderTest {

    private val workingPath = workingPath()

    @BeforeEach
    internal fun setUp() {
        File(workingPath).mkdir()
    }

    @AfterEach
    internal fun tearDown() {
        File(workingPath).deleteRecursively()
    }

    @Test
    internal fun `should load file`() {
        //given
        val holder = mock<DataHolder>()
        val loader = DataLoader(workingPath, holder) { FileInputStream(it) }

        //when
        loader.loadData(testFilePath(), testSchema())

        //then
        verify(holder).add(argThat { file -> assertFile(file) })
    }

    private fun assertFile(file: DataFile?): Boolean {
        return File(workingPath + "/" + file?.id).readLines() == File(testFilePath()).readLines()
    }
}
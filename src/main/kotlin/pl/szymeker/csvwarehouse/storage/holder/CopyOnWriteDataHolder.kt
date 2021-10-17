package pl.szymeker.csvwarehouse.storage.holder

import pl.szymeker.csvwarehouse.storage.DataFile
import java.time.LocalDateTime
import java.util.concurrent.CopyOnWriteArraySet

class CopyOnWriteDataHolder : DataHolder {

    private val loadedFiles: MutableSet<DataFile> = CopyOnWriteArraySet()

    override fun add(dataFile: DataFile) = loadedFiles.add(dataFile)

    override fun allFilesMatching(startDateTime: LocalDateTime?, endDateTime: LocalDateTime?): Sequence<DataFile> {
        return loadedFiles.asSequence().filter { file -> file.isInRange(startDateTime, endDateTime) }
    }
}
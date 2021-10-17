package pl.szymeker.csvwarehouse.storage.holder

import pl.szymeker.csvwarehouse.storage.DataFile
import java.time.LocalDateTime

interface DataHolder {
    fun add(dataFile: DataFile): Boolean

    fun allFilesMatching(startDateTime: LocalDateTime?, endDateTime: LocalDateTime?): Sequence<DataFile>
}
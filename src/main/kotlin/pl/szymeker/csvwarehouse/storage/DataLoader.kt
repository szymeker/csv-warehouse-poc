package pl.szymeker.csvwarehouse.storage

import pl.szymeker.csvwarehouse.dimension.DimensionSchema
import pl.szymeker.csvwarehouse.storage.holder.DataHolder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class DataLoader(
    private val workingPath: String,
    private val dataHolder: DataHolder,
    private val inputStreamFunction: (String) -> InputStream
) {

    fun loadData(fileUrl: String, schema: DimensionSchema): Boolean {
        val fileId = UUID.randomUUID()
        val file = File("$workingPath/$fileId")

        inputStreamFunction.invoke(fileUrl).use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return dataHolder.add(DataFile(fileId, schema, fileUrl, file))
    }
}
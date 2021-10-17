package pl.szymeker.csvwarehouse.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szymeker.csvwarehouse.dimension.*
import pl.szymeker.csvwarehouse.storage.DataLoader

@RestController
class LoadDataController(private val dataLoader: DataLoader) {

    @PostMapping("/loadDataFile")
    fun loadDataFile(
        @RequestParam("fileUrl") fileUrl: String,
        @RequestParam("timeColumn") timeColumn: String,
        @RequestParam("timeFormat") timeFormat: String,
        @RequestParam("dimensionColumns") groupColumns: List<String>,
        @RequestParam("metricColumns") metricColumns: List<String>,
        @RequestParam("metricColumnTypes") metricColumnTypes: List<String>,
    ): ResponseEntity<String> = try {
        if (metricColumns.size != metricColumnTypes.size) {
            ResponseEntity.badRequest().body("All metric columns should have specified type")
        }

        val schema = DimensionSchema(
            TimeDimension(timeColumn, timeFormat),
            groupColumns.map { GroupDimension(it) },
            metricColumns.mapIndexed { index, it ->
                MetricDimension(
                    it,
                    MetricDimensionType.valueOf(metricColumnTypes[index])
                )
            }
        )

        if (dataLoader.loadData(fileUrl, schema)) {
            ResponseEntity.ok("Loaded")
        } else {
            ResponseEntity.badRequest().body("Already loaded")
        }
    } catch (e: Exception) {
        ResponseEntity.internalServerError().body(e.message)
    }
}
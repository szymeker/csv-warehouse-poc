package pl.szymeker.csvwarehouse.controller

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.szymeker.csvwarehouse.dimension.GroupDimension
import pl.szymeker.csvwarehouse.query.QueryEngine
import java.time.LocalDateTime

@RestController
class QueryDataController(private val queryEngine: QueryEngine) {

    @GetMapping(path = ["/query"], produces = ["application/json"])
    fun query(
        @RequestParam("metrics", required = false)
        metrics: Set<String>?,

        @RequestParam("filters", required = false)
        filters: List<String>?,

        @RequestParam("startDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        startDateTime: LocalDateTime?,

        @RequestParam("endDateTime", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        endDateTime: LocalDateTime?,

        @RequestParam("groupColumns", required = false)
        groups: Set<String>?,

        @RequestParam("groupByTime", required = false)
        groupByTime: Boolean?
    ): Iterable<QueryEngine.Response> {
        val groupDimensions = groups?.map { GroupDimension(it) }
        val filterDimensions = parseFilters(filters)

        return queryEngine.query(startDateTime, endDateTime, groupByTime, groupDimensions, filterDimensions, metrics)
    }

    private fun parseFilters(filters: List<String>?): Map<GroupDimension, String>? {
        return filters?.associate { filter ->
            val filterParams = filter.split("=")
            GroupDimension(filterParams[0]) to filterParams[1]
        }
    }

}
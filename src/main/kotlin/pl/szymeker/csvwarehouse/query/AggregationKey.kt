package pl.szymeker.csvwarehouse.query

import pl.szymeker.csvwarehouse.dimension.GroupDimension
import java.time.LocalDateTime

data class AggregationKey(val time: LocalDateTime?, val dimensions: Map<GroupDimension, String>)

package pl.szymeker.csvwarehouse.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szymeker.csvwarehouse.query.QueryEngine
import pl.szymeker.csvwarehouse.storage.holder.CopyOnWriteDataHolder

@Configuration
class QueryConfiguration {

    @Bean
    fun queryEngine(dataHolder: CopyOnWriteDataHolder) = QueryEngine(dataHolder)
}
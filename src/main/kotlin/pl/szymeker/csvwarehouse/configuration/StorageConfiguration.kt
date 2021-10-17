package pl.szymeker.csvwarehouse.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szymeker.csvwarehouse.storage.DataLoader
import pl.szymeker.csvwarehouse.storage.holder.CopyOnWriteDataHolder
import java.net.URL

@Configuration
class StorageConfiguration {
    @Bean
    fun dataHolder() = CopyOnWriteDataHolder()

    @Bean
    fun dataLoader(dataHolder: CopyOnWriteDataHolder) = DataLoader(".", dataHolder) { URL(it).openStream() }
}
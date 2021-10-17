package pl.szymeker.csvwarehouse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CsvWarehouseApplication

fun main(args: Array<String>) {
    runApplication<CsvWarehouseApplication>(*args)
}

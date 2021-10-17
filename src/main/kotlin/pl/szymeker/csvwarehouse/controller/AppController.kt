package pl.szymeker.csvwarehouse.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppController {

    @GetMapping(path = ["/healthcheck"])
    fun healthcheck() = "OK"
}
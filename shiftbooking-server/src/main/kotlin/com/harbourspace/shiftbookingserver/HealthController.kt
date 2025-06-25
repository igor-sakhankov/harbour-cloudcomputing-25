package com.harbourspace.shiftbookingserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/health")
    fun health() = "OK"

    @GetMapping("/health2")
    fun health2() = "OK"
}

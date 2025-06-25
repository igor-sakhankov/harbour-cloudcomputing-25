package com.harbourspace.shiftbookingserver

import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController(private val environment: Environment) {
    @GetMapping("/health")
    fun health() = "OK"

    @GetMapping("/properties/{name}")
    fun properties(@PathVariable name: String): String {
        return environment.getProperty(name) ?: "Property '$name' not found"
    }
}


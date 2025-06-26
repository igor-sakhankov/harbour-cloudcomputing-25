package com.harbourspace.configserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    fun health(): String {
        // This endpoint is used to check the health of the Config Server
        return "{\"status\": \"UP\"}"
    }
}
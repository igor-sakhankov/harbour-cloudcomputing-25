package com.harbourspace.client

import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

@Service
class ShiftSender {

    private val restTemplate = RestTemplate()

    fun sendShift(shift: ShiftVm) {
        var attempts = 0
        while (attempts < 5) {
            try {
                restTemplate.postForObject(
                    "http://localhost:8181/shift",
                    shift,
                    String::class.java
                )
                println("Shift sent: $shift")
                return
            } catch (e: HttpServerErrorException) {
                attempts++
                println("Attempt $attempts FAILED: ${e.statusCode}")
                Thread.sleep(1000)
            }
        }
        println("Giving up on $shift after 5 failed attempts")
    }
}

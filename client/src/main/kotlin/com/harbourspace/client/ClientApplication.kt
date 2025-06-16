package com.harbourspace.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.boot.ApplicationRunner

@SpringBootApplication
class ClientApplication {

    @Bean
    fun run(sender: ShiftSender) = ApplicationRunner {
        val shifts = List(10) { index ->
            ShiftVm(
                companyId = "acme-corp",
                userId = "user${index + 1}",
                startTime = "2025-06-15T08:00:00",
                endTime = "2025-06-15T16:00:00",
                action = "add"
            )
        }

        shifts.forEach { shift ->
            sender.sendShift(shift)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ClientApplication>(*args)
}

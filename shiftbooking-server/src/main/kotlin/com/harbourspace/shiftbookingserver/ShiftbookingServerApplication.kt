package com.harbourspace.shiftbookingserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ShiftbookingServerApplication

fun main(args: Array<String>) {
    runApplication<ShiftbookingServerApplication>(*args)
}

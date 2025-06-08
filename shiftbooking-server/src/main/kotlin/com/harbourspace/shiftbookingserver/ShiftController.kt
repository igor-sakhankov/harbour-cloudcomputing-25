package com.harbourspace.shiftbookingserver

import com.harbourspace.shiftbookingserver.interceptor.FailureSimulator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/shifts")
class ShiftController {
    @FailureSimulator
    @GetMapping
    fun listShifts() = listOf("shift1", "shift2")

    @FailureSimulator
    @GetMapping("/{id}")
    fun getShift(@PathVariable id: String) = "shift $id"

    @FailureSimulator
    @PostMapping
    fun createShift() = "created"
}

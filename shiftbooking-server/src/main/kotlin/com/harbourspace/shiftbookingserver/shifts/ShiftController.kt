package com.harbourspace.shiftbookingserver.shifts

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ShiftController(val service: ShiftService) {

    @FailureSimulator
    @PostMapping("/shifts")
    fun modifyShifts(@RequestBody shiftVm: ShiftVm): String {

        val shift = Shift(
            companyId = shiftVm.companyId,
            userId = shiftVm.userId,
            startTime = shiftVm.startTime,
            endTime = shiftVm.endTime
        )
        when (shiftVm.action) {
            ShiftAction.ADD -> service.save(shift)
            ShiftAction.DELETE -> service.delete(shift)
        }

        return "{status: \"ok\"}"
    }

    @GetMapping("/shifts")
    fun getShifts(): ShiftsViewVm {
        val shifts = service.findAll().map { shift ->
            ShiftViewVm(
                shiftId = shift.id,
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
            )
        }
        return ShiftsViewVm(shifts)
    }
}

class ShiftsViewVm(
    val shifts: List<ShiftViewVm>
)

class ShiftVm(
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String,
    val action: ShiftAction
)

class ShiftViewVm(
    val shiftId: Long,
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String,
)

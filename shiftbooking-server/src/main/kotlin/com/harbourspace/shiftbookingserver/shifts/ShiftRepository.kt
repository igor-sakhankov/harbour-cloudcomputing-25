package com.harbourspace.shiftbookingserver.shifts

import org.springframework.data.jpa.repository.JpaRepository

interface ShiftRepository : JpaRepository<Shift, Long> {

    fun existsShiftsByUserIdAndCompanyIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
        userId: String,
        companyId: String,
        endTime: String,
        startTime: String
    ): Boolean
}
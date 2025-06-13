package com.harbourspace.shiftbookingserver.shifts

import org.springframework.boot.autoconfigure.kafka.KafkaProperties.IsolationLevel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class ShiftService(val repository: ShiftRepository, val userLevelLockRepository: UserLevelLockRepository) {

    @Transactional(isolation= Isolation.SERIALIZABLE)
    fun save(shift: Shift) {

        repository.existsShiftsByUserIdAndCompanyIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            shift.userId, shift.companyId, shift.endTime, shift.startTime
        ).let { exists ->
            if (exists) {
                throw OverlappingShiftException(
                    "Overlapping shift already exists for user ${shift.userId} in company ${shift.companyId} " +
                            "from ${shift.startTime} to ${shift.endTime}"
                )
            }
            repository.save(shift)
        }
    }

    fun delete(shift: Shift) {
        TODO("Not yet implemented")
    }

    fun findAll(): List<Shift> {
        return repository.findAll()
    }
}
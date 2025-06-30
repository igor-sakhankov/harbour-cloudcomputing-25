package com.harbourspace.client.shifts

import org.springframework.data.jpa.repository.JpaRepository

interface ShiftRequestRepository : JpaRepository<ShiftRequestEntity, Long> {
    fun findByStatus(status: ProcessingStatus): List<ShiftRequestEntity>
}


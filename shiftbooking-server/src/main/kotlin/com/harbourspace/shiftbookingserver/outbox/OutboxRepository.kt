package com.harbourspace.shiftbookingserver.outbox

import org.springframework.data.jpa.repository.JpaRepository

interface OutboxRepository : JpaRepository<OutboxMessage, Long> {
    fun findBySentFalse(): List<OutboxMessage>
}

package com.harbourspace.shiftbookingserver.shifts

import com.harbourspace.shiftbookingserver.sharding.ShardContext
import com.harbourspace.shiftbookingserver.outbox.OutboxMessage
import com.harbourspace.shiftbookingserver.outbox.OutboxRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.abs

@Service
class ShiftService(
    private val repository: ShiftRepository,
    private val outboxRepository: OutboxRepository,
    @Value("\${sharding.shards:2}") private val shardCount: Int
) {

    private fun shardForCompany(companyId: String): Int = abs(companyId.hashCode()) % shardCount

    @Transactional
    fun save(shift: Shift): Shift {
        val shard = shardForCompany(shift.companyId)
        ShardContext.setShard(shard)
        try {
            val saved = repository.save(shift)
            outboxRepository.save(OutboxMessage(payload = saved.id.toString()))
            return saved
        } finally {
            ShardContext.clear()
        }
    }

    fun findAll(): List<Shift> {
        val result = mutableListOf<Shift>()
        for (i in 0 until shardCount) {
            ShardContext.setShard(i)
            try {
                result += repository.findAll()
            } finally {
                ShardContext.clear()
            }
        }
        return result
    }

    fun deleteById(id: Long, companyId: String) {
        val shard = shardForCompany(companyId)
        ShardContext.setShard(shard)
        try {
            repository.deleteById(id)
        } finally {
            ShardContext.clear()
        }
    }
}

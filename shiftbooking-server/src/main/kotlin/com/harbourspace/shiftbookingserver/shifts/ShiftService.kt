package com.harbourspace.shiftbookingserver.shifts

import com.harbourspace.shiftbookingserver.sharding.ShardContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class ShiftService(private val repository: ShiftRepository,
                   @Value("\${sharding.shards:2}") private val shardCount: Int) {

    private fun shardForCompany(companyId: String): Int = abs(companyId.hashCode()) % shardCount

    fun save(shift: Shift): Shift {
        val shard = shardForCompany(shift.companyId)
        ShardContext.setShard(shard)
        try {
            return repository.save(shift)
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

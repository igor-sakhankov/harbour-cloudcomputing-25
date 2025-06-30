package com.harbourspace.shiftbookingserver.sharding

object ShardContext {
    private val context = ThreadLocal<Int>()

    fun setShard(shard: Int) {
        context.set(shard)
    }

    fun getShard(): Int? = context.get()

    fun clear() {
        context.remove()
    }
}

package com.harbourspace.shiftbookingserver.sharding

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import javax.sql.DataSource

class ShardRoutingDataSource : AbstractRoutingDataSource() {

    val shards: MutableMap<Any, DataSource> = HashMap()

    override fun determineCurrentLookupKey(): Any? {
        return ShardContext.getShard()
    }

    fun initTargets(targets: Map<Any, DataSource>, defaultKey: Any) {
        this.setTargetDataSources(targets)
        this.setDefaultTargetDataSource(targets[defaultKey]!!)
        this.afterPropertiesSet()
        shards.putAll(targets)
    }
}

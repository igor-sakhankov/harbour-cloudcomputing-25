package com.harbourspace.shiftbookingserver.sharding

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class ShardingConfig {
    @Value("\${sharding.shards:2}")
    private var shardCount: Int = 2

    @Bean
    fun dataSource(): DataSource {
        val routing = ShardRoutingDataSource()
        val targetDataSources = HashMap<Any, javax.sql.DataSource>()
        for (i in 0 until shardCount) {
            val ds = DataSourceBuilder.create().url("jdbc:h2:mem:shard${'$'}i;DB_CLOSE_DELAY=-1").driverClassName("org.h2.Driver").username("sa").password("").build()
            targetDataSources[i] = ds
        }
        routing.initTargets(targetDataSources, 0)
        return routing
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource) = JdbcTemplate(dataSource)
}

package com.harbourspace.shiftbookingserver.sharding

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@Configuration
class ShardInitializer {

    @Bean
    fun initializeShards(dataSource: DataSource): ApplicationRunner = ApplicationRunner {
        val routing = dataSource as ShardRoutingDataSource
        val populator = ResourceDatabasePopulator(ClassPathResource("schema.sql"))
        routing.shards.forEach { (_, ds) ->
            populator.execute(ds)
        }
    }
}

package com.harbourspace.shiftbookingserver.shifts

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.sql.DataSource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.harbourspace.shiftbookingserver.shifts"],
    entityManagerFactoryRef = "shiftsEntityManagerFactory",
    transactionManagerRef = "shiftsTransactionManager"
)
class ShiftsDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    fun shiftsDataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    @Primary
    fun shiftsDataSource(
        @Qualifier("shiftsDataSourceProperties") properties: DataSourceProperties
    ): DataSource = properties.initializeDataSourceBuilder().build()

    @Bean("shiftsEntityManagerFactory")
    @Primary
    fun shiftsEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("shiftsDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean =
        builder
            .dataSource(dataSource)
            .packages("com.harbourspace.shiftbookingserver.shifts")
            .persistenceUnit("shifts")
            .build()

    @Bean
    @Primary
    fun shiftsTransactionManager(
        @Qualifier("shiftsEntityManagerFactory") factory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager = JpaTransactionManager(factory.`object`!!)
}

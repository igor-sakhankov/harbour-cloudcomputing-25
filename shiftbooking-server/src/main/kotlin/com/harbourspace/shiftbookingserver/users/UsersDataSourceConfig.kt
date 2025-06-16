package com.harbourspace.shiftbookingserver.users

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.sql.DataSource
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.harbourspace.shiftbookingserver.users"],
    entityManagerFactoryRef = "usersEntityManagerFactory",
    transactionManagerRef = "usersTransactionManager"
)
class UsersDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "users.datasource")
    fun usersDataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    fun usersDataSource(
        @Qualifier("usersDataSourceProperties") properties: DataSourceProperties
    ): DataSource = properties.initializeDataSourceBuilder().build()

    @Bean("usersEntityManagerFactory")
    fun usersEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("usersDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean =
        builder
            .dataSource(dataSource)
            .packages(User::class.java)
            .persistenceUnit("users")
            .build()

    @Bean
    fun usersTransactionManager(
        @Qualifier("usersEntityManagerFactory") factory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager = JpaTransactionManager(factory.`object`!!)
}

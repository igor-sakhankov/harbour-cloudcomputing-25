package com.harbourspace.shiftbookingserver.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties,
    @Value("\${KAFKA_ACCESS_KEY}") private val accessKey: String,
    @Value("\${KAFKA_SECRET_KEY}") private val secretKey: String
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val props = kafkaProperties.buildConsumerProperties().toMutableMap()

        props["sasl.jaas.config"] =
            "org.apache.kafka.common.security.plain.PlainLoginModule required " +
            "username=\"$accessKey\" password=\"$secretKey\";"

        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}
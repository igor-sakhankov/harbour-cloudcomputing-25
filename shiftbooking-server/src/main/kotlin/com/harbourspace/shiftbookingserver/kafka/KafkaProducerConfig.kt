package com.harbourspace.shiftbookingserver.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties,
    @Value("\${KAFKA_ACCESS_KEY}") private val accessKey: String,
    @Value("\${KAFKA_SECRET_KEY}") private val secretKey: String
) {

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val props = kafkaProperties.buildProducerProperties().toMutableMap()

        props["sasl.jaas.config"] =
            "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                    "username=\"$accessKey\" password=\"$secretKey\";"

        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(producerFactory())
}
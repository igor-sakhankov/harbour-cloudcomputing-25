package com.harbourspace.shiftbookingserver.kafka

import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class MessageProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    fun sendMessage(topic: String, message: String) {
        kafkaTemplate.send(topic, "1", message)
        println("Sent message to $topic: $message")
    }

    fun sendFireAndForget(topic: String, message: String) {
        kafkaTemplate.send(topic, message)
        println("Fire-and-forget: Sent message to $topic")
    }

    fun sendSync(topic: String, message: String) {
        try {
            val result: SendResult<String, String> = kafkaTemplate.send(topic, message).get()
            val metadata: RecordMetadata = result.recordMetadata
            println("Sync: Sent to ${metadata.topic()} [${metadata.partition()}] offset ${metadata.offset()}")
        } catch (ex: Exception) {
            println("Sync send failed: ${ex.message}")
        }
    }

    fun sendAsync(topic: String, message: String) {
        val future: CompletableFuture<SendResult<String, String>> = kafkaTemplate.send(topic, message)

        future.whenComplete { result, ex ->
            if (ex != null) {
                println("Async send failed: ${ex.message}")
            } else {
                val metadata = result.recordMetadata
                println("Async: Sent to ${metadata.topic()} [${metadata.partition()}] offset ${metadata.offset()}")
            }
        }
    }
}
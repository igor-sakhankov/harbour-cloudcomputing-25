package com.harbourspace.shiftbookingserver.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class MyKafkaListener {

    @KafkaListener(topics = ["hw25-lecture-3"], groupId = "hw25-2-group")
    fun listen(message: String) {
        println("Received: $message")
    }


    @KafkaListener(topics = ["hw25-lecture-3"], groupId = "hw25-3-group")
    fun listen2(message: String) {
        println("Received from Consumer group 2: $message")
    }
}
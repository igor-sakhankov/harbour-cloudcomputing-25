package com.harbourspace.shiftbookingserver.kafka

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/kafka")
class KafkaController(private val messageProducer: MessageProducer) {

    @PostMapping("/send")
    fun send(@RequestParam topic: String, @RequestParam message: String): String {
        messageProducer.sendMessage(topic, message)
        return "Sent"
    }
}
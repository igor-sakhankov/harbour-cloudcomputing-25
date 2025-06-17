package com.harbourspace.shiftbookingserver.sqs

import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Component

@Component
class MySqsListener {
//    @SqsListener("\${aws.sqs.queue.name}")
    fun receiveMessage(message: String) {
        println("Received message: $message")
    }
}
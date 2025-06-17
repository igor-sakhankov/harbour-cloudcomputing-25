package com.harbourspace.shiftbookingserver.sqs

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture

@RestController
class CustomSqsController(
    private val sqsAsyncClient: SqsAsyncClient,
    @Value("\${aws.sqs.queue.name}") private val queueUrl: String
) {

    @PostMapping("/sqs/send")
    fun sendToSqs(@RequestBody body: Map<String, Any>): CompletableFuture<String> {
        val messageBody = body.toString()

        val request = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(messageBody)
            .build()

        return sqsAsyncClient.sendMessage(request).thenApply {
            "Message sent. ID: ${it.messageId()}"
        }
    }

    @PostMapping("/sqs/listen")
    fun listenToSqs() {
        val request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(20)
            .visibilityTimeout(3)
            .build()

        val messages = sqsAsyncClient.receiveMessage(request).get().messages()
        for (message in messages) {
            println("Received message: ${message.body()}")
            sleep(5000) // Simulate processing time
            sqsAsyncClient.deleteMessage { builder ->
                builder.queueUrl(queueUrl).receiptHandle(message.receiptHandle())
            }
        }
    }
}
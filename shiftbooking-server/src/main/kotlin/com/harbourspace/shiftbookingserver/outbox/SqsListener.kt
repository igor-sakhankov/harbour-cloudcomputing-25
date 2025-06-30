package com.harbourspace.shiftbookingserver.outbox

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@Service
class SqsListener(
    @Value("\${aws.sqs.queueUrl}") private val queueUrl: String,
    @Value("\${aws.region}") private val region: String
) {
    private val sqsClient: SqsClient = SqsClient.builder().region(Region.of(region)).build()

    @Scheduled(fixedDelay = 5000)
    fun poll() {
        val request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10)
            .waitTimeSeconds(1)
            .build()
        val messages = sqsClient.receiveMessage(request).messages()
        messages.forEach { msg ->
            println("Received SQS message: ${'$'}{msg.body()}")
            sqsClient.deleteMessage(
                DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())
                    .build()
            )
        }
    }
}

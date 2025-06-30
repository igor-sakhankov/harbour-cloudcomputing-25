package com.harbourspace.shiftbookingserver.outbox

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Service
class OutboxProcessor(
    private val outboxRepository: OutboxRepository,
    @Value("\${aws.sqs.queueUrl}") private val queueUrl: String,
    @Value("\${aws.region}") private val region: String
) {

    private val sqsClient: SqsClient = SqsClient.builder().region(Region.of(region)).build()

    @Scheduled(fixedDelay = 5000)
    fun process() {
        val unsent = outboxRepository.findBySentFalse()
        unsent.forEach { msg ->
            val request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(msg.payload)
                .build()
            try {
                sqsClient.sendMessage(request)
                msg.sent = true
                outboxRepository.save(msg)
            } catch (ex: Exception) {
                // leave message for retry
            }
        }
    }
}

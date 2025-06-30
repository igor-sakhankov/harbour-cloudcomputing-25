package com.harbourspace.client.shifts

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Service
class ShiftProcessingService(
    private val shiftRequestRepository: ShiftRequestRepository,
    private val batchRequestRepository: BatchRequestRepository,
    private val httpClient: WebClient
) {
    private val logger = LoggerFactory.getLogger(ShiftProcessingService::class.java)

    @Scheduled(fixedDelay = 5000)
    fun processShifts() {
        val pendingShifts = shiftRequestRepository.findByStatus(ProcessingStatus.PENDING)
        pendingShifts.forEach { shift ->
            logger.info("Processing shift ${'$'}{shift.id} from batch ${'$'}{shift.batch.id}")
            shift.status = ProcessingStatus.IN_PROGRESS
            shiftRequestRepository.save(shift)

            val shiftVm = ShiftRequestVm(
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
                action = shift.action
            )

            var success = false
            while (!success) {
                try {
                    httpClient.post()
                        .uri("/shifts")
                        .bodyValue(shiftVm)
                        .retrieve()
                        .bodyToMono(String::class.java)
                        .timeout(Duration.ofSeconds(5))
                        .retryWhen(
                            Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(500))
                                .filter { e -> e !is WebClientResponseException.NotFound }
                        )
                        .block()
                    success = true
                } catch (e: Exception) {
                    logger.error("Error sending shift ${'$'}{shift.id}: ${'$'}{e.message}")
                    Thread.sleep(1000)
                }
            }

            shift.status = ProcessingStatus.COMPLETED
            shiftRequestRepository.save(shift)

            val batch = shift.batch
            val remaining = shiftRequestRepository.findByStatus(ProcessingStatus.PENDING)
                .count { it.batch.id == batch.id }
            if (remaining == 0L) {
                batch.status = ProcessingStatus.COMPLETED
                batchRequestRepository.save(batch)
            }
        }
    }
}

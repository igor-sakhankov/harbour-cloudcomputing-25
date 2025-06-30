package com.harbourspace.client.shifts

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@RestController
class ClientController(
    val httpClient: WebClient,
    val batchRepository: BatchRequestRepository,
    val shiftRequestRepository: ShiftRequestRepository
) {
    private val logger = LoggerFactory.getLogger(ClientController::class.java)

    @GetMapping("/clientshifts")
    fun getShifts(): ShiftsResponseVm? {
        try {
            return httpClient.get()
                .uri("/shifts")
                .retrieve()
                .bodyToMono(ShiftsResponseVm::class.java)
                .timeout(Duration.ofMillis(5000))
                .onErrorResume { error ->
                    logger.error("Error getting shifts: ${error.message}")
                    Mono.just(ShiftsResponseVm(emptyList())) // Return empty shifts on error
                }
                .retryWhen(
                    Retry.backoff(3, Duration.ofMillis(500))
                        .maxBackoff(Duration.ofMillis(2000))
                        .filter { error -> error !is WebClientResponseException.NotFound }
                )
                .block() ?: ShiftsResponseVm(emptyList())
        } catch (e: Exception) {
            logger.error("Exception getting shifts: ${e.message}")
            return ShiftsResponseVm(emptyList())
        }
    }

    @PostMapping("/clientshifts")
    fun modifyShifts(@RequestBody shiftsVm: ShiftsRequestVm): BatchResponseVm {
        logger.info("Received async request with ${shiftsVm.shifts.size} shifts")

        val batch = batchRepository.save(BatchRequest())
        shiftsVm.shifts.forEach { shift ->
            val entity = ShiftRequestEntity(
                batch = batch,
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
                action = shift.action,
                status = ProcessingStatus.PENDING
            )
            shiftRequestRepository.save(entity)
        }

        return BatchResponseVm(batch.id, batch.status.name)
    }

    @GetMapping("/clientshifts/{batchId}")
    fun getBatchStatus(@PathVariable batchId: Long): BatchStatusVm {
        val batch = batchRepository.findById(batchId).orElseThrow()
        val shifts = shiftRequestRepository.findAll()
            .filter { it.batch.id == batchId }
            .map { ShiftStatusVm(it.id, it.status.name) }
        return BatchStatusVm(batch.id, batch.status.name, shifts)
    }
}

// Models for POST requests
class ShiftsRequestVm(
    val shifts: List<ShiftRequestVm>
)

class ShiftRequestVm(
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String,
    val action: String
)

// Models for GET responses
class ShiftsResponseVm(
    val shifts: List<ShiftResponseVm>
)

class ShiftResponseVm(
    val shiftId: Long,
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String
)

// Async batch status responses
class BatchResponseVm(
    val requestId: Long,
    val status: String
)

class BatchStatusVm(
    val requestId: Long,
    val status: String,
    val shifts: List<ShiftStatusVm>
)

class ShiftStatusVm(
    val shiftId: Long,
    val status: String
)
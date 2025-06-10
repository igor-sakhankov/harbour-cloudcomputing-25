package com.harbourspace.client.shifts

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@RestController
class ClientController(val httpClient: WebClient) {
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
    fun modifyShifts(@RequestBody shiftsVm: ShiftsRequestVm): String {
        logger.info("Received request to add ${shiftsVm.shifts.size} shifts")
        
        // Process each shift with unlimited retries until successful
        shiftsVm.shifts.forEach { shift ->
            val shiftVm = ShiftRequestVm(
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
                action = shift.action,
            )
            
            var success = false
            var attempts = 0

            while (!success) {
                attempts++
                try {
                    logger.info("Attempting to add shift for user ${shift.userId} (Attempt #$attempts)")
                    
                    val response = httpClient.post()
                        .uri("/shifts")
                        .bodyValue(shiftVm)
                        .retrieve()
                        .bodyToMono(String::class.java)
                        .onErrorResume { error ->
                            // Log the error but don't propagate it so we can retry
                            logger.error("Error adding shift for user ${shift.userId}: ${error.message}")
                            Mono.empty()
                        }
                        .block(Duration.ofSeconds(5))
                    
                    if (response != null) {
                        success = true
                        logger.info("Successfully added shift for user ${shift.userId} after $attempts attempts")
                    } else {
                        // If response is null, we had an error but caught it, so we need to retry
                        // Wait before retrying to avoid overwhelming the server
                        Thread.sleep(1000)
                        logger.info("Retrying after error for user ${shift.userId}")
                    }
                } catch (e: Exception) {
                    logger.error("Exception during shift booking for user ${shift.userId}: ${e.message}")
                    // Wait a bit longer after exceptions
                    Thread.sleep(2000)
                }
            }
        }

        logger.info("All shifts successfully processed")
        return "{\"status\": \"ok\"}"
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
package com.harbourspace.shiftbookingserver.shifts

import com.harbourspace.shiftbookingserver.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ShiftsExceptionHandler {

    @ExceptionHandler(OverlappingShiftException::class)
    fun handleShiftNotAllowed(ex: OverlappingShiftException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(message = ex.message ?: "Shift not allowed", errorCode = "OVERLAPPING_SHIFT_ERROR")
        return ResponseEntity(body, HttpStatus.CONFLICT)
    }
}
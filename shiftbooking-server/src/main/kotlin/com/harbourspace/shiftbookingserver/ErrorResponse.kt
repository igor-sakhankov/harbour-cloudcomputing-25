package com.harbourspace.shiftbookingserver

data class ErrorResponse(
    val message: String,
    val errorCode: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
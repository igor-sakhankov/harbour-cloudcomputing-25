package com.harbourspace.client

data class ShiftVm(
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String,
    val action: String
)

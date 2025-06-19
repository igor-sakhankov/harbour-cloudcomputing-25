package com.harbourspace.shiftbookingserver.dynamodb

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-purchases")
class UserPurchaseController(
    private val service: UserPurchaseService
) {
    @PostMapping
    fun saveBoth(@RequestBody payload: TransactionRequest): ResponseEntity<String> {
        service.saveUserAndPurchase(payload.user, payload.purchase)
        return ResponseEntity.ok("User and Purchase saved transactionally")
    }
}

data class TransactionRequest(
    val user: User,
    val purchase: Purchase
)
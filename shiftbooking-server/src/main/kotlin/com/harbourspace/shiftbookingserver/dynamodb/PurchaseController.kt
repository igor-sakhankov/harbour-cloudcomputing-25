package com.harbourspace.shiftbookingserver.dynamodb

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/purchases")
class PurchaseController(private val purchaseRepo: PurchaseRepository) {

    @PostMapping
    fun create(@RequestBody purchase: Purchase): ResponseEntity<String> {
        purchaseRepo.save(purchase)
        return ResponseEntity.ok("Purchase saved")
    }

    @GetMapping("/{userId}")
    fun getByUser(@PathVariable userId: String): List<Purchase> {
        return purchaseRepo.listByUser(userId)
    }
}
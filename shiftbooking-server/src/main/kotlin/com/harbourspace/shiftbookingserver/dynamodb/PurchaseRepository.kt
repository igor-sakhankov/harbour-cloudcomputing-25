package com.harbourspace.shiftbookingserver.dynamodb

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

@Repository
class PurchaseRepository(enhancedClient: DynamoDbEnhancedClient) {

    private val purchaseTable = enhancedClient.table("Purchases", TableSchema.fromBean(Purchase::class.java))

    fun save(purchase: Purchase) {
        purchaseTable.putItem(purchase)
    }

    fun getByUserAndPurchase(userId: String, purchaseId: String): Purchase? {
        val key = Key.builder()
            .partitionValue(userId)
            .sortValue(purchaseId)
            .build()
        return purchaseTable.getItem(key)
    }

    fun listByUser(userId: String): List<Purchase> {
        val queryConditional = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(userId).build()
        )

        return purchaseTable.query { builder ->
            builder.queryConditional(queryConditional)
        }.items().asSequence().toList()
    }
}
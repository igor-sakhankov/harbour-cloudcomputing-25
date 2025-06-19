package com.harbourspace.shiftbookingserver.dynamodb

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Service
class UserPurchaseService(
    private val dynamoDbClient: DynamoDbClient
) {
    fun saveUserAndPurchase(user: User, purchase: Purchase) {
        val userItem = mapOf(
            "userId" to AttributeValue.fromS(user.userId),
            "name" to AttributeValue.fromS(user.name),
            "email" to AttributeValue.fromS(user.email)
        )

        val purchaseItem = mapOf(
            "userId" to AttributeValue.fromS(purchase.userId),
            "purchaseId" to AttributeValue.fromS(purchase.purchaseId),
            "item" to AttributeValue.fromS(purchase.item),
            "amount" to AttributeValue.fromN(purchase.amount.toString())
        )

        val transactRequest = TransactWriteItemsRequest.builder()
            .transactItems(
                listOf(
                    TransactWriteItem.builder().put(
                        Put.builder()
                            .tableName("Users")
                            .item(userItem)
                            .build()
                    ).build(),
                    TransactWriteItem.builder().put(
                        Put.builder()
                            .tableName("Purchases")
                            .item(purchaseItem)
                            .build()
                    ).build()
                )
            ).build()

        dynamoDbClient.transactWriteItems(transactRequest)
    }
}
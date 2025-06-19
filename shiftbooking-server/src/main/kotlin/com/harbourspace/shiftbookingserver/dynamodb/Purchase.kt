package com.harbourspace.shiftbookingserver.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
data class Purchase(
    @get:DynamoDbPartitionKey
    var userId: String = "",
    @get:DynamoDbSortKey
    var purchaseId: String = "",

    var item: String = "",
    var amount: Double = 0.0
)
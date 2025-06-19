package com.harbourspace.shiftbookingserver.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*

@DynamoDbBean
data class User(
    @get:DynamoDbPartitionKey
    var userId: String = "",
    var name: String = "",
    var email: String = ""
)
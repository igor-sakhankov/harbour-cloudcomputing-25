package com.harbourspace.shiftbookingserver.dynamodb

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Repository
class UserRepository(enhancedClient: DynamoDbEnhancedClient) {

    private val userTable = enhancedClient.table("Users", TableSchema.fromBean(User::class.java))

    fun saveUser(user: User) {
        userTable.putItem(user)
    }

    fun getUser(userId: String): User? {
        return userTable.getItem(Key.builder().partitionValue(userId).build())

    }

    fun getUsers(): List<User> {
        val scanRequest = ScanEnhancedRequest.builder()
            .build()
        return userTable.scan(scanRequest)
            .items()
            .toList()
    }

    fun getUsersByEmail(email: String): List<User> {
        val expression = Expression.builder()
            .expression("email = :email")
            .putExpressionValue(":email", AttributeValue.fromS(email))
            .build()

        val scanRequest = ScanEnhancedRequest.builder()
            .filterExpression(expression)
            .build()

        return userTable.scan(scanRequest)
            .items()
            .toList()
    }
}
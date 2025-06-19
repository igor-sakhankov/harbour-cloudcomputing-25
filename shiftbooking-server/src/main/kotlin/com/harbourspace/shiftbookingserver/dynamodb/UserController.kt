package com.harbourspace.shiftbookingserver.dynamodb

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val repository: UserRepository) {

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<String> {
        repository.saveUser(user)
        return ResponseEntity.ok("User saved")
    }

    @GetMapping
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(repository.getUsers())
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<User> {
        val user = repository.getUser(id)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }
}
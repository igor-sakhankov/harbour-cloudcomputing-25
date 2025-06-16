package com.harbourspace.shiftbookingserver.users

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    open var id: Long = 0,

    @Column(name = "first_name", nullable = false)
    open var firstName: String = "",

    @Column(name = "last_name", nullable = false)
    open var lastName: String = "",
) {
    // JPA requires a no-arg constructor
    constructor() : this(0, "", "")
}
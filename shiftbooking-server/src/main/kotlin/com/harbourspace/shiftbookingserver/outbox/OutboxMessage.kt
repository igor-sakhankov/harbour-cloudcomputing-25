package com.harbourspace.shiftbookingserver.outbox

import jakarta.persistence.*

@Entity
@Table(name = "outbox_messages")
class OutboxMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(columnDefinition = "TEXT")
    var payload: String,

    var sent: Boolean = false
)

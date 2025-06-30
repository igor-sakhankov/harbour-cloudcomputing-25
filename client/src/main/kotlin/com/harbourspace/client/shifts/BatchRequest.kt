package com.harbourspace.client.shifts

import jakarta.persistence.*

@Entity
@Table(name = "shift_batches")
class BatchRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    var status: ProcessingStatus = ProcessingStatus.PENDING
)

enum class ProcessingStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}


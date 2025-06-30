package com.harbourspace.client.shifts

import jakarta.persistence.*

@Entity
@Table(name = "shift_requests")
class ShiftRequestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "batch_id")
    var batch: BatchRequest,

    var companyId: String,
    var userId: String,
    var startTime: String,
    var endTime: String,
    var action: String,

    @Enumerated(EnumType.STRING)
    var status: ProcessingStatus = ProcessingStatus.PENDING
)


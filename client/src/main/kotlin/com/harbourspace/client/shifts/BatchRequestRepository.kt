package com.harbourspace.client.shifts

import org.springframework.data.jpa.repository.JpaRepository

interface BatchRequestRepository : JpaRepository<BatchRequest, Long>


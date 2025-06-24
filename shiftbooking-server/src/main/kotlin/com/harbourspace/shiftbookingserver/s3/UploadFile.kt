package com.harbourspace.shiftbookingserver.s3

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UploadFile(private val s3Service: S3Service) {

    @GetMapping("/upload-url")
    fun getUploadUrl(): String {
        // This method should return a pre-signed URL for uploading files to S3
        // For now, we return a placeholder string
        return s3Service.generateUploadUrl("group-project")
    }
}
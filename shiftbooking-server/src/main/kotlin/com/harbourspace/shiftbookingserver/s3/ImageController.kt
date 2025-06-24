package com.harbourspace.shiftbookingserver.s3

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
class ImageController(
    private val s3Service: S3Service
) {

    @GetMapping("/")
    fun index(model: Model): String {
        val images = s3Service.listImages().map { s3Service.generateImageUrl(it) }
        model.addAttribute("images", images)
        return "index"
    }

    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile): String {
        s3Service.uploadFile(file)
        return "redirect:/"
    }
}
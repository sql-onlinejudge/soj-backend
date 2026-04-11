package me.suhyun.soj.domain.sandbox.presentation

import me.suhyun.soj.domain.sandbox.application.service.SandboxQueryService
import me.suhyun.soj.domain.sandbox.application.service.SandboxSetupService
import me.suhyun.soj.domain.sandbox.presentation.request.SandboxQueryRequest
import me.suhyun.soj.domain.sandbox.presentation.response.SandboxQueryResponse
import me.suhyun.soj.domain.sandbox.presentation.response.SandboxSessionResponse
import me.suhyun.soj.domain.sandbox.presentation.response.SandboxSetupResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/runs/sandbox")
class SandboxController(
    private val sandboxSetupService: SandboxSetupService,
    private val sandboxQueryService: SandboxQueryService
) {

    @PostMapping("/setup", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun setup(@RequestPart("image") image: MultipartFile): SandboxSetupResponse {
        val userId = SecurityContextHolder.getContext().authentication?.principal as? UUID
        val mediaType = image.contentType ?: "image/jpeg"
        return sandboxSetupService.setup(image.bytes, mediaType, userId)
    }

    @PostMapping("/{sessionKey}/query")
    fun executeQuery(
        @PathVariable sessionKey: String,
        @Valid @RequestBody request: SandboxQueryRequest
    ): SandboxQueryResponse {
        val userId = SecurityContextHolder.getContext().authentication?.principal as UUID
        return sandboxQueryService.executeQuery(sessionKey, request.query, userId)
    }

    @GetMapping("/{sessionKey}")
    fun getSession(@PathVariable sessionKey: String): SandboxSessionResponse {
        val userId = SecurityContextHolder.getContext().authentication?.principal as UUID
        return sandboxQueryService.getSession(sessionKey, userId)
    }
}

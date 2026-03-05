package me.suhyun.soj.global.log

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.UUID

@Component
class ApiLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)
        MDC.put("traceId", UUID.randomUUID().toString())
        val startTime = System.currentTimeMillis()

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val duration = System.currentTimeMillis() - startTime

            val logData = mutableMapOf<String, Any?>(
                "method" to request.method,
                "uri" to request.requestURI,
                "queryString" to request.queryString,
                "statusCode" to wrappedResponse.status,
                "duration" to duration,
                "clientIp" to request.remoteAddr
            )

            if (request.method == "POST" && request.requestURI.matches(Regex("/problems/\\d+/submissions"))) {
                logData["requestBody"] = String(wrappedRequest.contentAsByteArray)
            }

            val jsonLog = ObjectMapper().writeValueAsString(logData)
            if (wrappedResponse.status >= 500) {
                MDC.put("logType", "ERROR")
                log.error(jsonLog)
            } else {
                MDC.put("logType", "API")
                log.info(jsonLog)
            }
            MDC.clear()

            wrappedResponse.copyBodyToResponse()
        }
    }
}
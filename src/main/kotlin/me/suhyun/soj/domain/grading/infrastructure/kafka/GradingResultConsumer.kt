package me.suhyun.soj.domain.grading.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import me.suhyun.soj.domain.grading.infrastructure.sse.SseEmitterService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class GradingResultConsumer(
    private val sseEmitterService: SseEmitterService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["grading-results"], groupId = "grading-result-group")
    fun consume(message: String) {
        val event = objectMapper.readValue(message, GradingResultEvent::class.java)
        sseEmitterService.send(event.submissionId, event.status, event.verdict)
    }
}

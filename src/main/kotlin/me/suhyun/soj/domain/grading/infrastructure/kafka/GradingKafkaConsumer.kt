package me.suhyun.soj.domain.grading.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import me.suhyun.soj.domain.grading.application.service.GradingService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class GradingKafkaConsumer(
    private val gradingService: GradingService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(
        topics = [GradingKafkaProducer.TOPIC],
        groupId = "grading-group",
        concurrency = "\${sandbox.pool.size:10}"
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, GradingEvent::class.java)
        gradingService.grade(event.submissionId)
    }
}

package me.suhyun.soj.domain.grading.infrastructure.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class GradingKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    companion object {
        const val TOPIC = "grading-requests"
    }

    fun send(submissionId: Long) {
        val event = GradingEvent(submissionId)
        kafkaTemplate.send(TOPIC, submissionId.toString(), event)
    }
}

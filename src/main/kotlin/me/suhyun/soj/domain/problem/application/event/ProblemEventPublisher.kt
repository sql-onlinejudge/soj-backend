package me.suhyun.soj.domain.problem.application.event

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProblemEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val TOPIC = "problem-events"
    }

    fun publish(event: ProblemEvent) {
        try {
            kafkaTemplate.send(TOPIC, event.problemId.toString(), event)
            logger.info("Published event: $event to topic: $TOPIC")
        } catch (e: Exception) {
            logger.error("Failed to publish event: $event", e)
        }
    }
}

package me.suhyun.soj.domain.grading.application.event

import me.suhyun.soj.domain.grading.exception.GradingErrorCode
import me.suhyun.soj.domain.grading.infrastructure.kafka.GradingKafkaProducer
import me.suhyun.soj.global.exception.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class SubmissionEventListener(
    private val gradingKafkaProducer: GradingKafkaProducer
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleSubmissionCreated(event: SubmissionCreatedEvent) {
        try {
            gradingKafkaProducer.send(event.submissionId)
        } catch (e: Exception) {
            log.error("Failed to send grading request for submission ${event.submissionId}", e)
            throw BusinessException(GradingErrorCode.GRADING_SERVER_UNAVAILABLE)
        }
    }
}

package me.suhyun.soj.domain.problem.application.event

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemDocument
import me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemSearchRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProblemEventConsumer(
    private val problemRepository: ProblemRepository,
    private val problemSearchRepository: ProblemSearchRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = [ProblemEventPublisher.TOPIC], groupId = "soj-problem-consumer")
    fun consume(event: ProblemEvent) {
        try {
            when (event) {
                is ProblemEvent.Created -> handleCreated(event.problemId)
                is ProblemEvent.Updated -> handleUpdated(event.problemId)
                is ProblemEvent.Deleted -> handleDeleted(event.problemId)
            }
            logger.info("Processed event: $event")
        } catch (e: Exception) {
            logger.error("Failed to process event: $event", e)
        }
    }

    private fun handleCreated(problemId: Long) {
        val problem = problemRepository.findById(problemId) ?: run {
            logger.warn("Problem not found for created event: $problemId")
            return
        }
        val document = ProblemDocument.from(problem)
        problemSearchRepository.save(document)
    }

    private fun handleUpdated(problemId: Long) {
        val problem = problemRepository.findById(problemId) ?: run {
            logger.warn("Problem not found for updated event: $problemId")
            return
        }
        val document = ProblemDocument.from(problem)
        problemSearchRepository.save(document)
    }

    private fun handleDeleted(problemId: Long) {
        val existingDocument = problemSearchRepository.findById(problemId.toString())
        if (existingDocument.isPresent) {
            val updated = existingDocument.get().copy(isDeleted = true)
            problemSearchRepository.save(updated)
        } else {
            logger.warn("Problem document not found for deleted event: $problemId")
        }
    }
}

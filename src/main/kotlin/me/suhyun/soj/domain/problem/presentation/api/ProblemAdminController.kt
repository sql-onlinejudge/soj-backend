package me.suhyun.soj.domain.problem.presentation.api

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemDocument
import me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemSearchRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/problems")
class ProblemAdminController(
    private val problemRepository: ProblemRepository,
    private val problemSearchRepository: ProblemSearchRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/reindex")
    @Transactional(readOnly = true)
    fun reindexAll(): ResponseEntity<Map<String, Any>> {
        return try {
            logger.info("Starting reindex of all problems to Elasticsearch")

            val problems = problemRepository.findAll(
                page = 0,
                size = 10000,
                minDifficulty = null,
                maxDifficulty = null,
                keyword = null,
                sort = listOf("id:asc"),
                trialStatus = null,
                userId = null
            )

            logger.info("Found ${problems.size} problems to index")

            val documents = problems.map { ProblemDocument.from(it) }
            problemSearchRepository.saveAll(documents)

            logger.info("Reindex completed: ${documents.size} problems indexed")

            ResponseEntity.ok(mapOf(
                "success" to true,
                "indexed" to documents.size,
                "message" to "Successfully reindexed ${documents.size} problems"
            ))
        } catch (e: Exception) {
            logger.error("Reindex failed", e)
            ResponseEntity.internalServerError().body(mapOf(
                "success" to false,
                "error" to (e.message ?: "Unknown error"),
                "type" to e.javaClass.simpleName
            ))
        }
    }
}

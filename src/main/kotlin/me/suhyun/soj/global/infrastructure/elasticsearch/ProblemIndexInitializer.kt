package me.suhyun.soj.global.infrastructure.elasticsearch

import me.suhyun.soj.domain.problem.domain.entity.ProblemTable
import me.suhyun.soj.domain.problem.domain.entity.ProblemEntity
import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemDocument
import me.suhyun.soj.domain.problem.infrastructure.elasticsearch.ProblemSearchRepository
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component

@Component
class ProblemIndexInitializer(
    private val problemSearchRepository: ProblemSearchRepository,
    private val elasticsearchOperations: ElasticsearchOperations
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val BATCH_SIZE = 100
    }

    override fun run(args: ApplicationArguments) {
        try {
            if (!isIndexExists()) {
                logger.info("Problems index does not exist. Creating and indexing all problems...")
                reindexAll()
                logger.info("Initial problem indexing completed")
            } else {
                logger.info("Problems index already exists. Skipping initial indexing.")
            }
        } catch (e: Exception) {
            logger.error("Failed to initialize problem index", e)
        }
    }

    private fun isIndexExists(): Boolean {
        return try {
            elasticsearchOperations.indexOps(ProblemDocument::class.java).exists()
        } catch (e: Exception) {
            false
        }
    }

    fun reindexAll() {
        val problems = transaction {
            ProblemTable.selectAll()
                .where { ProblemTable.deletedAt.isNull() }
                .map { ProblemEntity.wrapRow(it) }
                .map { Problem.from(it) }
        }

        logger.info("Found ${problems.size} problems to index")

        problems.chunked(BATCH_SIZE).forEachIndexed { index, batch ->
            val documents = batch.map { ProblemDocument.from(it) }
            problemSearchRepository.saveAll(documents)
            logger.info("Indexed batch ${index + 1}: ${batch.size} problems")
        }

        logger.info("Reindexing completed: ${problems.size} problems indexed")
    }
}

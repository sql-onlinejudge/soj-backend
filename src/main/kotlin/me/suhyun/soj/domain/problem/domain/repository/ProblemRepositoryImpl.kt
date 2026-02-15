package me.suhyun.soj.domain.problem.domain.repository

import me.suhyun.soj.domain.problem.domain.entity.ProblemEntity
import me.suhyun.soj.domain.problem.domain.entity.ProblemTable
import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.SchemaMetadata
import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import me.suhyun.soj.domain.submission.domain.entity.SubmissionTable
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.notExists
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class ProblemRepositoryImpl : ProblemRepository {

    override fun save(problem: Problem): Problem {
        val entity = ProblemEntity.new {
            this.title = problem.title
            this.description = problem.description
            this.schemaSql = problem.schemaSql
            this.schemaMetadata = problem.schemaMetadata
            this.difficulty = problem.difficulty
            this.timeLimit = problem.timeLimit
            this.isOrderSensitive = problem.isOrderSensitive
            this.createdAt = LocalDateTime.now()
        }
        return Problem.from(entity)
    }

    override fun findById(id: Long): Problem? {
        return ProblemEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?.let { Problem.from(it) }
    }

    override fun findAll(
        page: Int,
        size: Int,
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        sort: List<String>,
        trialStatus: TrialStatus?,
        userId: UUID?
    ): List<Problem> {
        val query = buildFilteredQuery(minDifficulty, maxDifficulty, keyword, trialStatus, userId)

        val sortCriteria = parseSortCriteria(sort)
        sortCriteria.forEach { (column, order) ->
            query.orderBy(column, order)
        }

        return query
            .limit(size, (page * size).toLong())
            .map { ProblemEntity.wrapRow(it) }
            .map { Problem.from(it) }
    }

    private fun parseSortCriteria(sort: List<String>): List<Pair<org.jetbrains.exposed.sql.Column<*>, SortOrder>> {
        return sort.map { sortParam ->
            val parts = sortParam.split(":")
            val field = parts.getOrNull(0) ?: "id"
            val direction = parts.getOrNull(1)?.uppercase() ?: "DESC"

            val column = when (field) {
                "difficulty" -> ProblemTable.difficulty
                "submittedCount" -> ProblemTable.submissionCount
                "solvedCount" -> ProblemTable.solvedCount
                else -> ProblemTable.id
            }
            val order = if (direction == "ASC") SortOrder.ASC else SortOrder.DESC

            column to order
        }
    }

    override fun countAll(
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        trialStatus: TrialStatus?,
        userId: UUID?
    ): Long {
        val query = buildFilteredQuery(minDifficulty, maxDifficulty, keyword, trialStatus, userId)

        return query.count()
    }

    override fun update(
        id: Long,
        title: String?,
        description: String?,
        schemaSql: String?,
        schemaMetadata: SchemaMetadata?,
        difficulty: Int?,
        timeLimit: Int?,
        isOrderSensitive: Boolean?
    ): Problem? {
        val entity = ProblemEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?: return null

        title?.let { entity.title = it }
        description?.let { entity.description = it }
        schemaSql?.let {
            entity.schemaSql = it
            entity.schemaMetadata = schemaMetadata
        }
        difficulty?.let { entity.difficulty = it }
        timeLimit?.let { entity.timeLimit = it }
        isOrderSensitive?.let { entity.isOrderSensitive = it }
        entity.updatedAt = LocalDateTime.now()

        return Problem.from(entity)
    }

    override fun softDelete(id: Long): Boolean {
        val entity = ProblemEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?: return false

        entity.deletedAt = LocalDateTime.now()
        return true
    }

    override fun incrementSubmittedCount(id: Long) {
        ProblemTable.update({ ProblemTable.id eq id }) {
            it[submissionCount] = submissionCount + 1
            it[updatedAt] = LocalDateTime.now()
        }
    }

    override fun incrementSolvedCount(id: Long) {
        ProblemTable.update({ ProblemTable.id eq id }) {
            it[solvedCount] = solvedCount + 1
            it[updatedAt] = LocalDateTime.now()
        }
    }

    override fun findByIdsWithFilters(
        ids: List<Long>,
        minDifficulty: Int?,
        maxDifficulty: Int?,
        trialStatus: TrialStatus?,
        userId: UUID?,
        sort: List<String>
    ): List<Problem> {
        if (ids.isEmpty()) return emptyList()

        val query = ProblemTable.selectAll()
            .where { (ProblemTable.id inList ids) and ProblemTable.deletedAt.isNull() }

        minDifficulty?.let {
            query.andWhere { ProblemTable.difficulty greaterEq it }
        }

        maxDifficulty?.let {
            query.andWhere { ProblemTable.difficulty lessEq it }
        }

        if (trialStatus != null && userId != null) {
            val userIdStr = userId.toString()

            val hasSubmission = SubmissionTable.selectAll().where {
                (SubmissionTable.problemId eq ProblemTable.id) and
                    (SubmissionTable.userId eq userIdStr) and
                    SubmissionTable.deletedAt.isNull()
            }

            val hasAccepted = SubmissionTable.selectAll().where {
                (SubmissionTable.problemId eq ProblemTable.id) and
                    (SubmissionTable.userId eq userIdStr) and
                    (SubmissionTable.verdict eq SubmissionVerdict.ACCEPTED) and
                    SubmissionTable.deletedAt.isNull()
            }

            when (trialStatus) {
                TrialStatus.SOLVED -> query.andWhere { exists(hasAccepted) }
                TrialStatus.ATTEMPTED -> query.andWhere { exists(hasSubmission) and notExists(hasAccepted) }
                TrialStatus.NOT_ATTEMPTED -> query.andWhere { notExists(hasSubmission) }
            }
        }

        val sortCriteria = parseSortCriteria(sort)
        sortCriteria.forEach { (column, order) ->
            query.orderBy(column, order)
        }

        val problemsMap = query
            .map { ProblemEntity.wrapRow(it) }
            .map { Problem.from(it) }
            .associateBy { it.id }

        return ids.mapNotNull { problemsMap[it] }
    }

    private fun buildFilteredQuery(
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        trialStatus: TrialStatus? = null,
        userId: UUID? = null
    ): Query {
        val query = ProblemTable.selectAll()
            .andWhere { ProblemTable.deletedAt.isNull() }

        minDifficulty?.let {
            query.andWhere { ProblemTable.difficulty greaterEq it }
        }

        maxDifficulty?.let {
            query.andWhere { ProblemTable.difficulty lessEq it }
        }

        keyword?.let { kw ->
            val escaped = kw.replace("%", "\\%").replace("_", "\\_")
            val keywordAsId = kw.toLongOrNull()
            query.andWhere {
                if (keywordAsId != null) {
                    (ProblemTable.id eq keywordAsId) or (ProblemTable.title like "%$escaped%")
                } else {
                    ProblemTable.title like "%$escaped%"
                }
            }
        }

        if (trialStatus != null && userId != null) {
            val userIdStr = userId.toString()

            val hasSubmission = SubmissionTable.selectAll().where {
                (SubmissionTable.problemId eq ProblemTable.id) and
                    (SubmissionTable.userId eq userIdStr) and
                    SubmissionTable.deletedAt.isNull()
            }

            val hasAccepted = SubmissionTable.selectAll().where {
                (SubmissionTable.problemId eq ProblemTable.id) and
                    (SubmissionTable.userId eq userIdStr) and
                    (SubmissionTable.verdict eq SubmissionVerdict.ACCEPTED) and
                    SubmissionTable.deletedAt.isNull()
            }

            when (trialStatus) {
                TrialStatus.SOLVED -> query.andWhere { exists(hasAccepted) }
                TrialStatus.ATTEMPTED -> query.andWhere { exists(hasSubmission) and notExists(hasAccepted) }
                TrialStatus.NOT_ATTEMPTED -> query.andWhere { notExists(hasSubmission) }
            }
        }

        return query
    }
}

package me.suhyun.soj.domain.submission.domain.repository

import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import me.suhyun.soj.domain.submission.domain.entity.SubmissionEntity
import me.suhyun.soj.domain.submission.domain.entity.SubmissionTable
import me.suhyun.soj.domain.submission.domain.model.DailySubmissionCount
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionStatus
import me.suhyun.soj.domain.submission.domain.model.enums.SubmissionVerdict
import me.suhyun.soj.domain.submission.domain.model.Submission
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class SubmissionRepositoryImpl : SubmissionRepository {

    override fun save(submission: Submission): Submission {
        val entity = SubmissionEntity.new {
            this.problemId = submission.problemId
            this.userId = submission.userId.toString()
            this.query = submission.query
            this.status = submission.status
            this.verdict = submission.verdict
            this.createdAt = submission.createdAt
        }
        return Submission.from(entity)
    }

    override fun findById(id: Long): Submission? {
        return SubmissionEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?.let { Submission.from(it) }
    }

    override fun findAll(problemId: Long?, page: Int, size: Int): List<Submission> {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .apply { problemId?.let { andWhere { SubmissionTable.problemId eq it } } }
            .orderBy(SubmissionTable.createdAt, SortOrder.DESC)
            .limit(size, (page * size).toLong())
            .map { SubmissionEntity.wrapRow(it) }
            .map { Submission.from(it) }
    }

    override fun countAll(problemId: Long?): Long {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .apply { problemId?.let { andWhere { SubmissionTable.problemId eq it } } }
            .count()
    }

    override fun findByProblemId(problemId: Long, page: Int, size: Int): List<Submission> {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.problemId eq problemId }
            .orderBy(SubmissionTable.createdAt, SortOrder.DESC)
            .limit(size, (page * size).toLong())
            .map { SubmissionEntity.wrapRow(it) }
            .map { Submission.from(it) }
    }

    override fun findByProblemIdAndUserId(
        problemId: Long,
        userId: UUID,
        page: Int,
        size: Int
    ): List<Submission> {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.problemId eq problemId }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .orderBy(SubmissionTable.createdAt, SortOrder.DESC)
            .limit(size, (page * size).toLong())
            .map { SubmissionEntity.wrapRow(it) }
            .map { Submission.from(it) }
    }

    override fun countByProblemId(problemId: Long): Long {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.problemId eq problemId }
            .count()
    }

    override fun countByProblemIdAndUserId(problemId: Long, userId: UUID): Long {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.problemId eq problemId }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .count()
    }

    override fun getTrialStatuses(problemIds: List<Long>, userId: UUID): Map<Long, Boolean> {
        if (problemIds.isEmpty()) return emptyMap()

        val submissions = SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.problemId inList problemIds }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .map { SubmissionEntity.wrapRow(it) }

        return submissions.groupBy { it.problemId }
            .mapValues { (_, subs) -> subs.any { it.verdict == SubmissionVerdict.ACCEPTED } }
    }

    override fun getTrialStatus(problemId: Long, userId: UUID?): TrialStatus {
        if (userId == null) return TrialStatus.NOT_ATTEMPTED

        val submissions = SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.problemId eq problemId }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .map { SubmissionEntity.wrapRow(it) }

        if (submissions.isEmpty()) return TrialStatus.NOT_ATTEMPTED
        if (submissions.any { it.verdict == SubmissionVerdict.ACCEPTED }) return TrialStatus.SOLVED
        return TrialStatus.ATTEMPTED
    }

    override fun updateStatus(id: Long, status: SubmissionStatus, verdict: SubmissionVerdict?): Boolean {
        val entity = SubmissionEntity.findById(id) ?: return false
        entity.status = status
        entity.verdict = verdict
        entity.updatedAt = LocalDateTime.now()
        return true
    }

    override fun countAll(): Long {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .count()
    }

    override fun countByStatus(status: SubmissionStatus): Long {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.status eq status }
            .count()
    }

    override fun countByDateGrouped(startDate: LocalDateTime, endDate: LocalDateTime): List<DailySubmissionCount> {
        val dateExpr = SubmissionTable.createdAt.date()
        val countExpr = SubmissionTable.id.count()
        return SubmissionTable
            .select(dateExpr, countExpr)
            .where { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.createdAt greaterEq startDate }
            .andWhere { SubmissionTable.createdAt lessEq endDate }
            .groupBy(dateExpr)
            .orderBy(dateExpr)
            .map { row ->
                DailySubmissionCount(
                    date = row[dateExpr],
                    count = row[countExpr]
                )
            }
    }

    override fun findRecent(limit: Int): List<Submission> {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .orderBy(SubmissionTable.createdAt, SortOrder.DESC)
            .limit(limit)
            .map { SubmissionEntity.wrapRow(it) }
            .map { Submission.from(it) }
    }

    override fun findRecentByUserId(userId: UUID, limit: Int): List<Submission> {
        return SubmissionTable.selectAll()
            .andWhere { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .orderBy(SubmissionTable.createdAt, SortOrder.DESC)
            .limit(limit)
            .map { SubmissionEntity.wrapRow(it) }
            .map { Submission.from(it) }
    }

    override fun findSolvedProblemIdsByUserId(userId: UUID): Set<Long> {
        return SubmissionTable
            .select(SubmissionTable.problemId)
            .where { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .andWhere { SubmissionTable.verdict eq SubmissionVerdict.ACCEPTED }
            .map { it[SubmissionTable.problemId] }
            .toSet()
    }

    override fun countNonAcceptedByUserIdAndProblemId(userId: UUID, problemId: Long): Int {
        val total = SubmissionTable.selectAll()
            .where { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .andWhere { SubmissionTable.problemId eq problemId }
            .andWhere { SubmissionTable.verdict.isNotNull() }
            .count()
        val accepted = SubmissionTable.selectAll()
            .where { SubmissionTable.deletedAt.isNull() }
            .andWhere { SubmissionTable.userId eq userId.toString() }
            .andWhere { SubmissionTable.problemId eq problemId }
            .andWhere { SubmissionTable.verdict eq SubmissionVerdict.ACCEPTED }
            .count()
        return (total - accepted).toInt()
    }

    override fun migrateUserId(fromUserId: UUID, toUserId: UUID): Int {
        return SubmissionTable.update({ SubmissionTable.userId eq fromUserId.toString() }) {
            it[userId] = toUserId.toString()
        }
    }
}

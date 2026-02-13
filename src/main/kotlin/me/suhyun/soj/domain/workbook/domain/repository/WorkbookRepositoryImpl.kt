package me.suhyun.soj.domain.workbook.domain.repository

import me.suhyun.soj.domain.workbook.domain.entity.WorkbookEntity
import me.suhyun.soj.domain.workbook.domain.entity.WorkbookTable
import me.suhyun.soj.domain.workbook.domain.model.Workbook
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class WorkbookRepositoryImpl : WorkbookRepository {

    override fun save(workbook: Workbook): Workbook {
        val entity = WorkbookEntity.new {
            this.name = workbook.name
            this.description = workbook.description
            this.difficulty = workbook.difficulty
            this.createdAt = LocalDateTime.now()
        }
        return Workbook.from(entity)
    }

    override fun findById(id: Long): Workbook? {
        return WorkbookEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?.let { Workbook.from(it) }
    }

    override fun findAll(
        page: Int,
        size: Int,
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
        sort: List<String>,
    ): List<Workbook> {
        val query = buildFilteredQuery(minDifficulty, maxDifficulty, keyword)

        parseSortCriteria(sort).forEach { (column, order) ->
            query.orderBy(column, order)
        }

        return query
            .limit(size, (page * size).toLong())
            .map { WorkbookEntity.wrapRow(it) }
            .map { Workbook.from(it) }
    }

    override fun countAll(
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
    ): Long {
        return buildFilteredQuery(minDifficulty, maxDifficulty, keyword).count()
    }

    override fun update(
        id: Long,
        name: String?,
        description: String?,
        difficulty: Long?
    ): Workbook? {
        val entity = WorkbookEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?: return null

        name?.let { entity.name = it }
        description?.let { entity.description = it }
        difficulty?.let { entity.difficulty = it }
        entity.updatedAt = LocalDateTime.now()

        return Workbook.from(entity)
    }

    override fun softDelete(id: Long): Boolean {
        val entity = WorkbookEntity.findById(id)
            ?.takeIf { it.deletedAt == null }
            ?: return false

        entity.deletedAt = LocalDateTime.now()
        return true
    }

    private fun buildFilteredQuery(
        minDifficulty: Int?,
        maxDifficulty: Int?,
        keyword: String?,
    ): Query {
        val query = WorkbookTable.selectAll()
            .andWhere { WorkbookTable.deletedAt.isNull() }

        minDifficulty?.let {
            query.andWhere { WorkbookTable.difficulty greaterEq it.toLong() }
        }
        maxDifficulty?.let {
            query.andWhere { WorkbookTable.difficulty lessEq it.toLong() }
        }
        keyword?.let { kw ->
            val escaped = kw.replace("%", "\\%").replace("_", "\\_")
            val keywordAsId = kw.toLongOrNull()
            query.andWhere {
                if (keywordAsId != null) {
                    (WorkbookTable.id eq keywordAsId) or (WorkbookTable.name like "%$escaped%")
                } else {
                    WorkbookTable.name like "%$escaped%"
                }
            }
        }

        return query
    }

    private fun parseSortCriteria(sort: List<String>): List<Pair<org.jetbrains.exposed.sql.Column<*>, SortOrder>> {
        return sort.map { sortParam ->
            val parts = sortParam.split(":")
            val field = parts.getOrNull(0) ?: "id"
            val direction = parts.getOrNull(1)?.uppercase() ?: "DESC"

            val column = when (field) {
                "difficulty" -> WorkbookTable.difficulty
                "name" -> WorkbookTable.name
                else -> WorkbookTable.id
            }
            val order = if (direction == "ASC") SortOrder.ASC else SortOrder.DESC

            column to order
        }
    }
}

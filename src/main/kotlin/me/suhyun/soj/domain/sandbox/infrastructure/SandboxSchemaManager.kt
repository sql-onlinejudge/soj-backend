package me.suhyun.soj.domain.sandbox.infrastructure

import me.suhyun.soj.domain.sandbox.exception.SandboxErrorCode
import me.suhyun.soj.global.exception.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.sql.DataSource

data class SandboxQueryResult(
    val columns: List<String>,
    val rows: List<List<String?>>
)

@Component
class SandboxSchemaManager(
    @Qualifier("sandboxAdminDataSource")
    private val adminDataSource: DataSource,
    @Qualifier("sandboxReadonlyDataSource")
    private val readonlyDataSource: DataSource
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun setupSchema(schemaName: String, extractedSql: String) {
        try {
            adminDataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute("CREATE DATABASE IF NOT EXISTS `$schemaName`")
                    stmt.execute("USE `$schemaName`")
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 0")
                    try {
                        extractedSql.split(";")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .forEach { stmt.execute(it) }
                    } finally {
                        stmt.execute("SET FOREIGN_KEY_CHECKS = 1")
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Failed to setup sandbox schema: {}", schemaName, e)
            throw BusinessException(SandboxErrorCode.SANDBOX_SCHEMA_SETUP_FAILED)
        }
    }

    fun executeQuery(schemaName: String, query: String, timeoutMs: Int): SandboxQueryResult {
        val executor = Executors.newSingleThreadExecutor()
        try {
            val future = executor.submit<SandboxQueryResult> {
                readonlyDataSource.connection.use { conn ->
                    conn.createStatement().use { stmt ->
                        stmt.execute("USE `$schemaName`")
                        val resultSet = stmt.executeQuery(query)
                        resultSetToStructured(resultSet)
                    }
                }
            }
            return future.get(timeoutMs.toLong(), TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            throw BusinessException(SandboxErrorCode.SANDBOX_SCHEMA_SETUP_FAILED)
        } catch (e: Exception) {
            throw e.cause?.let { BusinessException(SandboxErrorCode.SANDBOX_SCHEMA_SETUP_FAILED) } ?: throw e
        } finally {
            executor.shutdownNow()
        }
    }

    fun dropSchema(schemaName: String) {
        try {
            adminDataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute("DROP DATABASE IF EXISTS `$schemaName`")
                }
            }
        } catch (e: Exception) {
            log.warn("Failed to drop sandbox schema: {}", schemaName, e)
        }
    }

    private fun resultSetToStructured(resultSet: ResultSet): SandboxQueryResult {
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount
        val columns = (1..columnCount).map { metaData.getColumnLabel(it) }
        val rows = mutableListOf<List<String?>>()
        while (resultSet.next()) {
            rows.add((1..columnCount).map { resultSet.getString(it) })
        }
        return SandboxQueryResult(columns, rows)
    }
}

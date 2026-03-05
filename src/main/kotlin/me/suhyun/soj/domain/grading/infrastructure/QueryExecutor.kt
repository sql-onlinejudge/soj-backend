package me.suhyun.soj.domain.grading.infrastructure

import me.suhyun.soj.domain.grading.exception.QueryExecutionException
import me.suhyun.soj.domain.grading.exception.QueryTimeoutException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.sql.DataSource

@Component
class QueryExecutor(
    @Qualifier("sandboxAdminDataSource")
    private val adminDataSource: DataSource,

    @Qualifier("sandboxReadonlyDataSource")
    private val readonlyDataSource: DataSource
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(schemaSql: String, initSql: String?, query: String, timeoutMs: Int): String {
        val executor = Executors.newSingleThreadExecutor()

        try {
            val future = executor.submit<String> {
                adminDataSource.connection.use { adminConn ->
                    adminConn.createStatement().use { stmt ->
                        val tableNames = extractTableNames(schemaSql)
                        stmt.execute("SET FOREIGN_KEY_CHECKS=0")
                        tableNames.forEach { stmt.execute("DROP TABLE IF EXISTS `$it`") }
                        stmt.execute("SET FOREIGN_KEY_CHECKS=1")
                        stmt.execute(schemaSql)
                        initSql?.let { stmt.execute(it) }
                    }
                }

                readonlyDataSource.connection.use { readonlyConn ->
                    readonlyConn.createStatement().use { stmt ->
                        val resultSet = stmt.executeQuery(query)
                        resultSetToString(resultSet)
                    }
                }
            }

            val startTime = System.currentTimeMillis()
            val result = future.get(timeoutMs.toLong(), TimeUnit.MILLISECONDS)
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed > 500) {
                log.warn("Slow query detected: ${elapsed}ms")
            }
            return result
        } catch (e: TimeoutException) {
            throw QueryTimeoutException("Query execution timed out after ${timeoutMs}ms")
        } catch (e: Exception) {
            when (val cause = e.cause) {
                is QueryTimeoutException -> throw cause
                else -> throw QueryExecutionException("Query execution failed: ${cause?.message ?: e.message}")
            }
        } finally {
            executor.shutdownNow()
        }
    }

    private fun extractTableNames(schemaSql: String): List<String> {
        val regex = Regex("""CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?`?(\w+)`?""", RegexOption.IGNORE_CASE)
        return regex.findAll(schemaSql).map { it.groupValues[1] }.toList()
    }

    private fun resultSetToString(resultSet: ResultSet): String {
        val result = StringBuilder()
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount

        val header = (1..columnCount).joinToString("\t") { i ->
            metaData.getColumnLabel(i)
        }
        result.appendLine(header)

        while (resultSet.next()) {
            val row = (1..columnCount).joinToString("\t") { i ->
                resultSet.getString(i) ?: "NULL"
            }
            result.appendLine(row)
        }

        return result.toString().trim()
    }
}

package me.suhyun.soj.domain.grading.infrastructure

import me.suhyun.soj.domain.grading.exception.QueryExecutionException
import me.suhyun.soj.domain.grading.exception.QueryTimeoutException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.sql.DataSource

@Component
class QueryExecutor(
    @Qualifier("sandboxDataSource")
    private val dataSource: DataSource
) {

    fun execute(schemaSql: String, initSql: String?, query: String, timeoutMs: Int): String {
        val executor = Executors.newSingleThreadExecutor()

        try {
            val future = executor.submit<String> {
                dataSource.connection.use { connection ->
                    connection.createStatement().use { statement ->
                        val tableNames = extractTableNames(schemaSql)
                        tableNames.forEach { tableName ->
                            statement.execute("DROP TABLE IF EXISTS $tableName")
                        }

                        statement.execute(schemaSql)

                        initSql?.let { statement.execute(it) }

                        val resultSet = statement.executeQuery(query)
                        resultSetToString(resultSet)
                    }
                }
            }

            return future.get(timeoutMs.toLong(), TimeUnit.MILLISECONDS)
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
        val regex = Regex("""CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?(\w+)""", RegexOption.IGNORE_CASE)
        return regex.findAll(schemaSql).map { it.groupValues[1] }.toList()
    }

    private fun resultSetToString(resultSet: ResultSet): String {
        val result = StringBuilder()
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount

        while (resultSet.next()) {
            val row = (1..columnCount).joinToString("\t") { i ->
                resultSet.getString(i) ?: "NULL"
            }
            result.appendLine(row)
        }

        return result.toString().trim()
    }
}

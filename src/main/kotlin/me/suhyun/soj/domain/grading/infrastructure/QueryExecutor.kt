package me.suhyun.soj.domain.grading.infrastructure

import me.suhyun.soj.domain.grading.exception.QueryExecutionException
import me.suhyun.soj.domain.grading.exception.QueryTimeoutException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.sql.Connection
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
    private val readonlyDataSource: DataSource,
    private val schemaPool: SandboxSchemaPool
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(schemaSql: String, initSql: String?, query: String, timeoutMs: Int): String {
        val executor = Executors.newSingleThreadExecutor()
        val schemaName = schemaPool.acquire()

        try {
            val future = executor.submit<String> {
                adminDataSource.connection.use { adminConn ->
                    adminConn.createStatement().use { stmt ->
                        stmt.execute("USE `$schemaName`")
                        dropAllTables(adminConn, schemaName)
                        stmt.execute(schemaSql)
                        initSql?.let { stmt.execute(it) }
                    }
                }

                readonlyDataSource.connection.use { readonlyConn ->
                    readonlyConn.createStatement().use { stmt ->
                        stmt.execute("USE `$schemaName`")
                        val resultSet = stmt.executeQuery(query)
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
            try {
                schemaPool.release(schemaName)
            } catch (e: Exception) {
                log.warn("Failed to release schema: {}", schemaName, e)
            }
            executor.shutdownNow()
        }
    }

    private fun dropAllTables(conn: Connection, schemaName: String) {
        conn.createStatement().use { stmt ->
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0")
            try {
                conn.createStatement().use { tableStmt ->
                    tableStmt.executeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema = '$schemaName'"
                    ).use { resultSet ->
                        while (resultSet.next()) {
                            val tableName = resultSet.getString(1)
                            stmt.execute("DROP TABLE IF EXISTS `$schemaName`.`$tableName`")
                        }
                    }
                }
            } finally {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1")
            }
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

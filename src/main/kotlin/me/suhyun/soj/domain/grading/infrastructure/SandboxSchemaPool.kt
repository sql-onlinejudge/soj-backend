package me.suhyun.soj.domain.grading.infrastructure

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.ArrayBlockingQueue
import javax.sql.DataSource

@Component
class SandboxSchemaPool(
    @Qualifier("sandboxAdminDataSource")
    private val adminDataSource: DataSource,
    @Value("\${sandbox.pool.size:10}") private val poolSize: Int
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val available = ArrayBlockingQueue<String>(poolSize)

    @PostConstruct
    fun init() {
        adminDataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                repeat(poolSize) { i ->
                    val schemaName = "sandbox_pool_$i"
                    stmt.execute("CREATE DATABASE IF NOT EXISTS `$schemaName`")
                    available.offer(schemaName)
                    log.info("Schema pool initialized: {}", schemaName)
                }
            }
        }
    }

    fun acquire(): String = available.take()

    fun release(schemaName: String) {
        available.offer(schemaName)
    }

    @PreDestroy
    fun destroy() {
        adminDataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                repeat(poolSize) { i ->
                    stmt.execute("DROP DATABASE IF EXISTS `sandbox_pool_$i`")
                }
            }
        }
    }
}

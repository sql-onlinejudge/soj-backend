package me.suhyun.soj.domain.sandbox.infrastructure

import me.suhyun.soj.domain.sandbox.exception.SandboxErrorCode
import me.suhyun.soj.global.exception.BusinessException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SandboxSqlValidatorTest {

    private lateinit var validator: SandboxSqlValidator

    @BeforeEach
    fun setUp() {
        validator = SandboxSqlValidator()
    }

    @Nested
    inner class `validateSetupSql` {

        @Test
        fun `should pass when SQL contains only CREATE TABLE`() {
            val sql = "CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100));"
            validator.validateSetupSql(sql)
        }

        @Test
        fun `should pass when SQL contains CREATE TABLE and INSERT`() {
            val sql = """
                CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100));
                INSERT INTO users VALUES (1, 'Alice');
            """.trimIndent()
            validator.validateSetupSql(sql)
        }

        @Test
        fun `should throw when SQL contains DROP TABLE`() {
            val sql = "DROP TABLE users;"
            assertThatThrownBy { validator.validateSetupSql(sql) }
                .isInstanceOf(BusinessException::class.java)
                .extracting { (it as BusinessException).errorCode }
                .isEqualTo(SandboxErrorCode.FORBIDDEN_SETUP_SQL)
        }

        @Test
        fun `should throw when SQL contains SELECT`() {
            val sql = "SELECT * FROM users;"
            assertThatThrownBy { validator.validateSetupSql(sql) }
                .isInstanceOf(BusinessException::class.java)
        }

        @Test
        fun `should throw when SQL contains DELETE`() {
            val sql = "DELETE FROM users WHERE id = 1;"
            assertThatThrownBy { validator.validateSetupSql(sql) }
                .isInstanceOf(BusinessException::class.java)
        }

        @Test
        fun `should throw when SQL contains UPDATE`() {
            val sql = "UPDATE users SET name = 'Bob' WHERE id = 1;"
            assertThatThrownBy { validator.validateSetupSql(sql) }
                .isInstanceOf(BusinessException::class.java)
        }
    }

    @Nested
    inner class `validateSelectQuery` {

        @Test
        fun `should pass for valid SELECT query`() {
            val query = "SELECT * FROM users"
            validator.validateSelectQuery(query)
        }

        @Test
        fun `should pass for SELECT with JOIN`() {
            val query = "SELECT u.name, o.amount FROM users u JOIN orders o ON u.id = o.user_id"
            validator.validateSelectQuery(query)
        }

        @Test
        fun `should throw when query is INSERT`() {
            val query = "INSERT INTO users VALUES (1, 'Alice')"
            assertThatThrownBy { validator.validateSelectQuery(query) }
                .isInstanceOf(BusinessException::class.java)
                .extracting { (it as BusinessException).errorCode }
                .isEqualTo(SandboxErrorCode.SANDBOX_FORBIDDEN)
        }

        @Test
        fun `should throw when query is DROP`() {
            val query = "DROP TABLE users"
            assertThatThrownBy { validator.validateSelectQuery(query) }
                .isInstanceOf(BusinessException::class.java)
        }
    }
}

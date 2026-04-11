package me.suhyun.soj.domain.sandbox.infrastructure

import me.suhyun.soj.domain.sandbox.exception.SandboxErrorCode
import me.suhyun.soj.global.exception.BusinessException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.Select
import org.springframework.stereotype.Component

@Component
class SandboxSqlValidator {

    fun validateSetupSql(sql: String) {
        val statements = CCJSqlParserUtil.parseStatements(sql)
        statements.forEach { statement ->
            if (statement !is CreateTable && statement !is Insert) {
                throw BusinessException(SandboxErrorCode.FORBIDDEN_SETUP_SQL)
            }
        }
    }

    fun validateSelectQuery(query: String) {
        val statement = CCJSqlParserUtil.parse(query)
        if (statement !is Select) {
            throw BusinessException(SandboxErrorCode.SANDBOX_FORBIDDEN)
        }
    }
}

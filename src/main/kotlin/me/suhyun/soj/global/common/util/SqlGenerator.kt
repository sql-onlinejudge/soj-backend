package me.suhyun.soj.global.common.util

import me.suhyun.soj.domain.problem.domain.model.SchemaMetadata
import me.suhyun.soj.domain.testcase.domain.model.AnswerMetadata
import me.suhyun.soj.domain.testcase.domain.model.InitMetadata

object SqlGenerator {

    fun generateSchema(metadata: SchemaMetadata): String {
        return metadata.tables.joinToString("\n") { table ->
            val columns = table.columns.joinToString(", ") { col ->
                val notNull = if (!col.nullable) " NOT NULL" else ""
                val constraints = if (col.constraints.isNotEmpty()) {
                    " " + col.constraints.joinToString(" ")
                } else ""
                "${col.name} ${col.type}$notNull$constraints"
            }
            "CREATE TABLE ${table.name} ($columns);"
        }
    }

    fun generateInit(metadata: InitMetadata): String {
        return metadata.statements.joinToString("\n") { stmt ->
            if (stmt.rows.isEmpty()) return@joinToString ""

            val columns = stmt.rows.first().keys.toList()
            val columnNames = columns.joinToString(", ")

            val values = stmt.rows.joinToString(", ") { row ->
                val rowValues = columns.map { col ->
                    val value = row[col]
                    formatValue(value)
                }.joinToString(", ")
                "($rowValues)"
            }

            "INSERT INTO ${stmt.table} ($columnNames) VALUES $values;"
        }.trim()
    }

    fun generateAnswer(metadata: AnswerMetadata): String {
        val header = metadata.columns.joinToString("\t")
        val rows = metadata.rows.joinToString("\n") { row ->
            row.joinToString("\t") { value ->
                value?.toString() ?: "NULL"
            }
        }
        return if (metadata.rows.isEmpty()) header else "$header\n$rows"
    }

    private fun formatValue(value: Any?): String {
        return when (value) {
            null -> "NULL"
            is String -> "'${value.replace("'", "''")}'"
            is Number -> value.toString()
            is Boolean -> if (value) "TRUE" else "FALSE"
            else -> "'${value.toString().replace("'", "''")}'"
        }
    }
}

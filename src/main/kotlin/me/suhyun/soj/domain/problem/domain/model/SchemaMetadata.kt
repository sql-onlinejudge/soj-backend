package me.suhyun.soj.domain.problem.domain.model

data class SchemaMetadata(
    val tables: List<TableMetadata>
)

data class TableMetadata(
    val name: String,
    val columns: List<ColumnMetadata>
)

data class ColumnMetadata(
    val name: String,
    val type: String,
    val nullable: Boolean = true,
    val constraints: List<String> = emptyList()
)

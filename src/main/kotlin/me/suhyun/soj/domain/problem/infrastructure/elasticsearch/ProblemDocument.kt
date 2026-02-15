package me.suhyun.soj.domain.problem.infrastructure.elasticsearch

import me.suhyun.soj.domain.problem.domain.model.Problem
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.Setting
import java.time.Instant
import java.time.ZoneOffset

@Document(indexName = "problems")
@Setting(settingPath = "/elasticsearch/problem-index-settings.json")
data class ProblemDocument(
    @Id
    val id: String,

    @Field(type = FieldType.Text, analyzer = "korean_ngram")
    val title: String,

    @Field(type = FieldType.Text, analyzer = "korean_ngram")
    val description: String,

    @Field(type = FieldType.Boolean)
    val isDeleted: Boolean = false
) {
    companion object {
        fun from(problem: Problem): ProblemDocument {
            return ProblemDocument(
                id = problem.id.toString(),
                title = problem.title,
                description = problem.description,
                isDeleted = problem.deletedAt != null
            )
        }
    }
}

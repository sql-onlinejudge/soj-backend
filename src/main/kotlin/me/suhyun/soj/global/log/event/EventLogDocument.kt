package me.suhyun.soj.global.log.event

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

@Document(indexName = "event_logs", createIndex = false)
data class EventLogDocument(
    @Id
    val id: String? = null,

    @Field(type = FieldType.Keyword)
    val userId: String?,

    @Field(type = FieldType.Keyword)
    val eventType: EventType,

    @Field(type = FieldType.Keyword)
    val page: String?,

    @Field(type = FieldType.Keyword)
    val targetId: String?,

    @Field(type = FieldType.Keyword)
    val ipAddress: String?,

    @Field(type = FieldType.Text)
    val userAgent: String?,

    @Field(type = FieldType.Object)
    val metadata: Map<String, Any>? = null,

    @Field(type = FieldType.Date)
    val createdAt: Instant = Instant.now()
)

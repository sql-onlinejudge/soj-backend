package me.suhyun.soj.domain.problem.infrastructure.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.StringQuery
import org.springframework.stereotype.Service

@Service
class ProblemSearchService(
    private val elasticsearchOperations: ElasticsearchOperations,
    private val objectMapper: ObjectMapper
) {

    fun search(keyword: String): List<Long> {
        val queryJson = buildQueryJson(keyword)

        val query = StringQuery(queryJson)

        val searchHits: SearchHits<ProblemDocument> = elasticsearchOperations.search(
            query,
            ProblemDocument::class.java
        )

        return searchHits.searchHits.map { it.content.id.toLong() }
    }

    private fun buildQueryJson(keyword: String): String {
        val must = mutableListOf<Map<String, Any>>()

        must.add(mapOf("term" to mapOf("isDeleted" to false)))

        val keywordAsId = keyword.toLongOrNull()
        if (keywordAsId != null) {
            must.add(
                mapOf(
                    "bool" to mapOf(
                        "should" to listOf(
                            mapOf("term" to mapOf("id" to keyword)),
                            mapOf(
                                "multi_match" to mapOf(
                                    "query" to keyword,
                                    "fields" to listOf("title", "description"),
                                    "type" to "best_fields"
                                )
                            )
                        ),
                        "minimum_should_match" to 1
                    )
                )
            )
        } else {
            must.add(
                mapOf(
                    "multi_match" to mapOf(
                        "query" to keyword,
                        "fields" to listOf("title", "description"),
                        "type" to "best_fields",
                        "fuzziness" to "AUTO"
                    )
                )
            )
        }

        val queryMap = mapOf("bool" to mapOf("must" to must))

        return objectMapper.writeValueAsString(queryMap)
    }
}

package me.suhyun.soj.domain.problem.infrastructure.elasticsearch

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ProblemSearchRepository : ElasticsearchRepository<ProblemDocument, String>

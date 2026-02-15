package me.suhyun.soj.global.infrastructure.elasticsearch.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackages = ["me.suhyun.soj.domain.*.infrastructure.elasticsearch"])
class ElasticsearchConfig

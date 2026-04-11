package me.suhyun.soj.global.infrastructure.anthropic

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(AnthropicProperties::class)
class AnthropicWebClientConfig(private val properties: AnthropicProperties) {

    @Bean("anthropicWebClient")
    fun anthropicWebClient(): WebClient = WebClient.builder()
        .baseUrl(properties.baseUrl)
        .defaultHeader("x-api-key", properties.apiKey)
        .defaultHeader("anthropic-version", properties.apiVersion)
        .defaultHeader("content-type", "application/json")
        .codecs { it.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) }
        .build()
}

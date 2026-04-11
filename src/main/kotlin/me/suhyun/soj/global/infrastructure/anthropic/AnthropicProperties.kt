package me.suhyun.soj.global.infrastructure.anthropic

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "anthropic")
data class AnthropicProperties(
    val apiKey: String = "",
    val baseUrl: String = "https://api.anthropic.com",
    val apiVersion: String = "2023-06-01"
)

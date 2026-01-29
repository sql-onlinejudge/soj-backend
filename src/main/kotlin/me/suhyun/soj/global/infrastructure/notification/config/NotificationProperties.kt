package me.suhyun.soj.global.infrastructure.notification.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "notification")
data class NotificationProperties(
    val routing: Map<String, String> = emptyMap(),
    val discord: DiscordProperties = DiscordProperties(),
    val slack: SlackProperties = SlackProperties(),
) {
    data class DiscordProperties(
        val webhookUrl: String = ""
    )

    data class SlackProperties(
        val webhookUrl: String = ""
    )
}

package me.suhyun.soj.global.infrastructure.notification

import me.suhyun.soj.global.infrastructure.notification.config.NotificationProperties
import me.suhyun.soj.global.infrastructure.notification.model.enums.NotificationType
import me.suhyun.soj.global.infrastructure.notification.model.enums.ProviderType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class DiscordNotifier(
    private val properties: NotificationProperties
) : Notifier {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val webClient = WebClient.create()

    override val providerType = ProviderType.DISCORD

    override fun notify(type: NotificationType, vararg bodies: Any) {
        val webhookUrl = properties.discord.webhookUrl
        if (webhookUrl.isBlank()) {
            log.error("Discord webhook URL is not configured")
            return
        }

        val message = buildMessage(type, *bodies)

        runCatching {
            webClient.post()
                .uri(webhookUrl)
                .bodyValue(mapOf("content" to message))
                .retrieve()
                .toBodilessEntity()
                .block()
        }.onFailure {
            log.error("Failed to send Discord notification", it)
        }
    }

    private fun buildMessage(type: NotificationType, vararg bodies: Any): String {
        val body = type.template.format(*bodies)
        return "## ${type.title}\n$body"
    }
}

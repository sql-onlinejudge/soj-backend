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
        val webhookUrl = getWebhookUrl(type)
        if (webhookUrl.isBlank()) {
            log.error("Discord webhook URL is not configured for type: {}", type)
            return
        }

        val embed = buildEmbed(type, *bodies)

        runCatching {
            webClient.post()
                .uri(webhookUrl)
                .bodyValue(mapOf("embeds" to listOf(embed)))
                .retrieve()
                .toBodilessEntity()
                .block()
        }.onFailure {
            log.error("Failed to send Discord notification", it)
        }
    }

    private fun getWebhookUrl(type: NotificationType): String {
        return when (type) {
            NotificationType.ERROR -> properties.discord.errorWebhookUrl
            else -> properties.discord.webhookUrl
        }.ifBlank { properties.discord.webhookUrl }
    }

    private fun buildEmbed(type: NotificationType, vararg bodies: Any): Map<String, Any> {
        return when (type) {
            NotificationType.ERROR -> buildErrorEmbed(*bodies)
            NotificationType.SUBMISSION -> buildSubmissionEmbed(*bodies)
        }
    }

    private fun buildErrorEmbed(vararg bodies: Any): Map<String, Any> {
        val args = bodies.map { it.toString() }
        val profile = args[0]
        val timestamp = args[1]
        val method = args[2]
        val path = args[3]
        val errorType = args[4]
        val message = args[5]
        val stackTrace = args[6].let { if (it.length > 1000) it.take(990) + "\n..." else it }
        return mapOf(
            "title" to NotificationType.ERROR.title,
            "color" to NotificationType.ERROR.color,
            "fields" to listOf(
                mapOf("name" to "Server", "value" to profile, "inline" to true),
                mapOf("name" to "Time", "value" to timestamp, "inline" to true),
                mapOf("name" to "Error Type", "value" to errorType, "inline" to false),
                mapOf("name" to "Message", "value" to "[$method] $path\n$message", "inline" to false),
                mapOf("name" to "Stack Trace", "value" to "```$stackTrace```", "inline" to false)
            )
        )
    }

    private fun buildSubmissionEmbed(vararg bodies: Any): Map<String, Any> {
        val (submissionId, query) = bodies.map { it.toString() }
        return mapOf(
            "title" to NotificationType.SUBMISSION.title,
            "color" to NotificationType.SUBMISSION.color,
            "fields" to listOf(
                mapOf("name" to "Submission ID", "value" to submissionId, "inline" to true),
                mapOf("name" to "Query", "value" to "```sql\n$query\n```", "inline" to false)
            )
        )
    }
}

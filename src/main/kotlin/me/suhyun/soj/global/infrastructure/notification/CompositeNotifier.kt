package me.suhyun.soj.global.infrastructure.notification

import me.suhyun.soj.global.infrastructure.notification.config.NotificationProperties
import me.suhyun.soj.global.infrastructure.notification.model.enums.NotificationType
import me.suhyun.soj.global.infrastructure.notification.model.enums.ProviderType
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
@Primary
class CompositeNotifier(
    private val notifiers: List<Notifier>,
    private val properties: NotificationProperties
) : Notifier {

    private val log = LoggerFactory.getLogger(this::class.java)

    override val providerType: ProviderType
        get() = throw UnsupportedOperationException("CompositeNotifier does not have a single provider type")

    @Async
    override fun notify(type: NotificationType, vararg bodies: Any) {
        val providerName = properties.routing[type.name.lowercase()]
        if (providerName.isNullOrBlank()) {
            log.error("No provider configured for notification type: ${type.name}")
            return
        }

        val targetProvider = runCatching { ProviderType.valueOf(providerName.uppercase()) }
            .onFailure { log.warn("Unknown provider type: $providerName") }
            .getOrNull() ?: return

        notifiers
            .find { it !== this && it.providerType == targetProvider }
            ?.let { notifier ->
                runCatching {
                    notifier.notify(type, *bodies)
                }.onFailure {
                    log.error("Failed to send notification via ${notifier.providerType}", it)
                }
            }
            ?: log.warn("No notifier found for provider: $targetProvider")
    }
}

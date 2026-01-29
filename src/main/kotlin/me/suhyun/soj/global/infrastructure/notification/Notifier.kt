package me.suhyun.soj.global.infrastructure.notification

import me.suhyun.soj.global.infrastructure.notification.model.enums.NotificationType
import me.suhyun.soj.global.infrastructure.notification.model.enums.ProviderType

interface Notifier {
    val providerType: ProviderType
    fun notify(type: NotificationType, vararg bodies: Any)
}

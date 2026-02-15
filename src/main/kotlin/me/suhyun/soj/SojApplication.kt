package me.suhyun.soj

import me.suhyun.soj.global.infrastructure.notification.config.NotificationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableConfigurationProperties(NotificationProperties::class)
@EnableAsync
@EnableKafka
class SojApplication

fun main(args: Array<String>) {
    runApplication<SojApplication>(*args)
}

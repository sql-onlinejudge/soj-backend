package me.suhyun.soj.global.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenValidity: Long = 3600000,
    val refreshTokenValidity: Long = 604800000
)

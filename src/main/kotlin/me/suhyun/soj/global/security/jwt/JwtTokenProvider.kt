package me.suhyun.soj.global.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import me.suhyun.soj.domain.user.domain.model.User
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun createAccessToken(user: User): String {
        return createToken(user, jwtProperties.accessTokenValidity)
    }

    fun createRefreshToken(user: User): String {
        return createToken(user, jwtProperties.refreshTokenValidity)
    }

    private fun createToken(user: User, validity: Long): String {
        val now = Date()
        val expiration = Date(now.time + validity)

        return Jwts.builder()
            .subject(user.uuid.toString())
            .claim("role", user.role.name)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = parseClaims(token)
            !claims.expiration.before(Date())
        } catch (e: SecurityException) {
            log.warn("Invalid JWT signature: ${e.message}")
            false
        } catch (e: MalformedJwtException) {
            log.warn("Invalid JWT token: ${e.message}")
            false
        } catch (e: ExpiredJwtException) {
            log.warn("Expired JWT token: ${e.message}")
            false
        } catch (e: UnsupportedJwtException) {
            log.warn("Unsupported JWT token: ${e.message}")
            false
        } catch (e: IllegalArgumentException) {
            log.warn("JWT claims string is empty: ${e.message}")
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val uuid = UUID.fromString(claims.subject)
        val role = claims["role"]?.toString() ?: "USER"
        val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
        return UsernamePasswordAuthenticationToken(uuid, null, authorities)
    }

    fun getUuid(token: String): UUID {
        return UUID.fromString(parseClaims(token).subject)
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}

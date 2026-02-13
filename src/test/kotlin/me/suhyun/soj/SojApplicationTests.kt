package me.suhyun.soj

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Requires full infrastructure (DB, Redis, etc.)")
class SojApplicationTests {

    @Test
    fun contextLoads() {
    }

}

package me.suhyun.soj.domain.stats.presentation

import me.suhyun.soj.domain.stats.application.service.StatsService
import me.suhyun.soj.domain.stats.presentation.response.StatsResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stats")
class StatsController(
    private val statsService: StatsService
) {

    @GetMapping
    fun getStats(): StatsResponse = statsService.getStats()
}

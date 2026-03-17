package me.suhyun.soj.domain.grading.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
import me.suhyun.soj.domain.grading.infrastructure.sse.RunSseEmitterService
import me.suhyun.soj.domain.run.domain.model.RunResult
import me.suhyun.soj.domain.run.domain.model.enums.RunStatus
import me.suhyun.soj.domain.run.presentation.response.RunResultResponse
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ExecutionResultConsumer(
    private val runSseEmitterService: RunSseEmitterService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["execution-results"], groupId = "execution-result-group")
    fun consume(message: String) {
        val event = objectMapper.readValue(message, ExecutionResultEvent::class.java)

        when (event.status) {
            RunStatus.IN_PROGRESS -> runSseEmitterService.send(event.runId, event.status, null, null)
            RunStatus.COMPLETED -> {
                val resultMap: Map<Long, String> = objectMapper.readValue(
                    event.resultJson!!,
                    object : TypeReference<Map<Long, String>>() {}
                )
                val responses = resultMap.map { (testCaseId, tsv) ->
                    val parsed = RunResult.parse(tsv)
                    RunResultResponse(
                        testCaseId = testCaseId,
                        columns = parsed.columns,
                        rows = parsed.rows,
                        errorMessage = null
                    )
                }
                runSseEmitterService.send(event.runId, event.status, responses, null)
            }
            RunStatus.FAILED -> runSseEmitterService.send(event.runId, event.status, null, event.errorMessage)
            else -> {}
        }
    }
}

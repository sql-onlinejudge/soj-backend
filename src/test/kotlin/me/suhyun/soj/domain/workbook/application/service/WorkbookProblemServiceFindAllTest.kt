package me.suhyun.soj.domain.workbook.application.service

import me.suhyun.soj.domain.problem.domain.model.Problem
import me.suhyun.soj.domain.problem.domain.model.enums.TrialStatus
import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.submission.domain.repository.SubmissionRepository
import me.suhyun.soj.domain.workbook.domain.model.WorkbookProblem
import me.suhyun.soj.domain.workbook.domain.repository.WorkbookProblemRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class WorkbookProblemServiceFindAllTest {

    @Mock
    private lateinit var workbookProblemRepository: WorkbookProblemRepository

    @Mock
    private lateinit var problemRepository: ProblemRepository

    @Mock
    private lateinit var submissionRepository: SubmissionRepository

    private lateinit var workbookProblemService: WorkbookProblemService

    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        workbookProblemService = WorkbookProblemService(workbookProblemRepository, problemRepository, submissionRepository)
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(userId, null, listOf(SimpleGrantedAuthority("ROLE_USER")))
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should return paginated problems for workbook with trialStatus`() {
        val workbookProblems = listOf(
            WorkbookProblem(id = 1L, workbookId = 1L, problemId = 10L),
            WorkbookProblem(id = 2L, workbookId = 1L, problemId = 20L),
        )

        whenever(workbookProblemRepository.findAllByWorkbookId(1L, 0, 20)).thenReturn(workbookProblems)
        whenever(workbookProblemRepository.countByWorkbookId(1L)).thenReturn(2L)
        whenever(problemRepository.findById(10L)).thenReturn(createProblem(10L, "Problem A"))
        whenever(problemRepository.findById(20L)).thenReturn(createProblem(20L, "Problem B"))
        whenever(submissionRepository.getTrialStatuses(listOf(10L, 20L), userId))
            .thenReturn(mapOf(10L to true))

        val result = workbookProblemService.findAll(1L, 0, 20)

        assertThat(result.content).hasSize(2)
        assertThat(result.totalElements).isEqualTo(2L)
        assertThat(result.content[0].title).isEqualTo("Problem A")
        assertThat(result.content[0].trialStatus).isEqualTo(TrialStatus.SOLVED)
        assertThat(result.content[1].title).isEqualTo("Problem B")
        assertThat(result.content[1].trialStatus).isEqualTo(TrialStatus.NOT_ATTEMPTED)
    }

    @Test
    fun `should return empty page when no problems in workbook`() {
        whenever(workbookProblemRepository.findAllByWorkbookId(1L, 0, 20)).thenReturn(emptyList())
        whenever(workbookProblemRepository.countByWorkbookId(1L)).thenReturn(0L)

        val result = workbookProblemService.findAll(1L, 0, 20)

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0L)
    }

    private fun createProblem(id: Long, title: String): Problem {
        return Problem(
            id = id,
            title = title,
            description = "desc",
            schemaSql = "CREATE TABLE t (id INT);",
            schemaMetadata = null,
            difficulty = 1,
            timeLimit = 1000,
            isOrderSensitive = false,
            solvedCount = 0,
            submissionCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }
}

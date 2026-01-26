package me.suhyun.soj.domain.testcase.application.service

import me.suhyun.soj.domain.problem.domain.repository.ProblemRepository
import me.suhyun.soj.domain.testcase.domain.model.TestCase
import me.suhyun.soj.domain.testcase.domain.repository.TestCaseRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TestCaseServiceFindAllTest {

    @Mock
    private lateinit var testCaseRepository: TestCaseRepository

    @Mock
    private lateinit var problemRepository: ProblemRepository

    private lateinit var testCaseService: TestCaseService

    @BeforeEach
    fun setUp() {
        testCaseService = TestCaseService(testCaseRepository, problemRepository)
    }

    private fun createTestCase(id: Long, problemId: Long): TestCase {
        return TestCase(
            id = id,
            problemId = problemId,
            initSql = "INSERT INTO test VALUES ($id)",
            answer = "$id",
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            deletedAt = null
        )
    }

    @Test
    fun `should return all testcases for problem`() {
        val problemId = 1L
        val testCases = listOf(
            createTestCase(1L, problemId),
            createTestCase(2L, problemId),
            createTestCase(3L, problemId)
        )

        whenever(testCaseRepository.findAllByProblemId(problemId)).thenReturn(testCases)

        val result = testCaseService.findAll(problemId)

        assertThat(result).hasSize(3)
        assertThat(result[0].id).isEqualTo(1L)
        assertThat(result[1].id).isEqualTo(2L)
        assertThat(result[2].id).isEqualTo(3L)
    }
}

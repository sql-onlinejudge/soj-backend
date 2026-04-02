package me.suhyun.soj.domain.problem.presentation.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import me.suhyun.soj.domain.problem.domain.model.SchemaMetadata
import me.suhyun.soj.domain.problem.domain.model.enums.ProblemCategory

data class UpdateProblemRequest(
    val title: String? = null,
    val description: String? = null,
    val schemaMetadata: SchemaMetadata? = null,

    @field:Min(1)
    @field:Max(5)
    val difficulty: Int? = null,

    @field:Positive
    val timeLimit: Int? = null,

    val isOrderSensitive: Boolean? = null,

    val category: ProblemCategory? = null
)

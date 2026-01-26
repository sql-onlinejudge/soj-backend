package me.suhyun.soj.global.exception

data class ErrorResponse(
    val httpStatus: Int,
    val code: String,
    val message: String
) {
    companion object {
        fun of(errorCode: ErrorCode) = ErrorResponse(
            httpStatus = errorCode.status.value(),
            code = (errorCode as Enum<*>).name,
            message = errorCode.message
        )
    }
}

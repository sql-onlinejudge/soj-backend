package me.suhyun.soj.global.common.entity

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

class JsonColumnType<T : Any>(
    private val klass: Class<T>
) : ColumnType() {

    private val objectMapper = ObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    override fun sqlType(): String = "JSON"

    override fun valueFromDB(value: Any): T {
        val json = when (value) {
            is String -> value
            is ByteArray -> String(value)
            else -> value.toString()
        }
        return objectMapper.readValue(json, klass)
    }

    override fun valueToDB(value: Any?): Any? {
        return value?.let { objectMapper.writeValueAsString(it) }
    }

    override fun notNullValueToDB(value: Any): Any {
        return objectMapper.writeValueAsString(value)
    }
}

inline fun <reified T : Any> Table.json(name: String): Column<T> =
    registerColumn(name, JsonColumnType(T::class.java))

package io.github.dector.exercism.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.UUID

fun moshi(): Moshi = Moshi.Builder()
    .add(UUID::class.java, UuidAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private class UuidAdapter : JsonAdapter<UUID>() {

    override fun fromJson(reader: JsonReader): UUID? =
        reader.nextString()?.let(UUID::fromString)

    override fun toJson(writer: JsonWriter, value: UUID?) {
        writer.value(value?.toString())
    }
}

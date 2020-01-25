package io.github.dector.exercism.track

import com.squareup.moshi.Json
import io.github.dector.exercism.utils.moshi
import java.io.File
import java.util.UUID

data class TrackConfig(
    val version: Int,
    val language: String,
    val blurb: String,
    val exercises: Exercises,
    @Json(name = "online_editor") val onlineEditor: OnlineEditor,
    @Json(name = "solution_pattern") val solutionPattern: String,
    val active: Boolean
) {

    data class Exercises(
        val concept: List<Exercise>,
        val practice: List<Exercise>
    )

    data class Exercise(
        val slug: String,
        val uuid: UUID,
        val concepts: List<String>,
        val prerequisites: List<String>
    )

    data class OnlineEditor(
        @Json(name = "indent_style") val indentStyle: String,
        @Json(name = "indent_size") val indentSize: Int
    )
}

fun loadTrackConfig(dir: File): TrackConfig {
    val file = dir.resolve("config.json")

    return moshi()
        .adapter(TrackConfig::class.java)
        .fromJson(file.readText())
        ?: error("Can't load track config from `${file.absolutePath}`")
}
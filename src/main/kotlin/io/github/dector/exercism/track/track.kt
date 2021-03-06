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
    @Json(name = "solution_pattern") val solutionPattern: String?,
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

fun TrackConfig.Exercises.all(): List<TrackConfig.Exercise> =
    concept + practice

fun TrackConfig.newExercise(slug: String): Pair<TrackConfig, TrackConfig.Exercise> {
    val exercise = TrackConfig.Exercise(
        slug = slug,
        uuid = newExerciseUuid(),
        concepts = emptyList(),
        prerequisites = emptyList()
    )


    val newConfig = run {
        val newConceptExercises = exercises.concept + exercise
        val newExercises = exercises.copy(concept = newConceptExercises)
        copy(exercises = newExercises)
    }

    return newConfig to exercise
}

fun TrackConfig.hasExerciseWith(slug: String) =
    exercises.all().any { it.slug == slug }

private fun TrackConfig.newExerciseUuid(): UUID {
    var uuid = UUID.randomUUID()

    while (exercises.all().any() { it.uuid == uuid }) {
        uuid = UUID.randomUUID()
    }

    return uuid
}

private fun File.configFile(): File = resolve("config.json")
fun File.exerciseDir(slug: String, isConcept: Boolean): File {
    val part = if (isConcept) "concept" else "practice"
    return resolve("exercises/$part/$slug")
}

fun loadTrackConfig(dir: File): TrackConfig {
    val file = dir.configFile()

    return moshi()
        .adapter(TrackConfig::class.java)
        .fromJson(file.readText())
        ?: error("Can't load track config from `${file.absolutePath}`")
}

fun TrackConfig.saveToProject(dir: File) {
    val file = dir.configFile()
    check(file.exists()) { "Config file '${file.absolutePath}' not found." }

    val json = moshi()
        .adapter(TrackConfig::class.java)
        .indent("  ")
        .toJson(this)
    file.writeText(json)
}

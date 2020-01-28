package io.github.dector.exercism.checks

import io.github.dector.exercism.track.TrackConfig

typealias Check = (CheckContext) -> Unit

data class CheckContext(val trackConfig: TrackConfig)

fun CheckContext.reportOk() {
    println("OK")
}

fun CheckContext.reportError(message: String, details: String? = null) {
    println("ERR")
    println(message)

    if (details != null) println(details)
}

fun uniqueIdsForExercises(context: CheckContext) {
    val allExercises = context.trackConfig.exercises.let { it.concept + it.practice }
    val duplicatedExercises = allExercises.groupBy { it.uuid }
        .filter { it.value.size > 1 }

    if (duplicatedExercises.isEmpty()) {
        context.reportOk()
    } else {
        val details = duplicatedExercises
            .map { (uuid, exercises) ->
                val exercisesStr = exercises.joinToString(prefix = "[", postfix = "]") { it.slug }
                "$uuid -> $exercisesStr"
            }
            .joinToString("\n")

        context.reportError("This exercises have duplicated UUIDs:", details)
    }
}

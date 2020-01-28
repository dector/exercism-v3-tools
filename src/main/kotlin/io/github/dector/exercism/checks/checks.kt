package io.github.dector.exercism.checks

import io.github.dector.exercism.track.TrackConfig

typealias Check = (CheckContext) -> Unit

data class CheckContext(val trackConfig: TrackConfig)

fun uniqueIdsForExercises(context: CheckContext) {
    val allExercises = context.trackConfig.exercises.let { it.concept + it.practice }
    val duplicatedExercises = allExercises.groupBy { it.uuid }
        .filter { it.value.size > 1 }

    if (duplicatedExercises.isEmpty()) {
        println("OK")
    } else {
        println("ERR")
        println("This exercises have duplicated UUIDs:")

        duplicatedExercises.forEach { (uuid, exercises) ->
            val exercisesStr = exercises.joinToString(prefix = "[", postfix = "]") { it.slug }
            println("$uuid -> $exercisesStr")
        }
    }
}

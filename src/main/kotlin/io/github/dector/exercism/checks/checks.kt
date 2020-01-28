package io.github.dector.exercism.checks

import io.github.dector.exercism.track.TrackConfig
import io.github.dector.exercism.track.exerciseDir
import java.io.File

typealias Check = (CheckContext) -> Unit

data class CheckContext(
    val projectDir: File,
    val trackConfig: TrackConfig
)

data class DocFiles(
    val dir: File
) {
    val introductionFile = dir.resolve("introduction.md")
    val instructionsFile = dir.resolve("instructions.md")
    val hintsFile = dir.resolve("hints.md")
    val afterFile = dir.resolve("after.md")

    companion object
}

fun CheckContext.docFilesFor(exercise: TrackConfig.Exercise, isConcept: Boolean = true): DocFiles {
    val docsDir = projectDir
        .exerciseDir(exercise.slug, isConcept = isConcept)
        .resolve(".docs")
    return DocFiles(
        dir = docsDir
    )
}

fun CheckContext.reportOk() {
    println("OK")
}

fun CheckContext.reportError(message: String, details: String? = null) {
    println("ERR: $message")

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

fun requiredImplementationExists(context: CheckContext) {
    val exercisesWithDocs = context.trackConfig.exercises.concept
        .map { it to context.docFilesFor(it, isConcept = true) }

    exercisesWithDocs.forEach { (exercise, docFiles) ->
        if (docFiles.dir.exists()) {
            val errorFiles = mutableListOf<File>()

            listOf(
                docFiles.introductionFile to "Introduction file",
                docFiles.instructionsFile to "Instructions file",
                docFiles.hintsFile to "Hints file",
                docFiles.afterFile to "After-info file"
            ).forEach { (file, title) ->
                val isRequired = file != docFiles.afterFile

                if (!file.exists()) {
                    if (isRequired) {
                        context.reportError("$title not found at '${file.absolutePath}'")
                        errorFiles += file
                    }
                } else if (file.readLines().any { it.startsWith("- THIS DIFF IS JUST DRAFT MARKER") }) {
                    context.reportError("$title for exercise '${exercise.slug}' is in a draft state")
                    errorFiles += file
                }
            }

            if (errorFiles.isEmpty()) context.reportOk()
        } else {
            context.reportError("Documentation not found for exercise '${exercise.slug}'",
                "Searched in '${docFiles.dir.normalize().absolutePath}'")
        }
    }
}

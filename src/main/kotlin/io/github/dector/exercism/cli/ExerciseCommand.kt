package io.github.dector.exercism.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.github.dector.exercism.track.TrackConfig
import io.github.dector.exercism.track.hasExerciseWith
import io.github.dector.exercism.track.loadTrackConfig
import io.github.dector.exercism.track.newExercise
import io.github.dector.exercism.track.saveToProject
import io.github.dector.exercism.utils.ExerciseContext
import io.github.dector.exercism.utils.asExerciseSlug
import io.github.dector.exercism.utils.fillWithFilesFrom
import io.github.dector.exercism.utils.from
import io.github.dector.exercism.utils.gradleWrappers
import io.github.dector.exercism.utils.sourcesDir
import io.github.dector.exercism.utils.testsDir
import java.io.File

class ExerciseCommand : CliktCommand(name = "exercise") {
    override fun run() {
    }

    class Create : CliktCommand(name = "create") {

        private val projectDir: File by option("--projectDir")
            .file(exists = true, folderOkay = true, fileOkay = false)
            .required()

        override fun run() {
            val (trackConfig, newExercise) = promptAndCreateExercise(loadTrackConfig(projectDir))

            bootstrapFileTreeFor(projectDir, newExercise)

            println("Writing `config.json`...")
            trackConfig.saveToProject(projectDir)

            println("Done.")
        }
    }
}

private fun bootstrapFileTreeFor(projectDir: File, exercise: TrackConfig.Exercise) {
    println("Creating files tree...")

    val exerciseContext = ExerciseContext.from(projectDir, exercise.slug)
        .also { it.dir.mkdirs() }

    exerciseContext.fillWithFilesFrom(
        projectDir.resolve(".templates/exercise"))

    exerciseContext.sourcesDir().apply {
        mkdirs()
        resolve(".keep").delete()
        resolve("${exercise.sourceFilesName()}.kt").createNewFile()
    }
    exerciseContext.testsDir().apply {
        mkdirs()
        resolve(".keep").delete()
        resolve("${exercise.testFilesName()}.kt").createNewFile()
    }

    exerciseContext.gradleWrappers().forEach { it.setExecutable(true) }
}

private fun promptAndCreateExercise(config: TrackConfig): Pair<TrackConfig, TrackConfig.Exercise> {
    var slug: String? = null

    var askForName = true
    while (askForName) {
        print("Enter exercise name (e.g. 'Reverse string'): ")
        val enteredSlug = readLine()!!
            .asExerciseSlug()

        when {
            enteredSlug.isBlank() ->
                println("Err: Name shouldn't be blank")
            config.hasExerciseWith(enteredSlug) ->
                println("Err: Exercise with this slug already exists")
            else -> {
                println("OK")
                slug = enteredSlug
                askForName = false
            }
        }
    }

    return config.newExercise(slug!!)
}

fun TrackConfig.Exercise.sourceFilesName() = slug.split("-").joinToString("") { it.capitalize() }
fun TrackConfig.Exercise.testFilesName() = "${sourceFilesName()}Test"

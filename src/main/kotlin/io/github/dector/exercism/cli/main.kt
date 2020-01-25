package io.github.dector.exercism.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.github.dector.exercism.track.TrackConfig
import io.github.dector.exercism.track.loadTrackConfig
import io.github.dector.exercism.track.saveToProject
import java.io.File
import java.util.UUID

fun main(args: Array<String>) {
    MainCommand().subcommands(
        ExerciseCommand().subcommands(
            ExerciseCommand.Create()
        ),
        DoctorCommand()
    ).main(args)
}

class MainCommand : CliktCommand() {
    override fun run() {
    }
}

class DoctorCommand : CliktCommand(name = "doctor") {

    val projectDir: File by option("--projectDir")
        .file(exists = true, folderOkay = true, fileOkay = false)
        .required()

    override fun run() {
        verifyConfig()
    }

    private fun verifyConfig() {
        println("Verifying `config.json`")
        val config = loadTrackConfig(projectDir)

        println("Concept exercises: ${config.exercises.concept.size}")
        println("Practice exercises: ${config.exercises.practice.size}")

        run {
            print("Unique uuids: ")
            val allExercises = config.exercises.let { it.concept + it.practice }
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
    }
}

class ExerciseCommand : CliktCommand(name = "exercise") {
    override fun run() {
    }

    class Create : CliktCommand(name = "create") {

        private val projectDir: File by option("--projectDir")
            .file(exists = true, folderOkay = true, fileOkay = false)
            .required()

        override fun run() {
            var slug = ""
            val uuid = UUID.randomUUID()
            val concepts = mutableListOf<String>()
            val prerequisites = mutableListOf<String>()

            // slug
            run {
                println("Enter exercise slug (e.g. 'Reverse string'):")
                val value = readLine()!!
                    .map { if (it.isLetterOrDigit()) it else ' ' }
                    .joinToString("")
                    .trim()
                    .split(" ").joinToString("-") { it.toLowerCase() }

                slug = value
            }

            // build exercise
            val exercise = TrackConfig.Exercise(
                slug = slug,
                uuid = uuid,
                concepts = concepts,
                prerequisites = prerequisites
            )

            println("Writing to `config.json`...")

            // merge with existing config
            val config = loadTrackConfig(projectDir)
                .let { config ->
                    val newConceptExercises = config.exercises.concept + exercise
                    val newExercises = config.exercises.copy(concept = newConceptExercises)
                    config.copy(exercises = newExercises)
                }

            config.saveToProject(projectDir)
        }
    }
}

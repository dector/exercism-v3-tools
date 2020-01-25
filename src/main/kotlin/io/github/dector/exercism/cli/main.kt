package io.github.dector.exercism.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.github.dector.exercism.track.hasExerciseWith
import io.github.dector.exercism.track.loadTrackConfig
import io.github.dector.exercism.track.newExercise
import io.github.dector.exercism.track.saveToProject
import java.io.File

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
            val existingConfig = loadTrackConfig(projectDir)

            val newExercise = run {
                var slug: String? = null

                var askForName = true
                while (askForName) {
                    print("Enter exercise name (e.g. 'Reverse string'): ")
                    val enteredSlug = readLine()!!
                        .asExerciseSlug()

                    when {
                        existingConfig.hasExerciseWith(enteredSlug) ->
                            System.err.println("Err: Exercise with this slug already exists")
                        enteredSlug.isBlank() ->
                            System.err.println("Err: Name shouldn't be blank")
                        else -> {
                            println("OK")
                            slug = enteredSlug
                            askForName = false
                        }
                    }
                }

                existingConfig.newExercise(slug!!)
            }

            println("Writing to `config.json`...")
            run {
                val config = loadTrackConfig(projectDir)
                    .let { config ->
                        val newConceptExercises = config.exercises.concept + newExercise
                        val newExercises = config.exercises.copy(concept = newConceptExercises)
                        config.copy(exercises = newExercises)
                    }
                config.saveToProject(projectDir)
            }

        }
    }
}

private fun String.asExerciseSlug() = this
    .map { if (it.isLetterOrDigit()) it else ' ' }
    .joinToString("")
    .split(" ")
    .map(String::trim)
    .joinToString("-") { it.toLowerCase() }

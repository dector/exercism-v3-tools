package io.github.dector.exercism.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.github.dector.exercism.track.loadTrackConfig
import java.io.File

fun main(args: Array<String>) {
    MainCommand().subcommands(
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

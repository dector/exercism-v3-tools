package io.github.dector.exercism.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import io.github.dector.exercism.checks.Check
import io.github.dector.exercism.checks.CheckContext
import io.github.dector.exercism.checks.requiredImplementationExists
import io.github.dector.exercism.checks.uniqueIdsForExercises
import io.github.dector.exercism.track.loadTrackConfig
import java.io.File

class DoctorCommand : CliktCommand(name = "doctor") {

    private val projectDir: File by option("--projectDir")
        .file(exists = true, folderOkay = true, fileOkay = false)
        .required()

    private val checks = mapOf<String, Check>(
        "Unique uuids" to ::uniqueIdsForExercises,
        "Required implementation" to ::requiredImplementationExists
    )

    override fun run() {
        verifyConfig()
    }

    private fun verifyConfig() {
        println("Verifying `config.json`")
        val config = loadTrackConfig(projectDir)

        println("Concept exercises: ${config.exercises.concept.size}")
        println("Practice exercises: ${config.exercises.practice.size}")

        val context = CheckContext(
            projectDir = projectDir,
            trackConfig = config
        )

        checks.forEach { (name, check) ->
            println("[Check] $name:")
            check(context)
        }
    }
}


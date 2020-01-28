package io.github.dector.exercism.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

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

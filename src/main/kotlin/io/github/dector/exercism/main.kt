package io.github.dector.exercism

import io.github.dector.exercism.track.loadTrackConfig
import java.io.File

fun main(args: Array<String>) {
    parseKotlinTrackConfig(args[0])
}

private fun parseKotlinTrackConfig(dir: String) {
    val config = loadTrackConfig(File(dir))

    println(config)
}

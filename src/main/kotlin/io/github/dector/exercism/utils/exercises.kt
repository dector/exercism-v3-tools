package io.github.dector.exercism.utils

fun String.asExerciseSlug() = this
    .map { if (it.isLetterOrDigit()) it else ' ' }
    .joinToString("")
    .split(" ")
    .map(String::trim)
    .joinToString("-") { it.toLowerCase() }

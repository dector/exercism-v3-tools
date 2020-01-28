package io.github.dector.exercism.utils

import java.io.File

data class ExerciseContext(val dir: File) {
    companion object
}

fun ExerciseContext.Companion.from(projectDir: File, exerciseSlug: String) =
    ExerciseContext(dir = projectDir.resolve("exercises/concept/$exerciseSlug"))

fun ExerciseContext.sourcesDir(): File = dir.resolve("src/main/kotlin")
fun ExerciseContext.testsDir(): File = dir.resolve("src/test/kotlin")
fun ExerciseContext.gradleWrappers(): List<File> = listOf(dir.resolve("gradlew"), dir.resolve("gradle.bat"))
fun ExerciseContext.fillWithFilesFrom(sourceDir: File) = sourceDir.copyRecursively(this.dir)

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.kotlin
    application
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    //implementation(Deps.kotlin_coroutines)

    implementation(Deps.moshi)
    implementation(Deps.moshi_adapters)
    implementation(Deps.moshi_kotlin)

    implementation(Deps.clikt)

    testImplementation(Deps.kotlin_test)
}

allprojects {
    group = "io.github.dector.${rootProject.name}"
    version = "0.1-SNAPSHOT"

    repositories {
        jcenter()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}

application {
    mainClassName = "io.github.dector.exercism.cli.MainKt"
}

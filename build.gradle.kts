import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktlint by configurations.creating

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version  "1.7.10"
    id("com.intershop.gradle.javacc") version "4.0.1"
    application
}

javacc {
    // configuration container for all javacc configurations
    javaCCVersion = "7.0.11"
    configs {
        register("turtlestar") {
            inputFile = file("src/main/javacc/turtlestar.jj")
            packageName = "me.alejandrorm.klosure.parser.turtle"
            lookahead = 1
        }
        register("sparqlstar") {
            inputFile = file("src/main/javacc/sparqlstar.jj")
            packageName = "me.alejandrorm.klosure.parser.sparql"
            lookahead = 1
        }
    }
}

group = "me.alejandrorm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.sourceforge.owlapi:owlapi-distribution:5.1.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.3")
    testImplementation(kotlin("test:1.7.0"))
    testImplementation("org.hamcrest:hamcrest:2.2")
    ktlint("com.pinterest:ktlint:0.46.1") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

val outputDir = "${project.buildDir}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    outputs.dir(outputDir)

    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("-F", "src/**/*.kt")
}

application {
    mainClass.set("MainKt")
}
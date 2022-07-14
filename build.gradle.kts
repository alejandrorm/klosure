import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.intershop.gradle.javacc") version "4.0.1"
    application
}

javacc {
    // configuration container for all javacc configurations
    javaCCVersion = "7.0.11"
    configs {
        register("template") {
            inputFile = file("src/main/javacc/turtlestar.jj")
            packageName = "me.alejandrorm.klosure.parser"
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
    testImplementation(kotlin("test"))
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
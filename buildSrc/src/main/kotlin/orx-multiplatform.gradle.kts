import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import ScreenshotsHelper.collectScreenshots
import org.gradle.kotlin.dsl.project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.apiVersion = "1.6"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        val openrndrVersion = ((findProperty("OPENRNDR.version")?:System.getenv("OPENRNDR_VERSION")) ?.toString()?.replace("v","")) ?: "0.5.1-SNAPSHOT"
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
                implementation("org.openrndr:openrndr-application:$openrndrVersion")
                implementation("org.openrndr:openrndr-draw:$openrndrVersion")
                implementation("org.openrndr:openrndr-filter:$openrndrVersion")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20")
                implementation("io.github.microutils:kotlin-logging:2.1.10")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                implementation("io.kotest:kotest-assertions-core:5.2.1")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-junit5"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                runtimeOnly("org.junit.jupiter:junit-jupiter-api:5.8.2")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
                implementation("org.spekframework.spek2:spek-dsl-jvm:2.0.15")
                implementation("org.amshove.kluent:kluent:1.68")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
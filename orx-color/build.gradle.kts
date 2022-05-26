import ScreenshotsHelper.collectScreenshots

plugins {
    `orx-multiplatform`
}

apply(from = "../dependencies.gradle.kts")

println("???")
println(ext["openrndrVersion"])
println(ext["openrndrOS"])

kotlin {
    jvm {
        compilations {
            val demo by creating {
                defaultSourceSet {
                    kotlin.srcDir("src/demo")
                    dependencies {
                        implementation(project(":orx-camera"))
                        implementation(project(":orx-mesh-generators"))
                        implementation(project(":orx-color"))
                        implementation(project(":orx-jvm:orx-gui"))

                        implementation(libs.openrndr.application)
                        implementation(libs.openrndr.extensions)
                        runtimeOnly(libs.openrndr.gl3)
                        runtimeOnly(libs.openrndr.gl3.natives)

                        implementation(compilations["main"]!!.output.allOutputs)
                    }
                }
                collectScreenshots { }
            }
        }
    }

    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation(project(":orx-parameters"))
                implementation(project(":orx-shader-phrases"))
            }
        }
    }
}
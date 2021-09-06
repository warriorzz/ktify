import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("org.jetbrains.dokka") version "1.5.0"
}

group = "io.github.warriorzz"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://schlaubi.jfrog.io/artifactory/envconf/")
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:1.6.3"))
    implementation("io.ktor", "ktor-client-okhttp")
    implementation("io.ktor", "ktor-client-serialization")

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.2")
    implementation("io.github.microutils", "kotlin-logging-jvm", "2.0.11")
    implementation("org.slf4j", "slf4j-simple", "1.7.31")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
    dokkaHtml {
        moduleName.set("ktify")
        dokkaSourceSets {
            configureEach {
                jdkVersion.set(11)
                platform.set(org.jetbrains.dokka.Platform.jvm)

                perPackageOption {
                    matchingRegex.set(".*.model.*")
                    suppress.set(true)
                }
                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))

                    skipEmptyPackages.set(true)

                    remoteUrl.set(uri("https://github.com/warriorzz/ktify/tree/main/src/main/kotlin").toURL())
                    remoteLineSuffix.set("#L")
                }
            }
        }
    }
}

ktlint {
    verbose.set(true)
    filter {
        disabledRules.add("no-wildcard-imports")
        disabledRules.add("no-multi-spaces")
        disabledRules.add("indent")

        exclude("**/build/**")
    }
}

apply(from = "publishing.gradle.kts")

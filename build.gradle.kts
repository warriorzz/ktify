import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "ee.bjarn"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:2.3.10"))
    implementation("io.ktor", "ktor-client-okhttp")
    implementation("io.ktor", "ktor-serialization-kotlinx-json")
    implementation("io.ktor", "ktor-client-content-negotiation")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.6.3")

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.8.0")
    implementation("io.github.microutils", "kotlin-logging-jvm", "3.0.5")
    implementation("org.slf4j", "slf4j-simple", "2.0.13")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
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

java {
    // This avoids a Gradle warning
    sourceCompatibility = JavaVersion.VERSION_11
}

apply(from = "publishing.gradle.kts")

plugins {
    kotlin("jvm")
}

group = "com.github.warriorzz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))

    implementation(platform("io.ktor:ktor-bom:1.6.2"))
    implementation("io.ktor", "ktor-server-core")
    implementation("io.ktor", "ktor-server-cio")
    implementation("io.ktor", "ktor-serialization")
    implementation("io.ktor", "ktor-client-okhttp")
    implementation("io.ktor", "ktor-client-serialization")

    implementation("io.github.cdimascio", "dotenv-kotlin", "6.2.2")
}

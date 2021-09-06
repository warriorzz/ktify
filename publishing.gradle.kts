apply(plugin = "org.gradle.maven-publish")
apply(plugin = "org.gradle.signing")
apply(plugin = "org.jetbrains.dokka")

val sonatypeUsername = project.findProperty("sonatypeUsername").toString()
val sonatypePassword = project.findProperty("sonatypePassword").toString()

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    val dokkaHtml = tasks.getByName("dokkaHtml")
    dependsOn(dokkaHtml)
    from(dokkaHtml)
}

val configurePublishing: PublishingExtension.() -> Unit = {
    repositories {
        maven {
            name = "oss"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
    publications {
        filterIsInstance<MavenPublication>().forEach { publication ->
            publication.artifact(javadocJar)
            publication.pom {
                name.set(project.name)
                description.set("A coroutine based wrapper around the Spotify Web API, written in Kotlin.")
                url.set("https://github.com/warriorzz/ktify")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/warriorzz/ktify/blob/main/LICENSE")
                    }
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/warriorzz/ktify/issues")
                }

                scm {
                    connection.set("https://github.com/warriorzz/ktify.git")
                    url.set("https://github.com/warriorzz/ktify")
                }

                developers {
                    developer {
                        name.set("Bjarne Eberhardt")
                        email.set("warriormayer@gmail.com")
                        url.set("https://warriorzz.github.io")
                        timezone.set("Europe/Berlin")
                    }
                }
            }
        }
    }
}

val configureSigning: SigningExtension.() -> Unit = {
    val signingKey = findProperty("signingKey")?.toString()
    val signingPassword = findProperty("signingPassword")?.toString()
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(
            String(java.util.Base64.getDecoder().decode(signingKey.toByteArray())),
            signingPassword
        )
    }

    publishing.publications.withType<MavenPublication> {
        sign(this)
    }
}

extensions.configure("signing", configureSigning)
extensions.configure("publishing", configurePublishing)

val Project.publishing: PublishingExtension
    get() =
        (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension

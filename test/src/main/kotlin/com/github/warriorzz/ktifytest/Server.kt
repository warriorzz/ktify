package com.github.warriorzz.ktifytest

import com.github.warriorzz.ktify.KtifyBuilder
import com.github.warriorzz.ktify.model.player.CurrentPlayingTrack
import io.github.cdimascio.dotenv.dotenv
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

suspend fun server() = embeddedServer(CIO, port = 8080) {
    val dotenv = dotenv()
    val clientId = dotenv["CLIENT_ID"]
    val clientSecret = dotenv["CLIENT_SECRET"]

    routing {
        get("/") {
            val authorizationCode = call.parameters["code"]
            if (authorizationCode != null) {
                val ktify = KtifyBuilder(clientId, clientSecret, authorizationCode, "http://localhost:8080").build()
                println((ktify.player.getCurrentPlayback() as? CurrentPlayingTrack?)?.item?.name)
            }
            call.respondRedirect("https://warriorzz.github.io")
        }
    }
}.start(wait = true)

suspend fun main() {
    server()
}

package com.github.warriorzz.ktify.player

import com.github.warriorzz.ktify.Ktify
import com.github.warriorzz.ktify.model.auth.Scope
import com.github.warriorzz.ktify.model.player.AvailableDevices
import com.github.warriorzz.ktify.model.player.CurrentPlayback
import com.github.warriorzz.ktify.model.player.CurrentPlayingTrack
import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

@OptIn(ExperimentalStdlibApi::class)
class KtifyPlayer internal constructor(val ktify: Ktify) {

    suspend fun getCurrentPlayback(market: String? = null, additionalTypes: String? = null): CurrentPlayback? {
        if (ktify.requestHelper.makeRequest(
                httpMethod = HttpMethod.Get,
                url = ktify.requestHelper.baseUrl + "me/player",
                parameters = buildMap {
                    if (market != null) {
                        "market" to market
                    }
                    if (additionalTypes != null) {
                        "additional_types" to additionalTypes
                    }
                },
                headers = null,
                body = null,
                requiresScope = Scope.USER_READ_PLAYBACK_STATE
            ) != HttpStatusCode.OK
        ) {
            return null
        }
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Get,
            url = ktify.requestHelper.baseUrl + "me/player",
            parameters = buildMap {
                if (market != null) {
                    "market" to market
                }
                if (additionalTypes != null) {
                    "additional_types" to additionalTypes
                }
            },
            headers = null,
            neededElement = "is_playing",
            deserializationStrategy = CurrentPlayback.serializer()
        )
    }

    suspend fun transferPlayback(deviceId: String, play: Boolean = false): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Put,
            url = ktify.requestHelper.baseUrl + "me/player",
            parameters = mapOf(
                "device_ids" to "{device_ids:[\"$deviceId\"]}",
                "play" to play.toString()
            ),
            headers = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    suspend fun getAvailableDevices(): AvailableDevices {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Get,
            url = ktify.requestHelper.baseUrl + "me/player/devices",
            parameters = null,
            headers = null,
            requiresAuthentication = true,
            requiresScope = Scope.USER_READ_PLAYBACK_STATE
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getCurrentPlayingTrack(market: String? = null, additionalTypes: String? = null): CurrentPlayingTrack? {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Get,
            url = ktify.requestHelper.baseUrl + "me/player/currently-playing",
            parameters = buildMap {
                if (market != null) {
                    "market" to market
                }
                if (additionalTypes != null) {
                    "additional_types" to additionalTypes
                }
            },
            headers = null,
            neededElement = "is_playing",
            deserializationStrategy = CurrentPlayingTrack.serializer(),
            requiresScope = Scope.USER_READ_CURRENTLY_PLAYING
        )
    }

    suspend fun resumePlayback(
        deviceId: String? = null,
        contextUri: String? = null,
        uris: List<String>? = null,
        offset: JsonObject? = null,
        positionMs: Int? = null
    ): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Put,
            url = ktify.requestHelper.baseUrl + "me/player/play",
            parameters = buildMap {
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = buildJsonObject {
                contextUri?.let {
                    put("context_uri", contextUri)
                }
                uris?.let {
                    put("uris", buildJsonArray {
                        uris.forEach { add(Json.parseToJsonElement(it)) }
                    })
                }
                offset?.let {
                    put("offset", offset)
                }
                positionMs?.let {
                    put("position_ms", positionMs)
                }
            },
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    // TODO: player methods

}

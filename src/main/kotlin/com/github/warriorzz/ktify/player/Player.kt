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

    /**
     *  Read the user's current playback.
     *  @param  market  (Optional) [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) code of a country. Can also be 'from_token', equivalent to the current users country.
     *  @return The [CurrentPlayback](https://github.com/warriorzz/ktify/blob/ac318a38f72f770893f2c4c9cf64dc18a2e05a86/src/main/kotlin/com/github/warriorzz/ktify/model/player/CurrentPlayback.kt#L16) object, can be null if nothing is currently played
     */
    suspend fun getCurrentPlayback(market: String? = null): CurrentPlayback? {
        if (ktify.requestHelper.makeRequest(
                httpMethod = HttpMethod.Get,
                url = ktify.requestHelper.baseUrl + "me/player",
                parameters = buildMap {
                    if (market != null) {
                        "market" to market
                    }
                    "additional_types" to "track" // TODO: Add episode
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
                "additional_types" to "track" // TODO: Add episode
            },
            headers = null,
            neededElement = "is_playing",
            deserializationStrategy = CurrentPlayback.serializer()
        )
    }

    /**
     *  Transfer playback to another device
     *  @param  deviceId    The ID of the device to which the playback should be transferred
     *  @param  play        If true, the playback will start, if false or not provided, the current playback state will be kept
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
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

    /**
     *  Get a list of the available devices
     *  @return The [AvailableDevices](https://github.com/warriorzz/ktify/blob/ac318a38f72f770893f2c4c9cf64dc18a2e05a86/src/main/kotlin/com/github/warriorzz/ktify/model/player/Devices.kt#L7) object
     */
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

    /**
     *  Get the track currently played on the user's account
     *  @param  market  (Optional) [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) code of a country. Can also be 'from_token', equivalent to the current users country.
     *  @return The [CurrentPlayingTrack](https://github.com/warriorzz/ktify/blob/ac318a38f72f770893f2c4c9cf64dc18a2e05a86/src/main/kotlin/com/github/warriorzz/ktify/model/player/CurrentPlayback.kt#L38) object, can be null if no track is currently played
     */
    suspend fun getCurrentPlayingTrack(market: String? = null): CurrentPlayingTrack? {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Get,
            url = ktify.requestHelper.baseUrl + "me/player/currently-playing",
            parameters = buildMap {
                if (market != null) {
                    "market" to market
                }
            },
            headers = null,
            neededElement = "is_playing",
            deserializationStrategy = CurrentPlayingTrack.serializer(),
            requiresScope = Scope.USER_READ_CURRENTLY_PLAYING
        )
    }

    /**
     *  Start or resume the user's playback
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
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

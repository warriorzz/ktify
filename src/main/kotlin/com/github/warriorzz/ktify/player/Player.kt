package com.github.warriorzz.ktify.player

import com.github.warriorzz.ktify.Ktify
import com.github.warriorzz.ktify.model.LinkedTrack
import com.github.warriorzz.ktify.model.Track
import com.github.warriorzz.ktify.model.auth.Scope
import com.github.warriorzz.ktify.model.player.*
import io.ktor.http.*
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
    suspend fun startPlayback(
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

    /**
     *  Pause the user's playback
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun pausePlayback(deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Put,
            url = ktify.requestHelper.baseUrl + "me/player/pause",
            parameters = buildMap {
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Skip the user's playback to the next track
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun skipToNextTrack(deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Post,
            url = ktify.requestHelper.baseUrl + "me/player/next",
            parameters = buildMap {
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Skip the user's playback to the previous track
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun skipToPreviousTrack(deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Post,
            url = ktify.requestHelper.baseUrl + "me/player/previous",
            parameters = buildMap {
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Seek to a specific position in the user's playback
     *  @param  positionMs  The position in milliseconds. Must be positive, if it is greater than the songs length, the player will skip to the next song.
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun seekToPosition(positionMs: Int, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Put,
            url = ktify.requestHelper.baseUrl + "me/player/seek",
            parameters = buildMap {
                "position_ms" to positionMs
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Set the repeat mode of the user's player
     *  @param  repeatState   The repeat state, represented by the [RepeatState](https://github.com/warriorzz/ktify/blob/39a37be45a471a4e2530256340f6c00d6a8dc4cd/src/main/kotlin/com/github/warriorzz/ktify/model/player/CurrentPlayback.kt#L89) enum class
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun setRepeatMode(repeatState: RepeatState, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Put,
            url = ktify.requestHelper.baseUrl + "me/player/repeat",
            parameters = buildMap {
                "state" to repeatState
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Set the volume of the user's player
     *  @param  volumePercent   The volume percentage (must be a value from 0 to 100 inclusive.)
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.BadRequest] if the volume wasn't between 0 and 100,  [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun setVolume(volumePercent: Int, deviceId: String? = null): HttpStatusCode {
        if (volumePercent > 100 || volumePercent < 0) {
            return HttpStatusCode.BadRequest
        }
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Put,
            url = ktify.requestHelper.baseUrl + "me/player/volume",
            parameters = buildMap {
                "volume_percent" to volumePercent
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Set the shuffle state of the user's player
     *  @param  shuffleState   The new shuffle state
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun toggleShufflePlayback(shuffleState: Boolean, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Put,
            url = ktify.requestHelper.baseUrl + "me/player/shuffle",
            parameters = buildMap {
                "state" to shuffleState
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Gets the recently played tracks of the user
     *  @param  limit   The maximum number of items returned. Needs to be between 1 and 50, otherwise it will be 20
     *  @param  after   Timestamp in milliseconds, returns items after this position (not including), either after or before must be present
     *  @param  before  Timestamp in milliseconds, returns items before this position (not including), either after or before must be present
     *  @return The [PlayHistoryCursorPagingObject]. Null if either the requirements aren't met or a private session was enabled.
     */
    suspend fun getRecentlyPlayedTracks(
        limit: Int? = null,
        after: Long? = null,
        before: Int? = null
    ): PlayHistoryCursorPagingObject? {
        if (after == null && before == null) {
            return null
        }
        if (ktify.requestHelper.makeRequest(
                httpMethod = HttpMethod.Get,
                url = ktify.requestHelper.baseUrl + "me/player/recently-played",
                parameters = buildMap {
                    limit?.let {
                        if (it in 1..50)
                            "limit" to limit
                    }
                    after?.let {
                        "after" to after
                    }
                    before?.let {
                        "before" to before
                    }
                },
                headers = null,
                body = null,
                requiresScope = Scope.USER_READ_RECENTLY_PLAYER
            ) != HttpStatusCode.OK
        ) {
            return null
        }
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Get,
            url = ktify.requestHelper.baseUrl + "me/player/recently-played",
            parameters = buildMap {
                limit?.let {
                    if (it in 1..50)
                        "limit" to limit
                }
                after?.let {
                    "after" to after
                }
                before?.let {
                    "before" to before
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_READ_RECENTLY_PLAYER,
            requiresAuthentication = true,
        )
    }

    /**
     *  Will be internal once the whole API is covered
     */
    suspend fun addItemToQueue(uri: String, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            httpMethod = HttpMethod.Post,
            url = ktify.requestHelper.baseUrl + "me/player/queue",
            parameters = buildMap {
                "uri" to uri
                deviceId?.let {
                    "device_id" to deviceId
                }
            },
            headers = null,
            body = null,
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        )
    }

    /**
     *  Adds a track to the user's queue
     *  @param  track   The track to add to the queue
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun addItemToQueue(track: Track, deviceId: String?) = addItemToQueue(track.uri, deviceId)

    /**
     *  Adds a track to the user's queue
     *  @param  track   The track to add to the queue
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun addItemToQueue(track: LinkedTrack, deviceId: String?) = addItemToQueue(track.uri, deviceId)
    // TODO: Episode
}

package ee.bjarn.ktify.player

import ee.bjarn.ktify.Ktify
import ee.bjarn.ktify.model.Episode
import ee.bjarn.ktify.model.Track
import ee.bjarn.ktify.model.auth.Scope
import ee.bjarn.ktify.model.player.*
import ee.bjarn.ktify.model.track.LinkedTrack
import io.ktor.client.request.*
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
                requiresScope = Scope.USER_READ_PLAYBACK_STATE
            ) {
                method = HttpMethod.Get
                url.takeFrom(ktify.requestHelper.baseUrl + "me/player")
                if (market != null) {
                    parameter("market", market)
                }
                parameter("additional_types", "track,episode")
            } != HttpStatusCode.OK
        ) {
            return null
        }
        return ktify.requestHelper.makeRequest(
            neededElement = "is_playing",
            deserializationStrategy = CurrentPlayback.serializer()
        ) {
            method = HttpMethod.Get
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player")
            if (market != null) {
                parameter("market", market)
            }
            parameter("additional_types", "track,episode")
        }
    }

    /**
     *  Transfer playback to another device
     *  @param  deviceId    The ID of the device to which the playback should be transferred
     *  @param  play        If true, the playback will start, if false or not provided, the current playback state will be kept
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun transferPlayback(deviceId: String, play: Boolean = false): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Put
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player")
            parameter("device_ids", "{device_ids:[\"$deviceId\"]}")
            parameter("play", play.toString())
        }
    }

    /**
     *  Get a list of the available devices
     *  @return The [AvailableDevices](https://github.com/warriorzz/ktify/blob/ac318a38f72f770893f2c4c9cf64dc18a2e05a86/src/main/kotlin/com/github/warriorzz/ktify/model/player/Devices.kt#L7) object
     */
    suspend fun getAvailableDevices(): AvailableDevices {
        return ktify.requestHelper.makeRequest(
            requiresAuthentication = true,
            requiresScope = Scope.USER_READ_PLAYBACK_STATE
        ) {
            method = HttpMethod.Get
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/devices")
        }
    }

    /**
     *  Get the track currently played on the user's account
     *  @param  market  (Optional) [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) code of a country. Can also be 'from_token', equivalent to the current users country.
     *  @return The [CurrentPlayingTrack](https://github.com/warriorzz/ktify/blob/ac318a38f72f770893f2c4c9cf64dc18a2e05a86/src/main/kotlin/com/github/warriorzz/ktify/model/player/CurrentPlayback.kt#L38) object, can be null if no track is currently played
     */
    suspend fun getCurrentPlayingTrack(market: String? = null): CurrentPlayingTrack? {
        return ktify.requestHelper.makeRequest(
            neededElement = "is_playing",
            deserializationStrategy = CurrentPlayingTrack.serializer(),
            requiresScope = Scope.USER_READ_CURRENTLY_PLAYING
        ) {
            method = HttpMethod.Get
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/currently-playing")
            if (market != null) {
                parameter("market", market)
            }
        }
    }

    /**
     *  Start or resume the user's playback, click [here](https://developer.spotify.com/documentation/web-api/reference/#category-player) for further context
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun startPlayback(
        deviceId: String? = null,
        contextUri: String? = null,
        uris: List<String>? = null,
        offset: JsonObject? = null,
        positionMs: Int? = null
    ): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Put
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/play")
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
            setBody(buildJsonObject {
                if (contextUri != null) {
                    put("context_uri", contextUri)
                }
                if (uris != null) {
                    put(
                        "uris",
                        buildJsonArray {
                            uris.forEach { add(Json.parseToJsonElement(it)) }
                        }
                    )
                }
                if (offset != null) {
                    put("offset", offset)
                }
                if (positionMs != null) {
                    put("position_ms", positionMs)
                }
            })
        }
    }

    /**
     *  Pause the user's playback
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun pausePlayback(deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Put
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/pause")
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
    }

    /**
     *  Skip the user's playback to the next track
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun skipToNextTrack(deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Post
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/next")
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
    }

    /**
     *  Skip the user's playback to the previous track
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun skipToPreviousTrack(deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Post
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/previous")
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
    }

    /**
     *  Seek to a specific position in the user's playback
     *  @param  positionMs  The position in milliseconds. Must be positive, if it is greater than the songs length, the player will skip to the next song.
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun seekToPosition(positionMs: Int, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Put
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/seek")
            parameter("position_ms", positionMs.toString())
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
    }

    /**
     *  Set the repeat mode of the user's player
     *  @param  repeatState   The repeat state, represented by the [RepeatState](https://github.com/warriorzz/ktify/blob/39a37be45a471a4e2530256340f6c00d6a8dc4cd/src/main/kotlin/com/github/warriorzz/ktify/model/player/CurrentPlayback.kt#L89) enum class
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun setRepeatMode(repeatState: RepeatState, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Put
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/repeat")
            parameter("state", Json.encodeToString(RepeatState.serializer(), repeatState).replace("\"", ""))
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
    }

    /**
     *  Set the volume of the user's player
     *  @param  volumePercent   The volume percentage (must be a value from 0 to 100 inclusive.)
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.BadRequest] if the volume wasn't between 0 and 100,  [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun setVolume(volumePercent: Int, deviceId: String? = null): HttpStatusCode {
        if (volumePercent > 100 || volumePercent < 0) {
            return HttpStatusCode.BadRequest
        }
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Put
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/volume")
            parameter("volume_percent", volumePercent.toString())
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
    }

    /**
     *  Set the shuffle state of the user's player
     *  @param  shuffleState   The new shuffle state
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun toggleShufflePlayback(shuffleState: Boolean, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Put
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/shuffle")
            parameter("state", shuffleState.toString())
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
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
                requiresScope = Scope.USER_READ_RECENTLY_PLAYED
            ) {
                method = HttpMethod.Get
                url.takeFrom(ktify.requestHelper.baseUrl + "me/player/recently-played")
                if (limit != null && limit in 1..50) {
                    parameter("limit", limit.toString())
                }
                if (after != null) {
                    parameter("after", after.toString())
                }
                if (before != null) {
                    parameter("before", before.toString())
                }
            } != HttpStatusCode.OK
        ) {
            return null
        }
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_READ_RECENTLY_PLAYED,
            requiresAuthentication = true
        ) {
            method = HttpMethod.Get
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/recently-played")
            if (limit != null && limit in 1..50) {
                parameter("limit", limit.toString())
            }
            if (after != null) {
                parameter("after", after.toString())
            }
            if (before != null) {
                parameter("before", before.toString())
            }
        }
    }

    /**
     *  Will be internal once the whole API is covered
     */
    suspend fun addItemToQueue(uri: String, deviceId: String? = null): HttpStatusCode {
        return ktify.requestHelper.makeRequest(
            requiresScope = Scope.USER_MODIFY_PLAYBACK_STATE
        ) {
            method = HttpMethod.Post
            url.takeFrom(ktify.requestHelper.baseUrl + "me/player/queue")
            parameter("uri", uri)
            if (deviceId != null) {
                parameter("device_id", deviceId)
            }
        }
    }

    /**
     *  Adds a track to the user's queue
     *  @param  track   The track to add to the queue
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun addItemToQueue(track: Track, deviceId: String?) = addItemToQueue(track.uri, deviceId)

    /**
     *  Adds a track to the user's queue
     *  @param  track   The track to add to the queue
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun addItemToQueue(track: LinkedTrack, deviceId: String?) = addItemToQueue(track.uri, deviceId)

    /**
     *  Adds an episode to the user's queue
     *  @param  episode   The episode to add to the queue
     *  @param  deviceId    The device ID, if not provided, the user's current active device is targeted
     *  @return [HttpStatusCode.NoContent] if the request succeeded, [HttpStatusCode.NotFound] if the device was not found, [HttpStatusCode.Forbidden] if the user is non-premium
     */
    suspend fun addItemToQueue(episode: Episode, deviceId: String?) = addItemToQueue(episode.uri, deviceId)
}

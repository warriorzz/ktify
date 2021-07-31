package com.github.warriorzz.ktify.model

import com.github.warriorzz.ktify.model.external.ExternalId
import com.github.warriorzz.ktify.model.external.ExternalUrl
import com.github.warriorzz.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  The album object of the <a href=https://developer.spotify.com/documentation/web-api/reference/#object-simplifiedtrackobject>Spotify Web API</a>.
 *  @see <a href=https://developer.spotify.com/documentation/web-api/reference/#object-trackobject>Spotify reference</a>
 */
@Serializable
data class Track(
    val album: Album? = null,
    val artists: List<Artist>,
    @SerialName("available_markets")
    val availableMarkets: List<String>,
    @SerialName("disc_number")
    val discNumber: Int,
    @SerialName("duration_ms")
    val durationMs: Int,
    val explicit: Boolean,
    @SerialName("external_urls")
    val externalUrls: ExternalUrl? = null,
    @SerialName("external_ids")
    val externalIds: ExternalId? = null,
    val href: String,
    val id: String,
    @SerialName("is_local")
    val isLocal: Boolean,
    @SerialName("is_playable")
    val isPlayable: Boolean? = null,
    @SerialName("linked_from")
    val linkedFrom: LinkedTrack? = null,
    val name: String,
    val popularity: Int,
    @SerialName("preview_url")
    val previewUrl: String? = null,
    val restrictions: TrackRestriction? = null,
    @SerialName("track_number")
    val trackNumber: Int,
    val type: ObjectType = ObjectType.TRACK,
    val uri: String,
)

@Serializable
data class LinkedTrack(
    @SerialName("external_urls")
    val externalUrls: ExternalUrl,
    val href: String,
    val id: String,
    val type: ObjectType = ObjectType.TRACK,
    val uri: String,
)

@Serializable
data class TuneableTrack(
    val acousticness: Float,
    val danceability: Float,
    @SerialName("duration_ms")
    val durationMs: Int,
    val energy: Float,
    val instrumentalness: Float,
    val key: Int,
    val liveness: Float,
    val loudness: Float,
    val mode: Int,
    val popularity: Float,
    val speechiness: Float,
    val tempo: Float,
    @SerialName("time_signature")
    val timeSignature: Int,
    val valence: Float
)

@Serializable
data class SavedTrack(
    @SerialName("added_at")
    val addedAt: String,
    val track: Track,
)

@Serializable
data class TrackActions(
    @SerialName("is_playing")
    val isPlaying: Boolean? = null,
    val disallows: TrackActionsDisallows
)

@Serializable
data class TrackActionsDisallows(
    val pausing: Boolean? = null,
    val resuming: Boolean? = null
)

@Serializable
data class TrackRestriction(
    val reason: RestrictionType
)

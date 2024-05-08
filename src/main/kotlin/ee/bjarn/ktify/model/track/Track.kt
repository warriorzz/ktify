package ee.bjarn.ktify.model.track

import ee.bjarn.ktify.model.*
import ee.bjarn.ktify.model.external.ExternalUrl
import ee.bjarn.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
data class TrackRestriction(
    val reason: RestrictionType
)

@Serializable
data class TracksResponse(
    val tracks: List<Track>
)

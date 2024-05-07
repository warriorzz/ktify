package ee.bjarn.ktify.model

import ee.bjarn.ktify.model.external.ExternalId
import ee.bjarn.ktify.model.external.ExternalUrl
import ee.bjarn.ktify.model.track.LinkedTrack
import ee.bjarn.ktify.model.track.TrackRestriction
import ee.bjarn.ktify.model.util.ObjectType
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
    override val type: ObjectType = ObjectType.TRACK,
    val uri: String,
) : KtifyObject()

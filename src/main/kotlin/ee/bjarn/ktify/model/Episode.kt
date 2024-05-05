package ee.bjarn.ktify.model

import ee.bjarn.ktify.model.external.ExternalUrl
import ee.bjarn.ktify.model.util.Image
import ee.bjarn.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Episode(
    @SerialName("audio_preview_url")
    val audioPreviewUrl: String? = null,
    val description: String,
    @SerialName("duration_ms")
    val durationMs: Long,
    val explicit: Boolean,
    @SerialName("external_urls")
    val externalUrl: ExternalUrl,
    val href: String,
    @SerialName("html_description")
    val htmlDescription: String,
    val id: String,
    val images: List<Image>,
    @SerialName("is_externally_hosted")
    val isExternallyHosted: Boolean,
    @SerialName("is_playable")
    val isPlayable: Boolean,
    val languages: List<String>,
    val name: String,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("release_date_precision")
    val releaseDatePrecision: ReleaseDatePrecision,
    val restrictions: EpisodeRestriction,
    @SerialName("resume_point")
    val resumePoint: ResumePoint? = null,
    val show: JsonObject,
    override val type: ObjectType = ObjectType.EPISODE,
    val uri: String,
) : KtifyObject()

@Serializable
data class EpisodePagingObject(
    val href: String,
    val items: List<Episode>,
    val limit: Int,
    val next: String? = null,
    val offset: Int,
    val previous: String? = null,
    val total: Int,
)

@Serializable
data class SavedEpisodeObject(
    @SerialName("added_at")
    val addedAt: String,
    val episode: Episode,
)

@Serializable
data class EpisodeRestriction(
    val reason: RestrictionType,
)

@Serializable
data class ResumePoint(
    @SerialName("fully_played")
    val fullyPlayed: Boolean,
    @SerialName("resume_position_ms")
    val resumePositionMs: Long,
)

package com.github.warriorzz.ktify.model

import com.github.warriorzz.ktify.model.external.ExternalId
import com.github.warriorzz.ktify.model.external.ExternalUrl
import com.github.warriorzz.ktify.model.util.Image
import com.github.warriorzz.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  The album object of the <a href=https://developer.spotify.com/documentation/web-api/reference/#object-albumobject>Spotify Web API</a>.
 *  @see <a href=https://developer.spotify.com/documentation/web-api/reference/#object-albumobject>Spotify reference</a>
 */
@Serializable
data class Album(
    @SerialName("album_group")
    val albumGroup: AlbumGroup? = null,
    @SerialName("album_type")
    val albumType: AlbumType,
    val artists: List<Artist>,
    @SerialName("available_markets")
    val availableMarkets: List<String>,
    val copyrights: List<Copyright>? = null,
    @SerialName("external_ids")
    val externalIds: ExternalId? = null,
    @SerialName("external_urls")
    val externalUrls: ExternalUrl,
    val genres: List<String>? = null,
    val href: String,
    val id: String,
    val images: List<Image>,
    val label: String? = null,
    val name: String,
    val popularity: Int? = null,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("release_date_precision")
    val releaseDatePrecision: ReleaseDatePrecision,
    val restrictions: List<AlbumRestriction>? = null,
    @SerialName("total_tracks")
    val totalTracks: Int,
    val tracks: List<Track>? = null,
    override val type: ObjectType = ObjectType.ALBUM,
    val uri: String,
) : KtifyObject()

@Serializable
data class AlbumPagingObject(
    val href: String,
    val items: List<Album>,
    val limit: Int,
    val next: String? = null,
    val offset: Int,
    val previous: String? = null,
    val total: Int,
)

@Serializable
data class AlbumRestriction(
    val reason: RestrictionType,
)

@Serializable
enum class AlbumType {
    @SerialName("album")
    ALBUM,

    @SerialName("single")
    SINGLE,

    @SerialName("compilation")
    COMPILATION,
}

@Serializable
enum class AlbumGroup {
    @SerialName("album")
    ALBUM,

    @SerialName("single")
    SINGLE,

    @SerialName("compilation")
    COMPILATION,

    @SerialName("appears_on")
    APPEARS_ON,
}

@Serializable
enum class ReleaseDatePrecision {
    @SerialName("year")
    YEAR,

    @SerialName("month")
    MONTH,

    @SerialName("day")
    DAY,
}

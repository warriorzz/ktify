package com.github.warriorzz.ktify.model

import com.github.warriorzz.ktify.model.external.ExternalUrl
import com.github.warriorzz.ktify.model.user.Followers
import com.github.warriorzz.ktify.model.util.Image
import com.github.warriorzz.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  The album object of the <a href=https://developer.spotify.com/documentation/web-api/reference/#object-artistobject>Spotify Web API</a>.
 *  @see <a href=https://developer.spotify.com/documentation/web-api/reference/#object-artistobject>Spotify reference</a>
 */
@Serializable
data class Artist(
    @SerialName("external_urls")
    val externalUrls: ExternalUrl,
    val followers: Followers? = null,
    val genres: List<String>? = null,
    val href: String,
    val id: String,
    val images: List<Image>? = null,
    val name: String,
    val popularity: Int? = null,
    val type: ObjectType = ObjectType.ARTIST,
    val uri: String,
)

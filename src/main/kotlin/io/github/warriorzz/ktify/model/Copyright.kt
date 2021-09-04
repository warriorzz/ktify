package io.github.warriorzz.ktify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  The album object of the <a href=https://developer.spotify.com/documentation/web-api/reference/#object-copyrightobject>Spotify Web API</a>.
 *  @see <a href=https://developer.spotify.com/documentation/web-api/reference/#object-copyrightobject>Spotify reference</a>
 */
@Serializable
data class Copyright(
    val text: String,
    val type: CopyrightType,
)

@Serializable
enum class CopyrightType {
    @SerialName("c")
    COPYRIGHT,

    @SerialName("p")
    PERFORMANCE,
}

@Serializable
enum class RestrictionType {
    @SerialName("market")
    MARKET,

    @SerialName("product")
    PRODUCT,

    @SerialName("explicit")
    EXPLICIT,

    @SerialName("unknown")
    UNKNOWN
}

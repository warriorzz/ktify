package io.github.warriorzz.ktify.model.util

import io.github.warriorzz.ktify.model.external.ExternalUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Context(
    @SerialName("external_urls")
    val externalUrls: ExternalUrl,
    val href: String,
    val type: ObjectType,
    val uri: String,
)

@Serializable
enum class ObjectType {
    @SerialName("artist")
    ARTIST,

    @SerialName("playlist")
    PLAYLIST,

    @SerialName("album")
    ALBUM,

    @SerialName("show")
    SHOW,

    @SerialName("track")
    TRACK,

    @SerialName("episode")
    EPISODE,

    @SerialName("user")
    USER,
}

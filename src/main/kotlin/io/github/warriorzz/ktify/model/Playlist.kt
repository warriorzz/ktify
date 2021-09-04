package io.github.warriorzz.ktify.model

import io.github.warriorzz.ktify.model.external.ExternalUrl
import io.github.warriorzz.ktify.model.util.Image
import io.github.warriorzz.ktify.model.util.ObjectType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class Playlist(
    val collaborative: Boolean,
    val description: String? = null,
    @SerialName("external_urls")
    val externalUrl: ExternalUrl,
    val followers: Followers,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val owner: PublicUser,
    val public: Boolean? = null,
    @SerialName("snapshot_id")
    val snapshotId: String,
    val tracks: List<PlaylistTrackObject>? = null,
    override val type: ObjectType = ObjectType.PLAYLIST,
    val uri: String,
) : KtifyObject()

@Serializable
data class PlaylistTrack(
    @SerialName("added_at")
    val addedAt: String? = null,
    @SerialName("added_by")
    val addedBy: PublicUser? = null,
    @SerialName("is_local")
    val isLocal: Boolean,
    val track: KtifyObject,
) : PlaylistTrackObject()

@Serializable
data class PlaylistTrackRef(
    val href: String,
    val total: Int
) : PlaylistTrackObject()

@Serializable(with = PlaylistTrackObjectSerializer::class)
sealed class PlaylistTrackObject {

    /**
     *  Get the instance as a PlaylistTrack object
     *  @return Null if the instance is an PlaylistTrackRef
     */
    fun asPlaylistTrack(): PlaylistTrack? = if (this is PlaylistTrack) this else null

    /**
     *  Get the instance as a PlaylistTrackRef object
     *  @return Null if the instance is an PlaylistTrack
     */
    fun asPlaylistTrackRef(): PlaylistTrackRef? = if (this is PlaylistTrackRef) this else null
}

object PlaylistTrackObjectSerializer : JsonContentPolymorphicSerializer<PlaylistTrackObject>(PlaylistTrackObject::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out PlaylistTrackObject> {
        return if (element.jsonObject["track"] != null) PlaylistTrack.serializer() else PlaylistTrackRef.serializer()
    }
}

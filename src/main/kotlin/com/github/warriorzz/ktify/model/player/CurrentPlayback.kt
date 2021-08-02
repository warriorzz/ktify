package com.github.warriorzz.ktify.model.player

import com.github.warriorzz.ktify.model.Track
import com.github.warriorzz.ktify.model.TrackActions
import com.github.warriorzz.ktify.model.util.Context
import com.github.warriorzz.ktify.model.util.Device
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = CurrentPlaybackSerializer::class)
sealed class CurrentPlayback {
    abstract val timestamp: Long

    @SerialName("progress_ms")
    abstract val progressMs: Long
    abstract val device: Device?

    @SerialName("is_playing")
    abstract val isPlaying: Boolean

    @SerialName("currently_playing_type")
    abstract val currentlyPlayingType: String

    @SerialName("shuffle_state")
    abstract val shuffleState: Boolean?

    @SerialName("repeat_state")
    abstract val repeatState: String?
    abstract val context: Context?
}

@Serializable
class CurrentPlayingTrack(
    override val timestamp: Long,
    @SerialName("progress_ms")
    override val progressMs: Long,
    override val device: Device? = null,
    @SerialName("is_playing")
    override val isPlaying: Boolean,
    @SerialName("currently_playing_type")
    override val currentlyPlayingType: String,
    @SerialName("shuffle_state")
    override val shuffleState: Boolean? = null,
    @SerialName("repeat_state")
    override val repeatState: String? = null,
    override val context: Context?,
    val item: Track? = null,
    val actions: TrackActions,
    val resuming: Boolean? = null
) : CurrentPlayback()

// TODO: Episode

@Serializable
class CurrentPlaybackNull(
    @Serializable(with = AnyAsEmptyStringSerializer::class)
    val item: Any,
    override val timestamp: Long,
    @SerialName("progress_ms")
    override val progressMs: Long,
    override val device: Device? = null,
    @SerialName("is_playing")
    override val isPlaying: Boolean,
    @SerialName("currently_playing_type")
    override val currentlyPlayingType: String,
    @SerialName("shuffle_state")
    override val shuffleState: Boolean? = null,
    @SerialName("repeat_state")
    override val repeatState: String? = null,
    override val context: Context?
) : CurrentPlayback()

object CurrentPlaybackSerializer : JsonContentPolymorphicSerializer<CurrentPlayback>(CurrentPlayback::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out CurrentPlayback> {
        return when (element.jsonObject["item"]?.jsonObject?.get("type")?.jsonPrimitive?.content) {
            "track" -> CurrentPlayingTrack.serializer()
            else -> CurrentPlaybackNull.serializer()
        }
    }

}

@Serializable
enum class RepeatState {
    @SerialName("track")
    TRACK,

    @SerialName("context")
    CONTEXT,

    @SerialName("off")
    OFF,
}

object AnyAsEmptyStringSerializer : KSerializer<Any> {
    override fun deserialize(decoder: Decoder): Any {
        return ""
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("kotlin-any", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        encoder.encodeString("")
    }

}

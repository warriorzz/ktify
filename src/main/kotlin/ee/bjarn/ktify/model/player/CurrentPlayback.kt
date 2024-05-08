package ee.bjarn.ktify.model.player

import ee.bjarn.ktify.model.Episode
import ee.bjarn.ktify.model.Track
import ee.bjarn.ktify.model.util.Context
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

    abstract val actions: CurrentPlaybackActions
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
    override val actions: CurrentPlaybackActions,
    @SerialName("smart_shuffle")
    val smartShuffle: Boolean?,
    val item: Track? = null,
    val resuming: Boolean? = null
) : CurrentPlayback()

@Serializable
class CurrentPlayingEpisode(
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
    override val actions: CurrentPlaybackActions,
    val item: Episode? = null,
    val resuming: Boolean? = null
) : CurrentPlayback()

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
    override val context: Context?,
    override val actions: CurrentPlaybackActions,
) : CurrentPlayback()

@Serializable
data class CurrentPlaybackActions(
    val disallows: Disallows
)

@Serializable
data class Disallows(
    @SerialName("interrupting_playback")
    val interruptingPlayback: Boolean? = null,
    val pausing: Boolean? = null,
    val resuming: Boolean? = null,
    val seeking: Boolean? = null,
    @SerialName("skipping_next")
    val skippingNext: Boolean? = null,
    @SerialName("skipping_prev")
    val skippingPrev: Boolean? = null,
    @SerialName("toggling_repeat_context")
    val togglingRepeatContext: Boolean? = null,
    @SerialName("toggling_shuffle")
    val togglingShuffle: Boolean? = null,
    @SerialName("toggling_repeat_track")
    val togglingRepeatTrack: Boolean? = null,
    @SerialName("transferring_playback")
    val transferringPlayback: Boolean? = null,
)

object CurrentPlaybackSerializer : JsonContentPolymorphicSerializer<CurrentPlayback>(CurrentPlayback::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<CurrentPlayback> {
        return when (element.jsonObject["item"]?.jsonObject?.get("type")?.jsonPrimitive?.content) {
            "track" -> CurrentPlayingTrack.serializer()
            "episode" -> CurrentPlayingEpisode.serializer()
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

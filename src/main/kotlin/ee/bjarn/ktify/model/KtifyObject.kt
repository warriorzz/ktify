package ee.bjarn.ktify.model

import ee.bjarn.ktify.model.util.ObjectType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
sealed class KtifyObject {

    abstract val type: ObjectType
}

@Serializable
data class RawKtifyObject(
    override val type: ObjectType
) : KtifyObject()

object KtifyObjectSerializer : JsonContentPolymorphicSerializer<KtifyObject>(KtifyObject::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out KtifyObject> {
        return when (element.jsonObject["item"]?.jsonObject?.get("type")?.jsonPrimitive?.content) {
            "track" -> Track.serializer()
            "album" -> Album.serializer()
            "artist" -> Artist.serializer()
            "user" -> PublicUser.serializer()
            "episode" -> Episode.serializer()
            "show" -> Show.serializer()
            else -> RawKtifyObject.serializer()
        }
    }
}

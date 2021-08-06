package com.github.warriorzz.ktify.model

import com.github.warriorzz.ktify.model.util.ObjectType
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
            else -> RawKtifyObject.serializer()
        }
    }
}

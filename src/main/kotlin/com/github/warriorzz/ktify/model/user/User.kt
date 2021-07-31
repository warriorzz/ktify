package com.github.warriorzz.ktify.model.user

import com.github.warriorzz.ktify.model.external.ExternalUrl
import com.github.warriorzz.ktify.model.util.Image
import com.github.warriorzz.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CurrentProfile(
    val country: String? = null,
    @SerialName("display_name")
    val displayName: String,
    val email: String? = null,
    @SerialName("explicit_content")
    val explicitContent: JsonObject? = null, // TODO
    @SerialName("external_urls")
    val externalUrl: ExternalUrl,
    val followers: Followers,
    val href: String,
    val id: String,
    val images: List<Image>,
    val product: String? = null,
    val type: ObjectType = ObjectType.USER,
    val uri: String
)

// TODO: other profiles
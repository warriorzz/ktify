package ee.bjarn.ktify.model

import ee.bjarn.ktify.model.external.ExternalUrl
import ee.bjarn.ktify.model.util.Image
import ee.bjarn.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentUser(
    val country: String? = null,
    @SerialName("display_name")
    val displayName: String,
    val email: String? = null,
    @SerialName("explicit_content")
    val explicitContent: ExplicitContentSettings? = null,
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

@Serializable
data class PublicUser(
    @SerialName("display_name")
    val displayName: String,
    @SerialName("external_urls")
    val externalUrl: ExternalUrl,
    val followers: Followers,
    val href: String,
    val id: String,
    val images: List<Image>,
    override val type: ObjectType = ObjectType.USER,
    val uri: String
) : KtifyObject()

@Serializable
data class UserPagingObject(
    val href: String,
    val items: List<PublicUser>,
    val limit: Int,
    val next: String? = null,
    val offset: Int,
    val previous: String? = null,
    val total: Int,
)

@Serializable
data class ExplicitContentSettings(
    @SerialName("filter_enabled")
    val filterEnabled: Boolean,
    @SerialName("filter_locked")
    val filterLocked: Boolean,
)

@Serializable
data class Followers(
    val href: String? = null,
    val total: Int
)

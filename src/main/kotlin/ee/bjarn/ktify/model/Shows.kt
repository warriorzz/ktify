package ee.bjarn.ktify.model

import ee.bjarn.ktify.model.external.ExternalUrl
import ee.bjarn.ktify.model.util.Image
import ee.bjarn.ktify.model.util.ObjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Show(
    @SerialName("available_markets")
    val availableMarkets: List<String>,
    val copyrights: List<Copyright>,
    val description: String,
    val episodes: List<Episode>,
    val explicit: Boolean,
    @SerialName("external_urls")
    val externalUrl: ExternalUrl,
    val href: String,
    @SerialName("html_description")
    val htmlDescription: String,
    val id: String,
    val images: List<Image>,
    @SerialName("is_externally_hosted")
    val isExternallyHosted: Boolean,
    val languages: List<String>,
    @SerialName("media_type")
    val mediaType: String,
    val name: String,
    val publisher: String,
    override val type: ObjectType = ObjectType.SHOW,
    val uri: String,
) : KtifyObject()

@Serializable
data class SavedShowObject(
    @SerialName("added_at")
    val addedAt: String,
    val show: Show,
)

@Serializable
data class ShowPagingObject(
    val href: String,
    val items: List<Show>,
    val limit: Int,
    val next: String? = null,
    val offset: Int,
    val previous: String? = null,
    val total: Int,
)

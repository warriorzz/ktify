package ee.bjarn.ktify.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginationObject<T>(
    val href: String,
    val limit: Int,
    val offset: Int,
    val total: Int,
    val items: List<T>,
    val previous: String? = null,
    val next: String? = null,
)

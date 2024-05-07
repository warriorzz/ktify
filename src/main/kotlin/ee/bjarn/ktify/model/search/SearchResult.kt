package ee.bjarn.ktify.model.search

import ee.bjarn.ktify.model.*
import ee.bjarn.ktify.model.Track
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val tracks: PaginationObject<Track>? = null,
    val episodes: PaginationObject<Episode>? = null,
    val albums: PaginationObject<Album>? = null,
    val artists: PaginationObject<Artist>? = null,
    val shows: PaginationObject<Show>? = null,
    val users: PaginationObject<PublicUser>? = null,
)

package com.github.warriorzz.ktify.model.search

import com.github.warriorzz.ktify.model.*
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val tracks: TrackPagingObject? = null,
    val episodes: EpisodePagingObject? = null,
    val albums: AlbumPagingObject? = null,
    val artists: ArtistPagingObject? = null,
    val shows: ShowPagingObject? = null,
    val users: UserPagingObject? = null,
)

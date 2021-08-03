package com.github.warriorzz.ktify.model.player

import com.github.warriorzz.ktify.model.Track
import com.github.warriorzz.ktify.model.util.Context
import com.github.warriorzz.ktify.model.util.Cursor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayHistoryObject(
    val context: Context,
    @SerialName("played_at")
    val playedAt: Long,
    val track: Track,
)

@Serializable
class PlayHistoryCursorPagingObject(
    val cursors: Cursor,
    val href: String,
    val items: List<PlayHistoryObject>,
    val limit: Int,
    val next : String? = null,
    val total: Int,
)

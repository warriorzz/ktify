package ee.bjarn.ktify.model.player

import ee.bjarn.ktify.model.Track
import ee.bjarn.ktify.model.util.Context
import ee.bjarn.ktify.model.util.Cursor
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
    val next: String? = null,
    val total: Int,
)

package ee.bjarn.ktify.model.track

import ee.bjarn.ktify.model.Track
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationSeed(
    val afterFilteringSize: Int,
    val afterRelinkingSize: Int,
    val href: String,
    val id: String,
    val initialPoolSize: Int,
    val type: String,
)

@Serializable
data class Recommendations(
    val seeds: List<RecommendationSeed>,
    val tracks: List<Track>,
)

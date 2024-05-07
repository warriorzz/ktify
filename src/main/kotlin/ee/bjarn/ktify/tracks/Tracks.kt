package ee.bjarn.ktify.tracks

import ee.bjarn.ktify.Ktify
import ee.bjarn.ktify.model.Artist
import ee.bjarn.ktify.model.PaginationObject
import ee.bjarn.ktify.model.Track
import ee.bjarn.ktify.model.auth.Scope
import ee.bjarn.ktify.model.track.*
import ee.bjarn.ktify.utils.InputException
import io.ktor.client.request.*
import io.ktor.http.*

/**
 *  @param  id  Spotify ID of the track
 *  @param  market  The market to search in
 *  @return The corresponding track object
 */
suspend fun Ktify.getTrack(
    id: String,
    market: String? = null
): Track {
    return requestHelper.makeRequest(
        requiresAuthentication = true
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "tracks/$id")
        if (market != null) {
            parameter("market", market)
        }
    }
}

/**
 *  @param  ids List of Spotify IDs of the tracks
 *  @param  market  The market to search in
 */
suspend fun Ktify.getSeveralTracks(
    ids: List<String>,
    market: String? = null
): TracksResponse {
    return requestHelper.makeRequest(
        requiresAuthentication = true
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "tracks")
        parameter("ids", ids.joinToString(","))
        if (market != null) {
            parameter("market", market)
        }
    }
}

/**
 *  @param  market  The market to search in
 *  @param  limit   The maximum number of items return
 *  @param  offset  The index of the first item to return
 *  @return A pagination object of SavedTracks
 */
suspend fun Ktify.getSavedTracks(
    market: String? = null,
    limit: Int? = null,
    offset: Int? = null
): PaginationObject<SavedTrack> {
    return requestHelper.makeRequest(
        requiresAuthentication = true,
        requiresScope = Scope.USER_LIBRARY_READ
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "me/tracks")
        if (market != null) {
            parameter("market", market)
        }
        if (limit != null) {
            parameter("limit", limit)
        }
        if (offset != null) {
            parameter("offset", offset)
        }
    }
}

/**
 *  Save tracks in the user's library. Maximum of 50 IDs, following IDs will be ignored.
 *  @param  ids List of track IDs to save
 */
suspend fun Ktify.saveTracks(
    ids: List<String>
) {
    requestHelper.makeRequest<Void>(
        requiresAuthentication = true,
        requiresScope = Scope.USER_LIBRARY_MODIFY
    ) {
        method = HttpMethod.Put
        url.takeFrom(requestHelper.baseUrl + "me/tracks")
        parameter("ids", (if (ids.size > 50) ids.subList(0, 50) else ids).joinToString(","))
    }
}

/**
 *  Delete tracks in the user's library. Maximum of 50 IDs, following IDs will be ignored.
 *  @param  ids List of track IDs to remove from the user's library
 */
suspend fun Ktify.removeSavedTracks(
    ids: List<String>
) {
    requestHelper.makeRequest<Void>(
        requiresAuthentication = true,
        requiresScope = Scope.USER_LIBRARY_MODIFY
    ) {
        method = HttpMethod.Delete
        url.takeFrom(requestHelper.baseUrl + "me/tracks")
        parameter("ids", (if (ids.size > 50) ids.subList(0, 50) else ids).joinToString(","))
    }
}

/*
 *  Check if tracks are already saved in the user's library.
 *  @param ids  List of track IDs to check, Maximum: 50, following will be ingnored
 *  @return List of booleans that indicate whether the track is already saved or not
 */
suspend fun Ktify.containsSavedTracks(
    ids: List<String>
): List<Boolean> {
    return requestHelper.makeRequest(
        requiresAuthentication = true,
        requiresScope = Scope.USER_LIBRARY_READ
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "me/tracks/contains")
        parameter("ids", (if (ids.size > 50) ids.subList(0, 50) else ids).joinToString(","))
    }
}

/**
 *  Fetch audio features for several tracks.
 *  @param  ids List of track IDs, Maximum: 100, following will be ignored
 *  @return Array of Audio Features object
 */
suspend fun Ktify.getSeveralAudioFeatures(
    ids: List<String>
): List<AudioFeatures> {
    return requestHelper.makeRequest(
        requiresAuthentication = true
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "audio-features")
        parameter("ids", (if (ids.size > 100) ids.subList(0, 100) else ids).joinToString(","))
    }
}

/**
 *  Fetch audio features.
 *  @param  id  Spotify track ID
 *  @return Audio Features object
 */
suspend fun Ktify.getAudioFeatures(
    id: String
): AudioFeatures {
    return requestHelper.makeRequest(
        requiresAuthentication = true
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "audio-features/$id")
    }
}

/**
 *  Get a detailed audio analysis of a track
 *  @param  id  Spotify track ID
 *  @return Audio Analysis object
 */
suspend fun Ktify.getAudioAnalysis(
    id: String
): AudioAnalysis {
    return requestHelper.makeRequest(
        requiresAuthentication = true
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "audio-analysis/$id")
    }
}

/**
 *  Get recommendations
 *  A minimum of one seed in either artists, genres or tracks has to be provided, a maximum of 5 in combination of all are allowed
 *  For parameter documentation. refer to [Spotify Documentation](https://developer.spotify.com/documentation/web-api/reference/get-recommendations)
 *  @return Recommendations object
 */
suspend fun Ktify.getRecommendations(
    seedArtists: List<Artist> = listOf(),
    seedGenres: List<String> = listOf(),
    seedTracks: List<Track> = listOf(),
    limit: Int? = null,
    market: String? = null,
    minAcousticness: Double? = null,
    maxAcousticness: Double? = null,
    targetAcousticness: Double? = null,
    minDanceability: Double? = null,
    maxDancability: Double? = null,
    targetDancability: Double? = null,
    minDurationMs: Int? = null,
    maxDurationMs: Int? = null,
    targetDurationMs: Int? = null,
    minEnergy: Double? = null,
    maxEnergy: Double? = null,
    targetEnergy: Double? = null,
    minInstrumentalness: Double? = null,
    maxInstrumentalness: Double? = null,
    targetInstrumentalness: Double? = null,
    minKey: Int? = null,
    maxKey: Int? = null,
    targetKey: Int? = null,
    minLiveness: Double? = null,
    maxLiveness: Double? = null,
    targetLiveness: Double? = null,
    minLoudness: Double? = null,
    maxLoudness: Double? = null,
    targetLoudness: Double? = null,
    minMode: Int? = null,
    maxMode: Int? = null,
    targetMode: Int? = null,
    minPopularity: Int? = null,
    maxPopularity: Int? = null,
    targetPopularity: Int? = null,
    minSpeechiness: Double? = null,
    maxSpeechiness: Double? = null,
    targetSpeechiness: Double? = null,
    minTempo: Double? = null,
    maxTempo: Double? = null,
    targetTempo: Double? = null,
    minTimeSignature: Int? = null,
    maxTimeSignature: Int? = null,
    targetTimeSignature: Int? = null,
    minValence: Double? = null,
    maxValence: Double? = null,
    targetValence: Double? = null,
): Recommendations {
    return requestHelper.makeRequest(
        requiresAuthentication = true
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "recommendations")
        val seedSize = seedArtists.size + seedGenres.size + seedTracks.size
        if (seedSize < 1 || seedSize > 5) {
            throw InputException(listOf("seedArtists", "seedGenres", "seedTracks"))
        }
        if (seedArtists.isNotEmpty()) parameter("seed_artists", seedArtists.joinToString(",") { it.id })
        if (seedGenres.isNotEmpty()) parameter("seed_genres", seedGenres.joinToString(","))
        if (seedTracks.isNotEmpty()) parameter("seed_tracks", seedTracks.joinToString(",") { it.id })

        if (limit != null) parameter("limit", limit)
        if (market != null) parameter("market", market)
        if (minAcousticness != null) parameter("min_acousticness", minAcousticness)
        if (maxAcousticness != null) parameter("max_acousticness", maxAcousticness)
        if (targetAcousticness != null) parameter("target_acousticness", targetAcousticness)
        if (minDanceability != null) parameter("min_danceability", minDanceability)
        if (maxDancability != null) parameter("max_danceability", maxDancability)
        if (targetDancability != null) parameter("target_danceability", targetDancability)
        if (minDurationMs != null) parameter("min_duration_ms", minDurationMs)
        if (maxDurationMs != null) parameter("max_duration_ms", maxDurationMs)
        if (targetDurationMs != null) parameter("target_duration_ms", targetDurationMs)
        if (minEnergy != null) parameter("min_energy", minEnergy)
        if (maxEnergy != null) parameter("max_energy", maxEnergy)
        if (targetEnergy != null) parameter("target_energy", targetEnergy)
        if (minInstrumentalness != null) parameter("min_instrumentalness", minInstrumentalness)
        if (maxInstrumentalness != null) parameter("max_instrumentalness", maxInstrumentalness)
        if (targetInstrumentalness != null) parameter("target_instrumentalness", targetInstrumentalness)
        if (minKey != null) parameter("min_key", minKey)
        if (maxKey != null) parameter("max_key", maxKey)
        if (targetKey != null) parameter("target_key", targetKey)
        if (minLiveness != null) parameter("min_liveness", minLiveness)
        if (maxLiveness != null) parameter("max_liveness", maxLiveness)
        if (targetLiveness != null) parameter("target_liveness", targetLiveness)
        if (minLoudness != null) parameter("min_loudness", minLoudness)
        if (maxLoudness != null) parameter("max_loudness", maxLoudness)
        if (targetLoudness != null) parameter("target_loudness", targetLoudness)
        if (minMode != null) parameter("min_mode", minMode)
        if (maxMode != null) parameter("max_mode", maxMode)
        if (targetMode != null) parameter("target_mode", targetMode)
        if (minPopularity != null) parameter("min_popularity", minPopularity)
        if (maxPopularity != null) parameter("max_popularity", maxPopularity)
        if (targetPopularity != null) parameter("target_popularity", targetPopularity)
        if (minSpeechiness != null) parameter("min_speechiness", minSpeechiness)
        if (maxSpeechiness != null) parameter("max_speechiness", maxSpeechiness)
        if (targetSpeechiness != null) parameter("target_speechiness", targetSpeechiness)
        if (minTempo != null) parameter("min_tempo", minTempo)
        if (maxTempo != null) parameter("max_tempo", maxTempo)
        if (targetTempo != null) parameter("target_tempo", targetTempo)
        if (minTimeSignature != null) parameter("min_time_signature", minTimeSignature)
        if (maxTimeSignature != null) parameter("max_time_signature", maxTimeSignature)
        if (targetTimeSignature != null) parameter("target_time_signature", targetTimeSignature)
        if (minValence != null) parameter("min_valence", minValence)
        if (maxValence != null) parameter("max_valence", maxValence)
        if (targetValence != null) parameter("target_valence", targetValence)
    }
}

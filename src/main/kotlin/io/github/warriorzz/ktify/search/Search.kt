package io.github.warriorzz.ktify.search

import io.github.warriorzz.ktify.Ktify
import io.github.warriorzz.ktify.model.search.SearchResult
import io.github.warriorzz.ktify.model.util.ObjectType
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

/**
 *  Search albums, artists, episodes, playlists, shows, tracks or users
 *  @param  types   The types of the searched items
 *  @param  limit   The limit of search results per category, must be between 1 and 50, otherwise it will be 20
 *  @param  offset  The offset, maximum is 1000 (including the limit)
 *  @param  includeExternal Weather to include external hosted audio or not
 *  @param  market  (Optional) [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) code of a country. Can also be 'from_token', equivalent to the current users country.
 *  @param  queue   The queue for searching items
 *  @return The search results as a SearchResult
 */
@OptIn(ExperimentalStdlibApi::class)
suspend fun Ktify.searchItem(
    types: List<ObjectType>,
    limit: Int = 20,
    offset: Int = 0,
    includeExternal: Boolean? = null,
    market: String? = null,
    queue: SearchQueueBuilder.() -> Unit
): SearchResult {
    val searchQueue = SearchQueueBuilder().apply(queue).build()
    return requestHelper.makeRequest(
        requiresAuthentication = true
    ) {
        method = HttpMethod.Get
        url.takeFrom(requestHelper.baseUrl + "search")
        parameter("q", searchQueue.value)
        parameter(
            "type",
            types.joinToString(separator = ",") { Json.encodeToString(ObjectType.serializer(), it) }
                .replace("\"", "")
        )
        parameter("limit", if (limit in 1..50) limit.toString() else "20")
        parameter("offset", if (offset + limit <= 1000 && offset >= 0) offset.toString() else "0")
        if (includeExternal != null) {
            parameter("include_external", includeExternal.toString())
        }
        if (market != null) {
            parameter("market", market)
        }
    }
}

/**
 *  The builder for the search queue, for further reference, take a look [here](https://developer.spotify.com/documentation/web-api/reference/#category-search)
 */
class SearchQueueBuilder {

    /**
     *  The keywords the results should match
     */
    var keywords: List<Phrase> = emptyList()

    /**
     *  The keywords the result should not match
     */
    var notKeywords: List<Phrase> = emptyList()

    /**
     *  The genre in which should be searched
     */
    var genre: String? = null

    /**
     *  The name of the artist to search for
     */
    var artist: String? = null

    /**
     *  The name of the album to search for
     */
    var album: String? = null

    /**
     *  The name of the track to search for
     */
    var track: String? = null

    /**
     *  The years in which the album should be
     */
    var years: YearPhase? = null

    /**
     *  If only albums with the lowest 10% popularity should be retrieved
     */
    var hipsterAlbumsOnly: Boolean = false

    /**
     *  If only albums released in the last two weeks should be retrieved
     */
    var newAlbumsOnly: Boolean = false

    /**
     *  The method for building the search queue
     */
    internal fun build(): Phrase {
        val keywordPhrase = if (keywords.isNotEmpty()) {
            keywords.reduce { acc, phrase -> acc + phrase }
        } else EmptyPhrase()
        val notKeywordPhrase = if (notKeywords.isNotEmpty()) {
            notKeywords.reduce { acc, phrase -> acc + phrase.not() }
        } else EmptyPhrase()
        val genreString = if (genre != null) "genre:\"$genre\" " else ""
        val artistString = if (artist != null) "artist:$artist " else ""
        val albumString = if (album != null) "album:$album " else ""
        val trackString = if (track != null) "track:$track " else ""
        val yearsString = if (years != null) "year:${years!!.getYears()} " else ""
        val hipsterString = if (hipsterAlbumsOnly) "tag:hipster " else ""
        val newAlbumsString = if (newAlbumsOnly) "tag:new " else ""
        return (keywordPhrase + notKeywordPhrase).append(genreString + artistString + albumString + trackString + yearsString + hipsterString + newAlbumsString)
            .encode()
    }
}

/**
 *  The class representing a part of a search queue
 *  @param  value   The keyword
 *  @param  explicit    Weather the keyword should be an exact match
 */
open class Phrase(val value: String, private val explicit: Boolean = false) {
    /**
     *  Inverting the phrase
     */
    fun not() = Phrase("NOT $value")

    /**
     *  Appending a keyword to the phrase
     *  @param  value   The keyword to append
     */
    internal fun append(value: String) = Phrase("${this.value} $value")

    /**
     *  Encoding the phrase to make it a proper search queue
     */
    internal fun encode() = Phrase(value.replace(" ", "%20"))

    override fun toString(): String = if (explicit) "($value)" else value
}

/**
 *  The class representing an empty phrase
 */
class EmptyPhrase : Phrase("")

operator fun Phrase.plus(value: String) = Phrase("$this $value")
operator fun Phrase.plus(value: Phrase) = if (value is EmptyPhrase) this.plus(value) else Phrase("$this $value")
operator fun Phrase.plus(value: EmptyPhrase) = this

operator fun EmptyPhrase.plus(value: String) = Phrase(value)
operator fun EmptyPhrase.plus(value: Phrase) = value
operator fun EmptyPhrase.plus(value: EmptyPhrase) = EmptyPhrase()

/**
 *  The class representing the year selection for a search queue
 */
class YearPhase(private val begin: Int, private val end: Int) {
    constructor(year: Int) : this(year, year)

    internal fun getYears() = if (begin == end) begin.toString() else "$begin-$end"
}

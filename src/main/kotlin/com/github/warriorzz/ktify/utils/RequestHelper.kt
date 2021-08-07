package com.github.warriorzz.ktify.utils

import com.github.warriorzz.ktify.Ktify
import com.github.warriorzz.ktify.model.auth.ClientCredentials
import com.github.warriorzz.ktify.model.auth.Scope
import com.github.warriorzz.ktify.model.auth.refresh
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal class RequestHelper(
    private val clientCredentials: ClientCredentials,
    private val ktify: Ktify
) {

    internal val baseUrl = "https://api.spotify.com/v1/"
    internal var rateLimitExpiryTimestamp: Long? = null

    /**
     *  Will be internal once the entire API is covered
     *  @throws RateLimitException  If the client is rate limited, check with [isRateLimited]
     */
    suspend inline fun <reified T> makeRequest(
        requiresAuthentication: Boolean,
        requiresScope: Scope? = null,
        client: HttpClient = ktify.httpClient,
        builder: HttpRequestBuilder.() -> Unit
    ): T {
        if (isRateLimited()) {
            val retryAfter = rateLimitExpiryTimestamp!! - System.currentTimeMillis()
            throw RateLimitException("Too many requests - Retry after $retryAfter ms", retryAfter)
        }
        if (requiresScope != null) { require(clientCredentials.scopes?.contains(requiresScope) ?: false) }
        return client.request {
            clientCredentials.refresh()
            if (requiresAuthentication) {
                header("Authorization", "${clientCredentials.tokenType} ${clientCredentials.accessToken}")
            }
            apply(builder)
        }
    }

    /**
     *  Will be internal once the entire API is covered
     */
    suspend inline fun <reified T> makeRequest(
        requiresAuthentication: Boolean = true,
        requiresScope: Scope? = null,
        neededElement: String,
        deserializationStrategy: DeserializationStrategy<T>,
        builder: HttpRequestBuilder.() -> Unit
    ): T? {
        if (isRateLimited()) {
            ktify.logger.error {
                "Client is currently rate limited! Retry after ${rateLimitExpiryTimestamp!! - System.currentTimeMillis()}ms"
            }
            return null
        }
        if (requiresScope != null) { if (clientCredentials.scopes?.contains(requiresScope) == false) return null }
        val jsonObject: JsonObject = makeRequest(requiresAuthentication = requiresAuthentication, builder = builder)
        return if (jsonObject.containsKey(neededElement))
            Json.decodeFromJsonElement(deserializationStrategy, jsonObject)
        else null
    }

    /**
     *  Will be internal once the entire API is covered
     */
    suspend inline fun makeRequest(
        requiresScope: Scope? = null,
        builder: HttpRequestBuilder.() -> Unit
    ): HttpStatusCode {
        if (isRateLimited()) {
            return HttpStatusCode.TooManyRequests
        }
        if (requiresScope != null) { require(clientCredentials.scopes?.contains(requiresScope) ?: false) }
        val responseData: HttpResponse =
            makeRequest(requiresAuthentication = true, client = ktify.jsonLessHttpClient, builder = builder)
        return responseData.status
    }

    /**
     *  Checks if the client is currently rate limited
     *  @return true if the client is rate limited
     */
    fun isRateLimited(): Boolean = rateLimitExpiryTimestamp?.let {
        it > System.currentTimeMillis()
    } ?: false
}

package com.github.warriorzz.ktify.utils

import com.github.warriorzz.ktify.Ktify
import com.github.warriorzz.ktify.model.auth.ClientCredentials
import com.github.warriorzz.ktify.model.auth.Scope
import com.github.warriorzz.ktify.model.auth.refresh
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal class RequestHelper(
    private val clientCredentials: ClientCredentials
) {

    internal val baseUrl = "https://api.spotify.com/v1/"

    /**
     *  Will be internal once the entire API is covered
     */
    suspend inline fun <reified T> makeRequest(
        httpMethod: HttpMethod,
        url: String,
        parameters: Map<String, String>?,
        headers: Map<String, String>?,
        body: JsonObject? = null,
        requiresAuthentication: Boolean = true,
        requiresScope: Scope? = null,
        client: HttpClient = Ktify.httpClient,
    ): T {
        requiresScope?.let { require(clientCredentials.scopes?.contains(requiresScope) ?: false) }
        return client.request {
            url(url)
            parameters?.forEach {
                parameter(it.key, it.value)
            }
            headers?.forEach {
                header(it.key, it.value)
            }
            body?.let {
                this.body = body
            }
            clientCredentials.refresh()
            if (requiresAuthentication) {
                header("Authorization", "${clientCredentials.tokenType} ${clientCredentials.accessToken}")
            }
            method = httpMethod
        }
    }

    /**
     *  Will be internal once the entire API is covered
     */
    suspend inline fun <reified T> makeRequest(
        httpMethod: HttpMethod,
        url: String,
        parameters: Map<String, String>?,
        headers: Map<String, String>?,
        body: JsonObject? = null,
        requiresAuthentication: Boolean = true,
        requiresScope: Scope? = null,
        neededElement: String,
        deserializationStrategy: DeserializationStrategy<T>
    ): T? {
        requiresScope?.let { if (clientCredentials.scopes?.contains(requiresScope) == false) return null }
        val jsonObject: JsonObject = makeRequest(httpMethod, url, parameters, headers, body, requiresAuthentication)
        return if (jsonObject.containsKey(neededElement))
            Json.decodeFromJsonElement(deserializationStrategy, jsonObject)
        else null
    }

    /**
     *  Will be internal once the entire API is covered
     */
    suspend fun makeRequest(
        httpMethod: HttpMethod,
        url: String,
        parameters: Map<String, String>?,
        headers: Map<String, String>?,
        body: JsonObject? = null,
        requiresScope: Scope? = null,
    ): HttpStatusCode {
        requiresScope?.let { require(clientCredentials.scopes?.contains(requiresScope) ?: false) }
        val responseData: HttpResponse =
            makeRequest(httpMethod, url, parameters, headers, body, true, requiresScope, Ktify.jsonLessHttpClient)
        return responseData.status
    }
}

package io.github.warriorzz.ktify

import io.github.warriorzz.ktify.model.auth.ClientCredentials
import io.github.warriorzz.ktify.model.auth.ClientCredentialsResponse
import io.github.warriorzz.ktify.model.auth.Scope
import io.github.warriorzz.ktify.player.KtifyPlayer
import io.github.warriorzz.ktify.utils.*
import io.github.warriorzz.ktify.utils.RequestHelper
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.json.JsonObject
import mu.KotlinLogging

/**
 *  The main wrapper class
 *  @param  clientCredentials   The client credentials for the Spotify API
 *  @property   player  The [KtifyPlayer] instance
 */
class Ktify(
    private val clientCredentials: ClientCredentials
) {
    internal val logger = KotlinLogging.logger {}
    internal val requestHelper = RequestHelper(clientCredentials, this)
    val player = KtifyPlayer(this)

    /**
     *  Will be internal once the entire API is covered
     */
    val httpClient: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
            HttpResponseValidator {
                validateResponse { httpResponse ->
                    if (httpResponse.status.value >= 400) {
                        if (httpResponse.status == HttpStatusCode.TooManyRequests) {
                            requestHelper.rateLimitExpiryTimestamp =
                                System.currentTimeMillis() + (httpResponse.headers["Retry-After"]?.toInt() ?: 0) * 1000
                            throw RateLimitException(retryAfterMs = (httpResponse.headers["Retry-After"]?.toInt() ?: 0) * 1000L)
                        }
                        val jsonObject = httpResponse.receive<JsonObject>()
                        if (jsonObject["message"] != null) {
                            val errorObject = kotlinx.serialization.json.Json.decodeFromJsonElement(
                                ErrorObject.serializer(),
                                jsonObject
                            )
                            throw RequestException("Request failed!", errorObject)
                        }
                        if (jsonObject["error"] != null) {
                            val errorObject = kotlinx.serialization.json.Json.decodeFromJsonElement(
                                AuthenticationErrorObject.serializer(),
                                jsonObject
                            )
                            throw AuthenticationException("Authentication failed!", errorObject)
                        }
                    }
                }
                handleResponseException {
                    it.printStackTrace()
                }
            }
            serializer = KotlinxSerializer(json)
        }
    }

    /**
     *  Will be internal once the entire API is covered
     */
    val jsonLessHttpClient = HttpClient(httpClient.engine)
}

/**
 *  The builder for the [Ktify] class
 *  @param  clientId    The client ID, provided by the spotify dashboard
 *  @param  clientSecret    The client secret, provided by the spotify dashboard
 *  @param  authorizationCode   returned by the request to the user
 *  @param  redirectUri Your redirect URI (just for confirmation)
 */
class KtifyBuilder(
    private val clientId: String,
    private val clientSecret: String,
    private val authorizationCode: String,
    private val redirectUri: String,
) {

    /**
     *  @return The [Ktify] instance
     */
    @OptIn(InternalAPI::class)
    suspend fun build(): Ktify {
        val clientCredentialsResponse: ClientCredentialsResponse =
            ktifyHttpClient.post("https://accounts.spotify.com/api/token") {
                header("Content-Type", "application/x-www-form-urlencoded")
                body =
                    "grant_type=authorization_code&client_id=$clientId&client_secret=$clientSecret&redirect_uri=$redirectUri&code=$authorizationCode"
            }
        return Ktify(
            ClientCredentials(
                clientId,
                clientSecret,
                clientCredentialsResponse.accessToken,
                clientCredentialsResponse.refreshToken,
                clientCredentialsResponse.expiresIn * 1000 + System.currentTimeMillis(),
                clientCredentialsResponse.scope.split(" ")
                    .map { Scope.valueOf(it.toUpperCasePreservingASCIIRules().replace("-", "_")) },
                clientCredentialsResponse.tokenType
            )
        )
    }
}

/**
 *  HttpClient to make default requests
 */
internal val ktifyHttpClient = HttpClient(OkHttp) {
    install(JsonFeature) {
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        }
        serializer = KotlinxSerializer(json)
    }
}

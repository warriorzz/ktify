package ee.bjarn.ktify

import ee.bjarn.ktify.model.auth.ClientCredentials
import ee.bjarn.ktify.model.auth.ClientCredentialsResponse
import ee.bjarn.ktify.model.auth.Scope
import ee.bjarn.ktify.player.KtifyPlayer
import ee.bjarn.ktify.utils.AuthenticationErrorObject
import ee.bjarn.ktify.utils.AuthenticationException
import ee.bjarn.ktify.utils.ErrorObject
import ee.bjarn.ktify.utils.RateLimitException
import ee.bjarn.ktify.utils.RequestException
import ee.bjarn.ktify.utils.RequestHelper
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.json.JsonObject
import mu.KotlinLogging
import java.util.UUID

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
        install(ContentNegotiation) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
            json(json)
        }
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                val httpResponse = clientException.response

                if (httpResponse.status.value >= 400) {
                    if (httpResponse.status == HttpStatusCode.TooManyRequests) {
                        requestHelper.rateLimitExpiryTimestamp =
                            System.currentTimeMillis() + (httpResponse.headers["Retry-After"]?.toInt() ?: 0) * 1000
                        throw RateLimitException(
                            retryAfterMs = (httpResponse.headers["Retry-After"]?.toInt() ?: 0) * 1000L
                        )
                    }
                    val jsonObject = httpResponse.body<JsonObject>()
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
        }
    }

    /**
     *  Will be internal once the entire API is covered
     */
    val jsonLessHttpClient = HttpClient(httpClient.engine)
}

/**
 *  The builder for the [Ktify] class. Currently designed for single use.
 *  @param  clientId    The client ID, provided by the spotify dashboard
 *  @param  clientSecret    The client secret, provided by the spotify dashboard
 *  @param  redirectUri Your redirect URI (just for confirmation)
 */
class KtifyBuilder(
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String,
) {
    private val state = UUID.randomUUID().toString().replace("-", "")

    /**
     *  @param  scopes  List of scopes required by the application
     *  @returns  The Spotify Authorization URL
    */
    fun getAuthorisationURL(scopes: List<Scope>): String {
        val baseUrl = "https://accounts.spotify.com/authorize"
        val scopesString = if (scopes.size > 0) scopes.map { it.value + "%20" }.reduce { acc, s -> acc + s }.dropLast(3) else "none"
        return "$baseUrl?client_id=&scope=$scopesString&redirect_uri=$redirectUri&state=$state&response_type=code"
    }

    /**
     *  @param  authorizationCode returned by the request to the user
     *  @return The [Ktify] instance
     */
    @OptIn(InternalAPI::class)
    suspend fun build(authorizationCode: String): Ktify {
        val clientCredentialsResponse: ClientCredentialsResponse =
            ktifyHttpClient.post("https://accounts.spotify.com/api/token") {
                header("Content-Type", "application/x-www-form-urlencoded")
                body =
                    "grant_type=authorization_code&client_id=$clientId&client_secret=$clientSecret&redirect_uri=$redirectUri&code=$authorizationCode"
            }.body()
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
    install(ContentNegotiation) {
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        }
        json(json)
    }
}

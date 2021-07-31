package com.github.warriorzz.ktify

import com.github.warriorzz.ktify.model.auth.ClientCredentials
import com.github.warriorzz.ktify.model.auth.ClientCredentialsResponse
import com.github.warriorzz.ktify.model.auth.Scope
import com.github.warriorzz.ktify.player.KtifyPlayer
import com.github.warriorzz.ktify.utils.*
import com.github.warriorzz.ktify.utils.RequestHelper
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.serialization.json.JsonObject
import mu.KotlinLogging

class Ktify(
    private val clientCredentials: ClientCredentials
) {
    internal val logger = KotlinLogging.logger {}
    internal val requestHelper = RequestHelper(clientCredentials)
    val player = KtifyPlayer(this)

    companion object {
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
        val jsonLessHttpClient = HttpClient(httpClient.engine) {
            HttpResponseValidator {
                validateResponse { httpResponse ->
                    if (httpResponse.status.value >= 400) {
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
        }
    }
}

class KtifyBuilder(
    private val clientId: String,
    private val clientSecret: String,
    private val authorizationCode: String,
    private val redirectUri: String,
) {
    @OptIn(InternalAPI::class)
    suspend fun build(): Ktify {
        val clientCredentialsResponse: ClientCredentialsResponse =
            Ktify.httpClient.post("https://accounts.spotify.com/api/token") {
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

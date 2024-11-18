package ee.bjarn.ktify.model.auth

import ee.bjarn.ktify.ktifyHttpClient
import ee.bjarn.ktify.utils.base64encode
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.*
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientCredentials(
    val clientId: String,
    val clientSecret: String,
    var accessToken: String? = null,
    val refreshToken: String?,
    var accessTokenExpiryStamp: Long? = null,
    internal var scopes: List<Scope>? = null,
    internal var tokenType: String? = null
)

@OptIn(InternalAPI::class)
suspend fun ClientCredentials.refresh() {
    if (refreshToken == null) return
    if (accessTokenExpiryStamp == null || (accessTokenExpiryStamp ?: return) < System.currentTimeMillis()) {
        val newCredentials: ClientCredentialsResponse =
            ktifyHttpClient.post("https://accounts.spotify.com/api/token") {
                body = "grant_type=refresh_token&refresh_token=$refreshToken"
                header("Authorization", "Basic ${"$clientId:$clientSecret".base64encode()}")
                header("Content-Type", "application/x-www-form-urlencoded")
            }.body()
        accessToken = newCredentials.accessToken
        accessTokenExpiryStamp = newCredentials.expiresIn * 1000 + System.currentTimeMillis()
        scopes = newCredentials.scope.split(" ")
            .map { Scope.valueOf(it.toUpperCasePreservingASCIIRules().replace("-", "_")) }
        tokenType = newCredentials.tokenType
    }
}

@Serializable
internal data class ClientCredentialsResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("scope")
    val scope: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("refresh_token")
    val refreshToken: String? = null
)

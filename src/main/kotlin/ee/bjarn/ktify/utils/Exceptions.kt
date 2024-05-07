package ee.bjarn.ktify.utils

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.Exception
import java.lang.RuntimeException

class RequestException(override val message: String = "Unauthorized", val error: ErrorObject) :
    RuntimeException(message)

class AuthenticationException(override val message: String, val error: AuthenticationErrorObject) :
    RuntimeException(message)

class RateLimitException(override val message: String = "Too many requests", val retryAfterMs: Long) : RuntimeException(message)

class InputException(val parameters: List<String>, override val message: String = "Provided parameter input is not valid") : Exception(message)

@Serializable
data class ErrorObject(
    @Serializable(with = HttpStatusCodeSerializer::class)
    val status: HttpStatusCode,
    val message: String
)

@Serializable
data class AuthenticationErrorObject(
    val error: String,
    @SerialName("error_description")
    val errorDescription: String
)

object HttpStatusCodeSerializer : KSerializer<HttpStatusCode> {
    override fun deserialize(decoder: Decoder): HttpStatusCode {
        return HttpStatusCode.fromValue(decoder.decodeInt())
    }

    override val descriptor = PrimitiveSerialDescriptor("httpstatuscode", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: HttpStatusCode) {
        encoder.encodeInt(value.value)
    }
}

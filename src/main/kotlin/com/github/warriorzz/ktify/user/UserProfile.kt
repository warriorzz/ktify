package com.github.warriorzz.ktify.user

import com.github.warriorzz.ktify.Ktify
import com.github.warriorzz.ktify.model.user.CurrentProfile
import io.ktor.http.*

/**
 *  @return The [CurrentProfile](https://github.com/warriorzz/ktify/blob/ac318a38f72f770893f2c4c9cf64dc18a2e05a86/src/main/kotlin/com/github/warriorzz/ktify/model/user/User.kt#L11) object corresponding to the current user
 */
suspend fun Ktify.getCurrentProfile() = requestHelper.makeRequest<CurrentProfile>(
    HttpMethod.Get,
    requestHelper.baseUrl + "me",
    null, null,
    null
)
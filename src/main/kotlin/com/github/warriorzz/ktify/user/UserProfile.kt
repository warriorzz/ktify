package com.github.warriorzz.ktify.user

import com.github.warriorzz.ktify.Ktify
import com.github.warriorzz.ktify.model.user.CurrentProfile
import io.ktor.http.*

suspend fun Ktify.getCurrentProfile() = requestHelper.makeRequest<CurrentProfile>(
    HttpMethod.Get,
    requestHelper.baseUrl + "me",
    null, null,
    null
)
package com.github.warriorzz.ktify.model.user

import kotlinx.serialization.Serializable

@Serializable
data class Followers(
    val href: String? = null,
    val total: Int // TODO?
)

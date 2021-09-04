package io.github.warriorzz.ktify.model.util

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val height: Int? = null,
    val url: String,
    val width: Int? = null
)

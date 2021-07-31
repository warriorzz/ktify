package com.github.warriorzz.ktify.model.external

import kotlinx.serialization.Serializable

@Serializable
data class ExternalId(
    val ean: String? = null,
    val isrc: String,
    val upc: String? = null
)

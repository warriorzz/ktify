package com.github.warriorzz.ktify.model.utils

import kotlinx.serialization.Serializable

@Serializable
data class Cursor(
    val after: String
)

@Serializable
data class CursorPagingObject(
    val cursors: Cursor,
    val href: String,
    val limit: Int,
    val next: String? = null,
    val total: Int,
)

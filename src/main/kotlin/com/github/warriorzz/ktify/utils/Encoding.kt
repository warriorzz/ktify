package com.github.warriorzz.ktify.extensions

import java.util.*

fun String.base64encode(): String =
    Base64.getEncoder().encodeToString(this.toByteArray())

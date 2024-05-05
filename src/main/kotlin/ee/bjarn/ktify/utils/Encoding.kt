package ee.bjarn.ktify.utils

import java.util.*

fun String.base64encode(): String =
    Base64.getEncoder().encodeToString(this.toByteArray())

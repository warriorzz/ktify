package com.github.warriorzz.ktify.model.player

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailableDevices(
    val devices: List<Device>
)

@Serializable
data class Device(
    val id: String,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("is_restricted")
    val isRestricted: Boolean,
    @SerialName("is_private_session")
    val isPrivateSession: Boolean,
    val name: String,
    val type: String,
    @SerialName("volume_percent")
    val volumePercent: Int
)

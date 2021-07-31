package com.github.warriorzz.ktify.model.player

import com.github.warriorzz.ktify.model.util.Device
import kotlinx.serialization.Serializable

@Serializable
data class AvailableDevices(
    val devices: List<Device>
)
package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class TimePreset(val id: Int, val time: Int, val userId: String)
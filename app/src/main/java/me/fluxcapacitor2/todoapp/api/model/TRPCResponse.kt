package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class TRPCResponse<T>(val result: TRPCResponseResult<T>)

@Serializable
data class TRPCResponseResult<T>(val data: TRPCResponseData<T>)

@Serializable
data class TRPCResponseData<T>(val json: T)
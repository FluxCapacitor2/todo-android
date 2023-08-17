package me.fluxcapacitor2.todoapp.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.fluxcapacitor2.todoapp.api.ApiUtils.client
import me.fluxcapacitor2.todoapp.api.model.ApiToken
import me.fluxcapacitor2.todoapp.api.model.NotificationToken
import me.fluxcapacitor2.todoapp.api.model.TRPCResponse
import me.fluxcapacitor2.todoapp.api.model.TimePreset

object Users {
    // https://firebase.google.com/docs/cloud-messaging/android/client#sample-register
    suspend fun addNotifToken(token: String) {
        client.post("user.addNotifToken") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(token)
                    )
                )
            )
        }
    }

    suspend fun removeNotifToken(token: String) {
        client.post("user.removeNotifToken") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(token)
                    )
                )
            )
        }
    }

    suspend fun getNotifTokens(): Array<NotificationToken> {
        return client.get("user.getNotifTokens")
            .body<TRPCResponse<Array<NotificationToken>>>().result.data.json
    }

    suspend fun getApiToken(): Array<ApiToken> {
        return client.get("user.getApiToken")
            .body<TRPCResponse<Array<ApiToken>>>().result.data.json
    }

    suspend fun invalidateApiToken(tokenId: String) {
        client.post("user.invalidateApiToken") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(tokenId)
                    )
                )
            )
        }
    }

    suspend fun getTimePresets(): Array<TimePreset> {
        return client.get("user.getTimePresets")
            .body<TRPCResponse<Array<TimePreset>>>().result.data.json
    }

    suspend fun addTimePreset(time: Int): Array<TimePreset> {
        return client.post("user.addTimePreset") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(time)
                    )
                )
            )
        }.body<TRPCResponse<Array<TimePreset>>>().result.data.json
    }

    suspend fun removeTimePreset(presetId: Int): Array<TimePreset> {
        return client.post("user.removeTimePreset") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(presetId)
                    )
                )
            )
        }.body<TRPCResponse<Array<TimePreset>>>().result.data.json
    }
}
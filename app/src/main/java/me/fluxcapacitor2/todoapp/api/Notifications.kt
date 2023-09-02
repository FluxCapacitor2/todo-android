package me.fluxcapacitor2.todoapp.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.fluxcapacitor2.todoapp.api.ApiUtils.client
import me.fluxcapacitor2.todoapp.api.model.Reminder
import me.fluxcapacitor2.todoapp.api.model.TRPCResponse

object Notifications {
    suspend fun list(taskId: Int): Array<Reminder> {
        return client.get("notification.list?input={\"json\":${taskId}}")
            .body<TRPCResponse<Array<Reminder>>>().result.data.json
    }

    suspend fun listAll(): Array<Reminder> {
        return client.get("notification.listAll")
            .body<TRPCResponse<Array<Reminder>>>().result.data.json
    }

    suspend fun add(
        projectId: String,
        taskId: Int,
        time: String
    ): TRPCResponse<Nothing> {
        return client.post("notifications.add") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonObject(
                            mapOf(
                                "projectId" to JsonPrimitive(projectId),
                                "taskId" to JsonPrimitive(taskId),
                                "time" to JsonPrimitive(time)
                            )
                        )
                    )
                )
            )
        }.body()
    }

    suspend fun remove(notificationId: Int): TRPCResponse<Nothing> {
        return client.post("notifications.remove") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(notificationId)
                    )
                )
            )
        }.body()
    }
}
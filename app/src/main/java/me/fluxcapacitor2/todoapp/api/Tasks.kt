package me.fluxcapacitor2.todoapp.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.fluxcapacitor2.todoapp.api.ApiUtils.client
import me.fluxcapacitor2.todoapp.api.model.FullTask
import me.fluxcapacitor2.todoapp.api.model.TRPCResponse
import me.fluxcapacitor2.todoapp.api.model.Task

object Tasks {
    suspend fun create(
        name: String,
        description: String,
        dueDate: String?,
        sectionId: Int
    ): TRPCResponse<Nothing> {
        return client.post("tasks.create") {
            header("Content-Type", "application/json")
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonObject(
                            mapOf(
                                "name" to JsonPrimitive(name),
                                "description" to JsonPrimitive(description),
                                "dueDate" to JsonPrimitive(dueDate),
                                "sectionId" to JsonPrimitive(sectionId),
                            )
                        ),
                        "meta" to JsonObject(
                            mapOf(
                                "values" to JsonObject(
                                    mapOf(
                                        "dueDates" to JsonArray(listOf(JsonPrimitive("Date")))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }.body()
    }

    suspend fun delete(taskId: Int) {
        client.post("tasks.delete") {
            header("Content-Type", "application/json")
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(taskId)
                    )
                )
            )
        }
    }

    suspend fun update(
        taskId: Int,
        name: String?,
        description: String?,
        priority: Int?,
        createdAt: String?,
        completed: Boolean?,
        startDate: String?,
        dueDate: String?
    ) {

        val map = mutableMapOf(
            "id" to JsonPrimitive(taskId)
        )

        val meta = mutableMapOf<String, JsonElement>()

        if (name != null) {
            map["name"] = JsonPrimitive(name)
        }
        if (description != null) {
            map["description"] = JsonPrimitive(description)
        }
        if (priority != null) {
            map["priority"] = JsonPrimitive(priority)
        }
        if (createdAt != null) {
            map["createdAt"] = JsonPrimitive(createdAt)
            meta["createdAt"] = JsonArray(listOf(JsonPrimitive("Date")))
        }
        if (completed != null) {
            map["completed"] = JsonPrimitive(completed)
        }
        if (startDate != null) {
            map["startDate"] = JsonPrimitive(startDate)
            meta["startDate"] = JsonArray(listOf(JsonPrimitive("Date")))
        }
        if (dueDate != null) {
            map["dueDate"] = JsonPrimitive(dueDate)
            meta["dueDate"] = JsonArray(listOf(JsonPrimitive("Date")))
        }

        client.post("tasks.update") {
            header("Content-Type", "application/json")
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonObject(map),
                        "meta" to JsonObject(mapOf("values" to JsonObject(meta)))
                    )
                )
            )
        }
    }

    suspend fun addSubtask(
        name: String,
        description: String,
        dueDate: String?,
        parentTaskId: Int
    ): TRPCResponse<Nothing> {
        return client.post("tasks.addSubtask") {
            header("Content-Type", "application/json")
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonObject(
                            mapOf(
                                "name" to JsonPrimitive(name),
                                "description" to JsonPrimitive(description),
                                "dueDate" to JsonPrimitive(dueDate),
                                "id" to JsonPrimitive(parentTaskId),
                            )
                        ),
                        "meta" to JsonObject(
                            mapOf(
                                "values" to JsonObject(
                                    mapOf(
                                        "dueDate" to JsonArray(listOf(JsonPrimitive("Date")))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }.body()
    }

    suspend fun get(taskId: Int): FullTask {
        return client.get("tasks.get?input={\"json\":{\"id\":${taskId}}}")
            .body<TRPCResponse<FullTask>>().result.data.json
    }

    suspend fun listCompleted(page: Int): Array<Task> {
        return client.get("tasks.listCompleted?input={\"page\":${page}}")
            .body<TRPCResponse<Array<Task>>>().result.data.json
    }

    suspend fun listTopLevel(): Array<Task> {
        return client.get("tasks.listTopLevel")
            .body<TRPCResponse<Array<Task>>>().result.data.json
    }
}
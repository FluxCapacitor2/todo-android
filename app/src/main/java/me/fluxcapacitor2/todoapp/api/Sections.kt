package me.fluxcapacitor2.todoapp.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.fluxcapacitor2.todoapp.api.ApiUtils.client
import me.fluxcapacitor2.todoapp.api.model.Section
import me.fluxcapacitor2.todoapp.api.model.TRPCResponse

object Sections {
    suspend fun create(name: String) {
        client.post("sections.create") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonObject(
                            mapOf(
                                "name" to JsonPrimitive(name)
                            )
                        )
                    )
                )
            )
        }
    }

    suspend fun delete(taskId: Int) {
        client.post("sections.delete") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonObject(
                            mapOf(
                                "id" to JsonPrimitive(taskId)
                            )
                        )
                    )
                )
            )
        }
    }

    suspend fun update(sectionId: Int, name: String?, archived: Boolean?) {

        val map = mutableMapOf(
            "id" to JsonPrimitive(sectionId)
        )

        if (name != null) {
            map["name"] = JsonPrimitive(name)
        }

        if (archived != null) {
            map["archived"] = JsonPrimitive(archived)
        }

        client.post("sections.update") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonObject(map)
                    )
                )
            )
        }
    }

    suspend fun getArchived(projectId: String): Array<Section> {
        return client.get("sections.getArchived?input={\"json\":\"${projectId}\"}")
            .body<TRPCResponse<Array<Section>>>().result.data.json
    }
}
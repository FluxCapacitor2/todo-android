package me.fluxcapacitor2.todoapp.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.fluxcapacitor2.todoapp.api.ApiUtils.client
import me.fluxcapacitor2.todoapp.api.model.Collaborator
import me.fluxcapacitor2.todoapp.api.model.Invitation
import me.fluxcapacitor2.todoapp.api.model.ProjectDetail
import me.fluxcapacitor2.todoapp.api.model.ProjectMeta
import me.fluxcapacitor2.todoapp.api.model.TRPCResponse

object Projects {

    object Collaborators {
        suspend fun list(projectId: String): Array<Collaborator> {
            return client.get("projects.collaborators.list?input={\"json\":\"${projectId}\"}")
                .body<TRPCResponse<Array<Collaborator>>>().result.data.json
        }

        suspend fun listInvitations(projectId: String): Array<Invitation> {
            return client.get("projects.collaborators.listInvitations?input={\"json\":\"${projectId}\"}")
                .body<TRPCResponse<Array<Invitation>>>().result.data.json
        }

        suspend fun invite(projectId: String, email: String) {
            client.post("projects.collaborators.invite") {
                setBody(
                    JsonObject(
                        mapOf(
                            "json" to JsonObject(
                                mapOf(
                                    "email" to JsonPrimitive(email),
                                    "projectId" to JsonPrimitive(projectId)
                                )
                            )
                        )
                    )
                )
            }
        }

        suspend fun remove(collaboratorId: String) {
            client.post("projects.collaborators.remove") {
                setBody(
                    JsonObject(
                        mapOf(
                            "json" to JsonPrimitive(collaboratorId)
                        )
                    )
                )
            }
        }
    }

    suspend fun list(): Array<ProjectMeta> {
        return client.get("projects.list")
            .body<TRPCResponse<Array<ProjectMeta>>>().result.data.json
    }

    suspend fun get(projectId: String): ProjectDetail {
        return client.get("projects.get?input={\"json\":\"${projectId}\"}")
            .body<TRPCResponse<ProjectDetail>>().result.data.json
    }

    suspend fun create(name: String) {
        client.post("projects.create") {
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

    suspend fun delete(projectId: String) {
        client.post("projects.delete") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(projectId)
                    )
                )
            )
        }
    }
}
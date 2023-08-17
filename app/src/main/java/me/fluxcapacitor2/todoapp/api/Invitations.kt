package me.fluxcapacitor2.todoapp.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.fluxcapacitor2.todoapp.api.ApiUtils.client
import me.fluxcapacitor2.todoapp.api.model.Invitation
import me.fluxcapacitor2.todoapp.api.model.TRPCResponse

object Invitations {
    suspend fun listIncoming(): Array<Invitation> {
        return client.get("invitation.listIncoming")
            .body<TRPCResponse<Array<Invitation>>>().result.data.json
    }

    suspend fun listOutgoing(): Array<Invitation> {
        return client.get("invitation.listOutgoing")
            .body<TRPCResponse<Array<Invitation>>>().result.data.json
    }

    suspend fun accept(invitationId: String) {
        client.post("invitation.accept") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(invitationId)
                    )
                )
            )
        }
    }

    suspend fun rescind(invitationId: String) {
        client.post("invitation.rescind") {
            setBody(
                JsonObject(
                    mapOf(
                        "json" to JsonPrimitive(invitationId)
                    )
                )
            )
        }
    }
}
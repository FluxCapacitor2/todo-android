@file:OptIn(ExperimentalSerializationApi::class)

package me.fluxcapacitor2.todoapp.api.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Entity
@Serializable(with = ProjectMetaSerializer::class)
data class ProjectMeta(
    @PrimaryKey val id: String,
    val name: String,
    val ownerId: String,
    val sections: Int,
    val tasks: Int
)

@Serializer(forClass = ProjectMeta::class)
object ProjectMetaSerializer : KSerializer<ProjectMeta> {
    override val descriptor: SerialDescriptor
        get() = JsonObject.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ProjectMeta) {
        error("Not implemented")
    }

    override fun deserialize(decoder: Decoder): ProjectMeta {
        val jsonObject = JsonObject.serializer().deserialize(decoder)
        val sections = jsonObject["sections"]!!.jsonArray.map {
            it.jsonObject["_count"]!!.jsonObject["tasks"]!!.jsonPrimitive.int
        }
        return ProjectMeta(
            id = jsonObject["id"]!!.jsonPrimitive.content,
            name = jsonObject["name"]!!.jsonPrimitive.content,
            ownerId = jsonObject["ownerId"]!!.jsonPrimitive.content,
            sections = sections.size,
            tasks = sections.sum()
        )
    }
}
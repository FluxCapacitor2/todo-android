package me.fluxcapacitor2.todoapp.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import me.fluxcapacitor2.todoapp.api.model.ProjectDetail
import me.fluxcapacitor2.todoapp.api.model.ProjectMeta
import me.fluxcapacitor2.todoapp.api.model.TRPCResponse
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.ResponseOrigin
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreResponse

object ApiUtils {

    val client = HttpClient(CIO) {
        install(DefaultRequest) {
            url("https://todo-app-seven-lime.vercel.app/api/trpc/")
            header("authorization", "Bearer <api-token>")
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
            filter { true }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    val projectListStore = StoreBuilder.from(
        fetcher = Fetcher.of {
            client.get("projects.list").body<TRPCResponse<List<ProjectMeta>>>().result.data.json
        },
        sourceOfTruth = SourceOfTruth.of(
            nonFlowReader = { _: Unit -> db.projectMetaDao().getAll() },
            writer = { _: Unit, items: List<ProjectMeta> ->
                db.projectMetaDao().insertAll(*items.toTypedArray())
            },
            delete = { _: Unit -> db.projectMetaDao().deleteAll() },
            deleteAll = db.projectMetaDao()::deleteAll,
        )
    )
        .build()

    val projectStore = StoreBuilder.from(
        fetcher = Fetcher.of { projectId: String ->
            client.get("projects.get?input={\"json\":\"${projectId}\"}")
                .body<TRPCResponse<ProjectDetail>>().result.data.json
        },
        sourceOfTruth = SourceOfTruth.of(
            nonFlowReader = db.projectDetailDao()::get,
            writer = { key: String, input: ProjectDetail ->
                db.projectDetailDao().insertAll(input)
            },
            delete = db.projectDetailDao()::delete,
            deleteAll = db.projectDetailDao()::deleteAll
        )
    )
        .build()

    @Composable
    fun <T : Any> Flow<StoreResponse<T>>.toMutableState(): StoreResponse<T> {

        var state by remember {
            mutableStateOf<StoreResponse<T>>(StoreResponse.Loading(ResponseOrigin.Fetcher))
        }

        var hasData by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            this@toMutableState.collect { response ->
                if (response is StoreResponse.Error.Exception) {
                    response.error.printStackTrace()
                }
                if (response is StoreResponse.Data) {
                    hasData = true
                }
                if (response is StoreResponse.Loading && hasData) {
                    // Prevent going into a loading state if we have cached data
                    return@collect
                }

                // Set the new state, causing the UI to update
                state = response
            }
        }

        return state
    }
}

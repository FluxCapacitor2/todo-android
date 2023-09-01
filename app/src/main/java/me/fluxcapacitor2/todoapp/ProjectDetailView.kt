package me.fluxcapacitor2.todoapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import me.fluxcapacitor2.todoapp.api.ApiUtils
import me.fluxcapacitor2.todoapp.api.ApiUtils.toMutableState
import org.mobilenativefoundation.store.store5.StoreRequest
import org.mobilenativefoundation.store.store5.StoreResponse

/**
 * A Composable containing one or multiple [SectionView]s, allowing full
 * access to all sections, tasks, and settings in a project.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectDetailView(projectId: String?) {
    if (projectId == null) return

    when (val details =
        ApiUtils.projectStore.stream(StoreRequest.cached(projectId, refresh = true))
            .toMutableState()) {
        is StoreResponse.Loading -> {
            CircularProgressIndicator()
        }

        is StoreResponse.Data -> {
            val projectDetail = details.value
            val state = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f
            ) {
                projectDetail.sections.size
            }
            HorizontalPager(state = state) {
                SectionView(projectDetail.sections[it])
            }
        }

        is StoreResponse.Error -> {
            Text("Error loading project")
        }

        is StoreResponse.NoNewData -> {} // Unexpected
    }
}
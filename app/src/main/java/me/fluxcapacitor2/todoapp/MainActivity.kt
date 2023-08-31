package me.fluxcapacitor2.todoapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import me.fluxcapacitor2.todoapp.api.ApiUtils
import me.fluxcapacitor2.todoapp.api.ApiUtils.toMutableState
import me.fluxcapacitor2.todoapp.api.initializeDatabase
import me.fluxcapacitor2.todoapp.api.model.ProjectMeta
import me.fluxcapacitor2.todoapp.api.model.Section
import me.fluxcapacitor2.todoapp.api.model.Task
import me.fluxcapacitor2.todoapp.ui.theme.TodoAppTheme
import me.fluxcapacitor2.todoapp.utils.formatDueDate
import me.fluxcapacitor2.todoapp.utils.formatStartDate
import org.mobilenativefoundation.store.store5.StoreRequest
import org.mobilenativefoundation.store.store5.StoreResponse

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeDatabase(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContent {
            TodoAppTheme {
                var pageTitle by remember {
                    mutableStateOf("Projects")
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
                        ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                            NavigationDrawerItem(
                                label = { Text("Projects") },
                                selected = false,
                                onClick = {
                                    navController.navigate("projects"); scope.launch { drawerState.close() }
                                })
                            NavigationDrawerItem(
                                label = { Text("Tasks") },
                                selected = false,
                                onClick = { navController.navigate("tasks"); scope.launch { drawerState.close() } })
                            NavigationDrawerItem(
                                label = { Text("Settings") },
                                selected = false,
                                onClick = { navController.navigate("settings"); scope.launch { drawerState.close() } })
                        }
                    }) {
                        Scaffold(
                            contentWindowInsets = WindowInsets(0),
                            topBar = {
                                TopAppBar(title = { Text(text = pageTitle) }, navigationIcon = {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu"
                                        )
                                    }
                                })
                            }) { padding ->
                            NavHost(
                                navController = navController,
                                startDestination = "projects",
                                modifier = Modifier.padding(padding),
                                enterTransition = { EnterTransition.None },
                                exitTransition = { ExitTransition.None }
                            ) {
                                composable("projects") {
                                    pageTitle = "Projects"
                                    ProjectsView(navController)
                                }
                                composable("tasks") {
                                    pageTitle = "Tasks"
                                    TasksView()
                                }
                                composable("settings") {
                                    pageTitle = "Settings"
                                    SettingsView()
                                }
                                composable(
                                    "project/{projectId}",
                                    arguments = listOf(navArgument("projectId") {
                                        type = NavType.StringType
                                    })
                                ) { backStackEntry ->
                                    ProjectDetailView(backStackEntry.arguments?.getString("projectId"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ProjectsView(navController: NavController) {
        when (val data = ApiUtils.projectListStore.stream(StoreRequest.cached(Unit, refresh = true))
            .toMutableState()) {
            is StoreResponse.Loading -> {
                CircularProgressIndicator()
            }

            is StoreResponse.Data -> {
                val projects = data.value
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2)
                ) {
                    items(projects.size) {
                        val project = projects[it]
                        ProjectTile(
                            project,
                            navController,
                        )
                    }
                }
            }

            is StoreResponse.Error -> {
                Text("Error loading projects!")
            }

            is StoreResponse.NoNewData -> {} // Unexpected
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProjectTile(projectMeta: ProjectMeta, navController: NavController) {
        Card(modifier = Modifier.padding(5.dp), onClick = {
            navController.navigate("project/${projectMeta.id}")
        }) {
            Column(modifier = Modifier.padding(5.dp)) {
                Text(
                    text = projectMeta.name,
                    fontSize = TextUnit(20F, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(text = "${projectMeta.sections} sections")
                Text(text = "${projectMeta.tasks} tasks")
            }
        }
    }

    @Composable
    fun TasksView() {
    }

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

    @Composable
    fun SectionView(section: Section) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .fillMaxHeight()
        ) {
            items(section.tasks.size + 2) {
                // First thing in the list is the section title
                if (it == 0) Text(
                    text = section.name,
                    fontSize = TextUnit(20F, TextUnitType.Sp),
                    modifier = Modifier.padding(5.dp)
                )
                // Last thing in the list is the add button
                else if (it == section.tasks.size + 1) Button(
                    onClick = { }, modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                ) { Text(text = "Add Item", maxLines = 3) }
                else TaskView(section.tasks[it - 1])
            }
            item {
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskView(task: Task) {

        var modalBottomSheet by remember {
            mutableStateOf(false)
        }

        ElevatedCard(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(4.dp),
            onClick = { modalBottomSheet = true }
        ) {
            Row(modifier = Modifier.padding(5.dp)) {
                var checked by remember {
                    mutableStateOf(false)
                }
                Checkbox(checked = checked, onCheckedChange = { checked = !checked })
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(task.name, fontSize = TextUnit(19F, TextUnitType.Sp))
                    if (task.description.isNotEmpty()) {
                        OutlinedCard(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                task.description.replace("\n\n", "\n"),
                                maxLines = 4,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                    if (!task.dueDate.isNullOrEmpty()) {
                        OutlinedCard(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                        ) {
                            Row {
                                Icon(
                                    imageVector = Icons.TwoTone.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.padding(5.dp)
                                )
                                Text(task.formatDueDate(), modifier = Modifier.padding(5.dp))
                            }
                        }
                    }
                }
            }
        }

        if (modalBottomSheet) {
            ModalBottomSheet(onDismissRequest = { modalBottomSheet = false }) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 80.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = task.completed, onCheckedChange = {})
                        Text(text = task.name, fontSize = TextUnit(24f, TextUnitType.Sp))
                    }

                    Column {
                        Text(
                            text = "Description",
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )
                        if (task.description.isEmpty()) {
                            Text("Add description", color = Color.Gray)
                        } else {
                            Text(text = task.description)
                        }
                    }

                    Column {
                        Text(
                            text = "Dates",
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )

                        var dueDatePickerOpen by remember {
                            mutableStateOf(false)
                        }
                        val dueDatePickerState = rememberDatePickerState()

                        if (dueDatePickerOpen) {
                            DatePickerDialog(
                                onDismissRequest = { dueDatePickerOpen = false },
                                confirmButton = {
                                    TextButton(onClick = { dueDatePickerOpen = false }) {
                                        Text(text = "Confirm")
                                    }
                                }) {
                                DatePicker(state = dueDatePickerState)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { dueDatePickerOpen = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(text = if (task.startDate != null) "Started ${task.formatStartDate()}" else "Add Start Date")
                            }
                            OutlinedButton(
                                onClick = { dueDatePickerOpen = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(text = if (task.dueDate != null) "Due ${task.formatDueDate()}" else "Add Due Date")
                            }
                        }
                    }

                    Column {
                        Text(
                            text = "Reminders",
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )
                        Text(text = "Schedule a notification linking to this task.")
                    }

                    Row {
                        Text(
                            text = "Sub-tasks ",
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20f, TextUnitType.Sp)
                        )
                        Text(text = "0/0 completed", color = Color.Gray)
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsView() {
    }
}
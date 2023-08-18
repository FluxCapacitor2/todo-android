package me.fluxcapacitor2.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import me.fluxcapacitor2.todoapp.api.Projects
import me.fluxcapacitor2.todoapp.api.model.ProjectDetail
import me.fluxcapacitor2.todoapp.api.model.ProjectMeta
import me.fluxcapacitor2.todoapp.api.model.Section
import me.fluxcapacitor2.todoapp.api.model.Task
import me.fluxcapacitor2.todoapp.ui.theme.TodoAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        Scaffold(topBar = {
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
                                modifier = Modifier.padding(padding)
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
        var projects by remember {
            mutableStateOf(emptyArray<ProjectMeta>())
        }
        var loading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = "fetch_projects") {
            try {
                projects = Projects.list()
                loading = false
            } catch (e: Exception) {
                error = true
            }
        }
        if (loading) {
            CircularProgressIndicator()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), modifier = Modifier.scrollable(
                    rememberScrollState(), Orientation.Vertical
                )
            ) {
                items(projects.size) {
                    val project = projects[it]
                    ProjectTile(
                        project,
                        navController
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProjectTile(projectMeta: ProjectMeta, navController: NavController) {
        var projectDetail by remember {
            mutableStateOf(null as ProjectDetail?)
        }
        LaunchedEffect(key1 = projectMeta.id) {
            projectDetail = Projects.get(projectMeta.id)
        }
        Card(modifier = Modifier.padding(5.dp), onClick = {
            navController.navigate("project/${projectMeta.id}")
        }) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = if (projectDetail != null) Arrangement.Top else Arrangement.spacedBy(
                    5.dp,
                    Alignment.Top
                )
            ) {
                Text(
                    text = projectMeta.name,
                    fontSize = TextUnit(20F, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
                if (projectDetail != null) {
                    Text(text = "${projectDetail!!.sections.size} sections")
                    Text(text = "${projectDetail!!.sections.sumOf { it.tasks.size }} tasks")
                } else {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .shimmer()
                            .background(Color.White, RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .shimmer()
                            .background(Color.White, RoundedCornerShape(4.dp))
                    )
                }
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
        var projectDetail by remember {
            mutableStateOf(null as ProjectDetail?)
        }
        LaunchedEffect(key1 = projectId) {
            projectDetail = Projects.get(projectId)
        }
        if (projectDetail != null) {
            val state = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f
            ) {
                projectDetail!!.sections.size
            }
            HorizontalPager(state = state) {
                SectionView(projectDetail!!.sections[it])
            }
        }
    }

    @Composable
    fun SectionView(section: Section) {
        LazyColumn(modifier = Modifier.padding(10.dp).fillMaxHeight()) {
            items(section.tasks.size + 2) {
                // First thing in the list is the section title
                if (it == 0) Text(text = section.name, fontSize = TextUnit(20F, TextUnitType.Sp), modifier = Modifier.padding(5.dp))
                // Last thing in the list is the add button
                else if (it == section.tasks.size + 1) Button(onClick = { }, modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()) { Text(text = "Add Item", maxLines = 3) }
                else TaskView(section.tasks[it - 1])
            }
        }
    }

    @Composable
    fun TaskView(task: Task) {
        ElevatedCard(modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(5.dp)) {
                var checked by remember {
                    mutableStateOf(false)
                }
                Checkbox(checked = checked, onCheckedChange = { checked = !checked } )
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(task.name, fontSize = TextUnit(19F, TextUnitType.Sp))
                    if (task.description.isNotEmpty()) {
                        OutlinedCard(modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()) {
                            Text(task.description.replace("\n\n", "\n"), maxLines = 4, modifier = Modifier.padding(5.dp))
                        }
                    }
                    if (!task.dueDate.isNullOrEmpty()) {
                        OutlinedCard(modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()) {
                            Row {
                                Icon(imageVector = Icons.TwoTone.DateRange, contentDescription = null, modifier = Modifier.padding(5.dp))
                                Text(task.dueDate, modifier = Modifier.padding(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsView() {
    }
}
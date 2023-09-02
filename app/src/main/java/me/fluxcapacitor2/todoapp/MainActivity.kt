package me.fluxcapacitor2.todoapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.fluxcapacitor2.todoapp.alarm.CustomAlarmManager
import me.fluxcapacitor2.todoapp.alarm.NotificationSetup
import me.fluxcapacitor2.todoapp.api.ApiUtils
import me.fluxcapacitor2.todoapp.api.ApiUtils.toMutableState
import me.fluxcapacitor2.todoapp.api.Notifications
import me.fluxcapacitor2.todoapp.api.initializeDatabase
import me.fluxcapacitor2.todoapp.api.model.ProjectMeta
import me.fluxcapacitor2.todoapp.ui.theme.TodoAppTheme
import me.fluxcapacitor2.todoapp.utils.shortDateFormat
import org.mobilenativefoundation.store.store5.StoreRequest
import org.mobilenativefoundation.store.store5.StoreResponse
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeDatabase(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val customAlarmManager = CustomAlarmManager(applicationContext)

        CoroutineScope(Dispatchers.IO).launch {
            Notifications.listAll().forEach {
                val date = ZonedDateTime.parse(it.time, DateTimeFormatter.ISO_DATE_TIME).withZoneSameLocal(ZoneOffset.UTC)
                val millis = date.toInstant().toEpochMilli()
                customAlarmManager.schedule(it.taskId.toString(), it.projectId, it.task.name, "Due ${shortDateFormat(it.task.dueDate)}", millis)
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        // Android 13+ (SDK 33) requires permission to post notifications
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

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
                // Create a notification channel for each project - todo is this the right place to do this?
                LaunchedEffect(projects) {
                    // "Recreating an existing notification channel with its original values performs no operation, so it's safe to call this code when starting an app."
                    projects.forEach {
                        NotificationSetup.registerChannel(
                            applicationContext,
                            "reminder_project_${it.id}",
                            "Reminders - ${it.name}",
                            NotificationManagerCompat.IMPORTANCE_HIGH
                        )
                    }
                }
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

    @Composable
    fun SettingsView() {
    }
}
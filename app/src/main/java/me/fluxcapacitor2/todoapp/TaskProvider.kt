package me.fluxcapacitor2.todoapp

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.fluxcapacitor2.todoapp.api.Tasks
import me.fluxcapacitor2.todoapp.api.model.Task

@Composable
fun TaskProvider(initial: Task, render: @Composable (task: MutableState<Task>) -> Unit) {
    var task = remember {
        mutableStateOf(initial)
    }
    var mounted by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val updateTask =
        debounce(delayMillis = 500, scope = rememberCoroutineScope()) { task: Task ->
            try {
                Tasks.update(
                    task.id,
                    task.name,
                    task.description,
                    task.priority,
                    task.createdAt,
                    task.completed,
                    task.startDate,
                    task.dueDate
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast(context).apply {
                    setText(e.message)
                    show()
                }
            }
        }
    LaunchedEffect(task.value) {
        if (!mounted) mounted = true
        else {
            updateTask(task.value)
        }
    }
    render(task)
}


private fun <T> debounce(
    delayMillis: Long,
    scope: CoroutineScope,
    callback: suspend (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(delayMillis)
            callback(param)
        }
    }
}
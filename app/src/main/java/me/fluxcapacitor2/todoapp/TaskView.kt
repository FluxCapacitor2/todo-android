package me.fluxcapacitor2.todoapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import me.fluxcapacitor2.todoapp.api.model.Task
import me.fluxcapacitor2.todoapp.utils.formatDueDate

/**
 * A card that contains basic information about a task.
 * When clicked, opens a [TaskDetailView] to provide more
 * information and the ability to edit the task.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskView(task: Task) {

    var modalBottomSheet by remember {
        mutableStateOf(false)
    }

    TaskProvider(initial = task) { task ->
        ElevatedCard(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(4.dp),
            onClick = { modalBottomSheet = true }
        ) {
            Row(modifier = Modifier.padding(5.dp)) {
                Checkbox(checked = task.value.completed, onCheckedChange = { task.value = task.value.copy(completed = it) })
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(task.value.name, fontSize = TextUnit(19F, TextUnitType.Sp))
                    if (task.value.description.isNotEmpty()) {
                        OutlinedCard(
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                task.value.description.replace("\n\n", "\n"),
                                maxLines = 4,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                    if (!task.value.dueDate.isNullOrEmpty()) {
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
                                Text(task.value.formatDueDate(), modifier = Modifier.padding(5.dp))
                            }
                        }
                    }
                }
            }
        }
        if (modalBottomSheet) {
            TaskDetailView(taskState = task) { modalBottomSheet = false }
        }
    }
}
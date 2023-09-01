package me.fluxcapacitor2.todoapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import me.fluxcapacitor2.todoapp.api.model.Task
import me.fluxcapacitor2.todoapp.utils.formatDueDate
import me.fluxcapacitor2.todoapp.utils.formatStartDate
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailView(taskState: MutableState<Task>, onClose: () -> Unit) {
    val task = taskState.value
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onClose, sheetState = sheetState, modifier = Modifier.fillMaxHeight(), windowInsets = WindowInsets.statusBars) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.completed, onCheckedChange = {taskState.value = task.copy(completed = it)})
                Text(text = task.name, fontSize = TextUnit(24f, TextUnitType.Sp))
            }

            Column {
                Text(
                    text = "Description",
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(20f, TextUnitType.Sp)
                )
                TextField(
                    value = task.description,
                    onValueChange = { taskState.value = task.copy(description = it) },
                    placeholder = { Text("Add description", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )
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
                            TextButton(onClick = {
                                // TODO - this is completely broken
                                dueDatePickerOpen = false
                                val millis = dueDatePickerState.selectedDateMillis ?: return@TextButton
                                val zonedDateFormat = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
                                taskState.value = taskState.value.copy(dueDate = zonedDateFormat.format(DateTimeFormatter.ISO_DATE_TIME.withZone(
                                    ZoneOffset.UTC)))
                            }) {
                                Text(text = "Confirm")
                            }
                        }) {
                        DatePicker(state = dueDatePickerState)
                    }
                }

                var startDatePickerOpen by remember {
                    mutableStateOf(false)
                }
                val startDatePickerState = rememberDatePickerState()
                if (startDatePickerOpen) {
                    DatePickerDialog(
                        onDismissRequest = { startDatePickerOpen = false },
                        dismissButton = {
                            TextButton(onClick = {
                                startDatePickerOpen = false
                            }) {
                                Text(text = "Close")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                startDatePickerOpen = false
                                val millis = startDatePickerState.selectedDateMillis ?: return@TextButton
                                val zonedDateFormat = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC)
                                taskState.value = taskState.value.copy(startDate = zonedDateFormat.format(DateTimeFormatter.ISO_DATE_TIME))
                            }) {
                                Text(text = "Confirm")

                            }
                        }) {
                        DatePicker(state = startDatePickerState)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { startDatePickerOpen = true },
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

            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)
            )
        }
    }
}
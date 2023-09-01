package me.fluxcapacitor2.todoapp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import me.fluxcapacitor2.todoapp.api.model.Section

/**
 * A column containing a section label, list of tasks, and a button to add a new task.
 */
@Composable
fun SectionView(section: Section) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp, 0.dp)
            .fillMaxHeight()
    ) {
        // Section name
        item {
            Row {
                Text(
                    text = section.name,
                    fontSize = TextUnit(20F, TextUnitType.Sp),
                    modifier = Modifier.padding(5.dp)
                )
                IconButton(onClick = {
                    // TODO open "edit section name" dialog
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
        }
        // Task list
        items(section.tasks.size) {
            TaskView(section.tasks[it])
        }
        // Add item button
        item {
            Button(
                onClick = { }, modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
            ) { Text(text = "Add Item", maxLines = 3) }
        }
        // Spacer for navigation bar
        item {
            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)
            )
        }
    }
}
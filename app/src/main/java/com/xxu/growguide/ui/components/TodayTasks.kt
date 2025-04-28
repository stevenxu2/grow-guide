package com.xxu.growguide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxu.growguide.ui.viewmodels.UserPlantWithDetails
import java.util.concurrent.TimeUnit

/**
 * Purpose: Displays a section showing the user's tasks for today and upcoming days
 *
 * @param gardenPlants List of the user's plants with details
 * @param onTaskClick Callback for when a task is clicked
 * @param onCheckTask Callback for when a task is checked/completed
 * @param onSeeAllClick Callback for when the "See all" button is clicked
 */
@Composable
fun TodayTasks(
    gardenPlants: List<UserPlantWithDetails?>,
    onTaskClick: (Long) -> Unit = {},
    onCheckTask: (Long, TaskType) -> Unit = { _, _ -> },
    onSeeAllClick: () -> Unit = {}
) {
    // If there are no plants, don't show the tasks section
    if (gardenPlants.isEmpty()) return

    // Generate tasks based on plant conditions
    val tasks = generateTasks(gardenPlants)

    // Limit to maximum 3 tasks on home screen
    val tasksToShow = tasks.take(3)

    // State for confirmation dialog
    var showWaterDialog by remember { mutableStateOf(false) }
    var plantToWater: Long? by remember { mutableStateOf(null) }
    var taskType: TaskType? by remember { mutableStateOf(null) }
    var hasConfirmedDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (tasks.size > 3) {
                TextButton(onClick = onSeeAllClick) {
                    Text(
                        text = "See all (${tasks.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = "${tasks.size} tasks",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (tasksToShow.isEmpty()) {
            // Show a message when there are no tasks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tasks for today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Show tasks
            tasksToShow.forEach { task ->
                TaskCard(
                    task = task,
                    onClick = { onTaskClick(task.plantId) },
                    onCheck = { isCheck: MutableState<Boolean> ->
                        isCheck.value = hasConfirmedDialog
                        showWaterDialog = true
                        plantToWater = task.plantId
                        taskType = task.type
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))

    // Watering dialog
    if (showWaterDialog && plantToWater != null) {
        AlertDialog(
            onDismissRequest = {
                hasConfirmedDialog = false
                showWaterDialog = false
                plantToWater = null
                taskType = null
            },
            title = { Text("Water Plant") },
            text = { Text("Record that you've watered this plant today?") },
            confirmButton = {
                Button(
                    onClick = {
                        onCheckTask(plantToWater!!, taskType!!)
                        hasConfirmedDialog = true
                        showWaterDialog = false
                        plantToWater = null
                        taskType = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Yes, I watered it")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        hasConfirmedDialog = false
                        showWaterDialog = false
                        plantToWater = null
                        taskType = null
                    }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

/**
 * Different types of tasks that can be assigned
 */
enum class TaskType {
    WATER_URGENT,
    WATER_SOON,
    CHECK_SUNLIGHT,
    WATER_ROUTINE,
    WATER_NEW_PLANT
}

/**
 * Data class for task info
 */
data class PlantTask(
    val plantId: Long,
    val plantName: String,
    val type: TaskType,
    val priority: Int, // 1 = highest, 3 = lowest
    val dueText: String,
    val isCompleted: Boolean = false
)

/**
 * Purpose: Generate tasks based on plants and their conditions
 */
fun generateTasks(plants: List<UserPlantWithDetails?>): List<PlantTask> {
    val tasks = mutableListOf<PlantTask>()
    val currentTime = System.currentTimeMillis()

    plants.filterNotNull().forEach { plantWithDetails ->
        val userPlant = plantWithDetails.userPlant
        val plant = plantWithDetails.plant

        // Get plant nickname or common name
        val plantName = userPlant.nickname ?: plant.commonName

        // 1. Check watering needs based on plant requirements
        val (wateringDays, _) = when {
            plant.watering.contains("frequent", ignoreCase = true) -> Pair(2, "Every 2-3 days")
            plant.watering.contains("average", ignoreCase = true) -> Pair(7, "Weekly")
            plant.watering.contains("minimum", ignoreCase = true) -> Pair(14, "Every 2-3 weeks")
            else -> Pair(7, "When soil is dry")
        }

        // Check if plant has never been watered
        val neverWatered = userPlant.lastWateredDate == null

        // Check how long since the plant was added to the garden
        val daysSinceAdded = TimeUnit.MILLISECONDS.toDays(currentTime - userPlant.dateAdded)

        // New plant that hasn't been watered yet (added within last 3 days)
        if (neverWatered && daysSinceAdded <= 3) {
            tasks.add(
                PlantTask(
                    plantId = userPlant.userPlantId,
                    plantName = plantName,
                    type = TaskType.WATER_NEW_PLANT,
                    priority = 1, // High priority for new plants
                    dueText = "New plant - needs first watering"
                )
            )
        } else {
            val lastWatered = userPlant.lastWateredDate ?: userPlant.plantingDate
            val daysSinceWatering = TimeUnit.MILLISECONDS.toDays(currentTime - lastWatered)

            // Calculate watering status
            val wateringProgress = if (wateringDays > 0) {
                (1f - (daysSinceWatering.toFloat() / wateringDays.toFloat())).coerceIn(0f, 1f)
            } else 1f

            when {
                // Urgent water need
                wateringProgress < 0.3f -> {
                    tasks.add(
                        PlantTask(
                            plantId = userPlant.userPlantId,
                            plantName = plantName,
                            type = TaskType.WATER_URGENT,
                            priority = 1,
                            dueText = "Overdue"
                        )
                    )
                }
                // Need water soon
                wateringProgress < 0.5f -> {
                    tasks.add(
                        PlantTask(
                            plantId = userPlant.userPlantId,
                            plantName = plantName,
                            type = TaskType.WATER_SOON,
                            priority = 2,
                            dueText = "Due today"
                        )
                    )
                }
                // Regular watering reminder for plants not watered in 5+ days
                daysSinceWatering > 5 && neverWatered && daysSinceAdded > 3 -> {
                    tasks.add(
                        PlantTask(
                            plantId = userPlant.userPlantId,
                            plantName = plantName,
                            type = TaskType.WATER_ROUTINE,
                            priority = 3,
                            dueText = "Not watered yet"
                        )
                    )
                }
            }
        }

//        if (plant.sunlight.contains("full sun", ignoreCase = true) &&
//            plant.indoor &&
//            (userPlant.dateAdded > currentTime - TimeUnit.DAYS.toMillis(7))) {
//            tasks.add(
//                PlantTask(
//                    plantId = userPlant.userPlantId,
//                    plantName = plantName,
//                    type = TaskType.CHECK_SUNLIGHT,
//                    priority = 2,
//                    dueText = "Recently added"
//                )
//            )
//        }
    }

    // Sort tasks by priority
    return tasks.sortedBy { it.priority }
}

/**
 * Purpose: Displays a single task card with checkbox, title, icon and due date
 *
 * @param task The task data to display
 * @param onClick Callback for when the card is clicked
 * @param onCheck Callback for when the checkbox is checked
 */
@Composable
fun TaskCard(
    task: PlantTask,
    onClick: () -> Unit,
    onCheck: (MutableState<Boolean>) -> Unit
) {
    val isChecked = remember { mutableStateOf(task.isCompleted) }

    // Determine icon and colors based on task type
    val (icon, iconTint, cardColor) = when (task.type) {
        TaskType.WATER_URGENT -> Triple(
            Icons.Default.WaterDrop,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
        TaskType.WATER_SOON -> Triple(
            Icons.Default.WaterDrop,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
        TaskType.CHECK_SUNLIGHT -> Triple(
            Icons.Default.WbSunny,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiaryContainer
        )
        TaskType.WATER_ROUTINE -> Triple(
            Icons.Default.WaterDrop,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondaryContainer
        )
        TaskType.WATER_NEW_PLANT -> Triple(
            Icons.Default.WaterDrop,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
        )
    }

    // Task title based on type
    val taskTitle = when (task.type) {
        TaskType.WATER_URGENT -> "Water ${task.plantName} urgently"
        TaskType.WATER_SOON -> "Water ${task.plantName}"
        TaskType.CHECK_SUNLIGHT -> "Check sunlight for ${task.plantName}"
        TaskType.WATER_ROUTINE -> "Water ${task.plantName}"
        TaskType.WATER_NEW_PLANT -> "First watering for ${task.plantName}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor.copy(alpha = 0.1f))
//            .clickable { onClick() }
            .border(1.dp, cardColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                //isChecked.value = it
                onCheck(isChecked)
            },
            colors = CheckboxDefaults.colors(
//                checkedColor = iconTint,
//                uncheckedColor = MaterialTheme.colorScheme.background,
//                checkmarkColor = MaterialTheme.colorScheme.onSurface,
                checkedColor = iconTint,
                checkmarkColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurface,
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

//        Icon(
//            imageVector = icon,
//            contentDescription = null,
//            tint = iconTint,
//            modifier = Modifier.size(20.dp)
//        )
//
//        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = taskTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = task.dueText,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = if (task.type == TaskType.WATER_URGENT)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}
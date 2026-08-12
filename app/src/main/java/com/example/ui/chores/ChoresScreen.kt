package com.example.ui.chores

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.RoomMateXViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoresScreen(
    viewModel: RoomMateXViewModel
) {
    val chores by viewModel.chores.collectAsState()
    val members by viewModel.members.collectAsState()

    var selectedTab by remember { mutableStateOf("PENDING") } // PENDING, COMPLETED, ALL
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredChores = when (selectedTab) {
        "PENDING" -> chores.filter { it.status == ChoreStatus.PENDING || it.status == ChoreStatus.IN_PROGRESS }
        "COMPLETED" -> chores.filter { it.status == ChoreStatus.COMPLETED || it.status == ChoreStatus.VERIFIED }
        else -> chores
    }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ElectricCyan,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.testTag("add_chore_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Chore")
            }
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "House Chores",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Turn chores into XP and level up your household status",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .padding(4.dp)
            ) {
                listOf("PENDING" to "Pending", "COMPLETED" to "Completed", "ALL" to "All Tasks").forEach { (tabKey, label) ->
                    val isSelected = selectedTab == tabKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricViolet else Color.Transparent)
                            .clickable { selectedTab = tabKey }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredChores.isEmpty()) {
                EmptyStateCard(
                    icon = Icons.Default.CleaningServices,
                    title = "No Chores Found",
                    description = if (selectedTab == "PENDING") "All assigned chores are complete! Add a new one below." else "No chores in this category.",
                    actionText = "Add First Chore",
                    onAction = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredChores, key = { it.id }) { chore ->
                        ChoreCard(
                            chore = chore,
                            onComplete = { viewModel.completeChore(chore) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddChoreDialog(
            members = members,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, assigneeId, assigneeName, priority, difficulty, recurrence ->
                viewModel.addChore(title, desc, assigneeId, assigneeName, priority, difficulty, recurrence)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ChoreCard(
    chore: Chore,
    onComplete: () -> Unit
) {
    val isDone = chore.status == ChoreStatus.COMPLETED || chore.status == ChoreStatus.VERIFIED

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
        color = DarkSurface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onComplete,
                        enabled = !isDone
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Complete",
                            tint = if (isDone) NeonMint else ElectricCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = chore.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        )
                        if (chore.description.isNotBlank()) {
                            Text(
                                text = chore.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    color = GoldXp.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "+${chore.xpReward} XP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldXp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = DarkCardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = DarkSurfaceVariant, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = "Assigned: ${chore.assignedToName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(color = DarkSurfaceVariant, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = chore.difficulty.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (chore.recurrence != ChoreRecurrence.NONE) {
                    Text(
                        text = "🔁 ${chore.recurrence.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChoreDialog(
    members: List<HouseMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, ChorePriority, ChoreDifficulty, ChoreRecurrence) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedMember by remember { mutableStateOf(members.firstOrNull() ?: HouseMember("m1", "h1", "u_atul_1", "Atul Bhagwat", HouseRole.OWNER)) }
    var selectedDifficulty by remember { mutableStateOf(ChoreDifficulty.MEDIUM) }
    var selectedRecurrence by remember { mutableStateOf(ChoreRecurrence.NONE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign New Chore", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Chore Title (e.g. Wash Dishes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Instructions / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Assign To:", style = MaterialTheme.typography.labelLarge, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.take(3).forEach { m ->
                        val isSel = m.userId == selectedMember.userId
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedMember = m },
                            label = { Text(m.name.split(" ").first()) }
                        )
                    }
                }

                Text("Difficulty & XP Reward:", style = MaterialTheme.typography.labelLarge, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ChoreDifficulty.values().forEach { diff ->
                        val isSel = diff == selectedDifficulty
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedDifficulty = diff },
                            label = { Text("${diff.name} (${diff.xpReward} XP)") }
                        )
                    }
                }

                Text("Recurrence:", style = MaterialTheme.typography.labelLarge, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ChoreRecurrence.values().forEach { rec ->
                        val isSel = rec == selectedRecurrence
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedRecurrence = rec },
                            label = { Text(rec.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title,
                            desc,
                            selectedMember.userId,
                            selectedMember.name,
                            ChorePriority.MEDIUM,
                            selectedDifficulty,
                            selectedRecurrence
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
            ) {
                Text("ASSIGN CHORE", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = DarkSurface
    )
}

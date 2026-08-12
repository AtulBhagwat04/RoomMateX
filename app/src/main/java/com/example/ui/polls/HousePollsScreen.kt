package com.example.ui.polls

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Poll
import com.example.ui.RoomMateXViewModel
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HousePollsScreen(
    viewModel: RoomMateXViewModel
) {
    val polls by viewModel.polls.collectAsState()
    val user by viewModel.user.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ElectricViolet,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Poll")
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
                text = "Household Polls",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Vote & make democratic decisions for your apartment",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (polls.isEmpty()) {
                EmptyStateCard(
                    icon = Icons.Default.Poll,
                    title = "No Active Polls",
                    description = "Need to decide on buying a microwave or party dates? Start a poll!",
                    actionText = "Create First Poll",
                    onAction = { showCreateDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(polls, key = { it.id }) { poll ->
                        PollCard(
                            poll = poll,
                            currentUserId = user.id,
                            onVote = { optionIdx -> viewModel.votePoll(poll.id, optionIdx) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePollDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { question, options ->
                viewModel.createPoll(question, options)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun PollCard(
    poll: Poll,
    currentUserId: String,
    onVote: (Int) -> Unit
) {
    val totalVotes = poll.options.sumOf { it.voteCount }
    val userVotedIdx = poll.userVotes[currentUserId]

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
        color = DarkSurface
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = poll.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Created by ${poll.createdByName} • $totalVotes total votes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            poll.options.forEachIndexed { idx, option ->
                val isSelected = userVotedIdx == idx
                val percent = if (totalVotes > 0) (option.voteCount.toFloat() / totalVotes.toFloat()) else 0f

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, if (isSelected) ElectricCyan else DarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onVote(idx) },
                    color = DarkSurfaceVariant
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Progress Fill
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percent)
                                .background(ElectricViolet.copy(alpha = 0.35f))
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ElectricCyan else Color.White
                            )

                            Text(
                                text = "${(percent * 100).toInt()}% (${option.voteCount})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePollDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>) -> Unit
) {
    var question by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }
    var opt3 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create House Poll", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Poll Question") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = opt1,
                    onValueChange = { opt1 = it },
                    label = { Text("Option 1") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = opt2,
                    onValueChange = { opt2 = it },
                    label = { Text("Option 2") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = opt3,
                    onValueChange = { opt3 = it },
                    label = { Text("Option 3 (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val opts = listOf(opt1, opt2, opt3).filter { it.isNotBlank() }
                    if (question.isNotBlank() && opts.size >= 2) {
                        onConfirm(question, opts)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("CREATE POLL", color = Color.White, fontWeight = FontWeight.Bold)
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

package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.RoomMateXViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: RoomMateXViewModel,
    onNavigateToExpenses: () -> Unit,
    onNavigateToChores: () -> Unit,
    onNavigateToBalances: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToHouseSettings: () -> Unit,
    onOpenGlobalSearch: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val house by viewModel.house.collectAsState()
    val members by viewModel.members.collectAsState()
    val chores by viewModel.chores.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val (netBalances, optimizedSettlements) = viewModel.balancesAndSettlements.collectAsState().value

    val pendingChores = chores.filter { it.status == ChoreStatus.PENDING || it.status == ChoreStatus.IN_PROGRESS }
    val totalSpending = expenses.sumOf { it.amount }

    // User's net balance summary
    val userNet = netBalances.find { it.userId == user.id }?.netAmount ?: 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // 1. Header Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToHouseSettings() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(ElectricCyan, ElectricViolet))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = house.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Switch House",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "${house.memberCount} Members • Code: ${house.inviteCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenGlobalSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                    IconButton(onClick = onOpenNotifications) {
                        BadgedBox(
                            badge = { Badge(containerColor = CoralRed) { Text("2") } }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. Personal Greeting & Streak Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good day, ${user.name.split(" ").firstOrNull() ?: "Roommate"} 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Share the space. Share the responsibility.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Streak Badge
                Surface(
                    color = AmberOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberOrange.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user.currentStreak} Days",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = AmberOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // 3. XP Card
        item {
            XpCard(
                xp = user.xp,
                level = user.level,
                title = user.name
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Quick Actions
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    title = "Expense",
                    icon = Icons.Default.AddCard,
                    gradient = listOf(ElectricViolet, DeepViolet),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToExpenses
                )
                QuickActionButton(
                    title = "Chore",
                    icon = Icons.Default.CleaningServices,
                    gradient = listOf(ElectricCyan, Color(0xFF00B0FF)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToChores
                )
                QuickActionButton(
                    title = "Settle",
                    icon = Icons.Default.Handshake,
                    gradient = listOf(NeonMint, Color(0xFF00C853)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBalances
                )
                QuickActionButton(
                    title = "Shopping",
                    icon = Icons.Default.ShoppingCart,
                    gradient = listOf(AmberOrange, Color(0xFFFF6D00)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToShopping
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5. Household Expense Summary Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ElectricCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Expense Summary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        TextButton(onClick = onNavigateToBalances) {
                            Text("Details", color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Your Balance Status", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (userNet >= 0) "+₹${userNet.toInt()}" else "-₹${(-userNet).toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (userNet >= 0) NeonMint else CoralRed
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Household Spending", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "₹${totalSpending.toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 6. Pending Chores Section
        item {
            SectionHeader(
                title = "Pending Chores (${pendingChores.size})",
                actionText = "See All",
                onActionClick = onNavigateToChores
            )

            if (pendingChores.isEmpty()) {
                EmptyStateCard(
                    icon = Icons.Default.CheckCircle,
                    title = "All Chores Completed!",
                    description = "No pending household tasks right now. Great teamwork!"
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    pendingChores.take(3).forEach { chore ->
                        ChoreItemRow(
                            chore = chore,
                            onComplete = { viewModel.completeChore(chore) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 7. Leaderboard Preview
        item {
            SectionHeader(
                title = "Household Leaderboard",
                actionText = "Full Board",
                onActionClick = onNavigateToLeaderboard
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    members.sortedByDescending { if (it.userId == user.id) user.xp else 250 }.take(3).forEachIndexed { idx, member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when(idx) { 0 -> "🥇"; 1 -> "🥈"; 2 -> "🥉"; else -> "#${idx + 1}" },
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.width(36.dp)
                                )
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = if (member.userId == user.id) "${user.xp} XP" else "250 XP",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldXp
                            )
                        }
                        if (idx < 2) Divider(color = DarkCardBorder)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 8. Recent Activity Feed
        item {
            SectionHeader(title = "Recent Activity")

            if (activities.isEmpty()) {
                Text(
                    text = "No recent activity yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    activities.take(4).forEach { activity ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            color = DarkSurfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ElectricViolet.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when(activity.type) {
                                            "EXPENSE" -> Icons.Default.ReceiptLong
                                            "CHORE" -> Icons.Default.CleaningServices
                                            "SETTLEMENT" -> Icons.Default.Handshake
                                            "LEVEL_UP" -> Icons.Default.EmojiEvents
                                            else -> Icons.Default.FlashOn
                                        },
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = activity.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "By ${activity.userName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ChoreItemRow(
    chore: Chore,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp)),
        color = DarkSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onComplete) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Complete Chore",
                        tint = ElectricCyan
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = chore.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Assigned to ${chore.assignedToName} • ${chore.difficulty.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = GoldXp.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "+${chore.xpReward} XP",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldXp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

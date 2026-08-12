package com.example.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GamificationEngine
import com.example.model.*
import com.example.ui.RoomMateXViewModel
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    viewModel: RoomMateXViewModel
) {
    val members by viewModel.members.collectAsState()
    val user by viewModel.user.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") } // WEEKLY, MONTHLY, ALL

    // Ranked member list with current user updated XP
    val rankedMembers = members.map { m ->
        if (m.userId == user.id) {
            Pair(m, user.xp)
        } else {
            val xpVal = when(m.userId) {
                "u_rahul_2" -> 280
                "u_aman_3" -> 210
                else -> 150
            }
            Pair(m, xpVal)
        }
    }.sortedByDescending { it.second }

    Scaffold(
        containerColor = DarkBackground
    ) { insets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Household Leaderboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Compete, earn XP & earn the House Legend crown",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .padding(4.dp)
            ) {
                listOf("WEEKLY" to "Weekly", "MONTHLY" to "Monthly", "ALL" to "All Time").forEach { (filterKey, label) ->
                    val isSel = selectedFilter == filterKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) ElectricViolet else Color.Transparent)
                            .clickable { selectedFilter = filterKey }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Podium Display for Top 3
            if (rankedMembers.size >= 3) {
                PodiumSection(
                    first = rankedMembers[0],
                    second = rankedMembers[1],
                    third = rankedMembers[2]
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ranked List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                itemsIndexed(rankedMembers) { idx, (member, xpVal) ->
                    val levelInfo = GamificationEngine.getLevelForXp(xpVal)
                    val isCurrentUser = member.userId == user.id

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                1.dp,
                                if (isCurrentUser) ElectricCyan else DarkCardBorder,
                                RoundedCornerShape(18.dp)
                            ),
                        color = if (isCurrentUser) DarkSurfaceVariant else DarkSurface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${idx + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when(idx) { 0 -> GoldXp; 1 -> Color(0xFFC0C0C0); 2 -> Color(0xFFCD7F32); else -> Color.Gray },
                                    modifier = Modifier.width(36.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ElectricViolet.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.name.take(1),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = member.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        if (isCurrentUser) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(color = ElectricCyan, shape = RoundedCornerShape(6.dp)) {
                                                Text("YOU", color = Color.Black, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                    Text(
                                        text = levelInfo.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = GoldXp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$xpVal XP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldXp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumSection(
    first: Pair<HouseMember, Int>,
    second: Pair<HouseMember, Int>,
    third: Pair<HouseMember, Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place
        PodiumPillar(
            member = second.first,
            xp = second.second,
            rankText = "2nd 🥈",
            height = 130.dp,
            color = Color(0xFFC0C0C0)
        )

        // 1st Place
        PodiumPillar(
            member = first.first,
            xp = first.second,
            rankText = "1st 👑",
            height = 160.dp,
            color = GoldXp
        )

        // 3rd Place
        PodiumPillar(
            member = third.first,
            xp = third.second,
            rankText = "3rd 🥉",
            height = 110.dp,
            color = Color(0xFFCD7F32)
        )
    }
}

@Composable
fun PodiumPillar(
    member: HouseMember,
    xp: Int,
    rankText: String,
    height: Dp,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(member.name.take(1), fontWeight = FontWeight.ExtraBold, color = color)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(member.name.split(" ").first(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Text("$xp XP", style = MaterialTheme.typography.labelSmall, color = color)

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            modifier = Modifier
                .width(80.dp)
                .height(height),
            color = DarkSurface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(rankText, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

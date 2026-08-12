package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.RoomMateXViewModel
import com.example.ui.components.XpCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: RoomMateXViewModel,
    onNavigateToAchievements: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToHouseSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val house by viewModel.house.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // Header Profile Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                    color = DarkSurface
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(ElectricCyan, ElectricViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text(user.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (user.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(user.bio, style = MaterialTheme.typography.bodySmall, color = ElectricCyan)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // XP Card
            item {
                XpCard(xp = user.xp, level = user.level, title = user.name)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Settings Options List
            item {
                Text("Settings & Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileOptionRow(icon = Icons.Default.EmojiEvents, title = "Achievements & Trophies", onClick = onNavigateToAchievements)
                    ProfileOptionRow(icon = Icons.Default.BarChart, title = "Analytics & Reports", onClick = onNavigateToAnalytics)
                    ProfileOptionRow(icon = Icons.Default.Home, title = "House Settings (${house.name})", onClick = onNavigateToHouseSettings)
                    ProfileOptionRow(icon = Icons.Default.Notifications, title = "Notification Preferences") {}
                    ProfileOptionRow(icon = Icons.Default.Security, title = "Privacy & Security") {}
                    ProfileOptionRow(icon = Icons.Default.ExitToApp, title = "Log Out", iconColor = CoralRed, onClick = onLogout)
                }
            }
        }
    }

    if (showEditProfileDialog) {
        var name by remember { mutableStateOf(user.name) }
        var phone by remember { mutableStateOf(user.phone) }
        var bio by remember { mutableStateOf(user.bio) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio / Tagline") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(name, phone, bio)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") } },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    iconColor: Color = ElectricCyan,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp)),
        color = DarkSurface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconColor)
                Spacer(modifier = Modifier.width(14.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

package com.example.ui.house

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.HouseMember
import com.example.ui.RoomMateXViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseManagementScreen(
    viewModel: RoomMateXViewModel,
    onNavigateBack: () -> Unit
) {
    val house by viewModel.house.collectAsState()
    val members by viewModel.members.collectAsState()

    var showCreateHouseDialog by remember { mutableStateOf(false) }
    var showJoinHouseDialog by remember { mutableStateOf(false) }
    var showQrCodeModal by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("House Settings & Members", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // House Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, ElectricCyan, RoundedCornerShape(20.dp)),
                    color = DarkSurface
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(house.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text("Currency: ${house.currency} • Invite Code: ${house.inviteCode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            IconButton(onClick = { showQrCodeModal = true }) {
                                Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = ElectricCyan, modifier = Modifier.size(32.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { showJoinHouseDialog = true },
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                            ) {
                                Text("Join House", color = ElectricCyan, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showCreateHouseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                            ) {
                                Text("Create New House", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Household Members (${members.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Member list
            items(members) { m ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ElectricViolet.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(m.name.take(1), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(m.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Surface(
                            color = if (m.role.name == "OWNER") ElectricCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = m.role.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (m.role.name == "OWNER") ElectricCyan else Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showQrCodeModal) {
        AlertDialog(
            onDismissRequest = { showQrCodeModal = false },
            title = { Text("House Invite QR Code", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.Black, modifier = Modifier.size(150.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Roommates can scan this or enter code:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(house.inviteCode, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = ElectricCyan)
                }
            },
            confirmButton = {
                Button(onClick = { showQrCodeModal = false }, colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)) {
                    Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showCreateHouseDialog) {
        var newHouseName by remember { mutableStateOf("") }
        var currency by remember { mutableStateOf("₹") }

        AlertDialog(
            onDismissRequest = { showCreateHouseDialog = false },
            title = { Text("Create House", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newHouseName,
                        onValueChange = { newHouseName = it },
                        label = { Text("House Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Currency Symbol (e.g. ₹, $, €)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHouseName.isNotBlank()) {
                            viewModel.createHouse(newHouseName, currency)
                            showCreateHouseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                ) {
                    Text("CREATE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateHouseDialog = false }) { Text("Cancel") }
            },
            containerColor = DarkSurface
        )
    }

    if (showJoinHouseDialog) {
        var codeInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showJoinHouseDialog = false },
            title = { Text("Join Existing House", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it },
                        label = { Text("Invite Code (e.g. RMX-8921)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (codeInput.isNotBlank()) {
                            viewModel.joinHouse(codeInput)
                            showJoinHouseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("JOIN", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinHouseDialog = false }) { Text("Cancel") }
            },
            containerColor = DarkSurface
        )
    }
}

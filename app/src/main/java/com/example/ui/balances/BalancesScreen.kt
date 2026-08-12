package com.example.ui.balances

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.RoomMateXViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun BalancesScreen(
    viewModel: RoomMateXViewModel
) {
    val (netBalances, optimizedSettlements) = viewModel.balancesAndSettlements.collectAsState().value
    val settlements by viewModel.settlements.collectAsState()
    val members by viewModel.members.collectAsState()

    var showSettleDialog by remember { mutableStateOf(false) }
    var preselectedPayee by remember { mutableStateOf<HouseMember?>(null) }

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
            item {
                Text(
                    text = "Balances & Settlement",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Simplified debt matrix & direct settlement record",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 1. Optimized Settlement Suggestions
            item {
                SectionHeader(title = "Optimized Settlement Paths")

                if (optimizedSettlements.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                        color = DarkSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎉 All Balances Settled!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NeonMint)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "No pending debts in the household right now.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        optimizedSettlements.forEach { opt ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                                color = DarkSurface
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(opt.fromUserName, fontWeight = FontWeight.Bold, color = CoralRed)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(opt.toUserName, fontWeight = FontWeight.Bold, color = NeonMint)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${opt.fromUserName} pays ${opt.toUserName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val targetPayee = members.find { it.userId == opt.toUserId }
                                            preselectedPayee = targetPayee
                                            showSettleDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonMint)
                                    ) {
                                        Text("Settle ₹${opt.amount.toInt()}", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. Individual Net Balances
            item {
                SectionHeader(title = "Individual Household Balances")

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    netBalances.forEach { net ->
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
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = net.userName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = if (net.netAmount >= 0) "Gets back ₹${net.netAmount.toInt()}" else "Owes ₹${(-net.netAmount).toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (net.netAmount >= 0) NeonMint else CoralRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3. Settlement History
            item {
                SectionHeader(title = "Settlement History")

                if (settlements.isEmpty()) {
                    Text("No settlements recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        settlements.forEach { set ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp)),
                                color = DarkSurfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Handshake, contentDescription = null, tint = NeonMint)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${set.payerName} paid ₹${set.amount.toInt()} to ${set.payeeName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        if (set.note.isNotBlank()) {
                                            Text(set.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSettleDialog) {
        SettleDialog(
            members = members,
            preselectedPayee = preselectedPayee,
            onDismiss = { showSettleDialog = false },
            onConfirm = { payeeId, payeeName, amt, note ->
                viewModel.recordSettlement(payeeId, payeeName, amt, note)
                showSettleDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleDialog(
    members: List<HouseMember>,
    preselectedPayee: HouseMember?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String) -> Unit
) {
    var selectedPayee by remember { mutableStateOf(preselectedPayee ?: members.firstOrNull { it.userId != "u_atul_1" } ?: members.first()) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Settlement", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select Roommate Paid:", style = MaterialTheme.typography.labelLarge, color = Color.White)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.filter { it.userId != "u_atul_1" }.forEach { m ->
                        val isSel = m.userId == selectedPayee.userId
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedPayee = m },
                            label = { Text(m.name.split(" ").first()) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Settlement Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (e.g. UPI Transfer / Cash)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(selectedPayee.userId, selectedPayee.name, amt, note)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonMint)
            ) {
                Text("RECORD SETTLEMENT", color = Color.Black, fontWeight = FontWeight.Bold)
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

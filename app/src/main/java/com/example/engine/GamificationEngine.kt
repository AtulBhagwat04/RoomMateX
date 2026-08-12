package com.example.engine

import com.example.model.*
import kotlin.math.min
import kotlin.math.roundToInt

object GamificationEngine {

    data class LevelInfo(
        val level: Int,
        val title: String,
        val minXp: Int,
        val maxXp: Int
    )

    val LEVELS = listOf(
        LevelInfo(1, "New Roommate", 0, 100),
        LevelInfo(2, "Helper", 100, 300),
        LevelInfo(3, "Responsible Roommate", 300, 600),
        LevelInfo(4, "House Captain", 600, 1000),
        LevelInfo(5, "House Hero", 1000, 1500),
        LevelInfo(6, "House Legend", 1500, 2500)
    )

    fun getLevelForXp(xp: Int): LevelInfo {
        for (i in LEVELS.indices.reversed()) {
            if (xp >= LEVELS[i].minXp) {
                return LEVELS[i]
            }
        }
        return LEVELS[0]
    }

    fun getXpProgress(xp: Int): Float {
        val levelInfo = getLevelForXp(xp)
        val range = levelInfo.maxXp - levelInfo.minXp
        if (range <= 0) return 1.0f
        val current = xp - levelInfo.minXp
        return (current.toFloat() / range.toFloat()).coerceIn(0f, 1f)
    }

    /**
     * Calculates optimized settlement paths between members to minimize total payments.
     * Takes all expenses and previous settlements in the house.
     */
    fun calculateOptimizedSettlements(
        members: List<HouseMember>,
        expenses: List<Expense>,
        settlements: List<Settlement>
    ): Pair<List<NetBalance>, List<OptimizedSettlement>> {
        if (members.isEmpty()) return Pair(emptyList(), emptyList())

        // Step 1: Initialize balance map for each member userId -> net balance (in currency)
        val balances = members.associate { it.userId to 0.0 }.toMutableMap()

        // Step 2: Calculate net effect of expenses
        for (expense in expenses) {
            val totalAmount = expense.amount
            val payerId = expense.paidById
            val participants = if (expense.participantIds.isNotEmpty()) expense.participantIds else members.map { it.userId }
            if (participants.isEmpty()) continue

            // Credit the payer full amount
            balances[payerId] = (balances[payerId] ?: 0.0) + totalAmount

            // Debtor calculations based on split type (Equal by default)
            val sharePerPerson = totalAmount / participants.size
            for (pId in participants) {
                balances[pId] = (balances[pId] ?: 0.0) - sharePerPerson
            }
        }

        // Step 3: Factor in existing settlements recorded
        for (settlement in settlements) {
            // Payer gave money to Payee
            balances[settlement.payerId] = (balances[settlement.payerId] ?: 0.0) + settlement.amount
            balances[settlement.payeeId] = (balances[settlement.payeeId] ?: 0.0) - settlement.amount
        }

        // Create NetBalance objects for display
        val memberNameMap = members.associate { it.userId to it.name }
        val netBalanceList = balances.map { (uId, netAmt) ->
            NetBalance(
                userId = uId,
                userName = memberNameMap[uId] ?: "Roommate",
                netAmount = (netAmt * 100.0).roundToInt() / 100.0
            )
        }

        // Step 4: Calculate simplified settlement transactions
        val creditors = mutableListOf<Pair<String, Double>>() // (userId, positive balance)
        val debtors = mutableListOf<Pair<String, Double>>()   // (userId, absolute negative balance)

        for ((uId, balance) in balances) {
            val rounded = (balance * 100.0).roundToInt() / 100.0
            if (rounded > 0.01) {
                creditors.add(Pair(uId, rounded))
            } else if (rounded < -0.01) {
                debtors.add(Pair(uId, -rounded))
            }
        }

        val optimizedList = mutableListOf<OptimizedSettlement>()
        var i = 0
        var j = 0

        while (i < debtors.size && j < creditors.size) {
            val debtor = debtors[i]
            val creditor = creditors[j]

            val settledAmount = min(debtor.second, creditor.second)
            val roundedAmount = (settledAmount * 100.0).roundToInt() / 100.0

            if (roundedAmount > 0) {
                optimizedList.add(
                    OptimizedSettlement(
                        fromUserId = debtor.first,
                        fromUserName = memberNameMap[debtor.first] ?: "Roommate",
                        toUserId = creditor.first,
                        toUserName = memberNameMap[creditor.first] ?: "Roommate",
                        amount = roundedAmount
                    )
                )
            }

            debtors[i] = Pair(debtor.first, debtor.second - settledAmount)
            creditors[j] = Pair(creditor.first, creditor.second - settledAmount)

            if (debtors[i].second < 0.01) i++
            if (creditors[j].second < 0.01) j++
        }

        return Pair(netBalanceList, optimizedList)
    }

    val INITIAL_ACHIEVEMENTS = listOf(
        Achievement("ach_1", "First Chore", "Complete your very first household task", "ic_chore", 50, AchievementRarity.COMMON, isUnlocked = true, progress = 1, totalRequired = 1),
        Achievement("ach_2", "First Expense", "Add a shared household expense", "ic_expense", 50, AchievementRarity.COMMON, isUnlocked = true, progress = 1, totalRequired = 1),
        Achievement("ach_3", "First Settlement", "Settle balances with a roommate", "ic_settle", 75, AchievementRarity.COMMON, isUnlocked = false, progress = 0, totalRequired = 1),
        Achievement("ach_4", "7-Day Streak", "Maintain a 7-day active streak in RoomMateX", "ic_streak", 150, AchievementRarity.RARE, isUnlocked = false, progress = 5, totalRequired = 7),
        Achievement("ach_5", "Clean Freak", "Complete 10 house cleaning chores", "ic_clean", 200, AchievementRarity.RARE, isUnlocked = false, progress = 4, totalRequired = 10),
        Achievement("ach_6", "Expense Master", "Log 25 household expenses", "ic_receipt", 300, AchievementRarity.EPIC, isUnlocked = false, progress = 6, totalRequired = 25),
        Achievement("ach_7", "Early Bird", "Complete 5 chores before due date", "ic_time", 150, AchievementRarity.RARE, isUnlocked = false, progress = 2, totalRequired = 5),
        Achievement("ach_8", "House Hero", "Reach Level 5 in RoomMateX", "ic_hero", 500, AchievementRarity.LEGENDARY, isUnlocked = false, progress = 3, totalRequired = 5)
    )
}

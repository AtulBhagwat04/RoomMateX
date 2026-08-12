package com.example.model

enum class HouseRole {
    OWNER, ADMIN, MEMBER
}

enum class SplitType {
    EQUAL, PERCENTAGE, CUSTOM, SHARES
}

enum class ChorePriority {
    LOW, MEDIUM, HIGH
}

enum class ChoreDifficulty(val xpReward: Int) {
    EASY(10),
    MEDIUM(25),
    HARD(50),
    EPIC(100)
}

enum class ChoreStatus {
    PENDING, IN_PROGRESS, COMPLETED, VERIFIED, OVERDUE
}

enum class ChoreRecurrence {
    NONE, DAILY, WEEKLY, MONTHLY
}

enum class AchievementRarity {
    COMMON, RARE, EPIC, LEGENDARY
}

data class User(
    val id: String = "u_atul_1",
    val name: String = "Atul Bhagwat",
    val email: String = "atul@example.com",
    val profileImage: String = "",
    val phone: String = "+91 98765 43210",
    val bio: String = "Keeping the apartment clean & organized! 🚀",
    val xp: Int = 340,
    val level: Int = 3,
    val currentStreak: Int = 5,
    val longestStreak: Int = 12,
    val currentHouseId: String = "house_sunrise_1",
    val createdAt: Long = System.currentTimeMillis()
)

data class House(
    val id: String = "house_sunrise_1",
    val name: String = "Sunrise Apartment",
    val ownerId: String = "u_atul_1",
    val inviteCode: String = "RMX-8921",
    val currency: String = "₹",
    val timezone: String = "Asia/Kolkata",
    val memberCount: Int = 4,
    val createdAt: Long = System.currentTimeMillis()
)

data class HouseMember(
    val id: String,
    val houseId: String,
    val userId: String,
    val name: String,
    val role: HouseRole,
    val profileImage: String = "",
    val joinedAt: Long = System.currentTimeMillis()
)

data class Expense(
    val id: String,
    val houseId: String,
    val title: String,
    val amount: Double,
    val category: String, // Rent, Groceries, Electricity, Water, Internet, Food, Maintenance, Subscriptions, Other
    val paidById: String,
    val paidByName: String,
    val splitType: SplitType = SplitType.EQUAL,
    val participantIds: List<String> = emptyList(),
    val notes: String = "",
    val receiptImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Chore(
    val id: String,
    val houseId: String,
    val title: String,
    val description: String,
    val assignedToId: String,
    val assignedToName: String,
    val createdById: String,
    val dueDate: Long,
    val priority: ChorePriority = ChorePriority.MEDIUM,
    val difficulty: ChoreDifficulty = ChoreDifficulty.MEDIUM,
    val xpReward: Int = difficulty.xpReward,
    val recurrence: ChoreRecurrence = ChoreRecurrence.NONE,
    val status: ChoreStatus = ChoreStatus.PENDING,
    val completedAt: Long = 0L,
    val verifiedBy: String = ""
)

data class Settlement(
    val id: String,
    val houseId: String,
    val payerId: String,
    val payerName: String,
    val payeeId: String,
    val payeeName: String,
    val amount: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class OptimizedSettlement(
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Double
)

data class NetBalance(
    val userId: String,
    val userName: String,
    val netAmount: Double // Positive = Owed to them, Negative = Owes to household
)

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val xpReward: Int,
    val rarity: AchievementRarity,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val progress: Int = 0,
    val totalRequired: Int = 1
)

data class ActivityItem(
    val id: String,
    val houseId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String = "",
    val message: String,
    val type: String, // EXPENSE, CHORE, SETTLEMENT, LEVEL_UP, ACHIEVEMENT
    val timestamp: Long = System.currentTimeMillis()
)

data class ShoppingItem(
    val id: String,
    val houseId: String,
    val name: String,
    val quantity: String = "1",
    val category: String = "Groceries",
    val addedById: String,
    val addedByName: String,
    val assignedToId: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class PollOption(
    val optionText: String,
    val voteCount: Int = 0
)

data class Poll(
    val id: String,
    val houseId: String,
    val question: String,
    val createdById: String,
    val createdByName: String,
    val options: List<PollOption>,
    val userVotes: Map<String, Int> = emptyMap(), // userId -> optionIndex
    val expiresAt: Long,
    val createdAt: Long = System.currentTimeMillis()
)

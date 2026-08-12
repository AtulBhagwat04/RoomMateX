package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val profileImage: String = "",
    val phone: String = "",
    val bio: String = "",
    val xp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val currentHouseId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "houses")
data class HouseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ownerId: String,
    val inviteCode: String,
    val currency: String = "₹", // INR default
    val timezone: String = "Asia/Kolkata",
    val memberCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "house_members")
data class HouseMemberEntity(
    @PrimaryKey val id: String,
    val houseId: String,
    val userId: String,
    val name: String,
    val role: String, // OWNER, ADMIN, MEMBER
    val profileImage: String = "",
    val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val houseId: String,
    val title: String,
    val amount: Double,
    val category: String, // Rent, Electricity, Water, Internet, Groceries, Food, etc.
    val paidById: String,
    val paidByName: String,
    val splitType: String, // EQUAL, PERCENTAGE, CUSTOM, SHARES
    val participantIds: String, // Comma separated IDs
    val notes: String = "",
    val receiptImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chores")
data class ChoreEntity(
    @PrimaryKey val id: String,
    val houseId: String,
    val title: String,
    val description: String,
    val assignedToId: String,
    val assignedToName: String,
    val createdById: String,
    val dueDate: Long,
    val priority: String, // LOW, MEDIUM, HIGH
    val difficulty: String, // EASY, MEDIUM, HARD, EPIC
    val xpReward: Int,
    val recurrence: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY
    val status: String, // PENDING, IN_PROGRESS, COMPLETED, VERIFIED, OVERDUE
    val completedAt: Long = 0L,
    val verifiedById: String = ""
)

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val id: String,
    val houseId: String,
    val payerId: String,
    val payerName: String,
    val payeeId: String,
    val payeeName: String,
    val amount: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isVerified: Boolean = true
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val xpReward: Int,
    val rarity: String, // COMMON, RARE, EPIC, LEGENDARY
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val progress: Int = 0,
    val totalRequired: Int = 1
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val houseId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String = "",
    val message: String,
    val activityType: String, // EXPENSE, CHORE, SETTLEMENT, LEVEL_UP, ACHIEVEMENT
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey val id: String,
    val houseId: String,
    val name: String,
    val quantity: String = "1",
    val category: String, // Groceries, Cleaning, Kitchen, Bathroom, Other
    val addedById: String,
    val addedByName: String,
    val assignedToId: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "polls")
data class PollEntity(
    @PrimaryKey val id: String,
    val houseId: String,
    val question: String,
    val createdById: String,
    val createdByName: String,
    val optionsJson: String, // JSON string of option text & votes
    val voterIdsJson: String, // JSON map of userId -> optionIndex
    val expiresAt: Long,
    val createdAt: Long = System.currentTimeMillis()
)

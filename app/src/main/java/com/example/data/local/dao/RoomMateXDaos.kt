package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("UPDATE users SET xp = :newXp, level = :newLevel WHERE id = :userId")
    suspend fun updateUserXpAndLevel(userId: String, newXp: Int, newLevel: Int)

    @Query("UPDATE users SET currentStreak = :current, longestStreak = :longest WHERE id = :userId")
    suspend fun updateUserStreak(userId: String, current: Int, longest: Int)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}

@Dao
interface HouseDao {
    @Query("SELECT * FROM houses WHERE id = :houseId")
    fun getHouseFlow(houseId: String): Flow<HouseEntity?>

    @Query("SELECT * FROM houses WHERE id = :houseId")
    suspend fun getHouseById(houseId: String): HouseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHouse(house: HouseEntity)

    @Query("SELECT * FROM house_members WHERE houseId = :houseId")
    fun getHouseMembersFlow(houseId: String): Flow<List<HouseMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<HouseMemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: HouseMemberEntity)

    @Query("DELETE FROM house_members WHERE houseId = :houseId AND userId = :userId")
    suspend fun removeMember(houseId: String, userId: String)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE houseId = :houseId ORDER BY createdAt DESC")
    fun getExpensesByHouseFlow(houseId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: String)
}

@Dao
interface ChoreDao {
    @Query("SELECT * FROM chores WHERE houseId = :houseId ORDER BY dueDate ASC")
    fun getChoresByHouseFlow(houseId: String): Flow<List<ChoreEntity>>

    @Query("SELECT * FROM chores WHERE houseId = :houseId AND status = 'PENDING' ORDER BY dueDate ASC")
    fun getPendingChoresFlow(houseId: String): Flow<List<ChoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChore(chore: ChoreEntity)

    @Query("UPDATE chores SET status = :status, completedAt = :completedAt WHERE id = :choreId")
    suspend fun updateChoreStatus(choreId: String, status: String, completedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM chores WHERE id = :choreId")
    suspend fun deleteChore(choreId: String)
}

@Dao
interface SettlementDao {
    @Query("SELECT * FROM settlements WHERE houseId = :houseId ORDER BY timestamp DESC")
    fun getSettlementsFlow(houseId: String): Flow<List<SettlementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY rarity DESC")
    fun getAllAchievementsFlow(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, unlockedAt: Long = System.currentTimeMillis())
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE houseId = :houseId ORDER BY timestamp DESC LIMIT 50")
    fun getActivitiesFlow(houseId: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)
}

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items WHERE houseId = :houseId ORDER BY createdAt DESC")
    fun getShoppingItemsFlow(houseId: String): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItemEntity)

    @Query("UPDATE shopping_items SET isCompleted = :isCompleted WHERE id = :itemId")
    suspend fun toggleShoppingItem(itemId: String, isCompleted: Boolean)

    @Query("DELETE FROM shopping_items WHERE id = :itemId")
    suspend fun deleteShoppingItem(itemId: String)
}

@Dao
interface PollDao {
    @Query("SELECT * FROM polls WHERE houseId = :houseId ORDER BY createdAt DESC")
    fun getPollsFlow(houseId: String): Flow<List<PollEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoll(poll: PollEntity)

    @Query("UPDATE polls SET voterIdsJson = :voterIdsJson WHERE id = :pollId")
    suspend fun updatePollVotes(pollId: String, voterIdsJson: String)

    @Query("DELETE FROM polls WHERE id = :pollId")
    suspend fun deletePoll(pollId: String)
}

package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        HouseEntity::class,
        HouseMemberEntity::class,
        ExpenseEntity::class,
        ChoreEntity::class,
        SettlementEntity::class,
        AchievementEntity::class,
        ActivityEntity::class,
        ShoppingItemEntity::class,
        PollEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RoomMateXDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun houseDao(): HouseDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun choreDao(): ChoreDao
    abstract fun settlementDao(): SettlementDao
    abstract fun achievementDao(): AchievementDao
    abstract fun activityDao(): ActivityDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun pollDao(): PollDao

    companion object {
        @Volatile
        private var INSTANCE: RoomMateXDatabase? = null

        fun getInstance(context: Context): RoomMateXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomMateXDatabase::class.java,
                    "roommatex_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

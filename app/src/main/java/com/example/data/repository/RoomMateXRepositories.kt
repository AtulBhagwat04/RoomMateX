package com.example.data.repository

import com.example.data.local.dao.*
import com.example.data.local.entities.*
import com.example.engine.GamificationEngine
import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class MainRepository(
    private val userDao: UserDao,
    private val houseDao: HouseDao,
    private val expenseDao: ExpenseDao,
    private val choreDao: ChoreDao,
    private val settlementDao: SettlementDao,
    private val achievementDao: AchievementDao,
    private val activityDao: ActivityDao,
    private val shoppingDao: ShoppingDao,
    private val pollDao: PollDao
) {

    // Seed initial mock data into database if empty
    suspend fun seedInitialDataIfEmpty() {
        val existingUser = userDao.getUserById("u_atul_1")
        if (existingUser == null) {
            // Seed User
            userDao.insertOrUpdateUser(
                UserEntity(
                    id = "u_atul_1",
                    name = "Atul Bhagwat",
                    email = "atul@example.com",
                    bio = "Keeping the apartment clean & organized! 🚀",
                    xp = 340,
                    level = 3,
                    currentStreak = 5,
                    longestStreak = 12,
                    currentHouseId = "house_sunrise_1"
                )
            )

            // Seed House
            houseDao.insertOrUpdateHouse(
                HouseEntity(
                    id = "house_sunrise_1",
                    name = "Sunrise Apartment",
                    ownerId = "u_atul_1",
                    inviteCode = "RMX-8921",
                    currency = "₹",
                    memberCount = 4
                )
            )

            // Seed Members
            val members = listOf(
                HouseMemberEntity("m1", "house_sunrise_1", "u_atul_1", "Atul Bhagwat", "OWNER"),
                HouseMemberEntity("m2", "house_sunrise_1", "u_rahul_2", "Rahul Sharma", "ADMIN"),
                HouseMemberEntity("m3", "house_sunrise_1", "u_aman_3", "Aman Verma", "MEMBER"),
                HouseMemberEntity("m4", "house_sunrise_1", "u_riya_4", "Riya Patel", "MEMBER")
            )
            houseDao.insertMembers(members)

            // Seed Expenses
            val expenses = listOf(
                ExpenseEntity("exp_1", "house_sunrise_1", "Apartment Monthly Rent", 24000.0, "Rent", "u_atul_1", "Atul Bhagwat", "EQUAL", "u_atul_1,u_rahul_2,u_aman_3,u_riya_4", "Paid via NetBanking", createdAt = System.currentTimeMillis() - 86400000 * 2),
                ExpenseEntity("exp_2", "house_sunrise_1", "Grocery Stockup & Veggies", 3200.0, "Groceries", "u_rahul_2", "Rahul Sharma", "EQUAL", "u_atul_1,u_rahul_2,u_aman_3,u_riya_4", "Supermarket receipt", createdAt = System.currentTimeMillis() - 86400000 * 1),
                ExpenseEntity("exp_3", "house_sunrise_1", "High-Speed Wi-Fi Bill", 1199.0, "Internet", "u_aman_3", "Aman Verma", "EQUAL", "u_atul_1,u_rahul_2,u_aman_3,u_riya_4", "Airtel Xstream", createdAt = System.currentTimeMillis() - 3600000 * 12),
                ExpenseEntity("exp_4", "house_sunrise_1", "Electricity & AC Bill", 2800.0, "Electricity", "u_riya_4", "Riya Patel", "EQUAL", "u_atul_1,u_rahul_2,u_aman_3,u_riya_4", "Torrent Power", createdAt = System.currentTimeMillis() - 3600000 * 4)
            )
            expenses.forEach { expenseDao.insertExpense(it) }

            // Seed Chores
            val chores = listOf(
                ChoreEntity("ch_1", "house_sunrise_1", "Deep Kitchen Cleaning", "Wipe down counters, clean stove & microwave", "u_atul_1", "Atul Bhagwat", "u_rahul_2", System.currentTimeMillis() + 86400000, "HIGH", "HARD", 50, "WEEKLY", "PENDING"),
                ChoreEntity("ch_2", "house_sunrise_1", "Wash & Sanitize Dishes", "Clear sink and stack dry rack", "u_rahul_2", "Rahul Sharma", "u_atul_1", System.currentTimeMillis() + 43200000, "MEDIUM", "EASY", 10, "DAILY", "PENDING"),
                ChoreEntity("ch_3", "house_sunrise_1", "Trash & Recycling Pickup", "Take out kitchen & balcony bins before morning", "u_aman_3", "Aman Verma", "u_atul_1", System.currentTimeMillis() + 21600000, "HIGH", "EASY", 10, "DAILY", "PENDING"),
                ChoreEntity("ch_4", "house_sunrise_1", "Bathroom Cleaning & Towels", "Scrub tiles and replace bath mats", "u_riya_4", "Riya Patel", "u_atul_1", System.currentTimeMillis() + 172800000, "MEDIUM", "MEDIUM", 25, "WEEKLY", "PENDING")
            )
            chores.forEach { choreDao.insertChore(it) }

            // Seed Settlements
            val settlements = listOf(
                SettlementEntity("set_1", "house_sunrise_1", "u_rahul_2", "Rahul Sharma", "u_atul_1", "Atul Bhagwat", 500.0, "Partial rent reimbursement", timestamp = System.currentTimeMillis() - 86400000)
            )
            settlements.forEach { settlementDao.insertSettlement(it) }

            // Seed Achievements
            val achievements = GamificationEngine.INITIAL_ACHIEVEMENTS.map {
                AchievementEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    iconName = it.iconName,
                    xpReward = it.xpReward,
                    rarity = it.rarity.name,
                    isUnlocked = it.isUnlocked,
                    progress = it.progress,
                    totalRequired = it.totalRequired
                )
            }
            achievementDao.insertAchievements(achievements)

            // Seed Activities
            val activities = listOf(
                ActivityEntity("act_1", "house_sunrise_1", "u_atul_1", "Atul Bhagwat", "", "Created house 'Sunrise Apartment'", "HOUSE", System.currentTimeMillis() - 86400000 * 3),
                ActivityEntity("act_2", "house_sunrise_1", "u_atul_1", "Atul Bhagwat", "", "Logged ₹24,000 Rent expense", "EXPENSE", System.currentTimeMillis() - 86400000 * 2),
                ActivityEntity("act_3", "house_sunrise_1", "u_rahul_2", "Rahul Sharma", "", "Settled ₹500 with Atul Bhagwat", "SETTLEMENT", System.currentTimeMillis() - 86400000 * 1),
                ActivityEntity("act_4", "house_sunrise_1", "u_atul_1", "Atul Bhagwat", "", "Reached Level 3 - Responsible Roommate! 🎉", "LEVEL_UP", System.currentTimeMillis() - 3600000 * 5)
            )
            activities.forEach { activityDao.insertActivity(it) }

            // Seed Shopping Items
            val shoppingItems = listOf(
                ShoppingItemEntity("shop_1", "house_sunrise_1", "Dishwashing Liquid & Sponge", "2 packs", "Cleaning", "u_atul_1", "Atul Bhagwat", "u_rahul_2", false),
                ShoppingItemEntity("shop_2", "house_sunrise_1", "Milk & Whole Wheat Bread", "2 Litres", "Groceries", "u_riya_4", "Riya Patel", "u_aman_3", false),
                ShoppingItemEntity("shop_3", "house_sunrise_1", "Garbage Bags (Large 30L)", "1 roll", "Cleaning", "u_rahul_2", "Rahul Sharma", "u_atul_1", true)
            )
            shoppingItems.forEach { shoppingDao.insertShoppingItem(it) }

            // Seed Poll
            pollDao.insertPoll(
                PollEntity(
                    id = "poll_1",
                    houseId = "house_sunrise_1",
                    question = "Which day should we schedule deep house cleaning this weekend?",
                    createdById = "u_atul_1",
                    createdByName = "Atul Bhagwat",
                    optionsJson = "[\"Saturday Morning 10 AM\",\"Sunday Evening 5 PM\",\"Friday Night 8 PM\"]",
                    voterIdsJson = "{\"u_atul_1\":0,\"u_rahul_2\":0,\"u_riya_4\":1}",
                    expiresAt = System.currentTimeMillis() + 86400000 * 2
                )
            )
        }
    }

    // User Flow
    fun getUserFlow(userId: String): Flow<User?> = userDao.getUserByIdFlow(userId).map { entity ->
        entity?.let {
            User(
                id = it.id,
                name = it.name,
                email = it.email,
                profileImage = it.profileImage,
                phone = it.phone,
                bio = it.bio,
                xp = it.xp,
                level = it.level,
                currentStreak = it.currentStreak,
                longestStreak = it.longestStreak,
                currentHouseId = it.currentHouseId
            )
        }
    }

    suspend fun awardXpToUser(userId: String, addedXp: Int): Pair<Int, Boolean> {
        val user = userDao.getUserById(userId) ?: return Pair(0, false)
        val newXp = user.xp + addedXp
        val oldLevel = user.level
        val newLevel = GamificationEngine.getLevelForXp(newXp).level
        val leveledUp = newLevel > oldLevel

        userDao.updateUserXpAndLevel(userId, newXp, newLevel)

        if (leveledUp) {
            val levelInfo = GamificationEngine.getLevelForXp(newXp)
            logActivity(user.currentHouseId, userId, user.name, "Reached Level $newLevel - ${levelInfo.title}! 🎉", "LEVEL_UP")
        }
        return Pair(newXp, leveledUp)
    }

    suspend fun updateProfile(userId: String, name: String, phone: String, bio: String) {
        val user = userDao.getUserById(userId) ?: return
        userDao.insertOrUpdateUser(user.copy(name = name, phone = phone, bio = bio))
    }

    // House Flow
    fun getHouseFlow(houseId: String): Flow<House?> = houseDao.getHouseFlow(houseId).map { entity ->
        entity?.let {
            House(
                id = it.id,
                name = it.name,
                ownerId = it.ownerId,
                inviteCode = it.inviteCode,
                currency = it.currency,
                timezone = it.timezone,
                memberCount = it.memberCount
            )
        }
    }

    fun getHouseMembersFlow(houseId: String): Flow<List<HouseMember>> = houseDao.getHouseMembersFlow(houseId).map { list ->
        list.map {
            HouseMember(
                id = it.id,
                houseId = it.houseId,
                userId = it.userId,
                name = it.name,
                role = HouseRole.valueOf(it.role),
                profileImage = it.profileImage
            )
        }
    }

    suspend fun createHouse(ownerId: String, ownerName: String, houseName: String, currency: String): String {
        val houseId = "house_" + System.currentTimeMillis()
        val inviteCode = "RMX-" + (1000..9999).random()
        val house = HouseEntity(
            id = houseId,
            name = houseName,
            ownerId = ownerId,
            inviteCode = inviteCode,
            currency = currency,
            memberCount = 1
        )
        houseDao.insertOrUpdateHouse(house)
        val member = HouseMemberEntity("m_" + System.currentTimeMillis(), houseId, ownerId, ownerName, "OWNER")
        houseDao.insertMember(member)

        // Update user's current house
        val user = userDao.getUserById(ownerId)
        if (user != null) {
            userDao.insertOrUpdateUser(user.copy(currentHouseId = houseId))
        }

        logActivity(houseId, ownerId, ownerName, "Created new house '$houseName'", "HOUSE")
        return houseId
    }

    suspend fun joinHouseByCode(userId: String, userName: String, inviteCode: String): Boolean {
        // Mock join logic - attach to house
        val currentHouse = houseDao.getHouseById("house_sunrise_1") ?: return false
        val member = HouseMemberEntity("m_" + System.currentTimeMillis(), currentHouse.id, userId, userName, "MEMBER")
        houseDao.insertMember(member)

        val user = userDao.getUserById(userId)
        if (user != null) {
            userDao.insertOrUpdateUser(user.copy(currentHouseId = currentHouse.id))
        }
        logActivity(currentHouse.id, userId, userName, "Joined the household!", "MEMBER")
        return true
    }

    // Expense Flow
    fun getExpensesFlow(houseId: String): Flow<List<Expense>> = expenseDao.getExpensesByHouseFlow(houseId).map { list ->
        list.map {
            Expense(
                id = it.id,
                houseId = it.houseId,
                title = it.title,
                amount = it.amount,
                category = it.category,
                paidById = it.paidById,
                paidByName = it.paidByName,
                splitType = SplitType.valueOf(it.splitType),
                participantIds = it.participantIds.split(",").filter { id -> id.isNotBlank() },
                notes = it.notes,
                receiptImage = it.receiptImage,
                createdAt = it.createdAt
            )
        }
    }

    suspend fun addExpense(expense: Expense) {
        val entity = ExpenseEntity(
            id = expense.id,
            houseId = expense.houseId,
            title = expense.title,
            amount = expense.amount,
            category = expense.category,
            paidById = expense.paidById,
            paidByName = expense.paidByName,
            splitType = expense.splitType.name,
            participantIds = expense.participantIds.joinToString(","),
            notes = expense.notes,
            receiptImage = expense.receiptImage,
            createdAt = expense.createdAt
        )
        expenseDao.insertExpense(entity)
        logActivity(expense.houseId, expense.paidById, expense.paidByName, "Added expense '${expense.title}' for ₹${expense.amount.toInt()}", "EXPENSE")
        awardXpToUser(expense.paidById, 20)
    }

    // Chore Flow
    fun getChoresFlow(houseId: String): Flow<List<Chore>> = choreDao.getChoresByHouseFlow(houseId).map { list ->
        list.map {
            Chore(
                id = it.id,
                houseId = it.houseId,
                title = it.title,
                description = it.description,
                assignedToId = it.assignedToId,
                assignedToName = it.assignedToName,
                createdById = it.createdById,
                dueDate = it.dueDate,
                priority = ChorePriority.valueOf(it.priority),
                difficulty = ChoreDifficulty.valueOf(it.difficulty),
                xpReward = it.xpReward,
                recurrence = ChoreRecurrence.valueOf(it.recurrence),
                status = ChoreStatus.valueOf(it.status),
                completedAt = it.completedAt,
                verifiedBy = it.verifiedById
            )
        }
    }

    suspend fun addChore(chore: Chore) {
        val entity = ChoreEntity(
            id = chore.id,
            houseId = chore.houseId,
            title = chore.title,
            description = chore.description,
            assignedToId = chore.assignedToId,
            assignedToName = chore.assignedToName,
            createdById = chore.createdById,
            dueDate = chore.dueDate,
            priority = chore.priority.name,
            difficulty = chore.difficulty.name,
            xpReward = chore.xpReward,
            recurrence = chore.recurrence.name,
            status = chore.status.name
        )
        choreDao.insertChore(entity)
        logActivity(chore.houseId, chore.createdById, "House Captain", "Assigned chore '${chore.title}' to ${chore.assignedToName}", "CHORE")
    }

    suspend fun completeChore(choreId: String, userId: String, userName: String): Pair<Int, Boolean> {
        choreDao.updateChoreStatus(choreId, ChoreStatus.COMPLETED.name)
        val chores = choreDao.getChoresByHouseFlow("house_sunrise_1").firstOrNull()
        val chore = chores?.find { it.id == choreId }
        val xpAward = chore?.xpReward ?: 25
        val (newXp, leveledUp) = awardXpToUser(userId, xpAward)
        logActivity("house_sunrise_1", userId, userName, "Completed chore '${chore?.title ?: "task"}' (+${xpAward} XP)", "CHORE")
        return Pair(xpAward, leveledUp)
    }

    // Settlement Flow
    fun getSettlementsFlow(houseId: String): Flow<List<Settlement>> = settlementDao.getSettlementsFlow(houseId).map { list ->
        list.map {
            Settlement(
                id = it.id,
                houseId = it.houseId,
                payerId = it.payerId,
                payerName = it.payerName,
                payeeId = it.payeeId,
                payeeName = it.payeeName,
                amount = it.amount,
                note = it.note,
                timestamp = it.timestamp
            )
        }
    }

    suspend fun recordSettlement(settlement: Settlement) {
        val entity = SettlementEntity(
            id = settlement.id,
            houseId = settlement.houseId,
            payerId = settlement.payerId,
            payerName = settlement.payerName,
            payeeId = settlement.payeeId,
            payeeName = settlement.payeeName,
            amount = settlement.amount,
            note = settlement.note,
            timestamp = settlement.timestamp
        )
        settlementDao.insertSettlement(entity)
        logActivity(settlement.houseId, settlement.payerId, settlement.payerName, "Settled ₹${settlement.amount.toInt()} with ${settlement.payeeName}", "SETTLEMENT")
        awardXpToUser(settlement.payerId, 30)
    }

    // Achievements & Activity
    fun getAchievementsFlow(): Flow<List<Achievement>> = achievementDao.getAllAchievementsFlow().map { list ->
        list.map {
            Achievement(
                id = it.id,
                name = it.name,
                description = it.description,
                iconName = it.iconName,
                xpReward = it.xpReward,
                rarity = AchievementRarity.valueOf(it.rarity),
                isUnlocked = it.isUnlocked,
                unlockedAt = it.unlockedAt,
                progress = it.progress,
                totalRequired = it.totalRequired
            )
        }
    }

    fun getActivitiesFlow(houseId: String): Flow<List<ActivityItem>> = activityDao.getActivitiesFlow(houseId).map { list ->
        list.map {
            ActivityItem(
                id = it.id,
                houseId = it.houseId,
                userId = it.userId,
                userName = it.userName,
                userAvatar = it.userAvatar,
                message = it.message,
                type = it.activityType,
                timestamp = it.timestamp
            )
        }
    }

    private suspend fun logActivity(houseId: String, userId: String, userName: String, message: String, type: String) {
        val entity = ActivityEntity(
            id = "act_" + System.currentTimeMillis(),
            houseId = houseId,
            userId = userId,
            userName = userName,
            message = message,
            activityType = type
        )
        activityDao.insertActivity(entity)
    }

    // Shopping List Flow
    fun getShoppingItemsFlow(houseId: String): Flow<List<ShoppingItem>> = shoppingDao.getShoppingItemsFlow(houseId).map { list ->
        list.map {
            ShoppingItem(
                id = it.id,
                houseId = it.houseId,
                name = it.name,
                quantity = it.quantity,
                category = it.category,
                addedById = it.addedById,
                addedByName = it.addedByName,
                assignedToId = it.assignedToId,
                isCompleted = it.isCompleted,
                createdAt = it.createdAt
            )
        }
    }

    suspend fun addShoppingItem(item: ShoppingItem) {
        val entity = ShoppingItemEntity(
            id = item.id,
            houseId = item.houseId,
            name = item.name,
            quantity = item.quantity,
            category = item.category,
            addedById = item.addedById,
            addedByName = item.addedByName,
            assignedToId = item.assignedToId,
            isCompleted = item.isCompleted
        )
        shoppingDao.insertShoppingItem(entity)
    }

    suspend fun toggleShoppingItem(itemId: String, isCompleted: Boolean) {
        shoppingDao.toggleShoppingItem(itemId, isCompleted)
    }

    suspend fun deleteShoppingItem(itemId: String) {
        shoppingDao.deleteShoppingItem(itemId)
    }

    // Polls Flow
    fun getPollsFlow(houseId: String): Flow<List<Poll>> = pollDao.getPollsFlow(houseId).map { list ->
        list.map { entity ->
            // Parse voters JSON
            val voterMap = mutableMapOf<String, Int>()
            if (entity.voterIdsJson.contains(":")) {
                entity.voterIdsJson.trim('{', '}').split(",").forEach { kv ->
                    val parts = kv.split(":")
                    if (parts.size == 2) {
                        val u = parts[0].trim('"', ' ')
                        val v = parts[1].trim('"', ' ').toIntOrNull()
                        if (u.isNotEmpty() && v != null) voterMap[u] = v
                    }
                }
            }

            // Parse options JSON
            val optStrings = entity.optionsJson.trim('[', ']').split("\",\"").map { it.trim('"', ' ') }
            val optionsWithVotes = optStrings.mapIndexed { idx, opt ->
                val count = voterMap.values.count { it == idx }
                PollOption(opt, count)
            }

            Poll(
                id = entity.id,
                houseId = entity.houseId,
                question = entity.question,
                createdById = entity.createdById,
                createdByName = entity.createdByName,
                options = optionsWithVotes,
                userVotes = voterMap,
                expiresAt = entity.expiresAt,
                createdAt = entity.createdAt
            )
        }
    }

    suspend fun voteOnPoll(pollId: String, userId: String, optionIndex: Int) {
        val polls = pollDao.getPollsFlow("house_sunrise_1").firstOrNull()
        val pollEntity = polls?.find { it.id == pollId } ?: return
        val currentMap = mutableMapOf<String, Int>()
        if (pollEntity.voterIdsJson.contains(":")) {
            pollEntity.voterIdsJson.trim('{', '}').split(",").forEach { kv ->
                val parts = kv.split(":")
                if (parts.size == 2) {
                    val u = parts[0].trim('"', ' ')
                    val v = parts[1].trim('"', ' ').toIntOrNull()
                    if (u.isNotEmpty() && v != null) currentMap[u] = v
                }
            }
        }
        currentMap[userId] = optionIndex

        val jsonMapStr = "{" + currentMap.entries.joinToString(",") { "\"${it.key}\":${it.value}" } + "}"
        pollDao.updatePollVotes(pollId, jsonMapStr)
    }

    suspend fun createPoll(poll: Poll) {
        val optionsJsonStr = "[" + poll.options.joinToString(",") { "\"${it.optionText}\"" } + "]"
        val entity = PollEntity(
            id = poll.id,
            houseId = poll.houseId,
            question = poll.question,
            createdById = poll.createdById,
            createdByName = poll.createdByName,
            optionsJson = optionsJsonStr,
            voterIdsJson = "{}",
            expiresAt = poll.expiresAt
        )
        pollDao.insertPoll(entity)
        logActivity(poll.houseId, poll.createdById, poll.createdByName, "Created new poll: '${poll.question}'", "POLL")
    }
}

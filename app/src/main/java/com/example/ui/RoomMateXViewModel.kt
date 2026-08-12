package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.RoomMateXDatabase
import com.example.data.repository.MainRepository
import com.example.engine.GamificationEngine
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoomMateXViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RoomMateXDatabase.getInstance(application)
    val repository = MainRepository(
        userDao = db.userDao(),
        houseDao = db.houseDao(),
        expenseDao = db.expenseDao(),
        choreDao = db.choreDao(),
        settlementDao = db.settlementDao(),
        achievementDao = db.achievementDao(),
        activityDao = db.activityDao(),
        shoppingDao = db.shoppingDao(),
        pollDao = db.pollDao()
    )

    // UI state feedback
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _levelUpDialog = MutableStateFlow<Pair<Int, String>?>(null) // (newLevel, title)
    val levelUpDialog: StateFlow<Pair<Int, String>?> = _levelUpDialog.asStateFlow()

    // Current logged in User ID
    val currentUserId = "u_atul_1"
    val currentHouseId = "house_sunrise_1"

    val user: StateFlow<User> = repository.getUserFlow(currentUserId)
        .map { it ?: User() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User())

    val house: StateFlow<House> = repository.getHouseFlow(currentHouseId)
        .map { it ?: House() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), House())

    val members: StateFlow<List<HouseMember>> = repository.getHouseMembersFlow(currentHouseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.getExpensesFlow(currentHouseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chores: StateFlow<List<Chore>> = repository.getChoresFlow(currentHouseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settlements: StateFlow<List<Settlement>> = repository.getSettlementsFlow(currentHouseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<Achievement>> = repository.getAchievementsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activities: StateFlow<List<ActivityItem>> = repository.getActivitiesFlow(currentHouseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItems: StateFlow<List<ShoppingItem>> = repository.getShoppingItemsFlow(currentHouseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val polls: StateFlow<List<Poll>> = repository.getPollsFlow(currentHouseId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived Balances and Optimized Settlements
    val balancesAndSettlements: StateFlow<Pair<List<NetBalance>, List<OptimizedSettlement>>> =
        combine(members, expenses, settlements) { mList, eList, sList ->
            GamificationEngine.calculateOptimizedSettlements(mList, eList, sList)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Pair(emptyList(), emptyList())
        )

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun dismissLevelUpDialog() {
        _levelUpDialog.value = null
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        notes: String = "",
        participantIds: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val currUser = user.value
            val currHouse = house.value
            val mList = members.value.map { it.userId }
            val participants = if (participantIds.isNotEmpty()) participantIds else mList

            val newExpense = Expense(
                id = "exp_" + System.currentTimeMillis(),
                houseId = currHouse.id,
                title = title,
                amount = amount,
                category = category,
                paidById = currUser.id,
                paidByName = currUser.name,
                splitType = SplitType.EQUAL,
                participantIds = participants,
                notes = notes
            )
            repository.addExpense(newExpense)
            _toastMessage.value = "Added expense '$title' (₹${amount.toInt()})! +20 XP"
        }
    }

    fun addChore(
        title: String,
        description: String,
        assignedToId: String,
        assignedToName: String,
        priority: ChorePriority,
        difficulty: ChoreDifficulty,
        recurrence: ChoreRecurrence
    ) {
        viewModelScope.launch {
            val currUser = user.value
            val currHouse = house.value
            val newChore = Chore(
                id = "ch_" + System.currentTimeMillis(),
                houseId = currHouse.id,
                title = title,
                description = description,
                assignedToId = assignedToId,
                assignedToName = assignedToName,
                createdById = currUser.id,
                dueDate = System.currentTimeMillis() + 86400000,
                priority = priority,
                difficulty = difficulty,
                recurrence = recurrence,
                status = ChoreStatus.PENDING
            )
            repository.addChore(newChore)
            _toastMessage.value = "Assigned chore '$title' to $assignedToName"
        }
    }

    fun completeChore(chore: Chore) {
        viewModelScope.launch {
            val currUser = user.value
            val (xpAward, leveledUp) = repository.completeChore(chore.id, currUser.id, currUser.name)
            _toastMessage.value = "Completed '${chore.title}'! +$xpAward XP"

            if (leveledUp) {
                val updatedUser = repository.getUserFlow(currUser.id).firstOrNull()
                val newLevel = updatedUser?.level ?: (currUser.level + 1)
                val info = GamificationEngine.getLevelForXp(updatedUser?.xp ?: 0)
                _levelUpDialog.value = Pair(newLevel, info.title)
            }
        }
    }

    fun recordSettlement(payeeId: String, payeeName: String, amount: Double, note: String) {
        viewModelScope.launch {
            val currUser = user.value
            val currHouse = house.value
            val newSettlement = Settlement(
                id = "set_" + System.currentTimeMillis(),
                houseId = currHouse.id,
                payerId = currUser.id,
                payerName = currUser.name,
                payeeId = payeeId,
                payeeName = payeeName,
                amount = amount,
                note = note
            )
            repository.recordSettlement(newSettlement)
            _toastMessage.value = "Recorded ₹${amount.toInt()} settlement with $payeeName! +30 XP"
        }
    }

    fun addShoppingItem(name: String, quantity: String, category: String) {
        viewModelScope.launch {
            val currUser = user.value
            val item = ShoppingItem(
                id = "shop_" + System.currentTimeMillis(),
                houseId = currentHouseId,
                name = name,
                quantity = quantity,
                category = category,
                addedById = currUser.id,
                addedByName = currUser.name
            )
            repository.addShoppingItem(item)
            _toastMessage.value = "Added '$name' to shopping list"
        }
    }

    fun toggleShoppingItem(itemId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleShoppingItem(itemId, isCompleted)
        }
    }

    fun deleteShoppingItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteShoppingItem(itemId)
        }
    }

    fun createPoll(question: String, options: List<String>) {
        viewModelScope.launch {
            val currUser = user.value
            val poll = Poll(
                id = "poll_" + System.currentTimeMillis(),
                houseId = currentHouseId,
                question = question,
                createdById = currUser.id,
                createdByName = currUser.name,
                options = options.map { PollOption(it, 0) },
                expiresAt = System.currentTimeMillis() + 86400000 * 2
            )
            repository.createPoll(poll)
            _toastMessage.value = "Poll created successfully!"
        }
    }

    fun votePoll(pollId: String, optionIndex: Int) {
        viewModelScope.launch {
            val currUser = user.value
            repository.voteOnPoll(pollId, currUser.id, optionIndex)
            _toastMessage.value = "Vote recorded!"
        }
    }

    fun createHouse(name: String, currency: String) {
        viewModelScope.launch {
            val currUser = user.value
            repository.createHouse(currUser.id, currUser.name, name, currency)
            _toastMessage.value = "House '$name' created successfully!"
        }
    }

    fun joinHouse(inviteCode: String) {
        viewModelScope.launch {
            val currUser = user.value
            val success = repository.joinHouseByCode(currUser.id, currUser.name, inviteCode)
            if (success) {
                _toastMessage.value = "Joined household successfully!"
            } else {
                _toastMessage.value = "Invalid invite code"
            }
        }
    }

    fun updateProfile(name: String, phone: String, bio: String) {
        viewModelScope.launch {
            repository.updateProfile(currentUserId, name, phone, bio)
            _toastMessage.value = "Profile updated!"
        }
    }
}

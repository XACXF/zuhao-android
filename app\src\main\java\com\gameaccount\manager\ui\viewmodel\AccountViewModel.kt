package com.gameaccount.manager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gameaccount.manager.data.local.AccountDatabase
import com.gameaccount.manager.data.local.entity.AccountEntity
import com.gameaccount.manager.data.local.entity.RecycleBinEntity
import com.gameaccount.manager.data.repository.AccountRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AccountUiState(
    val accounts: List<AccountEntity> = emptyList(),
    val filteredAccounts: List<AccountEntity> = emptyList(),
    val deletedAccounts: List<RecycleBinEntity> = emptyList(),
    val totalCount: Int = 0,
    val availableCount: Int = 0,
    val rentedCount: Int = 0,
    val maintenanceCount: Int = 0,
    val deletedCount: Int = 0,
    val selectedAccountForRent: AccountEntity? = null,
    val editingAccount: AccountEntity? = null,
    val showRentDialog: Boolean = false,
    val currentFilter: String = "all",
    val searchQuery: String = ""
)

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AccountDatabase.getDatabase(application)
    private val repository = AccountRepository(
        database.accountDao(),
        database.recycleBinDao()
    )

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // 收集账号列表
            repository.allAccounts.collect { accounts ->
                _uiState.update { state ->
                    state.copy(
                        accounts = accounts,
                        filteredAccounts = applyFilter(accounts, state.currentFilter, state.searchQuery)
                    )
                }
            }
        }

        viewModelScope.launch {
            // 收集统计信息
            repository.totalCount.collect { count ->
                _uiState.update { it.copy(totalCount = count) }
            }
        }

        viewModelScope.launch {
            repository.availableCount.collect { count ->
                _uiState.update { it.copy(availableCount = count) }
            }
        }

        viewModelScope.launch {
            repository.rentedCount.collect { count ->
                _uiState.update { it.copy(rentedCount = count) }
            }
        }

        viewModelScope.launch {
            repository.maintenanceCount.collect { count ->
                _uiState.update { it.copy(maintenanceCount = count) }
            }
        }

        viewModelScope.launch {
            // 收集回收站数据
            repository.allDeletedAccounts.collect { deleted ->
                _uiState.update { it.copy(deletedAccounts = deleted) }
            }
        }

        viewModelScope.launch {
            repository.deletedCount.collect { count ->
                _uiState.update { it.copy(deletedCount = count) }
            }
        }
    }

    private fun applyFilter(
        accounts: List<AccountEntity>,
        filter: String,
        query: String
    ): List<AccountEntity> {
        var result = accounts

        // 应用游戏筛选
        if (filter != "all") {
            result = result.filter { it.gameName == filter }
        }

        // 应用搜索
        if (query.isNotBlank()) {
            result = result.filter {
                it.gameName.contains(query, ignoreCase = true) ||
                it.server?.contains(query, ignoreCase = true) == true ||
                it.phone?.contains(query, ignoreCase = true) == true
            }
        }

        return result
    }

    fun setFilter(filter: String) {
        _uiState.update { state ->
            state.copy(
                currentFilter = filter,
                filteredAccounts = applyFilter(state.accounts, filter, state.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredAccounts = applyFilter(state.accounts, state.currentFilter, query)
            )
        }
    }

    fun addAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.addAccount(account)
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.updateAccount(account)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    fun selectAccountForRent(account: AccountEntity) {
        _uiState.update { it.copy(selectedAccountForRent = account, showRentDialog = true) }
    }

    fun hideRentDialog() {
        _uiState.update { it.copy(showRentDialog = false, selectedAccountForRent = null) }
    }

    fun rentAccount(
        accountId: Long,
        renterName: String?,
        renterContact: String?,
        duration: Int,
        unit: String,
        needHeartService: Boolean
    ) {
        viewModelScope.launch {
            repository.rentAccount(accountId, renterName, renterContact, duration, unit, needHeartService)
        }
    }

    fun returnAccount(accountId: Long) {
        viewModelScope.launch {
            repository.returnAccount(accountId)
        }
    }

    fun selectAccountForEdit(account: AccountEntity) {
        _uiState.update { it.copy(editingAccount = account) }
    }

    fun clearEditing() {
        _uiState.update { it.copy(editingAccount = null) }
    }

    fun restoreAccount(accountId: Long) {
        viewModelScope.launch {
            repository.restoreAccount(accountId)
        }
    }

    fun deletePermanently(accountId: Long) {
        viewModelScope.launch {
            repository.deletePermanently(accountId)
        }
    }

    fun clearRecycleBin() {
        viewModelScope.launch {
            repository.clearRecycleBin()
        }
    }
}

package com.gameaccount.manager.data.repository

import com.gameaccount.manager.data.local.dao.AccountDao
import com.gameaccount.manager.data.local.dao.RecycleBinDao
import com.gameaccount.manager.data.local.entity.AccountEntity
import com.gameaccount.manager.data.local.entity.RecycleBinEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

class AccountRepository(
    private val accountDao: AccountDao,
    private val recycleBinDao: RecycleBinDao
) {
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    val totalCount: Flow<Int> = accountDao.getTotalCount()
    val availableCount: Flow<Int> = accountDao.getAvailableCount()
    val rentedCount: Flow<Int> = accountDao.getRentedCount()
    val maintenanceCount: Flow<Int> = accountDao.getMaintenanceCount()
    val deletedCount: Flow<Int> = recycleBinDao.getDeletedCount()

    fun getAccountsByStatus(status: String): Flow<List<AccountEntity>> {
        return accountDao.getAccountsByStatus(status)
    }

    fun getAccountsByGame(gameName: String): Flow<List<AccountEntity>> {
        return accountDao.getAccountsByGame(gameName)
    }

    fun searchAccounts(query: String): Flow<List<AccountEntity>> {
        return accountDao.searchAccounts("%$query%")
    }

    suspend fun getAccountById(id: Long): AccountEntity? {
        return accountDao.getAccountById(id)
    }

    suspend fun addAccount(account: AccountEntity): Long {
        return accountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: AccountEntity) {
        accountDao.updateAccount(account)
    }

    suspend fun deleteAccount(account: AccountEntity) {
        // 移到回收站
        val deletedAccount = RecycleBinEntity(
            id = account.id,
            gameName = account.gameName,
            server = account.server,
            phone = account.phone,
            redCandles = account.redCandles,
            whiteCandles = account.whiteCandles,
            heartsCount = account.heartsCount,
            dailyPrice = account.dailyPrice,
            priceUnit = account.priceUnit,
            deposit = account.deposit,
            remarks = account.remarks,
            graduationProgress = account.graduationProgress,
            replicaItems = account.replicaItems,
            status = account.status,
            createdAt = account.createdAt,
            cardBg = account.cardBg,
            deletedAt = Date(),
            renterName = account.renterName,
            renterContact = account.renterContact,
            rentStartTime = account.rentStartTime,
            rentEndTime = account.rentEndTime,
            rentDuration = account.rentDuration,
            rentUnit = account.rentUnit,
            totalCost = account.totalCost,
            needHeartService = account.needHeartService,
            heartServiceStatus = account.heartServiceStatus
        )
        recycleBinDao.insertDeletedAccount(deletedAccount)
        accountDao.deleteAccount(account)
    }

    suspend fun rentAccount(
        accountId: Long,
        renterName: String?,
        renterContact: String?,
        duration: Int,
        unit: String,
        needHeartService: Boolean
    ) {
        val account = accountDao.getAccountById(accountId) ?: return
        
        val endTime = when (unit) {
            "hour" -> Date(System.currentTimeMillis() + duration * 60 * 60 * 1000)
            "day" -> Date(System.currentTimeMillis() + duration * 24 * 60 * 60 * 1000)
            "week" -> Date(System.currentTimeMillis() + duration * 7 * 24 * 60 * 60 * 1000)
            else -> Date()
        }

        val days = when (unit) {
            "hour" -> duration / 24.0
            "day" -> duration.toDouble()
            "week" -> duration * 7.0
            else -> duration.toDouble()
        }
        val totalCost = kotlin.math.ceil(days * account.dailyPrice)

        val updatedAccount = account.copy(
            status = "rented",
            renterName = renterName,
            renterContact = renterContact,
            rentStartTime = Date(),
            rentEndTime = endTime,
            rentDuration = duration,
            rentUnit = unit,
            totalCost = totalCost,
            needHeartService = needHeartService,
            heartServiceStatus = if (needHeartService) "pending" else null
        )
        accountDao.updateAccount(updatedAccount)
    }

    suspend fun returnAccount(accountId: Long) {
        val account = accountDao.getAccountById(accountId) ?: return
        val updatedAccount = account.copy(
            status = "available",
            renterName = null,
            renterContact = null,
            rentStartTime = null,
            rentEndTime = null,
            rentDuration = null,
            rentUnit = null,
            totalCost = null,
            needHeartService = false,
            heartServiceStatus = null
        )
        accountDao.updateAccount(updatedAccount)
    }

    // 回收站操作
    val allDeletedAccounts: Flow<List<RecycleBinEntity>> = recycleBinDao.getAllDeletedAccounts()

    suspend fun restoreAccount(accountId: Long) {
        val deletedAccount = recycleBinDao.getDeletedAccountById(accountId) ?: return
        
        val restoredAccount = AccountEntity(
            id = deletedAccount.id,
            gameName = deletedAccount.gameName,
            server = deletedAccount.server,
            phone = deletedAccount.phone,
            redCandles = deletedAccount.redCandles,
            whiteCandles = deletedAccount.whiteCandles,
            heartsCount = deletedAccount.heartsCount,
            dailyPrice = deletedAccount.dailyPrice,
            priceUnit = deletedAccount.priceUnit,
            deposit = deletedAccount.deposit,
            remarks = deletedAccount.remarks,
            graduationProgress = deletedAccount.graduationProgress,
            replicaItems = deletedAccount.replicaItems,
            status = deletedAccount.status,
            createdAt = deletedAccount.createdAt,
            cardBg = deletedAccount.cardBg,
            renterName = deletedAccount.renterName,
            renterContact = deletedAccount.renterContact,
            rentStartTime = deletedAccount.rentStartTime,
            rentEndTime = deletedAccount.rentEndTime,
            rentDuration = deletedAccount.rentDuration,
            rentUnit = deletedAccount.rentUnit,
            totalCost = deletedAccount.totalCost,
            needHeartService = deletedAccount.needHeartService,
            heartServiceStatus = deletedAccount.heartServiceStatus
        )
        accountDao.insertAccount(restoredAccount)
        recycleBinDao.deleteById(accountId)
    }

    suspend fun deletePermanently(accountId: Long) {
        recycleBinDao.deleteById(accountId)
    }

    suspend fun clearRecycleBin() {
        recycleBinDao.clearAll()
    }
}

package com.gameaccount.manager.data.local.dao

import androidx.room.*
import com.gameaccount.manager.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE status = :status")
    fun getAccountsByStatus(status: String): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE gameName = :gameName")
    fun getAccountsByGame(gameName: String): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Long): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity): Long

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccountById(id: Long)

    @Query("SELECT COUNT(*) FROM accounts")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM accounts WHERE status = 'available'")
    fun getAvailableCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM accounts WHERE status = 'rented'")
    fun getRentedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM accounts WHERE status = 'maintenance'")
    fun getMaintenanceCount(): Flow<Int>

    @Query("SELECT * FROM accounts WHERE gameName LIKE :query OR server LIKE :query OR phone LIKE :query")
    fun searchAccounts(query: String): Flow<List<AccountEntity>>
}

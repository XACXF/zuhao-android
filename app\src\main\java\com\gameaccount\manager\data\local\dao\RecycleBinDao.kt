package com.gameaccount.manager.data.local.dao

import androidx.room.*
import com.gameaccount.manager.data.local.entity.RecycleBinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecycleBinDao {
    @Query("SELECT * FROM recycle_bin ORDER BY deletedAt DESC")
    fun getAllDeletedAccounts(): Flow<List<RecycleBinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedAccount(account: RecycleBinEntity)

    @Delete
    suspend fun deletePermanently(account: RecycleBinEntity)

    @Query("DELETE FROM recycle_bin WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM recycle_bin")
    suspend fun clearAll()

    @Query("SELECT * FROM recycle_bin WHERE id = :id")
    suspend fun getDeletedAccountById(id: Long): RecycleBinEntity?

    @Query("SELECT COUNT(*) FROM recycle_bin")
    fun getDeletedCount(): Flow<Int>
}

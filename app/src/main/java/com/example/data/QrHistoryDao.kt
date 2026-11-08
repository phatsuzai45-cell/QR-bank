package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrHistoryDao {
    @Query("SELECT * FROM qr_history ORDER BY createdAt DESC")
    fun observeAllHistory(): Flow<List<QrHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQrHistory(item: QrHistoryEntity): Long

    @Query("DELETE FROM qr_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM qr_history")
    suspend fun clearAllHistory()
}

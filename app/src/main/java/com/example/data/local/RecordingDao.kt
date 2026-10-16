package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RecordingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {

    @Query("SELECT * FROM recordings ORDER BY timestamp DESC")
    fun getAllRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRecordings(limit: Int = 5): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE isStarred = 1 ORDER BY timestamp DESC")
    fun getStarredRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE callType = :type ORDER BY timestamp DESC")
    fun getRecordingsByType(type: String): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE title LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchRecordings(query: String): Flow<List<RecordingEntity>>

    @Query("SELECT COUNT(*) FROM recordings")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM recordings WHERE timestamp >= :startOfDay")
    fun getTodayCount(startOfDay: Long): Flow<Int>

    @Query("SELECT SUM(durationMs) FROM recordings")
    fun getTotalDuration(): Flow<Long?>

    @Query("SELECT SUM(fileSizeBytes) FROM recordings")
    fun getTotalSize(): Flow<Long?>

    @Query("SELECT * FROM recordings WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): RecordingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecordingEntity): Long

    @Update
    suspend fun update(entity: RecordingEntity)

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM recordings")
    suspend fun clearAll()
}

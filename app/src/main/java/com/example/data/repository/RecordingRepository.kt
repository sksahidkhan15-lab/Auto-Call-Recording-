package com.example.data.repository

import com.example.data.local.RecordingDao
import com.example.data.model.RecordingEntity
import com.example.util.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class RecordingRepository(private val dao: RecordingDao) {

    val allRecordings: Flow<List<RecordingEntity>> = dao.getAllRecordings()
    val recentRecordings: Flow<List<RecordingEntity>> = dao.getRecentRecordings(5)
    val starredRecordings: Flow<List<RecordingEntity>> = dao.getStarredRecordings()

    fun getRecordingsByType(type: String): Flow<List<RecordingEntity>> = dao.getRecordingsByType(type)
    fun searchRecordings(query: String): Flow<List<RecordingEntity>> = dao.searchRecordings(query)

    fun getTodayCount(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return dao.getTodayCount(calendar.timeInMillis)
    }

    val totalDuration: Flow<Long?> = dao.getTotalDuration()
    val totalSize: Flow<Long?> = dao.getTotalSize()

    suspend fun insertRecording(recording: RecordingEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(recording)
    }

    suspend fun updateRecording(recording: RecordingEntity) = withContext(Dispatchers.IO) {
        dao.update(recording)
    }

    suspend fun deleteRecording(recording: RecordingEntity): Boolean = withContext(Dispatchers.IO) {
        // Delete audio file from physical storage
        StorageHelper.deleteFile(recording.filePath)
        // Delete entry from Room database
        dao.deleteById(recording.id)
        true
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        dao.clearAll()
    }

    suspend fun exportMetadataJson(recordings: List<RecordingEntity>): String = withContext(Dispatchers.Default) {
        val jsonArray = JSONArray()
        for (item in recordings) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("filePath", item.filePath)
                put("phoneNumber", item.phoneNumber ?: "")
                put("contactName", item.contactName ?: "")
                put("callType", item.callType)
                put("timestamp", item.timestamp)
                put("durationMs", item.durationMs)
                put("fileSizeBytes", item.fileSizeBytes)
                put("format", item.format)
                put("isStarred", item.isStarred)
                put("note", item.note ?: "")
            }
            jsonArray.put(obj)
        }
        jsonArray.toString(2)
    }
}

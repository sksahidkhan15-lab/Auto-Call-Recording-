package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val filePath: String,
    val phoneNumber: String? = null,
    val contactName: String? = null,
    val callType: String = "MEMO", // INCOMING, OUTGOING, MEMO, IMPORTED
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L,
    val fileSizeBytes: Long = 0L,
    val format: String = "m4a",
    val isStarred: Boolean = false,
    val note: String? = null
)

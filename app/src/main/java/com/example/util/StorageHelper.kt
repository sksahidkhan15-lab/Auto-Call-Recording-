package com.example.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StorageHelper {

    private const val FOLDER_NAME = "AutoCallRecorder"

    /**
     * Resolves the primary directory for saving recordings according to Android Scoped Storage.
     * Prefers Public DIRECTORY_RECORDINGS/AutoCallRecorder when accessible,
     * with fallback to app-specific external files dir or internal files.
     */
    fun getRecordingsDirectory(context: Context): File {
        val recordingsDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RECORDINGS)
            File(publicDir, FOLDER_NAME)
        } else {
            val externalAppDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            File(externalAppDir ?: context.filesDir, FOLDER_NAME)
        }

        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs()
        }
        return recordingsDir
    }

    /**
     * Creates a new audio recording file with a structured naming scheme:
     * ACR_TYPE_PHONE_DATE.m4a
     */
    fun createAudioOutputFile(context: Context, callType: String, phoneNumber: String?): File {
        val dir = getRecordingsDirectory(context)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val cleanPhone = phoneNumber?.replace(Regex("[^0-9]"), "")?.takeLast(10) ?: "Unknown"
        val fileName = "ACR_${callType}_${cleanPhone}_$timeStamp.m4a"
        return File(dir, fileName)
    }

    /**
     * Formats bytes to readable size
     */
    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 KB"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
            else -> String.format(Locale.US, "%.0f KB", kb)
        }
    }

    /**
     * Formats duration milliseconds into MM:SS or HH:MM:SS
     */
    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }

    /**
     * Checks available storage space on device
     */
    fun getAvailableStorageBytes(context: Context): Long {
        return try {
            val path = getRecordingsDirectory(context)
            val stat = StatFs(path.path)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (e: Exception) {
            -1L
        }
    }

    /**
     * Obtains a secure content:// Uri via FileProvider for safe sharing
     */
    fun getContentUriForFile(context: Context, file: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    /**
     * Checks if file exists on disk
     */
    fun fileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }

    /**
     * Safely deletes file from disk
     */
    fun deleteFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) file.delete() else true
        } catch (e: Exception) {
            false
        }
    }
}

package com.example

import com.example.data.model.RecordingEntity
import com.example.util.StorageHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testFormatBytes() {
        assertEquals("0 KB", StorageHelper.formatBytes(0))
        assertEquals("500 KB", StorageHelper.formatBytes(500 * 1024))
        assertEquals("2.5 MB", StorageHelper.formatBytes((2.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun testFormatDuration() {
        assertEquals("00:00", StorageHelper.formatDuration(0))
        assertEquals("01:05", StorageHelper.formatDuration(65000))
        assertEquals("01:02:03", StorageHelper.formatDuration((3600 + 120 + 3) * 1000L))
    }

    @Test
    fun testRecordingEntity() {
        val entity = RecordingEntity(
            id = 1L,
            title = "Test Call",
            filePath = "/storage/emulated/0/Recordings/test.m4a",
            phoneNumber = "+1234567890",
            contactName = "John Doe",
            callType = "INCOMING",
            durationMs = 45000,
            fileSizeBytes = 120000
        )
        assertEquals("Test Call", entity.title)
        assertEquals("INCOMING", entity.callType)
        assertFalse(entity.isStarred)
        assertNotNull(entity.filePath)
    }
}

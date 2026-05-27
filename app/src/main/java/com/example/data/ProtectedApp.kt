package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "protected_apps")
data class ProtectedApp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val isApkWrapper: Boolean = false,
    val passwordType: String, // "PIN", "Alphanumeric", "Gesture Pattern", "Time-Based Match"
    val passwordValue: String,
    val lockDuration: Int = 0, // In minutes (0 = Always Lock / Immediate)
    val allowedSchedule: String = "Always", // "Always", "09:00 - 17:00", "22:00 - 06:00"
    val activeDays: String = "All Days", // "All Days", "Weekdays Only", "Weekends Only"
    val apkFileName: String? = null,
    val apkFileSize: String? = null,
    val wrappedApkPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

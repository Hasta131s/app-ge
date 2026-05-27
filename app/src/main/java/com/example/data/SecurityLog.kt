package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_logs")
data class SecurityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val attemptTime: Long = System.currentTimeMillis(),
    val isSuccess: Boolean,
    val enteredValue: String
)

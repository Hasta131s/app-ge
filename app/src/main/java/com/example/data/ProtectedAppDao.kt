package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtectedAppDao {
    @Query("SELECT * FROM protected_apps ORDER BY createdAt DESC")
    fun getAllProtectedApps(): Flow<List<ProtectedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtectedApp(app: ProtectedApp)

    @Delete
    suspend fun deleteProtectedApp(app: ProtectedApp)

    @Query("SELECT * FROM protected_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getProtectedAppByPackage(packageName: String): ProtectedApp?

    @Query("SELECT * FROM security_logs ORDER BY attemptTime DESC LIMIT 100")
    fun getSecurityLogs(): Flow<List<SecurityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityLog(log: SecurityLog)

    @Query("DELETE FROM security_logs")
    suspend fun clearLogs()
}

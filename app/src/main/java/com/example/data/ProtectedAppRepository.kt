package com.example.data

import kotlinx.coroutines.flow.Flow

class ProtectedAppRepository(private val dao: ProtectedAppDao) {
    val allProtectedApps: Flow<List<ProtectedApp>> = dao.getAllProtectedApps()
    val securityLogs: Flow<List<SecurityLog>> = dao.getSecurityLogs()

    suspend fun insertProtectedApp(app: ProtectedApp) {
        dao.insertProtectedApp(app)
    }

    suspend fun deleteProtectedApp(app: ProtectedApp) {
        dao.deleteProtectedApp(app)
    }

    suspend fun getProtectedAppByPackage(packageName: String): ProtectedApp? {
        return dao.getProtectedAppByPackage(packageName)
    }

    suspend fun insertSecurityLog(log: SecurityLog) {
        dao.insertSecurityLog(log)
    }

    suspend fun clearLogs() {
        dao.clearLogs()
    }
}

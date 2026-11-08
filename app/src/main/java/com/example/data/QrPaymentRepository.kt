package com.example.data

import kotlinx.coroutines.flow.Flow

class QrPaymentRepository(
    private val qrHistoryDao: QrHistoryDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    val allHistory: Flow<List<QrHistoryEntity>> = qrHistoryDao.observeAllHistory()
    val bankAccountFlow: Flow<BankAccountConfig> = userPreferencesRepository.bankAccountFlow
    val appSettingsFlow: Flow<AppSettingsConfig> = userPreferencesRepository.appSettingsFlow

    suspend fun insertHistory(entity: QrHistoryEntity): Long {
        return qrHistoryDao.insertQrHistory(entity)
    }

    suspend fun deleteHistoryById(id: Long) {
        qrHistoryDao.deleteById(id)
    }

    suspend fun clearAllHistory() {
        qrHistoryDao.clearAllHistory()
    }

    suspend fun saveBankAccount(config: BankAccountConfig) {
        userPreferencesRepository.saveBankAccount(config)
    }

    suspend fun updateAppSettings(
        keepScreenOn: Boolean? = null,
        autoMaxBrightness: Boolean? = null,
        hapticFeedback: Boolean? = null,
        autoSaveHistory: Boolean? = null,
        darkTheme: Boolean? = null
    ) {
        userPreferencesRepository.updateAppSettings(
            keepScreenOn = keepScreenOn,
            autoMaxBrightness = autoMaxBrightness,
            hapticFeedback = hapticFeedback,
            autoSaveHistory = autoSaveHistory,
            darkTheme = darkTheme
        )
    }
}

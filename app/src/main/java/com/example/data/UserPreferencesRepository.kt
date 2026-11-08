package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.VietnameseBanks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smart_qr_payment_prefs")

data class BankAccountConfig(
    val bankBin: String,
    val bankShortName: String,
    val bankFullName: String,
    val accountNumber: String,
    val accountHolder: String,
    val defaultTransferMessage: String
) {
    val isConfigured: Boolean
        get() = bankBin.length == 6 && accountNumber.trim().length >= 4 && accountHolder.trim().isNotEmpty()
}

data class AppSettingsConfig(
    val keepScreenOnDuringQr: Boolean = true,
    val autoMaxBrightnessInFullscreen: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val autoSaveQrHistory: Boolean = true,
    val darkThemeEnabled: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val BANK_BIN = stringPreferencesKey("bank_bin")
        val BANK_SHORT_NAME = stringPreferencesKey("bank_short_name")
        val BANK_FULL_NAME = stringPreferencesKey("bank_full_name")
        val ACCOUNT_NUMBER = stringPreferencesKey("account_number")
        val ACCOUNT_HOLDER = stringPreferencesKey("account_holder")
        val DEFAULT_TRANSFER_MESSAGE = stringPreferencesKey("default_transfer_message")

        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val AUTO_MAX_BRIGHTNESS = booleanPreferencesKey("auto_max_brightness")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val AUTO_SAVE_HISTORY = booleanPreferencesKey("auto_save_history")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val bankAccountFlow: Flow<BankAccountConfig> = context.dataStore.data.map { prefs ->
        val defaultBank = VietnameseBanks.supportedBanks.first() // Vietcombank 970436
        val savedBin = prefs[Keys.BANK_BIN] ?: defaultBank.bin
        val matchedBank = VietnameseBanks.findByBin(savedBin) ?: defaultBank

        BankAccountConfig(
            bankBin = matchedBank.bin,
            bankShortName = prefs[Keys.BANK_SHORT_NAME] ?: matchedBank.shortName,
            bankFullName = prefs[Keys.BANK_FULL_NAME] ?: matchedBank.fullName,
            accountNumber = prefs[Keys.ACCOUNT_NUMBER] ?: "1029384756",
            accountHolder = prefs[Keys.ACCOUNT_HOLDER] ?: "NGUYEN VAN AN",
            defaultTransferMessage = prefs[Keys.DEFAULT_TRANSFER_MESSAGE] ?: "THANH TOAN DON HANG"
        )
    }

    val appSettingsFlow: Flow<AppSettingsConfig> = context.dataStore.data.map { prefs ->
        AppSettingsConfig(
            keepScreenOnDuringQr = prefs[Keys.KEEP_SCREEN_ON] ?: true,
            autoMaxBrightnessInFullscreen = prefs[Keys.AUTO_MAX_BRIGHTNESS] ?: true,
            hapticFeedbackEnabled = prefs[Keys.HAPTIC_FEEDBACK] ?: true,
            autoSaveQrHistory = prefs[Keys.AUTO_SAVE_HISTORY] ?: true,
            darkThemeEnabled = prefs[Keys.DARK_THEME] ?: false
        )
    }

    suspend fun saveBankAccount(config: BankAccountConfig) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BANK_BIN] = config.bankBin.trim()
            prefs[Keys.BANK_SHORT_NAME] = config.bankShortName.trim()
            prefs[Keys.BANK_FULL_NAME] = config.bankFullName.trim()
            prefs[Keys.ACCOUNT_NUMBER] = config.accountNumber.trim()
            prefs[Keys.ACCOUNT_HOLDER] = config.accountHolder.trim()
            prefs[Keys.DEFAULT_TRANSFER_MESSAGE] = config.defaultTransferMessage.trim()
        }
    }

    suspend fun updateAppSettings(
        keepScreenOn: Boolean? = null,
        autoMaxBrightness: Boolean? = null,
        hapticFeedback: Boolean? = null,
        autoSaveHistory: Boolean? = null,
        darkTheme: Boolean? = null
    ) {
        context.dataStore.edit { prefs ->
            keepScreenOn?.let { prefs[Keys.KEEP_SCREEN_ON] = it }
            autoMaxBrightness?.let { prefs[Keys.AUTO_MAX_BRIGHTNESS] = it }
            hapticFeedback?.let { prefs[Keys.HAPTIC_FEEDBACK] = it }
            autoSaveHistory?.let { prefs[Keys.AUTO_SAVE_HISTORY] = it }
            darkTheme?.let { prefs[Keys.DARK_THEME] = it }
        }
    }
}

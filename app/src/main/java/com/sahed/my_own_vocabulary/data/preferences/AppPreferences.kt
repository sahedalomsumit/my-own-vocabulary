package com.sahed.my_own_vocabulary.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.vocabDataStore: DataStore<Preferences> by preferencesDataStore(name = "vocab_settings")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class QuizFormat {
    MULTIPLE_CHOICE,
    FLASHCARD,
    SPELLING
}

class AppPreferences(private val context: Context) {
    private val authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() {
            if (authPrefs.contains("is_logged_in")) {
                return authPrefs.getBoolean("is_logged_in", false)
            }
            val hasFirebaseUser = try {
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null
            } catch (e: Exception) {
                false
            }
            if (hasFirebaseUser) {
                authPrefs.edit().putBoolean("is_logged_in", true).apply()
                return true
            }
            return false
        }
        set(value) {
            authPrefs.edit().putBoolean("is_logged_in", value).apply()
        }

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_mode")
        private val SPEECH_RATE_KEY = floatPreferencesKey("speech_rate")
        private val SPEECH_PITCH_KEY = floatPreferencesKey("speech_pitch")
        private val AUTO_PRONOUNCE_SAVE_KEY = booleanPreferencesKey("auto_pronounce_on_save")
        private val AUTO_PRONOUNCE_QUIZ_KEY = booleanPreferencesKey("auto_pronounce_in_quiz")
        private val ACTIVE_LANGUAGE_FILTER_KEY = stringPreferencesKey("active_language_filter")
        private val LAST_SYNC_TIMESTAMP_KEY = stringPreferencesKey("last_sync_timestamp")
        private val DEFAULT_SOURCE_LANG_KEY = stringPreferencesKey("default_source_lang")
        private val DEFAULT_TARGET_LANG_KEY = stringPreferencesKey("default_target_lang")
        private val LAST_USED_MAIN_FOLDER_ID_KEY = stringPreferencesKey("last_used_main_folder_id")
        private val LAST_USED_SUB_FOLDER_ID_KEY = stringPreferencesKey("last_used_sub_folder_id")
        private val LAST_USED_SUB_SUB_FOLDER_ID_KEY = stringPreferencesKey("last_used_sub_sub_folder_id")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        const val NONE_MARKER = "__NONE__"
    }

    val lastUsedMainFolderId: Flow<String?> = context.vocabDataStore.data.map { preferences ->
        preferences[LAST_USED_MAIN_FOLDER_ID_KEY]
    }

    val lastUsedSubFolderId: Flow<String?> = context.vocabDataStore.data.map { preferences ->
        preferences[LAST_USED_SUB_FOLDER_ID_KEY]
    }

    val lastUsedSubSubFolderId: Flow<String?> = context.vocabDataStore.data.map { preferences ->
        preferences[LAST_USED_SUB_SUB_FOLDER_ID_KEY]
    }

    fun getLastUsedSubFolderForMain(mainFolderId: String): Flow<String?> =
        context.vocabDataStore.data.map { preferences ->
            preferences[stringPreferencesKey("last_used_sub_for_main_$mainFolderId")]
        }

    fun getLastUsedSubSubFolderForSub(subFolderId: String): Flow<String?> =
        context.vocabDataStore.data.map { preferences ->
            preferences[stringPreferencesKey("last_used_sub_sub_for_sub_$subFolderId")]
        }

    val defaultSourceLang: Flow<String> = context.vocabDataStore.data.map { preferences ->
        preferences[DEFAULT_SOURCE_LANG_KEY] ?: "de"
    }

    val defaultTargetLang: Flow<String> = context.vocabDataStore.data.map { preferences ->
        preferences[DEFAULT_TARGET_LANG_KEY] ?: "en"
    }

    val themeMode: Flow<ThemeMode> = context.vocabDataStore.data.map { preferences ->
        when (preferences[THEME_KEY]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            ThemeMode.SYSTEM.name -> ThemeMode.SYSTEM
            else -> ThemeMode.DARK // Default to dark mode luxury aesthetic
        }
    }

    val speechRate: Flow<Float> = context.vocabDataStore.data.map { preferences ->
        preferences[SPEECH_RATE_KEY] ?: 1.0f
    }

    val speechPitch: Flow<Float> = context.vocabDataStore.data.map { preferences ->
        preferences[SPEECH_PITCH_KEY] ?: 1.0f
    }

    val autoPronounceOnSave: Flow<Boolean> = context.vocabDataStore.data.map { preferences ->
        preferences[AUTO_PRONOUNCE_SAVE_KEY] ?: true
    }

    val autoPronounceInQuiz: Flow<Boolean> = context.vocabDataStore.data.map { preferences ->
        preferences[AUTO_PRONOUNCE_QUIZ_KEY] ?: true
    }

    val activeLanguageFilter: Flow<String?> = context.vocabDataStore.data.map { preferences ->
        preferences[ACTIVE_LANGUAGE_FILTER_KEY]
    }

    val lastSyncTimestamp: Flow<String?> = context.vocabDataStore.data.map { preferences ->
        preferences[LAST_SYNC_TIMESTAMP_KEY]
    }

    val notificationsEnabled: Flow<Boolean> = context.vocabDataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.vocabDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.vocabDataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }

    suspend fun setSpeechRate(rate: Float) {
        context.vocabDataStore.edit { preferences ->
            preferences[SPEECH_RATE_KEY] = rate
        }
    }

    suspend fun setSpeechPitch(pitch: Float) {
        context.vocabDataStore.edit { preferences ->
            preferences[SPEECH_PITCH_KEY] = pitch
        }
    }

    suspend fun setAutoPronounceOnSave(enabled: Boolean) {
        context.vocabDataStore.edit { preferences ->
            preferences[AUTO_PRONOUNCE_SAVE_KEY] = enabled
        }
    }

    suspend fun setAutoPronounceInQuiz(enabled: Boolean) {
        context.vocabDataStore.edit { preferences ->
            preferences[AUTO_PRONOUNCE_QUIZ_KEY] = enabled
        }
    }

    suspend fun setActiveLanguageFilter(languageCode: String?) {
        context.vocabDataStore.edit { preferences ->
            if (languageCode != null) {
                preferences[ACTIVE_LANGUAGE_FILTER_KEY] = languageCode
            } else {
                preferences.remove(ACTIVE_LANGUAGE_FILTER_KEY)
            }
        }
    }

    suspend fun setLastSyncTimestamp(timestamp: String) {
        context.vocabDataStore.edit { preferences ->
            preferences[LAST_SYNC_TIMESTAMP_KEY] = timestamp
        }
    }

    suspend fun setDefaultLanguages(source: String, target: String) {
        context.vocabDataStore.edit { preferences ->
            preferences[DEFAULT_SOURCE_LANG_KEY] = source.trim().lowercase()
            preferences[DEFAULT_TARGET_LANG_KEY] = target.trim().lowercase()
        }
    }

    suspend fun setLastUsedMainFolderId(id: String?) {
        context.vocabDataStore.edit { preferences ->
            if (id != null) preferences[LAST_USED_MAIN_FOLDER_ID_KEY] = id
            else preferences.remove(LAST_USED_MAIN_FOLDER_ID_KEY)
        }
    }

    suspend fun setLastUsedSubFolderId(id: String?) {
        context.vocabDataStore.edit { preferences ->
            if (id != null) preferences[LAST_USED_SUB_FOLDER_ID_KEY] = id
            else preferences.remove(LAST_USED_SUB_FOLDER_ID_KEY)
        }
    }

    suspend fun setLastUsedSubSubFolderId(id: String?) {
        context.vocabDataStore.edit { preferences ->
            if (id != null) preferences[LAST_USED_SUB_SUB_FOLDER_ID_KEY] = id
            else preferences.remove(LAST_USED_SUB_SUB_FOLDER_ID_KEY)
        }
    }

    suspend fun setLastUsedSubFolderForMain(mainFolderId: String, subFolderId: String?) {
        context.vocabDataStore.edit { preferences ->
            val key = stringPreferencesKey("last_used_sub_for_main_$mainFolderId")
            if (subFolderId != null) {
                preferences[key] = subFolderId
            } else {
                preferences[key] = NONE_MARKER
            }
        }
    }

    suspend fun setLastUsedSubSubFolderForSub(subFolderId: String, subSubFolderId: String?) {
        context.vocabDataStore.edit { preferences ->
            val key = stringPreferencesKey("last_used_sub_sub_for_sub_$subFolderId")
            if (subSubFolderId != null) {
                preferences[key] = subSubFolderId
            } else {
                preferences[key] = NONE_MARKER
            }
        }
    }

    suspend fun setLastUsedFolders(mainId: String?, subId: String?, subSubId: String?) {
        context.vocabDataStore.edit { preferences ->
            if (mainId != null) preferences[LAST_USED_MAIN_FOLDER_ID_KEY] = mainId
            else preferences.remove(LAST_USED_MAIN_FOLDER_ID_KEY)

            if (subId != null) preferences[LAST_USED_SUB_FOLDER_ID_KEY] = subId
            else preferences.remove(LAST_USED_SUB_FOLDER_ID_KEY)

            if (subSubId != null) preferences[LAST_USED_SUB_SUB_FOLDER_ID_KEY] = subSubId
            else preferences.remove(LAST_USED_SUB_SUB_FOLDER_ID_KEY)
        }
    }
}

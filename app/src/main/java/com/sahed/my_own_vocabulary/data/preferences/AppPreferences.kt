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
}

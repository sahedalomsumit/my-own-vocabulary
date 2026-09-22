package com.sahed.my_own_vocabulary.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import com.sahed.my_own_vocabulary.data.preferences.ThemeMode
import com.sahed.my_own_vocabulary.data.repository.AuthRepository
import com.sahed.my_own_vocabulary.data.repository.SyncRepository
import com.sahed.my_own_vocabulary.data.repository.VocabularyRepository
import com.sahed.my_own_vocabulary.util.JsonBackupHelper
import com.sahed.my_own_vocabulary.util.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = VocabularyRepository(
        database.mainFolderDao(),
        database.subFolderDao(),
        database.subSubFolderDao(),
        database.vocabularyEntryDao()
    )
    val preferences = AppPreferences(application)
    private val syncRepository = SyncRepository(repository, preferences)
    val authRepository = AuthRepository(application)
    val ttsManager = TtsManager(application)

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser

    val defaultSourceLang: StateFlow<String> = preferences.defaultSourceLang
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "de")

    val defaultTargetLang: StateFlow<String> = preferences.defaultTargetLang
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val themeMode: StateFlow<ThemeMode> = preferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DARK)

    val speechRate: StateFlow<Float> = preferences.speechRate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val speechPitch: StateFlow<Float> = preferences.speechPitch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val autoPronounceOnSave: StateFlow<Boolean> = preferences.autoPronounceOnSave
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val autoPronounceInQuiz: StateFlow<Boolean> = preferences.autoPronounceInQuiz
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val lastSyncTimestamp: StateFlow<String?> = preferences.lastSyncTimestamp
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    fun setDefaultLanguages(source: String, target: String) {
        viewModelScope.launch {
            preferences.setDefaultLanguages(source, target)
        }
    }

    fun setAutoPronounceAll(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAutoPronounceOnSave(enabled)
            preferences.setAutoPronounceInQuiz(enabled)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            preferences.setSpeechRate(rate)
        }
    }

    fun setSpeechPitch(pitch: Float) {
        viewModelScope.launch {
            preferences.setSpeechPitch(pitch)
        }
    }

    fun setAutoPronounceOnSave(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAutoPronounceOnSave(enabled)
        }
    }

    fun setAutoPronounceInQuiz(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAutoPronounceInQuiz(enabled)
        }
    }

    fun testPronunciation() {
        ttsManager.speak(
            text = "Hallo! Willkommen bei My Own Vocabulary.",
            languageCode = "de",
            speechRate = speechRate.value,
            pitch = speechPitch.value
        )
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = syncRepository.syncWithCloud()
            _isSyncing.value = false
            if (result.isSuccess) {
                _syncMessage.value = "Synced successfully at ${result.getOrNull()}"
            } else {
                _syncMessage.value = result.exceptionOrNull()?.message ?: "Sync failed"
            }
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    suspend fun exportData(): String {
        val entries = repository.getAllEntriesSnapshot()
        return JsonBackupHelper.exportToJson(entries)
    }

    suspend fun importData(jsonString: String): Int {
        val entries = JsonBackupHelper.importFromJson(jsonString)
        if (entries.isNotEmpty()) {
            repository.insertAllEntries(entries)
        }
        return entries.size
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun clearAllVocabulary() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}

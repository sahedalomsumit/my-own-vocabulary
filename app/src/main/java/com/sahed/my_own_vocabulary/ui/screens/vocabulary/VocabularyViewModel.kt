package com.sahed.my_own_vocabulary.ui.screens.vocabulary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import com.sahed.my_own_vocabulary.data.repository.VocabularyRepository
import com.sahed.my_own_vocabulary.util.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import com.sahed.my_own_vocabulary.data.local.entity.getPronunciationText
import com.sahed.my_own_vocabulary.data.repository.SyncRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class VocabularyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = VocabularyRepository(
        database.mainFolderDao(),
        database.subFolderDao(),
        database.subSubFolderDao(),
        database.vocabularyEntryDao()
    )
    private val preferences = AppPreferences(application)
    val ttsManager = TtsManager(application)

    init {
        // Auto-sync with Firestore in background on startup
        SyncRepository.getInstance(application).autoSync()
    }


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMainFolderId = MutableStateFlow<String?>(null)
    val selectedMainFolderId: StateFlow<String?> = _selectedMainFolderId.asStateFlow()

    private val _selectedSubFolderId = MutableStateFlow<String?>(null)
    val selectedSubFolderId: StateFlow<String?> = _selectedSubFolderId.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    val mainFolders: StateFlow<List<MainFolderEntity>> = repository.allMainFolders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subFoldersForSelectedMain: StateFlow<List<SubFolderEntity>> = _selectedMainFolderId
        .flatMapLatest { mainId ->
            if (mainId != null) repository.getSubFoldersForMain(mainId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWordsCount: StateFlow<Int> = repository.totalWordsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val masteredWordsCount: StateFlow<Int> = repository.masteredWordsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val streakDays: StateFlow<Int> = repository.streakDaysFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredEntries: StateFlow<List<VocabularyEntryEntity>> = combine(
        repository.allEntries,
        _searchQuery,
        _selectedMainFolderId,
        _selectedSubFolderId,
        _showFavoritesOnly
    ) { entries, query, mainId, subId, favsOnly ->
        entries.filter { entry ->
            val matchesQuery = if (query.isBlank()) true else {
                entry.originalWord.contains(query, ignoreCase = true) ||
                        entry.translatedWord.contains(query, ignoreCase = true) ||
                        (entry.exampleSentence?.contains(query, ignoreCase = true) == true) ||
                        (entry.notes?.contains(query, ignoreCase = true) == true)
            }
            val matchesMain = mainId == null || entry.mainFolderId == mainId
            val matchesSub = subId == null || entry.subFolderId == subId
            val matchesFav = !favsOnly || entry.isFavorite

            matchesQuery && matchesMain && matchesSub && matchesFav
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectMainFolder(mainFolderId: String?) {
        if (_selectedMainFolderId.value == mainFolderId) {
            _selectedMainFolderId.value = null
        } else {
            _selectedMainFolderId.value = mainFolderId
        }
        _selectedSubFolderId.value = null // reset sub-folder selection on main change
    }

    fun selectSubFolder(subFolderId: String?) {
        if (_selectedSubFolderId.value == subFolderId) {
            _selectedSubFolderId.value = null
        } else {
            _selectedSubFolderId.value = subFolderId
        }
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(entry: VocabularyEntryEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entry.id, !entry.isFavorite)
        }
    }

    fun deleteEntry(entry: VocabularyEntryEntity) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun speakWord(entry: VocabularyEntryEntity) {
        viewModelScope.launch {
            val rate = preferences.speechRate.first()
            val pitch = preferences.speechPitch.first()
            ttsManager.speak(
                text = entry.getPronunciationText(),
                languageCode = entry.sourceLanguage,
                speechRate = rate,
                pitch = pitch
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}

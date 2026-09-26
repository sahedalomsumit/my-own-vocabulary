package com.sahed.my_own_vocabulary.ui.screens.entry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubSubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.data.local.entity.getPronunciationText
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import com.sahed.my_own_vocabulary.data.repository.VocabularyRepository
import com.sahed.my_own_vocabulary.util.TtsManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AddVocabularyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = VocabularyRepository(
        database.mainFolderDao(),
        database.subFolderDao(),
        database.subSubFolderDao(),
        database.vocabularyEntryDao()
    )
    private val preferences = AppPreferences(application)
    val ttsManager = TtsManager(application)

    val mainFolders: StateFlow<List<MainFolderEntity>> = repository.allMainFolders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedMainFolder = MutableStateFlow<MainFolderEntity?>(null)
    val selectedMainFolder: StateFlow<MainFolderEntity?> = _selectedMainFolder.asStateFlow()

    val subFoldersForSelectedMain: StateFlow<List<SubFolderEntity>> = _selectedMainFolder
        .flatMapLatest { folder ->
            if (folder != null) repository.getSubFoldersForMain(folder.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSubFolder = MutableStateFlow<SubFolderEntity?>(null)
    val selectedSubFolder: StateFlow<SubFolderEntity?> = _selectedSubFolder.asStateFlow()

    val subSubFoldersForSelectedSub: StateFlow<List<SubSubFolderEntity>> = _selectedSubFolder
        .flatMapLatest { sub ->
            if (sub != null) repository.getSubSubFoldersForSub(sub.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSubSubFolder = MutableStateFlow<SubSubFolderEntity?>(null)
    val selectedSubSubFolder: StateFlow<SubSubFolderEntity?> = _selectedSubSubFolder.asStateFlow()

    // Form inputs
    val originalWord = MutableStateFlow("")
    val translatedWord = MutableStateFlow("")
    val articleOrGender = MutableStateFlow<String?>(null)
    val exampleSentence = MutableStateFlow("")
    val notes = MutableStateFlow("")

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()
    private var editingEntry: VocabularyEntryEntity? = null

    private val _saveSuccessEvent = MutableSharedFlow<Unit>()
    val saveSuccessEvent: SharedFlow<Unit> = _saveSuccessEvent.asSharedFlow()

    init {
        // Automatically restore last used folders when main folders are available
        viewModelScope.launch {
            repository.allMainFolders.collect { folders ->
                if (folders.isNotEmpty() && _selectedMainFolder.value == null) {
                    restoreLastUsedFolders()
                }
            }
        }

        // Cleanly handle deletions from Manage Folders: if a selected folder is deleted, reset it
        viewModelScope.launch {
            mainFolders.collect { list ->
                val current = _selectedMainFolder.value
                if (current != null && list.none { it.id == current.id }) {
                    _selectedMainFolder.value = list.firstOrNull()
                    _selectedSubFolder.value = null
                    _selectedSubSubFolder.value = null
                }
            }
        }

        viewModelScope.launch {
            subFoldersForSelectedMain.collect { list ->
                val current = _selectedSubFolder.value
                if (current != null && list.none { it.id == current.id }) {
                    _selectedSubFolder.value = null
                    _selectedSubSubFolder.value = null
                }
            }
        }

        viewModelScope.launch {
            subSubFoldersForSelectedSub.collect { list ->
                val current = _selectedSubSubFolder.value
                if (current != null && list.none { it.id == current.id }) {
                    _selectedSubSubFolder.value = null
                }
            }
        }
    }

    fun restoreLastUsedFolders() {
        viewModelScope.launch {
            val allMains = repository.allMainFolders.first()
            if (allMains.isEmpty()) return@launch

            val latestEntry = repository.getLatestEntry()

            // 1. Select Main Folder
            val currentMain = _selectedMainFolder.value
            val mainToSelect = if (currentMain != null && allMains.any { it.id == currentMain.id }) {
                currentMain
            } else {
                val savedMainId = preferences.lastUsedMainFolderId.first()
                allMains.find { it.id == savedMainId }
                    ?: allMains.find { it.id == latestEntry?.mainFolderId }
                    ?: allMains.first()
            }
            _selectedMainFolder.value = mainToSelect

            // 2. Select Sub Folder
            val subFolders = repository.getSubFoldersForMain(mainToSelect.id).first()
            val currentSub = _selectedSubFolder.value
            val subToSelect = if (currentSub != null && subFolders.any { it.id == currentSub.id }) {
                currentSub
            } else {
                val savedSubForMain = preferences.getLastUsedSubFolderForMain(mainToSelect.id).first()
                val savedSubId = preferences.lastUsedSubFolderId.first()
                when {
                    savedSubForMain == AppPreferences.NONE_MARKER -> null
                    savedSubForMain != null -> subFolders.find { it.id == savedSubForMain }
                    latestEntry?.mainFolderId == mainToSelect.id && latestEntry.subFolderId != null ->
                        subFolders.find { it.id == latestEntry.subFolderId }
                    savedSubId != null -> subFolders.find { it.id == savedSubId }
                    else -> null
                }
            }
            _selectedSubFolder.value = subToSelect

            // 3. Select Sub-Sub Folder
            if (subToSelect != null) {
                val subSubFolders = repository.getSubSubFoldersForSub(subToSelect.id).first()
                val currentSubSub = _selectedSubSubFolder.value
                val subSubToSelect = if (currentSubSub != null && subSubFolders.any { it.id == currentSubSub.id }) {
                    currentSubSub
                } else {
                    val savedSubSubForSub = preferences.getLastUsedSubSubFolderForSub(subToSelect.id).first()
                    val savedSubSubId = preferences.lastUsedSubSubFolderId.first()
                    when {
                        savedSubSubForSub == AppPreferences.NONE_MARKER -> null
                        savedSubSubForSub != null -> subSubFolders.find { it.id == savedSubSubForSub }
                        latestEntry?.subFolderId == subToSelect.id && latestEntry.subSubFolderId != null ->
                            subSubFolders.find { it.id == latestEntry.subSubFolderId }
                        savedSubSubId != null -> subSubFolders.find { it.id == savedSubSubId }
                        else -> null
                    }
                }
                _selectedSubSubFolder.value = subSubToSelect
            } else {
                _selectedSubSubFolder.value = null
            }
        }
    }

    fun startEditing(entry: VocabularyEntryEntity) {
        editingEntry = entry
        _isEditing.value = true
        originalWord.value = entry.originalWord
        translatedWord.value = entry.translatedWord
        articleOrGender.value = entry.articleOrGender
        exampleSentence.value = entry.exampleSentence ?: ""
        notes.value = entry.notes ?: ""
        viewModelScope.launch {
            val main = repository.getMainFolderById(entry.mainFolderId)
            _selectedMainFolder.value = main
            val sub = entry.subFolderId?.let { repository.getSubFolderById(it) }
            _selectedSubFolder.value = sub
            val subSub = entry.subSubFolderId?.let { repository.getSubSubFolderById(it) }
            _selectedSubSubFolder.value = subSub
        }
    }

    fun startNewEntry() {
        editingEntry = null
        _isEditing.value = false
        originalWord.value = ""
        translatedWord.value = ""
        articleOrGender.value = null
        exampleSentence.value = ""
        notes.value = ""
        // Crucial: Keep the last used sub-sub folder intact for smooth consecutive entry!
        // If _selectedSubSubFolder is currently null and a sub-folder is selected, restore last used sub-sub folder.
        if (_selectedSubSubFolder.value == null && _selectedSubFolder.value != null) {
            restoreLastUsedSubSubFolderForCurrentSub()
        }
    }

    private fun restoreLastUsedSubSubFolderForCurrentSub() {
        val currentSub = _selectedSubFolder.value ?: return
        viewModelScope.launch {
            val subSubFolders = repository.getSubSubFoldersForSub(currentSub.id).first()
            val savedSubSubForSub = preferences.getLastUsedSubSubFolderForSub(currentSub.id).first()
            val latestEntryForSub = repository.getLatestEntryForSubFolder(currentSub.id)

            val subSubToSelect = when {
                savedSubSubForSub == AppPreferences.NONE_MARKER -> null
                savedSubSubForSub != null -> subSubFolders.find { it.id == savedSubSubForSub }
                latestEntryForSub?.subSubFolderId != null -> subSubFolders.find { it.id == latestEntryForSub.subSubFolderId }
                else -> null
            }
            if (subSubToSelect != null) {
                _selectedSubSubFolder.value = subSubToSelect
            }
        }
    }

    fun selectMainFolder(folder: MainFolderEntity) {
        _selectedMainFolder.value = folder
        viewModelScope.launch {
            preferences.setLastUsedMainFolderId(folder.id)
            val subFolders = repository.getSubFoldersForMain(folder.id).first()
            val savedSubForMain = preferences.getLastUsedSubFolderForMain(folder.id).first()
            val latestEntry = repository.getLatestEntryForMainFolder(folder.id)

            val subToSelect = when {
                savedSubForMain == AppPreferences.NONE_MARKER -> null
                savedSubForMain != null -> subFolders.find { it.id == savedSubForMain }
                latestEntry?.subFolderId != null -> subFolders.find { it.id == latestEntry.subFolderId }
                else -> null
            }
            _selectedSubFolder.value = subToSelect
            preferences.setLastUsedSubFolderId(subToSelect?.id)

            if (subToSelect != null) {
                val subSubFolders = repository.getSubSubFoldersForSub(subToSelect.id).first()
                val savedSubSubForSub = preferences.getLastUsedSubSubFolderForSub(subToSelect.id).first()
                val latestEntryForSub = repository.getLatestEntryForSubFolder(subToSelect.id)

                val subSubToSelect = when {
                    savedSubSubForSub == AppPreferences.NONE_MARKER -> null
                    savedSubSubForSub != null -> subSubFolders.find { it.id == savedSubSubForSub }
                    latestEntryForSub?.subSubFolderId != null -> subSubFolders.find { it.id == latestEntryForSub.subSubFolderId }
                    else -> null
                }
                _selectedSubSubFolder.value = subSubToSelect
                preferences.setLastUsedSubSubFolderId(subSubToSelect?.id)
            } else {
                _selectedSubSubFolder.value = null
                preferences.setLastUsedSubSubFolderId(null)
            }
        }
    }

    fun selectSubFolder(subFolder: SubFolderEntity?) {
        _selectedSubFolder.value = subFolder
        viewModelScope.launch {
            val main = _selectedMainFolder.value
            if (main != null) {
                preferences.setLastUsedSubFolderForMain(main.id, subFolder?.id)
            }
            preferences.setLastUsedSubFolderId(subFolder?.id)

            if (subFolder == null) {
                _selectedSubSubFolder.value = null
                preferences.setLastUsedSubSubFolderId(null)
            } else {
                // Automatically select last used sub-sub folder for this sub-folder!
                val subSubFolders = repository.getSubSubFoldersForSub(subFolder.id).first()
                val savedSubSubForSub = preferences.getLastUsedSubSubFolderForSub(subFolder.id).first()
                val latestEntryForSub = repository.getLatestEntryForSubFolder(subFolder.id)

                val subSubToSelect = when {
                    savedSubSubForSub == AppPreferences.NONE_MARKER -> null
                    savedSubSubForSub != null -> subSubFolders.find { it.id == savedSubSubForSub }
                    latestEntryForSub?.subSubFolderId != null -> subSubFolders.find { it.id == latestEntryForSub.subSubFolderId }
                    else -> null
                }
                _selectedSubSubFolder.value = subSubToSelect
                preferences.setLastUsedSubSubFolderId(subSubToSelect?.id)
            }
        }
    }

    fun selectSubSubFolder(subSubFolder: SubSubFolderEntity?) {
        _selectedSubSubFolder.value = subSubFolder
        viewModelScope.launch {
            preferences.setLastUsedSubSubFolderId(subSubFolder?.id)
            val sub = _selectedSubFolder.value
            if (sub != null) {
                preferences.setLastUsedSubSubFolderForSub(sub.id, subSubFolder?.id)
            }
        }
    }

    fun selectArticle(article: String?) {
        if (articleOrGender.value == article) {
            articleOrGender.value = null
        } else {
            articleOrGender.value = article
        }
    }

    fun previewPronunciation() {
        val word = originalWord.value.trim()
        if (word.isBlank()) return
        val art = articleOrGender.value?.trim()
        val spokenText = if (!art.isNullOrBlank() &&
            !art.equals("verb", ignoreCase = true) &&
            !art.equals("adj", ignoreCase = true) &&
            !art.equals("other", ignoreCase = true) &&
            !word.startsWith(art, ignoreCase = true)
        ) {
            "$art $word"
        } else {
            word
        }
        val lang = _selectedMainFolder.value?.sourceLanguage ?: "de"
        viewModelScope.launch {
            val rate = preferences.speechRate.first()
            val pitch = preferences.speechPitch.first()
            ttsManager.speak(spokenText, lang, rate, pitch)
        }
    }

    fun createNewMainFolder(name: String, sourceLang: String = "de", targetLang: String = "en") {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newFolder = MainFolderEntity(
                name = name.trim(),
                sourceLanguage = sourceLang,
                targetLanguage = targetLang,
                colorHex = "#2E9C7E",
                order = mainFolders.value.size
            )
            repository.insertMainFolder(newFolder)
            _selectedMainFolder.value = newFolder
            _selectedSubFolder.value = null
            _selectedSubSubFolder.value = null
            preferences.setLastUsedFolders(newFolder.id, null, null)
        }
    }

    fun createNewSubFolder(name: String) {
        val main = _selectedMainFolder.value ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            val newSub = SubFolderEntity(
                mainFolderId = main.id,
                name = name.trim(),
                order = subFoldersForSelectedMain.value.size
            )
            repository.insertSubFolder(newSub)
            _selectedSubFolder.value = newSub
            preferences.setLastUsedSubFolderId(newSub.id)
            preferences.setLastUsedSubFolderForMain(main.id, newSub.id)
            _selectedSubSubFolder.value = null
            preferences.setLastUsedSubSubFolderId(null)
        }
    }

    fun createNewSubSubFolder(name: String) {
        val sub = _selectedSubFolder.value ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            val newSubSub = SubSubFolderEntity(
                subFolderId = sub.id,
                name = name.trim(),
                order = subSubFoldersForSelectedSub.value.size
            )
            repository.insertSubSubFolder(newSubSub)
            _selectedSubSubFolder.value = newSubSub
            preferences.setLastUsedSubSubFolderId(newSubSub.id)
            preferences.setLastUsedSubSubFolderForSub(sub.id, newSubSub.id)
        }
    }

    fun saveVocabulary() {
        val orig = originalWord.value.trim()
        val trans = translatedWord.value.trim()
        val main = _selectedMainFolder.value ?: return
        if (orig.isBlank() || trans.isBlank()) return

        val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            val current = editingEntry
            if (current != null) {
                val updated = current.copy(
                    mainFolderId = main.id,
                    mainFolderName = main.name,
                    subFolderId = _selectedSubFolder.value?.id,
                    subFolderName = _selectedSubFolder.value?.name,
                    subSubFolderId = _selectedSubSubFolder.value?.id,
                    subSubFolderName = _selectedSubSubFolder.value?.name,
                    originalWord = orig,
                    translatedWord = trans,
                    sourceLanguage = main.sourceLanguage,
                    targetLanguage = main.targetLanguage,
                    articleOrGender = articleOrGender.value,
                    exampleSentence = exampleSentence.value.trim().takeIf { it.isNotBlank() },
                    notes = notes.value.trim().takeIf { it.isNotBlank() }
                )
                repository.updateEntry(updated)

                if (preferences.autoPronounceOnSave.first()) {
                    val rate = preferences.speechRate.first()
                    val pitch = preferences.speechPitch.first()
                    ttsManager.speak(updated.getPronunciationText(), main.sourceLanguage, rate, pitch)
                }
            } else {
                val entry = VocabularyEntryEntity(
                    mainFolderId = main.id,
                    mainFolderName = main.name,
                    subFolderId = _selectedSubFolder.value?.id,
                    subFolderName = _selectedSubFolder.value?.name,
                    subSubFolderId = _selectedSubSubFolder.value?.id,
                    subSubFolderName = _selectedSubSubFolder.value?.name,
                    originalWord = orig,
                    translatedWord = trans,
                    sourceLanguage = main.sourceLanguage,
                    targetLanguage = main.targetLanguage,
                    articleOrGender = articleOrGender.value,
                    exampleSentence = exampleSentence.value.trim().takeIf { it.isNotBlank() },
                    notes = notes.value.trim().takeIf { it.isNotBlank() },
                    dateAddedString = todayDateString
                )

                repository.insertEntry(entry)

                // Auto-pronounce if enabled
                if (preferences.autoPronounceOnSave.first()) {
                    val rate = preferences.speechRate.first()
                    val pitch = preferences.speechPitch.first()
                    ttsManager.speak(entry.getPronunciationText(), main.sourceLanguage, rate, pitch)
                }
            }

            // Persist the last used folders so they remain selected across app launches & entries
            preferences.setLastUsedFolders(
                mainId = main.id,
                subId = _selectedSubFolder.value?.id,
                subSubId = _selectedSubSubFolder.value?.id
            )
            _selectedSubFolder.value?.id?.let { subId ->
                preferences.setLastUsedSubSubFolderForSub(subId, _selectedSubSubFolder.value?.id)
            }
            preferences.setLastUsedSubFolderForMain(main.id, _selectedSubFolder.value?.id)

            startNewEntry()
            _saveSuccessEvent.emit(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}


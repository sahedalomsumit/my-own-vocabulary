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
        // Automatically select first folder when loaded if none selected
        viewModelScope.launch {
            val folders = repository.allMainFolders.first()
            if (folders.isNotEmpty() && _selectedMainFolder.value == null) {
                _selectedMainFolder.value = folders.first()
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
        _selectedSubSubFolder.value = null
    }

    fun selectMainFolder(folder: MainFolderEntity) {
        _selectedMainFolder.value = folder
        _selectedSubFolder.value = null
        _selectedSubSubFolder.value = null
    }

    fun selectSubFolder(subFolder: SubFolderEntity?) {
        _selectedSubFolder.value = subFolder
        _selectedSubSubFolder.value = null
    }

    fun selectSubSubFolder(subSubFolder: SubSubFolderEntity?) {
        _selectedSubSubFolder.value = subSubFolder
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
            _selectedSubSubFolder.value = null
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

            startNewEntry()
            _saveSuccessEvent.emit(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}


package com.sahed.my_own_vocabulary.ui.screens.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import com.sahed.my_own_vocabulary.data.local.entity.getPronunciationText
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import com.sahed.my_own_vocabulary.data.preferences.QuizFormat
import com.sahed.my_own_vocabulary.data.repository.VocabularyRepository
import com.sahed.my_own_vocabulary.util.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class QuizState {
    IDLE,
    ACTIVE,
    COMPLETED
}

data class MultipleChoiceQuestion(
    val entry: VocabularyEntryEntity,
    val options: List<String>,
    val correctIndex: Int
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
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

    private val _selectedFolderId = MutableStateFlow<String?>(null)
    val selectedFolderId: StateFlow<String?> = _selectedFolderId.asStateFlow()

    private val _quizFormat = MutableStateFlow(QuizFormat.MULTIPLE_CHOICE)
    val quizFormat: StateFlow<QuizFormat> = _quizFormat.asStateFlow()

    private val _quizState = MutableStateFlow(QuizState.IDLE)
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _quizEntries = MutableStateFlow<List<VocabularyEntryEntity>>(emptyList())
    val quizEntries: StateFlow<List<VocabularyEntryEntity>> = _quizEntries.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    // Multiple Choice State
    private val _currentMultipleChoice = MutableStateFlow<MultipleChoiceQuestion?>(null)
    val currentMultipleChoice: StateFlow<MultipleChoiceQuestion?> = _currentMultipleChoice.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    // Flashcard State
    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    // Spelling State
    private val _typedAnswer = MutableStateFlow("")
    val typedAnswer: StateFlow<String> = _typedAnswer.asStateFlow()

    private val _spellingSubmitted = MutableStateFlow(false)
    val spellingSubmitted: StateFlow<Boolean> = _spellingSubmitted.asStateFlow()

    private val _isSpellingCorrect = MutableStateFlow(false)
    val isSpellingCorrect: StateFlow<Boolean> = _isSpellingCorrect.asStateFlow()

    // Quiz Metrics
    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount.asStateFlow()

    private val _mistakeEntries = MutableStateFlow<MutableList<VocabularyEntryEntity>>(mutableListOf())
    val mistakeEntries: StateFlow<List<VocabularyEntryEntity>> = _mistakeEntries.asStateFlow()

    fun selectFolder(folderId: String?) {
        _selectedFolderId.value = folderId
    }

    fun setQuizFormat(format: QuizFormat) {
        _quizFormat.value = format
    }

    fun startQuiz(onlyMistakes: Boolean = false) {
        viewModelScope.launch {
            val allEntries = repository.getAllEntriesSnapshot()
            val candidateEntries = if (onlyMistakes) {
                _mistakeEntries.value.toList()
            } else {
                val folderId = _selectedFolderId.value
                if (folderId != null) {
                    allEntries.filter { it.mainFolderId == folderId }
                } else {
                    allEntries
                }
            }

            if (candidateEntries.isEmpty()) return@launch

            val shuffled = candidateEntries.shuffled().take(15)
            _quizEntries.value = shuffled
            _currentIndex.value = 0
            _correctCount.value = 0
            _mistakeEntries.value = mutableListOf()
            _quizState.value = QuizState.ACTIVE

            loadQuestion(0, allEntries)
        }
    }

    private fun loadQuestion(index: Int, allEntries: List<VocabularyEntryEntity>) {
        val entries = _quizEntries.value
        if (index >= entries.size) {
            _quizState.value = QuizState.COMPLETED
            return
        }

        val entry = entries[index]
        _selectedAnswerIndex.value = null
        _isCardFlipped.value = false
        _typedAnswer.value = ""
        _spellingSubmitted.value = false
        _isSpellingCorrect.value = false

        when (_quizFormat.value) {
            QuizFormat.MULTIPLE_CHOICE -> {
                // Generate 3 distractors from allEntries
                val distractors = allEntries
                    .filter { it.id != entry.id && it.translatedWord.isNotBlank() }
                    .map { it.translatedWord }
                    .distinct()
                    .shuffled()
                    .take(3)

                val options = (distractors + entry.translatedWord).shuffled()
                val correctIdx = options.indexOf(entry.translatedWord)

                _currentMultipleChoice.value = MultipleChoiceQuestion(
                    entry = entry,
                    options = options,
                    correctIndex = correctIdx
                )
            }
            QuizFormat.FLASHCARD -> {
                // Flashcard ready
            }
            QuizFormat.SPELLING -> {
                // Spelling test ready
            }
        }
    }

    fun submitMultipleChoiceAnswer(selectedIndex: Int) {
        if (_selectedAnswerIndex.value != null) return // already answered
        _selectedAnswerIndex.value = selectedIndex

        val mc = _currentMultipleChoice.value ?: return
        val isCorrect = selectedIndex == mc.correctIndex

        if (isCorrect) {
            _correctCount.value += 1
        } else {
            _mistakeEntries.value.add(mc.entry)
        }

        // Record in database
        viewModelScope.launch {
            repository.recordQuizResult(
                id = mc.entry.id,
                isCorrect = isCorrect,
                currentMastery = mc.entry.masteryLevel,
                timesReviewed = mc.entry.timesReviewed,
                timesCorrect = mc.entry.timesCorrect
            )
            // Auto pronounce
            if (preferences.autoPronounceInQuiz.first()) {
                speakCurrentWord(mc.entry)
            }
        }
    }

    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun answerFlashcard(isCorrect: Boolean) {
        val entries = _quizEntries.value
        val entry = entries.getOrNull(_currentIndex.value) ?: return

        if (isCorrect) {
            _correctCount.value += 1
        } else {
            _mistakeEntries.value.add(entry)
        }

        viewModelScope.launch {
            repository.recordQuizResult(
                id = entry.id,
                isCorrect = isCorrect,
                currentMastery = entry.masteryLevel,
                timesReviewed = entry.timesReviewed,
                timesCorrect = entry.timesCorrect
            )
            advanceQuestion()
        }
    }

    fun setTypedAnswer(text: String) {
        _typedAnswer.value = text
    }

    fun submitSpellingAnswer() {
        if (_spellingSubmitted.value) return
        val entries = _quizEntries.value
        val entry = entries.getOrNull(_currentIndex.value) ?: return

        val target = entry.originalWord.trim()
        val userText = _typedAnswer.value.trim()

        // Strip german article if user omitted it
        val targetWithoutArticle = target.replace(Regex("^(der|die|das|el|la)\\s+", RegexOption.IGNORE_CASE), "")
        val isCorrect = userText.equals(target, ignoreCase = true) ||
                userText.equals(targetWithoutArticle, ignoreCase = true)

        _isSpellingCorrect.value = isCorrect
        _spellingSubmitted.value = true

        if (isCorrect) {
            _correctCount.value += 1
        } else {
            _mistakeEntries.value.add(entry)
        }

        viewModelScope.launch {
            repository.recordQuizResult(
                id = entry.id,
                isCorrect = isCorrect,
                currentMastery = entry.masteryLevel,
                timesReviewed = entry.timesReviewed,
                timesCorrect = entry.timesCorrect
            )
            if (preferences.autoPronounceInQuiz.first()) {
                speakCurrentWord(entry)
            }
        }
    }

    fun advanceQuestion() {
        viewModelScope.launch {
            val nextIdx = _currentIndex.value + 1
            _currentIndex.value = nextIdx
            val allEntries = repository.getAllEntriesSnapshot()
            loadQuestion(nextIdx, allEntries)
        }
    }

    fun exitQuiz() {
        _quizState.value = QuizState.IDLE
    }

    fun speakCurrentWord(entry: VocabularyEntryEntity) {
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

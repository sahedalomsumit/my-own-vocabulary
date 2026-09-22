package com.sahed.my_own_vocabulary.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import com.sahed.my_own_vocabulary.data.repository.VocabularyRepository
import com.sahed.my_own_vocabulary.util.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.sahed.my_own_vocabulary.data.local.entity.getPronunciationText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CalendarDay(
    val date: Date,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val dateString: String,
    val wordCount: Int
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = VocabularyRepository(
        database.mainFolderDao(),
        database.subFolderDao(),
        database.subSubFolderDao(),
        database.vocabularyEntryDao()
    )
    private val preferences = AppPreferences(application)
    val ttsManager = TtsManager(application)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Currently displayed Month & Year calendar
    private val _currentCalendarMonth = MutableStateFlow(Calendar.getInstance())
    val currentCalendarMonth: StateFlow<Calendar> = _currentCalendarMonth.asStateFlow()

    // Currently selected date string (default to today)
    private val _selectedDateString = MutableStateFlow(dateFormat.format(Date()))
    val selectedDateString: StateFlow<String> = _selectedDateString.asStateFlow()

    // Words added on the selected date
    val entriesForSelectedDate: StateFlow<List<VocabularyEntryEntity>> = _selectedDateString
        .flatMapLatest { dateStr ->
            repository.getEntriesByDate(dateStr)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All entries snapshot to map date strings to word counts
    val calendarDays: StateFlow<List<CalendarDay>> = combine(
        _currentCalendarMonth,
        repository.allEntries
    ) { cal, entries ->
        val countsByDate = entries.groupingBy { it.dateAddedString }.eachCount()
        buildCalendarDaysForMonth(cal, countsByDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(dateString: String) {
        _selectedDateString.value = dateString
    }

    fun nextMonth() {
        val nextCal = _currentCalendarMonth.value.clone() as Calendar
        nextCal.add(Calendar.MONTH, 1)
        _currentCalendarMonth.value = nextCal
    }

    fun previousMonth() {
        val prevCal = _currentCalendarMonth.value.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        _currentCalendarMonth.value = prevCal
    }

    fun speakWord(entry: VocabularyEntryEntity) {
        viewModelScope.launch {
            val rate = preferences.speechRate.first()
            val pitch = preferences.speechPitch.first()
            ttsManager.speak(entry.getPronunciationText(), entry.sourceLanguage, rate, pitch)
        }
    }

    fun toggleFavorite(entry: VocabularyEntryEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entry.id, !entry.isFavorite)
        }
    }

    private fun buildCalendarDaysForMonth(
        baseCal: Calendar,
        countsByDate: Map<String, Int>
    ): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val cal = baseCal.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val currentMonth = cal.get(Calendar.MONTH)
        val todayStr = dateFormat.format(Date())

        // Calculate leading days from previous month to align with Monday
        // In Java Calendar, SUNDAY=1, MONDAY=2, ... SATURDAY=7
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        // Convert to Monday=0, Tuesday=1 ... Sunday=6
        val dayOffset = (firstDayOfWeek + 5) % 7

        cal.add(Calendar.DAY_OF_MONTH, -dayOffset)

        // 6 rows of 7 days = 42 days grid
        for (i in 0 until 42) {
            val date = cal.time
            val dateStr = dateFormat.format(date)
            val isCurrentMonth = cal.get(Calendar.MONTH) == currentMonth
            val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            val count = countsByDate[dateStr] ?: 0

            days.add(
                CalendarDay(
                    date = date,
                    dayOfMonth = dayOfMonth,
                    isCurrentMonth = isCurrentMonth,
                    isToday = dateStr == todayStr,
                    dateString = dateStr,
                    wordCount = count
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}

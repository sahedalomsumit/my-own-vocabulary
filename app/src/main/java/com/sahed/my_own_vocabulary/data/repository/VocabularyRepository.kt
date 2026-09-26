package com.sahed.my_own_vocabulary.data.repository

import com.sahed.my_own_vocabulary.data.local.dao.MainFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.SubFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.SubSubFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.VocabularyEntryDao
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubSubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface SyncCallback {
    fun onEntryChanged(entry: VocabularyEntryEntity)
    fun onEntryDeleted(entryId: String)
    fun onMainFolderChanged(folder: MainFolderEntity)
    fun onMainFolderDeleted(folderId: String)
    fun onSubFolderChanged(subFolder: SubFolderEntity)
    fun onSubFolderDeleted(subFolderId: String)
    fun onSubSubFolderChanged(subSubFolder: SubSubFolderEntity)
    fun onSubSubFolderDeleted(subSubFolderId: String)
}

object SyncManager {
    var syncListener: SyncCallback? = null
}

class VocabularyRepository(
    private val mainFolderDao: MainFolderDao,
    private val subFolderDao: SubFolderDao,
    private val subSubFolderDao: SubSubFolderDao,
    private val vocabDao: VocabularyEntryDao
) {
    // Main Folders
    val allMainFolders: Flow<List<MainFolderEntity>> = mainFolderDao.getAllFlow()
    val allSubFolders: Flow<List<SubFolderEntity>> = subFolderDao.getAllFlow()
    val allSubSubFolders: Flow<List<SubSubFolderEntity>> = subSubFolderDao.getAllFlow()

    fun getSubFoldersForMain(mainFolderId: String): Flow<List<SubFolderEntity>> =
        subFolderDao.getByMainFolderFlow(mainFolderId)

    fun getSubSubFoldersForSub(subFolderId: String): Flow<List<SubSubFolderEntity>> =
        subSubFolderDao.getBySubFolderFlow(subFolderId)

    suspend fun getMainFolderById(id: String): MainFolderEntity? = mainFolderDao.getById(id)
    suspend fun getSubFolderById(id: String): SubFolderEntity? = subFolderDao.getById(id)
    suspend fun getSubSubFolderById(id: String): SubSubFolderEntity? = subSubFolderDao.getById(id)

    suspend fun insertMainFolder(folder: MainFolderEntity) {
        mainFolderDao.insert(folder)
        SyncManager.syncListener?.onMainFolderChanged(folder)
    }

    suspend fun insertAllMainFolders(folders: List<MainFolderEntity>) = mainFolderDao.insertAll(folders)

    suspend fun updateMainFolder(folder: MainFolderEntity) {
        mainFolderDao.update(folder)
        SyncManager.syncListener?.onMainFolderChanged(folder)
    }

    suspend fun deleteMainFolder(folder: MainFolderEntity) {
        mainFolderDao.delete(folder)
        SyncManager.syncListener?.onMainFolderDeleted(folder.id)
    }

    suspend fun insertSubFolder(subFolder: SubFolderEntity) {
        subFolderDao.insert(subFolder)
        SyncManager.syncListener?.onSubFolderChanged(subFolder)
    }

    suspend fun insertAllSubFolders(subFolders: List<SubFolderEntity>) = subFolderDao.insertAll(subFolders)

    suspend fun updateSubFolder(subFolder: SubFolderEntity) {
        subFolderDao.update(subFolder)
        SyncManager.syncListener?.onSubFolderChanged(subFolder)
    }

    suspend fun deleteSubFolder(subFolder: SubFolderEntity) {
        subFolderDao.delete(subFolder)
        SyncManager.syncListener?.onSubFolderDeleted(subFolder.id)
    }

    suspend fun insertSubSubFolder(subSubFolder: SubSubFolderEntity) {
        subSubFolderDao.insert(subSubFolder)
        SyncManager.syncListener?.onSubSubFolderChanged(subSubFolder)
    }

    suspend fun insertAllSubSubFolders(subSubFolders: List<SubSubFolderEntity>) =
        subSubFolderDao.insertAll(subSubFolders)

    suspend fun updateSubSubFolder(subSubFolder: SubSubFolderEntity) {
        subSubFolderDao.update(subSubFolder)
        SyncManager.syncListener?.onSubSubFolderChanged(subSubFolder)
    }

    suspend fun deleteSubSubFolder(subSubFolder: SubSubFolderEntity) {
        subSubFolderDao.delete(subSubFolder)
        SyncManager.syncListener?.onSubSubFolderDeleted(subSubFolder.id)
    }

    // Vocabulary Entries
    val allEntries: Flow<List<VocabularyEntryEntity>> = vocabDao.getAllFlow()
    val totalWordsCount: Flow<Int> = vocabDao.getTotalCountFlow()
    val masteredWordsCount: Flow<Int> = vocabDao.getMasteredCountFlow()
    val allDatesWithWords: Flow<List<String>> = vocabDao.getAllDatesWithEntriesFlow()

    fun getEntriesByMainFolder(mainFolderId: String): Flow<List<VocabularyEntryEntity>> =
        vocabDao.getByMainFolderFlow(mainFolderId)

    fun getEntriesBySubFolder(subFolderId: String): Flow<List<VocabularyEntryEntity>> =
        vocabDao.getBySubFolderFlow(subFolderId)

    fun getEntriesBySubSubFolder(subSubFolderId: String): Flow<List<VocabularyEntryEntity>> =
        vocabDao.getBySubSubFolderFlow(subSubFolderId)

    fun getEntriesByDate(dateString: String): Flow<List<VocabularyEntryEntity>> =
        vocabDao.getByDateAddedFlow(dateString)

    fun getFavorites(): Flow<List<VocabularyEntryEntity>> = vocabDao.getFavoritesFlow()

    fun searchEntries(query: String): Flow<List<VocabularyEntryEntity>> =
        vocabDao.searchEntriesFlow(query)

    suspend fun getEntryById(id: String): VocabularyEntryEntity? = vocabDao.getById(id)
    suspend fun getLatestEntry(): VocabularyEntryEntity? = vocabDao.getLatestEntry()
    suspend fun getLatestEntryForMainFolder(mainFolderId: String): VocabularyEntryEntity? =
        vocabDao.getLatestEntryForMainFolder(mainFolderId)
    suspend fun getLatestEntryForSubFolder(subFolderId: String): VocabularyEntryEntity? =
        vocabDao.getLatestEntryForSubFolder(subFolderId)

    suspend fun insertEntry(entry: VocabularyEntryEntity) {
        vocabDao.insert(entry)
        SyncManager.syncListener?.onEntryChanged(entry)
    }

    suspend fun insertAllEntries(entries: List<VocabularyEntryEntity>) = vocabDao.insertAll(entries)

    suspend fun updateEntry(entry: VocabularyEntryEntity) {
        vocabDao.update(entry)
        SyncManager.syncListener?.onEntryChanged(entry)
    }

    suspend fun deleteEntry(entry: VocabularyEntryEntity) {
        vocabDao.delete(entry)
        SyncManager.syncListener?.onEntryDeleted(entry.id)
    }

    suspend fun deleteEntryById(id: String) {
        vocabDao.deleteById(id)
        SyncManager.syncListener?.onEntryDeleted(id)
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        vocabDao.updateFavorite(id, isFavorite)
        vocabDao.getById(id)?.let { SyncManager.syncListener?.onEntryChanged(it) }
    }

    suspend fun recordQuizResult(
        id: String,
        isCorrect: Boolean,
        currentMastery: Int,
        timesReviewed: Int,
        timesCorrect: Int
    ) {
        val newReviewed = timesReviewed + 1
        val newCorrect = if (isCorrect) timesCorrect + 1 else timesCorrect
        val newMastery = when {
            isCorrect && currentMastery < 2 -> currentMastery + 1
            !isCorrect && currentMastery > 0 -> currentMastery - 1
            else -> currentMastery
        }
        vocabDao.recordQuizResult(id, newMastery, newReviewed, newCorrect)
        vocabDao.getById(id)?.let { SyncManager.syncListener?.onEntryChanged(it) }
    }

    suspend fun getAllEntriesSnapshot(): List<VocabularyEntryEntity> = vocabDao.getAllSnapshot()

    /**
     * Calculates consecutive streak days by inspecting distinct dates words were added.
     * Streak counts only if user adds vocabulary every day including today.
     * If vocabulary was not added today, streak resets to 0.
     * When words are added again, it counts from 1 (or continues previous consecutive streak if unbroken).
     */
    val streakDaysFlow: Flow<Int> = vocabDao.getAllDatesWithEntriesFlow().map { dateList ->
        if (dateList.isEmpty()) return@map 0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateSet = dateList.toSet()

        val calendar = Calendar.getInstance()
        val todayStr = dateFormat.format(calendar.time)

        // If user has not added any vocabulary today, streak resets to 0
        if (!dateSet.contains(todayStr)) {
            return@map 0
        }

        var streak = 0
        // Count backward consecutively starting from today
        while (true) {
            val dateStr = dateFormat.format(calendar.time)
            if (dateSet.contains(dateStr)) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        streak
    }

    suspend fun deleteAllEntries() = vocabDao.deleteAll()

    suspend fun clearAllData() {
        vocabDao.deleteAll()
        subSubFolderDao.deleteAll()
        subFolderDao.deleteAll()
        mainFolderDao.deleteAll()
    }
}



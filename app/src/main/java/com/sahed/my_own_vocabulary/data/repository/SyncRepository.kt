package com.sahed.my_own_vocabulary.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubSubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SyncRepository(
    private val vocabularyRepository: VocabularyRepository,
    private val preferences: AppPreferences
) : SyncCallback {
    private val tag = "SyncRepository"
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val syncScope = CoroutineScope(Dispatchers.IO)

    init {
        // Automatically register as listener for local repository changes
        SyncManager.syncListener = this
    }

    val currentUser get() = auth.currentUser
    val isUserSignedIn: Boolean get() = auth.currentUser != null

    /**
     * Trigger background automatic synchronization with Firestore.
     */
    fun autoSync() {
        if (!isUserSignedIn) return
        syncScope.launch {
            try {
                syncWithCloud()
            } catch (e: Exception) {
                Log.w(tag, "Auto-sync error: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Comprehensive two-way sync for vocabulary entries and all 3 folder tiers.
     */
    suspend fun syncWithCloud(): Result<String> {
        val user = currentUser ?: return Result.failure(Exception("Please sign in to sync with cloud"))
        val userId = user.uid

        return try {
            val userDocRef = firestore.collection("users").document(userId)

            // 1. Sync Main Folders
            val localMainFolders = vocabularyRepository.allMainFolders.first()
            val mainFoldersCollection = userDocRef.collection("main_folders")
            for (main in localMainFolders) {
                val map = hashMapOf(
                    "id" to main.id,
                    "name" to main.name,
                    "sourceLanguage" to main.sourceLanguage,
                    "targetLanguage" to main.targetLanguage,
                    "colorHex" to main.colorHex,
                    "iconName" to main.iconName,
                    "order" to main.order,
                    "createdAt" to main.createdAt
                )
                mainFoldersCollection.document(main.id).set(map, SetOptions.merge()).await()
            }
            // Fetch remote main folders
            val remoteMainDocs = mainFoldersCollection.get().await()
            val remoteMainFolders = remoteMainDocs.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val name = doc.getString("name") ?: return@mapNotNull null
                MainFolderEntity(
                    id = id,
                    name = name,
                    sourceLanguage = doc.getString("sourceLanguage") ?: "de",
                    targetLanguage = doc.getString("targetLanguage") ?: "en",
                    colorHex = doc.getString("colorHex") ?: "#2E9C7E",
                    iconName = doc.getString("iconName") ?: "book",
                    order = (doc.getLong("order") ?: 0).toInt(),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
            }
            if (remoteMainFolders.isNotEmpty()) {
                vocabularyRepository.insertAllMainFolders(remoteMainFolders)
            }

            // 2. Sync Sub Folders
            val localSubFolders = vocabularyRepository.allSubFolders.first()
            val subFoldersCollection = userDocRef.collection("sub_folders")
            for (sub in localSubFolders) {
                val map = hashMapOf(
                    "id" to sub.id,
                    "mainFolderId" to sub.mainFolderId,
                    "name" to sub.name,
                    "order" to sub.order,
                    "createdAt" to sub.createdAt
                )
                subFoldersCollection.document(sub.id).set(map, SetOptions.merge()).await()
            }
            // Fetch remote sub folders
            val remoteSubDocs = subFoldersCollection.get().await()
            val remoteSubFolders = remoteSubDocs.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val mainFolderId = doc.getString("mainFolderId") ?: return@mapNotNull null
                val name = doc.getString("name") ?: return@mapNotNull null
                SubFolderEntity(
                    id = id,
                    mainFolderId = mainFolderId,
                    name = name,
                    order = (doc.getLong("order") ?: 0).toInt(),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
            }
            if (remoteSubFolders.isNotEmpty()) {
                vocabularyRepository.insertAllSubFolders(remoteSubFolders)
            }

            // 3. Sync Sub-Sub Folders
            val localSubSubFolders = vocabularyRepository.allSubSubFolders.first()
            val subSubFoldersCollection = userDocRef.collection("sub_sub_folders")
            for (subSub in localSubSubFolders) {
                val map = hashMapOf(
                    "id" to subSub.id,
                    "subFolderId" to subSub.subFolderId,
                    "name" to subSub.name,
                    "order" to subSub.order,
                    "createdAt" to subSub.createdAt
                )
                subSubFoldersCollection.document(subSub.id).set(map, SetOptions.merge()).await()
            }
            // Fetch remote sub-sub folders
            val remoteSubSubDocs = subSubFoldersCollection.get().await()
            val remoteSubSubFolders = remoteSubSubDocs.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val subFolderId = doc.getString("subFolderId") ?: return@mapNotNull null
                val name = doc.getString("name") ?: return@mapNotNull null
                SubSubFolderEntity(
                    id = id,
                    subFolderId = subFolderId,
                    name = name,
                    order = (doc.getLong("order") ?: 0).toInt(),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
            }
            if (remoteSubSubFolders.isNotEmpty()) {
                vocabularyRepository.insertAllSubSubFolders(remoteSubSubFolders)
            }

            // 4. Sync Vocabulary Entries
            val localEntries = vocabularyRepository.getAllEntriesSnapshot()
            val entriesCollection = userDocRef.collection("vocabulary_entries")

            for (entry in localEntries) {
                val entryMap = hashMapOf(
                    "id" to entry.id,
                    "mainFolderId" to entry.mainFolderId,
                    "mainFolderName" to entry.mainFolderName,
                    "subFolderId" to entry.subFolderId,
                    "subFolderName" to entry.subFolderName,
                    "subSubFolderId" to entry.subSubFolderId,
                    "subSubFolderName" to entry.subSubFolderName,
                    "originalWord" to entry.originalWord,
                    "translatedWord" to entry.translatedWord,
                    "sourceLanguage" to entry.sourceLanguage,
                    "targetLanguage" to entry.targetLanguage,
                    "articleOrGender" to entry.articleOrGender,
                    "exampleSentence" to entry.exampleSentence,
                    "notes" to entry.notes,
                    "isFavorite" to entry.isFavorite,
                    "masteryLevel" to entry.masteryLevel,
                    "timesReviewed" to entry.timesReviewed,
                    "timesCorrect" to entry.timesCorrect,
                    "createdAt" to entry.createdAt,
                    "dateAddedString" to entry.dateAddedString
                )
                entriesCollection.document(entry.id).set(entryMap, SetOptions.merge()).await()
            }

            // 5. Fetch remote entries from cloud to restore any missing local entries
            val remoteDocs = entriesCollection.get().await()
            val remoteEntries = mutableListOf<VocabularyEntryEntity>()
            for (doc in remoteDocs) {
                val id = doc.getString("id") ?: doc.id
                val originalWord = doc.getString("originalWord") ?: continue
                val translatedWord = doc.getString("translatedWord") ?: ""
                val mainFolderId = doc.getString("mainFolderId") ?: ""
                val mainFolderName = doc.getString("mainFolderName") ?: ""
                val subFolderId = doc.getString("subFolderId")
                val subFolderName = doc.getString("subFolderName")
                val subSubFolderId = doc.getString("subSubFolderId")
                val subSubFolderName = doc.getString("subSubFolderName")
                val sourceLanguage = doc.getString("sourceLanguage") ?: "de"
                val targetLanguage = doc.getString("targetLanguage") ?: "en"
                val articleOrGender = doc.getString("articleOrGender")
                val exampleSentence = doc.getString("exampleSentence")
                val notes = doc.getString("notes")
                val isFavorite = doc.getBoolean("isFavorite") ?: false
                val masteryLevel = (doc.getLong("masteryLevel") ?: 0).toInt()
                val timesReviewed = (doc.getLong("timesReviewed") ?: 0).toInt()
                val timesCorrect = (doc.getLong("timesCorrect") ?: 0).toInt()
                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                val dateAddedString = doc.getString("dateAddedString")
                    ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(createdAt))

                remoteEntries.add(
                    VocabularyEntryEntity(
                        id = id,
                        mainFolderId = mainFolderId,
                        mainFolderName = mainFolderName,
                        subFolderId = subFolderId,
                        subFolderName = subFolderName,
                        subSubFolderId = subSubFolderId,
                        subSubFolderName = subSubFolderName,
                        originalWord = originalWord,
                        translatedWord = translatedWord,
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        articleOrGender = articleOrGender,
                        exampleSentence = exampleSentence,
                        notes = notes,
                        isFavorite = isFavorite,
                        masteryLevel = masteryLevel,
                        timesReviewed = timesReviewed,
                        timesCorrect = timesCorrect,
                        createdAt = createdAt,
                        dateAddedString = dateAddedString
                    )
                )
            }

            if (remoteEntries.isNotEmpty()) {
                vocabularyRepository.insertAllEntries(remoteEntries)
            }

            val timestamp = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
            preferences.setLastSyncTimestamp(timestamp)
            Result.success(timestamp)
        } catch (e: Exception) {
            Log.e(tag, "Sync error", e)
            Result.failure(e)
        }
    }

    // Direct single-item push to cloud
    fun syncEntry(entry: VocabularyEntryEntity) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                val entryMap = hashMapOf(
                    "id" to entry.id,
                    "mainFolderId" to entry.mainFolderId,
                    "mainFolderName" to entry.mainFolderName,
                    "subFolderId" to entry.subFolderId,
                    "subFolderName" to entry.subFolderName,
                    "subSubFolderId" to entry.subSubFolderId,
                    "subSubFolderName" to entry.subSubFolderName,
                    "originalWord" to entry.originalWord,
                    "translatedWord" to entry.translatedWord,
                    "sourceLanguage" to entry.sourceLanguage,
                    "targetLanguage" to entry.targetLanguage,
                    "articleOrGender" to entry.articleOrGender,
                    "exampleSentence" to entry.exampleSentence,
                    "notes" to entry.notes,
                    "isFavorite" to entry.isFavorite,
                    "masteryLevel" to entry.masteryLevel,
                    "timesReviewed" to entry.timesReviewed,
                    "timesCorrect" to entry.timesCorrect,
                    "createdAt" to entry.createdAt,
                    "dateAddedString" to entry.dateAddedString
                )
                firestore.collection("users").document(user.uid)
                    .collection("vocabulary_entries").document(entry.id)
                    .set(entryMap, SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to sync entry: ${e.localizedMessage}")
            }
        }
    }

    fun deleteEntryFromCloud(entryId: String) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                firestore.collection("users").document(user.uid)
                    .collection("vocabulary_entries").document(entryId).delete().await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to delete entry from cloud: ${e.localizedMessage}")
            }
        }
    }

    fun syncMainFolder(folder: MainFolderEntity) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                val map = hashMapOf(
                    "id" to folder.id,
                    "name" to folder.name,
                    "sourceLanguage" to folder.sourceLanguage,
                    "targetLanguage" to folder.targetLanguage,
                    "colorHex" to folder.colorHex,
                    "iconName" to folder.iconName,
                    "order" to folder.order,
                    "createdAt" to folder.createdAt
                )
                firestore.collection("users").document(user.uid)
                    .collection("main_folders").document(folder.id)
                    .set(map, SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to sync main folder: ${e.localizedMessage}")
            }
        }
    }

    fun deleteMainFolderFromCloud(folderId: String) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                firestore.collection("users").document(user.uid)
                    .collection("main_folders").document(folderId).delete().await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to delete main folder from cloud: ${e.localizedMessage}")
            }
        }
    }

    fun syncSubFolder(subFolder: SubFolderEntity) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                val map = hashMapOf(
                    "id" to subFolder.id,
                    "mainFolderId" to subFolder.mainFolderId,
                    "name" to subFolder.name,
                    "order" to subFolder.order,
                    "createdAt" to subFolder.createdAt
                )
                firestore.collection("users").document(user.uid)
                    .collection("sub_folders").document(subFolder.id)
                    .set(map, SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to sync sub folder: ${e.localizedMessage}")
            }
        }
    }

    fun deleteSubFolderFromCloud(subFolderId: String) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                firestore.collection("users").document(user.uid)
                    .collection("sub_folders").document(subFolderId).delete().await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to delete sub folder from cloud: ${e.localizedMessage}")
            }
        }
    }

    fun syncSubSubFolder(subSubFolder: SubSubFolderEntity) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                val map = hashMapOf(
                    "id" to subSubFolder.id,
                    "subFolderId" to subSubFolder.subFolderId,
                    "name" to subSubFolder.name,
                    "order" to subSubFolder.order,
                    "createdAt" to subSubFolder.createdAt
                )
                firestore.collection("users").document(user.uid)
                    .collection("sub_sub_folders").document(subSubFolder.id)
                    .set(map, SetOptions.merge()).await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to sync sub-sub folder: ${e.localizedMessage}")
            }
        }
    }

    fun deleteSubSubFolderFromCloud(subSubFolderId: String) {
        val user = currentUser ?: return
        syncScope.launch {
            try {
                firestore.collection("users").document(user.uid)
                    .collection("sub_sub_folders").document(subSubFolderId).delete().await()
            } catch (e: Exception) {
                Log.w(tag, "Failed to delete sub-sub folder from cloud: ${e.localizedMessage}")
            }
        }
    }

    // SyncCallback overrides
    override fun onEntryChanged(entry: VocabularyEntryEntity) = syncEntry(entry)
    override fun onEntryDeleted(entryId: String) = deleteEntryFromCloud(entryId)
    override fun onMainFolderChanged(folder: MainFolderEntity) = syncMainFolder(folder)
    override fun onMainFolderDeleted(folderId: String) = deleteMainFolderFromCloud(folderId)
    override fun onSubFolderChanged(subFolder: SubFolderEntity) = syncSubFolder(subFolder)
    override fun onSubFolderDeleted(subFolderId: String) = deleteSubFolderFromCloud(subFolderId)
    override fun onSubSubFolderChanged(subSubFolder: SubSubFolderEntity) = syncSubSubFolder(subSubFolder)
    override fun onSubSubFolderDeleted(subSubFolderId: String) = deleteSubSubFolderFromCloud(subSubFolderId)

    companion object {
        @Volatile
        private var INSTANCE: SyncRepository? = null

        fun getInstance(context: Context): SyncRepository {
            return INSTANCE ?: synchronized(this) {
                val database = AppDatabase.getInstance(context)
                val vocabularyRepository = VocabularyRepository(
                    database.mainFolderDao(),
                    database.subFolderDao(),
                    database.subSubFolderDao(),
                    database.vocabularyEntryDao()
                )
                val preferences = AppPreferences(context)
                val instance = SyncRepository(vocabularyRepository, preferences)
                INSTANCE = instance
                instance
            }
        }
    }
}


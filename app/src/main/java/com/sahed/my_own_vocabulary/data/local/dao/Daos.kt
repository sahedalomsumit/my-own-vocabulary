package com.sahed.my_own_vocabulary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubSubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MainFolderDao {
    @Query("SELECT * FROM main_folders ORDER BY `order` ASC, createdAt ASC")
    fun getAllFlow(): Flow<List<MainFolderEntity>>

    @Query("SELECT * FROM main_folders WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MainFolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: MainFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(folders: List<MainFolderEntity>)

    @Update
    suspend fun update(folder: MainFolderEntity)

    @Delete
    suspend fun delete(folder: MainFolderEntity)

    @Query("DELETE FROM main_folders WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM main_folders")
    suspend fun count(): Int

    @Query("DELETE FROM main_folders")
    suspend fun deleteAll()
}

@Dao
interface SubFolderDao {
    @Query("SELECT * FROM sub_folders ORDER BY `order` ASC, createdAt ASC")
    fun getAllFlow(): Flow<List<SubFolderEntity>>

    @Query("SELECT * FROM sub_folders WHERE mainFolderId = :mainFolderId ORDER BY `order` ASC, createdAt ASC")
    fun getByMainFolderFlow(mainFolderId: String): Flow<List<SubFolderEntity>>

    @Query("SELECT * FROM sub_folders WHERE mainFolderId = :mainFolderId ORDER BY `order` ASC, createdAt ASC")
    suspend fun getByMainFolder(mainFolderId: String): List<SubFolderEntity>

    @Query("SELECT * FROM sub_folders WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SubFolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subFolder: SubFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subFolders: List<SubFolderEntity>)

    @Update
    suspend fun update(subFolder: SubFolderEntity)

    @Delete
    suspend fun delete(subFolder: SubFolderEntity)

    @Query("DELETE FROM sub_folders WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM sub_folders")
    suspend fun deleteAll()
}

@Dao
interface SubSubFolderDao {
    @Query("SELECT * FROM sub_sub_folders ORDER BY `order` ASC, createdAt ASC")
    fun getAllFlow(): Flow<List<SubSubFolderEntity>>

    @Query("SELECT * FROM sub_sub_folders WHERE subFolderId = :subFolderId ORDER BY `order` ASC, createdAt ASC")
    fun getBySubFolderFlow(subFolderId: String): Flow<List<SubSubFolderEntity>>

    @Query("SELECT * FROM sub_sub_folders WHERE subFolderId = :subFolderId ORDER BY `order` ASC, createdAt ASC")
    suspend fun getBySubFolder(subFolderId: String): List<SubSubFolderEntity>

    @Query("SELECT * FROM sub_sub_folders WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SubSubFolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subSubFolder: SubSubFolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subSubFolders: List<SubSubFolderEntity>)

    @Update
    suspend fun update(subSubFolder: SubSubFolderEntity)

    @Delete
    suspend fun delete(subSubFolder: SubSubFolderEntity)

    @Query("DELETE FROM sub_sub_folders WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM sub_sub_folders")
    suspend fun deleteAll()
}

@Dao
interface VocabularyEntryDao {
    @Query("SELECT * FROM vocabulary_entries ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<VocabularyEntryEntity>>

    @Query("SELECT * FROM vocabulary_entries WHERE mainFolderId = :mainFolderId ORDER BY createdAt DESC")
    fun getByMainFolderFlow(mainFolderId: String): Flow<List<VocabularyEntryEntity>>

    @Query("SELECT * FROM vocabulary_entries WHERE subFolderId = :subFolderId ORDER BY createdAt DESC")
    fun getBySubFolderFlow(subFolderId: String): Flow<List<VocabularyEntryEntity>>

    @Query("SELECT * FROM vocabulary_entries WHERE subSubFolderId = :subSubFolderId ORDER BY createdAt DESC")
    fun getBySubSubFolderFlow(subSubFolderId: String): Flow<List<VocabularyEntryEntity>>

    @Query("SELECT * FROM vocabulary_entries WHERE dateAddedString = :dateString ORDER BY createdAt DESC")
    fun getByDateAddedFlow(dateString: String): Flow<List<VocabularyEntryEntity>>

    @Query("SELECT DISTINCT dateAddedString FROM vocabulary_entries")
    fun getAllDatesWithEntriesFlow(): Flow<List<String>>

    @Query("SELECT * FROM vocabulary_entries WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritesFlow(): Flow<List<VocabularyEntryEntity>>

    @Query("""
        SELECT * FROM vocabulary_entries 
        WHERE originalWord LIKE '%' || :query || '%' 
           OR translatedWord LIKE '%' || :query || '%' 
           OR exampleSentence LIKE '%' || :query || '%' 
           OR notes LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchEntriesFlow(query: String): Flow<List<VocabularyEntryEntity>>

    @Query("SELECT * FROM vocabulary_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): VocabularyEntryEntity?

    @Query("SELECT * FROM vocabulary_entries ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestEntry(): VocabularyEntryEntity?

    @Query("SELECT * FROM vocabulary_entries WHERE mainFolderId = :mainFolderId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestEntryForMainFolder(mainFolderId: String): VocabularyEntryEntity?

    @Query("SELECT * FROM vocabulary_entries WHERE subFolderId = :subFolderId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestEntryForSubFolder(subFolderId: String): VocabularyEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: VocabularyEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<VocabularyEntryEntity>)

    @Update
    suspend fun update(entry: VocabularyEntryEntity)

    @Delete
    suspend fun delete(entry: VocabularyEntryEntity)

    @Query("DELETE FROM vocabulary_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM vocabulary_entries")
    suspend fun deleteAll()

    @Query("UPDATE vocabulary_entries SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("""
        UPDATE vocabulary_entries 
        SET masteryLevel = :masteryLevel,
             timesReviewed = :timesReviewed,
             timesCorrect = :timesCorrect
        WHERE id = :id
    """)
    suspend fun recordQuizResult(
        id: String,
        masteryLevel: Int,
        timesReviewed: Int,
        timesCorrect: Int
    )

    @Query("SELECT COUNT(*) FROM vocabulary_entries")
    fun getTotalCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_entries WHERE masteryLevel = 2")
    fun getMasteredCountFlow(): Flow<Int>

    @Query("SELECT * FROM vocabulary_entries")
    suspend fun getAllSnapshot(): List<VocabularyEntryEntity>
}


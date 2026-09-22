package com.sahed.my_own_vocabulary.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sahed.my_own_vocabulary.data.local.dao.MainFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.SubFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.SubSubFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.VocabularyEntryDao
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubSubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity

@Database(
    entities = [
        MainFolderEntity::class,
        SubFolderEntity::class,
        SubSubFolderEntity::class,
        VocabularyEntryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mainFolderDao(): MainFolderDao
    abstract fun subFolderDao(): SubFolderDao
    abstract fun subSubFolderDao(): SubSubFolderDao
    abstract fun vocabularyEntryDao(): VocabularyEntryDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_own_vocabulary.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


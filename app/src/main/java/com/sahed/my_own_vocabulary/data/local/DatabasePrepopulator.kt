package com.sahed.my_own_vocabulary.data.local

import com.sahed.my_own_vocabulary.data.local.dao.MainFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.SubFolderDao
import com.sahed.my_own_vocabulary.data.local.dao.VocabularyEntryDao

object DatabasePrepopulator {
    /**
     * Vocabulary starts completely empty without pre-populated words or folders.
     */
    suspend fun populateInitialData(
        mainFolderDao: MainFolderDao,
        subFolderDao: SubFolderDao,
        vocabDao: VocabularyEntryDao
    ) {
        // No-op: Vocabulary starts empty.
    }
}


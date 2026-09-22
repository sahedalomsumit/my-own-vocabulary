package com.sahed.my_own_vocabulary.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubSubFolderEntity
import com.sahed.my_own_vocabulary.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SubFolderWithSubSubs(
    val subFolder: SubFolderEntity,
    val subSubFolders: List<SubSubFolderEntity>
)

data class MainFolderWithHierarchy(
    val folder: MainFolderEntity,
    val subFolders: List<SubFolderWithSubSubs>
)

class ManageFoldersViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = VocabularyRepository(
        database.mainFolderDao(),
        database.subFolderDao(),
        database.subSubFolderDao(),
        database.vocabularyEntryDao()
    )
    val ttsManager = com.sahed.my_own_vocabulary.util.TtsManager(application)

    val foldersWithHierarchy: StateFlow<List<MainFolderWithHierarchy>> = combine(
        repository.allMainFolders,
        repository.allSubFolders,
        repository.allSubSubFolders
    ) { mains, subs, subSubs ->
        val subSubMap = subSubs.groupBy { it.subFolderId }
        val subMap = subs.groupBy { it.mainFolderId }

        mains.map { main ->
            val mainSubs = subMap[main.id] ?: emptyList()
            MainFolderWithHierarchy(
                folder = main,
                subFolders = mainSubs.map { sub ->
                    SubFolderWithSubSubs(
                        subFolder = sub,
                        subSubFolders = subSubMap[sub.id] ?: emptyList()
                    )
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMainFolder(name: String, sourceLang: String = "de", targetLang: String = "en") {
        if (name.isBlank()) return
        viewModelScope.launch {
            val count = foldersWithHierarchy.value.size
            val folder = MainFolderEntity(
                name = name.trim(),
                sourceLanguage = sourceLang.trim().lowercase(),
                targetLanguage = targetLang.trim().lowercase(),
                order = count
            )
            repository.insertMainFolder(folder)
        }
    }

    fun updateMainFolder(folder: MainFolderEntity, newName: String, sourceLang: String, targetLang: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val updated = folder.copy(
                name = newName.trim(),
                sourceLanguage = sourceLang.trim().lowercase(),
                targetLanguage = targetLang.trim().lowercase()
            )
            repository.updateMainFolder(updated)
        }
    }

    fun deleteMainFolder(folder: MainFolderEntity) {
        viewModelScope.launch {
            repository.deleteMainFolder(folder)
        }
    }

    fun addSubFolder(mainFolderId: String, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val existingSubs = foldersWithHierarchy.value.find { it.folder.id == mainFolderId }?.subFolders ?: emptyList()
            val sub = SubFolderEntity(
                mainFolderId = mainFolderId,
                name = name.trim(),
                order = existingSubs.size
            )
            repository.insertSubFolder(sub)
        }
    }

    fun updateSubFolder(subFolder: SubFolderEntity, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val updated = subFolder.copy(name = newName.trim())
            repository.updateSubFolder(updated)
        }
    }

    fun deleteSubFolder(subFolder: SubFolderEntity) {
        viewModelScope.launch {
            repository.deleteSubFolder(subFolder)
        }
    }

    fun addSubSubFolder(subFolderId: String, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val sub = SubSubFolderEntity(
                subFolderId = subFolderId,
                name = name.trim()
            )
            repository.insertSubSubFolder(sub)
        }
    }

    fun updateSubSubFolder(subSubFolder: SubSubFolderEntity, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val updated = subSubFolder.copy(name = newName.trim())
            repository.updateSubSubFolder(updated)
        }
    }

    fun deleteSubSubFolder(subSubFolder: SubSubFolderEntity) {
        viewModelScope.launch {
            repository.deleteSubSubFolder(subSubFolder)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}

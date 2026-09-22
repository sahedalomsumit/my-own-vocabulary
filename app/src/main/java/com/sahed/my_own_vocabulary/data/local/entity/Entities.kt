package com.sahed.my_own_vocabulary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "main_folders",
    indices = [Index(value = ["order"])]
)
data class MainFolderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val sourceLanguage: String = "de",
    val targetLanguage: String = "en",
    val colorHex: String = "#2E9C7E",
    val iconName: String = "book",
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sub_folders",
    foreignKeys = [
        ForeignKey(
            entity = MainFolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["mainFolderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mainFolderId"]),
        Index(value = ["order"])
    ]
)
data class SubFolderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val mainFolderId: String,
    val name: String,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sub_sub_folders",
    foreignKeys = [
        ForeignKey(
            entity = SubFolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["subFolderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subFolderId"]),
        Index(value = ["order"])
    ]
)
data class SubSubFolderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val subFolderId: String,
    val name: String,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "vocabulary_entries",
    foreignKeys = [
        ForeignKey(
            entity = MainFolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["mainFolderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mainFolderId"]),
        Index(value = ["subFolderId"]),
        Index(value = ["subSubFolderId"]),
        Index(value = ["dateAddedString"]),
        Index(value = ["isFavorite"]),
        Index(value = ["originalWord"])
    ]
)
data class VocabularyEntryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val mainFolderId: String,
    val mainFolderName: String,
    val subFolderId: String? = null,
    val subFolderName: String? = null,
    val subSubFolderId: String? = null,
    val subSubFolderName: String? = null,
    val originalWord: String,
    val translatedWord: String,
    val sourceLanguage: String = "de",
    val targetLanguage: String = "en",
    val articleOrGender: String? = null, // e.g. "der", "die", "das", or "verb", "adj"
    val exampleSentence: String? = null,
    val notes: String? = null,
    val isFavorite: Boolean = false,
    val masteryLevel: Int = 0, // 0 = New, 1 = Learning, 2 = Mastered
    val timesReviewed: Int = 0,
    val timesCorrect: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val dateAddedString: String // Format: "yyyy-MM-dd" for instant calendar lookups
)

/**
 * Returns text formatted for pronunciation, prepending the article (e.g., "das Auto", "der Tisch")
 * if an article/gender is specified and not already part of the word.
 */
fun VocabularyEntryEntity.getPronunciationText(): String {
    val art = articleOrGender?.trim()
    val word = originalWord.trim()
    return if (!art.isNullOrBlank() &&
        !art.equals("verb", ignoreCase = true) &&
        !art.equals("adj", ignoreCase = true) &&
        !art.equals("other", ignoreCase = true) &&
        !word.startsWith(art, ignoreCase = true)
    ) {
        "$art $word"
    } else {
        word
    }
}


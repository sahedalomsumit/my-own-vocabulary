package com.sahed.my_own_vocabulary.util

import com.sahed.my_own_vocabulary.data.local.entity.VocabularyEntryEntity
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object JsonBackupHelper {
    fun exportToJson(entries: List<VocabularyEntryEntity>): String {
        val jsonArray = JSONArray()
        for (entry in entries) {
            val obj = JSONObject().apply {
                put("id", entry.id)
                put("mainFolderId", entry.mainFolderId)
                put("mainFolderName", entry.mainFolderName)
                put("subFolderId", entry.subFolderId ?: JSONObject.NULL)
                put("subFolderName", entry.subFolderName ?: JSONObject.NULL)
                put("originalWord", entry.originalWord)
                put("translatedWord", entry.translatedWord)
                put("sourceLanguage", entry.sourceLanguage)
                put("targetLanguage", entry.targetLanguage)
                put("articleOrGender", entry.articleOrGender ?: JSONObject.NULL)
                put("exampleSentence", entry.exampleSentence ?: JSONObject.NULL)
                put("notes", entry.notes ?: JSONObject.NULL)
                put("isFavorite", entry.isFavorite)
                put("masteryLevel", entry.masteryLevel)
                put("timesReviewed", entry.timesReviewed)
                put("timesCorrect", entry.timesCorrect)
                put("createdAt", entry.createdAt)
                put("dateAddedString", entry.dateAddedString)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    fun importFromJson(jsonString: String): List<VocabularyEntryEntity> {
        val list = mutableListOf<VocabularyEntryEntity>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val entry = VocabularyEntryEntity(
                id = obj.optString("id", UUID.randomUUID().toString()),
                mainFolderId = obj.optString("mainFolderId", ""),
                mainFolderName = obj.optString("mainFolderName", "General"),
                subFolderId = if (obj.isNull("subFolderId")) null else obj.optString("subFolderId"),
                subFolderName = if (obj.isNull("subFolderName")) null else obj.optString("subFolderName"),
                originalWord = obj.optString("originalWord", ""),
                translatedWord = obj.optString("translatedWord", ""),
                sourceLanguage = obj.optString("sourceLanguage", "de"),
                targetLanguage = obj.optString("targetLanguage", "en"),
                articleOrGender = if (obj.isNull("articleOrGender")) null else obj.optString("articleOrGender"),
                exampleSentence = if (obj.isNull("exampleSentence")) null else obj.optString("exampleSentence"),
                notes = if (obj.isNull("notes")) null else obj.optString("notes"),
                isFavorite = obj.optBoolean("isFavorite", false),
                masteryLevel = obj.optInt("masteryLevel", 0),
                timesReviewed = obj.optInt("timesReviewed", 0),
                timesCorrect = obj.optInt("timesCorrect", 0),
                createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                dateAddedString = obj.optString("dateAddedString", "")
            )
            if (entry.originalWord.isNotBlank()) {
                list.add(entry)
            }
        }
        return list
    }
}

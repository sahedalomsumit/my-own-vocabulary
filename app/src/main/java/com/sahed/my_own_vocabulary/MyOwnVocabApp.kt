package com.sahed.my_own_vocabulary

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.sahed.my_own_vocabulary.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyOwnVocabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder().build()
                )
                .build()
            firestore.firestoreSettings = settings
        } catch (e: Exception) {
            Log.e("MyOwnVocabApp", "Firebase initialization deferred or offline: ${e.message}")
        }

        // Initialize and seed Room database asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.getInstance(this@MyOwnVocabApp).mainFolderDao().count()
            } catch (e: Exception) {
                Log.e("MyOwnVocabApp", "Database warm-up failed: ${e.message}")
            }
        }
    }
}

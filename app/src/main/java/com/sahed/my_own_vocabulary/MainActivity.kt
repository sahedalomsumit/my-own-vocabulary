package com.sahed.my_own_vocabulary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import com.sahed.my_own_vocabulary.data.preferences.ThemeMode
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldDesignTheme
import com.sahed.my_own_vocabulary.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = AppPreferences(applicationContext)

        setContent {
            val themeMode by preferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.DARK)

            EmeraldDesignTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()
                }
            }
        }
    }
}

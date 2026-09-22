package com.sahed.my_own_vocabulary.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {
    private val tag = "TtsManager"
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var focusRequest: AudioFocusRequest? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(tag, "Failed to instantiate TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    abandonAudioFocus()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    abandonAudioFocus()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    abandonAudioFocus()
                    Log.w(tag, "TTS utterance error code: $errorCode")
                }
            })
        } else {
            isInitialized = false
            Log.w(tag, "TTS initialization failed with code: $status")
        }
    }

    fun speak(
        text: String,
        languageCode: String,
        speechRate: Float = 1.0f,
        pitch: Float = 1.0f
    ) {
        if (!isInitialized || textToSpeech == null) {
            Log.w(tag, "TTS not yet initialized")
            return
        }

        // Request audio focus so other audio pauses or ducks
        requestAudioFocus()

        val locale = resolveLocale(languageCode)
        textToSpeech?.language = locale
        textToSpeech?.setSpeechRate(speechRate.coerceIn(0.5f, 2.0f))
        textToSpeech?.setPitch(pitch.coerceIn(0.5f, 2.0f))

        // Strip articles for German speech if requested, or speak the full text
        val utteranceId = "vocab_${System.currentTimeMillis()}"
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
        abandonAudioFocus()
    }

    fun shutdown() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }

    private fun requestAudioFocus() {
        if (audioManager == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(playbackAttributes)
                .build()
            focusRequest = request
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
        }
    }

    private fun abandonAudioFocus() {
        if (audioManager == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    companion object {
        fun resolveLocale(langCode: String): Locale {
            val normalized = langCode.trim().lowercase()
            return when {
                normalized.startsWith("de") || normalized == "german" || normalized == "deutsch" -> Locale.GERMAN
                normalized.startsWith("en") || normalized == "english" -> Locale.ENGLISH
                normalized.startsWith("fr") || normalized == "french" || normalized == "français" -> Locale.FRENCH
                normalized.startsWith("es") || normalized == "spanish" || normalized == "español" -> Locale.forLanguageTag("es-ES")
                normalized.startsWith("it") || normalized == "italian" || normalized == "italiano" -> Locale.ITALIAN
                normalized.startsWith("pt") || normalized == "portuguese" -> Locale.forLanguageTag("pt-PT")
                normalized.startsWith("nl") || normalized == "dutch" -> Locale.forLanguageTag("nl-NL")
                normalized.startsWith("ja") || normalized == "japanese" -> Locale.JAPANESE
                normalized.startsWith("ko") || normalized == "korean" -> Locale.KOREAN
                normalized.startsWith("zh") || normalized == "chinese" -> Locale.CHINESE
                normalized.startsWith("ru") || normalized == "russian" -> Locale.forLanguageTag("ru-RU")
                normalized.startsWith("tr") || normalized == "turkish" -> Locale.forLanguageTag("tr-TR")
                normalized.startsWith("ar") || normalized == "arabic" -> Locale.forLanguageTag("ar")
                normalized.startsWith("bn") || normalized == "bengali" -> Locale.forLanguageTag("bn-BD")
                normalized.startsWith("hi") || normalized == "hindi" -> Locale.forLanguageTag("hi-IN")
                normalized.startsWith("pl") || normalized == "polish" -> Locale.forLanguageTag("pl-PL")
                normalized.startsWith("sv") || normalized == "swedish" -> Locale.forLanguageTag("sv-SE")
                normalized.startsWith("no") || normalized == "norwegian" -> Locale.forLanguageTag("no-NO")
                normalized.startsWith("da") || normalized == "danish" -> Locale.forLanguageTag("da-DK")
                normalized.startsWith("fi") || normalized == "finnish" -> Locale.forLanguageTag("fi-FI")
                normalized.startsWith("el") || normalized == "greek" -> Locale.forLanguageTag("el-GR")
                normalized.startsWith("vi") || normalized == "vietnamese" -> Locale.forLanguageTag("vi-VN")
                normalized.startsWith("id") || normalized == "indonesian" -> Locale.forLanguageTag("id-ID")
                normalized.startsWith("th") || normalized == "thai" -> Locale.forLanguageTag("th-TH")
                normalized.startsWith("he") || normalized == "hebrew" -> Locale.forLanguageTag("he-IL")
                else -> Locale.forLanguageTag(langCode)
            }
        }
    }
}

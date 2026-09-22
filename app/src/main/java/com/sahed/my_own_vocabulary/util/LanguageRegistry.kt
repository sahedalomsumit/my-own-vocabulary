package com.sahed.my_own_vocabulary.util

data class SupportedLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val country: String,
    val flagEmoji: String,
    val greetingSample: String = "Hello!"
)

object LanguageRegistry {
    val languages: List<SupportedLanguage> = listOf(
        SupportedLanguage("de", "German", "Deutsch", "Germany", "🇩🇪", "Hallo! Willkommen."),
        SupportedLanguage("en", "English", "English", "United Kingdom / USA", "🇬🇧", "Hello! Welcome."),
        SupportedLanguage("es", "Spanish", "Español", "Spain / Mexico", "🇪🇸", "¡Hola! Bienvenido."),
        SupportedLanguage("fr", "French", "Français", "France", "🇫🇷", "Bonjour! Bienvenue."),
        SupportedLanguage("it", "Italian", "Italiano", "Italy", "🇮🇹", "Ciao! Benvenuto."),
        SupportedLanguage("pt", "Portuguese", "Português", "Portugal / Brazil", "🇵🇹", "Olá! Bem-vindo."),
        SupportedLanguage("nl", "Dutch", "Nederlands", "Netherlands", "🇳🇱", "Hallo! Welkom."),
        SupportedLanguage("ru", "Russian", "Русский", "Russia", "🇷🇺", "Привет! Добро пожаловать."),
        SupportedLanguage("tr", "Turkish", "Türkçe", "Turkey", "🇹🇷", "Merhaba! Hoş geldiniz."),
        SupportedLanguage("pl", "Polish", "Polski", "Poland", "🇵🇱", "Cześć! Witamy."),
        SupportedLanguage("sv", "Swedish", "Svenska", "Sweden", "🇸🇪", "Hej! Välkommen."),
        SupportedLanguage("no", "Norwegian", "Norsk", "Norway", "🇳🇴", "Hei! Velkommen."),
        SupportedLanguage("da", "Danish", "Dansk", "Denmark", "🇩🇰", "Hej! Velkommen."),
        SupportedLanguage("fi", "Finnish", "Suomi", "Finland", "🇫🇮", "Hei! Tervetuloa."),
        SupportedLanguage("el", "Greek", "Ελληνικά", "Greece", "🇬🇷", "Γεια σας! Καλώς ήρθατε."),
        SupportedLanguage("cs", "Czech", "Čeština", "Czech Republic", "🇨🇿", "Ahoj! Vítejte."),
        SupportedLanguage("hu", "Hungarian", "Magyar", "Hungary", "🇭🇺", "Szia! Üdvözöljük."),
        SupportedLanguage("ro", "Romanian", "Română", "Romania", "🇷🇴", "Salut! Bun venit."),
        SupportedLanguage("uk", "Ukrainian", "Українська", "Ukraine", "🇺🇦", "Привіт! Ласкаво просимо."),
        SupportedLanguage("ja", "Japanese", "日本語", "Japan", "🇯🇵", "こんにちは！ようこそ。"),
        SupportedLanguage("ko", "Korean", "한국어", "South Korea", "🇰🇷", "안녕하세요! 환영합니다."),
        SupportedLanguage("zh", "Chinese", "中文", "China", "🇨🇳", "你好！欢迎。"),
        SupportedLanguage("ar", "Arabic", "العربية", "Saudi Arabia / UAE", "🇸🇦", "مرحباً! أهلاً وسهلاً."),
        SupportedLanguage("bn", "Bengali", "বাংলা", "Bangladesh", "🇧🇩", "হ্যালো! স্বাগতম।"),
        SupportedLanguage("hi", "Hindi", "हिन्दी", "India", "🇮🇳", "नमस्ते! आपका स्वागत है।"),
        SupportedLanguage("vi", "Vietnamese", "Tiếng Việt", "Vietnam", "🇻🇳", "Xin chào! Chào mừng."),
        SupportedLanguage("id", "Indonesian", "Bahasa Indonesia", "Indonesia", "🇮🇩", "Halo! Selamat datang."),
        SupportedLanguage("th", "Thai", "ไทย", "Thailand", "🇹🇭", "สวัสดี! ยินดีต้อนรับ"),
        SupportedLanguage("he", "Hebrew", "עברית", "Israel", "🇮🇱", "שלום! ברוכים הבאים.")
    )

    fun findByCode(code: String?): SupportedLanguage? {
        if (code == null) return null
        val clean = code.trim().lowercase()
        return languages.find { it.code == clean }
    }

    fun getFlagForCode(code: String?): String {
        return findByCode(code)?.flagEmoji ?: "🌐"
    }

    fun search(query: String): List<SupportedLanguage> {
        if (query.isBlank()) return languages
        val q = query.trim().lowercase()
        return languages.filter {
            it.name.lowercase().contains(q) ||
            it.nativeName.lowercase().contains(q) ||
            it.country.lowercase().contains(q) ||
            it.code.lowercase().contains(q)
        }
    }
}

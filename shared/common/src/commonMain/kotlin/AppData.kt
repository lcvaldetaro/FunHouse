package com.funhouse.shared.common

import kotlin.jvm.JvmStatic

object AppData {
    @JvmStatic var isListening = false
    @JvmStatic var voiceService: Any? = null
    @JvmStatic var applicationContext: Any? = null
    @JvmStatic var appPackage = ""
    @JvmStatic var appName = "Default App Name"
    @JvmStatic var releaseVersion = true
    @JvmStatic var version = ""
    @JvmStatic var versionCode = 0L
    @JvmStatic var ttsHandle: Any? = null
    @JvmStatic var gameFolder = "funhouse"
    @JvmStatic var gameFolderFile: Any? = null
    @JvmStatic var packageFolder = ""
    @JvmStatic var packageFolderFile: Any? = null
    @JvmStatic var secretGamesEnabled = false
    @JvmStatic var darkMode = false
}

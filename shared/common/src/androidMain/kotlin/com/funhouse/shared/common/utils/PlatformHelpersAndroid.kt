package com.funhouse.shared.common.utils

import android.speech.tts.TextToSpeech
import com.funhouse.shared.common.AppData
import java.io.File
import androidx.compose.ui.graphics.asImageBitmap

actual fun speakText(text: String) {
    val tts = AppData.ttsHandle as? TextToSpeech
    tts?.speak(text, TextToSpeech.QUEUE_ADD, null)
}

actual fun readAssetFile(fileName: String): String? {
    try {
        val ctx = AppData.applicationContext as? android.content.Context ?: return null
        val assetManager = ctx.assets
        val stream = try {
            assetManager.open(fileName)
        } catch (e: Exception) {
            try {
                assetManager.open("files/$fileName")
            } catch (e2: Exception) {
                try {
                    assetManager.open("composeResources/funhousemultiplatform.shared.common.generated.resources/files/$fileName")
                } catch (e3: Exception) {
                    assetManager.open("composeResources/com.funhouse.shared.common.generated.resources/files/$fileName")
                }
            }
        }
        return stream.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        return null
    }
}

actual fun saveTextToFile(fileName: String, text: String) {
    try {
        val packageFolder = AppData.packageFolder
        val destFile = File(packageFolder, fileName)
        destFile.parentFile?.mkdirs()
        destFile.writeText(text)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

actual fun readTextFromFile(fileName: String): String? {
    try {
        val packageFolder = AppData.packageFolder
        val file = File(packageFolder, fileName)
        if (file.exists()) {
            return file.readText()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

actual fun installFile(name: String, overwrite: Boolean) {
    try {
        val destFolder = AppData.gameFolderFile as File
        if (!destFolder.exists()) {
            destFolder.mkdirs()
        }
        val destFile = File(destFolder, name)
        if (destFile.exists() && !overwrite) {
            return
        }
        val ctx = AppData.applicationContext as? android.content.Context ?: return
        val assetManager = ctx.assets
        val stream = try {
            assetManager.open(name)
        } catch (e: Exception) {
            try {
                assetManager.open("files/$name")
            } catch (e2: Exception) {
                try {
                    assetManager.open("composeResources/funhousemultiplatform.shared.common.generated.resources/files/$name")
                } catch (e3: Exception) {
                    try {
                        assetManager.open("composeResources/com.funhouse.shared.common.generated.resources/files/$name")
                    } catch (e4: Exception) {
                        assetManager.open("composeResources/com.funhouse.shared.common.generated.resources/drawable/$name")
                    }
                }
            }
        }
        stream.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        club.gepetto.GcLog.d("File $name installed successfully on Android (binary)")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

actual fun loadImageBitmapFromFile(fileName: String): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        val file = File(AppData.gameFolderFile as File, fileName)
        if (!file.exists()) return null
        val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

actual fun stopGameThread() {
    // No-op, managed inside subclass stop method natively on JVM
}

actual fun appendTerminalText(text: String) {}
actual fun getLatestTerminalText(): String = ""
actual fun clearTerminalText() {}

actual val isWebTarget: Boolean = false
actual val isLocalWebSocketSupported: Boolean = true





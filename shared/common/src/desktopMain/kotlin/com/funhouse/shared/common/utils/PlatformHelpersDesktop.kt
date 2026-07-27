package com.funhouse.shared.common.utils

import com.funhouse.shared.common.AppData
import java.io.File
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

private val ttsExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()

actual fun speakText(text: String) {
    if (text.isBlank()) return
    ttsExecutor.submit {
        try {
            val osName = System.getProperty("os.name").lowercase()
            val pb = when {
                osName.contains("mac") -> {
                    ProcessBuilder("say", text)
                }
                osName.contains("win") -> {
                    val escapedText = text.replace("'", "''")
                    ProcessBuilder(
                        "powershell",
                        "-Command",
                        "Add-Type -AssemblyName System.Speech; \$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; \$synth.Speak('$escapedText')"
                    )
                }
                else -> {
                    ProcessBuilder("spd-say", text)
                }
            }
            pb.redirectErrorStream(true)
            val process = pb.start()
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

actual fun readAssetFile(fileName: String): String? {
    try {
        val resource = AppData::class.java.classLoader.getResourceAsStream(fileName)
            ?: AppData::class.java.classLoader.getResourceAsStream("files/$fileName")
            ?: AppData::class.java.classLoader.getResourceAsStream("composeResources/com.funhouse.shared.common.generated.resources/files/$fileName")
            ?: return null
        return resource.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        return null
    }
}

actual fun saveTextToFile(fileName: String, text: String) {
    try {
        val packageFolder = AppData.packageFolder
        val destFile = File(packageFolder, fileName)
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
        val resourceStream = AppData::class.java.classLoader.getResourceAsStream(name)
            ?: AppData::class.java.classLoader.getResourceAsStream("files/$name")
            ?: AppData::class.java.classLoader.getResourceAsStream("composeResources/com.funhouse.shared.common.generated.resources/files/$name")
            ?: AppData::class.java.classLoader.getResourceAsStream("composeResources/com.funhouse.shared.common.generated.resources/drawable/$name")
            ?: return
        resourceStream.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        club.gepetto.GcLog.d("File $name installed successfully on Desktop (binary)")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

actual fun loadImageBitmapFromFile(fileName: String): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        val file = File(AppData.gameFolderFile as File, fileName)
        if (!file.exists()) return null
        val bytes = file.readBytes()
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
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





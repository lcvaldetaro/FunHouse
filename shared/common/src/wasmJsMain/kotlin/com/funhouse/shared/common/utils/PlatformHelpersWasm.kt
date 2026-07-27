package com.funhouse.shared.common.utils

import kotlinx.browser.window

actual fun speakText(text: String): Unit = js("""{
    if (text && text.trim() !== '') {
        if ('speechSynthesis' in window) {
            var utterance = new SpeechSynthesisUtterance(text);
            window.speechSynthesis.speak(utterance);
        }
    }
}""")

actual fun readAssetFile(fileName: String): String? {
    return AssetCache.cache[fileName]
}

actual fun saveTextToFile(fileName: String, text: String) {
    window.localStorage.setItem(fileName, text)
}

actual fun readTextFromFile(fileName: String): String? {
    return window.localStorage.getItem(fileName)
}

actual fun installFile(name: String, overwrite: Boolean) {
    // No-op on Web target
}

actual fun loadImageBitmapFromFile(fileName: String): androidx.compose.ui.graphics.ImageBitmap? = null

actual fun stopGameThread() {
    kotlin.Thread.interruptAll()
}

private val terminalLines = mutableListOf<String>()

actual fun appendTerminalText(text: String) {
    terminalLines.addAll(text.lines().map { it.trim() }.filter { it.isNotEmpty() })
    while (terminalLines.size > 20) {
        terminalLines.removeAt(0)
    }
}

actual fun getLatestTerminalText(): String {
    return terminalLines.joinToString("\n")
}

actual fun clearTerminalText() {
    terminalLines.clear()
}

actual val isWebTarget: Boolean = true

fun isLocalWebSocketSupportedJs(): Boolean = js(
    """
    (function() {
        if (window.location.protocol !== 'https:') {
            return true;
        }
        try {
            var ws = new WebSocket('ws://127.0.0.1:9999');
            ws.close();
            return true;
        } catch (e) {
            return false;
        }
    })()
    """
)

actual val isLocalWebSocketSupported: Boolean = isLocalWebSocketSupportedJs()




package com.funhouse.shared.common.utils

expect fun speakText(text: String)
expect fun readAssetFile(fileName: String): String?

expect fun saveTextToFile(fileName: String, text: String)
expect fun readTextFromFile(fileName: String): String?

expect fun installFile(name: String, overwrite: Boolean = true)

expect fun loadImageBitmapFromFile(fileName: String): androidx.compose.ui.graphics.ImageBitmap?

expect fun stopGameThread()

expect fun appendTerminalText(text: String)
expect fun getLatestTerminalText(): String
expect fun clearTerminalText()

expect val isWebTarget: Boolean
expect val isLocalWebSocketSupported: Boolean

class GcInputQueue<T> {
    private val channel = kotlinx.coroutines.channels.Channel<T>(kotlinx.coroutines.channels.Channel.UNLIMITED)
    
    fun put(element: T) {
        channel.trySend(element)
    }
    
    suspend fun take(): T {
        return channel.receive()
    }
    
    fun clear() {
        while (true) {
            val result = channel.tryReceive()
            if (result.isFailure || result.isClosed) break
        }
    }
}



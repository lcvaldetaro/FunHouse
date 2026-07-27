package com.funhouse.feature.funhouseenginekotlin.net

import org.w3c.dom.WebSocket
import kotlinx.browser.window
import kotlinx.serialization.json.Json

actual class GameSocketClient actual constructor() {
    private var client: WebSocket? = null
    private var pingIntervalId: Int = -1
    
    actual fun connect(
        host: String,
        port: Int,
        onMessage: (message: String) -> Unit,
        onOpen: () -> Unit,
        onClose: () -> Unit
    ) {
        val ws = WebSocket("ws://$host:$port/game")
        client = ws
        
        val clearPing = {
            if (pingIntervalId != -1) {
                window.clearInterval(pingIntervalId)
                pingIntervalId = -1
            }
        }
        
        ws.onopen = {
            onOpen()
            clearPing()
            pingIntervalId = window.setInterval({
                try {
                    send(Json.encodeToString(NetworkMessage.serializer(), NetworkMessage.Ping))
                } catch (e: Exception) {
                    club.gepetto.GcLog.e("Failed to send ping", e)
                }
                null
            }, 10000)
        }
        ws.onmessage = { event ->
            val data = event.data
            if (data != null) {
                val str = data.unsafeCast<kotlin.js.JsString>().toString()
                onMessage(str)
            }
        }
        ws.onclose = {
            clearPing()
            onClose()
        }
        ws.onerror = {
            clearPing()
            onClose()
        }
    }
    
    actual fun send(message: String) {
        client?.send(message)
    }
    
    actual fun close() {
        if (pingIntervalId != -1) {
            window.clearInterval(pingIntervalId)
            pingIntervalId = -1
        }
        client?.close()
    }
}

actual class GameSocketServer actual constructor() {
    actual fun start(
        port: Int,
        onMessage: (session: String, message: String) -> Unit,
        onConnect: (session: String) -> Unit,
        onClose: (session: String) -> Unit
    ) {
        // Stub: Browsers cannot host websocket servers
    }
    actual fun sendToSession(session: String, message: String) {}
    actual fun closeSession(session: String) {}
    actual fun stop() {}
}

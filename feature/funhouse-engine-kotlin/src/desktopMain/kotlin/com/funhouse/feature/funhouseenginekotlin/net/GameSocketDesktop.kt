package com.funhouse.feature.funhouseenginekotlin.net

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.URI
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

actual class GameSocketClient actual constructor() {
    private var client: WebSocketClient? = null
    
    actual fun connect(
        host: String,
        port: Int,
        onMessage: (message: String) -> Unit,
        onOpen: () -> Unit,
        onClose: () -> Unit
    ) {
        val uri = URI("ws://$host:$port/game")
        client = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                onOpen()
            }
            override fun onMessage(message: String?) {
                if (message != null) {
                    onMessage(message)
                }
            }
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                onClose()
            }
            override fun onError(ex: Exception?) {
                ex?.printStackTrace()
            }
        }
        client?.connectionLostTimeout = 10
        client?.connect()
    }
    
    actual fun send(message: String) {
        client?.send(message)
    }
    
    actual fun close() {
        client?.close()
    }
}

actual class GameSocketServer actual constructor() {
    private var server: WebSocketServer? = null
    private val sessionsMap = ConcurrentHashMap<String, WebSocket>()
    
    actual fun start(
        port: Int,
        onMessage: (session: String, message: String) -> Unit,
        onConnect: (session: String) -> Unit,
        onClose: (session: String) -> Unit
    ) {
        val address = InetSocketAddress(port)
        server = object : WebSocketServer(address) {
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                if (conn != null) {
                    val sessionId = conn.hashCode().toString()
                    sessionsMap[sessionId] = conn
                    onConnect(sessionId)
                }
            }
            
            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                if (conn != null) {
                    val sessionId = conn.hashCode().toString()
                    sessionsMap.remove(sessionId)
                    onClose(sessionId)
                }
            }
            
            override fun onMessage(conn: WebSocket?, message: String?) {
                if (conn != null && message != null) {
                    val sessionId = conn.hashCode().toString()
                    onMessage(sessionId, message)
                }
            }
            
            override fun onError(conn: WebSocket?, ex: java.lang.Exception?) {
                ex?.printStackTrace()
            }
            
            override fun onStart() {
                club.gepetto.GcLog.d("WebSocket server started on port $port")
            }
        }
        server?.connectionLostTimeout = 10
        server?.start()
    }
    
    actual fun sendToSession(session: String, message: String) {
        sessionsMap[session]?.send(message)
    }
    
    actual fun closeSession(session: String) {
        sessionsMap[session]?.close()
    }
    
    actual fun stop() {
        server?.stop()
    }
}

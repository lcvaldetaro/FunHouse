package com.funhouse.feature.funhouseenginekotlin.net

expect class GameSocketClient() {
    fun connect(
        host: String,
        port: Int,
        onMessage: (message: String) -> Unit,
        onOpen: () -> Unit,
        onClose: () -> Unit
    )
    fun send(message: String)
    fun close()
}

expect class GameSocketServer() {
    fun start(
        port: Int,
        onMessage: (session: String, message: String) -> Unit,
        onConnect: (session: String) -> Unit,
        onClose: (session: String) -> Unit
    )
    fun sendToSession(session: String, message: String)
    fun closeSession(session: String)
    fun stop()
}

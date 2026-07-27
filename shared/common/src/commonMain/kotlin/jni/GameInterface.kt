package com.funhouse.shared.common.jni

interface GameInterface {
    fun sendCommand(command: String): Int
    fun start()
    fun start(gameNickName: String)
    fun start(gameNickName: String, isMultiplayer: Boolean) { start(gameNickName) }
    fun registerTerminalCallback(callback: TerminalDataCallback)
    fun stop() {}

    companion object {
        fun loadLibrary() {}
        fun reload() {}
        fun writeTerminalData(data: String) {}
    }
}

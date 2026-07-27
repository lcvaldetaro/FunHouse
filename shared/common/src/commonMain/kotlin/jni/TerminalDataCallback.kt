package com.funhouse.shared.common.jni

fun interface TerminalDataCallback {
    fun onNewTerminalDataReceived(newData: String)
}

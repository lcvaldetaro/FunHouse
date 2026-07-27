package com.funhouse.feature.funhouseenginekotlin.net

expect class DiscoveryHelper() {
    var onHostDiscovered: ((String, Int) -> Unit)?
    fun registerService(gameNickName: String, hostNickname: String, port: Int)
    fun unregisterService()
    fun discoverServices(targetGameNickName: String)
    fun stopDiscovery()
}

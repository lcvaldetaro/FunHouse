package com.funhouse.feature.funhouseenginekotlin.net

actual class DiscoveryHelper actual constructor() {
    actual var onHostDiscovered: ((String, Int) -> Unit)? = null
    actual fun registerService(gameNickName: String, hostNickname: String, port: Int) {}
    actual fun unregisterService() {}
    actual fun discoverServices(targetGameNickName: String) {}
    actual fun stopDiscovery() {}
}

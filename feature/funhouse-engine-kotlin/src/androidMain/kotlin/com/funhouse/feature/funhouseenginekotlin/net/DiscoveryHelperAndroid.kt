package com.funhouse.feature.funhouseenginekotlin.net

import com.funhouse.shared.common.AppData

actual class DiscoveryHelper actual constructor() {
    private val nsd = NsdHelper(AppData.applicationContext as android.content.Context)
    
    actual var onHostDiscovered: ((String, Int) -> Unit)?
        get() = nsd.onHostDiscovered
        set(value) { nsd.onHostDiscovered = value }
        
    actual fun registerService(gameNickName: String, hostNickname: String, port: Int) {
        nsd.registerService(gameNickName, hostNickname, port)
    }
    
    actual fun unregisterService() {
        nsd.unregisterService()
    }
    
    actual fun discoverServices(targetGameNickName: String) {
        nsd.discoverServices(targetGameNickName)
    }
    
    actual fun stopDiscovery() {
        nsd.stopDiscovery()
    }
}

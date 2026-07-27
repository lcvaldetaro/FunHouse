package com.funhouse.feature.funhouseenginekotlin.net

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import club.gepetto.GcLog

class NsdHelper(private val context: Context) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    private var multicastLock: WifiManager.MulticastLock? = null

    private fun acquireMulticastLock() {
        if (multicastLock == null) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifiManager != null) {
                multicastLock = wifiManager.createMulticastLock("FunHouseNsdMulticastLock").apply {
                    setReferenceCounted(false)
                    try {
                        acquire()
                        GcLog.d("NSD MulticastLock acquired")
                    } catch (e: Exception) {
                        GcLog.e("Failed to acquire MulticastLock: ${e.message}")
                    }
                }
            }
        }
    }

    private fun releaseMulticastLock() {
        multicastLock?.let {
            try {
                if (it.isHeld) {
                    it.release()
                    GcLog.d("NSD MulticastLock released")
                }
            } catch (e: Exception) {
                GcLog.e("Failed to release MulticastLock: ${e.message}")
            }
        }
        multicastLock = null
    }

    // Callback when a service is resolved
    var onHostDiscovered: ((String, Int) -> Unit)? = null

    fun registerService(gameNickName: String, hostNickname: String, port: Int) {
        GcLog.d("NsdHelper.registerService() called with gameNickName=$gameNickName, hostNickname=$hostNickname, port=$port")
        acquireMulticastLock()
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "${gameNickName}_$hostNickname"
            serviceType = "_funhouse-game._tcp."
            setPort(port)
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                GcLog.d("NSD Service registered: ${NsdServiceInfo.serviceName}")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                GcLog.e("NSD Registration failed: $errorCode")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                GcLog.d("NSD Service unregistered: ${arg0.serviceName}")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                GcLog.e("NSD Unregistration failed: $errorCode")
            }
        }

        try {
            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        } catch (e: Exception) {
            GcLog.e("Failed to register NSD service", e)
        }
    }

    fun unregisterService() {
        GcLog.d("NsdHelper.unregisterService() called")
        registrationListener?.let {
            try {
                nsdManager.unregisterService(it)
            } catch (e: Exception) {
                GcLog.e("Failed to unregister NSD service", e)
            }
            registrationListener = null
        }
        releaseMulticastLock()
    }

    fun discoverServices(targetGameNickName: String) {
        GcLog.d("NsdHelper.discoverServices() called with targetGameNickName=$targetGameNickName")
        acquireMulticastLock()
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                GcLog.e("NSD Discovery failed to start: $errorCode")
                try {
                    nsdManager.stopServiceDiscovery(this)
                } catch (e: Exception) {}
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                GcLog.e("NSD Discovery failed to stop: $errorCode")
            }

            override fun onDiscoveryStarted(serviceType: String) {
                GcLog.d("NSD Discovery started")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                GcLog.d("NSD Discovery stopped")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                GcLog.d("NSD Service found: ${serviceInfo.serviceName}")
                val type = serviceInfo.serviceType
                if (type.contains("_funhouse-game")) {
                    if (serviceInfo.serviceName.startsWith("${targetGameNickName}_")) {
                        resolveService(serviceInfo)
                    }
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                GcLog.d("NSD Service lost: ${serviceInfo.serviceName}")
            }
        }

        try {
            nsdManager.discoverServices("_funhouse-game._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (e: Exception) {
            GcLog.e("Failed to discover NSD services", e)
        }
    }

    fun stopDiscovery() {
        GcLog.d("NsdHelper.stopDiscovery() called")
        discoveryListener?.let {
            try {
                nsdManager.stopServiceDiscovery(it)
            } catch (e: Exception) {
                GcLog.e("Failed to stop NSD discovery", e)
            }
            discoveryListener = null
        }
        releaseMulticastLock()
    }

    private fun resolveService(serviceInfo: NsdServiceInfo) {
        GcLog.d("NsdHelper.resolveService() called for serviceName=${serviceInfo.serviceName}")
        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                GcLog.e("NSD Resolve failed: $errorCode")
            }

            override fun onServiceResolved(resolvedServiceInfo: NsdServiceInfo) {
                GcLog.d("NSD Service resolved: ${resolvedServiceInfo.serviceName}")
                val hostAddress = resolvedServiceInfo.host.hostAddress
                val hostPort = resolvedServiceInfo.port
                if (hostAddress != null) {
                    onHostDiscovered?.invoke(hostAddress, hostPort)
                }
            }
        }
        try {
            nsdManager.resolveService(serviceInfo, resolveListener)
        } catch (e: Exception) {
            GcLog.e("Failed to resolve NSD service", e)
        }
    }
}

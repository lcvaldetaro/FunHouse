package com.funhouse.feature.funhouseenginekotlin.util

actual class GcConcurrentMap<K, V> actual constructor() {
    private val map = java.util.concurrent.ConcurrentHashMap<K, V>()
    actual fun put(key: K, value: V): V? = map.put(key, value)
    actual fun remove(key: K): V? = map.remove(key)
    actual operator fun get(key: K): V? = map[key]
    actual operator fun set(key: K, value: V) {
        map[key] = value
    }
    actual fun clear() = map.clear()
    actual fun containsKey(key: K): Boolean = map.containsKey(key)
    actual val values: Collection<V> get() = map.values
    actual val entries: Set<Map.Entry<K, V>> get() = map.entries
    actual fun forEach(action: (Map.Entry<K, V>) -> Unit) = map.entries.forEach(action)
    actual fun getOrPut(key: K, defaultValue: () -> V): V {
        return map.getOrPut(key, defaultValue)
    }
    actual operator fun iterator(): Iterator<Map.Entry<K, V>> {
        return map.iterator()
    }
}

actual class GcQueue<T> actual constructor() {
    private val queue = java.util.concurrent.LinkedBlockingQueue<T>()
    actual fun put(element: T) {
        queue.put(element)
    }
    actual suspend fun take(): T {
        return queue.take()
    }
    actual fun poll(): T? {
        return queue.poll()
    }
    actual fun clear() {
        queue.clear()
    }
}

actual class GcThreadLocal<T> actual constructor() {
    private val local = ThreadLocal<T>()
    actual fun get(): T? = local.get()
    actual fun set(value: T) = local.set(value)
}

actual fun <T> gcThreadLocal(initial: () -> T): GcThreadLocal<T> {
    val local = GcThreadLocal<T>()
    local.set(initial())
    return local
}

actual class GcThreadRef(val thread: java.lang.Thread) {
    actual fun interrupt() {
        thread.interrupt()
    }
}

actual fun GcThreadRef.isCurrentThread(): Boolean {
    return this.thread == Thread.currentThread()
}

actual fun gcThread(name: String, block: suspend () -> Unit): GcThreadRef {
    val t = kotlin.concurrent.thread(name = name, isDaemon = true) {
        kotlinx.coroutines.runBlocking {
            block()
        }
    }
    return GcThreadRef(t)
}

actual fun <R> gcSynchronized(lock: Any, block: () -> R): R {
    return synchronized(lock, block)
}

actual fun getLocalIps(): List<String> {
    val ips = mutableListOf<String>()
    try {
        val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            if (networkInterface.isLoopback || !networkInterface.isUp) continue
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (address is java.net.Inet4Address) {
                    address.hostAddress?.let { ips.add(it) }
                }
            }
        }
    } catch (e: Exception) {
        club.gepetto.GcLog.e("Failed to get local IP addresses", e)
    }
    return ips
}

actual fun isHostPortAvailable(ip: String, port: Int): Boolean {
    return try {
        val socket = java.net.Socket()
        socket.connect(java.net.InetSocketAddress(ip, port), 200)
        socket.close()
        true
    } catch (e: Exception) {
        false
    }
}

actual fun scanSubnetForHost(myIp: String, networkPort: Int, onHostDiscovered: (String) -> Unit) {
    val hostFound = java.util.concurrent.atomic.AtomicBoolean(false)
    val parts = myIp.split(".")
    if (parts.size == 4) {
        val prefix = "${parts[0]}.${parts[1]}.${parts[2]}."
        val candidates = mutableListOf<String>()
        for (i in 1..254) {
            val target = "$prefix$i"
            if (target != myIp) candidates.add(target)
        }
        val pool = java.util.concurrent.Executors.newFixedThreadPool(32)
        for (ip in candidates) {
            if (hostFound.get()) break
            pool.submit {
                if (!hostFound.get() && isHostPortAvailable(ip, networkPort)) {
                    if (hostFound.compareAndSet(false, true)) {
                        onHostDiscovered(ip)
                    }
                }
            }
        }
        pool.shutdown()
        try { pool.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS) } catch (e: Exception) {}
    }
}

actual fun gcSleep(ms: Long) {
    try {
        Thread.sleep(ms)
    } catch (e: Exception) {
        // Ignore
    }
}

actual fun isWebPlatform(): Boolean = false


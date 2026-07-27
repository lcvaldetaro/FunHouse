package com.funhouse.feature.funhouseenginekotlin.util

import kotlinx.coroutines.launch

actual class GcConcurrentMap<K, V> actual constructor() {
    private val map = HashMap<K, V>()
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
    private val channel = kotlinx.coroutines.channels.Channel<T>(kotlinx.coroutines.channels.Channel.UNLIMITED)
    actual fun put(element: T) {
        channel.trySend(element)
    }
    actual suspend fun take(): T {
        return channel.receive()
    }
    actual fun poll(): T? {
        return channel.tryReceive().getOrNull()
    }
    actual fun clear() {
        while (true) {
            val result = channel.tryReceive()
            if (result.isFailure || result.isClosed) break
        }
    }
}

actual class GcThreadLocal<T> actual constructor() {
    private var value: T? = null
    actual fun get(): T? = value
    actual fun set(value: T) {
        this.value = value
    }
}

actual fun <T> gcThreadLocal(initial: () -> T): GcThreadLocal<T> {
    val local = GcThreadLocal<T>()
    local.set(initial())
    return local
}

actual class GcThreadRef(val job: kotlinx.coroutines.Job? = null) {
    actual fun interrupt() {
        job?.cancel()
    }
}

actual fun GcThreadRef.isCurrentThread(): Boolean = true

actual fun gcThread(name: String, block: suspend () -> Unit): GcThreadRef {
    val job = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
        try {
            block()
        } catch (e: Throwable) {
            // Safe catch
        }
    }
    return GcThreadRef(job)
}

actual fun <R> gcSynchronized(lock: Any, block: () -> R): R {
    return block()
}

actual fun getLocalIps(): List<String> = emptyList()

actual fun isHostPortAvailable(ip: String, port: Int): Boolean = false

actual fun scanSubnetForHost(myIp: String, networkPort: Int, onHostDiscovered: (String) -> Unit) {
    // No-op on Web
}

actual fun gcSleep(ms: Long) {
    // No-op on Web
}

actual fun isWebPlatform(): Boolean = true


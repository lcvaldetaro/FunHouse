package com.funhouse.feature.funhouseenginekotlin.util

expect class GcConcurrentMap<K, V>() {
    fun put(key: K, value: V): V?
    fun remove(key: K): V?
    operator fun get(key: K): V?
    operator fun set(key: K, value: V)
    fun clear()
    fun containsKey(key: K): Boolean
    val values: Collection<V>
    val entries: Set<Map.Entry<K, V>>
    fun forEach(action: (Map.Entry<K, V>) -> Unit)
    fun getOrPut(key: K, defaultValue: () -> V): V
    operator fun iterator(): Iterator<Map.Entry<K, V>>
}

expect class GcQueue<T>() {
    fun put(element: T)
    suspend fun take(): T
    fun poll(): T?
    fun clear()
}

expect class GcThreadLocal<T>() {
    fun get(): T?
    fun set(value: T)
}

expect fun <T> gcThreadLocal(initial: () -> T): GcThreadLocal<T>

expect class GcThreadRef {
    fun interrupt()
}

expect fun GcThreadRef.isCurrentThread(): Boolean

expect fun gcThread(name: String, block: suspend () -> Unit): GcThreadRef

expect fun <R> gcSynchronized(lock: Any, block: () -> R): R

expect fun getLocalIps(): List<String>

expect fun isHostPortAvailable(ip: String, port: Int): Boolean

expect fun scanSubnetForHost(myIp: String, networkPort: Int, onHostDiscovered: (String) -> Unit)

expect fun gcSleep(ms: Long)

expect fun isWebPlatform(): Boolean


package kotlin

import java.io.PrintWriter
import kotlin.math.pow as kotlinPow
import kotlin.math.round as kotlinRound

class Thread {
    private var interrupted = false
    fun interrupt() {
        interrupted = true
    }
    val isInterrupted: Boolean get() = interrupted

    companion object {
        private var current: Thread = Thread()
        fun sleep(millis: Long) {}
        fun currentThread(): Thread = current
        fun setCurrentThread(t: Thread) {
            current = t
        }
        
        private val activeThreads = mutableListOf<Thread>()
        fun register(t: Thread) {
            activeThreads.add(t)
        }
        fun interruptAll() {
            activeThreads.forEach { it.interrupt() }
            activeThreads.clear()
        }
    }
}

class InterruptedException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}

object System {
    fun currentTimeMillis(): Long = 0L
    val err = PrintWriter(null)
    fun exit(status: Int) {}
}

object Math {
    fun abs(x: Int): Int = kotlin.math.abs(x)
    fun abs(x: Long): Long = kotlin.math.abs(x)
    fun abs(x: Float): Float = kotlin.math.abs(x)
    fun abs(x: Double): Double = kotlin.math.abs(x)

    fun min(a: Int, b: Int): Int = kotlin.math.min(a, b)
    fun min(a: Long, b: Long): Long = kotlin.math.min(a, b)
    fun min(a: Float, b: Float): Float = kotlin.math.min(a, b)
    fun min(a: Double, b: Double): Double = kotlin.math.min(a, b)

    fun max(a: Int, b: Int): Int = kotlin.math.max(a, b)
    fun max(a: Long, b: Long): Long = kotlin.math.max(a, b)
    fun max(a: Float, b: Float): Float = kotlin.math.max(a, b)
    fun max(a: Double, b: Double): Double = kotlin.math.max(a, b)

    fun pow(a: Double, b: Double): Double = a.kotlinPow(b)
    fun sqrt(x: Double): Double = kotlin.math.sqrt(x)
    fun random(): Double = kotlin.random.Random.nextDouble()
    fun round(x: Double): Long = kotlinRound(x).toLong()
    fun floor(x: Double): Double = kotlin.math.floor(x)
    fun ceil(x: Double): Double = kotlin.math.ceil(x)
    fun toRadians(angdeg: Double): Double = angdeg / 180.0 * kotlin.math.PI
}

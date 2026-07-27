package java.util

import kotlin.random.Random as KotlinRandom

class Random {
    constructor()
    constructor(seed: Long)

    fun nextInt(): Int = KotlinRandom.nextInt()
    fun nextInt(bound: Int): Int = KotlinRandom.nextInt(bound)
    fun nextDouble(): Double = KotlinRandom.nextDouble()
    fun nextFloat(): Float = KotlinRandom.nextFloat()
    fun nextLong(): Long = KotlinRandom.nextLong()
    fun nextBoolean(): Boolean = KotlinRandom.nextBoolean()
}

class Locale {
    companion object {
        fun getDefault(): Locale = Locale()
    }
    val language: String get() = "en"
}

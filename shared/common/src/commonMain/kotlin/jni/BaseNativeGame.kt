package com.funhouse.shared.common.jni

object BaseNativeGame {
    const val greetings = ""
    fun queryVoice(): Int = if (com.funhouse.shared.common.models.currentSettings.usingVoice) 1 else 0
    
    fun initializeMedia(context: Any? = null) {}
    fun playBicycle() = com.funhouse.shared.common.jni.playBicycle()
    fun haltBicycle() = com.funhouse.shared.common.jni.haltBicycle()
    fun playCoin() = com.funhouse.shared.common.jni.playCoin()
    fun playBell() = com.funhouse.shared.common.jni.playBell()
    fun playJackpot() = com.funhouse.shared.common.jni.playJackpot()
    fun playJackpotBigger() = com.funhouse.shared.common.jni.playJackpotBigger()
    fun playJackpotMusic() = com.funhouse.shared.common.jni.playJackpotMusic()
    fun playTennisBall() = com.funhouse.shared.common.jni.playTennisBall()
    fun playFlip() = com.funhouse.shared.common.jni.playFlip()
    fun playChip() = com.funhouse.shared.common.jni.playChip()
    fun playBump() = com.funhouse.shared.common.jni.playBump()
    fun playBoing() = com.funhouse.shared.common.jni.playBoing()
    fun playDice() = com.funhouse.shared.common.jni.playDice()

    object Companion {
        fun initializeMedia(context: Any? = null) {}
        fun playBicycle() = com.funhouse.shared.common.jni.playBicycle()
        fun haltBicycle() = com.funhouse.shared.common.jni.haltBicycle()
        fun playCoin() = com.funhouse.shared.common.jni.playCoin()
        fun playBell() = com.funhouse.shared.common.jni.playBell()
        fun playJackpot() = com.funhouse.shared.common.jni.playJackpot()
        fun playJackpotBigger() = com.funhouse.shared.common.jni.playJackpotBigger()
        fun playJackpotMusic() = com.funhouse.shared.common.jni.playJackpotMusic()
        fun playTennisBall() = com.funhouse.shared.common.jni.playTennisBall()
        fun playFlip() = com.funhouse.shared.common.jni.playFlip()
        fun playChip() = com.funhouse.shared.common.jni.playChip()
        fun playBump() = com.funhouse.shared.common.jni.playBump()
        fun playBoing() = com.funhouse.shared.common.jni.playBoing()
        fun playDice() = com.funhouse.shared.common.jni.playDice()
    }
}

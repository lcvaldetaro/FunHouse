package com.funhouse.shared.common.jni

import com.funhouse.shared.common.utils.formatWithArgs
import club.gepetto.GcLog
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.models.WALLET_KEY
import com.funhouse.shared.common.models.currentSettings

open class BaseKotlinGame : GameInterface {
    var callback: TerminalDataCallback? = null
    override fun sendCommand(command: String): Int { return 0 }
    override fun start() {
        com.funhouse.shared.common.utils.clearTerminalText()
    }
    override fun start(gameNickName: String) {
        com.funhouse.shared.common.utils.clearTerminalText()
    }
    override fun start(gameNickName: String, isMultiplayer: Boolean) {
        com.funhouse.shared.common.utils.clearTerminalText()
        start(gameNickName)
    }
    override fun registerTerminalCallback(callback: TerminalDataCallback) {
        this.callback = callback
    }
    
    override fun stop() {
        com.funhouse.shared.common.utils.stopGameThread()
        com.funhouse.shared.common.utils.clearTerminalText()
    }

    fun myPrintf(fmt: String, vararg args: Any?) {
        fun formatString(fmt: String): String {
            return fmt.replace("%ld", "%d")
        }

        try {
            val formatted = formatString(fmt).formatWithArgs(args)
            com.funhouse.shared.common.utils.appendTerminalText(formatted)
            callback?.onNewTerminalDataReceived(formatted)
            GcLog.d(formatted)
        } catch (e: Exception) {
            GcLog.e("Error formatting string: $fmt", e)
            com.funhouse.shared.common.utils.appendTerminalText(fmt)
            callback?.onNewTerminalDataReceived(fmt)
        }
    }

    fun greetings() {
        if (queryVoice() == 0)
            myPrintf("\n\n" + Companion.greetings + "\n\n")

        if (currentSettings.playerNickName.isNotEmpty())
            myPrintf("Hello, ${currentSettings.playerNickName}!\n")
    }

    companion object {
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

        val greetings: String
            get() = ""

        fun queryVoice() : Int = if (currentSettings.usingVoice) 1 else 0

        fun updatePlayerName(name: String) {
            currentSettings = currentSettings.copy(playerNickName = name)
            currentSettings.save()
        }

        fun updateStringMap(key:String, string: String) {
            currentSettings.stringsMap[key] = string
            currentSettings.save()
        }

        fun getStringMap(key:String) : String {
            return currentSettings.stringsMap[key].orEmpty()
        }

        fun getWallet(key: String) : String {
            val walletKey = "${WALLET_KEY}${key}"
            return currentSettings.stringsMap[walletKey].orEmpty()
        }

        fun getWalletValue(key: String) : Float {
            val walletKey = "${WALLET_KEY}${key}"
            var value = 0f
            try { value  = currentSettings.stringsMap[walletKey].orEmpty().toFloat() } catch (e: Exception) {}
            return value
        }

        fun updateWallet(key:String, string: String) {
            val walletKey = "${WALLET_KEY}${key}"
            currentSettings.stringsMap[walletKey] = string
            currentSettings.save()
        }

        fun getTotalWallets() = currentSettings.getTotalWallets()
    }
}

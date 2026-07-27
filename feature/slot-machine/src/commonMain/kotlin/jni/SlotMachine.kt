package jni
import com.funhouse.shared.common.jni.BaseKotlinGame

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.Settings
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog
import java.io.File

class SlotMachine(
    library: String,
    settingsParam: Settings,
    gameFolderParam: String,
    about: String,
    callback: TerminalDataCallback?
) : BaseKotlinGame()  {

    private val gameFolder: String
    private val about: String

    init {
        settings = settingsParam
        gameFolder = gameFolderParam
        this.about = about

        if (packageFolder.isNotEmpty()) {
            val folder = File(packageFolder, gameFolder)
            folder.mkdir()
        }
        restoreGame()
    }

    companion object {
        private val TOTAL_TAG = "-total"
        private val TOTAL_FOREVER_TAG = "-total-forever"
        private val packageFolder = AppData.packageFolder
        private var settings: Settings = Settings()
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "slotmachine"
        var tokenBalance = 0
        var totalTokensLost = 0
        var totalTokensWon = 0
        var foreverTokensLost = 0
        var foreverTokensWon = 0
        val lirasToToken = 1

        fun saveGame(betSize: Int) {
            val oldValue = BaseKotlinGame.getWalletValue(key)
            val newValue = oldValue + (betSize * lirasToToken)
            updateWallet(key, "${newValue}")

            GcLog.d("saving tokenValue = ${newValue} tokenbalance = $tokenBalance")

            updateStringMap(key, "${newValue} ${(newValue / lirasToToken) + tokenBalance}")
            updateStringMap("${key}${TOTAL_TAG}", "${totalTokensLost} ${totalTokensWon}")
            updateStringMap("${key}${TOTAL_FOREVER_TAG}", "${foreverTokensLost} ${foreverTokensWon}")
        }

        fun restoreGame() {
            var stringMap = BaseKotlinGame.getStringMap(key)
            var word = stringMap.split(" ")
            try {
                tokenBalance = word[1].toInt()
                GcLog.d("restored tokenValue= ${word[0]} tokenbalance = $tokenBalance")
            }
            catch (e: Exception) { }

            stringMap = BaseKotlinGame.getStringMap("${key}${TOTAL_TAG}")
            word = stringMap.split(" ")
            try {
                totalTokensLost = word[0].toInt()
                totalTokensWon = word[1].toInt()
            }
            catch (e: Exception) { }

            stringMap = BaseKotlinGame.getStringMap("${key}${TOTAL_FOREVER_TAG}")
            word = stringMap.split(" ")
            try {
                foreverTokensLost = word[0].toInt()
                foreverTokensWon = word[1].toInt()
            }
            catch (e: Exception) { }
        }

        fun getGameWalletValue() = BaseKotlinGame.getWalletValue(key)

        fun getAllWalletValue() = currentSettings.getTotalWallets()
    }
}
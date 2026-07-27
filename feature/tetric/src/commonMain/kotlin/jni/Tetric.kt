package jni
import com.funhouse.shared.common.jni.BaseKotlinGame

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.Settings
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog
import java.io.File

class Tetric(
    library: String,
    settingsParam: Settings,
    gameFolderParam: String,
    about: String,
    callback: TerminalDataCallback?
) : BaseKotlinGame() {
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
    }

    companion object {
        private val packageFolder = AppData.packageFolder
        private var settings: Settings = Settings()
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "tetric"

        fun saveGame(currentWinnings: Int) {
            val liraWinnings = currentWinnings.toFloat() / 100f
            val newValue = BaseKotlinGame.getWalletValue(key) + liraWinnings
            GcLog.d("saving ${newValue} to key ${key}")
            updateWallet(key, "${newValue}")
        }

        fun getGameWalletValue() = BaseKotlinGame.getWalletValue(key)
    }
}
package jni
import com.funhouse.shared.common.jni.BaseKotlinGame

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.Settings
import com.funhouse.shared.common.models.currentSettings
import java.io.File

class Craps(
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
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "craps"

        fun getGameWalletValue() = BaseKotlinGame.getWalletValue(key)
    }
}


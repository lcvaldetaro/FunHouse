package jni
import com.funhouse.shared.common.jni.BaseKotlinGame

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.Settings
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog
import java.io.File

class ClassicArcades(
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
        //lateinit var tennisBallPLayer: MediaPlayer
        //lateinit var bellPLayer: MediaPlayer
        private val packageFolder = AppData.packageFolder
        private var settings: Settings = Settings()
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "paddleball"
        var tokenBalance = 0
        val lirasToToken = 1

        /*fun initializeMedia(ctx: Context) {
            tennisBallPLayer = MediaPlayer.create(ctx, com.funhouse.shared.common.R.raw.tennisball).apply { setOnErrorListener { _, _, _ -> true } }
            bellPLayer = MediaPlayer.create(ctx, com.funhouse.shared.common.R.raw.bell).apply { setOnErrorListener { _, _, _ -> true } }
        }

         */

        fun saveGame(gameResult: Int) {
            val oldValue = BaseKotlinGame.getWalletValue(key)

            when (currentSettings.currentGame!!.nickName) {
                "paddleball" -> {
                    restoreGame()
                    tokenBalance += gameResult

                    val tokenValueIncrease = gameResult * lirasToToken
                    val newValue = oldValue + tokenValueIncrease

                    GcLog.d("saving tokenValue = ${newValue} tokenbalance = ${tokenBalance}")
                    updateWallet(key, "${newValue}")
                    updateStringMap(key, "${newValue} ${tokenBalance}")
                }
            }
        }

        fun restoreGame() {
            val stringMap = BaseKotlinGame.getStringMap(key)
            when (currentSettings.currentGame!!.nickName) {
                "paddleball" -> {
                    val word = stringMap.split(" ")
                    try {
                        tokenBalance = word[1].toInt()
                        GcLog.d("restored tokenValue= ${word[0]} tokenbalance = ${tokenBalance}")
                    } catch (e: Exception) { }
                }
            }
        }

        fun getGameWalletValue() = BaseKotlinGame.getWalletValue(key)

        /*fun playTennisBall() {
            tennisBallPLayer.play()
        }

        fun playBell() {
            bellPLayer.play()
        }*/
    }
}

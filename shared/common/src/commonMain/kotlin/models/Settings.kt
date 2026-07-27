package com.funhouse.shared.common.models

import com.funhouse.shared.common.AppData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import club.gepetto.GcLog


@Serializable
data class Settings (
    var usingVoice: Boolean = false,
    var secretGames: Boolean = AppData.secretGamesEnabled,
    var autoSave: Boolean = true,
    var autoRestore: Boolean = true,
    var runningAsService: Boolean = false,
    var currentlyHosting: Boolean = false,
    var hostName: String = "",
    var playerNickName: String = "",
    var playerType: String = "",
    var playerDescription: String = "",
    var playerHandle: String = "",
    var handleType: String = "",
    var handleDescription: String = "",
    var currentGame: Game? = null,
    var stringsMap: MutableMap<String, String> = mutableMapOf(),
    var forceLightMode: Boolean = false,
    var forceDarkMode: Boolean = false,
) {
    companion object {
        fun fromJson(jsonStr: String): Settings? {
            var result: Settings? = null
            try {
                val newSettings = Json.decodeFromString<Settings>(jsonStr)
                result = newSettings
            } catch (e: Exception) {
                GcLog.e(e, "Exception ${e}")
            }
            return result
        }

        fun restore(filename: String = "settingsSaved.json") : Settings {
            var result: Settings? = Settings()
            val savedText = com.funhouse.shared.common.utils.readTextFromFile(filename)
            if (savedText != null) {
                GcLog.v("Saved settings exist and loaded: ${filename}")
                result = fromJson(savedText)
            }
            else {
                GcLog.v("Saved settings do not exist: ${filename}. using default and returning $result")
            }

            if (result != null) {
                if (result.playerNickName == "Unknown")
                    currentSettings = result.copy(playerNickName = "")
                else
                    currentSettings = result
            }

            return if (result != null) result else Settings()
        }
    }

    fun toJson() : String {
        return Json.encodeToString(this)
    }

    fun save (filename: String = "settingsSaved.json") {
        currentSettings = this
        com.funhouse.shared.common.utils.saveTextToFile(filename, this.toJson())
    }

    fun walletValues (games: List<Game>?) : Map<String, Float> {
        val map: MutableMap<String, Float> = mutableMapOf()
        games?.forEach { game ->
            val value = stringsMap["${WALLET_KEY}${game.nickName}"]
            if (value != null) {
                try {
                    val floatValue = value.toFloat()
                    map[game.nickName] = floatValue
                } catch (e: Exception) {
                }
            }
        }
        return map
    }

    fun getTotalWallets () : Float {
        var inWallet = 0f
        this.stringsMap
            .filter { it.key.startsWith(WALLET_KEY)}
            .forEach {
                try {
                    inWallet += it.value.toFloat()
                } catch (e: Exception) {}
            }
        return inWallet
    }
}

var currentSettings: Settings = Settings()

const val WALLET_KEY = "wallet-"
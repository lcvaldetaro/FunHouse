package com.gepetto.funhouse.models

import com.funhouse.shared.common.AppData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import club.gepetto.GcLog
import java.io.File

@Serializable
data class GameList (
    val gameFiles: List<DownloadableFile>? = null,
    val games: List<Game>? = null,
) {
    var success = false

    fun toJson() : String {
        return Json.encodeToString(this)
    }

    fun findGame (label: String) : Game? {
        games?.forEach { game ->
            if (game.menuTitle.isNotEmpty() && game.menuTitle == label)
                return game
        }
        games?.forEach { game ->
            if (game.title == label)
                return game
        }
        return null
    }

    fun save(directory: File? = AppData.packageFolderFile as? File, filename: String = "gameListSaved.json") {
        val versionCode = File(directory, "versionCode.txt")
        versionCode.writeText(AppData.version)

        val savedData = File(directory, filename)
        savedData.writeText(toJson())
    }

    fun add(moreGames: List<Game>) : GameList {
        val currentGames: MutableList<Game> = this.games!!.toMutableList()
        moreGames.forEach { game ->
            if ((this.games.firstOrNull { it.nickName == game.nickName }) == null)
                currentGames.add(game)
        }
        return this.copy(games = currentGames)
    }

    fun readLicenses() : GameList {
        val thisGame = this
        if (thisGame.games != null) {
            thisGame.games.forEach { game ->
                if (game.licenseFile != null) {
                    val licenseFile = File(AppData.gameFolderFile as File, game.licenseFile!!.fileName)
                    val bytes = licenseFile.readBytes()
                    game.license = bytes.decodeToString()
                }
            }
        }
        return thisGame
    }

    companion object {
        fun fromJson (jsonStr: String) : GameList? {
            var result : GameList? = null
            try {
                val newdbSaved = Json.decodeFromString<GameList>(jsonStr)
                val mappedGames = newdbSaved.games?.map {
                    if (it.multiPlayer) it.copy(gameType = GameType.MULTIPLAYER) else it
                }
                result = newdbSaved.copy(games = mappedGames)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun restore(directory: File? = AppData.packageFolderFile as? File, filename: String = "gameListSaved.json") : GameList? {
            var result: GameList? = null
            val savedData = File(directory, filename)
            if (savedData.exists()) {
                GcLog.e("Cached data exists and will restore on ${directory}/${filename}")
                result = fromJson(savedData.readText())
            }

            if (result == null) {
                result = defaultGameList.readLicenses()
            } else {
                // Merge: use compile-time defaultGameList for default games, and keep any custom games from cached result
                val defaultGames = defaultGameList.games.orEmpty()
                val mergedGames = defaultGames.toMutableList()
                result.games?.forEach { cachedGame ->
                    if (defaultGames.none { it.nickName == cachedGame.nickName }) {
                        mergedGames.add(cachedGame)
                    }
                }
                result = result.copy(games = mergedGames)
                result = result.readLicenses()
            }
            return result
        }

        fun clean(directory: File? = AppData.packageFolderFile as? File, filename: String = "gameListSaved.json") {
            val savedData = File( directory, filename)
            try {
                savedData.delete()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

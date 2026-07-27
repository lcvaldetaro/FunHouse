package com.funhouse.shared.common.models

import com.funhouse.shared.common.BACKGROUND_TERMINAL_COLOR
import com.funhouse.shared.common.TEXT_TERMINAL_COLOR
import com.funhouse.shared.common.AppData
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import club.gepetto.GcLog
import com.funhouse.shared.common.jni.GameInterface
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
data class Game(
    val gameName: String = "",
    val nickName: String = "",
    val title: String = "",
    val menuTitle: String = "",
    val version: String = "1.0",
    val echo: Boolean = true,
    val composableTextGame: Boolean = false,
    @Transient val gameClass: GameInterface? = null,
    val composableGame: Boolean = false,
    val activityGame: Boolean = false,
    val gameType: GameType = GameType.ADVENTURE,
    val useBackgroundBitmap: Boolean = false,
    val forceDark: Boolean = false,
    val forceLight: Boolean = false,
    val secretGame: Boolean = false,
    val soundsOff: Boolean = false,
    val directions: List<Direction> = defaultDirections,
    val directionColumns: Int? = 7,
    @Transient val textColorLight: Color? = TEXT_TERMINAL_COLOR,
    @Transient val backgroundColorLight: Color? = BACKGROUND_TERMINAL_COLOR,
    @Transient val textColorDark: Color? = Color.White,
    @Transient val backgroundColorDark: Color? = Color.Black,
    val saveCommand: String = "",
    val restoreCommand: String = "",
    val autoSave: Boolean = false,
    val about: String = "",
    var license: String? = null,
    val printDelay: Int = 20,
    val printYou: Boolean = true,
    val multiPlayer: Boolean = false,
    val gameGenre: GameGenre = GameGenre.OTHER,

    // If the following field is used, the others are ignored
    val localGame: LocalGame? = null,

    // Following fields are for networked games
    val greeting: String = "",
    val description: String = "",
    val maxObjects: Int = 10,
    val saveFilePrefix: String = "",
    val startPlaces: List<Int> = listOf(1),
    val mainGameFile: DownloadableFile = DownloadableFile(""),
    val placesFile: DownloadableFile = DownloadableFile(""),
    val objectsFile: DownloadableFile = DownloadableFile(""),
    val goalsFile: DownloadableFile = DownloadableFile(""),
    val helpFile: DownloadableFile = DownloadableFile(""),
    val wanderMiscFile: DownloadableFile? = null,
    val wanderWrldFile: DownloadableFile? = null,
    val gameImage: DownloadableFile? = null,
    val licenseFile: DownloadableFile? = null,
    val library: String = "",
) {
    var success = false

    companion object {
        fun fromJson (jsonStr: String) : Game? {
            var result : Game? = null
            try {
                val newGameSaved = Json.decodeFromString<Game>(jsonStr)
                result = newGameSaved
            }
            catch (e: Exception) {
                GcLog.e("Exception ${e}", e)
                e.printStackTrace()
            }
            return result
        }
    }

    fun isGameNetworked() : Boolean {
        if (this.localGame == null)
            return true
        return false
    }
}

enum class GameGenre { ADVENTURE, PUZZLE, STRATEGY, SPACE, WAR, MISTERY, OTHER }

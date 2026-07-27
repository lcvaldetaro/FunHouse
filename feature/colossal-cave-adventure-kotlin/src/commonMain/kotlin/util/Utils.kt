package colossalcaveadventurekotlin.utils
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import jni.AdventureKotlin

import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.models.Direction

fun installFiles() {
    // install game files
    try {
        // Adventure
        installFile("adventure.help")
        installFile("adventure.md")
        installFile("adventure.text")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultCcvDirections: List<Direction> =
    listOf(
        Direction("N", "north"),
        Direction("S", "south"),
        Direction("W", "west"),
        Direction("E", "east"),
        Direction("Yes", "yes"),
        Direction("No", "no"),
        Direction("Up", "up"),
        Direction("Down", "down"),
        Direction("In", "in"),
        Direction("Out", "out"),
        Direction("Look", "look"),
        Direction("Inv", "inventory"),
        Direction("Save", "save"),
        Direction("Resume", "resume"),
        )

val adventureAbout: String = """
    ${AppData.applicationContext?.getString(R.string.adventure_about) ?: ""}
     Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
    """.trimIndent()

val adventureGame = Game(
    nickName = "adventure",
    title = "Colossal Cave Adventure",
    library = "gepetto.adv",
    menuTitle = "Colossal Cave",
    forceDark = true,
    useBackgroundBitmap = true,
    composableTextGame = true,
    gameClass = AdventureKotlin(),
    version = "2.14",
    about = adventureAbout,
    helpFile = DownloadableFile("adventure.md"),
    directionColumns = 6,
    directions = defaultCcvDirections,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)


package misterymansionkotlin.utils


import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile
import jni.MansionKotlin

fun installFiles() {
    try {
        installFile("mansion.html")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultMansionDirections: List<Direction> =
    listOf(
        Direction("N", "N"),
        Direction("S", "S"),
        Direction("W", "W"),
        Direction("E", "E"),
        Direction("Yes", "Y"),
        Direction("No", "N"),
        Direction("Up", "U"),
        Direction("Down", "D"),
        Direction("In", "IN"),
        Direction("Out", "OUT"),
        Direction("Look", "L"),
        Direction("Help", "HELP"),
        Direction("About", "ABOUT"),
    )

val defaultMansionGame = Game(
    nickName = "mansion",
    library = "gepetto.mansion",
    title = "Mystery Mansion",
    menuTitle = "Mystery Mansion",
    version = "19.2",
    composableTextGame = true,
    gameClass = MansionKotlin(),
    helpFile = DownloadableFile("mansion.html"),
    about = """
        Copyright (C) 1999,2000 James Garnett garnett@catbelly.com.
        Permission granted for personal (non-commercial) use only.
        Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
    """.trimIndent(),
    directions = defaultMansionDirections,
    directionColumns = 5,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

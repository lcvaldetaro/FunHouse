package dinkumkotlin.utils


import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile
import jni.DinkumKotlin

fun installFiles() {
    try {
        installFile("dinkum.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultDinkumDirections: List<Direction> =
    listOf(
        Direction("N", "north"),
        Direction("S", "south"),
        Direction("W", "west"),
        Direction("E", "east"),
        Direction("NE", "ne"),
        Direction("NW", "nw"),
        Direction("SW", "sw"),
        Direction("SE", "se"),
        Direction("Yes", "yes"),
        Direction("No", "no"),
        Direction("Up", "up"),
        Direction("Down", "down"),
        Direction("In", "go in"),
        Direction("Out", "go out"),
        Direction("Look", "look"),
        Direction("Inv", "inventory"),
    )

val defaultDinkumGame = Game(
    nickName = "dinkum",
    library = "gepetto.dink",
    title = "Dinkum",
    version = "2.14",
    about = """
        --- The Dinkum Program ---

        Software by Gary A. Allen, Jr. 23 February 1994 Version: Mk 2.14
        (c) Copyright 1994 by Gary A. Allen, Jr.
        Permission granted for personal (non-commercial) use only.
        Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
    """.trimIndent(),
    helpFile = DownloadableFile("dinkum.md"),
    directions = defaultDinkumDirections,
    directionColumns = 6,
    composableTextGame = true,
    gameClass = DinkumKotlin(),
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

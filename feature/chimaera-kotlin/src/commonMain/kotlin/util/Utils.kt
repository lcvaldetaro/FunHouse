package chimaerakotlin.utils

import jni.ChimaeraKotlin

import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    try {
        installFile("chimaera.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultChimaeraDirections : List<Direction> =
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
        Direction("In", "in"),
        Direction("Out", "out"),
        Direction("Look", "look"),
        Direction("help", "help"),
        Direction("hint", "hint"),
        Direction("Inv", "inventory"),
        Direction("Save", "save"),
        Direction("Restore", "restore"),
        )

val defaultChimaeraGame = Game(
    nickName = "chimaera",
    title = "Chimaera",
    version = "C1.002",
    composableTextGame = true,
    gameClass = ChimaeraKotlin(),
    about =  """
Chimaera Text Adventure   
Version C1.002   
Written by Nicholas Perre-Wetherall
           [aka Chris Newall]
             Copyright 1984
          All rights reserved
Permission granted for personal (non-commercial) use only.
Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
""".trimIndent(),
    helpFile = DownloadableFile("chimaera.md"),
    directions = defaultChimaeraDirections,
    directionColumns = 7,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

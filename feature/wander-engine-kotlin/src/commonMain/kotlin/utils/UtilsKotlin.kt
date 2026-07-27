package wanderkotlin.utils

import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    try {
        // All Wander games
        installFile("gnulicense.txt")
        installFile("misc.nr")

        // Wander A3
        installFile("wandera3.md")
        installFile("a3.wrld")
        installFile("a3.misc")
        installFile("wandera3.json")
        installFile("wandera3.png")

        // Wander Castle
        installFile("wandercastle.md")
        installFile("wandercastle.png")
        installFile("wandercastle.json")
        installFile("castle.wrld")
        installFile("castle.misc")

        // Wander Library
        installFile("wanderlibrary.md")
        installFile("wanderlibrary.json")
        installFile("wanderlibrary.png")
        installFile("library.wrld")
        installFile("library.misc")

        // Wander Logical Bit Operations
        installFile("wandertut.md")
        installFile("wandertut.json")
        installFile("wandertut.png")
        installFile("tut.wrld")
        installFile("tut.misc")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
    
This game was developed with the game engine Wander from the 70's.
The game itself is in public domain.
The game engine is subject to the GNU license, as follows.           
    
Wander Game Engine Version 1.6 2/23/85
WANDER -- Non-deterministic fantasy story tool
This game engine has been used under permission from the copyright owner, Peter S. Langston.
Copyright (c) 1978 by Peter S. Langston - New  York,  N.Y.
 Converted to Kotlin by Valdetaro Consulting, LLC in 2026.

 GNU GENERAL PUBLIC LICENSE
 Version 3, 29 June 2007
 Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.
""".trimIndent()

val defaultWanderDirections: List<Direction> =
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
        Direction("Inv", "inventory"),
        Direction("Save", "save"),
        Direction("Restore", "restore"),
    )

val defaultWanderAldebaranGame = Game(
    nickName = "wandera3",
    library = "gepetto.wan",
    title = "Wander Aldebaran III",
    menuTitle = "Aldebaran",
    version = "2.12",
    helpFile = DownloadableFile("wandera3.md"),
    wanderMiscFile = DownloadableFile("a3.misc"),
    wanderWrldFile = DownloadableFile("a3.wrld"),
    gameImage = DownloadableFile("wandera3.png"),
    licenseFile = DownloadableFile("gnulicense.txt"),
    directions = defaultWanderDirections,
    about = """
        
        
        Wander Aldebaran III Game
        
${defaultAbout}
""".trimIndent(),
    directionColumns = 6,
)

val defaultWanderLibraryGame = Game(
    nickName = "wanderlibrary",
    library = "gepetto.wan",
    title = "Wander Library",
    menuTitle = "Library",
    version = "2.12",
    helpFile = DownloadableFile("wanderlibrary.md"),
    wanderMiscFile = DownloadableFile("library.misc"),
    wanderWrldFile = DownloadableFile("library.wrld"),
    gameImage = DownloadableFile("wanderlibrary.png"),
    licenseFile = DownloadableFile("gnulicense.txt"),
    directions = defaultWanderDirections,
    about = """
        
        
        Wander Library Game

${defaultAbout}
""".trimIndent(),
    directionColumns = 6,
)

val defaultLogicalOpsDirections: List<Direction> =
    listOf(
        Direction("about", "about"),
    )

val defaultWanderLogicalOperationsGame = Game(
    nickName = "wandertut",
    library = "gepetto.wan",
    title = "Logical Bit Operations",
    directions = defaultLogicalOpsDirections,
    directionColumns = 6,
    menuTitle = "Logic",
    version = "2.12",
    gameType = GameType.OTHER,
    helpFile = DownloadableFile("wandertut.md"),
    wanderMiscFile = DownloadableFile("tut.misc"),
    wanderWrldFile = DownloadableFile("tut.wrld"),
    gameImage = DownloadableFile("wandertut.png"),
    licenseFile = DownloadableFile("gnulicense.txt"),
    about = """
        
        
        Wander Logical Bit Operations

${defaultAbout}
""".trimIndent(),
)

val defaultWanderCastleGame = Game(
    nickName = "wandercastle",
    library = "gepetto.wan",
    title = "Wander Castle",
    menuTitle = "Wander Castle",
    version = "2.12",
    helpFile = DownloadableFile("wandercastle.md"),
    wanderMiscFile = DownloadableFile("castle.misc"),
    wanderWrldFile = DownloadableFile("castle.wrld"),
    gameImage = DownloadableFile("wandercastle.png"),
    licenseFile = DownloadableFile("gnulicense.txt"),
    directions = defaultWanderDirections,
    about = """
        
        
        Wander Castle Game
        
${defaultAbout}
""".trimIndent(),
    directionColumns = 6,
)

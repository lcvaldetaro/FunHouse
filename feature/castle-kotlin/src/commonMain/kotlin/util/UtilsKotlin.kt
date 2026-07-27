package castlekotlin.utils


import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile
import jni.CastleKotlin

fun installFiles() {
    try {
        installFile("castle.hlp")
        installFile("castle.md")
        installFile("castle.sav", overwrite = false)
        installFile("castlelocations.dat")
        installFile("castlegame.castlesaved", overwrite = false)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultHelp = """
```
    
    Adventure Castle Adventure
    You have been given the mission of exploring the ancient ruins of long lost ADVENTURE CASTLE, which are said to be buried nearby.
```

    

Local people are afraid of this area... so you are on your own.  Those foolish enough to attempt the task have  never  been  heard  from  again. There are rumors of terrible monsters and strange magic...

    

Your job is to find and explore the  castle and surrounding area, and to retrieve the fabulous treasures said to be lost within its depths.  If you do manage to find anything of value, pile it on the beach where  your employer can get it (If you can't find the beach, you're in REAL trouble).

    

Since you can't fit inside the computer where this fantasy land really exists, we will send your ALTER EGO who can.  You may communicate through commands typed on the keyboard.  Most commands are of the general form of a verb and a noun, such as 'take knife',  although your alter ego usually can determine what you want from more complicated commands.

    

Use 'quit' or 'stop' to end the game, 'score' or 'points' to find  out how badly you are doing, and 'carry' or 'inventory' to find out  what you possess. Use 'suspend/resume' to save/return to a game.

    

```
To get full points,  you must place all treasures safely on the beach, and destroy all monsters which may be inhabiting the area... GOOD LUCK!!!
    
    
```
""".trimIndent().trimIndent()

val defaultCastleDirections: List<Direction> =
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
    )

val defaultCastkeGame = Game(
    nickName = "castle",
    title = "Castle Adventure",
    menuTitle = "Castle Adventure",
    version = "1.0",
    composableTextGame = true,
    gameClass = CastleKotlin(),
    helpFile = DownloadableFile("castle.md"),
    about = """
        Copyright 1983-2001 Dave Dunfield
        All rights reserved.

        Permission granted for personal (non-commercial) use only.
        Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
    """.trimIndent(),
    directions = defaultCastleDirections,
    directionColumns = 6,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

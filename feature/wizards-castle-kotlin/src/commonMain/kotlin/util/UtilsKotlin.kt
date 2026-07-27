package wizardscastlekotlin.utils


import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameGenre
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile
import jni.WizardsCastleKotlin

fun installFiles() {
    try {
        installFile("wizardscastle.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
          The Wizard's Castle           
 written in C by Leslie S. Bird 11/2/09 
 original BASIC by Joseph R. Power 1980 
 extended by Verne R. Walrafen 2/29/84  
 This game is in public domain          

The Wizard's Castle, aka Orb Of Zot is a classic text adventure that has been around in various forms since 1980. Originally in BASIC, Leslie Bird has rewritten it in C for Amiga and other systems, and it has now been migrated to Kotlin. 

The game is turn based and takes place in an 8 x 8 x 8 dungeon that is randomly stocked with monsters, treasure and various other items. Goals involve fighting monsters, recovering magic items, avoiding traps and rescuing various persons, both evil and good.

HISTORY

Many cycles ago, Leslie owned a Commodore 128. The 128 was the last and greatest of the 8-bit era of computing and had many different capabilities; one of which was a full implementation of CP/M. CP/M was well on its way out, while MS-DOS was on its way in, which made locating CP/M software difficult. Fortunately, he had a friend with an Osborne with which to trade software. One of the disks had a BASIC game called ZOT.BAS, otherwise known as The Wizard's Castle.

He enjoyed this game a great deal. Although it was completely text-based and non-graphical, it was challenging with good repeat playability. He later copied it to MS-DOS and then on to AmigaBASIC.

Many cycles passed. BASIC and the systems that used it faded into obscurity. Hard drives lived and died.

Then recently, while reminiscing about the game and wishing he could play it again, he had an idea: WHY NOT DO SOMETHING ABOUT IT? DUH! And thus his project was born.

He decided to rewrite the game in C, for maximum compatibility with just about every system he owned. He also made the decision not to add improvements such as graphics and sound, but to remain as faithful as possible to the look and feel of the original. Such enhanced and updated versions already exist.

He needed a listing of the BASIC program to get started, which is when he discovered that the game he had played was not, in fact, the original Wizard's Castle! The original was written by Joseph R. Power for the Exidy Sorcerer and appeared in the July/August 1980 issue of Recreational Computing Magazine. The version he was used to playing was an Enhanced version written by Verne R. Walrafen in 1984. This was the version that he wanted to convert, but he was unable to find any listing of it on the Internet. Well, good thing he still had his Commodore 128, and his original disk was still in readable shape!

A few cycles later, and The Wizard's Castle came back to life. Enjoy looking for the Orb Of Zot, watch what you kiss, be careful when opening books and try not to step on any frogs!

Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
""".trimIndent()

val defaultWizardCastleDirections: List<Direction> = listOf(
    Direction("a", "a"),
    Direction("b", "b"),
    Direction("c", "c"),
    Direction("d", "d"),
    Direction("e", "e"),
    Direction("f", "f"),
    Direction("g", "g"),
    Direction("h", "h"),
    Direction("i", "i"),
    Direction("j", "j"),
    Direction("k", "k"),
    Direction("l", "l"),
    Direction("m", "m"),
    Direction("n", "n"),
    Direction("o", "o"),
    Direction("p", "p"),
    Direction("q", "q"),
    Direction("r", "r"),
    Direction("s", "s"),
    Direction("t", "t"),
    Direction("u", "u"),
    Direction("v", "v"),
    Direction("w", "w"),
    Direction("x", "x"),
    Direction("y", "y"),
    Direction("0", "0"),
    Direction("1", "1"),
    Direction("2", "2"),
    Direction("3", "3"),
    Direction("4", "4"),
    Direction("5", "5"),
    Direction("6", "6"),
    Direction("7", "7"),
    Direction("8", "8"),
    Direction("9", "9"),
)

val defaultGame = Game(
    nickName = "wizardscastle",
    echo = false,
    library = "gepetto.wizardscastle",
    title = "Orb Of Zot\nThe Wizards' Castle",
    menuTitle = "Orb Of Zot",
    gameGenre = GameGenre.MISTERY,
    version = "1.0",
    gameType = GameType.ADVENTURE,
    composableTextGame = true,
    gameClass = WizardsCastleKotlin(),
    helpFile = DownloadableFile("wizardscastle.md"),
    about = defaultAbout,
    directions = defaultWizardCastleDirections,
    directionColumns = 9,
    localGame = LocalGame(
        icon = 0,
        image = 0
    )
)

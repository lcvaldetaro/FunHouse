package hangmankotlin.utils


import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile
import jni.HangmanKotlin

fun installFiles() {
    try {
        installFile("hangman.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val directionHangman = listOf(
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
    Direction("z", "z"),
    Direction("new", "new"),
    Direction("mean", "meaning"),
    Direction("score", "score"),
)

val defaultAbout = """
This game of "Hangman" is Copyright(c) 1992-2018 Luiz Claudio Valdetaro.
Copyright(c) 2018-2025 Valdetaro Consulting, LLC, DBA Gepetto Club.
 Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
All Rights Reserved.""".trimIndent()

val hangmanGame = Game(
    nickName = "hangman",
    echo = false,
    composableTextGame = true,
    gameClass = HangmanKotlin(),
    title = "Hangman",
    menuTitle = "Hangman",
    version = "1.0",
    gameType = GameType.SKILL,
    helpFile = DownloadableFile("hangman.md"),
    about = defaultAbout,
    directions = directionHangman,
    directionColumns = 8,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

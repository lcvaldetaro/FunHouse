package elizakotlin.utils
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString


import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

import jni.utils.ElizaKotlin

fun installFiles() {
    // install game files
    try {
        // Eliza
        installFile("eliza.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout: String = """
    ${getString(R.string.eliza_about)}
     Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
    """.trimIndent()


val elizaGame = Game(
    nickName = "eliza",
    echo = false,
    composableTextGame = true,
    gameClass = ElizaKotlin(),
    title = "Eliza the psychotherapist chatbot game",
    menuTitle = "Eliza",
    version = "1.0",
    gameType = GameType.OTHER,
    helpFile = DownloadableFile("eliza.md"),
    about = defaultAbout,
    directions = listOf(),
    directionColumns = 6,
    useBackgroundBitmap = true,
    forceLight = true,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

package blackjack.utils
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString


import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile


import jni.Blackjack

fun installFiles() {
    // install game files
    try {
        installFile("blackjack.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultHelp: String
    get() = getString(R.string.blackjack_help)

val defaultBlackjackDirections : List<Direction>
    get() = listOf(
        Direction(getString(R.string.deal_label).ifEmpty { "Deal" }, "deal"),
        Direction(getString(R.string.hit_label).ifEmpty { "hit" }, "hit"),
        Direction(getString(R.string.stand_label).ifEmpty { "stand" }, "stand"),
        Direction(getString(R.string.score_label).ifEmpty { "score" }, "score"),
        Direction(getString(R.string.about_label).ifEmpty { "about" }, "about"),
    )


val defaultAbout = getString(R.string.blackjack_about)

val defaultGame: Game
    get() = Game(
        nickName = "blackjack",
        composableTextGame = true,
        gameClass = Blackjack(),
        title = "Blackjack",
        menuTitle = "Blackjack",
        version = "1.0",
        gameType = GameType.SKILL,
        helpFile = DownloadableFile("blackjack.md"),
        useBackgroundBitmap = true,
        forceLight = true,
        about = defaultAbout,
        directions = defaultBlackjackDirections,
        directionColumns = 6,
        localGame = LocalGame(
            icon = 0,
            image = 0,
        )
    )

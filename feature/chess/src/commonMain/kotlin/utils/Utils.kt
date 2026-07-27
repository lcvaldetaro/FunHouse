package chess.utils

import com.funhouse.shared.common.TABLE_COLOR_GREEN
import com.funhouse.shared.common.TABLE_COLOR_BLACK
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.isDark
import club.gepetto.composeutils.isLandscape

import com.funhouse.shared.common.GepettoSubscription
import com.funhouse.shared.common.currentSubscription
import jni.ChessView
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile
import club.gepetto.gcadslib.ui.AdNativeLargeBanner
import club.gepetto.gcadslib.ui.AdNativeLeaderboard
import com.funhouse.shared.common.ADS_REFRESH

fun installFiles() {
    // install game files
    try {
        installFile("chess.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val chessGame = Game(
    nickName = "chess",
    echo = false,
    title = "Chess",
    menuTitle = "Chess",
    version = "1.0",
    composableGame = true,
    gameType = GameType.SKILL,
    about = """
 """.trimIndent(),
    directions = listOf(),
    directionColumns = 6,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)


@Composable
fun ChessRoot(
    modifier: Modifier = Modifier,
    onHelpClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    GcTheme {
        BoxWithConstraints(modifier.fillMaxSize().background(color = if (isDark()) TABLE_COLOR_BLACK else TABLE_COLOR_GREEN)) {
            val isLarge = this.maxWidth >= 800.dp
            val adHeight = if (isLarge) 90.dp else 100.dp
            val ads = currentSubscription == GepettoSubscription.NONE
            ChessView(
                onExit = onExit,
                modifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
                    .align(Alignment.TopCenter)
                    .padding(bottom = if (ads && !isLandscape()) adHeight else 0.dp)
            )
            if (ads && !isLandscape()) {
                val adModifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
                    .align(Alignment.BottomCenter)
                if (isLarge) {
                    AdNativeLeaderboard(adModifier, refreshTimer = ADS_REFRESH)
                } else {
                    AdNativeLargeBanner(adModifier, refreshTimer = ADS_REFRESH)
                }
            }
        }
    }
}

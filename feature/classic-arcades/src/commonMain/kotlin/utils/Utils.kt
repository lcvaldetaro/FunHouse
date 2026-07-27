package classicarcades.utils
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import com.funhouse.shared.common.TABLE_COLOR_BLACK
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.isLandscape
import club.gepetto.gcadslib.ui.AdNativeLargeBanner
import club.gepetto.gcadslib.ui.AdNativeLeaderboard

import com.funhouse.shared.common.ADS_REFRESH
import com.funhouse.shared.common.GepettoSubscription
import com.funhouse.shared.common.currentSubscription
import com.funhouse.shared.common.AppData
import jni.InvadersView
import jni.PINBALL_TABLE_BACKGROUND_COLOR
import jni.PaddleBallView
import jni.PinballView
import jni.PolePositionView
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    // install game files
    try {
        installFile("paddleball.md")
        installFile("aliens.md")
        installFile("pinball.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val retroCircuitGame = Game(
        nickName = "retrocircuit",
        echo = false,
        library = "",
        title = "Retro Circuit",
        menuTitle = "Retro Circuit",
        version = "1.0",
        composableGame = true,
        gameType = GameType.ARCADE,
        //helpFile = DownloadableFile("secretforest.md"),
        about = """
            
        """.trimIndent(),
        directions = listOf(),
        directionColumns = 6,
        localGame = LocalGame(
            icon = 0,
            image = 0,
        )
    )

val paddleAbout = """
 """.trimIndent()

val paddleBallGame = Game(
        nickName = "paddleball",
        echo = false,
        library = "",
        title = "Paddle Ball",
        menuTitle = "Paddle Ball",
        version = "1.0",
        composableGame = true,
        gameType = GameType.ARCADE,
        //helpFile = DownloadableFile("secretforest.md"),
        about = paddleAbout,
        directions = listOf(),
        directionColumns = 6,
        localGame = LocalGame(
            icon = 0,
            image = 0,
        )
    )

val alienInvadersAbout = """
 """.trimIndent()

val aliensGame = Game(
        nickName = "aliens",
        echo = false,
        library = "",
        title = "Alien Invaders",
        menuTitle = "Alien Invaders",
        version = "1.0",
        composableGame = true,
        gameType = GameType.ARCADE,
        //helpFile = DownloadableFile("secretforest.md"),
        about = alienInvadersAbout,
        directions = listOf(),
        directionColumns = 6,
        localGame = LocalGame(
            icon = 0,
            image = 0,
        )
    )

val pinballAbout = """
 """.trimIndent()

val pinballGame = Game(
        nickName = "pinball",
        echo = false,
        library = "",
        title = "Pinball",
        menuTitle = "Pinball",
        version = "1.0",
        composableGame = true,
        gameType = GameType.ARCADE,
        //helpFile = DownloadableFile("secretforest.md"),
        about = pinballAbout,
        directions = listOf(),
        directionColumns = 6,
        localGame = LocalGame(
            icon = 0,
            image = 0,
        )
    )

@Composable
fun PinballRoot(
    modifier: Modifier = Modifier,
    onHelpClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onVoiceClicked: (Boolean) -> Unit = {},
    onExit: () -> Unit = {}
) {
    GcTheme {
        Box(modifier.fillMaxSize().background(PINBALL_TABLE_BACKGROUND_COLOR
        )) {
            PinballView(
                onExit = onExit,
                modifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
            )
        }
    }
}

@Composable
fun InvadersRoot(
    modifier: Modifier = Modifier,
    onHelpClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onVoiceClicked: (Boolean) -> Unit = {},
    onExit: () -> Unit = {}
) {
    GcTheme {
        Box(modifier.fillMaxSize().background(TABLE_COLOR_BLACK)) {
            InvadersView(
                onExit = onExit,
                modifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
            )
        }
    }
}


@Composable
fun PoleRoot(
    modifier: Modifier = Modifier,
    onHelpClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onVoiceClicked: (Boolean) -> Unit = {},
    onExit: () -> Unit = {}
) {
    GcTheme {
        Box(modifier.fillMaxSize().background(TABLE_COLOR_BLACK)) {
            PolePositionView(
                onBack = onExit,
                modifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
            )
        }
    }
}


@Composable
fun PaddleBallRoot(
    modifier: Modifier = Modifier,
    onHelpClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onVoiceClicked: (Boolean) -> Unit = {},
    onExit: () -> Unit = {}
) {
    GcTheme {
        BoxWithConstraints(modifier.fillMaxSize().background(TABLE_COLOR_BLACK)) {
            val isLarge = this.maxWidth >= 800.dp
            val adHeight = if (isLarge) 90.dp else 100.dp
            val ads = currentSubscription == GepettoSubscription.NONE

            PaddleBallView(
                modifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
                    .padding(bottom = if (ads && !isLandscape()) adHeight else 0.dp)) { onExit() }
            if (ads && !isLandscape()) {
                val adModifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
                    .align(Alignment.BottomCenter)
                if (isLarge) {
                    AdNativeLeaderboard(darkMode = true, modifier = adModifier, refreshTimer = ADS_REFRESH)
                } else {
                    AdNativeLargeBanner(darkMode = true, modifier = adModifier, refreshTimer = ADS_REFRESH)
                }
            }
        }
    }
}

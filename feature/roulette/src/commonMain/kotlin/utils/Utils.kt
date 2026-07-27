package roulette.utils

import com.funhouse.shared.common.TABLE_COLOR_GREEN
import androidx.compose.foundation.background
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
import club.gepetto.gcadslib.ui.AdNativeBanner
import club.gepetto.gcadslib.ui.AdNativeLargeBanner
import club.gepetto.gcadslib.ui.AdNativeLeaderboard

import com.funhouse.shared.common.ADS_REFRESH
import com.funhouse.shared.common.GepettoSubscription
import com.funhouse.shared.common.currentSubscription
import jni.RouletteView
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    // install game files
    try {
        installFile("roulette.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
 """.trimIndent()

val defaultGame = Game(
    nickName = "roulette",
    echo = false,
    library = "",
    title = "Roulette",
    menuTitle = "Roulette",
    version = "1.0",
    composableGame = true,
    gameType = GameType.LUCK,
    //helpFile = DownloadableFile("secretforest.md"),
    about = defaultAbout,
    directions = listOf(),
    directionColumns = 6,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

@Composable
fun GameRoot(
    modifier: Modifier = Modifier,
    onHelpClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    GcTheme {
        BoxWithConstraints(modifier.fillMaxSize().background(TABLE_COLOR_GREEN)) {
            val isLarge = !isLandscape() && this.maxWidth >= 800.dp
            val adHeight = if (isLarge) 90.dp else 100.dp
            val ads = currentSubscription == GepettoSubscription.NONE
            RouletteView(
                onHelpClicked = onHelpClicked,
                onAboutClicked = onAboutClicked,
                onExit = onExit,
                modifier = Modifier
                    .displayCutoutPadding().statusBarsPadding().systemBarsPadding()
                    .align(Alignment.TopCenter)
                    .padding(bottom = if (isLandscape() || !ads) 0.dp else adHeight)
            )
            if (ads) {
                if (isLandscape()) {
                    AdNativeBanner(
                        refreshTimer = ADS_REFRESH,
                        modifier = Modifier
                            .displayCutoutPadding().statusBarsPadding().systemBarsPadding()
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 50.dp, end = 8.dp)
                    )
                } else {
                    val adModifier = Modifier
                        .displayCutoutPadding().statusBarsPadding().systemBarsPadding()
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
}

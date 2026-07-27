package poker.utils
import androidx.compose.foundation.clickable

import com.funhouse.shared.common.utils.Preview

import com.funhouse.shared.common.TABLE_COLOR_GREEN
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
import jni.PokerView
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    // install game files
    try {
        installFile("poker.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
 """.trimIndent()

val defaultGame = Game(
    nickName = "poker",
    echo = false,
    library = "",
    title = "Poker",
    menuTitle = "Poker",
    version = "1.0",
    composableGame = true,
    gameType = GameType.SKILL,
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
    onExit: () -> Unit= {}
) {
    GcTheme {
        BoxWithConstraints(modifier.fillMaxSize().background(TABLE_COLOR_GREEN)) {
            val isLarge = this.maxWidth >= 800.dp
            val adHeight = if (isLarge) 90.dp else 100.dp
            val ads = currentSubscription == GepettoSubscription.NONE
            GcImage(
                imageResource = 0,
                modifier = Modifier.align(Alignment.Center).fillMaxSize()
            )
            PokerView(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
                    .padding(bottom = if (ads && !isLandscape()) adHeight else 0.dp)
            ) { onExit() }
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

@Preview
@Composable
private fun PreviewFunc() {
    GameRoot {  }
}

@Composable
private fun GcImage(
    imageResource: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    androidx.compose.foundation.Image(
        painter = org.jetbrains.compose.resources.painterResource(imageResource),
        contentDescription = null,
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        contentScale = contentScale
    )
}

@Composable
private fun GcImage(
    imageResource: Int?,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    // No-op box for dummy / layout placeholder integer resources
    androidx.compose.foundation.layout.Box(modifier)
}

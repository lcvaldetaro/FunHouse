package slotmachine.utils

import com.funhouse.shared.common.TABLE_COLOR_BLACK
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import club.gepetto.gcadslib.ui.AdNativeLargeBanner

import com.funhouse.shared.common.ADS_REFRESH
import com.funhouse.shared.common.GepettoSubscription
import com.funhouse.shared.common.currentSubscription
import jni.SlotMachineView
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    // install game files
    try {
        installFile("slotmachine.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
 """.trimIndent()

val defaultGame = Game(
    nickName = "slotmachine",
    echo = false,
    library = "",
    title = "Slot Machine",
    menuTitle = "Slot Machine",
    version = "1.0",
    composableGame = true,
    gameType = GameType.LUCK,
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
        Box(modifier.fillMaxSize().background(TABLE_COLOR_BLACK)) {
            SlotMachineView(
                modifier = Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
                    .align(Alignment.BottomCenter)
            ) { onExit() }
            if (currentSubscription == GepettoSubscription.NONE)
                AdNativeLargeBanner(
                    darkMode = true,
                    refreshTimer = ADS_REFRESH,
                    modifier = Modifier
                        .displayCutoutPadding()
                        .statusBarsPadding()
                        .systemBarsPadding()
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
        }
    }
}

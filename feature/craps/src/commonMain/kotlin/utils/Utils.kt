package craps.utils
import com.funhouse.shared.common.utils.Preview
import com.funhouse.shared.common.generated.resources.Res as CommonR
import com.funhouse.shared.common.generated.resources.underconstruction
import com.funhouse.shared.common.generated.resources.backarrow
import com.funhouse.shared.common.generated.resources.ic_profile
import com.funhouse.shared.common.generated.resources.call_spk_on
import com.funhouse.shared.common.generated.resources.speaker_off


import com.funhouse.shared.common.TABLE_COLOR_GREEN
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import club.gepetto.composeutils.GcTheme

import jni.CrapsView
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    // install game files
    try {
        installFile("craps.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
 """.trimIndent()

val defaultGame = Game(
    nickName = "craps",
    echo = false,
    library = "",
    title = "Craps",
    menuTitle = "Craps",
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
        Box(modifier.fillMaxSize().background(TABLE_COLOR_GREEN)) {
            CrapsView(
                onExit = onExit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .systemBarsPadding()
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFunc() {
    GameRoot {}
}



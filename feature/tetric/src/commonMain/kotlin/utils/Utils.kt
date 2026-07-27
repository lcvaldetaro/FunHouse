package tetric.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

import com.gepetto.tetric.logic.SoundUtil
import com.gepetto.tetric.ui.TetricView

fun installFiles() {
    // install game files
    installFile("tetric.md")
    try {
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
    """.trimIndent()

val defaultGame = Game(
    nickName = "tetric",
    title = "Tetric",
    version = "1.0",
    gameType = GameType.ARCADE,
    composableGame = true,
    secretGame = true,
    forceDark = true,
    helpFile = DownloadableFile("tetric.md"),
    directionColumns = 5,
    about = defaultAbout,
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
    (LocalContext.current as? android.content.Context)?.let { SoundUtil.init(it) }
    TetricView(modifier) { onExit() }
}




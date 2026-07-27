import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import club.gepetto.utils.GcAppInfo
import com.funhouse.shared.common.AppData
import com.gepetto.funhouse.Constants
import com.gepetto.funhouse.models.installAssetFiles
import com.gepetto.funhouse.ui.main.MainView
import java.io.File
import org.jetbrains.compose.resources.painterResource
import com.funhouse.shared.common.generated.resources.*
import com.funhouse.shared.common.generated.resources.funhouseicon
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.gepetto.funhouse.intentprocessors.FunHouseIntentProcessor

fun main() {
    startKoin {
        modules(module {
            single { FunHouseIntentProcessor() }
        })
    }

    val cacheDir = club.gepetto.utils.getAppDataDir("com.gepetto.gamescollection")
    GcAppInfo.filesDir = cacheDir
    GcAppInfo.appPackageFolder = cacheDir.absolutePath + File.separator

    AppData.appPackage = "com.gepetto.gamescollection"
    AppData.packageFolder = cacheDir.absolutePath
    AppData.packageFolderFile = cacheDir
    AppData.gameFolder = Constants.GAMES_FOLDER
    AppData.gameFolderFile = File(cacheDir, Constants.GAMES_FOLDER)
    (AppData.gameFolderFile as File).mkdirs()
    AppData.appName = "FunHouse"

    val osName = System.getProperty("os.name").lowercase()
    val isMac = osName.contains("mac")
    val desktopCode = if (isMac) com.gepetto.gamescollection.CommonConfig.desktopVersionCode else com.gepetto.gamescollection.CommonConfig.desktopVersionCode + 1

    GcAppInfo.versionName = com.gepetto.gamescollection.CommonConfig.versionName
    GcAppInfo.versionCode = desktopCode
    GcAppInfo.releaseVersion = true

    AppData.version = "${com.gepetto.gamescollection.CommonConfig.versionName}/bld $desktopCode"
    AppData.versionCode = desktopCode
    AppData.releaseVersion = GcAppInfo.releaseVersion
    AppData.secretGamesEnabled = !AppData.releaseVersion

    club.gepetto.GcLog.plant(club.gepetto.GcLog.DebugTree())

    installAssetFiles()

    application {
        val windowState = rememberWindowState(
            width = 1200.dp,
            height = 800.dp
        )
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "FunHouse",
            icon = painterResource(Res.drawable.funhouseicon)
        ) {
            AppData.darkMode = isSystemInDarkTheme()
            MainView()
        }
    }
}

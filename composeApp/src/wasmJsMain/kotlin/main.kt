import java.io.File
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import club.gepetto.utils.GcAppInfo
import com.funhouse.shared.common.AppData
import com.gepetto.gamescollection.CommonConfig
import com.gepetto.funhouse.ui.main.MainView
import com.funhouse.shared.common.generated.resources.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.gepetto.funhouse.intentprocessors.FunHouseIntentProcessor

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(module {
            single { FunHouseIntentProcessor() }
        })
    }
    GcAppInfo.versionName = CommonConfig.versionName
    GcAppInfo.versionCode = CommonConfig.webVersionCode

    AppData.appPackage = "com.gepetto.gamescollection"
    AppData.appName = "FunHouse"
    AppData.version = "${CommonConfig.versionName} (${CommonConfig.webVersionCode})"
    AppData.versionCode = CommonConfig.webVersionCode
    AppData.releaseVersion = true
    AppData.secretGamesEnabled = false
    AppData.packageFolder = ""
    AppData.gameFolder = ""
    AppData.packageFolderFile = File("")
    AppData.gameFolderFile = File("")

    ComposeViewport(viewportContainerId = "compose-App") {
        var isLoaded by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            val fileNames = listOf(
                "blackjack.md", "castle.md", "chess.md", "chimaera.md", "paddleball.md", 
                "alieninvaders.md", "pinball.md", "adventure.help", "adventure.text", 
                "adventure.data", "adventure.md", "craps.md", "dinkum.md", "eliza.md", 
                "funhouselicense.txt", "aegisquest.md", "island.md", "islandgoals.csv", 
                "funhouse.csv", "islandobjects.csv", "funhouseplaces.csv", "funhouse.md", 
                "funhousegoals.csv", "funhouseobjects.csv", "islandgame.csv", "island.csv", 
                "island.json", "aegisquestplaces.csv", "aegisquest.csv", "aegisquestobjects.csv", 
                "aegisquest.json", "islandplaces.csv", "aegisquestgoals.csv", "islandlicense.txt", 
                "funhouse.json", "hangman.md", "mansion.html", "poker.md", "roulette.md", 
                "secretforest.md", "slotmachine.md", "bsdlicense.txt", "themedata.thmx", 
                "filelist.xml", "spacewars.md", "tetric.md", "wanderlibrary.json", 
                "library.misc", "wandertut.md", "gnulicense.txt", "tut.wrld", "castle.wrld", 
                "misc.nr", "wanderlibrary.md", "wandertut.json", "a3.wrld", 
                "wandercastle.md", "a3.misc", "wandera3.md", "castle.misc", 
                "wandercastle.json", "tut.misc", "library.wrld", "wizardscastle.md",
                "about_de.md", "about_en.md", "about_es.md", "about_fr.md", "about_it.md", "about_pt.md",
                "privacy_de.md", "privacy_en.md", "privacy_es.md", "privacy_fr.md", "privacy_it.md", "privacy_pt.md"
            )
            for (file in fileNames) {
                try {
                    val bytes = Res.readBytes("files/$file")
                    com.funhouse.shared.common.utils.AssetCache.cache[file] = bytes.decodeToString()
                } catch (e: Exception) {
                    // Ignore missing files
                }
            }
            isLoaded = true
        }
        
        if (isLoaded) {
            MainView()
        } else {
            Text("Loading game resources...")
        }
    }
}

package com.gepetto.funhouse.intentprocessors
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import androidx.lifecycle.viewModelScope
import club.gepetto.circum.CircumEffect
import club.gepetto.circum.CircumIntentProcessor
import club.gepetto.composeutils.models.GcNotes
import club.gepetto.gcadslib.AnalyticsTracker
import com.funhouse.shared.common.GAMES_FOLDER
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.models.Game
import com.gepetto.funhouse.models.GameList
//import jni.Castle
import jni.Chess
//import jni.Chimaera
//import jni.Craps
//import jni.Dinkum
//import jni.Dungeon
import com.funhouse.shared.common.jni.GameInterface
import jni.GengameKotlin
//import jni.Hangman
//import jni.Mansion
//import jni.Poker
//import jni.Roulette
//import jni.SecretForest
//import jni.SlotMachine
//import jni.SpaceWars
//import jni.Tetric
//import jni.Wander
import jni.WanderKotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.Settings
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog


//import jni.WizardsCastle

class FunHouseIntentProcessor : CircumIntentProcessor<FunHouseState, FunHouseIntentCommand, CircumEffect>() {
    var textView = ""
    lateinit var currentGame: Game
    var gameList = GameList(gameFiles = emptyList(), games = emptyList())
    var cached: Boolean = false
    var currentGameType: GameType? = null
    var settings = Settings.restore()
    var gameLibrary: GameInterface = run {
        GengameKotlin()
    }


    init {
       getGamesList()
    }

    override fun onIntentCommand(intent: FunHouseIntentCommand, state: FunHouseState?) {
        when (intent) {
            is FunHouseIntentCommand.Initial -> {
                setState(FunHouseState.Initial)
            }

            is FunHouseIntentCommand.UpdateGameType -> {
                currentGameType = intent.gameType
            }

            is FunHouseIntentCommand.GotoLoaded -> {
                setState(FunHouseState.Loaded(intent.gameList, intent.cached, gameType = currentGameType))
            }

            is FunHouseIntentCommand.FunHouseActionClickedCommand -> onActionClicked(intent, state)

            is FunHouseIntentCommand.CommandEntered -> {
                if (currentGame.echo)
                    textView = "$textView\n${intent.command}\n"

                if (currentGame.composableTextGame) {
                    val engine = currentGame.gameClass ?: gameLibrary
                    engine.sendCommand(intent.command)
                    setState(FunHouseState.TextGamePlay(currentGame, textView, settings.usingVoice))
                }
                else {
                    gameLibrary.sendCommand(intent.command)
                    setState(FunHouseState.NativeTextGamePlay(currentGame, textView, settings.usingVoice))
                }
            }

            is FunHouseIntentCommand.SettingsUpdated -> {
                settings = intent.settings
                state?.let { setState(it) }
            }
        }
    }

    private fun onActionClicked(intent: FunHouseIntentCommand.FunHouseActionClickedCommand, state: FunHouseState?) {
        when (intent.action) {
            is FunHouseAction.HomeClicked -> {
                currentGameType = null
                setState(FunHouseState.Loaded(gameList = gameList, gameType = currentGameType, forceType = true))
            }
            is FunHouseAction.GotoUrl ->
                setState(FunHouseState.WebPageState(intent.action.url))
            is FunHouseAction.AboutClicked ->
                setState(FunHouseState.AboutState)
            is FunHouseAction.PrivacyClicked -> {
                val fromAbout = state is FunHouseState.AboutState
                setState(FunHouseState.PrivacyState(fromAbout = fromAbout))
            }
            is FunHouseAction.NotesClicked -> {
                val game = when (state) {
                    is FunHouseState.NativeTextGamePlay -> state.game
                    is FunHouseState.TextGamePlay -> state.game
                    else -> null
                }
                if (game != null) {
                    val filename = "${game.nickName}.notes"
                    val notes = GcNotes.restore(AppData.packageFolder, filename).copy(title = "${game.title} notes")
                    setState(FunHouseState.GameNotes(game, filename, notes))
                }
            }

            is FunHouseAction.NotesExited -> {
                if (state is FunHouseState.GameNotes)
                    popState()
            }

            is FunHouseAction.BackClicked -> {
                GcLog.d("Back clicked")
                if (state is FunHouseState.PrivacyState && state.fromAbout) {
                    setState(FunHouseState.AboutState)
                } else {
                    if (state is FunHouseState.Loaded)
                        System.exit(0)
                    if (state is FunHouseState.TextGamePlay || state is FunHouseState.NativeTextGamePlay ||
                        state is FunHouseState.ComposableGamePlay || state is FunHouseState.ActivityGamePlay) {
                        try {
                            val engine = currentGame.gameClass ?: gameLibrary
                            engine.stop()
                        } catch (e: Exception) {
                            GcLog.e("Error stopping game engine on BackClicked", e)
                        }
                    }
                    setState(FunHouseState.Loaded(gameList = gameList, gameType = currentGameType))
                }
            }

            is FunHouseAction.GameResumeClicked -> {
                currentGame = intent.action.game
                GcLog.d("Resuming game ${currentGame.nickName}")
                if (currentGame.composableTextGame) {
                    val engine = currentGame.gameClass ?: gameLibrary
                    engine.registerTerminalCallback { newData -> updateState(newData) }
                    setState(
                        FunHouseState.TextGamePlay(
                            game = currentGame,
                            textView = textView,
                            usingVoice = settings.usingVoice,
                            resume = true
                        )
                    )
                } else {
                    gameLibrary.registerTerminalCallback { newData -> updateState(newData) }
                    setState(
                        FunHouseState.NativeTextGamePlay(
                            game = currentGame,
                            textView = textView,
                            usingVoice = settings.usingVoice,
                            resume = true
                        )
                    )
                }
            }

            is FunHouseAction.GameStartClicked -> {
                currentGame = intent.action.game
                currentSettings.currentGame = currentGame
                GcLog.d("Starting game ${currentGame.nickName}")
                com.funhouse.shared.common.utils.clearTerminalText()

                AnalyticsTracker.logScreenView(currentGame.nickName)

                val about = getString(R.string.game_version_template, currentGame.version, currentGame.about)

                gameLibrary = when {
                    /*
                    (currentGame.nickName == "tetric") -> {
                        Tetric(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }

                    (currentGame.nickName == "slotmachine") -> {
                        SlotMachine(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }

                    (currentGame.nickName == "roulette") -> {
                        Roulette(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }

                    (currentGame.nickName == "poker") -> {
                        Poker(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }

                    (currentGame.nickName == "craps") -> {
                        Craps(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }
                    */

                    (currentGame.nickName == "chess") -> {
                        // Chess(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                        Chess()
                    }

                    //(currentGame.nickName == "adventureC") -> {
                    //    Adventure(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    //}

                    /*(currentGame.nickName == "blackjack") -> {
                        Blackjack(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }*/

                    /*(currentGame.nickName == "chimaera") -> {
                        Chimaera(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }*/

                    /*(currentGame.nickName == "dinkum") -> {
                        Dinkum(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    }*/

                    //(currentGame.nickName == "elizaC") -> {
                    //    Eliza(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    //}

                    //(currentGame.nickName == "hangman") -> {
                    //    Hangman(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    //}

                    //(currentGame.nickName == "secretforest") -> {
                    //    SecretForest(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    //}

//                    (currentGame.nickName == "spacewars") -> {
//                        SpaceWars(currentGame.library, Settings(), GAMES_FOLDER, about, null)
//                    }

                    //(currentGame.nickName == "castle") -> {
                    //    Castle(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    //}

                    //(currentGame.nickName == "mansion") -> {
                    //    Mansion(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    //}

                    //(currentGame.nickName == "dungeon") -> {
                    //    Dungeon(currentGame.library, Settings(), GAMES_FOLDER, about, null)
                    //}

//                    (currentGame.nickName == "wizardscastle") -> {
//                        WizardsCastle(currentGame.library, Settings(), GAMES_FOLDER, about, null)
//                    }

                    // Wander networked game
                    (currentGame.nickName.startsWith("wander")) -> {
                        WanderKotlin(currentGame.library, Settings(), GAMES_FOLDER, about, currentGame.nickName, null)
                    }

                    else -> {
                        // Assume it is Gengame networked
                        GengameKotlin()
                    }
                }

                if (com.funhouse.shared.common.utils.isWebTarget && isThreadBasedGame(currentGame)) {
                    textView = "\n\nThis classic game requires background thread scheduling and is not supported in the web browser. Please run the Android or Desktop version of FunHouse to play!\n\n"
                    setState(FunHouseState.TextGamePlay(game = currentGame, textView = textView, usingVoice = settings.usingVoice))
                    return
                }

                when {
                    currentGame.composableTextGame -> {
                        textView = ""
                        val engine = currentGame.gameClass ?: gameLibrary
                        engine.registerTerminalCallback({ newData -> updateState(newData) })
                        engine.start(currentGame.nickName, currentGame.multiPlayer)
                        setState(FunHouseState.TextGamePlay(game = currentGame, textView = textView, usingVoice = settings.usingVoice))
                    }
                    currentGame.composableGame ->
                        setState(FunHouseState.ComposableGamePlay(game = currentGame))
                    currentGame.activityGame ->
                        setState(FunHouseState.ActivityGamePlay(game = currentGame))
                    else -> {
                        gameLibrary.registerTerminalCallback({ newData -> updateState(newData) })
                        textView = ""
                        gameLibrary.start(currentGame.nickName, currentGame.multiPlayer)

                        // TODO - We need to create a boolean on the game definition (textGame = true | false),
                        // and set a different state for non-text games - think of a way so we don't have to modify the code, we
                        // just, for example, pass some kind of composable to the state, and

                        setState(FunHouseState.NativeTextGamePlay(game = currentGame, textView = textView, usingVoice = settings.usingVoice))
                    }
                }

                /*if (currentGame.composableTextGame) {
                    textView = ""

                    currentGame.gameClass?.registerTerminalCallback({ newData -> updateState(newData) })
                    currentGame.gameClass?.start(currentGame.nickName)

                    setState(FunHouseState.TextGamePlay(game = currentGame, textView = textView, usingVoice = settings.usingVoice))
                }
                else
                if (currentGame.composableGame) {
                    setState(FunHouseState.ComposableGamePlay(game = currentGame))
                }
                else
                if (currentGame.activityGame) {
                    setState(FunHouseState.ActivityGamePlay(game = currentGame))
                }
                else {
                    gameLibrary.registerTerminalCallback({ newData -> updateState(newData) })
                    textView = ""
                    gameLibrary.start(currentGame.nickName)

                    // TODO - We need to create a boolean on the game definition (textGame = true | false),
                    // and set a different state for non-text games - think of a way so we don't have to modify the code, we
                    // just, for example, pass some kind of composable to the state, and

                    setState(
                        FunHouseState.NativeTextGamePlay(
                            game = currentGame,
                            textView = textView,
                            usingVoice = settings.usingVoice,
                        )
                    )
                }*/
            }

            FunHouseAction.RetryClicked -> {
                setState(FunHouseState.Loading)
                getGamesList()
            }

            is FunHouseAction.CommandEntered -> {
                sendIntentCommand(FunHouseIntentCommand.CommandEntered(intent.action.command))
            }

            is FunHouseAction.SettingsUpdated -> {
                sendIntentCommand(FunHouseIntentCommand.SettingsUpdated(intent.action.settings))
            }

            is FunHouseAction.GameAboutClicked -> {
                setState(FunHouseState.AboutGameState(intent.action.game))
            }

            FunHouseAction.ToggleVoice -> {
                val newUsingVoice = !settings.usingVoice
                settings = settings.copy(usingVoice = newUsingVoice)
                settings.save()
                when (state) {
                    is FunHouseState.NativeTextGamePlay -> {
                        val newState = state.copy(usingVoice = newUsingVoice)
                        setState(newState)
                    }
                    is FunHouseState.TextGamePlay -> {
                        val newState = state.copy(usingVoice = newUsingVoice)
                        setState(newState)
                    }
                    else -> {}
                }
                currentSettings = currentSettings.copy(usingVoice = newUsingVoice)
            }

            is FunHouseAction.GameHelpClicked -> {
                if (intent.action.game.helpFile.fileName.isNotEmpty()) {
                    setState(FunHouseState.GameHelpState(intent.action.game))
                }
            }
        }
    }

    private fun getGamesList() {
        val vm = this

        viewModelScope.launch(Dispatchers.IO) {
            val gameList = GameList.restore()

            if (gameList != null) {
                vm.gameList = gameList
                vm.gameList = vm.gameList.copy(games = vm.gameList.games!!.sortedBy { it.title })
                sendIntentCommand(FunHouseIntentCommand.GotoLoaded(vm.gameList, false))
            } else {
                setState(FunHouseState.Error)
            }
        }
    }

    private fun updateState(text: String) {
        textView = "$textView$text"
        Thread.sleep(currentGame.printDelay.toLong())
        if (currentGame.composableTextGame) {
            setState(FunHouseState.TextGamePlay(currentGame, textView, settings.usingVoice))
        } else {
            setState(FunHouseState.NativeTextGamePlay(currentGame, textView, settings.usingVoice))
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            if (::currentGame.isInitialized) {
                val engine = currentGame.gameClass ?: gameLibrary
                engine.stop()
            } else {
                gameLibrary.stop()
            }
        } catch (e: Exception) {
            GcLog.e("Error stopping engine onCleared", e)
        }
    }

    private fun isThreadBasedGame(game: Game): Boolean {
        if (game.nickName == "eliza") return false
        if (game.nickName.startsWith("wander")) return false
        if (game.nickName == "chess") return false
        if (game.nickName == "adventure") return false
        if (game.nickName == "secretforest") return false
        if (game.nickName == "hangman") return false
        if (game.nickName == "castle" || game.nickName == "mansion" || game.nickName == "dinkum" || game.nickName == "wizardscastle" || game.nickName == "chimaera" || game.nickName == "spacewars" || game.nickName == "blackjack") return false
        if (game.nickName == "funhouse" || game.nickName == "funhousesingle" || game.nickName == "island" || game.nickName == "islandsingle" || game.nickName == "aegisquest" || game.nickName == "aegisquestsingle") return false
        return game.composableTextGame || (!game.composableGame && !game.activityGame)
    }
}

sealed interface FunHouseState {
    data object Initial: FunHouseState
    data object Loading: FunHouseState
    data class Loaded(val gameList: GameList, val cached: Boolean = false, val gameType: GameType? = null, val forceType: Boolean? = null): FunHouseState
    data object Error: FunHouseState
    data class NativeTextGamePlay(val game: Game, val textView: String, val usingVoice: Boolean, val resume: Boolean = false) : FunHouseState
    data class TextGamePlay(val game: Game, val textView: String, val usingVoice: Boolean, val resume: Boolean = false) : FunHouseState
    data class ComposableGamePlay(val game: Game, val resume: Boolean = false) : FunHouseState
    data class ActivityGamePlay(val game: Game, val resume: Boolean = false) : FunHouseState
    data class GameNotes(val game: Game, val filename: String, val notes: GcNotes) : FunHouseState
    data class SettingsState(val settings: Settings): FunHouseState
    data object AboutState: FunHouseState
    data class PrivacyState(val fromAbout: Boolean = false): FunHouseState
    data class AboutGameState(val game: Game) : FunHouseState
    data class GameHelpState(val game: Game) : FunHouseState
    data object ExitState: FunHouseState
    data class WebPageState(val url: String): FunHouseState
}

sealed interface FunHouseIntentCommand {
    data object Initial: FunHouseIntentCommand
    data class GotoLoaded(val gameList: GameList, val cached: Boolean): FunHouseIntentCommand
    data class FunHouseActionClickedCommand(val action: FunHouseAction): FunHouseIntentCommand
    data class CommandEntered(val command: String, ) : FunHouseIntentCommand
    data class SettingsUpdated(val settings: Settings) : FunHouseIntentCommand
    data class UpdateGameType(val gameType: GameType?) : FunHouseIntentCommand
}

sealed interface FunHouseAction {
    data object BackClicked: FunHouseAction
    data object RetryClicked: FunHouseAction
    data class GameStartClicked(val game: Game) : FunHouseAction
    data class GameResumeClicked(val game: Game) : FunHouseAction
    data class CommandEntered(val command: String) : FunHouseAction
    data class SettingsUpdated(val settings: Settings) : FunHouseAction
    data object ToggleVoice: FunHouseAction
    data class GameAboutClicked(val game: Game) : FunHouseAction
    data class GameHelpClicked(val game: Game) : FunHouseAction
    data object NotesClicked : FunHouseAction
    data object NotesExited : FunHouseAction
    data object AboutClicked: FunHouseAction
    data object PrivacyClicked: FunHouseAction
    data object HomeClicked: FunHouseAction
    data class GotoUrl(val url: String) : FunHouseAction
}
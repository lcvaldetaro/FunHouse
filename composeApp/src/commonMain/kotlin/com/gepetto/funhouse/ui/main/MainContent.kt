package com.gepetto.funhouse.ui.main

import com.funhouse.shared.common.utils.CommonBackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color



import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.editor.GcFileEditor
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.webpage.GcWebPageScreen
import com.funhouse.shared.common.AppData
import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.intentprocessors.FunHouseIntentCommand
import com.gepetto.funhouse.intentprocessors.FunHouseState
import com.gepetto.funhouse.models.ActivityGameRoot
import com.gepetto.funhouse.models.ComposableGameRoot


import com.gepetto.funhouse.ui.common.About
import com.gepetto.funhouse.ui.game.GameHelp
import com.gepetto.funhouse.ui.game.TextGamePlay
import com.gepetto.funhouse.ui.common.SettingsContent
import club.gepetto.GcLog
import club.gepetto.composeutils.resetDarkMode
import com.gepetto.funhouse.ui.common.Privacy

@Composable
fun MainContent(
    state: FunHouseState,
    modifier: Modifier = Modifier,
    onClickAction: (FunHouseAction) -> Unit,
    onIntentCommand: (FunHouseIntentCommand) -> Unit,
    onThemeChanged: () -> Unit = {}
) {
    val e2eModifier = Modifier.displayCutoutPadding().statusBarsPadding().systemBarsPadding()


    CommonBackHandler(true) { onClickAction(FunHouseAction.BackClicked) }

    Surface {
        val background =
            if (state == FunHouseState.Loading) Color.Gray.copy(alpha = 0.75f) else sysBackgroundColor()
        Box(modifier = modifier.fillMaxSize().background(background)) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                when (state) {
                    is FunHouseState.Loading -> MainLoadingState()

                    is FunHouseState.WebPageState -> {
                        GcLog.d("Executing url '${state.url}'")
                        GcWebPageScreen(
                            url = state.url,
                            closeIcon = false,
                            backIcon = true,
                            onBackClick = { onClickAction(FunHouseAction.BackClicked) },
                            modifier = e2eModifier
                        )
                    }

                    is FunHouseState.ExitState -> {}

                    is FunHouseState.Error -> MainErrorState(
                        onClickAction = onClickAction,
                        modifier = e2eModifier
                    )

                    is FunHouseState.AboutState -> About(
                        onClickAction = onClickAction,
                        modifier = e2eModifier
                    )

                    is FunHouseState.PrivacyState -> Privacy(
                        onClickAction = onClickAction,
                        modifier = e2eModifier
                    )

                    is FunHouseState.Initial -> {}

                    is FunHouseState.Loaded -> {
                        MainLoadedState(
                            state = state,
                            onClickAction = onClickAction,
                            onIntentCommand = onIntentCommand,
                            onThemeChanged = onThemeChanged,
                            modifier = e2eModifier
                        )
                    }

                    is FunHouseState.GameNotes -> {
                        GcFileEditor(
                            filename = state.filename,
                            folder = AppData.packageFolder,
                            notes = state.notes,
                            onExit = { onClickAction(FunHouseAction.NotesExited) },
                            modifier = e2eModifier
                        )
                    }

                    is FunHouseState.ComposableGamePlay -> {
                        ComposableGameRoot(state.game, onClickAction = onClickAction)
                    }

                    is FunHouseState.ActivityGamePlay -> {
                        ActivityGameRoot(state.game, onClickAction = onClickAction)
                    }

                    is FunHouseState.TextGamePlay -> {
                        val darkMode = when {state.game.forceDark -> true; state.game.forceLight -> false; else -> AppData.darkMode }

                        GcTheme(darkMode) {
                            TextGamePlay(
                                game = state.game,
                                textView = state.textView,
                                usingVoice = state.usingVoice,
                                commandEntered = { onClickAction(FunHouseAction.CommandEntered(it)) },
                                onClickAction = {
                                    resetDarkMode(AppData.darkMode)
                                    onClickAction(it)
                                },
                                modifier = e2eModifier
                            )
                        }
                    }

                    is FunHouseState.NativeTextGamePlay -> {
                        val darkMode = when {state.game.forceDark -> true; state.game.forceLight -> false; else -> AppData.darkMode }

                        GcTheme(darkMode) {
                            TextGamePlay(
                                game = state.game,
                                textView = state.textView,
                                usingVoice = state.usingVoice,
                                commandEntered = { onClickAction(FunHouseAction.CommandEntered(it)) },
                                onClickAction = {
                                    resetDarkMode(AppData.darkMode)
                                    onClickAction(it)
                                },
                                modifier = e2eModifier
                            )
                        }
                    }

                    is FunHouseState.SettingsState ->
                        SettingsContent(
                            settings = state.settings,
                            onClickAction = onClickAction,
                            modifier = e2eModifier
                        )

                    is FunHouseState.AboutGameState -> {
                        About(game = state.game, modifier = e2eModifier) {
                            onClickAction(
                                FunHouseAction.GameResumeClicked(state.game)
                            )
                        }
                    }

                    is FunHouseState.GameHelpState -> {
                        GameHelp(
                            game = state.game,
                            onClickAction = onClickAction,
                            modifier = e2eModifier
                        )
                    }
                }
            }
        }
    }
}

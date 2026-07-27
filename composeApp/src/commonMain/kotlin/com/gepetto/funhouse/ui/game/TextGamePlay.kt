@file:JvmName("GamePlayKt")

package com.gepetto.funhouse.ui.game
import kotlin.jvm.JvmName
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource


import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale


import androidx.compose.ui.unit.dp
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.image.GcImage
import club.gepetto.composeutils.navbar.GcNavButton
import club.gepetto.composeutils.scaffold.GcAdaptiveScaffold
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysForegroundColor

import com.funhouse.shared.common.models.Game
import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.ui.common.FunHouseTopBar
import kotlinx.collections.immutable.toImmutableList

import club.gepetto.composeutils.isDark
import com.funhouse.shared.common.GepettoSubscription
import com.funhouse.shared.common.currentSubscription
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import club.gepetto.gcadslib.ui.AdNativeBanner
import club.gepetto.gcadslib.ui.AdNativeFullBanner
import club.gepetto.gcadslib.ui.AdNativeLargeBanner
import club.gepetto.gcadslib.ui.AdNativeLeaderboard
import com.funhouse.shared.common.ADS_REFRESH


import com.funhouse.shared.common.utils.CommonBackHandler

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TextGamePlay(
    game: Game,
    commandEntered: (String) -> Unit,
    modifier: Modifier = Modifier,
    textView: String = "",
    usingVoice: Boolean = false,
    onClickAction: (FunHouseAction) -> Unit
) {
    val shouldScroll = remember { mutableStateOf(true) }
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val voiceIcon = if (usingVoice) Icons.Default.VolumeUp else Icons.Default.VolumeMute

    CommonBackHandler(true) { onClickAction(FunHouseAction.BackClicked) }

    val tint = if (isDark() || game.forceDark) Color.White else Color.Black

    val gcButtonList = if (game.soundsOff) {
        mutableListOf(
            GcNavButton(resourceResIcon = Res.drawable.notes, tint = tint, label = stringResource(Res.string.notes), onClick = { onClickAction(FunHouseAction.NotesClicked) }),
            GcNavButton(resourceResIcon = com.funhouse.shared.common.utils.getGameIconResource(game.nickName), vector = false, label = stringResource(Res.string.about), outline = true, rounded = true, onClick = { onClickAction(FunHouseAction.GameAboutClicked(game = game)) })
        )
    }
    else {
        mutableListOf(
            GcNavButton(imageVector = Icons.Default.Note, tint = tint, label = stringResource(Res.string.notes), onClick = { onClickAction(FunHouseAction.NotesClicked) }),
            GcNavButton(imageVector = voiceIcon, tint = tint, label = stringResource(Res.string.voice), onClick = { onClickAction(FunHouseAction.ToggleVoice) }),
            GcNavButton(resourceResIcon = com.funhouse.shared.common.utils.getGameIconResource(game.nickName), vector = false, label = stringResource(Res.string.about), outline = true, rounded = true, onClick = { onClickAction(FunHouseAction.GameAboutClicked(game = game)) })
        )
    }

    if (game.helpFile.fileName.isNotEmpty()) {
        gcButtonList.add(0, GcNavButton(imageVector = Icons.Default.Help, tint = tint, label = stringResource(Res.string.help), onClick = { onClickAction(FunHouseAction.GameHelpClicked(game = game))} ))
    }

    val gCnavButtons = gcButtonList.toImmutableList()

    val foregroundColor =
        when {
            game.forceDark -> Color.White
            game.forceLight -> Color.Black
            else -> sysForegroundColor()
        }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val height = this.maxHeight
        val width = this.maxWidth
        if (game.useBackgroundBitmap) {
            androidx.compose.foundation.Image(
                painter = com.funhouse.shared.common.utils.getGameBackgroundPainter(game.nickName),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
        GcAdaptiveScaffold(
            modifier = Modifier.align(Alignment.Center),
            customNav = if (isKeyboardVisible) NavigationSuiteType.None else null,
            topBar = {
                FunHouseTopBar(
                    hasBackArrow = true,
                    label = game.title,
                    backgroundColor = if (game.useBackgroundBitmap) Color.Transparent else sysBackgroundColor(),
                    foregroundColor = foregroundColor,
                    onClickAction = onClickAction
                )
            },
            gcNavButtons = gCnavButtons,
            foregroundColor = foregroundColor,
            backgroundColor = if (game.useBackgroundBitmap) Color.Transparent else sysBackgroundColor()
        ) { landscape ->
            if (landscape) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(Modifier.weight(7f)) {
                        Column(Modifier.weight(7f).fillMaxHeight()) {
                            TerminalWindow(
                                textView = textView,
                                usingVoice = if (game.soundsOff) false else usingVoice,
                                shouldScroll = shouldScroll.value,
                                modifier = Modifier.padding(10.dp).weight(1f)
                            )

                            CommandLine(
                                label = stringResource(Res.string.command_label),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).imePadding(),
                                onEnterKey = {
                                    commandEntered(it)
                                    shouldScroll.value = true
                                }
                            )
                        }
                        if (!isKeyboardVisible) {
                            DirectionButtons(
                                game = game,
                                landScape = true,
                                modifier = Modifier.fillMaxWidth().padding(2.dp).weight(3f),
                            ) {
                                commandEntered(it)
                                shouldScroll.value = true
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TerminalWindow(
                        textView = textView,
                        usingVoice = if (game.soundsOff) false else usingVoice,
                        shouldScroll = shouldScroll.value,
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    )

                    if (currentSubscription == GepettoSubscription.NONE /*&& !isKeyboardVisible*/) {
                        when {
                            isKeyboardVisible && width < 800.dp -> AdNativeBanner(refreshTimer = ADS_REFRESH)
                            isKeyboardVisible && width >= 800.dp -> AdNativeFullBanner(refreshTimer = ADS_REFRESH)
                            width < 800.dp -> AdNativeBanner(refreshTimer = ADS_REFRESH)
                            width >= 800.dp -> AdNativeLeaderboard(refreshTimer = ADS_REFRESH)
                            else -> AdNativeLargeBanner(refreshTimer = ADS_REFRESH)
                        }
                    }

                    CommandLine(
                        label = stringResource(Res.string.command_label),
                        onEnterKey = {
                            commandEntered(it)
                            shouldScroll.value = true
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).imePadding()
                    )

                    if (!isKeyboardVisible) {
                        DirectionButtons(
                            game = game,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            commandEntered(it)
                            shouldScroll.value = true
                        }
                    }
                }
            }
        }
    }
}

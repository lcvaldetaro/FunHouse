package com.gepetto.funhouse.ui.main
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource



import com.funhouse.shared.common.utils.CommonBackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.clickable
import club.gepetto.composeutils.GcBanner
import club.gepetto.composeutils.GcTheme

import club.gepetto.composeutils.image.GcIcon
import club.gepetto.composeutils.isCompact
import club.gepetto.composeutils.isLandscape
import club.gepetto.composeutils.isLarge
import club.gepetto.composeutils.navbar.GcNavButton
import club.gepetto.composeutils.scaffold.GcAdaptiveLayoutType
import club.gepetto.composeutils.scaffold.GcAdaptiveScaffold
import club.gepetto.composeutils.sysBackgroundColor
import com.funhouse.shared.common.AppData


import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.models.GameList
import com.gepetto.funhouse.models.defaultGameList
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.models.currentSettings
import com.funhouse.shared.common.models.gameTypeMapDefaultToggles
import club.gepetto.GcLog
import club.gepetto.composeutils.isDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryContent(
    gameList: GameList,
    gameType: GameType,
    modifier: Modifier = Modifier.Companion,
    onClickAction: (FunHouseAction) -> Unit,
) {
    var showingAboutBanner by remember { mutableStateOf(gameTypeMapDefaultToggles.toMutableMap()) }
    var forceRecomposition by remember { mutableStateOf(false) }

    CommonBackHandler(true) {
        GcLog.d("Clicked back")
        onClickAction(FunHouseAction.BackClicked)
    }

    var choice by remember { mutableStateOf<String?>("Home") }

    BoxWithConstraints(modifier = modifier) {
        val fontSize = if (isLarge()) 16.sp else if (isLandscape()) 8.sp else 10.sp

        val startPadding = when {
            isLandscape() && isLarge() -> 128.dp
            isLandscape() -> 128.dp
            isLarge() -> 0.dp
            isCompact() -> 128.dp
            else -> 128.dp
        }

        val startTextPadding = when {
            isLandscape() && isLarge() -> 128.dp
            isLandscape() -> 32.dp
            isLarge() -> 0.dp
            else -> 12.dp
        }

        val endTextPadding = if (isLandscape() && isLarge()) 32.dp else 0.dp

        val buttonsList: MutableList<GcNavButton> = mutableListOf()

        // Build button list from games
        gameList.games?.filter { it.gameType == gameType && (!it.secretGame || currentSettings.secretGames) }?.forEach { game ->
            val gcNavButton = GcNavButton(
                label = if (game.menuTitle.isNotEmpty()) game.menuTitle else game.title,
                resourceResIcon = com.funhouse.shared.common.utils.getGameIconResource(game.nickName),
                contentDescription = game.title,
                extraDp = 22.dp,
                outline = true,
                vector = false,
                navChoice = choice,
                onClick = {
                    GcLog.d("Clicked on ${game.title}")
                    choice = if (game.menuTitle.isNotEmpty()) game.menuTitle else game.title
                    onClickAction(FunHouseAction.GameStartClicked(game))
                }
            )

            buttonsList.add(gcNavButton)
        }

        GcAdaptiveScaffold(
            iconBackgroundColor = sysBackgroundColor(),
            modifier = Modifier.align(Alignment.BottomCenter),
            gcNavButtons = buttonsList,
            layoutType = if (isLandscape()) GcAdaptiveLayoutType.PORTRAIT_USES_BAR else GcAdaptiveLayoutType.PORTRAIT_USES_RAIL,
            railColumns = 2,
        )

        GcLog.d("title closed - value for ${gameType} is ${showingAboutBanner[gameType]} fr = ${forceRecomposition}");

        if (showingAboutBanner[gameType] == true) {
            GcBanner(Modifier.padding(start = startPadding).align(Alignment.BottomEnd)) {
                Column(
                    Modifier
                        .padding(top = 32.dp, start = startTextPadding, end = endTextPadding)
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(when (gameType) {
                            GameType.ADVENTURE -> Res.string.adventure_title
                            GameType.SKILL -> Res.string.skill_title
                            GameType.LUCK -> Res.string.luck_title
                            GameType.ARCADE -> Res.string.arcade_title
                            GameType.OTHER -> Res.string.other_title
                            GameType.CHATBOT -> Res.string.other_title // TODO: add chatbot title
                            GameType.MULTIPLAYER -> Res.string.multiplayer_title
                        }),
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Text(
                        text = stringResource(when (gameType) {
                            GameType.SKILL -> Res.string.skill_banner_content
                            GameType.LUCK -> Res.string.luck_banner_content
                            GameType.ADVENTURE -> Res.string.adventure_banner_content
                            GameType.ARCADE -> Res.string.arcade_banner_content
                            GameType.OTHER -> Res.string.other_banner_content
                            GameType.CHATBOT -> Res.string.other_banner_content // TODO: add chatbot banner content
                            GameType.MULTIPLAYER -> Res.string.multiplayer_banner_content
                        }),
                        fontSize = fontSize,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                }
                Icon(
                    painter = painterResource(Res.drawable.close_white_x),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(end = endTextPadding)
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .clickable { showingAboutBanner[gameType] = false; forceRecomposition = !forceRecomposition }
                )
            }
        }
    }

}

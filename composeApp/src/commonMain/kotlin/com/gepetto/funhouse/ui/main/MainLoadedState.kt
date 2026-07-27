package com.gepetto.funhouse.ui.main
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcBanner
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.image.GcIcon
import club.gepetto.composeutils.image.GcImage
import club.gepetto.composeutils.isLandscape
import club.gepetto.composeutils.isLarge
import club.gepetto.composeutils.isSystemInLandscape
import club.gepetto.composeutils.scaffold.GcAdaptiveLayoutType
import club.gepetto.composeutils.scaffold.GcAdaptiveScaffold
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysForegroundColor
import com.funhouse.shared.common.AppData
import com.gepetto.funhouse.Constants

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.intentprocessors.FunHouseIntentCommand
import com.gepetto.funhouse.intentprocessors.FunHouseState

import com.gepetto.funhouse.ui.common.FunHouseTopBar
import com.gepetto.funhouse.ui.common.NavigationButtons
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLoadedState (
    state: FunHouseState.Loaded,
    onClickAction: (FunHouseAction) -> Unit,
    onIntentCommand: (FunHouseIntentCommand) -> Unit,
    onThemeChanged: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profileLabel = stringResource(Res.string.nav_profile)
    var choice by remember { mutableStateOf(profileLabel) }
    var categoryClicked by remember { mutableStateOf(state.gameType) }
    var showProfile by remember { mutableStateOf(false)}
    var showingAboutBanner by remember { mutableStateOf(true) }

    if (state.forceType != null && state.forceType) categoryClicked = state.gameType

    Constants.DEFAULT_IMAGE = if (AppData.darkMode) Res.drawable.funhouseinverted else Res.drawable.funhouse
    Column (modifier.fillMaxSize().background(sysBackgroundColor())) {
        FunHouseTopBar(
            label = AppData.appName,
            modifier = Modifier.fillMaxWidth(),
            onClickAction = onClickAction
        )

        BoxWithConstraints {
            GcImage(
                imageResourceRes = Constants.DEFAULT_IMAGE,
                contentDescription = null,
                contentScale = if (isSystemInLandscape()) ContentScale.FillHeight else ContentScale.FillWidth,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            if (state.cached)
                Text(stringResource(Res.string.cached), modifier = Modifier.align(Alignment.TopCenter), color = sysForegroundColor())

            val fontSize = if (isLarge()) 16.sp else if (isSystemInLandscape()) 8.sp else 10.sp

            val startPadding = when {
                isSystemInLandscape() && isLarge() -> 128.dp
                isSystemInLandscape() -> 64.dp
                isLarge() -> 0.dp
                else -> 12.dp
            }

            val startTextPadding = when {
                isSystemInLandscape() && isLarge() -> 128.dp
                isSystemInLandscape() -> 64.dp
                isLarge() -> 0.dp
                else -> 12.dp
            }

            val endTextPadding = if (isSystemInLandscape() && isLarge()) 32.dp else 0.dp

            val buttonList = NavigationButtons(
                currentChoice = choice,
                home = categoryClicked == null,
                onClickAction = { ch, action ->
                    choice = ch
                    showProfile = !showProfile
                    onClickAction(action)
                },
                onCategoryChosen = { ch, type ->
                    choice = ch
                    categoryClicked = type
                    showProfile = false
                    onIntentCommand(FunHouseIntentCommand.UpdateGameType(type))
                },
                landscape = isLandscape(),
                profileTint = sysForegroundColor(),
                profileLabel = profileLabel,
                infoLabel = stringResource(Res.string.nav_info),
                chatbotsLabel = stringResource(Res.string.nav_chatbots),
                skillsLabel = stringResource(Res.string.nav_skills),
                arcadeLabel = stringResource(Res.string.nav_arcade),
                chanceLabel = stringResource(Res.string.nav_chance),
                adventureLabel = stringResource(Res.string.nav_adventure),
                adventLabel = stringResource(Res.string.nav_advent),
                multiplayerLabel = stringResource(Res.string.nav_multiplayer),
            )

            GcAdaptiveScaffold(
                iconBackgroundColor = sysBackgroundColor(),
                modifier = Modifier.align(Alignment.BottomCenter),
                layoutType = if (isLandscape()) GcAdaptiveLayoutType.PORTRAIT_USES_RAIL else GcAdaptiveLayoutType.PORTRAIT_USES_BAR,
                gcNavButtons = buttonList.buttonList,
            ) {
                if (categoryClicked != null)
                    CategoryContent(
                        gameList = state.gameList,
                        gameType = categoryClicked!!,
                        onClickAction = onClickAction
                    )
            }

            if (categoryClicked == null && showingAboutBanner) {
                GcBanner(Modifier.padding(start = startPadding, bottom = 72.dp).align(Alignment.BottomEnd)) {
                    Text(
                        fontSize = fontSize,
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(Res.string.main_banner_content),
                        modifier = Modifier.align(Alignment.BottomCenter).padding(top = 32.dp, start = startTextPadding, end = endTextPadding)
                    )
                    Icon(
                        painter = painterResource(Res.drawable.close_white_x),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .size(32.dp).align(Alignment.TopEnd)
                            .clickable{ showingAboutBanner = false }
                    )
                }
            }


            if (showProfile)
                SettingsContent(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onExit = { showProfile = false },
                    onThemeChanged = onThemeChanged
                )
        }
    }
}

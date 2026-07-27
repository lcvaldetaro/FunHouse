package com.gepetto.funhouse.ui.common
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource



import com.funhouse.shared.common.utils.CommonBackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.image.GcImage
import club.gepetto.composeutils.isSystemInLandscape
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysTextColor
import club.gepetto.composeutils.GcMarkdown
import com.funhouse.shared.common.AppData
import com.gepetto.funhouse.Constants

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.funhouse.shared.common.models.Game
import com.gepetto.funhouse.ui.game.GameNavBar
import club.gepetto.GcLog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height

import androidx.compose.runtime.remember

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

@Composable
fun About(
    modifier: Modifier = Modifier,
    game: Game? = null,
    onClickAction: (FunHouseAction) -> Unit,
) {
    val imageHeight = if (isSystemInLandscape()) 120.dp else 240.dp

    CommonBackHandler(true) {
        GcLog.d("Clicked back")
        onClickAction(FunHouseAction.BackClicked)
    }

    Column(modifier.background(sysBackgroundColor())) {
        GameNavBar(
            label = if (game != null) stringResource(Res.string.about_game, game.title) else AppData.appName,
            hasBackArrow = true,
            onClickAction = { onClickAction(FunHouseAction.BackClicked) },
        )

        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
        ) {
            val bitmap = if (game != null) {
                com.funhouse.shared.common.utils.getGameIconImageBitmap(game.nickName)
            } else {
                com.funhouse.shared.common.utils.getGameIconImageBitmap("funhouse")
            }

            GcImage(
                imageBitmap = bitmap,
                contentDescription = null,
                contentScale = if (isSystemInLandscape()) ContentScale.Companion.FillHeight else ContentScale.Companion.Inside,
                modifier = Modifier.Companion
                    .height(imageHeight)
                    .align(Alignment.Companion.CenterHorizontally)
            )

            Text(
                stringResource(Res.string.version_text, AppData.appName, AppData.version),
                textAlign = TextAlign.Companion.Center,
                fontWeight = FontWeight.Companion.Bold,
                color = sysTextColor(),
                fontSize = 24.sp,
                modifier = Modifier.Companion
                    .align(Alignment.Companion.CenterHorizontally)
                    .padding(16.dp)
            )

            if (game == null) {
                val currentLocale = java.util.Locale.getDefault().language
                val fileName = if (currentLocale in listOf("pt", "es", "it", "de", "fr")) {
                    "about_${currentLocale}.md"
                } else {
                    "about_en.md"
                }
                val markdownContent = remember(fileName) {
                    com.funhouse.shared.common.utils.readAssetFile(fileName) ?: ""
                }

                val parentUriHandler = LocalUriHandler.current
                val customUriHandler = remember {
                    object : UriHandler {
                        override fun openUri(uri: String) {
                            if (uri.contains("privacy_")) {
                                onClickAction(FunHouseAction.PrivacyClicked)
                            } else {
                                try {
                                    parentUriHandler.openUri(uri)
                                } catch (e: Exception) {
                                    GcLog.e("Failed to open URI: $uri", e)
                                }
                            }
                        }
                    }
                }

                CompositionLocalProvider(LocalUriHandler provides customUriHandler) {
                    GcMarkdown(
                        content = markdownContent,
                        textColor = sysTextColor(),
                        modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                    )
                }
            } else {
                Text(
                    text = stringResource(Res.string.copyright),
                    textAlign = TextAlign.Companion.Center,
                    color = sysTextColor(),
                    modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally),
                )
                Text(
                    text = game.about,
                    color = sysTextColor(),
                    fontFamily = FontFamily.Companion.Monospace,
                    fontSize = 10.sp,
                    modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                )
                if (game.license != null) {
                    Text(
                        text = game.license!!,
                        color = sysTextColor(),
                        fontFamily = FontFamily.Companion.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                    )
                }
            }
        }

    }
}

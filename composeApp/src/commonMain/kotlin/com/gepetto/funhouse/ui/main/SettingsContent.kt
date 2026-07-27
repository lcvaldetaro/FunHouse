package com.gepetto.funhouse.ui.main

import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.PreviewLightDark
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcBanner
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.image.GcIcon
import com.gepetto.funhouse.models.defaultGameList
import com.funhouse.shared.common.models.currentSettings

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {},
    onThemeChanged: () -> Unit = {},
) {
    var usingVoice by remember { mutableStateOf(currentSettings.usingVoice) }
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(currentSettings.playerNickName) }
    val walletValues = currentSettings.walletValues(defaultGameList.games)
    var walletString = if (walletValues.size > 0) stringResource(Res.string.winnings_per_game) else ""
    val total = currentSettings.getTotalWallets()

    walletValues.forEach { walletString = "$walletString${stringResource(Res.string.wallet_entry, it.key, it.value.toString())}" }

    GcBanner(modifier) {
        Box {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditingName) {
                        BasicTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                currentSettings.playerNickName = it
                                currentSettings.save()
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(Color.Black),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { isEditingName = false }
                            ),
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .widthIn(min = 100.dp, max = 200.dp)
                        )
                    } else {
                        Text(
                            text = nameInput.ifEmpty { "Player" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.clickable { isEditingName = true }
                        )
                    }
                    GcIcon(
                        imageResourceRes = if (usingVoice) Res.drawable.call_spk_on else Res.drawable.speaker_off,
                        modifier = Modifier.size(32.dp)
                    ) {
                        currentSettings.usingVoice = !currentSettings.usingVoice
                        currentSettings.save()
                        usingVoice = currentSettings.usingVoice
                    }
                }
                Text(text = walletString, color = Color.Black)
                Text(text = stringResource(Res.string.total_in_wallet).replace("%s", total.toString()), color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Theme: ",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Auto
                    val isAuto = !currentSettings.forceLightMode && !currentSettings.forceDarkMode
                    Text(
                        text = "Auto",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = if (isAuto) FontWeight.ExtraBold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable {
                                currentSettings.forceLightMode = false
                                currentSettings.forceDarkMode = false
                                currentSettings.save()
                                onThemeChanged()
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .drawBehind {
                                if (isAuto) {
                                    val strokeWidth = 2.dp.toPx()
                                    val y = size.height - strokeWidth / 2
                                    drawLine(
                                        color = Color.Black,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                            }
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    // Light
                    val isLight = currentSettings.forceLightMode
                    Text(
                        text = "Light",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = if (isLight) FontWeight.ExtraBold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable {
                                currentSettings.forceLightMode = true
                                currentSettings.forceDarkMode = false
                                currentSettings.save()
                                onThemeChanged()
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .drawBehind {
                                if (isLight) {
                                    val strokeWidth = 2.dp.toPx()
                                    val y = size.height - strokeWidth / 2
                                    drawLine(
                                        color = Color.Black,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                            }
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    // Dark
                    val isDark = currentSettings.forceDarkMode
                    Text(
                        text = "Dark",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = if (isDark) FontWeight.ExtraBold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable {
                                currentSettings.forceLightMode = false
                                currentSettings.forceDarkMode = true
                                currentSettings.save()
                                onThemeChanged()
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .drawBehind {
                                if (isDark) {
                                    val strokeWidth = 2.dp.toPx()
                                    val y = size.height - strokeWidth / 2
                                    drawLine(
                                        color = Color.Black,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = strokeWidth
                                    )
                                }
                            }
                    )
                }
            }


            Icon(
                painter = painterResource(Res.drawable.close_white_x),
                contentDescription = "",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onExit() }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    GcTheme {
        SettingsContent()
    }
}
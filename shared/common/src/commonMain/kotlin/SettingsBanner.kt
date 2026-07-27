package com.funhouse.shared.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcBanner
import com.funhouse.shared.common.models.currentSettings
import org.jetbrains.compose.resources.painterResource

import com.funhouse.shared.common.generated.resources.Res
import com.funhouse.shared.common.generated.resources.call_spk_on
import com.funhouse.shared.common.generated.resources.speaker_off
import com.funhouse.shared.common.generated.resources.close_white_x

@Composable
fun SettingsBanner(
    gameWinnings: Float,
    usingVoice: Boolean,
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {},
    setUsingVoice: (Boolean) -> Unit
) {
    GcBanner(modifier) {
        Box {
            Column(Modifier.align(Alignment.Center)) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentSettings.playerNickName, fontSize = 24.sp)
                    Icon(
                        painter = painterResource(if (usingVoice) Res.drawable.call_spk_on else Res.drawable.speaker_off),
                        contentDescription = "Toggle Voice",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp).clickable {
                            currentSettings.usingVoice = !currentSettings.usingVoice
                            currentSettings.save()
                            setUsingVoice(currentSettings.usingVoice)
                        }
                    )
                }
                Text(text = "Winnings in game: " + gameWinnings.toString())
                Text(text = "Total in wallet: " + currentSettings.getTotalWallets().toString())
            }

            Icon(
                painter = painterResource(Res.drawable.close_white_x),
                contentDescription = "Close",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onExit() }
            )
        }
    }
}

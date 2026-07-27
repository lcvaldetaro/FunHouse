package com.gepetto.funhouse.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.funhouse.shared.common.models.Settings
import com.gepetto.funhouse.ui.game.GameNavBar

@Composable
fun SettingsContent(
    settings: Settings,
    modifier: Modifier = Modifier.Companion,
    onClickAction: (FunHouseAction) -> Unit
) {
    Surface {
        Column(modifier) {
            GameNavBar(
                label = "Settings",
                hasBackArrow = true,
                onClickAction = onClickAction,
            )
            Text("Settings:")
            Text("${settings}")
        }
    }
}

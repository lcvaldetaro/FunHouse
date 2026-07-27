package com.gepetto.funhouse.ui.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import club.gepetto.circum.circumIntentProcessor
import club.gepetto.composeutils.GcTheme
import com.funhouse.shared.common.AppData
import com.gepetto.funhouse.intentprocessors.FunHouseIntentCommand
import com.gepetto.funhouse.intentprocessors.FunHouseIntentProcessor
import com.gepetto.funhouse.intentprocessors.FunHouseState

import androidx.compose.runtime.LaunchedEffect

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    intentProcessor: FunHouseIntentProcessor = circumIntentProcessor<FunHouseIntentProcessor>(),
) {
    val state by intentProcessor.collectState(initialState = FunHouseState.Loading)
    var themeTrigger by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
    val isSystemDark = isSystemInDarkTheme()
    val darkMode = when {
        com.funhouse.shared.common.models.currentSettings.forceDarkMode -> true
        com.funhouse.shared.common.models.currentSettings.forceLightMode -> false
        else -> isSystemDark
    }
    AppData.darkMode = darkMode
    themeTrigger.hashCode()

    LaunchedEffect(Unit) {
        com.funhouse.shared.common.initStringCache()
        val format = com.funhouse.shared.common.getString(com.funhouse.shared.common.R.string.version_build_format)
        if (format.isNotEmpty()) {
            AppData.version = com.funhouse.shared.common.getString(
                com.funhouse.shared.common.R.string.version_build_format,
                club.gepetto.utils.GcAppInfo.versionName ?: "",
                club.gepetto.utils.GcAppInfo.versionCode ?: 0L
            )
        }
    }

    GcTheme(darkMode) {
        Surface {
            MainContent(
                state = state,
                modifier = modifier,
                onClickAction = { intentProcessor.sendIntentCommand(FunHouseIntentCommand.FunHouseActionClickedCommand(it)) },
                onIntentCommand = { intentProcessor.sendIntentCommand(it)},
                onThemeChanged = { themeTrigger++ }
            )
        }
    }
}

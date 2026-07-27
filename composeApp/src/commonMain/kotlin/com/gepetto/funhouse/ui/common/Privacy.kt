package com.gepetto.funhouse.ui.common
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource


import com.funhouse.shared.common.utils.CommonBackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.GcLog
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysTextColor
import club.gepetto.composeutils.GcMarkdown

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.ui.game.GameNavBar

@Composable
fun Privacy(
    modifier: Modifier = Modifier,
    onClickAction: (FunHouseAction) -> Unit,
) {
    CommonBackHandler(true) {
        GcLog.d("Clicked back")
        onClickAction(FunHouseAction.BackClicked)
    }

    Column(modifier.background(sysBackgroundColor())) {
        GameNavBar(
            label = stringResource(Res.string.privacy_policy_title),
            hasBackArrow = true,
            onClickAction = { onClickAction(FunHouseAction.BackClicked) },
        )

        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            val currentLocale = java.util.Locale.getDefault().language
            val fileName = if (currentLocale in listOf("pt", "es", "it", "de", "fr")) {
                "privacy_${currentLocale}.md"
            } else {
                "privacy_en.md"
            }
            val markdownContent = remember(fileName) {
                com.funhouse.shared.common.utils.readAssetFile(fileName) ?: ""
            }

            GcMarkdown(
                content = markdownContent,
                textColor = sysTextColor(),
                modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
            )
        }
    }
}

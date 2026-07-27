package com.gepetto.funhouse.ui.game

import com.funhouse.shared.common.ECHO_PREFIX
import com.funhouse.shared.common.AppData
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.GcLog

import org.jetbrains.compose.resources.Font
import com.funhouse.shared.common.generated.resources.Res
import com.funhouse.shared.common.generated.resources.robotomono_regular
import com.funhouse.shared.common.generated.resources.dejavusans

@Composable
fun TerminalWindow(
    textView: String,
    usingVoice: Boolean,
    shouldScroll: Boolean,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val oldTextView = remember { mutableStateOf("") }
    val terminalFontFamily = FontFamily(
        Font(Res.font.robotomono_regular),
        Font(Res.font.dejavusans)
    )

    if (oldTextView.value != textView) {
        val size = oldTextView.value.length
        if (textView.length >= size) {
            val voiceText = textView.substring(size)

            if (usingVoice && !voiceText.startsWith(ECHO_PREFIX)) {
                GcLog.v("voiceText might be spoken")
                com.funhouse.shared.common.utils.speakText(voiceText)
            }
        }

        oldTextView.value = textView
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = sysBackgroundColor()),
        border = BorderStroke(1.dp, color = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray)
    ) {
        Column {
            Text(
                text = oldTextView.value,
                fontFamily = terminalFontFamily,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .verticalScroll(scrollState)
            )
        }
    }

    if (shouldScroll) {
        LaunchedEffect(scrollState.canScrollForward) {
            scrollState.animateScrollBy(3000f)
        }
    }
}

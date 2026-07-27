package com.gepetto.funhouse.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysForegroundColor

@Composable
fun CommandLine(
    label: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    onEnterKey: (String) -> Unit
) {
     var text by remember { mutableStateOf("") }
     Column (modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = sysBackgroundColor(),
                    shape = MaterialTheme.shapes.medium
                ),
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            label = { Text(text = label, fontSize = 12.sp) },
            textStyle = MaterialTheme.typography.bodySmall,
            value = text,
            onValueChange = { text = it },
            keyboardActions = KeyboardActions(
                onDone = {
                    onEnterKey(text)
                    text = ""
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = sysForegroundColor()
            )
        )
    }
}

package com.gepetto.funhouse.ui.main
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

import club.gepetto.composeutils.GcE2eBox
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.isSystemInLandscape
import club.gepetto.composeutils.sysTextColor



@Composable
fun MainLoadingState (
    modifier: Modifier = Modifier
) {
    GcE2eBox(
        modifier = modifier,
        imageResourceRes = Res.drawable.funhouseicon,
        darkImageResourceRes = Res.drawable.funhouseicon,
        contentScale = if (isSystemInLandscape()) ContentScale.FillHeight else ContentScale.Crop,
        shaded = true,
        progress = true,
    ) {
        Box (Modifier.fillMaxSize()) {
            Text(
                text = stringResource(Res.string.loading_games),
                color = sysTextColor(),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

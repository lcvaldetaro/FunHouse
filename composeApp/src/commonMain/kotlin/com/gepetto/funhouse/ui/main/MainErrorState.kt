package com.gepetto.funhouse.ui.main
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysForegroundColor

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.ui.common.IconImage



@Composable
fun MainErrorState(
    modifier: Modifier = Modifier,
    onClickAction: (FunHouseAction) -> Unit,
) {
    Column (modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Spacer(Modifier.weight(1f))

        IconImage(
            imageResource = Res.drawable.funhouseicon,
            modifier = Modifier
                .requiredSize(width = 96.dp, height = 168.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.weight(1f))

        Text(
            stringResource(Res.string.network_failed),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = sysForegroundColor(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.weight(4f))

        Button(
            onClick = { onClickAction(FunHouseAction.BackClicked) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(Res.string.retry))
        }
    }
}

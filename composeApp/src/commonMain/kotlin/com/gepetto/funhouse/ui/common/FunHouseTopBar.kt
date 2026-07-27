package com.gepetto.funhouse.ui.common
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.image.GcIcon
import club.gepetto.composeutils.isSystemInLandscape
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysForegroundColor
import com.gepetto.funhouse.Constants

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.models.MenuOption

@Composable
fun FunHouseTopBar(
    label: String,
    modifier: Modifier = Modifier,
    hasBackArrow: Boolean = false,
    hasExitButton: Boolean = false,
    hasMenuIcon: Boolean = false,
    foregroundColor: Color = sysForegroundColor(),
    backgroundColor: Color = Color.Transparent,
    menuOptions: List<MenuOption> = emptyList(),
    onClickAction: (FunHouseAction) -> Unit = {},
) {
    val startPadding = if (isSystemInLandscape()) 18.dp else 30.dp
    Row(
        modifier = modifier
            .padding(start = startPadding)
            .background(color = backgroundColor)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasBackArrow)
            Icon(
                painter = painterResource(Res.drawable.backarrow),
                contentDescription = stringResource(Res.string.back),
                modifier = Modifier
                    .width(Constants.ICON_SIZE)
                    .height(Constants.ICON_SIZE)
                    .clickable { onClickAction(FunHouseAction.BackClicked) },
                tint = foregroundColor
            )
        else
            Spacer(Modifier.size(Constants.ICON_SIZE))

        Text(
            text = label,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            color = foregroundColor
        )

        if (hasExitButton) {
            GcIcon(
                imageResourceRes = Res.drawable.poweroff,
                contentDescription = stringResource(Res.string.exit),
                size = 48.dp,
                onClick = { onClickAction(FunHouseAction.BackClicked) }
            )
        }
        else
            Spacer(Modifier.size(48.dp))
    }
}

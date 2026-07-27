package com.gepetto.funhouse.ui.game
import com.funhouse.shared.common.generated.resources.*
import org.jetbrains.compose.resources.stringResource


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import club.gepetto.composeutils.image.GcImage
import club.gepetto.composeutils.navbar.GcNavButton
import club.gepetto.composeutils.sysBackgroundColor
import club.gepetto.composeutils.sysForegroundColor
import com.gepetto.funhouse.Constants.ICON_SIZE

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.gepetto.funhouse.models.MenuOption

@Composable
fun GameNavBar(
    label: String,
    modifier: Modifier = Modifier,
    hasBackArrow: Boolean = false,
    hasExitButton: Boolean = false,
    hasMenuIcon: Boolean = false,
    menuOptions: List<MenuOption> = emptyList(),
    GcNavButtons: List<GcNavButton>? = null,
    onClickAction: (FunHouseAction) -> Unit
) {
    val foregroundColor = sysForegroundColor()

    Row (
        modifier = modifier
            .background(color = Color.Transparent)
            .padding(4.dp)
    ) {
        if (hasBackArrow) {
            Icon(
                painter = painterResource(Res.drawable.backarrow),
                contentDescription = stringResource(Res.string.back),
                modifier = Modifier
                    .width(ICON_SIZE)
                    .height(ICON_SIZE)
                    .padding(2.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onClickAction(FunHouseAction.BackClicked)},
                tint = foregroundColor
            )
        }

        val rightModifier = if (GcNavButtons == null)
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        else
            Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)

        Column(modifier = rightModifier) {
            Text(
                text = label,
                textAlign = TextAlign.Center,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterHorizontally),
                color  = foregroundColor
            )
        }

        if (GcNavButtons != null) {
            GcNavButtons.forEach { rightButton ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                        .clickable { rightButton.onClick(rightButton) }
                ) {
                    if (rightButton.label != null) {
                        Text(
                            text = rightButton.label!!,
                            fontSize = 32.sp,
                            color = foregroundColor,
                            modifier = modifier.padding(horizontal = 8.dp)
                        )
                    }
                    else
                    if (rightButton.resourceResIcon != null) {
                        Icon(
                            painter = painterResource(rightButton.resourceResIcon!!),
                            contentDescription = rightButton.contentDescription,
                            modifier = Modifier
                                .width(ICON_SIZE)
                                .height(ICON_SIZE)
                                .padding(vertical = 2.dp),
                            tint = foregroundColor
                        )
                    }
                    else
                    if (rightButton.bitmap != null) {
                        GcImage(
                            imageBitmap = rightButton.bitmap,
                            contentDescription = rightButton.contentDescription,
                            modifier = Modifier
                                .width(ICON_SIZE)
                                .height(ICON_SIZE)
                                .padding(vertical = 2.dp, horizontal = 8.dp),
                        )
                    }
                }
            }
        }

        if (hasExitButton) {
            Icon(
                painter = painterResource(Res.drawable.close_white_x),
                contentDescription = stringResource(Res.string.exit),
                modifier = Modifier
                    .width(ICON_SIZE)
                    .height(ICON_SIZE)
                    .padding(2.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { onClickAction(FunHouseAction.BackClicked)},
                tint = foregroundColor
            )
        }
    }
}

package com.gepetto.funhouse.ui.game

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.isDark
import club.gepetto.composeutils.sysForegroundColor
import com.funhouse.shared.common.models.Game

@Composable
fun DirectionButtons(
    game: Game,
    modifier: Modifier = Modifier,
    landScape: Boolean = false,
    onClick: (String) -> Unit,
) {
    val columns = if (landScape) game.directionColumns!! / 2 else game.directionColumns!!

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
    ) {
        val buttons = game.directions
        items(buttons.size) { index ->
            val direction = buttons[index]
            DirectionButton(
                label = direction.label,
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 2.dp).height(24.dp),
                command = direction.command,
                color = if (isDark() || game.forceDark) Color.Black else Color.White,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun DirectionButton(
    label: String,
    command: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = sysForegroundColor()
) {
    Button (
        onClick = { onClick(command) },
        modifier = modifier.padding(0.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = label, fontSize = 14.sp, color = color)
    }
}

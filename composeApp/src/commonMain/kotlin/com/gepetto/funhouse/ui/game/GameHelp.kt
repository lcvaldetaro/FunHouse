package com.gepetto.funhouse.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gepetto.funhouse.intentprocessors.FunHouseAction
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import com.funhouse.shared.common.utils.CommonBackHandler
import androidx.compose.ui.layout.ContentScale
import club.gepetto.composeutils.GcTheme

import club.gepetto.composeutils.GcMarkdown
import java.io.File
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.models.Game
import club.gepetto.GcLog
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

@Composable
fun GameHelp (
    game: Game,
    modifier: Modifier = Modifier,
    preview: String?  = null,
    onClickAction: (FunHouseAction) -> Unit,
) {
    CommonBackHandler(true) {
        GcLog.d( "Clicked back")
        onClickAction(FunHouseAction.GameResumeClicked(game))
    }
    if (game.helpFile.fileName.isNotEmpty()) {
        Column(modifier.fillMaxWidth()) {
            GameNavBar(
                label = game.title,
                hasBackArrow = true,
                onClickAction = { onClickAction(FunHouseAction.GameResumeClicked(game)) }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                androidx.compose.foundation.Image(
                    bitmap = com.funhouse.shared.common.utils.getGameIconImageBitmap(game.nickName),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                )
                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    if (preview != null) {
                        GcMarkdown(preview)
                    } else {
                        val mdFile = File("${AppData.packageFolder}/${AppData.gameFolder}", game.helpFile.fileName)
                        if (mdFile.exists()) {
                            val mdContent = mdFile.readBytes().decodeToString()
                            GcMarkdown(mdContent)
                        } else {
                            GcLog.e("Markdown file not found: ${mdFile.absolutePath}")
                        }
                    }
                }
            }

        }
    }
}

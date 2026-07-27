package com.gepetto.tetric.ui
import com.funhouse.shared.common.utils.Preview
import com.funhouse.shared.common.generated.resources.Res as CommonR
import com.funhouse.shared.common.generated.resources.underconstruction
import com.funhouse.shared.common.generated.resources.backarrow
import com.funhouse.shared.common.generated.resources.ic_profile
import com.funhouse.shared.common.generated.resources.call_spk_on
import com.funhouse.shared.common.generated.resources.speaker_off
import com.funhouse.shared.common.generated.resources.eliza
import com.funhouse.shared.common.generated.resources.funhouse
import com.funhouse.shared.common.generated.resources.steamboat_willie
import com.funhouse.shared.common.generated.resources.android
import com.funhouse.shared.common.generated.resources.numberseven
import com.funhouse.shared.common.generated.resources.call_mute
import com.funhouse.shared.common.generated.resources.pause

import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.painterResource



import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString


import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

import androidx.lifecycle.viewmodel.compose.viewModel

import com.gepetto.tetric.logic.Brick
import com.gepetto.tetric.logic.GameStatus
import com.gepetto.tetric.logic.GameViewModel
import com.gepetto.tetric.logic.NextMatrix
import com.gepetto.tetric.logic.Spirit
import com.gepetto.tetric.logic.SpiritType
import com.gepetto.tetric.ui.theme.BrickMatrix
import com.gepetto.tetric.ui.theme.BrickSpirit
import com.gepetto.tetric.ui.theme.ScreenBackground
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlin.math.min

@ObsoleteCoroutinesApi
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    scaleRatio: Float = 1f,
) {
    val viewModel = viewModel { GameViewModel() }
    val viewState = viewModel.viewState.value

    Box(
        modifier
            .background(Color.Black)
            .padding(1.dp)
            .background(ScreenBackground)
            .padding(10.dp)
    ) {

        val animateValue by rememberInfiniteTransition().animateFloat(
            initialValue = 0f, targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500),
                repeatMode = RepeatMode.Reverse,
            ),
        )

        val context = LocalContext.current
        val textMeasurer = rememberTextMeasurer()
        Canvas(modifier = Modifier.fillMaxSize()) {
            val brickSize = min(
                size.width / viewState.matrix.first,
                size.height / viewState.matrix.second
            )

            drawMatrix(brickSize, viewState.matrix)
            drawMatrixBorder(brickSize, viewState.matrix)
            drawBricks(viewState.bricks, brickSize, viewState.matrix)
            drawSpirit(viewState.spirit, brickSize, viewState.matrix)
            drawText(textMeasurer, viewState.gameStatus, brickSize, viewState.matrix, animateValue)
        }

        GameScoreboard(
            spirit = run {
                if (viewState.spirit == Spirit.Empty) Spirit.Empty
                else viewState.spiritNext.rotate()
            },
            score = viewState.score,
            line = viewState.line,
            level = viewState.level,
            isMute = viewState.isMute,
            isPaused = viewState.isPaused
        )
    }
}

@Composable
fun GameScoreboard(
    modifier: Modifier = Modifier,
    scaleRatio: Float = 1f,
    brickSize: Float = 35f * scaleRatio,
    spirit: Spirit,
    score: Int = 0,
    line: Int = 0,
    level: Int = 1,
    isMute: Boolean = false,
    isPaused: Boolean = false
) {
    Row(modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(0.65f))
        val textSize = 10.sp
        val margin = 8.dp
        Column(
            Modifier
                .fillMaxHeight()
                .weight(0.35f)
        ) {
            Text(stringResource(R.string.liras), fontSize = textSize)
            LedLiraNumber(Modifier.fillMaxWidth(), score, 6)

            Spacer(modifier = Modifier.height(margin))

            Text(stringResource(R.string.lines), fontSize = textSize)
            LedNumber(Modifier.fillMaxWidth(), line, 6)

            Spacer(modifier = Modifier.height(margin))

            Text(stringResource(R.string.level), fontSize = textSize)
            LedNumber(Modifier.fillMaxWidth(), level, 1)

            Spacer(modifier = Modifier.height(margin))

            Text(stringResource(R.string.next), fontSize = textSize)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            ) {
                drawMatrix(brickSize, NextMatrix)
                drawSpirit(
                    spirit.adjustOffset(NextMatrix),
                    brickSize = brickSize, NextMatrix
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Row {
                Image(
                    modifier = Modifier.width(15.dp * scaleRatio),
                    painter = painterResource(if (isMute) CommonR.drawable.call_mute else CommonR.drawable.call_spk_on),
                    colorFilter = ColorFilter.tint(if (isMute) BrickSpirit else BrickMatrix),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier.width(16.dp * scaleRatio),
                    painter = painterResource(CommonR.drawable.pause),
                    colorFilter = ColorFilter.tint(if (isPaused) BrickSpirit else BrickMatrix),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.weight(1f))

                LedClock()
            }
        }
    }
}

private fun DrawScope.drawText(
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    gameStatus: GameStatus,
    brickSize: Float,
    matrix: Pair<Int, Int>,
    alpha: Float,
) {
    val center = Offset(
        brickSize * matrix.first / 2,
        brickSize * matrix.second / 2
    )
    val drawText = { text: String, size: Float ->
        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = TextStyle(
                color = Color.Black.copy(alpha = alpha),
                fontSize = size.toSp(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        val textWidth = textLayoutResult.size.width
        val textHeight = textLayoutResult.size.height
        drawText(textLayoutResult, topLeft = Offset(center.x - textWidth / 2f, center.y - textHeight / 2f))
    }
    if (gameStatus == GameStatus.Onboard) {
        drawText(com.funhouse.shared.common.getString(com.funhouse.shared.common.R.string.body_label), 32f)
    } else if (gameStatus == GameStatus.GameOver) {
        drawText(com.funhouse.shared.common.getString(com.funhouse.shared.common.R.string.game_over_hint), 24f)
    }
}


private fun DrawScope.drawMatrix(brickSize: Float, matrix: Pair<Int, Int>) {
    (0 until matrix.first).forEach { x ->
        (0 until matrix.second).forEach { y ->
            drawBrick(
                brickSize,
                Offset(x.toFloat(), y.toFloat()),
                BrickMatrix
            )
        }
    }
}

private fun DrawScope.drawMatrixBorder(brickSize: Float, matrix: Pair<Int, Int>) {
    val gap = matrix.first * brickSize * 0.05f
    drawRect(
        Color.Black,
        size = Size(
            matrix.first * brickSize + gap,
            matrix.second * brickSize + gap
        ),
        topLeft = Offset(
            -gap / 2,
            -gap / 2
        ),
        style = Stroke(1.dp.toPx())
    )
}

private fun DrawScope.drawBricks(brick: List<Brick>, brickSize: Float, matrix: Pair<Int, Int>) {
    clipRect(
        0f, 0f,
        matrix.first * brickSize,
        matrix.second * brickSize
    ) {
        brick.forEach {
            drawBrick(brickSize, it.location, BrickSpirit)
        }
    }
}

private fun DrawScope.drawSpirit(spirit: Spirit, brickSize: Float, matrix: Pair<Int, Int>) {
    clipRect(
        0f, 0f,
        matrix.first * brickSize,
        matrix.second * brickSize
    ) {
        spirit.location.forEach {
            drawBrick(
                brickSize,
                Offset(it.x, it.y),
                BrickSpirit
            )
        }
    }
}

private fun DrawScope.drawBrick(
    brickSize: Float,
    offset: Offset,
    color: Color
) {
    val actualLocation = Offset(
        offset.x * brickSize,
        offset.y * brickSize
    )

    val outerSize = brickSize * 0.8f
    val outerOffset = (brickSize - outerSize) / 2

    drawRect(
        color,
        topLeft = actualLocation + Offset(outerOffset, outerOffset),
        size = Size(outerSize, outerSize),
        style = Stroke(outerSize / 10)
    )

    val innerSize = brickSize * 0.5f
    val innerOffset = (brickSize - innerSize) / 2

    drawRect(
        color,
        actualLocation + Offset(innerOffset, innerOffset),
        size = Size(innerSize, innerSize)
    )

}


@Preview
@Composable
fun PreviewGamescreen(modifier: Modifier = Modifier.width(260.dp).height(300.dp)) {
    Box(
        modifier
            .background(Color.Black)
            .padding(1.dp)
            .background(ScreenBackground)
            .padding(10.dp)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
        ) {
            val brickSize = min(
                size.width / 12,
                size.height / 24
            )

            drawMatrix(brickSize = brickSize, 12 to 24)
            drawMatrixBorder(brickSize = brickSize, 12 to 24)
        }

        val type = SpiritType[6]
        GameScoreboard(
            spirit = Spirit(type, Offset(0f, 0f)).rotate(),
            score = 1204,
            line = 12
        )
    }
}

@Preview
@Composable
fun PreviewSpiritType() {
    Row(
        Modifier
            .size(300.dp, 50.dp)
            .background(ScreenBackground)
    ) {
        val matrix = 2 to 4
        SpiritType.forEach {
            Canvas(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp)

            ) {
                drawBricks(
                    Brick.of(
                        Spirit(it).adjustOffset(matrix)
                    ), min(
                        size.width / matrix.first,
                        size.height / matrix.second
                    ), matrix
                )
            }
        }

    }
}

@Preview
@Composable
private fun PreviewGameScreenLandscape(modifier: Modifier = Modifier.width(300.dp).height(180.dp)) {
    Box(
        modifier
            .background(Color.Black)
            .padding(1.dp)
            .background(ScreenBackground)
            .padding(10.dp)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBackground)
        ) {

            val brickSize = min(
                size.width / 12,
                size.height / 24
            )

            drawMatrix(brickSize = brickSize, 12 to 24)
            drawMatrixBorder(brickSize = brickSize, 12 to 24)
        }

        val type = SpiritType[6]
        GameScoreboard(
            spirit = Spirit(type, Offset(0f, 0f)).rotate(),
            score = 1204,
            line = 12
        )

    }
}

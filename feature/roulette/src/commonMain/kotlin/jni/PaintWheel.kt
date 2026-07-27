package jni
import com.funhouse.shared.common.utils.Preview
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin


import com.funhouse.shared.common.TABLE_COLOR_GREEN
import com.funhouse.shared.common.WOOD_COLOR


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcTheme
import jni.Roulette.Companion.numberSpun
import jni.models.LINES_COLOR
import jni.models.SPINNER_COLOR
import jni.models.redNumbers
import jni.models.rouletteNumbers
import kotlin.math.floor

@Composable
fun PaintWheel(
    targetRotation: Float,
    modifier: Modifier = Modifier,
    onCompletion: (Int) -> Unit = {}
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DrawWheel(targetRotation) { num ->
            onCompletion(num)
        }
    }
}

@Composable
private fun BoxScope.DrawWheel(
    targetRotation: Float,
    modifier: Modifier = Modifier,
    onCompletion: (Int) -> Unit = {}
) {
    val rotationAngle by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 5000),
        finishedListener = { finalRotation ->
            val currentWinningNumber = getWinner(finalRotation)
            numberSpun = currentWinningNumber
            onCompletion(currentWinningNumber)
        },
        label = "rouletteRotation"
    )
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize().padding(8.dp)) {
        val wedgeAngle = 360f / 37
        val strokeWidth = 1.dp.toPx()
        rotate(rotationAngle) {
            for (i in 0 until 37) {
                val number = rouletteNumbers[i]
                val startAngle = i * wedgeAngle - 90 - wedgeAngle / 2
                drawArc(
                    color = getRouletteColor(number),
                    startAngle = startAngle,
                    sweepAngle = wedgeAngle,
                    useCenter = true,
                    size = size
                )
                drawArc(
                    color = LINES_COLOR,
                    startAngle = startAngle,
                    sweepAngle = wedgeAngle,
                    useCenter = true,
                    size = size,
                    style = Stroke(width = strokeWidth)
                )
            }
            /// Draw numbers
            for (i in 0 until 37) {
                val number = rouletteNumbers[i]
                val angleMid = i * wedgeAngle - 90
                val angleRad = (0.0 - angleMid) * 3.141592653589793 / 180.0
                val radius = size.minDimension / 2 * 0.88f
                val x = center.x + radius * cos(angleRad).toFloat()
                val y = center.y - radius * sin(angleRad).toFloat()

                val textLayoutResult = textMeasurer.measure(
                    text = number.toString(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
                val textWidth = textLayoutResult.size.width
                val textHeight = textLayoutResult.size.height
                val topLeft = Offset(x - textWidth / 2f, y - textHeight / 2f)

                rotate(angleMid + 90, Offset(x, y)) {
                    drawText(textLayoutResult, topLeft = topLeft)
                }
            }

            drawCircle(
                color = WOOD_COLOR,
                radius = size.minDimension / 2,
                style = Stroke(width = 16.dp.toPx())
            )
            drawCircle(
                color = LINES_COLOR,
                radius = (size.minDimension / 2) * .80F,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = TABLE_COLOR_GREEN,
                radius = size.minDimension / 3,
            )
            drawCircle(
                color = LINES_COLOR,
                radius = size.minDimension / 3,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = WOOD_COLOR,
                radius = size.minDimension / 2 * 0.5f,
            )
            drawCircle(
                color = LINES_COLOR,
                radius = size.minDimension / 2 * 0.5f,
                style = Stroke(width = 1.dp.toPx())
            )

            // Draw spinner in the center
            val crossRadius = size.minDimension / 7
            val crossStrokeWidth = 3.dp.toPx()
            drawLine(
                color = SPINNER_COLOR,
                start = androidx.compose.ui.geometry.Offset(center.x - crossRadius, center.y),
                end = androidx.compose.ui.geometry.Offset(center.x + crossRadius, center.y),
                strokeWidth = crossStrokeWidth
            )
            drawLine(
                color = SPINNER_COLOR,
                start = androidx.compose.ui.geometry.Offset(center.x, center.y - crossRadius),
                end = androidx.compose.ui.geometry.Offset(center.x, center.y + crossRadius),
                strokeWidth = crossStrokeWidth
            )

            // Draw balls on the tips of the cross
            val ballRadius = size.minDimension / 48
            drawCircle(
                color = SPINNER_COLOR,
                radius = ballRadius,
                center = androidx.compose.ui.geometry.Offset(center.x - crossRadius, center.y)
            )
            drawCircle(
                color = SPINNER_COLOR,
                radius = ballRadius,
                center = androidx.compose.ui.geometry.Offset(center.x + crossRadius, center.y)
            )
            drawCircle(
                color = SPINNER_COLOR,
                radius = ballRadius,
                center = androidx.compose.ui.geometry.Offset(center.x, center.y - crossRadius)
            )
            drawCircle(
                color = SPINNER_COLOR,
                radius = ballRadius,
                center = androidx.compose.ui.geometry.Offset(center.x, center.y + crossRadius)
            )
            // Draw center of spinner
            drawCircle(
                color = SPINNER_COLOR,
                radius = ballRadius * 2,
                //center = androidx.compose.ui.geometry.Offset(center.x - crossRadius, center.y)
            )
        }
    }
    // Pointer
    Canvas(modifier = Modifier.fillMaxSize()) {
        val pointerWidth = 20.dp.toPx()
        val pointerHeight = 30.dp.toPx()
        val path = Path().apply {
            moveTo(center.x - pointerWidth / 2, 0f)
            lineTo(center.x + pointerWidth / 2, 0f)
            lineTo(center.x, pointerHeight)
            close()
        }
        drawPath(path, Color.Yellow)
    }
}

private fun getWinner(targetRotation: Float): Int {
    val N = 37
    val W = 360.0 / N
    // The pointer is at the top (like -90 degrees in drawArc), so we find what angle on the wheel landed there.
    val normAngle = (-targetRotation % 360 + 360) % 360
    // Shift by half a wedge to align with the center of the wedges
    val shiftedAngle = (normAngle + W / 2) % 360
    val winningIndex = floor(shiftedAngle / W).toInt() % N
    return rouletteNumbers[winningIndex]
}

internal fun getRouletteColor(number: Int): Color {
    return when {
        number == 0 -> TABLE_COLOR_GREEN
        redNumbers.contains(number) -> Color.Red
        else -> Color.Black
    }
}

@Preview
@Composable
private fun PreviewFunc() {
    GcTheme {
        Surface {
            PaintWheel(1000f, modifier = Modifier.size(350.dp))
        }
    }
}



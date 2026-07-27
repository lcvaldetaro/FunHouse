package jni
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

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable



import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcTheme

import club.gepetto.composeutils.scaleRatio
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.initializeMedia
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playBoing
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playBump
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private var BALL_RADIUS = 20f
private var FLIPPER_WIDTH = 120f
private var FLIPPER_HEIGHT = 20f
private var FLIPPER_ANGLE_UP = -45f
private var FLIPPER_ANGLE_DOWN = 45f
private var BUMPER_RADIUS = 30f
private var PLUNGER_WIDTH = 40f
private var PLUNGER_HEIGHT = 100f
private var PLUNGER_MAX_PULL = 100f
private var LANE_WALL_WIDTH = 20f
private var FRAME_WIDTH = 20f
private var E2E_THICKNESS = 96f

private val BASELINE = 705.dp
private val BACKGROUND_COLOR = Color.Black
internal val PINBALL_TABLE_BACKGROUND_COLOR = Color(0xFF2C3E50)
private val LEFT_FLIPPER_COLOR = Color.Yellow
private val RIGHT_FLIPPER_COLOR = Color.Yellow
private val BUMPER_HIT_COLOR = Color.Red
private val BUMPER_PALETTE = listOf(Color.Blue) //listOf(Color.Cyan, Color.Green, Color.Yellow, Color.Magenta, Color.Blue)
private val BALL_COLOR = Color.White
private val UI_TEXT_COLOR = Color.White
private val PLUNGER_COLOR = Color.LightGray
private val FUNNEL_COLOR = Color.Black
private val PLUNGER_LANE_COLOR = Color.Black
private val MAIN_WALL_COLOR = PINBALL_TABLE_BACKGROUND_COLOR

private enum class FlipperSide { LEFT, RIGHT }
private enum class PinballGameState { ReadyToLaunch, Launching, Playing, GameOver }
private data class Ball(val position: Offset, val velocity: Offset)
private data class Flipper(val side: FlipperSide, var angle: Float, val color: Color, val upAngle: Float, val downAngle: Float)
private data class Bumper(val position: Offset, val radius: Float, var isHit: Boolean = false, val baseColor: Color)
private data class Plunger(val pull: Float)

@Composable
fun PinballView(
    modifier: Modifier = Modifier,
    init: Boolean = true,
    onExit: () -> Unit = {},
) {
    val usingVoice = currentSettings.usingVoice
    var scaled by remember { mutableStateOf(false) }

    if (init) initializeMedia(LocalContext.current)

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(BACKGROUND_COLOR)) {
        val scaleRatio = scaleRatio(BASELINE)
        GcLog.d("scaleRatio = ${scaleRatio}")

        if (!scaled && scaleRatio != 1f) {
            BALL_RADIUS = BALL_RADIUS * scaleRatio
            FLIPPER_WIDTH = FLIPPER_WIDTH * scaleRatio
            FLIPPER_HEIGHT = FLIPPER_HEIGHT  * scaleRatio
            BUMPER_RADIUS = BUMPER_RADIUS * scaleRatio
            PLUNGER_WIDTH = PLUNGER_WIDTH * scaleRatio
            PLUNGER_HEIGHT = PLUNGER_HEIGHT * scaleRatio
            PLUNGER_MAX_PULL = PLUNGER_MAX_PULL * scaleRatio
            LANE_WALL_WIDTH = LANE_WALL_WIDTH * scaleRatio
            E2E_THICKNESS = E2E_THICKNESS * scaleRatio
            scaled = true
        }
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = (constraints.maxHeight - E2E_THICKNESS).toFloat()

        var gameState by remember { mutableStateOf(PinballGameState.ReadyToLaunch) }
        var score by remember { mutableStateOf(0) }
        val coroutineScope = rememberCoroutineScope()

        var ball by remember { mutableStateOf(Ball(Offset(canvasWidth - PLUNGER_WIDTH / 2, canvasHeight - 100f - BALL_RADIUS), Offset(0f, 0f))) }
        val leftFlipper = remember { Flipper(FlipperSide.LEFT, FLIPPER_ANGLE_DOWN, LEFT_FLIPPER_COLOR, FLIPPER_ANGLE_UP, FLIPPER_ANGLE_DOWN) }
        val rightFlipper = remember { Flipper(FlipperSide.RIGHT, -FLIPPER_ANGLE_DOWN, RIGHT_FLIPPER_COLOR, -FLIPPER_ANGLE_UP, -FLIPPER_ANGLE_DOWN) }
        val bumperColors = BUMPER_PALETTE
        var bumpers by remember {
            mutableStateOf(
                mutableStateListOf(
                    Bumper(Offset(canvasWidth / 2f, canvasHeight / 2f), BUMPER_RADIUS, baseColor = BUMPER_PALETTE.random()),
                    Bumper(Offset(canvasWidth / 3f, canvasHeight / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                    Bumper(Offset(canvasWidth * 2 / 3f, canvasHeight / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                    Bumper(Offset(canvasWidth / 4f, canvasHeight * 2 / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                    Bumper(Offset(canvasWidth * 3 / 4f, canvasHeight * 2 / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                    Bumper(Offset(canvasWidth / 4f, canvasHeight / 5f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                    Bumper(Offset(canvasWidth * 3 / 4f, canvasHeight / 5f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                    Bumper(Offset(canvasWidth / 3f, canvasHeight * 5 / 6f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                    Bumper(Offset(canvasWidth * 2 / 3f, canvasHeight * 5 / 6f), BUMPER_RADIUS, baseColor = bumperColors.random())
                )
            )
        }
        var plunger by remember { mutableStateOf(Plunger(0f)) }

        val funnelLeftLine = Offset(0f, canvasHeight - 300f) to Offset(canvasWidth / 2f - FLIPPER_WIDTH / 2, canvasHeight - 100f)
        val funnelRightLine = Offset(canvasWidth - PLUNGER_WIDTH - 10f, canvasHeight - 300f) to Offset(canvasWidth / 2f + FLIPPER_WIDTH / 2, canvasHeight - PLUNGER_MAX_PULL)

        fun initializeGame() {
            ball = Ball(Offset(canvasWidth - PLUNGER_WIDTH / 2, canvasHeight - 100f - BALL_RADIUS), Offset(0f, 0f))
            leftFlipper.angle = leftFlipper.downAngle
            rightFlipper.angle = rightFlipper.downAngle
            bumpers = mutableStateListOf(
                Bumper(Offset(canvasWidth / 2f, canvasHeight / 2f), BUMPER_RADIUS, baseColor = BUMPER_PALETTE.random()),
                Bumper(Offset(canvasWidth / 3f, canvasHeight / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                Bumper(Offset(canvasWidth * 2 / 3f, canvasHeight / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                Bumper(Offset(canvasWidth / 4f, canvasHeight * 2 / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                Bumper(Offset(canvasWidth * 3 / 4f, canvasHeight * 2 / 3f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                Bumper(Offset(canvasWidth / 4f, canvasHeight / 5f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                Bumper(Offset(canvasWidth * 3 / 4f, canvasHeight / 5f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                Bumper(Offset(canvasWidth / 3f, canvasHeight * 5 / 6f), BUMPER_RADIUS, baseColor = bumperColors.random()),
                Bumper(Offset(canvasWidth * 2 / 3f, canvasHeight * 5 / 6f), BUMPER_RADIUS, baseColor = bumperColors.random())
            )
            bumpers.forEach { it.isHit = false }
            score = 0
            plunger = Plunger(0f)
            gameState = PinballGameState.ReadyToLaunch
        }

        // Game Loop
        LaunchedEffect(gameState) {
            if (gameState != PinballGameState.Playing && gameState != PinballGameState.Launching) return@LaunchedEffect

            while (isActive && (gameState == PinballGameState.Playing || gameState == PinballGameState.Launching)) {
                withFrameNanos { _ ->
                    if (gameState == PinballGameState.Launching) {
                        var newVelocity = ball.velocity.copy(y = ball.velocity.y + 0.5f)
                        var newPosition = ball.position.plus(newVelocity)

                        if (newPosition.y < canvasHeight / 2f) {
                            gameState = PinballGameState.Playing
                        }

                        if (newPosition.y > canvasHeight - BALL_RADIUS) {
                            newPosition = newPosition.copy(y = canvasHeight - BALL_RADIUS)
                            newVelocity = newVelocity.copy(y = -newVelocity.y * 0.4f)
                        }

                        ball = ball.copy(position = newPosition, velocity = newVelocity)
                    } else { // Playing
                        var newVelocity = ball.velocity.copy(y = ball.velocity.y + 0.5f)
                        var newPosition = ball.position.plus(newVelocity)

                        // Wall collisions
                        if (newPosition.x < BALL_RADIUS) {
                            newPosition = newPosition.copy(x = BALL_RADIUS)
                            newVelocity = newVelocity.copy(x = -newVelocity.x * 0.9f)
                        } else if (newPosition.x > canvasWidth - BALL_RADIUS - PLUNGER_WIDTH - 10f && ball.position.y > canvasHeight / 2f) { // Right wall of plunger lane
                            newPosition = newPosition.copy(x = canvasWidth - BALL_RADIUS - PLUNGER_WIDTH - 10f)
                            newVelocity = newVelocity.copy(x = -newVelocity.x * 0.9f)
                        } else if (newPosition.x > canvasWidth - BALL_RADIUS) {
                            newPosition = newPosition.copy(x = canvasWidth - BALL_RADIUS)
                            newVelocity = newVelocity.copy(x = -newVelocity.x * 0.9f)
                        }

                        if (newPosition.y < BALL_RADIUS) {
                            newPosition = newPosition.copy(y = BALL_RADIUS)
                            newVelocity = newVelocity.copy(y = -newVelocity.y * 0.9f)
                        }

                        // Funnel collisions
                        val closestPointLeft = closestPointOnLine(funnelLeftLine.first, funnelLeftLine.second, newPosition)
                        val distanceLeft = (newPosition - closestPointLeft).getDistance()
                        if (distanceLeft < BALL_RADIUS + 20f) {
                            val normal = (newPosition - closestPointLeft).normalized()
                            newVelocity = newVelocity.reflect(normal) * 0.9f
                            // Reposition ball to avoid tunneling/sticking
                            newPosition = closestPointLeft + normal * (BALL_RADIUS + 20f)
                        }

                        val closestPointRight = closestPointOnLine(funnelRightLine.first, funnelRightLine.second, newPosition)
                        val distanceRight = (newPosition - closestPointRight).getDistance()
                        if (distanceRight < BALL_RADIUS + 20f) {
                            val normal = (newPosition - closestPointRight).normalized()
                            newVelocity = newVelocity.reflect(normal) * 0.9f
                            // Reposition ball to avoid tunneling/sticking
                            newPosition = closestPointRight + normal * (BALL_RADIUS + 20f)
                        }

                        // Flipper collisions
                        if (checkCollision(ball, leftFlipper, canvasWidth, canvasHeight)) {
                            newVelocity = newVelocity.copy(y = -newVelocity.y * 1.5f, x = newVelocity.x + 5f)
                            score += 10
                        }

                        if (checkCollision(ball, rightFlipper, canvasWidth, canvasHeight)) {
                            newVelocity = newVelocity.copy(y = -newVelocity.y * 1.5f, x = newVelocity.x - 5f)
                            score += 10
                        }

                        // Bumper collisions
                        bumpers.forEach { bumper ->
                            val distance = (newPosition - bumper.position).getDistance()
                            if (distance < BALL_RADIUS + bumper.radius) {
                                if (!bumper.isHit) {
                                    score += 100
                                    bumper.isHit = true
                                    coroutineScope.launch {
                                        delay(150L) // Flash duration in milliseconds
                                        bumper.isHit = false
                                    }
                                    if (usingVoice) playBump()
                                }
                                val normal = (newPosition - bumper.position).normalized()
                                newVelocity = newVelocity.reflect(normal) * 1.1f
                            }
                        }

                        ball = ball.copy(position = newPosition, velocity = newVelocity)

                        // Game Over
                        if (ball.position.y > canvasHeight) {
                            gameState = PinballGameState.GameOver
                        }
                    }
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(gameState) {
                    if (gameState == PinballGameState.ReadyToLaunch) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                plunger = plunger.copy(pull = (plunger.pull + dragAmount.y).coerceIn(0f, PLUNGER_MAX_PULL))
                            },
                            onDragEnd = {
                                ball = ball.copy(velocity = Offset(0f, -plunger.pull / 1.5f))
                                gameState = PinballGameState.Launching
                                plunger = plunger.copy(pull = 0f)
                            }
                        )
                    } else if (gameState == PinballGameState.Playing) {
                        detectTapGestures(
                            onPress = {
                                println("Tap detected at ${it.x}, ${it.y}")
                                if (it.x < canvasWidth / 2) {
                                    leftFlipper.angle = leftFlipper.upAngle
                                } else {
                                    rightFlipper.angle = rightFlipper.upAngle
                                }
                                awaitRelease()
                                leftFlipper.angle = leftFlipper.downAngle
                                rightFlipper.angle = rightFlipper.downAngle
                            }
                        )
                    }
                }
        ) {
            drawPinballTable(canvasWidth, canvasHeight)
            if (gameState != PinballGameState.GameOver) {
                drawBall(ball)
            }
            drawFlipper(leftFlipper, canvasWidth, canvasHeight)
            drawFlipper(rightFlipper, canvasWidth, canvasHeight)
            bumpers.forEach { drawBumper(it) }
            drawPlunger(plunger, canvasWidth, canvasHeight)
        }

        // UI Overlay
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = stringResource(R.string.score_label, score),
                style = TextStyle(color = UI_TEXT_COLOR, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            if (gameState == PinballGameState.GameOver) {
                if (usingVoice) playBoing()
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.game_over), style = TextStyle(color = UI_TEXT_COLOR, fontSize = 48.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { initializeGame() }) {
                        Text(stringResource(R.string.play_again))
                    }
                }
            }

            if (gameState == PinballGameState.ReadyToLaunch) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.pull_plunger),
                    style = TextStyle(color = UI_TEXT_COLOR, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                )
            }
        }


        GcImage(
            imageResource = CommonR.drawable.backarrow,
            onClick = onExit,
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
        )

        GcImage(
            imageResource = CommonR.drawable.underconstruction,
            modifier = Modifier
                .padding(bottom = 16.dp, end = 48.dp)
                .size(96.dp)
                .align(Alignment.BottomEnd)
        )
    }
}

private fun DrawScope.drawPinballTable(width: Float, height: Float) {
    drawRect(PINBALL_TABLE_BACKGROUND_COLOR)
    // Walls
    drawRect(MAIN_WALL_COLOR, size = androidx.compose.ui.geometry.Size(width, FRAME_WIDTH))
    drawRect(MAIN_WALL_COLOR, topLeft = Offset(0f, height - 20f), size = androidx.compose.ui.geometry.Size(width, FRAME_WIDTH))
    drawRect(MAIN_WALL_COLOR, size = androidx.compose.ui.geometry.Size(FRAME_WIDTH, height))
    drawRect(MAIN_WALL_COLOR, topLeft = Offset(width - 20f, 0f), size = androidx.compose.ui.geometry.Size(FRAME_WIDTH, height))

    // Plunger lane
    drawRect(PLUNGER_LANE_COLOR, topLeft = Offset(width - PLUNGER_WIDTH - 20f, height / 3.0f), size = androidx.compose.ui.geometry.Size(LANE_WALL_WIDTH, PLUNGER_MAX_PULL + (height + 240) / 2f) )

    // Funnel
    drawLine(FUNNEL_COLOR, start = Offset(0f, height - 300f), end = Offset(width / 2f - FLIPPER_WIDTH / 2, height - 100f), strokeWidth = LANE_WALL_WIDTH)
    drawLine(FUNNEL_COLOR, start = Offset(width - PLUNGER_WIDTH - 10f, height - 300f), end = Offset(width / 2f + FLIPPER_WIDTH / 2, height - 100f), strokeWidth = LANE_WALL_WIDTH)
}

private fun DrawScope.drawBall(ball: Ball) {
    drawCircle(BALL_COLOR, radius = BALL_RADIUS, center = ball.position)
}

private fun DrawScope.drawFlipper(flipper: Flipper, canvasWidth: Float, canvasHeight: Float) {
    val pivot = if (flipper.side == FlipperSide.LEFT) {
        Offset(canvasWidth / 2f - FLIPPER_WIDTH / 2, canvasHeight - 100f)
    } else {
        Offset(canvasWidth / 2f + FLIPPER_WIDTH / 2, canvasHeight - 100f)
    }

    rotate(degrees = flipper.angle, pivot = pivot) {
        drawRect(
            color = flipper.color,
            topLeft = pivot - Offset(FLIPPER_WIDTH / 2, FLIPPER_HEIGHT / 2),
            size = androidx.compose.ui.geometry.Size(FLIPPER_WIDTH, FLIPPER_HEIGHT)
        )
    }
}

private fun DrawScope.drawBumper(bumper: Bumper) {
    drawCircle(
        color = if (bumper.isHit) BUMPER_HIT_COLOR else bumper.baseColor,
        radius = bumper.radius,
        center = bumper.position
    )
}

private fun DrawScope.drawPlunger(plunger: Plunger, canvasWidth: Float, canvasHeight: Float) {
    drawRect(
        color = PLUNGER_COLOR,
        topLeft = Offset(canvasWidth - PLUNGER_WIDTH, canvasHeight - 100f + plunger.pull),
        size = androidx.compose.ui.geometry.Size(PLUNGER_WIDTH, PLUNGER_HEIGHT)
    )
}

private fun checkCollision(ball: Ball, flipper: Flipper, canvasWidth: Float, canvasHeight: Float): Boolean {
    val pivot = if (flipper.side == FlipperSide.LEFT) {
        Offset(canvasWidth / 2f - FLIPPER_WIDTH / 2, canvasHeight - 100f)
    } else {
        Offset(canvasWidth / 2f + FLIPPER_WIDTH / 2, canvasHeight - 100f)
    }

    val ballRotated = ball.position.rotate(-flipper.angle, pivot)
    val flipperRect = androidx.compose.ui.geometry.Rect(
        pivot - Offset(FLIPPER_WIDTH / 2, FLIPPER_HEIGHT / 2),
        androidx.compose.ui.geometry.Size(FLIPPER_WIDTH, FLIPPER_HEIGHT)
    )

    return flipperRect.contains(ballRotated)
}

private fun Offset.getDistance() = sqrt(x * x + y * y)

private fun Offset.getDistanceSq() = x * x + y * y

private fun Offset.rotate(angle: Float, center: Offset): Offset {
    val angleRad = Math.toRadians(angle.toDouble()).toFloat()
    val cosAngle = cos(angleRad)
    val sinAngle = sin(angleRad)
    val translated = this - center
    val rotated = Offset(
        translated.x * cosAngle - translated.y * sinAngle,
        translated.x * sinAngle + translated.y * cosAngle
    )
    return rotated + center
}

private fun Offset.normalized(): Offset {
    val length = getDistance()
    return if (length > 0) this / length else this
}

private fun Offset.reflect(normal: Offset): Offset {
    return this - normal * 2f * (this.x * normal.x + this.y * normal.y)
}

private fun closestPointOnLine(start: Offset, end: Offset, point: Offset): Offset {
    val line = end - start
    val lineLengthSq = line.getDistanceSq()
    if (lineLengthSq == 0f) return start

    val t = ((point - start).x * line.x + (point - start).y * line.y) / lineLengthSq
    val tClamped = t.coerceIn(0f, 1f)

    return start + line * tClamped
}

@Preview
@Composable
private fun PreviewPinballView() {
    GcTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            PinballView(init = false)
        }
    }
}

@Preview
@Composable
private fun TabletPreview() {
    GcTheme {
        Surface {
            PinballView(modifier = Modifier.fillMaxSize(), init = false)
        }
    }
}

@Composable
private fun GcImage(
    imageResource: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    androidx.compose.foundation.Image(
        painter = org.jetbrains.compose.resources.painterResource(imageResource),
        contentDescription = null,
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        contentScale = contentScale
    )
}

@Composable
private fun GcImage(
    imageResource: Int?,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    // No-op box for dummy / layout placeholder integer resources
    androidx.compose.foundation.layout.Box(modifier)
}

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

import androidx.compose.runtime.Composable



import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.painterResource

import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcTheme

import com.funhouse.shared.common.SettingsBanner
import com.funhouse.shared.common.jni.BaseNativeGame
import com.funhouse.shared.common.TABLE_COLOR_BLACK
import com.funhouse.shared.common.models.currentSettings
import kotlinx.coroutines.isActive
import kotlin.random.Random

private const val PADDLE_WIDTH = 100f
private const val PADDLE_HEIGHT = 20f
private const val BALL_RADIUS = 15f
private const val WINNING_SCORE = 5
private val TEXT_COLOR = Color.White
private val WINNING_COLOR = Color.White
private val BALL_COLOR = Color.White
private val PADDLE_COLOR = Color.White
private val NET_COLOR = Color.White

enum class PaddleBallDifficulty { EASY, MEDIUM, HARD }

private data class PaddleBallDifficultySettings(val gepettoSpeed: Float, val ballSpeed: Float)

private val difficultySettings = mapOf(
    PaddleBallDifficulty.EASY to PaddleBallDifficultySettings(gepettoSpeed = 0.08f, ballSpeed = 8f),
    PaddleBallDifficulty.MEDIUM to PaddleBallDifficultySettings(gepettoSpeed = 0.12f, ballSpeed = 12f),
    PaddleBallDifficulty.HARD to PaddleBallDifficultySettings(gepettoSpeed = 0.20f, ballSpeed = 16f)
)

@Composable
fun PaddleBallView(
    modifier: Modifier = Modifier,
    difficultyStart: PaddleBallDifficulty = PaddleBallDifficulty.MEDIUM,
    init: Boolean = true,
    onExit: () -> Unit = {},
) {
    val difficulty by remember { mutableStateOf(difficultyStart)}
    val settings = difficultySettings.getValue(difficulty)
    var winner by remember { mutableStateOf<String?>("none") }
    var playerScore by remember { mutableStateOf(0) }
    var gepettoScore by remember { mutableStateOf(0) }
    var usingVoice by remember { mutableStateOf(value = if (init) currentSettings.usingVoice else false) }
    var showSettings by remember { mutableStateOf(false) }
    var firstTime by remember { mutableStateOf(true) }

    if (firstTime) {
        if (init) {
            ClassicArcades.restoreGame()
            BaseNativeGame.initializeMedia(LocalContext.current)
        }
        firstTime = false
    }

    Box(modifier.background(TABLE_COLOR_BLACK)) {
        Column {

            Column(Modifier.height(64.dp)) {
                if (winner == null) {
                    Row {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(stringResource(R.string.you_score_label, playerScore), style = TextStyle(fontSize = 24.sp, color = TEXT_COLOR), modifier = Modifier.padding(16.dp))
                            Text(stringResource(R.string.gepetto_score_label, gepettoScore), style = TextStyle(fontSize = 24.sp, color = TEXT_COLOR), modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }

            BoxWithConstraints(modifier = Modifier.padding(bottom = 64.dp).fillMaxSize()) {
                val canvasWidth = constraints.maxWidth.toFloat()
                val canvasHeight = constraints.maxHeight.toFloat()

                var ballPosition by remember(canvasWidth, canvasHeight) { mutableStateOf(Offset(canvasWidth / 2, canvasHeight / 2)) }
                var ballVelocity by remember(canvasWidth, canvasHeight, difficulty) {
                    mutableStateOf(
                        Offset(
                            x = if (Random.nextBoolean()) settings.ballSpeed else -settings.ballSpeed,
                            y = settings.ballSpeed
                        )
                    )
                }

                var playerPaddlePosition by remember(canvasWidth) { mutableStateOf(PADDLE_WIDTH / 2) }
                var gepettoPaddlePosition by remember(canvasWidth) { mutableStateOf(canvasWidth / 2 - PADDLE_WIDTH / 2) }
                var gameIsRunning by remember { mutableStateOf(false) }

                fun resetBall(serveToPlayer: Boolean) {
                    ballPosition = Offset(canvasWidth / 2, canvasHeight / 2)
                    val randomX = if (Random.nextBoolean()) 1f else -1f
                    val randomY = if (serveToPlayer) 1f else -1f
                    ballVelocity = Offset(x = settings.ballSpeed * randomX, y = settings.ballSpeed * randomY)
                }

                LaunchedEffect(gameIsRunning, canvasWidth, canvasHeight, difficulty) {
                    if (!gameIsRunning || canvasWidth == 0f) {
                        return@LaunchedEffect
                    }

                    while (isActive) {
                        withFrameNanos { _ ->
                            if (!gameIsRunning) return@withFrameNanos

                            ballPosition += ballVelocity
                            val aiTarget = ballPosition.x - PADDLE_WIDTH / 2
                            gepettoPaddlePosition += (aiTarget - gepettoPaddlePosition) * settings.gepettoSpeed
                            gepettoPaddlePosition = gepettoPaddlePosition.coerceIn(0f, canvasWidth - PADDLE_WIDTH)

                            if (ballPosition.x <= BALL_RADIUS || ballPosition.x >= canvasWidth - BALL_RADIUS) {
                                ballVelocity = ballVelocity.copy(x = -ballVelocity.x)
                            }

                            val playerPaddleY = canvasHeight - PADDLE_HEIGHT - 50f
                            val aiPaddleY = 50f
                            val playerPaddleRect = androidx.compose.ui.geometry.Rect(playerPaddlePosition, playerPaddleY, playerPaddlePosition + PADDLE_WIDTH, playerPaddleY + PADDLE_HEIGHT)
                            val aiPaddleRect = androidx.compose.ui.geometry.Rect(gepettoPaddlePosition, aiPaddleY, gepettoPaddlePosition + PADDLE_WIDTH, aiPaddleY + PADDLE_HEIGHT)

                            if (ballVelocity.y > 0 && ballPosition.y + BALL_RADIUS >= playerPaddleY && ballPosition.x in playerPaddleRect.left..playerPaddleRect.right) {
                                // ball hit by paddle
                                if (usingVoice) BaseNativeGame.playTennisBall()
                                ballVelocity = ballVelocity.copy(y = -ballVelocity.y)
                            } else if (ballVelocity.y < 0 && ballPosition.y - BALL_RADIUS <= aiPaddleY + PADDLE_HEIGHT && ballPosition.x in aiPaddleRect.left..aiPaddleRect.right) {
                                // ball hit by paddle
                                if (usingVoice) BaseNativeGame.playTennisBall()
                                ballVelocity = ballVelocity.copy(y = -ballVelocity.y)
                            }

                            if (ballPosition.y >= canvasHeight) {
                                gepettoScore++
                                if (usingVoice) BaseNativeGame.playBell()
                                if (gepettoScore >= WINNING_SCORE) {
                                    if (usingVoice) BaseNativeGame.playBell()
                                    winner = "Gepetto"
                                    gameIsRunning = false
                                    ClassicArcades.saveGame(-1 - difficulty.ordinal * 5)
                                } else {
                                    resetBall(false)
                                }
                            } else if (ballPosition.y <= 0) {
                                playerScore++
                                if (usingVoice) BaseNativeGame.playBell()
                                if (playerScore >= WINNING_SCORE) {
                                    if (usingVoice) BaseNativeGame.playBell()
                                    winner = "You"
                                    gameIsRunning = false
                                    ClassicArcades.saveGame(1 + difficulty.ordinal * 5)
                                } else {
                                    resetBall(true)
                                }
                            }
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newPos = playerPaddlePosition + dragAmount.x
                                playerPaddlePosition = newPos.coerceIn(0f, canvasWidth - PADDLE_WIDTH)
                            }
                        }
                ) {
                    val playerPaddleY = size.height - PADDLE_HEIGHT - 50f
                    val aiPaddleY = 50f
                    // Draw net
                    for (i in 0..size.width.toInt() step 30) {
                        drawRect(NET_COLOR, topLeft = Offset(i.toFloat(), size.height / 2), size = Size(10f, 15f))
                    }

                    drawRect(PADDLE_COLOR, topLeft = Offset(playerPaddlePosition, playerPaddleY), size = Size(PADDLE_WIDTH, PADDLE_HEIGHT))
                    drawRect(PADDLE_COLOR, topLeft = Offset(gepettoPaddlePosition, aiPaddleY), size = Size(PADDLE_WIDTH, PADDLE_HEIGHT))
                    drawCircle(BALL_COLOR, radius = BALL_RADIUS, center = ballPosition)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (winner != null) {
                        Spacer(modifier = Modifier.height(48.dp))

                        if (winner != "none") {
                            val text = if (winner == "You") stringResource(R.string.you_won_arcade) else stringResource(R.string.gepetto_wins)
                            Text(text, style = TextStyle(fontSize = 48.sp, color = WINNING_COLOR))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(enabled = true, onClick = {
                            playerScore = 0
                            gepettoScore = 0
                            winner = null
                            resetBall(true)
                            gameIsRunning = true
                        }) { if (winner == "none") Text(stringResource(R.string.play)) else Text(stringResource(R.string.play_again)) }
                    }
                }
            }
        }

        GcImage(
            imageResource = CommonR.drawable.backarrow,
            onClick = onExit,
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp).size(32.dp)
        )

        Icon(
            painter = painterResource(CommonR.drawable.ic_profile),
            contentDescription = stringResource(R.string.profile),
            tint = TEXT_COLOR,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .size(32.dp)
                .align(Alignment.BottomStart)
                .clickable { showSettings = !showSettings }
        )

        Icon(
            painter = painterResource(if (usingVoice) CommonR.drawable.call_spk_on else CommonR.drawable.speaker_off),
            contentDescription = if (usingVoice) stringResource(R.string.turn_off_sound) else stringResource(R.string.turn_on_sound),
            tint = TEXT_COLOR,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(32.dp)
                .align(Alignment.BottomCenter)
                .clickable {
                    if (init) {
                        usingVoice = !usingVoice
                        currentSettings.usingVoice = usingVoice
                    }
                }
        )




        GcImage(
            imageResource = CommonR.drawable.underconstruction,
            modifier = Modifier
                .padding(bottom = 4.dp, end = 4.dp)
                .size(64.dp)
                .align(Alignment.BottomEnd)
        )

        if (showSettings)
            SettingsBanner(ClassicArcades.getGameWalletValue(), usingVoice, onExit = { showSettings = false }) { usingVoice = it}
    }
}

@Preview
@Composable
private fun PreviewFunc() {
    Surface {
        PaddleBallView(
            modifier = Modifier.fillMaxSize(), init = false,
        )
    }
}

@Preview
@Composable
private fun LandscapePreview() {
    GcTheme {
        Surface {
            PaddleBallView(
                modifier = Modifier.fillMaxSize(), init = false,
            )
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

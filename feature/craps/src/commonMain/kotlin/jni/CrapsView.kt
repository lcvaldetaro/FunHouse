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



import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.R as CrapsR
import com.funhouse.shared.common.getString

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.funhouse.shared.common.TABLE_COLOR_GREEN
import com.funhouse.shared.common.models.currentSettings
import com.funhouse.shared.common.SettingsBanner
import com.funhouse.shared.common.jni.BaseNativeGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val DICE_BACKGROUND = Color.White
private val DICE_DOT = Color.Black
private val TEXT_COLOR = Color.White
private val DICE_FACE_SIZE = 48.dp

private enum class CrapsGameState {
    COME_OUT_ROLL,
    POINT_ROLL,
    PLAYER_WINS,
    PLAYER_LOSES
}

@Composable
fun CrapsView(
    modifier: Modifier = Modifier,
    init: Boolean = true,
    onExit: () -> Unit = {},
) {
    val context = LocalContext.current
    var dice1 by remember { mutableIntStateOf(1) }
    var dice2 by remember { mutableIntStateOf(1) }
    var point by remember { mutableStateOf<Int?>(null) }
    
    var messageResId by remember { mutableIntStateOf(CrapsR.string.roll_to_start) }
    var messageArgs by remember { mutableStateOf<List<Any>>(emptyList()) }
    
    val message = if (messageArgs.isEmpty()) {
        stringResource(messageResId)
    } else {
        stringResource(messageResId, *messageArgs.toTypedArray())
    }

    var gameState by remember { mutableStateOf(CrapsGameState.COME_OUT_ROLL) }
    var isRolling by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var dice1Offset by remember { mutableStateOf(Offset.Zero) }
    var dice2Offset by remember { mutableStateOf(Offset.Zero) }
    var dice1Rotation by remember { mutableFloatStateOf(0f) }
    var dice2Rotation by remember { mutableFloatStateOf(0f) }

    var showSettings by remember { mutableStateOf(false)}
    val gameValue by remember { mutableStateOf(Craps.getGameWalletValue())}
    var usingVoice by remember { mutableStateOf(currentSettings.usingVoice) }

    if (init) {
        SideEffect {
            BaseNativeGame.initializeMedia(context)
        }
    }

    // Game logic
    fun handleRoll() {
        val sum = dice1 + dice2
        when (gameState) {
            CrapsGameState.COME_OUT_ROLL -> {
                when (sum) {
                    7, 11 -> {
                        messageResId = CrapsR.string.you_rolled_win
                        messageArgs = listOf(sum)
                        gameState = CrapsGameState.PLAYER_WINS
                    }
                    2, 3, 12 -> {
                        messageResId = CrapsR.string.you_rolled_craps_lose
                        messageArgs = listOf(sum)
                        gameState = CrapsGameState.PLAYER_LOSES
                    }
                    else -> {
                        point = sum
                        messageResId = CrapsR.string.you_rolled_point
                        messageArgs = listOf(sum, point!!)
                        gameState = CrapsGameState.POINT_ROLL
                    }
                }
            }
            CrapsGameState.POINT_ROLL -> {
                when (sum) {
                    point -> {
                        messageResId = CrapsR.string.you_rolled_hit_point
                        messageArgs = listOf(sum)
                        gameState = CrapsGameState.PLAYER_WINS
                    }
                    7 -> {
                        messageResId = CrapsR.string.you_rolled_seven_out
                        messageArgs = emptyList()
                        gameState = CrapsGameState.PLAYER_LOSES
                    }
                    else -> {
                        messageResId = CrapsR.string.you_rolled_still_point
                        messageArgs = listOf(sum, point!!)
                    }
                }
            }
            CrapsGameState.PLAYER_WINS, CrapsGameState.PLAYER_LOSES -> {
                // Game is over, reset for new game
                point = null
                messageResId = CrapsR.string.roll_to_start_new
                messageArgs = emptyList()
                gameState = CrapsGameState.COME_OUT_ROLL
            }
        }
    }


    // Helper function to roll dice with animation
    fun rollDiceAnimated() {
        coroutineScope.launch {
            if (currentSettings.usingVoice) BaseNativeGame.playDice()
            isRolling = true
            val animationDuration = 700L // milliseconds
            val startTime = System.currentTimeMillis()

            // Initial random positions (off-screen bottom)
            dice1Offset = Offset(Random.nextFloat() * 200 - 100, 200f) // Random X, off-screen Y (bottom)
            dice2Offset = Offset(Random.nextFloat() * 200 - 100, 200f) // Random X, off-screen Y (bottom)
            dice1Rotation = Random.nextFloat() * 360
            dice2Rotation = Random.nextFloat() * 360

            val animatableOffset1 = Animatable(dice1Offset.y)
            val animatableOffset2 = Animatable(dice2Offset.y)
            val animatableRotation1 = Animatable(dice1Rotation)
            val animatableRotation2 = Animatable(dice2Rotation)

            // Animate dice falling (moving up to center) and rotating
            launch {
                animatableOffset1.animateTo(
                    targetValue = 0f, // Settle near center (relative to Row)
                    animationSpec = tween(durationMillis = animationDuration.toInt(), easing = EaseOutBounce)
                ) { dice1Offset = dice1Offset.copy(y = value) }
            }
            launch {
                animatableOffset2.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = animationDuration.toInt(), easing = EaseOutBounce)
                ) { dice2Offset = dice2Offset.copy(y = value) }
            }
            launch {
                animatableRotation1.animateTo(
                    targetValue = dice1Rotation + Random.nextFloat() * 720 + 360, // Spin multiple times
                    animationSpec = tween(durationMillis = animationDuration.toInt(), easing = EaseOutCubic)
                ) { dice1Rotation = value }
            }
            launch {
                animatableRotation2.animateTo(
                    targetValue = dice2Rotation + Random.nextFloat() * 720 + 360,
                    animationSpec = tween(durationMillis = animationDuration.toInt(), easing = EaseOutCubic)
                ) { dice2Rotation = value }
            }

            // Rapidly change numbers during animation
            while (System.currentTimeMillis() - startTime < animationDuration) {
                dice1 = Random.nextInt(1, 7)
                dice2 = Random.nextInt(1, 7)
                delay(50) // Update dice every 50ms
            }

            // Ensure final values are set after animation
            dice1 = Random.nextInt(1, 7)
            dice2 = Random.nextInt(1, 7)

            // Reset animation states for next roll
            dice1Offset = Offset.Zero
            dice2Offset = Offset.Zero
            dice1Rotation = 0f
            dice2Rotation = 0f

            isRolling = false
            handleRoll()
        }
    }

    Box (modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            Text(text = stringResource(CrapsR.string.craps_game_title), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TEXT_COLOR)

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                DiceFace(value = dice1, modifier = Modifier.size(DICE_FACE_SIZE), currentOffset = dice1Offset, currentRotation = dice1Rotation)
                DiceFace(value = dice2, modifier = Modifier.size(DICE_FACE_SIZE), currentOffset = dice2Offset, currentRotation = dice2Rotation)
            }

            Spacer(modifier = Modifier.height(16.dp))

            point?.let {
                Text(text = stringResource(CrapsR.string.point_label, it), fontSize = 24.sp, color = TEXT_COLOR)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = message, fontSize = 20.sp, textAlign = TextAlign.Center, color = TEXT_COLOR)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.padding(bottom = 64.dp),
                onClick = {
                    if (gameState == CrapsGameState.PLAYER_WINS || gameState == CrapsGameState.PLAYER_LOSES) {
                        // Reset game state before rolling for a new game
                        point = null
                        messageResId = CrapsR.string.roll_to_start
                        messageArgs = emptyList()
                        gameState = CrapsGameState.COME_OUT_ROLL
                        dice1Offset = Offset.Zero
                        dice2Offset = Offset.Zero
                        dice1Rotation = 0f
                        dice2Rotation = 0f
                        rollDiceAnimated()
                    } else {
                        rollDiceAnimated()
                    }
                },
                enabled = !isRolling
            ) {
                Text(text = if (gameState == CrapsGameState.PLAYER_WINS || gameState == CrapsGameState.PLAYER_LOSES) stringResource(CrapsR.string.play_again) else stringResource(CrapsR.string.roll_dice), color = TEXT_COLOR)
            }
        }



        GcImage(
            imageResource = CommonR.drawable.backarrow,
            onClick = onExit,
            modifier = Modifier
                .padding(start = 4.dp, top = 4.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
        )

        Icon(
            painter = painterResource(CommonR.drawable.ic_profile),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(Alignment.BottomStart)
                .clickable{ showSettings = !showSettings }
        )

        Icon(
            painter = painterResource(if (usingVoice) CommonR.drawable.call_spk_on else CommonR.drawable.speaker_off),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(Alignment.BottomCenter)
                .clickable{ currentSettings.usingVoice = ! currentSettings.usingVoice; usingVoice = currentSettings.usingVoice }
        )

        GcImage(
            imageResource = CommonR.drawable.underconstruction,
            modifier = Modifier
                .padding(bottom = 4.dp, end = 4.dp)
                .size(64.dp)
                .align(Alignment.BottomEnd)
        )

        if (showSettings)
            SettingsBanner(gameValue, usingVoice, onExit = { showSettings = false }) { usingVoice = it}
    }
}

private const val DOT_RADIUS_RATIO = 0.12f // Ratio of dot radius to dice size
private const val DOT_OFFSET_RATIO = 0.25f // Ratio of dot offset from edge to dice size

@Composable
fun DiceFace(
    value: Int,
    modifier: Modifier = Modifier,
    currentOffset: Offset = Offset.Zero,
    currentRotation: Float = 0f
) {
    Canvas(modifier = modifier
        .offset(x = currentOffset.x.dp, y = currentOffset.y.dp)
        .rotate(currentRotation)
    ) {
        val diceSize = size.minDimension
        val dotRadius = diceSize * DOT_RADIUS_RATIO
        val dotOffset = diceSize * DOT_OFFSET_RATIO

        // Draw the dice background
        drawRoundRect(color = DICE_BACKGROUND, cornerRadius = CornerRadius(16f, 16f))

        // Draw the dots
        when (value) {
            1 -> {
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize / 2, diceSize / 2))
            }
            2 -> {
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, diceSize - dotOffset))
            }
            3 -> {
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize / 2, diceSize / 2))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, diceSize - dotOffset))
            }
            4 -> {
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, diceSize - dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, diceSize - dotOffset))
            }
            5 -> {
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, diceSize - dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, diceSize - dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize / 2, diceSize / 2))
            }
            6 -> {
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, diceSize / 2))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, diceSize / 2))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(dotOffset, diceSize - dotOffset))
                drawCircle(color = DICE_DOT, radius = dotRadius, center = Offset(diceSize - dotOffset, diceSize - dotOffset))
            }
        }
    }
}

@Preview
@Composable
fun PreviewCrapsView() {
    CrapsView(Modifier.background(TABLE_COLOR_GREEN), init = false)
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

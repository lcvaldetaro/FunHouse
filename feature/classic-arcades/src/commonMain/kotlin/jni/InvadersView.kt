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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.funhouse.shared.common.TABLE_COLOR_BLACK
import kotlinx.coroutines.isActive
import kotlin.random.Random

private const val PLAYER_WIDTH = 80f
private const val PLAYER_HEIGHT = 40f
private const val ALIEN_GRID_ROWS = 5
private const val ALIEN_GRID_COLS = 11
private const val ALIEN_SIZE = 40f
private const val ALIEN_SPACING = 20f
private val TABLE_COLOR = TABLE_COLOR_BLACK
private val PLAYER_BULLET_COLOR = Color.Green
private val ALIEN_BULLET_COLOR = Color.Red
private val TEXT_COLOR = Color.Cyan
private val FIRE_BUTTON_COLOR = Color.Red
private val PLAYER_COLOR = Color.Green
private val ALIEN_COLOR = Color.Magenta

private data class Alien(var rect: Rect, var isAlive: Boolean = true)
private data class Bullet(var rect: Rect)

enum class InvadersGameState { Playing, GameOver, Won }

enum class InvaderDifficulty { Easy, Medium, Hard }

data class InvadersDifficultySettings(
    val alienSpeed: Float,
    val alienSpeedIncreaseFactor: Float,
    val alienFiringChance: Float,
    val alienBulletSpeed: Float,
    val playerBulletSpeed: Float
)

private val easySettings = InvadersDifficultySettings(alienSpeed = 1f, alienSpeedIncreaseFactor = 1.02f, alienFiringChance = 0.01f, alienBulletSpeed = 8f, playerBulletSpeed = 22f)
private val mediumSettings = InvadersDifficultySettings(alienSpeed = 2f, alienSpeedIncreaseFactor = 1.05f, alienFiringChance = 0.02f, alienBulletSpeed = 10f, playerBulletSpeed = 20f)
private val hardSettings = InvadersDifficultySettings(alienSpeed = 3f, alienSpeedIncreaseFactor = 1.08f, alienFiringChance = 0.04f, alienBulletSpeed = 12f, playerBulletSpeed = 18f)

@Composable
fun InvadersView(
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {},
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(TABLE_COLOR)) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()

        var playerPosition by remember { mutableStateOf(Offset(canvasWidth / 2, canvasHeight - PLAYER_HEIGHT * 2)) }
        val aliens = remember { mutableStateListOf<Alien>() }
        val playerBullets = remember { mutableStateListOf<Bullet>() }
        val alienBullets = remember { mutableStateListOf<Bullet>() }
        var score by remember { mutableStateOf(0) }
        var gameState by remember { mutableStateOf(InvadersGameState.Playing) }
        var difficulty by remember { mutableStateOf(InvaderDifficulty.Medium) }

        var alienDirection by remember { mutableStateOf(1f) } // 1 for right, -1 for left
        var alienMoveDown by remember { mutableStateOf(0f) }
        var alienSpeed by remember { mutableStateOf(2f) }

        fun initializeGame() {
            val settings = when (difficulty) {
                InvaderDifficulty.Easy -> easySettings
                InvaderDifficulty.Medium -> mediumSettings
                InvaderDifficulty.Hard -> hardSettings
            }
            playerPosition = Offset(canvasWidth / 2, canvasHeight - PLAYER_HEIGHT * 2)
            aliens.clear()
            val gridWidth = ALIEN_GRID_COLS * (ALIEN_SIZE + ALIEN_SPACING) - ALIEN_SPACING
            val startX = (canvasWidth - gridWidth) / 2
            val startY = 100f
            for (row in 0 until ALIEN_GRID_ROWS) {
                for (col in 0 until ALIEN_GRID_COLS) {
                    val x = startX + col * (ALIEN_SIZE + ALIEN_SPACING)
                    val y = startY + row * (ALIEN_SIZE + ALIEN_SPACING)
                    aliens.add(Alien(Rect(x, y, x + ALIEN_SIZE, y + ALIEN_SIZE)))
                }
            }
            playerBullets.clear()
            alienBullets.clear()
            score = 0
            alienDirection = 1f
            alienSpeed = settings.alienSpeed
            gameState = InvadersGameState.Playing
        }

        LaunchedEffect(canvasWidth, canvasHeight) {
            if (canvasWidth > 0) {
                initializeGame()
            }
        }

        // Main Game Loop
        LaunchedEffect(gameState) {
            if (gameState != InvadersGameState.Playing) return@LaunchedEffect

            val settings = when (difficulty) {
                InvaderDifficulty.Easy -> easySettings
                InvaderDifficulty.Medium -> mediumSettings
                InvaderDifficulty.Hard -> hardSettings
            }

            while (isActive) {
                withFrameNanos { _ ->
                    // Move Player Bullets
                    playerBullets.forEach { it.rect = it.rect.translate(0f, -settings.playerBulletSpeed) }
                    playerBullets.removeIf { it.rect.bottom < 0 }

                    // Move Alien Bullets
                    alienBullets.forEach { it.rect = it.rect.translate(0f, settings.alienBulletSpeed) }
                    alienBullets.removeIf { it.rect.top > canvasHeight }

                    // Move Aliens
                    var switchDirection = false
                    for (alien in aliens) {
                        if (!alien.isAlive) continue
                        alien.rect = alien.rect.translate(alienSpeed * alienDirection, alienMoveDown)
                        if ((alien.rect.right > canvasWidth && alienDirection > 0) || (alien.rect.left < 0 && alienDirection < 0)) {
                            switchDirection = true
                        }
                        if (alien.rect.bottom >= playerPosition.y) {
                            gameState = InvadersGameState.GameOver
                        }
                    }
                    alienMoveDown = 0f
                    if (switchDirection) {
                        alienDirection *= -1
                        alienMoveDown = ALIEN_SIZE / 2
                        alienSpeed *= settings.alienSpeedIncreaseFactor // Speed up slightly
                    }

                    // Alien Firing
                    if (Random.nextFloat() < settings.alienFiringChance) { // % chance to fire each frame
                        aliens.filter { it.isAlive }.randomOrNull()?.let {
                            val bulletX = it.rect.center.x
                            val bulletY = it.rect.bottom
                            alienBullets.add(Bullet(Rect(bulletX - 5, bulletY, bulletX + 5, bulletY + 20)))
                        }
                    }

                    // Collision Detection
                    val playerRect = Rect(playerPosition.x - PLAYER_WIDTH / 2, playerPosition.y, playerPosition.x + PLAYER_WIDTH / 2, playerPosition.y + PLAYER_HEIGHT)
                    alienBullets.forEach { bullet ->
                        if (bullet.rect.overlaps(playerRect)) {
                            gameState = InvadersGameState.GameOver
                        }
                    }

                    val bulletsToRemove = mutableListOf<Bullet>()
                    val aliensToKill = mutableListOf<Alien>()
                    playerBullets.forEach { bullet ->
                        aliens.filter{ it.isAlive }.forEach { alien ->
                            if (bullet.rect.overlaps(alien.rect)) {
                                bulletsToRemove.add(bullet)
                                aliensToKill.add(alien)
                                score += 10
                            }
                        }
                    }
                    playerBullets.removeAll(bulletsToRemove)
                    aliensToKill.forEach { it.isAlive = false }

                    if (aliens.all { !it.isAlive }) {
                        gameState = InvadersGameState.Won
                    }
                }
            }
        }

        // Game Canvas for drawing and input
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newX = (playerPosition.x + dragAmount.x).coerceIn(PLAYER_WIDTH / 2, canvasWidth - PLAYER_WIDTH / 2)
                        playerPosition = playerPosition.copy(x = newX)
                    }
                )
            }
        ) {
            drawPlayer(playerPosition)
            aliens.forEach { if (it.isAlive) drawAlien(it.rect) }
            playerBullets.forEach { drawBullet(it.rect, PLAYER_BULLET_COLOR) }
            alienBullets.forEach { drawBullet(it.rect, ALIEN_BULLET_COLOR) }
        }

        // UI Overlay
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            GcImage(
                imageResource = CommonR.drawable.backarrow,
                onClick = onExit,
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
                    .align(Alignment.TopStart)
            )



            // Top UI: Score
            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = stringResource(R.string.score_label, score),
                style = TextStyle(color = TEXT_COLOR, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            // Center UI: Game Over/Won message
            if (gameState != InvadersGameState.Playing) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val message = if (gameState == InvadersGameState.GameOver) stringResource(R.string.game_over) else stringResource(R.string.you_won)
                    Text(message, style = TextStyle(color = TEXT_COLOR, fontSize = 48.sp, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(stringResource(R.string.select_difficulty), style = TextStyle(color = TEXT_COLOR, fontSize = 24.sp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(onClick = { difficulty = InvaderDifficulty.Easy }) { Text(stringResource(R.string.easy)) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = { difficulty = InvaderDifficulty.Medium }) { Text(stringResource(R.string.medium)) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = { difficulty = InvaderDifficulty.Hard }) { Text(stringResource(R.string.hard)) }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = { initializeGame() }) {
                        Text(stringResource(R.string.play_again))
                    }
                }
            }


            // Left-Center UI: Fire Button
            Button(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(32.dp),
                onClick = {
                    if (playerBullets.size < 3) { // Limit player bullets on screen
                        playerBullets.add(Bullet(Rect(playerPosition.x - 5, playerPosition.y, playerPosition.x + 5, playerPosition.y - 20)))
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = FIRE_BUTTON_COLOR),
                enabled = gameState == InvadersGameState.Playing
            ) {
                // No text, the button is just a red circle
            }
        }
        GcImage(
            imageResource = CommonR.drawable.underconstruction,
            modifier = Modifier
                .padding(bottom = 16.dp, end = 4.dp)
                .size(64.dp)
                .align(Alignment.BottomEnd)
        )
    }
}

private fun DrawScope.drawPlayer(position: Offset) {
    val baseWidth = PLAYER_WIDTH
    val baseHeight = PLAYER_HEIGHT * 0.6f
    val cannonWidth = PLAYER_WIDTH * 0.2f
    val cannonHeight = PLAYER_HEIGHT * 0.4f

    val baseTopLeft = Offset(position.x - baseWidth / 2, position.y + cannonHeight)
    val cannonTopLeft = Offset(position.x - cannonWidth / 2, position.y)

    drawRect(
        color = PLAYER_COLOR,
        topLeft = baseTopLeft,
        size = androidx.compose.ui.geometry.Size(baseWidth, baseHeight)
    )
    drawRect(
        color = PLAYER_COLOR,
        topLeft = cannonTopLeft,
        size = androidx.compose.ui.geometry.Size(cannonWidth, cannonHeight)
    )
}

private fun DrawScope.drawAlien(rect: Rect) {
    // Recreate the icon shape programmatically
    val pixelSize = rect.width / 11f // The icon is on an 11-pixel wide grid
    val color = ALIEN_COLOR

    // Row 1
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 4 * pixelSize, y = rect.top), size = androidx.compose.ui.geometry.Size(pixelSize * 3, pixelSize))
    // Row 2
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 3 * pixelSize, y = rect.top + pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize * 5, pixelSize))
    // Row 3
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 2 * pixelSize, y = rect.top + 2 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize * 7, pixelSize))
    // Row 4 (with eyes)
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 1 * pixelSize, y = rect.top + 3 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize * 9, pixelSize))
    // Row 5
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left, y = rect.top + 4 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize * 11, pixelSize))
    // Row 6
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left, y = rect.top + 5 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 2 * pixelSize, y = rect.top + 5 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize * 7, pixelSize))
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 10 * pixelSize, y = rect.top + 5 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
    // Row 7
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left, y = rect.top + 6 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 2 * pixelSize, y = rect.top + 6 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 8 * pixelSize, y = rect.top + 6 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 10 * pixelSize, y = rect.top + 6 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
    // Row 8 (bottom)
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 3 * pixelSize, y = rect.top + 7 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
    drawRect(color, topLeft = rect.topLeft.copy(x = rect.left + 7 * pixelSize, y = rect.top + 7 * pixelSize), size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize))
}

private fun DrawScope.drawBullet(rect: Rect, color: Color) {
    drawRect(color = color, topLeft = rect.topLeft, size = rect.size)
}

@Preview
@Composable
private fun PreviewInvadersView() {
    Surface(modifier = Modifier.fillMaxSize()) {
        InvadersView()
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

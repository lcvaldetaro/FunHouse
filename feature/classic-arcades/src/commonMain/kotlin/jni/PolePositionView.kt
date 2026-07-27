package jni
import com.funhouse.shared.common.utils.Preview
import com.funhouse.shared.common.generated.resources.Res as CommonR
import com.funhouse.shared.common.generated.resources.underconstruction
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.funhouse.shared.common.generated.resources.backarrow

// --- Constants ---
private const val ROAD_WIDTH_AT_HORIZON = 0.1f // Percentage of screen width (narrower top for longer feel)
private const val ROAD_WIDTH_AT_BOTTOM = 0.8f // Percentage of screen width
private const val ROAD_SEGMENT_LENGTH = 40f // How "long" each segment appears
private const val ROAD_LINE_WIDTH = 0.02f // Percentage of screen width
private const val ROAD_LINE_LENGTH = 0.1f // Percentage of segment length
private const val CAR_WIDTH = 0.1f // Percentage of screen width
private const val CAR_HEIGHT = 0.05f // Percentage of screen height
private const val CAR_Y_POSITION = 0.90f // Percentage of screen height from top

private const val GAME_SPEED = 10f // Base speed of the game
private const val STEERING_SPEED = 0.05f // How fast the car moves left/right
private const val BLINK_INTERVAL_MS = 100L // Milliseconds for shoulder line blinking (faster)

private val POLE_POSITION_BACKGROUND_COLOR = Color.DarkGray
private val POLE_POSITION_TEXT_COLOR = Color.White
private val ROAD_SHOULDER_COLOR = Color.White
private val ROAD_SHOULDER_COLOR_ALT = Color.Gray
private const val BLINK_INTERVAL = 5f // Interval for shoulder line blinking (smaller for faster blinking)

private val SKY_COLOR = Color(0xFF87CEEB)
private val HILLS_COLOR = Color(0xFF228B22)
private val ROAD_STRIPE_COLOR_DARK = POLE_POSITION_BACKGROUND_COLOR
private val ROAD_STRIPE_COLOR_LIGHT = Color.Gray
private val ROAD_LINE_COLOR = Color.White
private val PLAYER_CAR_COLOR = Color.Red
private val CAR_DETAIL_COLOR = Color.Black
private val OPPONENT_CAR_WING_COLOR_MAIN = Color.DarkGray
private val OPPONENT_CAR_WING_COLOR_TOP = Color.Gray
private val OPPONENT_CAR_PALETTE = listOf(Color.Blue, Color.Green, Color.Magenta, Color.Cyan)

private data class OpponentCar(val xPosition: Float, val depth: Float, val color: Color, val widthRatio: Float, val heightRatio: Float)

private enum class PolePositionGameState { Playing, GameOver }

enum class PolePositionDifficulty(val maxCars: Int) { Easy(2), Moderate(5), Hard(7) }

@Composable
fun PolePositionView(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    var currentDifficulty by remember { mutableStateOf(PolePositionDifficulty.Easy) }
    var score by remember { mutableStateOf(" ") }
    Box(modifier.fillMaxSize()) {
        PolePositionGame(
            modifier = Modifier.fillMaxSize(),
            difficulty = currentDifficulty,
            onScoreUpdate = { points ->
                score = " " + getString(R.string.points_label, points) + " "
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                PolePositionDifficulty.values().forEach { level ->
                    val textResId = when (level) {
                        PolePositionDifficulty.Easy -> R.string.easy
                        PolePositionDifficulty.Moderate -> R.string.medium
                        PolePositionDifficulty.Hard -> R.string.hard
                    }
                    Button(onClick = { currentDifficulty = level }) {
                        Text(stringResource(textResId))
                    }
                }
            }
            Text (text = score)
        }

        GcImage(
            imageResource = CommonR.drawable.backarrow,
            modifier = Modifier
                .padding(top = 4.dp, start = 4.dp)
                .size(24.dp)
                .align(Alignment.TopStart)
                .clickable { onBack() }
        )
    }
}

@Composable
fun PolePositionGame(
    modifier: Modifier = Modifier,
    difficulty: PolePositionDifficulty = PolePositionDifficulty.Easy,
    onScoreUpdate: (Long) -> Unit = {}
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(POLE_POSITION_BACKGROUND_COLOR)) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()

        var carXPosition by remember { mutableStateOf(-1f) } // Center of the car
        var roadOffset by remember { mutableStateOf(0f) } // Used to scroll the road
        val speed by remember { mutableStateOf(GAME_SPEED) } // Current game speed
        val opponentCars = remember { mutableStateListOf<OpponentCar>() }
        var gameState by remember { mutableStateOf(PolePositionGameState.Playing) }
        var blinkPhase by remember { mutableStateOf(0) } // 0 or 1 for alternating colors
        var lastBlinkTime by remember { mutableStateOf(0L) } // Last time the blink phase changed
        var startTime by remember { mutableStateOf(System.currentTimeMillis())}

        LaunchedEffect(canvasWidth) {
            if (carXPosition == -1f && canvasWidth > 0f) {
                carXPosition = canvasWidth / 2f
            }
        }

        fun restartGame() {
            carXPosition = canvasWidth / 2f
            roadOffset = 0f
            opponentCars.clear()
            gameState = PolePositionGameState.Playing
            blinkPhase = 0 // Reset blink phase
            lastBlinkTime = 0L // Reset blink timer
            startTime = System.currentTimeMillis()
        }

        // Game Loop
        LaunchedEffect(gameState, canvasWidth, canvasHeight) {
            if (gameState != PolePositionGameState.Playing) return@LaunchedEffect
            if (canvasWidth <= 0f || canvasHeight <= 0f) return@LaunchedEffect

            while (isActive) {
                withFrameNanos { frameTimeNanos ->
                    if (canvasWidth <= 0f || canvasHeight <= 0f) return@withFrameNanos
                    roadOffset = (roadOffset + speed) % ROAD_SEGMENT_LENGTH // Scroll road, loop back

                    // Update blink phase for shoulder lines
                    val currentTimeMs = frameTimeNanos / 1_000_000L

                    // Initialize lastBlinkTime on the first frame of playing
                    if (lastBlinkTime == 0L && gameState == PolePositionGameState.Playing) {
                        lastBlinkTime = currentTimeMs
                    }

                    // Only update blink phase if game is playing and enough time has passed
                    if (gameState == PolePositionGameState.Playing && currentTimeMs >= lastBlinkTime + BLINK_INTERVAL_MS) {
                        onScoreUpdate ((System.currentTimeMillis() - startTime) / 100)
                        blinkPhase = 1 - blinkPhase // Flip between 0 and 1
                        // Advance lastBlinkTime by multiples of BLINK_INTERVAL_MS
                        // to catch up if frames were dropped
                        lastBlinkTime += ((currentTimeMs - lastBlinkTime) / BLINK_INTERVAL_MS).toLong() * BLINK_INTERVAL_MS
                    }

                    // Move opponent cars
                    val carsToRemove = mutableListOf<OpponentCar>()
                    val carsToKeep = mutableListOf<OpponentCar>()

                    opponentCars.forEach { car ->
                        val newDepth = car.depth + speed * 0.8f
                        if (newDepth > canvasHeight + 50f) { // Car passed player (off bottom of screen)
                            carsToRemove.add(car)
                        } else {
                            carsToKeep.add(car.copy(depth = newDepth))
                        }
                    }
                    opponentCars.clear()
                    opponentCars.addAll(carsToKeep)

                    // Spawn new opponent cars randomly
                    if (opponentCars.size < difficulty.maxCars && Random.nextFloat() < 0.05f) { // Max cars based on difficulty
                        val randomX = (0.1f + Random.nextFloat() * 0.8f).toFloat() * canvasWidth // Random X within road
                        val randomDepth = (canvasHeight * 0.3f) + Random.nextFloat() * 100f // Start near horizon
                        val randomColor = OPPONENT_CAR_PALETTE.random()
                        opponentCars.add(OpponentCar(randomX, randomDepth, randomColor, 0.08f, 0.04f))
                    }

                    // --- Collision Detection ---
                    val playerCarLeft = carXPosition - (canvasWidth * CAR_WIDTH / 2)
                    val playerCarRight = carXPosition + (canvasWidth * CAR_WIDTH / 2)
                    val playerCarTop = canvasHeight * CAR_Y_POSITION
                    val playerCarBottom = playerCarTop + (canvasHeight * CAR_HEIGHT)

                    opponentCars.forEach { opponentCar ->
                        val horizonY = canvasHeight * 0.3f
                        val roadBottomY = canvasHeight

                        val scale = (opponentCar.depth - horizonY) / (roadBottomY - horizonY)
                        if (scale < 0 || scale > 1) return@forEach // Car is off-screen vertically

                        val opponentCarScreenY = horizonY + (roadBottomY - horizonY) * scale
                        val currentOpponentCarWidth = canvasWidth * opponentCar.widthRatio * scale
                        val currentOpponentCarHeight = canvasHeight * opponentCar.heightRatio * scale

                        val roadWidthAtCarY = canvasWidth * (ROAD_WIDTH_AT_HORIZON + (ROAD_WIDTH_AT_BOTTOM - ROAD_WIDTH_AT_HORIZON) * scale)
                        val x_left_road = (canvasWidth - roadWidthAtCarY) / 2

                        val opponentCarScreenX = x_left_road + roadWidthAtCarY * (opponentCar.xPosition / canvasWidth)

                        val opponentCarLeft = opponentCarScreenX - currentOpponentCarWidth / 2
                        val opponentCarRight = opponentCarScreenX + currentOpponentCarWidth / 2
                        val opponentCarTop = opponentCarScreenY - currentOpponentCarHeight / 2
                        val opponentCarBottom = opponentCarScreenY + currentOpponentCarHeight / 2

                        // Check for overlap
                        val overlapX = playerCarLeft < opponentCarRight && playerCarRight > opponentCarLeft
                        val overlapY = playerCarTop < opponentCarBottom && playerCarBottom > opponentCarTop

                        if (overlapX && overlapY) {
                            gameState = PolePositionGameState.GameOver
                            return@withFrameNanos // Stop processing this frame
                        }
                    }
                }
            }
        }

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(gameState) {
                if (gameState == PolePositionGameState.Playing) {
                    detectTapGestures(
                        onPress = { offset ->
                            if (offset.x < canvasWidth / 2) {
                                // Move car left
                                carXPosition = max(carXPosition - STEERING_SPEED * canvasWidth, canvasWidth * CAR_WIDTH / 2)
                            } else {
                                // Move car right
                                carXPosition = min(carXPosition + STEERING_SPEED * canvasWidth, canvasWidth * (1 - CAR_WIDTH / 2))
                            }
                        }
                    )
                }
            }
        ) {
            drawSky(canvasWidth, canvasHeight)
            // Draw green ground behind the road
            drawRect(
        color = HILLS_COLOR, // Green for distant hills/trees
                topLeft = Offset(0f, canvasHeight * 0.3f), // Starts at horizon
                size = Size(canvasWidth, canvasHeight * 0.7f) // Extends to bottom
            )
            drawRoad(canvasWidth, canvasHeight, roadOffset, carXPosition, blinkPhase)
            opponentCars.forEach { car ->
                drawOpponentCar(canvasWidth, canvasHeight, car)
            }
            drawCar(canvasWidth, canvasHeight, carXPosition)
        }

        if (gameState == PolePositionGameState.GameOver) {
            Box(
                modifier = Modifier.fillMaxSize().background(POLE_POSITION_BACKGROUND_COLOR.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.game_over_shout), color = POLE_POSITION_TEXT_COLOR, fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { restartGame() }) {
                        Text(stringResource(R.string.restart))
                    }
                }
            }
        }
        GcImage(
            imageResource = CommonR.drawable.underconstruction,
            modifier = Modifier
                .padding(bottom = 4.dp, end = 4.dp)
                .size(64.dp)
                .align(Alignment.BottomEnd)
        )
    }
}

private fun DrawScope.drawSky(width: Float, height: Float) {
    val horizonY = height * 0.3f // New horizon
    drawRect(
        color = SKY_COLOR, // Sky blue
        topLeft = Offset(0f, 0f),
        size = Size(width, horizonY) // Sky takes top portion
    )
}

private fun DrawScope.drawRoad(width: Float, height: Float, offset: Float, carX: Float, blinkPhase: Int) {
    val horizonY = height * 0.3f
    val roadBottomY = height

    var currentY = roadBottomY // Start from the bottom of the screen
    var currentDepth = 0f // Depth in game world, relative to bottom

    while (currentY > horizonY) {
        // Calculate y1 and y2 for the current segment
        val y2 = currentY
        val y1 = max(horizonY, currentY - ROAD_SEGMENT_LENGTH) // Ensure y1 doesn't go above horizon

        // Calculate scale based on y1 and y2
        val scale1 = (y1 - horizonY) / (roadBottomY - horizonY)
        val scale2 = (y2 - horizonY) / (roadBottomY - horizonY)

        val roadWidth1 = width * (ROAD_WIDTH_AT_HORIZON + (ROAD_WIDTH_AT_BOTTOM - ROAD_WIDTH_AT_HORIZON) * scale1)
        val roadWidth2 = width * (ROAD_WIDTH_AT_HORIZON + (ROAD_WIDTH_AT_BOTTOM - ROAD_WIDTH_AT_HORIZON) * scale2)

        val x1_left = (width - roadWidth1) / 2
        val x1_right = (width + roadWidth1) / 2
        val x2_left = (width - roadWidth2) / 2
        val x2_right = (width + roadWidth2) / 2

        // Draw road segment (trapezoid) - Alternating colors for stripes
        val segmentIndex = ((currentDepth + offset) / ROAD_SEGMENT_LENGTH).toInt()
        drawPath(Path().apply {
            moveTo(x1_left, y1)
            lineTo(x1_right, y1)
            lineTo(x2_right, y2)
            lineTo(x2_left, y2)
            close()
        }, color = if (segmentIndex % 2 == 0) ROAD_STRIPE_COLOR_DARK else ROAD_STRIPE_COLOR_LIGHT)

        // Draw road lines (center and sides)
        if (segmentIndex % 2 == 0) { // Draw center line on alternating segments
            drawPath(Path().apply {
                // Center line
                val centerLineX1_left = x1_left + roadWidth1 * (0.5f - ROAD_LINE_WIDTH / 2)
                val centerLineX1_right = x1_left + roadWidth1 * (0.5f + ROAD_LINE_WIDTH / 2)
                val centerLineX2_left = x2_left + roadWidth2 * (0.5f - ROAD_LINE_WIDTH / 2)
                val centerLineX2_right = x2_left + roadWidth2 * (0.5f + ROAD_LINE_WIDTH / 2)

                moveTo(centerLineX1_left, y1)
                lineTo(centerLineX1_right, y1)
                lineTo(centerLineX2_right, y2)
                lineTo(centerLineX2_left, y2)
                close()
            }, color = ROAD_LINE_COLOR)
        }

        // Left Side Line (Shoulder) - Dashed and Alternating Color
        if (segmentIndex % 2 == 0) { // Draw on alternating segments
            val sideLineX1_left = x1_left + roadWidth1 * (0.0f) // Start at road edge
            val sideLineX1_right = x1_left + roadWidth1 * (ROAD_LINE_WIDTH) // Width of side line
            val sideLineX2_left = x2_left + roadWidth2 * (0.0f)
            val sideLineX2_right = x2_left + roadWidth2 * (ROAD_LINE_WIDTH)

            // Color alternates based on screen Y position and offset
            val colorAlternator = ((currentY + offset) / BLINK_INTERVAL).toInt() % 2
            drawPath(Path().apply {
                moveTo(sideLineX1_left, y1)
                lineTo(sideLineX1_right, y1)
                lineTo(sideLineX2_right, y2)
                lineTo(sideLineX2_left, y2)
                close()
            }, color = if (blinkPhase == 0) ROAD_SHOULDER_COLOR else ROAD_SHOULDER_COLOR_ALT) // Alternating colors
        }

        // Right Side Line (Shoulder) - Dashed and Alternating Color
        if (segmentIndex % 2 == 0) { // Draw on alternating segments
            val sideLineX1_left_r = x1_right - roadWidth1 * (ROAD_LINE_WIDTH)
            val sideLineX1_right_r = x1_right - roadWidth1 * (0.0f)
            val sideLineX2_left_r = x2_right - roadWidth2 * (ROAD_LINE_WIDTH)
            val sideLineX2_right_r = x2_right - roadWidth2 * (0.0f)

            drawPath(Path().apply {
                moveTo(sideLineX1_left_r, y1)
                lineTo(sideLineX1_right_r, y1)
                lineTo(sideLineX2_right_r, y2)
                lineTo(sideLineX2_left_r, y2)
                close()
            }, color = if (blinkPhase == 0) ROAD_SHOULDER_COLOR else ROAD_SHOULDER_COLOR_ALT) // Alternating colors
        }

        currentY -= ROAD_SEGMENT_LENGTH // Move up for the next segment
        currentDepth += ROAD_SEGMENT_LENGTH
    }
}

private fun DrawScope.drawCar(width: Float, height: Float, carX: Float) {
    val carWidth = width * CAR_WIDTH
    val carHeight = height * CAR_HEIGHT
    val carY = height * CAR_Y_POSITION

    drawFormulaOneCar(
        color = PLAYER_CAR_COLOR,
        topLeft = Offset(carX - carWidth / 2, carY),
        size = Size(carWidth, carHeight)
    )
}

private fun DrawScope.drawOpponentCar(width: Float, height: Float, car: OpponentCar) {
    val horizonY = height * 0.3f
    val roadBottomY = height

    // Map car depth to screen Y position
    val scale = (car.depth - horizonY) / (roadBottomY - horizonY)
    if (scale < 0 || scale > 1) return // Car is off-screen vertically

    val carScreenY = horizonY + (roadBottomY - horizonY) * scale

    // Scale car width and height based on depth
    val currentCarWidth = width * car.widthRatio * scale
    val currentCarHeight = height * car.heightRatio * scale

    // Map carXPosition (relative to road center) to screen X
    val roadWidthAtCarY = width * (ROAD_WIDTH_AT_HORIZON + (ROAD_WIDTH_AT_BOTTOM - ROAD_WIDTH_AT_HORIZON) * scale)
    val x_left_road = (width - roadWidthAtCarY) / 2

    val carScreenX = x_left_road + roadWidthAtCarY * (car.xPosition / width) // Adjust xPosition to be relative to road

    drawFormulaOneCar(
        color = car.color,
        topLeft = Offset(carScreenX - currentCarWidth / 2, carScreenY - currentCarHeight / 2),
        size = Size(currentCarWidth, currentCarHeight)
    )
}

private fun DrawScope.drawFormulaOneCar(color: Color, topLeft: Offset, size: Size) {
    val carWidth = size.width
    val carHeight = size.height
    val centerX = topLeft.x + carWidth / 2
    val centerY = topLeft.y + carHeight / 2

    // Rear Wheels (draw first so body and wing are on top)
    val rearWheelWidth = carWidth * 0.25f
    val rearWheelHeight = carHeight * 0.3f
    val rearWheelHorizontalOffset = carWidth * 0.35f // Distance from center to wheel's inner edge

    // Left Rear Wheel
    drawRect(
        color = CAR_DETAIL_COLOR,
        topLeft = Offset(centerX - rearWheelHorizontalOffset - rearWheelWidth, topLeft.y + carHeight - rearWheelHeight),
        size = Size(rearWheelWidth, rearWheelHeight)
    )

    // Right Rear Wheel
    drawRect(
        color = CAR_DETAIL_COLOR,
        topLeft = Offset(centerX + rearWheelHorizontalOffset, topLeft.y + carHeight - rearWheelHeight),
        size = Size(rearWheelWidth, rearWheelHeight)
    )

    // Front Wheels
    val frontWheelWidth = carWidth * 0.15f // Thinner
    val frontWheelHeight = carHeight * 0.2f // A bit shorter
    val frontWheelHorizontalOffset = carWidth * 0.2f // Closer to each other
    val frontWheelY = topLeft.y + carHeight * 0.2f // Moved further up (was 0.3f)

    // Left Front Wheel
    drawRect(
        color = CAR_DETAIL_COLOR,
        topLeft = Offset(centerX - frontWheelHorizontalOffset - frontWheelWidth, frontWheelY),
        size = Size(frontWheelWidth, frontWheelHeight)
    )

    // Right Front Wheel
    drawRect(
        color = CAR_DETAIL_COLOR,
        topLeft = Offset(centerX + frontWheelHorizontalOffset, frontWheelY),
        size = Size(frontWheelWidth, frontWheelHeight)
    )


    // Main Body (more pronounced trapezoid)
    val bodyBottomWidth = carWidth * 0.5f
    val bodyTopWidth = carWidth * 0.3f // Tapered more
    val bodyHeight = carHeight * 0.8f
    val bodyPath = Path().apply {
        moveTo(centerX - bodyTopWidth / 2, topLeft.y) // Top-left (front of car)
        lineTo(centerX + bodyTopWidth / 2, topLeft.y) // Top-right
        lineTo(centerX + bodyBottomWidth / 2, topLeft.y + bodyHeight) // Bottom-right
        lineTo(centerX - bodyBottomWidth / 2, topLeft.y + bodyHeight) // Bottom-left
        close()
    }
    drawPath(bodyPath, color = color)

    // Rear Wing (prominent) - Adjusted size and color
    val wingWidth = carWidth * 0.9f
    val wingHeight = carHeight * 0.25f // Increased height
    drawRect(
        color = OPPONENT_CAR_WING_COLOR_MAIN, // Wing color
        topLeft = Offset(centerX - wingWidth / 2, topLeft.y + carHeight - wingHeight),
        size = Size(wingWidth, wingHeight)
    )

    // Rear Wing (top element - slightly smaller) - Adjusted color
    val topWingWidth = wingWidth * 0.8f
    val topWingHeight = wingHeight * 0.3f
    drawRect(
        color = OPPONENT_CAR_WING_COLOR_TOP, // Changed color for contrast
        topLeft = Offset(centerX - topWingWidth / 2, topLeft.y + carHeight - wingHeight - topWingHeight),
        size = Size(topWingWidth, topWingHeight)
    )

    // Cockpit (small rectangle on top of body)
    val cockpitWidth = carWidth * 0.2f
    val cockpitHeight = carHeight * 0.15f
    drawRect(
        color = CAR_DETAIL_COLOR,
        topLeft = Offset(centerX - cockpitWidth / 2, topLeft.y + carHeight * 0.1f), // Positioned near the front of the body
        size = Size(cockpitWidth, cockpitHeight)
    )
}

@Preview
@Composable
fun PreviewPolePositionView() {
    PolePositionView()
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

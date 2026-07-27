package com.gepetto.tetric.ui
import com.funhouse.shared.common.utils.Preview
import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.isLandscape
import club.gepetto.composeutils.scaleRatio

import com.gepetto.tetric.logic.Direction
import com.gepetto.tetric.ui.theme.BodyColor
import com.gepetto.tetric.ui.theme.ScreenBackground

@Composable
fun GameBody(
    modifier: Modifier = Modifier,
    clickable: Clickable = combinedClickable(),
    screen: @Composable (Float) -> Unit
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val landscape = this.isLandscape()
        val scaleRatio = scaleRatio()

        //Screen
        @Composable fun ScreenContent(modifier: Modifier = Modifier) =
            Box(modifier) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(330.dp * scaleRatio, 400.dp * scaleRatio)
                        .padding(top = 20.dp)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(5.dp)
                        .background(BodyColor)
                )

                Box(
                    Modifier
                        .width(120.dp)
                        .height(45.dp)
                        .align(Alignment.TopCenter)
                        .background(BodyColor)
                ) {
                    Text(
                        stringResource(id = R.string.body_label),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Box(
                    Modifier
                        .align(Alignment.Center)
                        .size(360.dp * scaleRatio, 380.dp* scaleRatio)
                        .padding(start = 50.dp, end = 50.dp, top = 50.dp, bottom = 30.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawScreenBorder(
                            Offset(0f, 0f),
                            Offset(size.width, 0f),
                            Offset(0f, size.height),
                            Offset(size.width, size.height)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .background(ScreenBackground)
                    ) {
                        screen(scaleRatio)
                    }
                }
            }

        val SettingText = @Composable { text: String, modifier: Modifier ->
            Text(
                text, modifier = modifier,
                color = Color.Black.copy(0.9f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        @Composable fun ControlsColumn(modifier: Modifier = Modifier) = Column {
            //Setting Buttons
            Column(modifier) {
                Row {
                    SettingText(stringResource(id = R.string.button_pause), Modifier.weight(1f))
                    SettingText(stringResource(id = R.string.button_reset), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    //PAUSE
                    GameButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 20.dp, end = 20.dp),
                        onClick = { clickable.onPause() },
                        size = SettingButtonSize
                    ) {}

                    //RESET
                    GameButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 20.dp, end = 20.dp),
                        onClick = { clickable.onRestart() },
                        size = SettingButtonSize
                    ) {}

                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            //Game Buttons
            val ButtonText = @Composable { modifier: Modifier, text: String ->
                Text(
                    text, modifier = modifier,
                    color = Color.White.copy(0.9f),
                    fontSize = 18.sp * scaleRatio
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 40.dp, end = 40.dp)
                    .height(160.dp * scaleRatio)
            ) {
                //DIRECTION BTN
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    GameButton(
                        Modifier.align(Alignment.TopCenter),
                        onClick = { clickable.onMove(Direction.Up) },
                        autoInvokeWhenPressed = false,
                        size = DirectionButtonSize * scaleRatio
                    ) {
                        ButtonText(it, stringResource(id = R.string.button_up))
                    }
                    GameButton(
                        Modifier.align(Alignment.CenterStart),
                        onClick = { clickable.onMove(Direction.Left) },
                        autoInvokeWhenPressed = true,
                        size = DirectionButtonSize * scaleRatio
                    ) {
                        ButtonText(it, stringResource(id = R.string.button_left))
                    }
                    GameButton(
                        Modifier.align(Alignment.CenterEnd),
                        onClick = { clickable.onMove(Direction.Right) },
                        autoInvokeWhenPressed = true,
                        size = DirectionButtonSize * scaleRatio
                    ) {
                        ButtonText(it, stringResource(id = R.string.button_right))
                    }
                    GameButton(
                        Modifier.align(Alignment.BottomCenter),
                        onClick = { clickable.onMove(Direction.Down) },
                        autoInvokeWhenPressed = true,
                        size = DirectionButtonSize * scaleRatio
                    ) {
                        ButtonText(it, stringResource(id = R.string.button_down))
                    }

                }

                //ROTATE BTN
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    GameButton(
                        Modifier.align(Alignment.CenterEnd),
                        onClick = { clickable.onRotate() },
                        autoInvokeWhenPressed = false,
                        size = RotateButtonSize * scaleRatio
                    ) {
                        ButtonText(it, stringResource(id = R.string.button_rotate))
                    }
                }
            }
        }

        if (landscape)
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ScreenContent()
                ControlsColumn()
            }
        else
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScreenContent()
                ControlsColumn()
            }
    }
}

fun DrawScope.drawScreenBorder(
    topLef: Offset,
    topRight: Offset,
    bottomLeft: Offset,
    bottomRight: Offset
) {
    var path = Path().apply {
        moveTo(topLef.x, topLef.y)
        lineTo(topRight.x, topRight.y)
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            topLef.y + topRight.x / 2 + topLef.x / 2
        )
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            bottomLeft.y - topRight.x / 2 + topLef.x / 2
        )
        lineTo(bottomLeft.x, bottomLeft.y)
        close()
    }
    drawPath(path, Color.Black.copy(0.5f))

    path = Path().apply {
        moveTo(bottomRight.x, bottomRight.y)
        lineTo(bottomLeft.x, bottomLeft.y)
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            bottomLeft.y - topRight.x / 2 + topLef.x / 2
        )
        lineTo(
            topRight.x / 2 + topLef.x / 2,
            topLef.y + topRight.x / 2 + topLef.x / 2
        )
        lineTo(topRight.x, topRight.y)
        close()
    }

    drawPath(path, Color.White.copy(0.5f))
}

data class Clickable constructor(
    val onMove: (Direction) -> Unit,
    val onRotate: () -> Unit,
    val onRestart: () -> Unit,
    val onPause: () -> Unit,
    val onMute: () -> Unit
)

fun combinedClickable(
    onMove: (Direction) -> Unit = {},
    onRotate: () -> Unit = {},
    onRestart: () -> Unit = {},
    onPause: () -> Unit = {},
    onMute: () -> Unit = {}
) = Clickable(onMove, onRotate, onRestart, onPause, onMute)

private val DirectionButtonSize = 60.dp
private val RotateButtonSize = 90.dp
private val SettingButtonSize = 32.dp

@Preview
@Composable
fun PreviewGameBody() {
    Box(Modifier.fillMaxSize().background(Color.White)) {
        GameBody {}
    }
}

@Preview
@Composable
private fun PreviewLandscape() {
    Box(Modifier.fillMaxSize().background(Color.White)) {
        GameBody {}
    }
}

@Preview
@Composable
private fun TabletPreview() {
    Box(Modifier.fillMaxSize().background(Color.White)) {
        GameBody {}
    }
}

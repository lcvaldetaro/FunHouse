package com.gepetto.tetric.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gepetto.tetric.ui.theme.Purple200
import com.gepetto.tetric.ui.theme.Purple500
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ObsoleteCoroutinesApi::class)
@Composable
fun GameButton(
    modifier: Modifier = Modifier,
    size: Dp,
    onClick: () -> Unit = {},
    autoInvokeWhenPressed: Boolean = false,
    content: @Composable (Modifier) -> Unit = {}
) {
    val backgroundShape = RoundedCornerShape(size / 2)
    lateinit var tickerChannel: ReceiveChannel<Unit>

    val coroutineScope = rememberCoroutineScope()
    val pressedInteraction = remember { mutableStateOf<PressInteraction.Press?>(null) }
    val interactionSource = MutableInteractionSource()

    tickerChannel = ticker(initialDelayMillis = 300, delayMillis = 60)
    Box(
        modifier = modifier
            .shadow(5.dp, shape = backgroundShape)
            .size(size = size)
            .clip(backgroundShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Purple200,
                        Purple500
                    ),
                    startY = 0f,
                    endY = 80f
                )
            )// TODO // .indication(interactionSource = interactionSource, indication = rememberRipple())
            .run {
                if (autoInvokeWhenPressed) {
                    pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                val press = PressInteraction.Press(offset)
                                coroutineScope.launch {
                                    pressedInteraction.value?.let { oldValue ->
                                        val interaction = PressInteraction.Cancel(oldValue)
                                        interactionSource.emit(interaction)
                                        pressedInteraction.value = null
                                    }
                                    interactionSource.emit(press)
                                    pressedInteraction.value = press
                                }

                                tickerChannel = ticker(initialDelayMillis = 300, delayMillis = 60)
                                val collectJob = coroutineScope.launch {
                                    tickerChannel
                                        .receiveAsFlow()
                                        .collect { onClick() }
                                }
                                
                                val released = try {
                                    awaitRelease()
                                    true
                                } catch (c: Exception) {
                                    false
                                }
                                
                                tickerChannel.cancel()
                                collectJob.cancel()
                                
                                coroutineScope.launch {
                                    pressedInteraction.value?.let {
                                        val interaction = PressInteraction.Cancel(it)
                                        interactionSource.emit(interaction)
                                        pressedInteraction.value = null
                                    }
                                }
                                
                                if (released) {
                                    onClick()
                                }
                            }
                        )
                    }
                } else {
                    clickable { onClick() }
                }
            }

    ) {
        content(Modifier.align(Alignment.Center))
    }
}

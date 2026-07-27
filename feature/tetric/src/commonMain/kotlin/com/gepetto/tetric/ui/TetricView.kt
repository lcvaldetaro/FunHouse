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




import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import org.jetbrains.compose.resources.painterResource

import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.isLandscape
import club.gepetto.composeutils.scaleRatio
import com.funhouse.shared.common.SettingsBanner
import com.gepetto.tetric.logic.Action
import com.gepetto.tetric.logic.Direction
import com.gepetto.tetric.logic.GameViewModel
import com.gepetto.tetric.ui.theme.BodyColor
import jni.Tetric
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import com.funhouse.shared.common.models.currentSettings

@Composable
fun TetricView(
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {}
) {
    var showSettings by remember { mutableStateOf(false) }
    var usingVoice by remember { mutableStateOf(currentSettings.usingVoice) }
    val voiceIcon = if (usingVoice) CommonR.drawable.call_spk_on else CommonR.drawable.speaker_off

    GcTheme {
        val viewModel = viewModel { GameViewModel() }
        val viewState = viewModel.viewState.value

        LaunchedEffect(key1 = Unit) {
            while (isActive) {
                delay(650L - 55 * (viewState.level - 1))
                viewModel.dispatch(Action.GameTick)
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = Unit) {
            val observer = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    viewModel.dispatch(Action.Resume)
                }

                override fun onPause(owner: LifecycleOwner) {
                    viewModel.dispatch(Action.Pause)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        BoxWithConstraints (
            modifier
                .fillMaxSize()
                .background(BodyColor)
                .displayCutoutPadding()
                .systemBarsPadding()
        ) {
            val landscape = isLandscape()
            val scaleRatio = scaleRatio()

            GameBody(
                modifier = Modifier.align(Alignment.Center).padding(
                   start = if (landscape) 24.dp * scaleRatio else 0.dp
                ),
                clickable = combinedClickable(
                    onMove = { direction: Direction ->
                        if (direction == Direction.Up) viewModel.dispatch(Action.Drop)
                        else viewModel.dispatch(Action.Move(direction))
                    },
                    onRotate = {
                        viewModel.dispatch(Action.Rotate)
                    },
                    onRestart = {
                        viewModel.dispatch(Action.Reset)
                    },
                    onPause = {
                        if (viewModel.viewState.value.isRuning) {
                            viewModel.dispatch(Action.Pause)
                        } else {
                            viewModel.dispatch(Action.Resume)
                        }
                    },
                    onMute = {
                        viewModel.dispatch(Action.Mute)
                    }
                )) { scaleRatio -> GameScreen(Modifier.fillMaxSize(), scaleRatio) }

            Icon(
                painter = painterResource(CommonR.drawable.ic_profile),
                contentDescription = "",
                tint = Color.Black,
                modifier = Modifier
                   // .padding(if (landscape) 4.dp else 16.dp)
                    .size(32.dp * scaleRatio)
                    .align(Alignment.BottomStart)
                    .clickable{ showSettings = !showSettings }
            )

            Icon(
                painter = painterResource(voiceIcon),
                contentDescription = "",
                tint = Color.Black,
                modifier = Modifier
                   // .padding(16.dp)
                    .size(32.dp * scaleRatio)
                    .align(Alignment.BottomCenter)
                    .clickable {
                        currentSettings.usingVoice = ! currentSettings.usingVoice
                        usingVoice = currentSettings.usingVoice
                        viewModel.dispatch(Action.Mute)
                    }
            )

            Icon(
                painter = painterResource(CommonR.drawable.backarrow),
                contentDescription = "",
                tint = Color.Black,
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
                    .align(Alignment.TopStart)
                    .clickable {
                        Tetric.saveGame(viewState.score)
                        onExit()
                    }
            )

            if (showSettings)
                SettingsBanner(Tetric.getGameWalletValue(), currentSettings.usingVoice, onExit = { showSettings = false }) { currentSettings.usingVoice = it; usingVoice = it}
        }
    }
}

@Preview
@Composable
private fun PreviewFunc() {
    TetricView()
}

@Preview
@Composable
private fun PreviewLandscape() {
    TetricView()
}

@Preview
@Composable
private fun TabletPreview() {
    TetricView()
}

@Preview
@Composable
private fun TabletPreviewLandscape() {
    TetricView()
}



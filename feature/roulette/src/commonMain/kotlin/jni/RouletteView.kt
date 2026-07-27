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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import club.gepetto.composeutils.GcTheme
import kotlin.random.Random
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.painterResource

import club.gepetto.composeutils.GcPortraitInLandscape

import club.gepetto.composeutils.isLandscape
import club.gepetto.composeutils.scaleRatio
import com.funhouse.shared.common.SettingsBanner
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.haltBicycle
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.initializeMedia
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playBell
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playBicycle
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playChip
import jni.Roulette.Companion.getAllBets
import jni.Roulette.Companion.handleBet
import jni.models.RouletteBet
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog

@Composable
fun RouletteView(
    modifier: Modifier = Modifier,
    winner: Int? = null,
    init: Boolean = true,
    onHelpClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    var targetRotation by remember { mutableStateOf(Random.nextFloat()) }
    var spinning by remember { mutableStateOf(false) }
    var currentWinningNumber by remember { mutableStateOf(winner) }
    var allBets by remember { mutableStateOf(listOf<RouletteBet>())}
    var showSettings by remember { mutableStateOf(false)}
    var usingVoice by remember { mutableStateOf(currentSettings.usingVoice) }

    if (init) initializeMedia(LocalContext.current)

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val landscape = isLandscape()
        val scaleRatio = scaleRatio()

        Icon(
            painter = painterResource(CommonR.drawable.ic_profile),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(alignment = if (landscape) Alignment.BottomEnd else Alignment.BottomStart)
                .clickable{ showSettings = !showSettings }
        )

        Icon(
            painter = painterResource(if (usingVoice) CommonR.drawable.call_spk_on else CommonR.drawable.speaker_off),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(alignment = if (!landscape) Alignment.CenterStart else Alignment.BottomCenter)
                .clickable{ currentSettings.usingVoice = ! currentSettings.usingVoice; usingVoice = currentSettings.usingVoice }
        )

        if (landscape)
            GcPortraitInLandscape (clockwise = false, modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp * scaleRatio, end = 8.dp * scaleRatio)) {
                RouletteTable(bets = allBets, landscape = true, scaleRatio = scaleRatio) { bet ->
                    handleBet(bet)
                    allBets = getAllBets()
                    GcLog.d("bets: ${allBets}")
                }
            }
        else
            RouletteTable(bets = allBets, scaleRatio = scaleRatio, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 8.dp * scaleRatio, bottom = 8.dp * scaleRatio)) { bet->
                if (usingVoice) playChip()

                handleBet(bet)
                allBets = getAllBets()
                GcLog.d("bets: ${allBets}")
            }

        val wheelModifier = Modifier
            .padding(8.dp * scaleRatio)
            .size(200.dp * scaleRatio)
            .align(alignment = if (landscape) Alignment.BottomStart else Alignment.TopStart)
            .clickable {
                spinning = true
                currentWinningNumber = null
                spinning = true
                targetRotation += (Random.nextFloat() * 3600) + 1800
                if (usingVoice) playBicycle()
            }


        PaintWheel(targetRotation = targetRotation, modifier = wheelModifier) { num ->
            currentWinningNumber = num
            spinning = false
            if (usingVoice) {
                haltBicycle()
                playBell()
            }
        }

        val infoBoxModifier = if (landscape)
            Modifier.align(Alignment.TopStart)
        else
            Modifier.align(Alignment.TopEnd)

        if (!spinning) {
            RouletteInfoBox(
                currentWinningNumber,
                modifier = infoBoxModifier,
                scaleRatio = scaleRatio,
                onComplete = { allBets = getAllBets() }
            )
        }

        if (showSettings)
            SettingsBanner(
                modifier = Modifier.align(if (landscape) Alignment.BottomCenter else Alignment.CenterStart),
                gameWinnings = Roulette.getGameWalletValue(),
                usingVoice = usingVoice,
                onExit = { showSettings = false }
            ) { usingVoice = it}

        GcImage(
            imageResource = CommonR.drawable.backarrow,
            onClick = onExit,
            modifier = Modifier
                .padding(4.dp * scaleRatio)
                .size(32.dp )
                .align(Alignment.TopStart)
        )
    }
}

@Preview
@Composable
private fun PreviewFunc() {
    GcTheme {
        Surface {
            RouletteView(winner = 30, init = false)
        }
    }
}

@Preview
@Composable
private fun LandscapePreview() {
    GcTheme {
        Surface {
            RouletteView(winner = 30, init = false)
        }
    }
}

@Preview
@Composable
private fun TabletPreview() {
    GcTheme {
        Surface {
            RouletteView(winner = 30, init = false)
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

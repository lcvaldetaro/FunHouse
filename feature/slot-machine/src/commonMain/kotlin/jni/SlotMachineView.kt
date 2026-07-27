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
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import com.funhouse.shared.common.generated.resources.dejavusans
import com.funhouse.shared.common.generated.resources.steamboat_willie
import com.funhouse.shared.common.generated.resources.android
import com.funhouse.shared.common.generated.resources.numberseven
import com.funhouse.shared.common.generated.resources.slotmachine
import com.funhouse.shared.common.generated.resources.call_mute
import com.funhouse.shared.common.generated.resources.pause
import com.funhouse.shared.common.generated.resources.cherries
import com.funhouse.shared.common.generated.resources.orange
import com.funhouse.shared.common.generated.resources.grapes
import com.funhouse.shared.common.generated.resources.watermelon
import com.funhouse.shared.common.generated.resources.lemon
import com.funhouse.shared.common.generated.resources.bell
import com.funhouse.shared.common.generated.resources.cheese
import com.funhouse.shared.common.generated.resources.star
import com.funhouse.shared.common.generated.resources.banana
import com.funhouse.shared.common.generated.resources.pineapple
import com.funhouse.shared.common.generated.resources.red_apple
import com.funhouse.shared.common.generated.resources.green_apple
import com.funhouse.shared.common.generated.resources.pear
import com.funhouse.shared.common.generated.resources.strawberry
import com.funhouse.shared.common.generated.resources.joker

import com.funhouse.shared.common.R


import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.getString

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.painterResource

import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcTheme

import club.gepetto.composeutils.scaleRatio

import com.funhouse.shared.common.SettingsBanner
import com.funhouse.shared.common.ELIZA_CARD
import com.funhouse.shared.common.FUNHOUSE_CARD
import com.funhouse.shared.common.NUMBER_SEVEN
import com.funhouse.shared.common.ROBOT
import com.funhouse.shared.common.STEAMBOAT_WILLIE_CARD
import com.funhouse.shared.common.CHERRIES
import com.funhouse.shared.common.ORANGE_CARD
import com.funhouse.shared.common.GRAPES
import com.funhouse.shared.common.WATERMELLON
import com.funhouse.shared.common.LEMMON
import com.funhouse.shared.common.BELL_CARD
import com.funhouse.shared.common.CHEESE
import com.funhouse.shared.common.STAR_CARD
import com.funhouse.shared.common.BANANA
import com.funhouse.shared.common.PINEAPPLE
import com.funhouse.shared.common.RED_APPLE
import com.funhouse.shared.common.GREEN_APPLE
import com.funhouse.shared.common.PEAR
import com.funhouse.shared.common.STRAWBERRY
import com.funhouse.shared.common.JOKER_CARD
import com.funhouse.shared.common.TABLE_COLOR_GREEN
import com.funhouse.shared.common.fruitSymbols
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.haltBicycle
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.initializeMedia
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playBell
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playBicycle
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playCoin
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playJackpotBigger
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playJackpotMusic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog

internal val symbols = fruitSymbols

@Composable
fun SlotMachineView(
    modifier: Modifier = Modifier,
    init: Boolean = true,
    onExit: () -> Unit = {}
) {
    var showSettings by remember { mutableStateOf(false) }
    var usingVoice by remember { mutableStateOf(currentSettings.usingVoice) }

    if (init) initializeMedia(LocalContext.current)

    val initialFruits = fruitSymbols.toMutableList()
    val is1 = initialFruits.random(); initialFruits.remove(is1)
    val is2 = initialFruits.random(); initialFruits.remove(is2)
    val is3 = initialFruits.random(); initialFruits.remove(is3)
    var reel1 by remember { mutableStateOf(is1) }
    var reel2 by remember { mutableStateOf(is2) }
    var reel3 by remember { mutableStateOf(is3) }

    var isSpinning by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<SlotWin?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val voiceIcon = if (usingVoice) CommonR.drawable.backarrow else CommonR.drawable.speaker_off

    fun checkResults(jackpot: Boolean) {
        GcLog.d("checkResults")
        if (usingVoice) haltBicycle()

        result = checkSymbols(listOf(reel1, reel2, reel3))
        var betSize = 0
        if (result != null) {
            if (result?.ordinal != null) {
                val payout = result?.payout!!
                if (payout > 0) {
                    if (usingVoice) {
                        playCoin()
                        playBell()
                        if (payout > SlotWin.TWO_OF_A_KIND.payout) {
                            playJackpotMusic()
                            playJackpotBigger()
                        }
                        else {
                            playCoin()
                        }
                    }
                    SlotMachine.tokenBalance += payout
                    SlotMachine.totalTokensWon += payout
                    SlotMachine.foreverTokensWon += payout
                    betSize = payout
                }
                else {
                    if (usingVoice) playCoin()

                    SlotMachine.tokenBalance--
                    SlotMachine.totalTokensLost++
                    SlotMachine.foreverTokensLost++
                    betSize = -1
                }

                if (jackpot) {
                    SlotMachine.totalTokensLost = 0
                    SlotMachine.totalTokensWon = 0
                }
                SlotMachine.saveGame(betSize)
            }
        }
        isSpinning = false
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(Color.Transparent)) {
        val scaleRatio = scaleRatio()

        GcImage(
            imageResource = CommonR.drawable.slotmachine,
            modifier = Modifier.fillMaxSize()
        )

        GcImage(
            imageResource = CommonR.drawable.underconstruction,
            modifier = Modifier
                .padding(4.dp)
                .size(96.dp)
                .align(Alignment.BottomEnd)
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
            painter = painterResource(voiceIcon),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(Alignment.BottomCenter)
                .clickable { currentSettings.usingVoice = ! currentSettings.usingVoice; usingVoice = currentSettings.usingVoice }
        )

        if (showSettings)
            SettingsBanner(SlotMachine.getGameWalletValue(), usingVoice, onExit = { showSettings = false }) { usingVoice = it}

        GcImage(
            imageResource = CommonR.drawable.backarrow,
            onClick = onExit,
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .background(Color.Transparent),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp).background(Color.Black)
            ) {
                Reel(symbol = reel1, scaleRatio = scaleRatio)
                Reel(symbol = reel2, scaleRatio = scaleRatio)
                Reel(symbol = reel3, scaleRatio = scaleRatio)
            }

            if (!isSpinning && result != null) {
                val payout = result?.payout!!
                if (result != SlotWin.NONE)
                    Text (stringResource(R.string.you_got_liras, payout + 1, result?.let { stringResource(it.descriptionResId) } ?: ""), color = Color.Yellow)
                else
                    Text(stringResource(R.string.you_lost), color = Color.Red)
            }
            else
                Text (" ")
        }
        Button(
            modifier = Modifier.align(Alignment.CenterEnd).padding(bottom = 152.dp),
            colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Red),
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    if (usingVoice) playBicycle()

                    coroutineScope.launch {
                        val durations = listOf(1000L, 2000L, 3000L, 5000L, 7000L)
                        val spinDuration = durations.random()
                        val startTime = System.currentTimeMillis()
                        val biggest = 500L
                        val delays = mutableListOf(1L, 250L, biggest)

                        val delay1 = delays.random(); delays.remove(delay1)
                        val delay2 = delays.random(); delays.remove(delay2)
                        val delay3 = delays.first()

                        var jackpot = false
                        var predeterminedReel1 : String? = null
                        var predeterminedReel2 : String? = null
                        var predeterminedReel3 : String? = null

                        var card = checkIfIsTimeForJackpot(SlotMachine.foreverTokensWon, SlotMachine.foreverTokensLost)
                        if (card != null) {
                            predeterminedReel1 = card
                            predeterminedReel2 = card
                            predeterminedReel3 = card
                            jackpot = true
                        }
                        else {
                            card = checkIfIsTimeForJackpot(SlotMachine.totalTokensWon, SlotMachine.totalTokensLost)
                            if (card != null) {
                                predeterminedReel1 = card
                                predeterminedReel2 = card
                                predeterminedReel3 = card
                                jackpot = true
                            }
                        }

                        launch {
                            delay(delay1)
                            val ticks = spinDuration / 50
                            for (i in 0 until ticks) {
                                reel1 = symbols.random()
                                delay(50)
                            }
                            reel1 = predeterminedReel1 ?: symbols.random()
                            if (delay1 == biggest) checkResults(jackpot)
                        }

                        launch {
                            delay(delay2) // Stagger the reels
                            val ticks = spinDuration / 50
                            for (i in 0 until ticks) {
                                reel2 = symbols.random()
                                delay(50)
                            }
                            reel2 = predeterminedReel2 ?: symbols.random()
                            if (delay2 == biggest) checkResults(jackpot)
                        }

                        launch {
                            delay(delay3) // Stagger the reels
                            val ticks = spinDuration / 50
                            for (i in 0 until ticks) {
                                reel3 = symbols.random()
                                delay(50)
                            }
                            reel3 = predeterminedReel3 ?: symbols.random()
                            if (delay3 == biggest) checkResults(jackpot)
                        }
                    }
                }
            },
            enabled = !isSpinning
        ) {
           // Text(text = if (isSpinning) "" else "spin", fontSize = 8.sp)
        }

    }
}

@Composable
private fun Reel(
    symbol: String,
    modifier: Modifier = Modifier,
    scaleRatio: Float = 1f,
) {
    val textStyle = TextStyle(fontSize = 50.sp * scaleRatio, color = Color.Black)
    val height = 40.dp * scaleRatio
    val width = 80.dp * scaleRatio

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(90.dp * scaleRatio)
            .height(160.dp * scaleRatio)
            .background(Color.White)
    ) {
        when (symbol) {
            NUMBER_SEVEN ->
                GcImage(
                    imageResource = CommonR.drawable.numberseven,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            ELIZA_CARD ->
                GcImage(
                    imageResource = CommonR.drawable.eliza,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            FUNHOUSE_CARD ->
                GcImage(
                    imageResource = CommonR.drawable.funhouse,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            STEAMBOAT_WILLIE_CARD ->
                GcImage(
                    imageResource = CommonR.drawable.steamboat_willie,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            ROBOT ->
                Icon(
                    painter = painterResource(CommonR.drawable.android),
                    contentDescription = "",
                    modifier = Modifier.width(width).height(height),
                    tint = Color.Green,
                )
            CHERRIES ->
                GcImage(
                    imageResource = CommonR.drawable.cherries,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            ORANGE_CARD ->
                GcImage(
                    imageResource = CommonR.drawable.orange,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            GRAPES ->
                GcImage(
                    imageResource = CommonR.drawable.grapes,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            WATERMELLON ->
                GcImage(
                    imageResource = CommonR.drawable.watermelon,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            LEMMON ->
                GcImage(
                    imageResource = CommonR.drawable.lemon,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            BELL_CARD ->
                GcImage(
                    imageResource = CommonR.drawable.bell,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            CHEESE ->
                GcImage(
                    imageResource = CommonR.drawable.cheese,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            STAR_CARD ->
                GcImage(
                    imageResource = CommonR.drawable.star,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            BANANA ->
                GcImage(
                    imageResource = CommonR.drawable.banana,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            PINEAPPLE ->
                GcImage(
                    imageResource = CommonR.drawable.pineapple,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            RED_APPLE ->
                GcImage(
                    imageResource = CommonR.drawable.red_apple,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            GREEN_APPLE ->
                GcImage(
                    imageResource = CommonR.drawable.green_apple,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            PEAR ->
                GcImage(
                    imageResource = CommonR.drawable.pear,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            STRAWBERRY ->
                GcImage(
                    imageResource = CommonR.drawable.strawberry,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            JOKER_CARD ->
                GcImage(
                    imageResource = CommonR.drawable.joker,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(width).height(height)
                )
            else ->
                Text(text = symbol, style = textStyle)
        }
    }
}

@Preview
@Composable
private fun PreviewFunc() {
    GcTheme {
        Surface {
            SlotMachineView(Modifier.background(TABLE_COLOR_GREEN), init = false)
        }
    }
}

@Preview
@Composable
private fun TabletPreview() {
    GcTheme {
        Surface {
            SlotMachineView(Modifier.background(TABLE_COLOR_GREEN), init = false)
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

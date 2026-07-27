package jni
import com.funhouse.shared.common.utils.Preview
import com.funhouse.shared.common.generated.resources.Res as CommonR
import com.funhouse.shared.common.generated.resources.backarrow
import com.funhouse.shared.common.generated.resources.ic_profile
import com.funhouse.shared.common.generated.resources.call_spk_on
import com.funhouse.shared.common.generated.resources.speaker_off
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import com.funhouse.shared.common.generated.resources.dejavusans
import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import club.gepetto.composeutils.GcTheme
import club.gepetto.composeutils.scaleRatio
import com.funhouse.shared.common.CARD_BACK
import com.funhouse.shared.common.PlayCard
import com.funhouse.shared.common.TABLE_COLOR_GREEN
import com.funhouse.shared.common.SettingsBanner
import com.funhouse.shared.common.TABLE_COLOR_BLACK
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.initializeMedia
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playBell
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playCoin
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playFlip
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playJackpot
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playJackpotBigger
import com.funhouse.shared.common.jni.BaseNativeGame.Companion.playJackpotMusic
import jni.Poker.Companion.drawCard
import jni.Poker.Companion.getCardNumbers
import jni.Poker.Companion.getScreenCardsWinnings
import jni.Poker.Companion.newDeck
import jni.Poker.Companion.updateWinnings
import com.funhouse.shared.common.models.currentSettings

enum class PokerGameState{ BEGIN, DEALT, SWAP, END }

@Composable
fun PokerView(
    modifier: Modifier = Modifier,
    initialState: PokerGameState = PokerGameState.BEGIN,
    init: Boolean = true,
    onExit: () -> Unit = {}
) {
    val hiddenCard = PlayCard(CARD_BACK, Color.Blue)
    var displayedCards by remember { mutableStateOf(listOf(hiddenCard, hiddenCard, hiddenCard, hiddenCard, hiddenCard))}
    var title by remember { mutableStateOf("") }
    var isDrawing by remember { mutableStateOf(false) }
    var winnings by remember { mutableStateOf<Int?>(null) }
    var gameState by remember { mutableStateOf(initialState) }
    var showSettings by remember { mutableStateOf(false)}
    var gameValue by remember { mutableStateOf(Poker.getGameWalletValue())}
    var usingVoice by remember { mutableStateOf(currentSettings.usingVoice) }

    if (init) initializeMedia(LocalContext.current)

    fun awards(win: Int) {
        if (win > 0) {
            winnings = win
            updateWinnings(0, win)
            if (usingVoice) {
                playCoin()
                playBell()
                if (win > 1) {
                    playJackpotMusic()
                    playJackpotBigger()
                } else
                    playJackpot()
            }
        }
        else {
            updateWinnings(1, 0)
            if (usingVoice)
                playCoin()
        }
        gameValue = Poker.getGameWalletValue()
    }

    fun draw() : List<PlayCard> {
        var newCards = displayedCards
        if (!isDrawing) {
            isDrawing = true
            winnings = null

            val newList = displayedCards.toMutableList()
            var c = 0
            newList.forEachIndexed { index, card ->
                if (card == hiddenCard) {
                    c++
                    newList[index] = drawCard()
                }
            }
            newCards = if (c > 0) {
                newList
            }
            else {
                mutableListOf(
                    drawCard(),
                    drawCard(),
                    drawCard(),
                    drawCard(),
                    drawCard()
                )
            }

            displayedCards = newCards
            isDrawing = false
        }
        return newCards
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(Color.Transparent)) {
        val scaleRatio = scaleRatio()

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

        if (showSettings)
            SettingsBanner(gameValue, usingVoice, onExit = { showSettings = false }) { usingVoice = it}

        GcImage(
            imageResource = CommonR.drawable.backarrow,
            onClick = onExit,
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
        )

        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            displayedCards.forEachIndexed { index, card ->
                CardDisplay(card = card, scaleRatio = scaleRatio) {
                    if (gameState != PokerGameState.BEGIN) {
                        if (usingVoice)
                            playFlip()
                        if (card.symbol == CARD_BACK)
                            isDrawing = false

                        gameState = PokerGameState.SWAP
                        val newList = displayedCards.toMutableList()
                        newList[index] = hiddenCard
                        displayedCards = newList
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp)
        ) {
            Button(
                enabled = gameState == PokerGameState.END || gameState == PokerGameState.BEGIN,
                onClick = {
                    newDeck()
                    draw()
                    gameState = PokerGameState.DEALT
                }
            ) { Text(stringResource(R.string.deal)) }

            Button(
                enabled =  gameState == PokerGameState.SWAP,
                onClick = {
                    val newCards = draw()
                    val win = getScreenCardsWinnings(newCards)
                    awards(win)
                    gameState = PokerGameState.END
                },
            ) {
                Text(stringResource(R.string.show_cards))
            }
            Button(
                enabled =  gameState == PokerGameState.DEALT,
                onClick = {
                    val win = getScreenCardsWinnings(displayedCards)
                    awards(win)
                    gameState = PokerGameState.END
                },
            ) {
                Text(stringResource(R.string.no_cards))
            }
        }

        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 164.dp)) {
            Text(title, Modifier.background(TABLE_COLOR_BLACK), color = Color.Cyan)
            if (gameState == PokerGameState.END) {
                if (winnings != null) {
                    val pWin = checkNumbers(getCardNumbers(displayedCards))
                    title = stringResource(R.string.you_won_poker, pWin.payout, stringResource(pWin.descriptionResId))
                }
                else {
                    title = stringResource(R.string.you_lost_poker)
                }
            }
            else
                title = ""
        }
    }
}
@Composable
fun CardDisplay(
    card: PlayCard,
    modifier: Modifier = Modifier,
    scaleRatio: Float = 1f,
    onClick : () -> Unit
) {
    val dejavuFontFamily = FontFamily(Font(CommonR.font.dejavusans))
    BasicText(
        text = card.symbol,
        style = TextStyle(fontSize = 64.sp * scaleRatio, color = card.color, fontFamily = dejavuFontFamily),
        modifier = modifier.background(Color.White).clickable{ onClick() }
    )
}

@Preview
@Composable
private fun PreviewFunc() {
    GcTheme {
        Surface {
            PokerView(Modifier.background(TABLE_COLOR_GREEN), init = false)
        }
    }
}

@Preview
@Composable
private fun TabletPreview() {
    GcTheme {
        Surface {
            PokerView(Modifier.background(TABLE_COLOR_GREEN), init = false)
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

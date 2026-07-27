package jni
import com.funhouse.shared.common.utils.Preview
import com.funhouse.shared.common.generated.resources.Res as CommonR
import com.funhouse.shared.common.generated.resources.underconstruction
import com.funhouse.shared.common.generated.resources.backarrow
import com.funhouse.shared.common.generated.resources.ic_profile
import com.funhouse.shared.common.generated.resources.call_spk_on
import com.funhouse.shared.common.generated.resources.speaker_off

import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import club.gepetto.composeutils.GcTheme
import jni.Roulette.Companion.clearBets
import jni.Roulette.Companion.getAllBets
import jni.Roulette.Companion.numWins
import jni.Roulette.Companion.numberSpun
import jni.Roulette.Companion.processBets
import jni.Roulette.Companion.saveGameValues

@Composable
fun RouletteInfoBox(
    currentWinningNumber: Int?,
    modifier: Modifier = Modifier,
    scaleRatio: Float = 1f,
    onComplete: () -> Unit = {},
) {
    val fontSize = 18.sp * scaleRatio
    Column(
        modifier = modifier.padding(8.dp * scaleRatio),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier.size(32.dp * scaleRatio))
        Box(contentAlignment = Alignment.Center) {
            Column {
                if (currentWinningNumber != null) {
                    val tokensBet = getAllBets().size
                    val winnerColor = getRouletteColor(currentWinningNumber)
                    val colorString = if (winnerColor == Color.Black) stringResource(R.string.roulette_black) else if (winnerColor == Color.Red) stringResource(R.string.roulette_red) else ""
                    if (currentWinningNumber > 0) {
                        Text(stringResource(R.string.roulette_winning_number, colorString, numberSpun), fontSize = fontSize, color = winnerColor)
                        val total = processBets()
                        if (total > 0) {
                            if (tokensBet == 1) {
                                Text(stringResource(R.string.roulette_won_single, total), fontSize = fontSize, color = Color.White)
                                Roulette.tokenBalance += total
                                saveGameValues()
                            }
                            else {
                                Text(stringResource(R.string.roulette_won_multiple, total, tokensBet - numWins), fontSize = fontSize, color = Color.White)
                                Roulette.tokenBalance = Roulette.tokenBalance - tokensBet + numWins + total
                            }
                            saveGameValues()
                        }
                        else {
                            if (tokensBet > 0) {
                                Text(stringResource(R.string.roulette_lost_multiple, tokensBet), fontSize = fontSize, color = Color.White)
                                Roulette.tokenBalance -= tokensBet
                                saveGameValues()
                            }
                            else
                                Text(stringResource(R.string.roulette_no_bets), fontSize = fontSize, color = Color.White)
                        }
                    } else {
                        Text(stringResource(R.string.roulette_zero), fontSize = fontSize, color = Color.White)
                        Roulette.tokenBalance -= tokensBet
                        saveGameValues()
                    }
                    clearBets()
                    onComplete()
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewFunc() {
    GcTheme {
        Surface {
            RouletteInfoBox(
                currentWinningNumber = 1,
            )
        }
    }
}
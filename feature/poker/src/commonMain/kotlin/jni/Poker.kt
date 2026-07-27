package jni
import com.funhouse.shared.common.jni.BaseKotlinGame

import com.funhouse.shared.common.CARD_JOKER
import com.funhouse.shared.common.PlayCard
import com.funhouse.shared.common.cardDeckSymbols
import androidx.compose.ui.graphics.Color
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.Settings
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog
import java.io.File
import java.util.Random

class Poker(
    library: String,
    settingsParam: Settings,
    gameFolderParam: String,
    about: String,
    callback: TerminalDataCallback?
) : BaseKotlinGame() {

    private val gameFolder: String
    private val about: String

    init {
        settings = settingsParam
        gameFolder = gameFolderParam
        this.about = about

        if (packageFolder.isNotEmpty()) {
            val folder = File(packageFolder, gameFolder)
            folder.mkdir()
        }
        restoreGame()
    }

    companion object {
        var random = Random()
        private val packageFolder = AppData.packageFolder
        private var settings: Settings = Settings()
        private val pokerWinnings = PokerWinnings()
        var cards = cardDeckSymbols.toMutableList()
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "poker"
        var tokenBalance = 0
        val lirasToToken = 1

        fun restoreGame() {
            val stringMap = BaseKotlinGame.getStringMap(key)
            val word = stringMap.split(" ")
            try {
                tokenBalance = word[1].toInt()
            }
            catch (e: Exception) { }
        }

        fun newDeck() {
            cards = cardDeckSymbols.toMutableList()
        }

        fun updateWinnings(numberTokens: Int, winnings: Int) {
            GcLog.d("updateWinnings called. numTokens = $numberTokens, winnings = $winnings")
            var walletValue = BaseKotlinGame.getWalletValue(key)

            if (winnings > 0) {
                pokerWinnings.numberTokensWon += winnings
                walletValue += (winnings * lirasToToken)
            }
            else {
                pokerWinnings.numberTokensLost -= numberTokens
                walletValue -= (numberTokens * lirasToToken)
            }

            tokenBalance = tokenBalance + winnings - numberTokens

            updateStringMap(key, "${walletValue} ${(walletValue / lirasToToken) + tokenBalance}")
            updateWallet(key, "${walletValue}")
        }

        fun drawCard() : PlayCard {
            val i =  random.nextInt(cards.size - 1)
            val card = cards[i]
            if (cards.isEmpty()) return PlayCard(CARD_JOKER, Color.Transparent)
            cards.removeAt(i)
            return card
        }

        fun getCardNumbers(cards: List<PlayCard>) : List<Int> {
            val numbers: MutableList<Int> = mutableListOf()
            cards.forEach{ card -> numbers.add(cardDeckSymbols.indexOf(card)) }

            return numbers
        }

        fun getScreenCardsWinnings(cards: List<PlayCard>) : Int {
            val numbers: MutableList<Int> = mutableListOf()
            cards.forEach{ card -> numbers.add(cardDeckSymbols.indexOf(card)) }

            return getWinnningsFromCardCodes(numbers)
        }

        fun getWinnningsFromCardCodes(numbers: List<Int>) : Int {
            GcLog.d("numbers: ${numbers}")
            if (numbers[0] == -1) return 0
            GcLog.d("Winnings Payout = ${checkNumbers(numbers).payout}")
            return checkNumbers(numbers).payout
        }

        fun getPokerWinnings() : PokerWinnings = pokerWinnings

        fun getGameWalletValue() = BaseKotlinGame.getWalletValue(key)
    }
}


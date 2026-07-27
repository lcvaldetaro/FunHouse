package jni
import com.funhouse.shared.common.jni.BaseKotlinGame

import jni.models.BLACK
import jni.models.BoxUsed
import jni.models.COL1
import jni.models.COL2
import jni.models.COL3
import jni.models.DOZEN1
import jni.models.DOZEN2
import jni.models.DOZEN3
import jni.models.EVEN
import jni.models.ODD
import jni.models.RED
import jni.models.RouletteBet
import jni.models.V19_36
import jni.models.V1_18
import jni.models.redNumbers
import jni.models.column1
import jni.models.column2
import jni.models.column3
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.Settings
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog

class Roulette(
    library: String,
    settingsParam: Settings,
    gameFolderParam: String,
    about: String,
    callback: TerminalDataCallback?
) : BaseKotlinGame()  {
    init {
        gameFolder = gameFolderParam
        settings = settingsParam
    }
    companion object {
        var numberSpun = 0
        var gameFolder : String = ""
        var settings: Settings = Settings()
        var bets: MutableList<RouletteBet> = mutableListOf()
        var totalBets = 0
        var totalWins = 0
        var numWins = 0
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "roulette"
        var tokenBalance = 0
        val lirasToToken = 1

        fun saveGame() {
            val oldValue = BaseKotlinGame.getWalletValue(key)
            val newValue = oldValue + (tokenBalance * lirasToToken)
            updateStringMap(key, "${newValue} ${(newValue / lirasToToken) + tokenBalance}")
            updateWallet(key, "${newValue}")
        }

        fun restoreGame() {
            val stringMap = BaseKotlinGame.getStringMap(key)
            val word = stringMap.split(" ")
            try { tokenBalance = word[1].toInt() } catch (e: Exception) { }
        }

        fun dozen(number: Int = numberSpun) : Int {
            return if (number == 0) -1 else number / 12
        }

        fun line(number: Int = numberSpun) : Int {
            return if (number == 0) -1 else number / 3
        }

        fun odd(number: Int = numberSpun) : Boolean {
            return if (number == 0) false else (number % 2) == 1
        }

        fun even(number: Int = numberSpun) : Boolean {
            return if (number == 0) false else (number % 2) == 0
        }

        fun top(number: Int = numberSpun) : Boolean {
            return number > 19
        }

        fun bottom(number: Int = numberSpun) : Boolean {
            return number > 0 && number < 19
        }

        fun red(number: Int = numberSpun) : Boolean {
            return if (number == 0) false else redNumbers.contains(number)
        }

        fun black(number: Int = numberSpun) : Boolean {
            return if (number == 0) false else !redNumbers.contains(number)
        }

        fun column1(number: Int = numberSpun) : Boolean {
            return if (number == 0) false else column1.contains(number)
        }

        fun column2(number: Int = numberSpun) : Boolean {
            return if (number == 0) false else column2.contains(number)
        }

        fun column3(number: Int = numberSpun) : Boolean {
            return if (number == 0) false else column3.contains(number)
        }

        fun handleBet(bet: RouletteBet) {
            GcLog.d("Received bet ${bet}")
            if (bet.placed)
                bets.add(bet)
            else
                bets.remove(bet.copy(placed = true))
        }

        fun checkBet(bet: RouletteBet) : Int {
            GcLog.d("Checking bet ${bet} for number $numberSpun, ${numberSpun % 2}")
            val result = when (bet.type) {
                BoxUsed.NUMBER ->
                    if (bet.id == numberSpun) 35 else 0
                BoxUsed.HEADER_COLUMN -> {
                    when {
                        bet.id == COL1 && column1() ||
                        bet.id == COL2 && column2() ||
                        bet.id == COL3 && column3() ||
                        bet.id == DOZEN1 && numberSpun >= 1 && numberSpun <= 12 ||
                        bet.id == DOZEN2 && numberSpun >= 13 && numberSpun <= 24 ||
                        bet.id == DOZEN3 && numberSpun >= 25 && numberSpun <= 36 -> 2
                        else -> 0
                    }
                }
                BoxUsed.SIDEBAR -> {
                    when {
                        bet.id == V1_18 && bottom() ||
                        bet.id == V19_36 && top() ||
                        bet.id == ODD && numberSpun % 2 > 0 ||
                        bet.id == EVEN && numberSpun % 2 == 0 ||
                        bet.id == RED && red() ||
                        bet.id == BLACK && black() -> 1
                        else -> 0
                    }
                }

                BoxUsed.ZERO_BAR -> { // TODO
                    if (numberSpun == 0) 35 else 0
                }
            }
            GcLog.d("Result = $result")
            return result
        }
        fun processBets() : Int {
            var total = 0
            numWins = 0
            bets.forEach {
                val win = checkBet(it)
                if (win > 0) numWins++
                total += win
            }
            GcLog.d("Winnings = $total")
            return total
        }
        fun clearBets() {
            bets = mutableListOf()
        }
        fun getAllBets() : List<RouletteBet> {
            return bets
        }
        fun saveBets() {
            totalBets += bets.size
        }
        fun saveNumberOfWinnings(total: Int = processBets()) {
            totalWins += total
        }

        fun saveGameValues() {
            saveGame()
            saveNumberOfWinnings()
        }

        fun getGameWalletValue() = BaseKotlinGame.getWalletValue(key)
    }
}


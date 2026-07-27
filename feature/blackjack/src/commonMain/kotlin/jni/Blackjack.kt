package jni
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import com.funhouse.shared.common.AppData
import blackjack.utils.defaultAbout

import jni.models.BlackjackGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog
import java.io.File
import java.util.Random

import com.funhouse.shared.common.AppData.gameFolder

class Blackjack : BaseKotlinGame() {
    private val about: String = defaultAbout
    private var game: BlackjackGame = BlackjackGame()
    private var waitingForName = false


    override fun start() {
        GcLog.d("start() called")
        if (packageFolder.isNotEmpty()) {
            val folder = File(packageFolder, gameFolder)
            folder.mkdirs()
        }
    }

    override fun start(gameNickName: String) {
        start()
        GcLog.d("start(gameNickName) called")
        restoreGame()
        if (currentSettings.playerNickName.isEmpty()) {
            print(getString(R.string.what_is_your_name))
            waitingForName = true
        }
        else {
            greetings()
            score()
            restartGame()
        }
    }

    fun printTheDesk() {
        print(getString(R.string.dealer_cards, game.dealer.paintedCards()))
        print(getString(R.string.player_cards, game.player.paintedCards()))
    }

    override fun sendCommand(command: String): Int {
        when {
            waitingForName -> {
                currentSettings.playerNickName = command
                currentSettings.save()
                waitingForName = false
                greetings()
                score()
                restartGame()
            }
            command.startsWith("decks ", ignoreCase = true) -> {
                val word = command.split(" ")
                try {
                    val nDecks = word[1].toInt()
                    game.numOfDecks = nDecks
                    print(getString(R.string.using_decks, nDecks))
                } catch(e: Exception) {
                    print(getString(R.string.invalid_bet))
                }
            }
            command.startsWith("bet ", ignoreCase = true) -> {
                val word = command.split(" ")
                try {
                    val bet = word[1].toFloat()
                    //if (bet > game.bet) {
                        game.bet = bet
                        print(getString(R.string.betting_liras, game.bet.toString()))
                    //}
                    //else {
                    //    print("Bet hes to be bigger than the current bet (GP$${game.bet}\n")
                    //}
                } catch(e: Exception) {
                    print(getString(R.string.invalid_bet))
                }
            }
            command.equals("about", ignoreCase = true) ->
                print(about + "\n")
            command.equals("score", ignoreCase = true) ->
                score()
            command.equals("deal", ignoreCase = true) -> {
                if (game.started)
                    game.balance -= game.bet
                restartGame()
            }
            command.equals("stand", ignoreCase = true) -> {
                game.player.stood = true
                print(getString(R.string.you_stood))
                CoroutineScope(Dispatchers.Main.immediate).launch {
                    dealersTurn()
                    checkIfThereIsWinner()
                }
            }
            command.equals("hit", ignoreCase = true) ->{
                if (game.player.end() && !game.player.isBusted()) {
                    print(getString(R.string.already_five_cards))
                }
                else {
                    game.player.draw(game)
                    printTheDesk()
                    CoroutineScope(Dispatchers.Main.immediate).launch {
                        if (!checkIfPlayerIsDone())
                            dealersTurn()
                        checkIfThereIsWinner()
                    }
                }
            }
            else ->
                print(getString(R.string.invalid_command))
        }
        return 0
    }

    fun score() {
        print(getString(R.string.score_info, game.numOfDecks, game.balance.toString(), game.bet.toString(), game.wins, game.losses, game.draws))
    }

    suspend fun dealersTurn() {
        when {
            game.dealer.stood ->
                print(getString(R.string.dealer_stood))
            game.player.stood ->
                while (true) {
                    delay(1000)
                    if (dealerDraws())
                        break
                }
            else ->
                dealerDraws()
        }
    }

    fun dealerDraws(): Boolean {
        if (game.dealer.sum < 17 ||
            game.dealer.sum < game.player.sum && game.dealer.sum >= 17
        ) {
            game.dealer.draw(game)
            print(getString(R.string.dealer_drew, game.dealer.paintedCards()))
            if (checkIfDealerIsDone()) {
                return true
            }
        } else {
            game.dealer.stood = true
            print(getString(R.string.dealer_stood))
            return true
        }
        return false
    }

    fun checkIfThereIsWinner() {
        when {
            game.dealer.isBlackjack() || game.player.isBusted() ||
            game.player.sum < game.dealer.sum && game.player.stood && !game.dealer.isBusted()-> {
                print(getString(R.string.you_lost))
                game.balance -= game.bet
                game.losses++
                game.started = false
                //score()
                saveGame()
            }
            game.player.isBlackjack() || game.dealer.isBusted() ||
            game.player.sum > game.dealer.sum && game.dealer.stood  && !game.player.isBusted()-> {
                print(getString(R.string.you_won))
                game.balance += game.bet
                game.wins++
                game.started = false
                //score()
                saveGame()
            }
            game.player.sum == game.dealer.sum && game.dealer.stood && game.player.stood -> {
                print(getString(R.string.it_is_draw))
                game.draws++
                game.started = false
                //score()
                saveGame()
            }
        }
    }

    fun checkIfDealerIsDone(): Boolean {
        if (game.dealer.isBlackjack()) {
            print(getString(R.string.dealer_blackjack))
            game.dealer.blackjack = true
            return true
        }

        if (game.dealer.end()) {
            if (game.dealer.isBusted()) {
                print(getString(R.string.dealer_busted))
                game.dealer.busted = true
                return true
            }
            // dealer got max of cards
            game.dealer.stood = true
        }
        return false
    }

    fun checkIfPlayerIsDone(): Boolean {
        if (game.player.isBlackjack()) {
            print(getString(R.string.blackjack_shout))
            game.player.blackjack = true
            return true
        }

        if (game.player.end()) {
            if (game.player.isBusted()) {
                print(getString(R.string.you_busted))
                game.player.busted = true
                return true
            }
            // player got max of cards.
            game.player.stood = true
            return false
        }
        return false
    }

    fun newGame(newgame: BlackjackGame = BlackjackGame()) {
        print(getString(R.string.new_game))
        random = Random()
        game = newgame

        game.dealer.draw(game)
        game.player.draw(game)
        game.player.draw(game)
        printTheDesk()
        game.started = true
        checkIfThereIsWinner()
    }

    fun print(string: String) {
        myPrintf(string)
    }

    fun restartGame() {
        newGame(BlackjackGame(
            balance = game.balance,
            wins = game.wins,
            losses = game.losses,
            draws = game.draws,
            bet = game.bet,
            numOfDecks = game.numOfDecks,
        ))
    }

    fun restoreGame() {
        val stringMap = getStringMap(key)
        val word = stringMap.split(" ")
        game = BlackjackGame()
        if (word.size == 6) {
            try {
                game = BlackjackGame(
                    wins = word[0].toInt(),
                    losses = word[1].toInt(),
                    draws = word[2].toInt(),
                    balance = word[3].toFloat(),
                    numOfDecks = word[4].toInt(),
                    bet = word[5].toFloat(),
                )
            }
            catch (e: Exception) { }
        }
    }

    fun saveGame() {
        val stringMap = "${game.wins} ${game.losses} ${game.draws} ${game.balance} ${game.numOfDecks}"
        updateStringMap(key, stringMap)
        val wallet = game.wins * 3 - game.losses + game.balance
        updateWallet(key, wallet.toString())
    }

    companion object {
        private val packageFolder = AppData.packageFolder
        private var readTerminalCallback: TerminalDataCallback? = null
        var random = Random()
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "blackjack"

        /*@JvmStatic
        private fun myPrintf(data: String) {
            writeTerminal(data)
        }*/
    }
}

const val  SPADE   = "\u2660"
const val  CLUB    = "\u2663"
const val  HEART   = "\u2764\uFE0F"
const val  DIAMOND = "\u2666\uFE0F"
val SUIT = listOf(SPADE, CLUB, HEART, DIAMOND)
val NUM = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

package jni.models


import jni.Blackjack.Companion.random
import jni.NUM
import jni.SUIT
import kotlinx.serialization.Serializable
import club.gepetto.GcLog

@Serializable
data class BlackjackPlayer(
    val cardList: MutableList<Int> = mutableListOf(),
    var sum: Int = 0,
    var numAce: Int = 0,
    var stood: Boolean = false,
    var busted: Boolean = false,
    var blackjack: Boolean = false,
) {
    fun cards() : String {
        var retStr = ""
        cardList.forEach { card -> retStr = "$retStr${printedCard(card)} " }
        return retStr
    }

    fun paintedCards() : String {
        var line1 = ""
        var line2 = ""
        var line3 = ""
        var dashes = ""

        cardList.forEach {
            dashes = "$dashes ____  "
            line1 = "$line1${cardLine1(it)} "
            line2 = "$line2${cardLine2(it)} "
            line3 = "$line3${cardLine3(it)} "
        }

        return("$dashes\n$line1\n$line2\n$line3\n")
    }

    fun printedCard(card: Int) : String {
        return "${SUIT[card / 13]} ${NUM[card % 13]} "
    }

    fun cardLine1(card: Int): String {
        if ((card % 13) == 9)
            return "|  ${NUM[card % 13]}|"
        return "|   ${NUM[card % 13]}|"
    }

    fun cardLine2(card: Int): String {
        return "| ${SUIT[card / 13]} |"
    }

    fun cardLine3(card: Int): String {
        if ((card % 13) == 9)
            return "|${NUM[card % 13]}__|"
        return "|${NUM[card % 13]}___|"
    }

    fun draw(game: BlackjackGame) {
        val index = randomCardIndex(game.cardDecks.size)
        val card = game.cardDecks[index]
        game.cardDecks.remove(index)
        cardList.add(card)
        addSum(card)
        GcLog.d("card randomized = $card")
    }

    fun randomCardIndex(numOfCards: Int = 52) : Int {
        val cardIndex = random.nextInt(numOfCards - 1)
        GcLog.d("random card index = $cardIndex")
        return cardIndex
    }

    fun addSum(card: Int) {
        var x = card % 13
        //if the number is 10,J,Q,K, all value = 10
        if (x > 9)
            x = 9
        else
            if (x == 0) { // If the card is Ace, sum add 11
                numAce++
                x = 10
            }
        sum += x+1 // adjust the x because x is 0~51 originally
        if (sum > 21 && numAce > 0){ // if the sum is greater than 21, change Ace to 1 instead of 11.
            sum -= 10
            numAce--
        }
        GcLog.d("Sum is $sum")
    }

    fun end() : Boolean{
        if (sum > 21 || cardList.size == 5)  // If sum is greater than 21 or the number of card is equal to five, player can't add card.
            return true;
        return false;
    }

    fun isBusted() : Boolean{
        if (sum > 21 )
            return true;
        return false;
    }

    fun is21() : Boolean { // Check BlackJack
        if (sum == 21) return true
        return false
    }

    fun isBlackjack() : Boolean { // Check BlackJack
        if (sum == 21 && cardList.size == 2) return true
        return false
    }
}

package jni
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import com.funhouse.shared.common.CARD_ACE
import com.funhouse.shared.common.CARD_JACK
import com.funhouse.shared.common.CARD_TEN
import com.funhouse.shared.common.SUIT_DIAMONDS
import club.gepetto.GcLog


data class PokerWinnings(
    var numberTokensLost: Int = 0,
    var numberTokensWon: Int = 0,
)

internal fun removeSuit(numbers: List<Int>) : List<Int> {
    val noSuitNumbers: MutableList<Int> = mutableListOf()
    numbers.forEach { noSuitNumbers.add(it % 13) }
    return noSuitNumbers
}

internal fun removeNumbers(numbers: List<Int>) : List<Int> {
    val suits: MutableList<Int> = mutableListOf()
    numbers.forEach { suits.add(it / 13) }
    return suits
}

internal fun isRoyalStreetFlush(numbers: List<Int>) : Boolean {
    val suit = removeNumbers(numbers)
//    GcLog.d("rsf suit: ${suit}")
    return isRoyalFlush(numbers) && suit[0] == SUIT_DIAMONDS
}

internal fun isRoyalFlush(numbers: List<Int>) : Boolean {
    val sNumbers = removeSuit(numbers).sorted()
//    GcLog.d("rf sNumbers: ${sNumbers}")
    return isStraightFlush(numbers) && sNumbers[0] == CARD_ACE
}

internal fun isStraightFlush(numbers: List<Int>) : Boolean {
//    GcLog.d("sf numbers: ${numbers}")
    return isFlush(numbers) && isStraight(numbers)
}

internal fun isFourOfAKind(numbers: List<Int>) : Boolean {
    var count = 1
    val sNumbers = removeSuit(numbers).sorted()
//    GcLog.d("4k sNumbers: ${sNumbers}")
    for(c in 1..4)
        if (sNumbers[c] == sNumbers[c - 1]) {
            if (++count == 4) return true
        } else
            count = 1
    return false
}

internal fun isFullHouse(numbers: List<Int>) : Boolean {
    val sNumbers = removeSuit(numbers).sorted()
//    GcLog.d("fh sNumbers: ${sNumbers}")

    if (sNumbers[0] == sNumbers[1] && sNumbers[2] == sNumbers[3] && sNumbers[3] == sNumbers[4])
        return true

    if (sNumbers[0] == sNumbers[1] && sNumbers[1] == sNumbers[2] && sNumbers[3] == sNumbers[4])
        return true

    return false
}

internal fun isFlush(numbers: List<Int>) : Boolean {
    val suits = removeNumbers(numbers)
//    GcLog.d("fl suits: ${suits}")
    for (c in 1..4) if (suits[c] != suits[0]) return false
    return true
}

internal fun isStraight(numbers: List<Int>) : Boolean {
    val hand = removeSuit(numbers).sorted()
//    GcLog.d("s hand: ${hand}")
    if (hand[0] != CARD_ACE) {
        for (c in 1..4) if (hand[c] != hand[c - 1] + 1) return false
    }
    else {
        if (hand[1] != CARD_TEN) return false
        for (c in 2..4) if (hand[c] != hand[c - 1] + 1) return false
    }
    return true
}

internal fun isThreeOfAKind(numbers: List<Int>) : Boolean {
    var count = 1
    val sNumbers = removeSuit(numbers).sorted()
//    GcLog.d("3k sNumbers: ${sNumbers}")
    for(c in 1..4)
        if (sNumbers[c] == sNumbers[c - 1]) {
            if (++count == 3) return true
        } else
            count = 1
    return false
}

internal fun isTwoPairs(numbers: List<Int>) : Boolean {
    val sNumbers = removeSuit(numbers).sorted()
//    GcLog.d("2p sNumbers: ${sNumbers}")

    if (sNumbers[0] == sNumbers[1] && sNumbers[2] == sNumbers[3])
        return true

    if (sNumbers[1] == sNumbers[2] && sNumbers[3] == sNumbers[4])
        return true

    if (sNumbers[0] == sNumbers[1] && sNumbers[3] == sNumbers[4])
        return true

    return false
}

internal fun isJacksOrBetter(numbers: List<Int>) : Boolean {
    var count = 1
    var doubleCard = -1
    val sNumbers = removeSuit(numbers).sorted()
//    GcLog.d("jb sNumbers: ${sNumbers}")

    for(c in 1..4)  if (sNumbers[c] == sNumbers[c - 1]) {
        count++
        doubleCard = sNumbers[c]
    }
    return count == 2 && (doubleCard >= CARD_JACK || doubleCard == CARD_ACE)
}

internal fun checkNumbers(numbers: List<Int>) : PokerWin {
    GcLog.d("Trying to calculate winnings")
    if (isRoyalStreetFlush(numbers)) return PokerWin.ROYAL_STREET_FLUSH
    if (isRoyalFlush(numbers)) return PokerWin.ROYAL_FLUSH
    if (isStraightFlush(numbers)) return PokerWin.STRAIGHT_FLUSH
    if (isFourOfAKind(numbers)) return PokerWin.FOUR_OF_A_KIND
    if (isFullHouse(numbers)) return PokerWin.FULL_HOUSE
    if (isFlush(numbers)) return PokerWin.FLUSH
    if (isStraight(numbers)) return PokerWin.STRAIGHT
    if (isThreeOfAKind(numbers)) return PokerWin.THREE_OF_A_KIND
    if (isTwoPairs(numbers)) return PokerWin.TWO_PAIRS
    if (isJacksOrBetter(numbers)) return PokerWin.JACKS_OR_BETTER
    GcLog.d("None found")
    return PokerWin.NONE
}

internal enum class PokerWin(val descriptionResId: Int, val payout: Int) {
    ROYAL_STREET_FLUSH(R.string.poker_royal_street_flush, 3200), //0
    ROYAL_FLUSH(R.string.poker_royal_flush, 800), //1
    STRAIGHT_FLUSH(R.string.poker_straight_flush, 50), //2
    FOUR_OF_A_KIND(R.string.poker_four_of_a_kind, 25), //3
    FULL_HOUSE(R.string.poker_full_house, 9), //4
    FLUSH(R.string.poker_flush, 6), //5
    STRAIGHT(R.string.poker_straight, 4), //6
    THREE_OF_A_KIND(R.string.poker_three_of_a_kind, 3), //7
    TWO_PAIRS(R.string.poker_two_pairs, 2), //8
    JACKS_OR_BETTER(R.string.poker_jacks_or_better, 1), //9
    NONE(R.string.poker_none, 0), //10
}

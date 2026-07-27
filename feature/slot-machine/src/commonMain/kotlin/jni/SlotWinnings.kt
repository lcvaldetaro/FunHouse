package jni
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import com.funhouse.shared.common.NUMBER_SEVEN
import com.funhouse.shared.common.STAR_CARD
import com.funhouse.shared.common.ELIZA_CARD
import com.funhouse.shared.common.FUNHOUSE_CARD
import com.funhouse.shared.common.JOKER_CARD
import com.funhouse.shared.common.STEAMBOAT_WILLIE_CARD
import club.gepetto.GcLog


internal fun isAllJokers(numbers: List<String>) : Boolean {
    numbers.forEach { if (it != JOKER_CARD) return false }
    return true
}

internal fun isAllWillies(numbers: List<String>) : Boolean {
    numbers.forEach { if (it != STEAMBOAT_WILLIE_CARD) return false }
    return true
}

internal fun isAllElizas(numbers: List<String>) : Boolean {
    numbers.forEach { if (it != ELIZA_CARD) return false }
    return true
}

internal fun isAllFunhouse(numbers: List<String>) : Boolean {
    numbers.forEach { if (it != FUNHOUSE_CARD) return false }
    return true
}

internal fun isAllSevens(numbers: List<String>) : Boolean {
    numbers.forEach { if (it != NUMBER_SEVEN) return false }
    return true
}

internal fun isAllStars(numbers: List<String>) : Boolean {
    numbers.forEach { if (it != STAR_CARD) return false }
    return true
}

internal fun isAllSame(numbers: List<String>) : Boolean {
    return numbers[0] == numbers[1] && numbers[1] == numbers[2]
}

internal fun isAllSameWithTrump(numbers: List<String>) : Boolean {
    val oneTrump = (numbers[0] == numbers[1] && numbers[2] == JOKER_CARD) ||
    (numbers[0] == numbers[2] && numbers[1] == JOKER_CARD) ||
    (numbers[1] == numbers[2] && numbers[0] == JOKER_CARD)
    val twoTrumps = numbers.count { it == JOKER_CARD } == 2
    return oneTrump || twoTrumps
}

internal fun isTwoOfAKind(symbol: List<String>) : Boolean {
    return symbol[0] == symbol[1] ||
            symbol[0] == symbol[2] ||
            symbol[1] == symbol[2]
}

internal fun checkSymbols(symbols: List<String>) : SlotWin {
    if (isAllFunhouse(symbols)) return SlotWin.ALL_FUNHOUSE
    if (isAllElizas(symbols)) return SlotWin.ALL_ELIZAS
    if (isAllWillies(symbols)) return SlotWin.ALL_WILLIES
    if (isAllJokers(symbols)) return SlotWin.ALL_JOKERS
    if (isAllSevens(symbols)) return SlotWin.ALL_SEVENS
    if (isAllStars(symbols)) return SlotWin.ALL_STARS
    if (isAllSame(symbols)) return SlotWin.ALL_SAME
    if (isAllSameWithTrump(symbols)) return SlotWin.ALL_SAME_WITH_JOKER
    if (isTwoOfAKind(symbols)) return SlotWin.TWO_OF_A_KIND

    return SlotWin.NONE
}

internal enum class SlotWin(val descriptionResId: Int, val payout: Int) {
    ALL_FUNHOUSE(R.string.slot_all_funhouse, 4000),
    ALL_JOKERS(R.string.slot_all_jokers, 3200),
    ALL_ELIZAS(R.string.slot_all_elizas, 2000),
    ALL_WILLIES(R.string.slot_all_willies, 1600),
    ALL_SEVENS(R.string.slot_all_sevens, 800),
    ALL_STARS(R.string.slot_all_stars, 50),
    ALL_SAME(R.string.slot_all_same, 25),
    ALL_SAME_WITH_JOKER(R.string.slot_all_same_with_joker, 12),
    TWO_OF_A_KIND(R.string.slot_two_of_a_kind, 2),
    NONE(R.string.slot_none, 0)
}

internal fun checkIfIsTimeForJackpot(
    totalWins: Int,
    totalBets: Int,
) : String? {
    var percent = 0f

    // check all funhouse
    percent =
        if (totalWins == 0 || totalBets == 0)
            0f
        else
            (totalWins + SlotWin.ALL_FUNHOUSE.payout).toFloat() / totalBets.toFloat()
    GcLog.d("funhouse w: $totalWins, b: $totalBets, p: $percent")
    if (percent > 0 && percent < 0.98f)
        return JOKER_CARD

    // check all eliza
    percent =
        if (totalWins == 0 || totalBets == 0)
            0f
        else
            (totalWins + SlotWin.ALL_ELIZAS.payout).toFloat() / totalBets.toFloat()
    GcLog.d("elizas w: $totalWins, b: $totalBets, p: $percent")
    if (percent > 0 && percent < 0.98f)
        return JOKER_CARD

    // check all willies
    percent =
        if (totalWins == 0 || totalBets == 0)
            0f
        else
            (totalWins + SlotWin.ALL_WILLIES.payout).toFloat() / totalBets.toFloat()
    GcLog.d("willies w: $totalWins, b: $totalBets, p: $percent")
    if (percent > 0 && percent < 0.98f)
        return JOKER_CARD

    // check all jokers
    percent =
        if (totalWins == 0 || totalBets == 0)
            0f
        else
            (totalWins + SlotWin.ALL_JOKERS.payout).toFloat() / totalBets.toFloat()
    GcLog.d("jokers w: $totalWins, b: $totalBets, p: $percent")
    if (percent > 0 && percent < 0.98f)
        return JOKER_CARD

    // check 777
    percent =
        if (totalWins == 0 || totalBets == 0)
            0f
        else
            (totalWins + SlotWin.ALL_SEVENS.payout).toFloat() / totalBets.toFloat()
    GcLog.d("sevens w: $totalWins, b: $totalBets, p: $percent")
    if (percent > 0 && percent < 0.98f)
        return NUMBER_SEVEN

    // check all stars
    percent =
        if (totalWins == 0 || totalBets == 0)
            0f
        else
            (totalWins + SlotWin.ALL_STARS.payout).toFloat() / totalBets.toFloat()
    GcLog.d("stars w: $totalWins, b: $totalBets, p: $percent")
    if (percent > 0 && percent < 0.98f)
        return STAR_CARD

    return null
}
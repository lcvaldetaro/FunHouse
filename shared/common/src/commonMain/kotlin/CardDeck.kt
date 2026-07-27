package com.funhouse.shared.common

import androidx.compose.ui.graphics.Color

val cardDeckCodes = listOf(
    // Spades
    "\uD83C\uDCA1", // Ace of Spades
    "\uD83C\uDCA2", // Two of Spades
    "\uD83C\uDCA3", // Three of Spades
    "\uD83C\uDCA4", // Four of Spades
    "\uD83C\uDCA5", // Five of Spades
    "\uD83C\uDCA6", // Six of Spades
    "\uD83C\uDCA7", // Seven of Spades
    "\uD83C\uDCA8", // Eight of Spades
    "\uD83C\uDCA9", // Nine of Spades
    "\uD83C\uDCAA", // Ten of Spades
    "\uD83C\uDCAB", // Jack of Spades
    "\uD83C\uDCAD", // Queen of Spades
    "\uD83C\uDCAE", // King of Spades
    // Clubs
    "\uD83C\uDCD1", // Ace of Clubs
    "\uD83C\uDCD2", // Two of Clubs
    "\uD83C\uDCD3", // Three of Clubs
    "\uD83C\uDCD4", // Four of Clubs
    "\uD83C\uDCD5", // Five of Clubs
    "\uD83C\uDCD6", // Six of Clubs
    "\uD83C\uDCD7", // Seven of Clubs
    "\uD83C\uDCD8", // Eight of Clubs
    "\uD83C\uDCD9", // Nine of Clubs
    "\uD83C\uDCDA", // Ten of Clubs
    "\uD83C\uDCDB", // Jack of Clubs
    "\uD83C\uDCDD", // Queen of Clubs
    "\uD83C\uDCDE", // King of Clubs
    // Hearts
    "\uD83C\uDCB1", // Ace of Hearts
    "\uD83C\uDCB2", // Two of Hearts
    "\uD83C\uDCB3", // Three of Hearts
    "\uD83C\uDCB4", // Four of Hearts
    "\uD83C\uDCB5", // Five of Hearts
    "\uD83C\uDCB6", // Six of Hearts
    "\uD83C\uDCB7", // Seven of Hearts
    "\uD83C\uDCB8", // Eight of Hearts
    "\uD83C\uDCB9", // Nine of Hearts
    "\uD83C\uDCBA", // Ten of Hearts
    "\uD83C\uDCBB", // Jack of Hearts
    "\uD83C\uDCBD", // Queen of Hearts
    "\uD83C\uDCBE", // King of Hearts
    // Diamonds
    "\uD83C\uDCC1", // Ace of Diamonds
    "\uD83C\uDCC2", // Two of Diamonds
    "\uD83C\uDCC3", // Three of Diamonds
    "\uD83C\uDCC4", // Four of Diamonds
    "\uD83C\uDCC5", // Five of Diamonds
    "\uD83C\uDCC6", // Six of Diamonds
    "\uD83C\uDCC7", // Seven of Diamonds
    "\uD83C\uDCC8", // Eight of Diamonds
    "\uD83C\uDCC9", // Nine of Diamonds
    "\uD83C\uDCCA", // Ten of Diamonds
    "\uD83C\uDCCB", // Jack of Diamonds
    "\uD83C\uDCCD", // Queen of Diamonds
    "\uD83C\uDCCE", // King of Diamonds
)

const val CARD_JOKER = "\uD83C\uDCCF"
const val CARD_BACK = "\uD83C\uDCA0"

const val CARD_ACE = 0
const val CARD_TEN = 9
const val CARD_JACK = 10
const val SUIT_SPACEDS = 0
const val SUIT_CLUBS = 1
const val SUIT_HEARTS = 2
const val SUIT_DIAMONDS = 3

/**
 * A data class to represent a playing card, holding its symbol and color.
 */
data class PlayCard(val symbol: String, val color: Color)



/**
 * A complete 52-card deck with symbols and their respective colors.
 */
val cardDeckSymbols = listOf(
    // Spades - Black
    PlayCard("\uD83C\uDCA1", Color.Black), // Ace of Spades
    PlayCard("\uD83C\uDCA2", Color.Black), // Two of Spades
    PlayCard("\uD83C\uDCA3", Color.Black), // Three of Spades
    PlayCard("\uD83C\uDCA4", Color.Black), // Four of Spades
    PlayCard("\uD83C\uDCA5", Color.Black), // Five of Spades
    PlayCard("\uD83C\uDCA6", Color.Black), // Six of Spades
    PlayCard("\uD83C\uDCA7", Color.Black), // Seven of Spades
    PlayCard("\uD83C\uDCA8", Color.Black), // Eight of Spades
    PlayCard("\uD83C\uDCA9", Color.Black), // Nine of Spades
    PlayCard("\uD83C\uDCAA", Color.Black), // Ten of Spades
    PlayCard("\uD83C\uDCAB", Color.Black), // Jack of Spades
    PlayCard("\uD83C\uDCAD", Color.Black), // Queen of Spades
    PlayCard("\uD83C\uDCAE", Color.Black), // King of Spades
    // Clubs - Black
    PlayCard("\uD83C\uDCD1", Color.Black), // Ace of Clubs
    PlayCard("\uD83C\uDCD2", Color.Black), // Two of Clubs
    PlayCard("\uD83C\uDCD3", Color.Black), // Three of Clubs
    PlayCard("\uD83C\uDCD4", Color.Black), // Four of Clubs
    PlayCard("\uD83C\uDCD5", Color.Black), // Five of Clubs
    PlayCard("\uD83C\uDCD6", Color.Black), // Six of Clubs
    PlayCard("\uD83C\uDCD7", Color.Black), // Seven of Clubs
    PlayCard("\uD83C\uDCD8", Color.Black), // Eight of Clubs
    PlayCard("\uD83C\uDCD9", Color.Black), // Nine of Clubs
    PlayCard("\uD83C\uDCDA", Color.Black), // Ten of Clubs
    PlayCard("\uD83C\uDCDB", Color.Black), // Jack of Clubs
    PlayCard("\uD83C\uDCDD", Color.Black), // Queen of Clubs
    PlayCard("\uD83C\uDCDE", Color.Black), // King of Clubs
    // Hearts - Red
    PlayCard("\uD83C\uDCB1", Color.Red), // Ace of Hearts
    PlayCard("\uD83C\uDCB2", Color.Red), // Two of Hearts
    PlayCard("\uD83C\uDCB3", Color.Red), // Three of Hearts
    PlayCard("\uD83C\uDCB4", Color.Red), // Four of Hearts
    PlayCard("\uD83C\uDCB5", Color.Red), // Five of Hearts
    PlayCard("\uD83C\uDCB6", Color.Red), // Six of Hearts
    PlayCard("\uD83C\uDCB7", Color.Red), // Seven of Hearts
    PlayCard("\uD83C\uDCB8", Color.Red), // Eight of Hearts
    PlayCard("\uD83C\uDCB9", Color.Red), // Nine of Hearts
    PlayCard("\uD83C\uDCBA", Color.Red), // Ten of Hearts
    PlayCard("\uD83C\uDCBB", Color.Red), // Jack of Hearts
    PlayCard("\uD83C\uDCBD", Color.Red), // Queen of Hearts
    PlayCard("\uD83C\uDCBE", Color.Red), // King of Hearts
    // Diamonds - Red
    PlayCard("\uD83C\uDCC1", Color.Red), // Ace of Diamonds
    PlayCard("\uD83C\uDCC2", Color.Red), // Two of Diamonds
    PlayCard("\uD83C\uDCC3", Color.Red), // Three of Diamonds
    PlayCard("\uD83C\uDCC4", Color.Red), // Four of Diamonds
    PlayCard("\uD83C\uDCC5", Color.Red), // Five of Diamonds
    PlayCard("\uD83C\uDCC6", Color.Red), // Six of Diamonds
    PlayCard("\uD83C\uDCC7", Color.Red), // Seven of Diamonds
    PlayCard("\uD83C\uDCC8", Color.Red), // Eight of Diamonds
    PlayCard("\uD83C\uDCC9", Color.Red), // Nine of Diamonds
    PlayCard("\uD83C\uDCCA", Color.Red), // Ten of Diamonds
    PlayCard("\uD83C\uDCCB", Color.Red), // Jack of Diamonds
    PlayCard("\uD83C\uDCCD", Color.Red), // Queen of Diamonds
    PlayCard("\uD83C\uDCCE", Color.Red)  // King of Diamonds
)

fun List<Any>.findIndex(card: Any) : Int {
    var i = 0
    while (i < cardDeckSymbols.size) {
        if (cardDeckSymbols[i] == card) return i
        i++
    }
    return -1
}

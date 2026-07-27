package com.funhouse.shared.common

import androidx.compose.ui.graphics.Color

val ECHO_PREFIX = ">You: "

const val GAMES_FOLDER = "funhouse"
val BACKGROUND_TERMINAL_COLOR = Color.White // = 0xFF2084E6
val TEXT_TERMINAL_COLOR = Color.Black
val TABLE_COLOR_GREEN = Color(0xFF35654d)
val TABLE_COLOR_BLACK = Color(0xFF000000)
val WOOD_COLOR = Color(0xFF855E42)

val fruitSymbols = listOf(
    "\uD83C\uDF52", // Cherries
    "\uD83C\uDF4A", // Orange
    "\uD83C\uDF47", // Grapes
    "\uD83C\uDF49", // Watermelon
    "\uD83C\uDF4B", // Lemon
    "\uD83D\uDD14", // Bell
    "\uD83E\uDDC0", // Cheese
    "\u2B50",         // Star
    "S", "F", "E", "7",
    "\uD83C\uDF4C", // Banana
    "\uD83C\uDF4D", // Pineapple
    "\uD83C\uDF4E", // Red Apple
    "\uD83C\uDF4F", // Green Apple
    "\uD83C\uDF50", // Pear
    "\uD83C\uDF53", // Strawberry
    "\uD83C\uDCCF", // Joker Card
    "\uD83E\uDD16"  // Robot
)

const val JOKER_CARD = "\uD83C\uDCCF"
const val NUMBER_SEVEN =  "7"
const val STAR_CARD = "\u2B50"
const val BELL_CARD = "\uD83D\uDD14"
const val BANANA = "\uD83C\uDF4C"
const val WATERMELLON = "\uD83C\uDF49"
const val GRAPES = "\uD83C\uDF47"
const val CHERRIES = "\uD83C\uDF52"
const val RED_APPLE = "\uD83C\uDF4E"
const val PEAR = "\uD83C\uDF50"
const val LEMMON =  "\uD83C\uDF4B"
const val PINEAPPLE = "\uD83C\uDF4D"
const val ORANGE_CARD =  "\uD83C\uDF4A"
const val GREEN_APPLE = "\uD83C\uDF4F"
const val STRAWBERRY = "\uD83C\uDF53"
const val ROBOT = "\uD83E\uDD16"
const val ELIZA_CARD = "E"
const val FUNHOUSE_CARD = "F"
const val STEAMBOAT_WILLIE_CARD = "S"
const val CHEESE = "\uD83E\uDDC0"

const val ADS_REFRESH = 60

enum class GepettoSubscription { NONE, BASIC, PRO }
val currentSubscription = GepettoSubscription.NONE
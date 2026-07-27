package jni.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Standard single-zero roulette wheel layout
internal val rouletteNumbers = listOf(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)
internal val redNumbers = setOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)

internal val column1 = listOf(1,4,7,10,13,16,19,22,25,28,31,34)
internal val column2 = listOf(2,4,8,11,14,16,20,23,26,29,32,35)
internal val column3 = listOf(3,6,9,12,15,18,21,24,27,30,33,36)

internal val SPINNER_COLOR = Color.Yellow //Color(0xFFDAA520)
internal val LINES_COLOR = Color.Yellow
internal val TOKEN_COLOR = Color.White


internal val HEIGHT_CELL = 34.dp
internal val WIDTH_CELL = 36.dp
internal val WIDTH_NUMBER_CELL = 48.dp
internal val HEIGHT_NUMBER_CELL = 34.dp

internal val HEIGHT_CELL_L = 40.dp
internal val WIDTH_CELL_L = 36.dp
internal val WIDTH_NUMBER_CELL_L = 34.dp
internal val HEIGHT_NUMBER_CELL_L = 40.dp

// Vertical bars
internal const val ODD = 1
internal const val EVEN = 2
internal const val RED = 3
internal const val BLACK = 4
internal const val V1_18 = 5
internal const val V19_36 = 6

// Column Headers
internal const val DOZEN1 = 7
internal const val DOZEN2 = 8
internal const val DOZEN3 = 9
internal const val COL1 = 10
internal const val COL2 = 11
internal const val COL3 = 12

// Horizontal Bars
internal const val ONE_ZERO = 13
internal const val TWO_ZEROS = 14
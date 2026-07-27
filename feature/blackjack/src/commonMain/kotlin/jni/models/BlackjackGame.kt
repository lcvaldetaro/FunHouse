package jni.models

import kotlinx.serialization.Serializable

@Serializable
data class BlackjackGame(
    var player: BlackjackPlayer = BlackjackPlayer(),
    var dealer: BlackjackPlayer = BlackjackPlayer(),
    var started: Boolean = false,
    var bet: Float = 10F,
    var balance: Float = 0F,
    var losses:Int = 0,
    var wins:Int = 0,
    var draws:Int = 0,
    var cardDecks: MutableList<Int> = mutableListOf(),
    var numOfDecks: Int = 4,
) {
    init { newDecks() }

    fun newDecks() {
        cardDecks = mutableListOf()
        repeat(numOfDecks) {
            var c = 0; while(c < 52) cardDecks.add(c++)
        }
    }
}
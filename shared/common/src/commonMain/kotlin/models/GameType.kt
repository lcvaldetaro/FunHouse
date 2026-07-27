package com.funhouse.shared.common.models

import kotlinx.serialization.Serializable

@Serializable
enum class GameType {
    CHATBOT, SKILL, LUCK, ARCADE, ADVENTURE, OTHER, MULTIPLAYER
}

val gameTypeMapDefaultToggles = mapOf(
    GameType.CHATBOT to true,
    GameType.SKILL to true,
    GameType.LUCK to true,
    GameType.ARCADE to true,
    GameType.ADVENTURE to true,
    GameType.OTHER to true,
    GameType.MULTIPLAYER to true
)

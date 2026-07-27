package com.funhouse.feature.funhouseenginekotlin.net

import kotlinx.serialization.Serializable

@Serializable
sealed class NetworkMessage {

    @Serializable
    data class JoinRequest(
        val nickname: String = "",
        val gender: String = "",
        val description: String = "",
        val gameNickName: String = "",
        val playerHandle: String = nickname,
        val handleType: String = gender,
        val handleDescription: String = description
    ) : NetworkMessage()

    @Serializable
    data class JoinResponse(
        val success: Boolean,
        val playerIndex: Int,
        val gameTitle: String,
        val gameDescription: String,
        val playerGoal: String,
        val errorMessage: String? = null
    ) : NetworkMessage()

    @Serializable
    data class SubmitCommand(val text: String) : NetworkMessage()

    @Serializable
    data class TerminalUpdate(val text: String) : NetworkMessage()

    @Serializable
    data class BroadcastUpdate(val text: String) : NetworkMessage()

    @Serializable
    data object Ping : NetworkMessage()
}


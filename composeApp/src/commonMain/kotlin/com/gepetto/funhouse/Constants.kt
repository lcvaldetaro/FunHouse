package com.gepetto.funhouse

import androidx.compose.ui.unit.dp

import com.funhouse.shared.common.generated.resources.*

object Constants {
    const val NETWORK_FOLDER = "games"
    const val GAMES_FOLDER = "funhouse"
    const val BASE_URL = "https://gepetto.club/"
    val FOLDER_BASE_URL = "${BASE_URL}${NETWORK_FOLDER}/"
    const val PRIVACY_POLICY_URL = "https://gepetto.club/privacypolicy.html"
    const val GAME_LIST_QUERY_STRING = "gameList.json"
    const val CACHING = true

    val DEFAULT_ICON = 0
    var DEFAULT_IMAGE: org.jetbrains.compose.resources.DrawableResource = Res.drawable.funhouse
    val ICON_SIZE = 24.dp
}

package com.funhouse.shared.common.models

import com.funhouse.shared.common.AppData
import kotlinx.serialization.Serializable

@Serializable
data class LocalGame(
    //val gameInterfaceClass: GameInterface? = null,
    val packageFolder: String = AppData.packageFolder,
    val gameFolder: String = AppData.gameFolder,
    val icon: Int,
    val image: Int,
)

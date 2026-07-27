package com.funhouse.shared.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

import com.funhouse.shared.common.generated.resources.Res
import com.funhouse.shared.common.generated.resources.blackjack
import com.funhouse.shared.common.generated.resources.chess
import com.funhouse.shared.common.generated.resources.horse
import com.funhouse.shared.common.generated.resources.castle
import com.funhouse.shared.common.generated.resources.chimaera
import com.funhouse.shared.common.generated.resources.paddleball
import com.funhouse.shared.common.generated.resources.cave
import com.funhouse.shared.common.generated.resources.craps
import com.funhouse.shared.common.generated.resources.outback
import com.funhouse.shared.common.generated.resources.eliza
import com.funhouse.shared.common.generated.resources.funhouse
import com.funhouse.shared.common.generated.resources.hangman
import com.funhouse.shared.common.generated.resources.mansion
import com.funhouse.shared.common.generated.resources.poker
import com.funhouse.shared.common.generated.resources.roulette
import com.funhouse.shared.common.generated.resources.secretforest
import com.funhouse.shared.common.generated.resources.slotmachine
import com.funhouse.shared.common.generated.resources.spacewars_white
import com.funhouse.shared.common.generated.resources.ic_launcher
import com.funhouse.shared.common.generated.resources.wizardscastle
import com.funhouse.shared.common.generated.resources.island
import com.funhouse.shared.common.generated.resources.aegisquest
import com.funhouse.shared.common.generated.resources.funhouseicon
import com.funhouse.shared.common.generated.resources.wandera3
import com.funhouse.shared.common.generated.resources.wandercastle
import com.funhouse.shared.common.generated.resources.wanderlibrary
import com.funhouse.shared.common.generated.resources.wandertut
import com.funhouse.shared.common.generated.resources.alieninvanders
import com.funhouse.shared.common.generated.resources.pinball
import com.funhouse.shared.common.generated.resources.retrocircuit
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.generated.resources.funhouseinverted

import com.funhouse.shared.common.generated.resources.cardtablecloth
import com.funhouse.shared.common.generated.resources.pokertable

fun getGameIconResource(nickName: String): org.jetbrains.compose.resources.DrawableResource {
    return when (nickName.lowercase().trim()) {
        "blackjack" -> Res.drawable.blackjack
        "chess" -> Res.drawable.horse
        "castle" -> Res.drawable.castle
        "chimaera" -> Res.drawable.chimaera
        "paddleball" -> Res.drawable.paddleball
        "adventure" -> Res.drawable.cave
        "craps" -> Res.drawable.craps
        "dinkum" -> Res.drawable.outback
        "eliza" -> Res.drawable.eliza
        "funhouse" -> if (AppData.darkMode) Res.drawable.funhouseinverted else Res.drawable.funhouse
        "hangman" -> Res.drawable.hangman
        "mansion" -> Res.drawable.mansion
        "poker" -> Res.drawable.poker
        "roulette" -> Res.drawable.roulette
        "secretforest" -> Res.drawable.secretforest
        "spacewars" -> Res.drawable.spacewars_white
        "tetric" -> Res.drawable.ic_launcher
        "wizardscastle", "wizards" -> Res.drawable.wizardscastle
        "island", "islandsingle" -> Res.drawable.island
        "aegis", "aegisquest", "aegisquestsingle" -> Res.drawable.aegisquest
        "gepetto" -> Res.drawable.funhouseicon
        "slotmachine" -> Res.drawable.slotmachine
        "wandera3" -> Res.drawable.wandera3
        "wandercastle" -> Res.drawable.wandercastle
        "wanderlibrary" -> Res.drawable.wanderlibrary
        "wandertut" -> Res.drawable.wandertut
        "aliens" -> Res.drawable.alieninvanders
        "pinball" -> Res.drawable.pinball
        "retrocircuit" -> Res.drawable.retrocircuit
        else -> Res.drawable.funhouse
    }
}

@Composable
fun getGameIconImageBitmap(nickName: String): ImageBitmap {
    return imageResource(getGameIconResource(nickName))
}

@Composable
fun getGameBackgroundPainter(nickName: String): Painter {
    return painterResource(
        when (nickName.lowercase().trim()) {
            "blackjack" -> Res.drawable.cardtablecloth
            "poker" -> Res.drawable.pokertable
            "chess" -> Res.drawable.chess
            "castle" -> Res.drawable.castle
            "adventure" -> Res.drawable.cave
            "dinkum" -> Res.drawable.outback
            "secretforest" -> Res.drawable.secretforest
            "spacewars" -> Res.drawable.spacewars_white
            "tetric" -> Res.drawable.ic_launcher
            "eliza" -> Res.drawable.eliza
            else -> Res.drawable.funhouse
        }
    )
}

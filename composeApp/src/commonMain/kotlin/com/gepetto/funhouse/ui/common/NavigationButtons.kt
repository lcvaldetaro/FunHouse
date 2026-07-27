package com.gepetto.funhouse.ui.common
import com.funhouse.shared.common.generated.resources.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.Color
import club.gepetto.composeutils.navbar.GcNavButton
import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.funhouse.shared.common.models.GameType

class NavigationButtons (
    val currentChoice: String = "",
    val home: Boolean = false,
    val landscape: Boolean = false,
    val onClickAction: (String, FunHouseAction) -> Unit,
    val onCategoryChosen: (String, GameType) -> Unit,
    val profileTint: Color,
    val profileLabel: String,
    val infoLabel: String,
    val chatbotsLabel: String,
    val skillsLabel: String,
    val arcadeLabel: String,
    val chanceLabel: String,
    val adventureLabel: String,
    val adventLabel: String,
    val multiplayerLabel: String,
) {
    var choice = currentChoice

    val buttonList = buildList {
        add(GcNavButton(label = profileLabel, outline = true, navChoice = choice, imageVector = Icons.Default.AccountCircle, tint = profileTint,
            onClick = { choice = profileLabel;  onClickAction(choice, FunHouseAction.HomeClicked) }
        ))
        add(GcNavButton(label = infoLabel, navChoice = choice, resourceResIcon = Res.drawable.document, vector = false,
            onClick = { choice = infoLabel; onClickAction(choice, FunHouseAction.AboutClicked) }
        ))
        add(GcNavButton(label = chatbotsLabel, navChoice = choice, resourceResIcon = Res.drawable.eliza, vector = false,
            onClick = { choice = chatbotsLabel; onCategoryChosen(choice, GameType.OTHER) }
        ))
        add(GcNavButton(label = skillsLabel, navChoice = choice, resourceResIcon = Res.drawable.blackjack, vector = false,
            onClick = { choice = skillsLabel; onCategoryChosen(choice, GameType.SKILL) }
        ))
        add(GcNavButton(label = arcadeLabel, navChoice = choice, resourceResIcon = Res.drawable.arcade, vector = false,
            onClick = { choice = arcadeLabel; onCategoryChosen(choice, GameType.ARCADE) }
        ))
        add(GcNavButton(label = chanceLabel, navChoice = choice, resourceResIcon = Res.drawable.luck, vector = false,
            onClick = { choice = chanceLabel; onCategoryChosen(choice, GameType.LUCK) }
        ))
        add(GcNavButton(
            label = if (landscape) adventureLabel else adventLabel, navChoice = choice, resourceResIcon = Res.drawable.funhouse, vector = false,
            onClick = { choice = if (landscape) adventureLabel else adventLabel; onCategoryChosen(choice, GameType.ADVENTURE) }
        ))
        if (com.funhouse.shared.common.utils.isLocalWebSocketSupported) {
            add(GcNavButton(
                label = multiplayerLabel, navChoice = choice, imageVector = Icons.Default.Groups, tint = profileTint,
                onClick = { choice = multiplayerLabel; onCategoryChosen(choice, GameType.MULTIPLAYER) }
            ))
        }
    }
}
package com.gepetto.funhouse.models

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.gepetto.funhouse.intentprocessors.FunHouseAction
import com.funhouse.shared.common.models.Game

val defaultGameList = GameList(
    gameFiles = null,
    games = listOf(
        com.funhouse.feature.funhouseenginekotlin.utils.defaultIslandGame,
        com.funhouse.feature.funhouseenginekotlin.utils.defaultIslandGameSingle,
        //eliza.utils.defaultGame,
        elizakotlin.utils.elizaGame,
        com.funhouse.feature.funhouseenginekotlin.utils.defaultFunhouseGame,
        com.funhouse.feature.funhouseenginekotlin.utils.defaultFunhouseGameSingle,
        com.funhouse.feature.funhouseenginekotlin.utils.defaultAegisQuestGame,
        com.funhouse.feature.funhouseenginekotlin.utils.defaultAegisQuestGameSingle,
        //colossalcaveadventure.utils.defaultGame,
        colossalcaveadventurekotlin.utils.adventureGame,
        //chimaera.utils.defaultChimaeraGame,
        chimaerakotlin.utils.defaultChimaeraGame,
        //dinkum.utils.defaultDinkumGame,
        dinkumkotlin.utils.defaultDinkumGame,
        spacewarskotlin.utils.defaultGame,
        //misterymansion.utils.defaultMansionGame,
        misterymansionkotlin.utils.defaultMansionGame,
        //castle.utils.defaultGame,
        castlekotlin.utils.defaultCastkeGame,
        // dungeon.utils.defaultGame, // possible IP issues
        //wander.utils.defaultWanderAldebaranGame,
        //wander.utils.defaultWanderLibraryGame,
        //wander.utils.defaultWanderLogicalOperationsGame,
        //wander.utils.defaultWanderCastleGame,
        wanderkotlin.utils.defaultWanderAldebaranGame,
        wanderkotlin.utils.defaultWanderLibraryGame,
        wanderkotlin.utils.defaultWanderLogicalOperationsGame,
        wanderkotlin.utils.defaultWanderCastleGame,
        //hangman.utils.defaultGame,
        hangmankotlin.utils.hangmanGame,
        //secretforest.utils.defaultGame,
        secretforestkotlin.utils.secretForestGame,
        blackjack.utils.defaultGame,
        slotmachine.utils.defaultGame,
        roulette.utils.defaultGame,
        poker.utils.defaultGame,
        classicarcades.utils.paddleBallGame,
        classicarcades.utils.aliensGame,
        classicarcades.utils.pinballGame,
        classicarcades.utils.retroCircuitGame,
        chess.utils.chessGame,
        craps.utils.defaultGame,
        tetric.utils.defaultGame,
        //wizardscastle.utils.defaultGame // not ready
        wizardscastlekotlin.utils.defaultGame
    )
)

@Composable
fun ComposableGameRoot(
    game: Game,
    modifier: Modifier = Modifier,
    onClickAction: (FunHouseAction) -> Unit = {}
) {
    when (game) {
        slotmachine.utils.defaultGame -> slotmachine.utils.GameRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        roulette.utils.defaultGame -> roulette.utils.GameRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        poker.utils.defaultGame -> poker.utils.GameRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        classicarcades.utils.paddleBallGame -> classicarcades.utils.PaddleBallRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        classicarcades.utils.aliensGame -> classicarcades.utils.InvadersRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        classicarcades.utils.pinballGame -> classicarcades.utils.PinballRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        classicarcades.utils.retroCircuitGame -> classicarcades.utils.PoleRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        chess.utils.chessGame -> chess.utils.ChessRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        craps.utils.defaultGame -> craps.utils.GameRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
        tetric.utils.defaultGame -> tetric.utils.GameRoot(modifier) { onClickAction(FunHouseAction.BackClicked) }
    }
}

@Composable
fun ComposableTextGameRoot(
    game: Game,
    modifier: Modifier = Modifier,
    onClickAction: (FunHouseAction) -> Unit = {}
) {
    Text("TODO")
}

// used for testing
@Composable
fun ActivityGameRoot(
    game: Game,
    modifier: Modifier = Modifier,
    onClickAction: (FunHouseAction) -> Unit = {}
) {
}

private fun copyAssetToFilesFolder(fileName: String) {
    com.funhouse.shared.common.utils.installFile(fileName)
}

fun installAssetFiles() {
    // install game files
    try {
        copyAssetToFilesFolder("about_en.md")
        copyAssetToFilesFolder("about_it.md")
        copyAssetToFilesFolder("about_fr.md")
        copyAssetToFilesFolder("about_es.md")
        copyAssetToFilesFolder("about_de.md")
        copyAssetToFilesFolder("about_pt.md")

        copyAssetToFilesFolder("privacy_en.md")
        copyAssetToFilesFolder("privacy_it.md")
        copyAssetToFilesFolder("privacy_fr.md")
        copyAssetToFilesFolder("privacy_es.md")
        copyAssetToFilesFolder("privacy_de.md")
        copyAssetToFilesFolder("privacy_pt.md")

        // networked games list
        //installFile("gameList.json")

        // funhouse
        com.funhouse.feature.funhouseenginekotlin.utils.installFiles()

        // Adventure Classic
        //colossalcaveadventure.utils.installFiles()

        // Adventure Kotlin
        colossalcaveadventurekotlin.utils.installFiles()

        // Space Wars
        spacewarskotlin.utils.installFiles()

        // Chimaera
        //chimaera.utils.installFiles()
        chimaerakotlin.utils.installFiles()

        // Castle
        //castle.utils.installFiles()
        castlekotlin.utils.installFiles()

        // Eliza Classic
        //eliza.utils.installFiles()

        // Eliza Kotlin
        elizakotlin.utils.installFiles()

        // Dungeon
        // dungeon.utils.installFiles()

        // Wander
        //wander.utils.installFiles()
        wanderkotlin.utils.installFiles()

        // Dinkum
        //dinkum.utils.installFiles()
        dinkumkotlin.utils.installFiles()

        // Mistery Mansion
        //misterymansion.utils.installFiles()
        misterymansionkotlin.utils.installFiles()

        // Hangman
        //hangman.utils.installFiles()
        hangmankotlin.utils.installFiles()

        // Secret forest
        //secretforest.utils.installFiles()
        secretforestkotlin.utils.installFiles()

        // Blackjack
        blackjack.utils.installFiles()

        // Slot Machine
        slotmachine.utils.installFiles()

        // Roulette
        roulette.utils.installFiles()

        // Poker
        poker.utils.installFiles()

        // Paddle Ball, Alien Invaders, Pinball
        classicarcades.utils.installFiles()

        // Chess
        chess.utils.installFiles()

        // Craps
        craps.utils.installFiles()

        // Tetric
        tetric.utils.installFiles()

        // WIzards Castke
        //wizardscastle.utils.installFiles()
        wizardscastlekotlin.utils.installFiles()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

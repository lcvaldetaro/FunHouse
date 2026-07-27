package com.funhouse.feature.funhouseenginekotlin.utils

import com.funhouse.shared.common.models.Direction
import jni.GengameKotlin
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.GameGenre
import com.funhouse.shared.common.models.defaultDirections
import com.funhouse.shared.common.models.GameType
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    try {
        // funhouse
        installFile("funhouse.json")
        installFile("funhouse.csv")
        installFile("funhouseplaces.csv")
        installFile("funhouseobjects.csv")
        installFile("funhousegoals.csv")
        installFile("funhouse.md")
        installFile("funhouseicon.png")
        installFile("funhouselicense.txt")

        // island
        installFile("island.json")
        installFile("island.csv")
        installFile("islandgoals.csv")
        installFile("islandobjects.csv")
        installFile("islandplaces.csv")
        installFile("island.md")
        installFile("island.png")
        installFile("islandlicense.txt")

        // aegisquest
        installFile("aegisquest.json")
        installFile("aegisquest.csv")
        installFile("aegisquestgoals.csv")
        installFile("aegisquestobjects.csv")
        installFile("aegisquestplaces.csv")
        installFile("aegisquest.md")
        installFile("aegisquest.png")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getGenreForNickname(nickname: String): GameGenre {
    return when (nickname.lowercase()) {
        "aegisquest", "aegisquestsingle" -> GameGenre.SPACE
        "island", "islandsingle" -> GameGenre.ADVENTURE
        "funhouse", "funhousesingle" -> GameGenre.MISTERY
        else -> GameGenre.OTHER
    }
}

val defaultFunhouseDirections: List<Direction> =
    listOf(
        Direction("N", "north"),
        Direction("S", "south"),
        Direction("W", "west"),
        Direction("E", "east"),
        Direction("NE", "ne"),
        Direction("NW", "nw"),
        Direction("SW", "sw"),
        Direction("SE", "se"),
        Direction("Yes", "yes"),
        Direction("No", "no"),
        Direction("Up", "up"),
        Direction("Down", "down"),
        Direction("In", "in"),
        Direction("Out", "out"),
        Direction("Look", "look"),
        Direction("Inv", "inventory"),
        Direction("Save", "save"),
        Direction("Restore", "restore"),
    )

val defaultFunhouseGame = Game(
    gameName = "funhouse",
    nickName = "funhouse",
    multiPlayer = true,
    gameType = GameType.MULTIPLAYER,
    gameGenre = GameGenre.MISTERY,
    saveFilePrefix = "funhouse",
    menuTitle = "Fun House",
    title = "Fun House Multi Player",
    greeting = "Welcome to the Fun House!",
    description = "Explore a mysterious hall of mirrors, solve puzzling riddles, avoid dangerous traps, and find your way out of this bizarre amusement park before it's too late!",
    version = "2.0",
    about = "Fun House Adventure\nEscaped if you can!\nConverted to Kotlin in 2026.\n",
    startPlaces = listOf(1),
    maxObjects = 5,
    directions = defaultFunhouseDirections,
    directionColumns = 6,
    mainGameFile = DownloadableFile("funhouse.csv"),
    placesFile = DownloadableFile("funhouseplaces.csv"),
    objectsFile = DownloadableFile("funhouseobjects.csv"),
    goalsFile = DownloadableFile("funhousegoals.csv"),
    helpFile = DownloadableFile("funhouse.md"),
    gameImage = DownloadableFile("funhouseicon.png"),
    licenseFile = DownloadableFile("funhouselicense.txt"),
    composableTextGame = true,
    gameClass = GengameKotlin(),
    localGame = null,
)

val defaultFunhouseGameSingle = Game(
    gameName = "funhousesingle",
    nickName = "funhousesingle",
    multiPlayer = false,
    gameGenre = GameGenre.MISTERY,
    saveFilePrefix = "funhousesingle",
    menuTitle = "Fun House",
    title = "Fun House Single Player",
    greeting = "Welcome to the Fun House!",
    description = "Explore a mysterious hall of mirrors, solve puzzling riddles, avoid dangerous traps, and find your way out of this bizarre amusement park before it's too late!",
    version = "2.0",
    about = "Fun House Adventure\nEscaped if you can!\nConverted to Kotlin in 2026.\n",
    startPlaces = listOf(1),
    maxObjects = 5,
    directions = defaultFunhouseDirections,
    directionColumns = 6,
    mainGameFile = DownloadableFile("funhouse.csv"),
    placesFile = DownloadableFile("funhouseplaces.csv"),
    objectsFile = DownloadableFile("funhouseobjects.csv"),
    goalsFile = DownloadableFile("funhousegoals.csv"),
    helpFile = DownloadableFile("funhouse.md"),
    gameImage = DownloadableFile("funhouseicon.png"),
    licenseFile = DownloadableFile("funhouselicense.txt"),
    composableTextGame = true,
    gameClass = GengameKotlin(),
    localGame = null,
)

val defaultIslandGame = Game(
    gameName = "island",
    nickName = "island",
    multiPlayer = true,
    gameType = GameType.MULTIPLAYER,
    gameGenre = GameGenre.ADVENTURE,
    saveFilePrefix = "island",
    title = "Lost Island Multi Player",
    menuTitle = "Island",
    greeting = "Welcome do the Island adventure!",
    description = "Survive a plane crash on an uncharted tropical island. Navigate dense forests, uncover hidden pirate treasures, and find a way to repair and fuel the escape boat on the shore.",
    version = "2.0",
    about = "Lost Island Adventure\nFind the treasure and escape!\nConverted to Kotlin in 2026.\n",
    startPlaces = listOf(1, 6, 14, 16, 19, 20),
    maxObjects = 5,
    directions = defaultFunhouseDirections,
    directionColumns = 6,
    mainGameFile = DownloadableFile("island.csv"),
    placesFile = DownloadableFile("islandplaces.csv"),
    objectsFile = DownloadableFile("islandobjects.csv"), // Fixed typo from native version (islandbjects.csv -> islandobjects.csv)
    goalsFile = DownloadableFile("islandgoals.csv"),
    helpFile = DownloadableFile("island.md"),
    gameImage = DownloadableFile("island.png"),
    licenseFile = DownloadableFile("funhouselicense.txt"),
    composableTextGame = true,
    gameClass = GengameKotlin(),
    localGame = null,
)

val defaultIslandGameSingle = Game(
    gameName = "islandsingle",
    nickName = "islandsingle",
    multiPlayer = false,
    gameGenre = GameGenre.ADVENTURE,
    saveFilePrefix = "islandsingle",
    title = "Lost Island Single Player",
    menuTitle = "Island",
    greeting = "Welcome do the Island adventure!",
    description = "Survive a plane crash on an uncharted tropical island. Navigate dense forests, uncover hidden pirate treasures, and find a way to repair and fuel the escape boat on the shore.",
    version = "2.0",
    about = "Lost Island Adventure\nFind the treasure and escape!\nConverted to Kotlin in 2026.\n",
    startPlaces = listOf(1, 6, 14, 16, 19, 20),
    maxObjects = 5,
    directions = defaultFunhouseDirections,
    directionColumns = 6,
    mainGameFile = DownloadableFile("island.csv"),
    placesFile = DownloadableFile("islandplaces.csv"),
    objectsFile = DownloadableFile("islandobjects.csv"), // Fixed typo from native version (islandbjects.csv -> islandobjects.csv)
    goalsFile = DownloadableFile("islandgoals.csv"),
    helpFile = DownloadableFile("island.md"),
    gameImage = DownloadableFile("island.png"),
    licenseFile = DownloadableFile("funhouselicense.txt"),
    composableTextGame = true,
    gameClass = GengameKotlin(),
    localGame = null,
)

val defaultAegisQuestGame = Game(
    gameName = "aegisquest",
    nickName = "aegisquest",
    multiPlayer = true,
    gameType = GameType.MULTIPLAYER,
    gameGenre = GameGenre.SPACE,
    saveFilePrefix = "aegisquest",
    title = "Space Station Aegis\nMulti Player",
    menuTitle = "Aegis Station",
    greeting = "Welcome to Space Station Aegis!",
    description = "Infiltrate the besieged Aegis Prime Space Station. Choose your faction, navigate dark decks, hack control terminals, and secure critical station modules to win the orbital war.",
    version = "1.0",
    about = "Space Station Aegis Quest\nSecure Aegis Prime Space Station!\nCreated in 2026.\n",
    startPlaces = listOf(1, 16, 31, 46, 61, 76, 86, 96),
    maxObjects = 5,
    directions = defaultFunhouseDirections,
    directionColumns = 6,
    mainGameFile = DownloadableFile("aegisquest.csv"),
    placesFile = DownloadableFile("aegisquestplaces.csv"),
    objectsFile = DownloadableFile("aegisquestobjects.csv"),
    goalsFile = DownloadableFile("aegisquestgoals.csv"),
    helpFile = DownloadableFile("aegisquest.md"),
    gameImage = DownloadableFile("aegisquest.png"),
    licenseFile = DownloadableFile("funhouselicense.txt"),
    composableTextGame = true,
    gameClass = GengameKotlin(),
    localGame = null,
)

val defaultAegisQuestGameSingle = Game(
    gameName = "aegisquestsingle",
    nickName = "aegisquestsingle",
    multiPlayer = false,
    gameGenre = GameGenre.SPACE,
    saveFilePrefix = "aegisquestsingle",
    menuTitle = "Aegis Station",
    title = "Space Station Aegis\nSingle Player",
    greeting = "Welcome to Space Station Aegis!",
    description = "Infiltrate the besieged Aegis Prime Space Station. Choose your faction, navigate dark decks, hack control terminals, and secure critical station modules to win the orbital war.",
    version = "1.0",
    about = "Space Station Aegis Quest\nSecure Aegis Prime Space Station!\nCreated in 2026.\n",
    startPlaces = listOf(1, 16, 31, 46, 61, 76, 86, 96),
    maxObjects = 5,
    directions = defaultFunhouseDirections,
    directionColumns = 6,
    mainGameFile = DownloadableFile("aegisquest.csv"),
    placesFile = DownloadableFile("aegisquestplaces.csv"),
    objectsFile = DownloadableFile("aegisquestobjects.csv"),
    goalsFile = DownloadableFile("aegisquestgoals.csv"),
    helpFile = DownloadableFile("aegisquest.md"),
    gameImage = DownloadableFile("aegisquest.png"),
    licenseFile = DownloadableFile("funhouselicense.txt"),
    composableTextGame = true,
    gameClass = GengameKotlin(),
    localGame = null,
)

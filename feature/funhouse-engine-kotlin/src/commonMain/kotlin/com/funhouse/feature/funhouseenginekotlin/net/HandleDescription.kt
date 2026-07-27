package com.funhouse.feature.funhouseenginekotlin.net

import com.funhouse.shared.common.models.GameGenre
import kotlin.random.Random

enum class HandleDescription(val text: String, val genre: GameGenre) {
    // MISTERY
    PHANTOM("A spooky phantom drifting between rooms", GameGenre.MISTERY),
    BUCCANEER("A swashbuckling buccaneer seeking plunder", GameGenre.MISTERY),
    TRICKSTER("A mischievous trickster hunting for pots of gold", GameGenre.MISTERY),
    ARCANE_MASTER("A master of arcane spells and ancient lore", GameGenre.MISTERY),
    SILENT_SHADOW("A silent shadow moving unseen in the night", GameGenre.MISTERY),
    SCALED_BEAST("A mighty scaled beast guarding ancient treasure", GameGenre.MISTERY),
    HOLY_KNIGHT("A holy knight on a righteous quest", GameGenre.MISTERY),
    NIGHT_CREATURE("A creature of the night craving glory", GameGenre.MISTERY),
    GREMLIN("A mischievous cute creature of the night craving glory", GameGenre.MISTERY),

    // SPACE
    ASTRONAUT_DESC("A space explorer far from home", GameGenre.SPACE),
    CYBER_DESC("A futuristic blend of machine and mortal", GameGenre.SPACE),
    STAR_CAPTAIN_DESC("A seasoned pilot navigating the stars", GameGenre.SPACE),
    ALIEN_OBSERVER("A mysterious extraterrestrial observing human behavior", GameGenre.SPACE),
    DROID_SOLDIER("A combat droid programmed for tactical decisions", GameGenre.SPACE),

    // WAR
    GENERAL_DESC("A battle-hardened commander plotting tactical advances", GameGenre.WAR),
    SNIPER_DESC("A silent sniper watching from the high ridge", GameGenre.WAR),
    MEDIC_DESC("A front-line medic patch-repairing squadmates under fire", GameGenre.WAR),
    COMMANDO_DESC("A stealthy commando trained in sabotage operations", GameGenre.WAR),
    SPY_DESC("An intelligence operative gathering enemy secrets", GameGenre.WAR),

    // ADVENTURE
    EXPLORER_DESC("A daring explorer seeking lost cities", GameGenre.ADVENTURE),
    ARCHEOLOGIST_DESC("A dusty archeologist translating ancient ruins", GameGenre.ADVENTURE),
    HUNTER_DESC("A wild hunter tracking rare and exotic beasts", GameGenre.ADVENTURE),
    SCHOLAR_DESC("A curious scholar researching long-forgotten history", GameGenre.ADVENTURE),
    OUTLAW_DESC("A rugged outlaw running from the local authorities", GameGenre.ADVENTURE),

    // PUZZLE
    CODEBREAKER_DESC("A brilliant mind deciphering security grids", GameGenre.PUZZLE),
    MATH_GENIUS_DESC("A calculating genius solving complex equations", GameGenre.PUZZLE),
    PHILOSOPHER_DESC("A thoughtful philosopher pondering deep paradoxes", GameGenre.PUZZLE),
    INVENTOR_DESC("An inventor crafting intricate clockwork contraptions", GameGenre.PUZZLE),
    CHESSMASTER_DESC("A master of foresight thinking three moves ahead", GameGenre.PUZZLE),

    // STRATEGY
    TACTICIAN_DESC("A cunning strategist calculating grand maneuvers", GameGenre.STRATEGY),
    WARLORD_DESC("A stern leader rallying clans for domination", GameGenre.STRATEGY),
    DIPLOMAT_DESC("A patient negotiator drafting terms of alliance", GameGenre.STRATEGY),
    GOVERNOR_DESC("A meticulous administrator managing station sectors", GameGenre.STRATEGY),
    SPYMASTER_DESC("A shadow coordinator pulling strings behind the scenes", GameGenre.STRATEGY),

    // OTHER
    WANDERER_DESC("A curious wanderer walking through unknown corridors", GameGenre.OTHER),
    GAMER_DESC("A passionate gamer testing the virtual reality matrix", GameGenre.OTHER),
    TOURIST_DESC("A casual tourist looking for souvenir datacards", GameGenre.OTHER),
    OBSERVER_DESC("An objective observer recording historical events", GameGenre.OTHER);

    companion object {
        fun random(genre: GameGenre): HandleDescription {
            val matching = entries.filter { it.genre == genre }
            if (matching.isEmpty()) {
                return PHANTOM
            }
            return matching[Random.nextInt(matching.size)]
        }
    }
}

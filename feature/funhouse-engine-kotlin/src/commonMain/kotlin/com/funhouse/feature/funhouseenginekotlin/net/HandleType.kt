package com.funhouse.feature.funhouseenginekotlin.net

import com.funhouse.shared.common.models.GameGenre
import kotlin.random.Random

enum class HandleType(val displayName: String, val genre: GameGenre) {
    // MISTERY
    GHOST("Ghost", GameGenre.MISTERY),
    PIRATE("Pirate", GameGenre.MISTERY),
    LEPRECHAUN("Leprechaun", GameGenre.MISTERY),
    WIZARD("Wizard", GameGenre.MISTERY),
    NINJA("Ninja", GameGenre.MISTERY),
    DRAGON("Dragon", GameGenre.MISTERY),
    PALADIN("Paladin", GameGenre.MISTERY),
    VAMPIRE("Vampire", GameGenre.MISTERY),

    // SPACE
    ASTRONAUT("Astronaut", GameGenre.SPACE),
    CYBORG("Cyborg", GameGenre.SPACE),
    STAR_CAPTAIN("Star Captain", GameGenre.SPACE),
    ALIEN("Alien", GameGenre.SPACE),
    DROID("Droid", GameGenre.SPACE),

    // WAR
    GENERAL("General", GameGenre.WAR),
    SNIPER("Sniper", GameGenre.WAR),
    MEDIC("Medic", GameGenre.WAR),
    COMMANDO("Commando", GameGenre.WAR),
    SPY("Spy", GameGenre.WAR),

    // ADVENTURE
    EXPLORER("Explorer", GameGenre.ADVENTURE),
    ARCHEOLOGIST("Archeologist", GameGenre.ADVENTURE),
    HUNTER("Hunter", GameGenre.ADVENTURE),
    SCHOLAR("Scholar", GameGenre.ADVENTURE),
    OUTLAW("Outlaw", GameGenre.ADVENTURE),

    // PUZZLE
    CODEBREAKER("Codebreaker", GameGenre.PUZZLE),
    MATH_GENIUS("Math Genius", GameGenre.PUZZLE),
    PHILOSOPHER("Philosopher", GameGenre.PUZZLE),
    INVENTOR("Inventor", GameGenre.PUZZLE),
    CHESSMASTER("Chessmaster", GameGenre.PUZZLE),

    // STRATEGY
    TACTICIAN("Tactician", GameGenre.STRATEGY),
    WARLORD("Warlord", GameGenre.STRATEGY),
    DIPLOMAT("Diplomat", GameGenre.STRATEGY),
    GOVERNOR("Governor", GameGenre.STRATEGY),
    SPYMASTER("Spymaster", GameGenre.STRATEGY),

    // OTHER
    WANDERER("Wanderer", GameGenre.OTHER),
    GAMER("Gamer", GameGenre.OTHER),
    TOURIST("Tourist", GameGenre.OTHER),
    OBSERVER("Observer", GameGenre.OTHER);

    companion object {
        fun random(genre: GameGenre): HandleType {
            val matching = entries.filter { it.genre == genre }
            if (matching.isEmpty()) {
                return GHOST
            }
            return matching[Random.nextInt(matching.size)]
        }
    }
}

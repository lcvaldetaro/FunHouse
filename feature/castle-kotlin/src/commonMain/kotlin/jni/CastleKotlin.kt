package jni

import club.gepetto.GcLog
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.GAMES_FOLDER
import com.funhouse.shared.common.jni.BaseKotlinGame

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlinx.coroutines.*
import com.funhouse.shared.common.utils.GcInputQueue

class CastleKotlin : BaseKotlinGame() {

    // Constants
    companion object {
        private const val OBJECTS = 41
        private const val LOCS = 181
        private const val DARK = 16
        private const val DOORS = 14
        private const val CARRY = 5
        private const val MONSTERS = 12
        private const val STARTING_HITS = 50
        private const val LAMP_DIM_1 = 300
        private const val LAMP_DIM_2 = 350
        private const val LAMP_DIE = 400
        private const val EXTRA_POINTS = 32

        // Vocabulary structures
        private class VocabEntry(val word: String, val code: Int)
        private val vocabulary = listOf(
            // Verbs
            VocabEntry("GO", 1), VocabEntry("RUN", 1), VocabEntry("WALK", 1),
            VocabEntry("TAKE", 2), VocabEntry("GRAB", 2), VocabEntry("GET", 2),
            VocabEntry("DROP", 3), VocabEntry("DIG", 4),
            VocabEntry("LIGHT", 5), VocabEntry("ON", 5), VocabEntry("OFF", 6),
            VocabEntry("OPEN", 7), VocabEntry("UNLOCK", 7), VocabEntry("MOVE", 7), VocabEntry("PUSH", 7), VocabEntry("REMOVE", 7),
            VocabEntry("CLOSE", 8), VocabEntry("LOCK", 8),
            VocabEntry("LOOK", 9), VocabEntry("VIEW", 9),
            VocabEntry("INVENTORY", 10), VocabEntry("CARRY", 10),
            VocabEntry("QUIT", 11), VocabEntry("STOP", 11),
            VocabEntry("READ", 12),
            VocabEntry("WAVE", 13), VocabEntry("RUB", 13), VocabEntry("OPERATE", 13), VocabEntry("USE", 13),
            VocabEntry("ABRACADABRA", 14), VocabEntry("HOKUS", 14), VocabEntry("POKUS", 14), VocabEntry("SESAME", 14), VocabEntry("SHAZAM", 14), VocabEntry("PLUGH", 14), VocabEntry("XYZZY", 14),
            VocabEntry("FARLEY", 15), VocabEntry("SUSPEND", 16), VocabEntry("RESUME", 17),
            VocabEntry("ATTACK", 18), VocabEntry("SWING", 18), VocabEntry("STAB", 18), VocabEntry("THRUST", 18), VocabEntry("KILL", 18), VocabEntry("HIT", 18),
            VocabEntry("DRINK", 19), VocabEntry("EMPTY", 20), VocabEntry("DUMP", 20), VocabEntry("SPILL", 20),
            VocabEntry("RETREAT", 21), VocabEntry("BACKUP", 21), VocabEntry("LAST", 21), VocabEntry("BACK", 21), VocabEntry("OUT", 21),
            VocabEntry("OIL", 22), VocabEntry("LUBRICATE", 22),
            VocabEntry("SCORE", 23), VocabEntry("POINTS", 23),
            VocabEntry("SHIT", 24), VocabEntry("FUCK", 24), VocabEntry("DAMN", 24), VocabEntry("PISS", 24),
            // Nouns (50+)
            VocabEntry("GATE", 50), VocabEntry("GRATE", 50), VocabEntry("FENCE", 50), VocabEntry("DOOR", 50), VocabEntry("SLAB", 50), VocabEntry("SIGN", 50), VocabEntry("BOOKCASE", 50), VocabEntry("SHELVES", 50), VocabEntry("SHELF", 50), VocabEntry("BARS", 50), VocabEntry("CELL", 50),
            VocabEntry("PIT", 51), VocabEntry("HOLE", 51),
            VocabEntry("STAIRS", 52), VocabEntry("LADDER", 52),
            VocabEntry("WALL", 53), VocabEntry("CAVE", 53), VocabEntry("CAVERN", 53), VocabEntry("GROUND", 53), VocabEntry("FLOOR", 53),
            // Objects (100+)
            VocabEntry("LAMP", 100), VocabEntry("LANTERN", 100),
            VocabEntry("KEY", 101),
            VocabEntry("ROPE", 102), VocabEntry("STRING", 102), VocabEntry("CORD", 102),
            VocabEntry("KNIFE", 103), VocabEntry("DAGGER", 103),
            VocabEntry("AXE", 104), VocabEntry("HATCHET", 104),
            VocabEntry("SWORD", 105), VocabEntry("HILT", 105),
            VocabEntry("SACK", 106), VocabEntry("BAG", 106),
            VocabEntry("BATTERIES", 107), VocabEntry("BATTERY", 107),
            VocabEntry("BAR", 108), VocabEntry("CROWBAR", 108),
            VocabEntry("ROD", 109), VocabEntry("WAND", 109),
            VocabEntry("SCROLL", 110), VocabEntry("PAPER", 110), VocabEntry("ROLL", 110),
            VocabEntry("POTION", 111), VocabEntry("BOTTLE", 111),
            VocabEntry("FLASK", 112), VocabEntry("LIQUID", 112),
            VocabEntry("VELVET", 113), VocabEntry("PILLOW", 113),
            VocabEntry("ROCK", 114), VocabEntry("STONE", 114), VocabEntry("PURPLE", 114),
            VocabEntry("CANNON", 115), VocabEntry("BALL", 115),
            VocabEntry("METAL", 116), VocabEntry("DETECTOR", 116),
            // Treasures (120+)
            VocabEntry("COINS", 120), VocabEntry("MONEY", 120),
            VocabEntry("RING", 121), VocabEntry("BAND", 121),
            VocabEntry("STATUE", 122), VocabEntry("CRYSTAL", 122), VocabEntry("DRAGON", 122),
            VocabEntry("IVORY", 123), VocabEntry("AMULET", 123),
            VocabEntry("EBONY", 124), VocabEntry("GLOBE", 124),
            VocabEntry("CROWN", 125),
            VocabEntry("DIAMOND", 126),
            VocabEntry("GOLD", 127), VocabEntry("NUGGET", 127), VocabEntry("NUGGETS", 127),
            VocabEntry("PRECIOUS", 128), VocabEntry("JEWELS", 128), VocabEntry("JEWELRY", 128),
            VocabEntry("PLATINUM", 129), VocabEntry("PYRAMID", 129),
            VocabEntry("CHEST", 130), VocabEntry("TREASURE", 130),
            VocabEntry("EMERALD", 131), VocabEntry("EARRING", 131),
            VocabEntry("RARE", 132), VocabEntry("SPICE", 132), VocabEntry("SPICES", 132),
            VocabEntry("PERSIAN", 133), VocabEntry("RUG", 133),
            VocabEntry("RUBY", 134), VocabEntry("STAFF", 134),
            VocabEntry("MING", 135), VocabEntry("VASE", 135),
            VocabEntry("SILVER", 136), VocabEntry("PENDANT", 136),
            VocabEntry("PAINTING", 137), VocabEntry("ART", 137),
            VocabEntry("BRONZE", 138), VocabEntry("CANDLE", 138), VocabEntry("CANDLESTICK", 138),
            VocabEntry("SILK", 139), VocabEntry("JACKET", 139), VocabEntry("SILKEN", 139), VocabEntry("COAT", 139),
            VocabEntry("BOOK", 140), VocabEntry("BIBLE", 140), VocabEntry("LEATHER", 140),
            // Directions (240+)
            VocabEntry("NORTH", 240), VocabEntry("N", 240),
            VocabEntry("EAST", 241), VocabEntry("E", 241),
            VocabEntry("SOUTH", 242), VocabEntry("S", 242),
            VocabEntry("WEST", 243), VocabEntry("W", 243),
            VocabEntry("UP", 244), VocabEntry("U", 244),
            VocabEntry("DOWN", 245), VocabEntry("D", 245),
            // Fillers
            VocabEntry("SAY", 0), VocabEntry("THE", 0), VocabEntry("A", 0), VocabEntry("WITH", 0), VocabEntry("TURN", 0), VocabEntry("AT", 0)
        )

        private val doors = arrayOf(
            "The grate is locked|The grate is open",
            "A large bookcase fills the east wall|A large bookcase is standing beside a small exit in the east wall",
            "The door is locked shut|The door is open",
            "A massive stone slab leans against the west wall|A massive stone slab stands beside a passage leading west",
            "The fissure is too wide to cross|A solid stone bridge crosses the fissure",
            "The trapdoor is locked|The trapdoor is open",
            "The sign is firmly fastened to the wall|The sign stands beside a small passage",
            "The door is locked|The door is open",
            "The hinges on the door are rusted solid|The door is open",
            "The door is tightly locked|The door is standing open",
            "The north wall is filled with sturdy shelves|A small passage exits from the north wall, beside a pile of rubble",
            "The bars are locked shut|The bars are open",
            "The bars are locked shut|The bars are open",
            "The bars are locked shut|The bars are open"
        )

        private val door_keys = byteArrayOf(
            1, 8, 1, 8, 0, 1, 8, 1, 0, 1, 8, 1, 1, 1
        )

        private val damage = byteArrayOf(
            10, 18, 34
        )

        private val l_descriptions = arrayOf(
            "An unlit brass lantern is here|A brightly glowing brass lantern is here|A dimly glowing brass lantern is here|A very dim brass lantern is here",
            "There is a small key on the ground here",
            "There is a long length of rope here",
            "A small sharp knife is on the ground nearby",
            "A little axe is lying on the ground here",
            "The hilt of a shining sword protrudes from the stone|There is a long shiny sword here",
            "A large brown sack is lying in a heap on the floor",
            "There are fresh batteries here|Some old worn-out batteries are discarded nearby",
            "A long crowbar is lying on the ground here",
            "There is a long black rod with a rusty star on its end here",
            "A tightly rolled paper scroll is here|A blank sheet of paper is here",
            "There is a small bottle here, containing a golden potion|A small empty bottle is discarded nearby",
            "There is a small flask full of a dark liquid here|A small empty flask is discarded nearby",
            "A soft velvet pillow is resting on the floor here",
            "A funny looking purple stone is lying on the ground here",
            "There is a large black cannon ball here",
            "A battered metal detector is lying on the ground here",
            "", "", "",
            "There is a pile of coins on the ground here",
            "A small golden ring is lying on the ground here",
            "There is a small crystal statue of a dragon here",
            "A small ivory amulet is lying to one side",
            "A small globe of ebony is resting on the floor here",
            "There is a many jeweled crown here",
            "A large sparkling diamond is here",
            "There are nuggets of gold here",
            "There is precious jewelry here",
            "There is a platinum pyramid here, eight inches to a side",
            "The thiefs treasure chest is here",
            "A large emerald earring is lying on the ground here",
            "There are rare spices here",
            "A valuable persion rug is spread out on the floor",
            "A ruby studded staff is lying on the ground here",
            "A precious ming vase is resting delicately here|The floor is littered with worthless shards of pottery",
            "There is a large silver pendant lying here",
            "A beautiful painting is lying against the wall",
            "There is a beautiful bronze candlestick here",
            "There is a lovely silken jacket here",
            "A rare leather bound bible is lying on the ground here"
        )

        private val s_descriptions = arrayOf(
            "Brass lantern|Brightly shining brass lantern|Dim brass lantern|Very dim brass lantern",
            "Small key",
            "Coil of rope",
            "Small sharp knife",
            "Little axe",
            "Long shiny sword",
            "Large sack",
            "Fresh batteries|Worn-out batteries",
            "Long crowbar",
            "Long black rod",
            "Paper scroll|Blank paper scroll",
            "Bottle containing golden potion|Empty bottle",
            "Flask containing dark liquid|Empty flask",
            "Velvet pillow",
            "Purple stone",
            "Cannon ball",
            "Metal detector",
            "", "", "",
            "Many coins",
            "Small golden ring",
            "Crystal statue",
            "Ivory amulet",
            "Ebony globe",
            "Jeweled crown",
            "Sparkling diamond",
            "Gold nuggets",
            "Precious jewelry",
            "Platinum pyramid",
            "Treasure chest",
            "Emerald earring",
            "Rare spices",
            "Persion rug",
            "Rubby studded staff",
            "Ming vase|Broken vase",
            "Silver pendant",
            "Beautiful painting",
            "Bronze candlestick",
            "Silken jacket",
            "Rare bible"
        )

        private val travel = arrayOf(
            intArrayOf(0, 0, 0, 0, 0, 0),
            intArrayOf(5, 0, 2, 6, 0, 0),
            intArrayOf(1, 2, 2, 3, 0, 0),
            intArrayOf(0, 2, 0, 0, 0, 4),
            intArrayOf(0, 0, 0, 0, 3, 0),
            intArrayOf(7, 8, 1, 6, 0, 0), // 5
            intArrayOf(7, 5, 1, 7, 0, 0),
            intArrayOf(7, 7, 5, 7, 0, 0),
            intArrayOf(7, 7, 9, 5, 0, 0),
            intArrayOf(8, 7, 7, 7, 0, 0x200f),
            intArrayOf(105, 0, 0, 0xc068, 0, 0), // 10
            intArrayOf(0, 0, 126, 0, 0, 0),
            intArrayOf(164, 0, 163, 165, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0),
            intArrayOf(26, 16, 17, 25, 0x2009, 0), // 15
            intArrayOf(0, 0x211e, 0, 15, 0, 0),
            intArrayOf(15, 19, 0, 18, 0, 0),
            intArrayOf(0, 17, 0, 0, 0, 0),
            intArrayOf(0, 0, 20, 17, 0, 0),
            intArrayOf(19, 0, 21, 22, 0, 0), // 20
            intArrayOf(20, 0, 0, 0, 0, 0),
            intArrayOf(0, 20, 0, 23, 0, 0),
            intArrayOf(24, 22, 0, 0, 0, 0),
            intArrayOf(25, 0, 23, 0, 0, 0),
            intArrayOf(31, 15, 24, 0, 0, 0), // 25
            intArrayOf(27, 0, 15, 0, 0, 0),
            intArrayOf(0x221c, 0, 26, 0, 0, 0),
            intArrayOf(0, 29, 0x221b, 0, 0, 0),
            intArrayOf(0, 0, 0, 28, 0, 0),
            intArrayOf(0, 0, 0, 16, 0, 32), // 30
            intArrayOf(0, 0, 25, 0, 0, 0),
            intArrayOf(33, 0, 0, 0, 30, 0),
            intArrayOf(34, 0, 44, 32, 0, 0),
            intArrayOf(38, 40, 33, 0x2323, 0, 0),
            intArrayOf(36, 34, 0, 0, 0, 0), // 35
            intArrayOf(0, 37, 35, 0, 0, 0),
            intArrayOf(0, 0, 0, 36, 0, 61),
            intArrayOf(0, 39, 34, 0, 0, 0),
            intArrayOf(0, 0, 0, 38, 0, 0),
            intArrayOf(0, 42, 0x6629, 34, 0x6629, 0), // 40
            intArrayOf(40, 0, 0, 0, 0, 0),
            intArrayOf(0, 43, 0, 40, 0, 0),
            intArrayOf(0, 42, 0, 0, 0, 0),
            intArrayOf(0, 45, 0, 33, 0, 0),
            intArrayOf(0, 46, 48 + 0xe000, 44, 0, 0), // 45
            intArrayOf(0, 47, 0, 45, 0, 0),
            intArrayOf(0, 0, 0, 46, 0, 0),
            intArrayOf(56 + 0xe000, 55 + 0xe000, 53 + 0xe000, 49 + 0xe000, 45, 52 + 0xe000),
            intArrayOf(50 + 0xe000, 49 + 0xe000, 48 + 0xe000, 49 + 0xe000, 57 + 0xe000, 51 + 0xe000),
            intArrayOf(51 + 0xe000, 54 + 0xe000, 53 + 0xe000, 56 + 0xe000, 58 + 0xe000, 50 + 0xe000), // 50
            intArrayOf(49 + 0xe000, 55 + 0xe000, 54 + 0xe000, 50 + 0xe000, 57 + 0xe000, 57 + 0xe000),
            intArrayOf(48 + 0xe000, 56 + 0xe000, 55 + 0xe000, 49 + 0xe000, 54 + 0xe000, 52 + 0xe000),
            intArrayOf(50 + 0xe000, 57 + 0xe000, 54 + 0xe000, 59 + 0xe000, 58 + 0xe000, 48 + 0xe000),
            intArrayOf(57 + 0xe000, 54 + 0xe000, 49 + 0xe000, 52 + 0xe000, 57 + 0xe000, 56 + 0xe000),
            intArrayOf(54 + 0xe000, 56 + 0xe000, 59 + 0xe000, 52 + 0xe000, 53 + 0xe000, 51 + 0xe000), // 55
            intArrayOf(48 + 0xe000, 57 + 0xe000, 52 + 0xe000, 53 + 0xe000, 58 + 0xe000, 50 + 0xe000),
            intArrayOf(56 + 0xe000, 49 + 0xe000, 53 + 0xe000, 51 + 0xe000, 54 + 0xe000, 52 + 0xe000),
            intArrayOf(54 + 0xe000, 56 + 0xe000, 48 + 0xe000, 55 + 0xe000, 55 + 0xe000, 50 + 0xe000),
            intArrayOf(58 + 0xe000, 55 + 0xe000, 57 + 0xe000, 52 + 0xe000, 60, 53 + 0xe000),
            intArrayOf(0, 0, 0, 0, 0, 59 + 0xe000), // 60
            intArrayOf(0, 0, 62, 0, 37, 0),
            intArrayOf(61, 63, 0, 71, 0, 0),
            intArrayOf(64, 66, 0xe243, 62, 0, 0),
            intArrayOf(0, 0, 63, 0, 0x6641, 0),
            intArrayOf(0, 0, 0, 0, 0, 64), // 65
            intArrayOf(0, 0, 68, 63, 0, 0),
            intArrayOf(0xe23f, 68, 0, 0, 0, 0),
            intArrayOf(66, 0, 69, 67, 0, 0),
            intArrayOf(0, 68, 0x5749, 70, 0, 0),
            intArrayOf(71, 0, 69, 80 + 0xe000, 0, 0), // 70
            intArrayOf(72, 62, 70, 0, 0, 0),
            intArrayOf(0, 0, 71, 0, 0, 0xc258),
            intArrayOf(69, 0, 0x574a, 0, 0, 0),
            intArrayOf(0x5749, 0, 0x574b, 0x574c, 0, 0),
            intArrayOf(0x574a, 0x574e, 0, 0x574c, 0, 0), // 75
            intArrayOf(0x574a, 0x574b, 0x574d, 0, 0, 0),
            intArrayOf(0x574c, 0x574e, 0x584f, 0, 0, 0),
            intArrayOf(0x574b, 0, 0x584f, 0x574d, 0, 0),
            intArrayOf(0, 78, 0, 77, 0, 0),
            intArrayOf(81 + 0xe00, 81 + 0xe00, 83 + 0xe00, 85 + 0xe00, 84 + 0xe00, 70), // 80
            intArrayOf(83 + 0xe00, 82 + 0xe00, 80 + 0xe00, 86 + 0xe00, 85 + 0xe00, 81 + 0xe00),
            intArrayOf(82 + 0xe00, 85 + 0xe00, 80 + 0xe00, 81 + 0xe00, 86 + 0xe00, 84 + 0xe00),
            intArrayOf(85 + 0xe00, 83 + 0xe00, 82 + 0xe00, 84 + 0xe00, 83 + 0xe00, 83 + 0xe00),
            intArrayOf(83 + 0xe00, 84 + 0xe00, 84 + 0xe00, 82 + 0xe00, 83 + 0xe00, 84 + 0xe00),
            intArrayOf(85 + 0xe00, 81 + 0xe00, 82 + 0xe00, 86 + 0xe00, 82 + 0xe00, 81 + 0xe00), // 85
            intArrayOf(86 + 0xe00, 82 + 0xe00, 84 + 0xe00, 83 + 0xe00, 83 + 0xe00, 87),
            intArrayOf(0, 0, 0, 0, 86 + 0xe00, 0),
            intArrayOf(0, 0, 89, 0x245f, 72, 0),
            intArrayOf(88, 90, 91, 0, 0, 0),
            intArrayOf(0, 0, 0, 89, 0, 0), // 90
            intArrayOf(89, 92, 93, 0, 0, 0),
            intArrayOf(0, 0, 0, 91, 0, 0),
            intArrayOf(91, 94, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 93, 0, 0),
            intArrayOf(0, 0x2458, 97, 96, 0, 0), // 95
            intArrayOf(0, 95, 0, 0, 0, 0),
            intArrayOf(95, 0, 98, 0, 0, 0),
            intArrayOf(97, 0, 100, 99, 0, 0),
            intArrayOf(0, 98, 0, 0, 0, 0x2565),
            intArrayOf(98, 0, 0, 0, 0, 0), // 100
            intArrayOf(0, 102, 0, 0, 0x2563, 0),
            intArrayOf(112, 0, 103, 101, 0, 0),
            intArrayOf(102, 104, 0, 106, 0, 0),
            intArrayOf(0, 0xc00a, 0, 103, 0, 0),
            intArrayOf(0, 0, 10, 0, 0, 0), // 105
            intArrayOf(0, 103, 107, 0, 0, 0),
            intArrayOf(106, 0, 0xe26c, 109, 0, 0),
            intArrayOf(0, 0xe26b, 0, 109, 0, 0),
            intArrayOf(110, 107, 108, 0, 0, 0),
            intArrayOf(116, 111, 109, 120, 0, 0), // 110
            intArrayOf(118, 112, 0, 110, 0, 0),
            intArrayOf(0, 113, 102, 111, 0, 0),
            intArrayOf(114, 0, 0, 112, 0, 0),
            intArrayOf(0, 115, 113, 116, 0, 0),
            intArrayOf(0, 0, 0, 114, 0, 0), // 115
            intArrayOf(0x2675, 114, 110, 0, 0, 0),
            intArrayOf(0, 0, 116, 0, 0, 0),
            intArrayOf(0xe077, 0, 111, 0, 0, 0),
            intArrayOf(0xe177, 0xe177, 0xc176, 0xe177, 0xe177, 0xe177),
            intArrayOf(121, 110, 0, 0, 0, 0), // 120
            intArrayOf(0, 0, 120, 0, 0, 122),
            intArrayOf(0, 123, 131, 0, 121, 0),
            intArrayOf(0, 125, 0x287c, 122, 0, 0),
            intArrayOf(123, 0, 0, 0, 0, 0),
            intArrayOf(126, 0, 132, 123, 0, 0), // 125
            intArrayOf(11, 0, 125, 127, 0, 0),
            intArrayOf(128, 126, 0, 129, 0, 0),
            intArrayOf(0, 0, 127, 0, 0, 0),
            intArrayOf(127, 0, 131, 130, 0, 0),
            intArrayOf(129, 0, 133, 0, 0, 0), // 130
            intArrayOf(122, 0x2787, 0xe285, 129, 0, 0),
            intArrayOf(125, 0, 133, 0, 0, 0),
            intArrayOf(0xe283, 132, 134, 130, 0, 0),
            intArrayOf(133, 0, 0, 0, 0, 136),
            intArrayOf(0, 0, 0, 0x2783, 0, 0), // 135
            intArrayOf(137, 0, 0, 0, 134, 0),
            intArrayOf(138, 0, 136, 0, 0, 0),
            intArrayOf(0, 139, 137, 144, 0, 0),
            intArrayOf(140, 0, 0, 138, 0, 0),
            intArrayOf(142, 0, 139, 141, 0, 0), // 140
            intArrayOf(0, 140, 0, 0, 0, 0),
            intArrayOf(0, 0, 140, 143, 0, 0),
            intArrayOf(0x2994, 142, 0, 147, 0, 0),
            intArrayOf(145, 138, 0, 0, 0, 0),
            intArrayOf(147, 146, 144, 0, 0, 0), // 145
            intArrayOf(0x2a95, 0, 0, 145, 0, 0),
            intArrayOf(0, 143, 145, 0, 0, 0),
            intArrayOf(0, 0, 0x298f, 0, 0, 0),
            intArrayOf(0, 0, 146, 0, 0, 150),
            intArrayOf(0, 0, 151, 0, 149, 0), // 150
            intArrayOf(150, 152, 0, 160, 0, 0),
            intArrayOf(151, 0x2b9b, 153, 0, 0, 0),
            intArrayOf(152, 0x2c9c, 154, 0, 0, 0),
            intArrayOf(153, 0x2d9d, 158, 0, 0, 0),
            intArrayOf(0, 0, 0, 0x2b98, 0, 0), // 155
            intArrayOf(0, 0, 0, 0x2c99, 0, 0),
            intArrayOf(0, 0, 0, 0x2d9a, 0, 0),
            intArrayOf(0, 154, 159, 160, 0, 0),
            intArrayOf(158, 0, 0, 0, 0, 0),
            intArrayOf(151, 0, 158, 161, 0, 0), // 160
            intArrayOf(162, 160, 168, 163, 0, 0),
            intArrayOf(0, 0, 161, 0, 0, 0),
            intArrayOf(12, 161, 0, 166, 0, 0),
            intArrayOf(0, 0, 12, 0, 0, 0),
            intArrayOf(0, 12, 166, 0, 0, 0), // 165
            intArrayOf(165, 163, 167, 169, 0, 0),
            intArrayOf(166, 168, 0, 0, 0, 0),
            intArrayOf(161, 0, 0, 167, 0, 0),
            intArrayOf(170, 166, 173, 171, 0, 0),
            intArrayOf(0, 0, 169, 177, 0, 0), // 170
            intArrayOf(0, 169, 0, 172, 0, 0),
            intArrayOf(178, 171, 175, 0, 0, 0),
            intArrayOf(169, 0, 0, 174, 0, 0),
            intArrayOf(0, 173, 0, 175, 0, 0),
            intArrayOf(172, 174, 0xe3b0, 0, 0, 0), // 175
            intArrayOf(175, 0, 0, 0, 0, 0),
            intArrayOf(0, 170, 0, 178, 0, 0),
            intArrayOf(0, 177, 172, 179, 0, 0),
            intArrayOf(0, 178, 0, 180, 0, 0),
            intArrayOf(0, 179, 0, 0, 0, 0) // 180
        )

        private val diemsgs = arrayOf(
            "Oh dear.. You seem to have gotten yourself killed!\nI might be of some assistance although I have never actually done this before.\nWould you like me to try and re-incarnate you",
            "Clumsy oaf, you've done it again...\nI don't know how long I can keep this up!\nWould you like me to try again to re-incarnate you",
            "Now you've REALLY done it!!!\nIm all out of orange smoke!!!\nYou don't expect me to do a decent re-incarnation\nwithout orange smoke do you"
        )

        private val resmsgs = arrayOf(
            "All right, but don't blame me if something goes wrong...\n((( POOF!!! )))\nYou are engulfed in a cloud of bright orange smoke...\nYou emerge to find...",
            "Ok... Now where did I put my orange smoke...\n((( POOF!!! )))\nEverything disappears in a dense cloud of orange smoke",
            "If you're so smart.. do it yourself... Im leaving."
        )

        private val monsters = arrayOf(
            "There is a nasty looking skeleton attacking you!",
            "There is a nasty big orc in the room with you!",
            "A wicked little goblin is in the room with you!",
            "There is a fearsome bugbear in the room with you!",
            "A huge lizard-man is here in the room with you!",
            "There is a fearsome looking ogre in the room with you!",
            "There is a wretched looking mummy attacking you!",
            "A large suit of armor is attacking you",
            "A huge big giant is trying to kill you!",
            "Near the edge of your vision, a slight movement attracts your attention!",
            "A awful *DRAGON* is here in the room, he dosn't look friendly",
            "A little man in a long flowing robe is looking at you"
        )

        private val monster_attack = arrayOf(
            "The skeleton lunges at you!",
            "The orc swings at you with a nasty long sword!",
            "The goblin thrusts at you with a sharp spear!",
            "The bugbear claws and bites at you!",
            "The lizard-man snaps at you!",
            "The ogre bashes at you with a giant club!",
            "The mummy howls, and lunges at you!",
            "The suit of armour swings a fearsome sword!",
            "The giant clobbers at you with its huge fists!",
            "You hear the swoosh of a dagger behind your back!",
            "The *DRAGON* breathes fire.... Wow is it getting warm in here!!!",
            "The wizard waves his hands, and pronounces a spell!"
        )

        private val dead_monster = arrayOf(
            "A shattered pile of bones is lying to one side.",
            "A dead orc is lying here.",
            "A dead goblin is lying to one side.",
            "The body of a dead bugbear is peacefully resting here.",
            "The body of a lizard-man is lying here.",
            "A huge ugly nasty horrible dead ogre is lying on the ground here.",
            "A pile of cloth and bones is lying to one side.",
            "Pieces of broken armor litter the floor.",
            "The body of a huge giant is partially blocking the passage.",
            "A dead small elvish humanoid is here, wearing a small black mask.",
            "The body of a huge dead dragon is nearly blocking the passage.",
            "A small pile of ashes and a wizard's hat are lying here."
        )

        private val monster_short = arrayOf(
            "skeleton", "orc", "goblin", "bugbear", "lizard-man", "ogre",
            "mummy", "suit of armor", "giant", "thief", "dragon", "wizard"
        )

        private val monster_start = intArrayOf(
            15, 32, 32, 61, 61, 88, 101, 122, 122, 32, 137, 150
        )

        private val ranks = arrayOf(
            "You are obviously a rank amateur. Better luck next time.",
            "Your score qualifies you as a novice class adventurer.",
            "You have acheived the rateing 'Experienced Adventurer'.",
            "You may now consider yourself a 'Seasoned Adventurer'.",
            "You have reached 'Junior Master' status.",
            "You now rate as a master adventurer class D.",
            "You now rate as a master adventurer class C.",
            "You now rate as a master adventurer class B.",
            "You now rate as a master adventurer class A.",
            "All adventuredom gives tribute to you, 'Adventurer Grandmaster'."
        )

        private const val about = """
            Copyright 1983-2001 Dave Dunfield
            All rights reserved.

            Permission granted for personal (non-commercial) use only.
            Converted to Kotlin by Valdetaro Consulting, LLC in 2026.
        """
    }

    // State Variables
    private val prop = IntArray(OBJECTS)
    private val door_stat = IntArray(DOORS)
    private val location = IntArray(OBJECTS)
    private var lstate = 1
    private var died = 0
    private var found_chest = 0
    private var carry = 0
    private var turns = 0
    private var lturns = 0
    private var current_loc = 1
    private var old_loc = 1
    private var deep = 0
    private var hits = STARTING_HITS
    private val monster_hits = IntArray(MONSTERS)
    private val monster_loc = IntArray(MONSTERS)

    private val place_description = Array(LOCS) { "" }

    private var inbuf = ""
    private var inptrIndex = 0
    private var reading_not_finished = true

    private val inputQueue = GcInputQueue<String>()
    private var gameJob: Job? = null
    private val random = java.util.Random()

    private val backupDirectory: String
        get() = "${AppData.packageFolder}/${GAMES_FOLDER}"

    private fun rand(): Int {
        return random.nextInt(32768)
    }

    override fun start() {
        GcLog.d("CastleKotlin.start() called")
        gameJob?.cancel()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                runGame()
            } catch (e: CancellationException) {
                GcLog.d("Castle game job cancelled")
            }
        }
        greetings()
    }

    override fun start(gameNickName: String) {
        start()
    }

    override fun stop() {
        super.stop()
        gameJob?.cancel()
    }

    override fun sendCommand(command: String): Int {
        if (command.trim().equals("about", ignoreCase = true)) {
            myPrintf("Adventure Castle\n$about\n")
            return 0
        }
        inputQueue.put(command + "\n")
        return 0
    }

    private suspend fun get_line(prompt: String) {
        myPrintf(prompt + "\n")
        val raw = inputQueue.take()
        inbuf = raw.uppercase()
        if (inbuf.endsWith("\n")) {
            inbuf = inbuf.substring(0, inbuf.length - 1)
        }
        inptrIndex = 0
    }

    private fun parse(): Int {
        var sub = inbuf.substring(inptrIndex)
        // skip leading whitespace
        var skip = 0
        while (skip < sub.length && (sub[skip] == ' ' || sub[skip] == '\t')) {
            skip++
        }
        inptrIndex += skip
        sub = sub.substring(skip)

        if (sub.isEmpty()) {
            reading_not_finished = false
            return 0
        }

        for (entry in vocabulary) {
            if (sub.startsWith(entry.word)) {
                val len = entry.word.length
                if (len == sub.length) {
                    inptrIndex += len
                    reading_not_finished = false
                    return entry.code
                } else if (sub[len] == ' ' || sub[len] == '\t') {
                    inptrIndex += len
                    // skip subsequent blanks
                    while (inptrIndex < inbuf.length && (inbuf[inptrIndex] == ' ' || inbuf[inptrIndex] == '\t')) {
                        inptrIndex++
                    }
                    return entry.code
                }
            }
        }

        // No match
        val err = inbuf.trimEnd()
        myPrintf("I don't understand what '$err' means.\n")
        reading_not_finished = false
        return 0
    }

    private fun initialize_game() {
        val loc = intArrayOf(
            6, 4, 39, 8, 77, 33, 18, -1, 21, 24, 29,
            92, 64, 108, 10, 144, 146, -1, -1, -1,
            31, 67, 115, 65, 105, 79, 141, 41, 42, 117, -1,
            -1, 96, 12, 90, 163, 128, 124, 11, 135, 164
        )

        val mon_hits = intArrayOf(
            10, 20, 25, 30, 40, 55, 60, 60, 75, 50, 100, 100
        )

        val mon_loc = intArrayOf(
            15, 25, 41, 55, 69, 83, 97, 111, 125, 139, 153, 167
        )

        for (i in 0 until OBJECTS) {
            prop[i] = 0
            location[i] = loc[i]
        }

        for (i in 0 until MONSTERS) {
            monster_hits[i] = mon_hits[i]
            monster_loc[i] = mon_loc[i]
        }

        for (i in 0 until DOORS) {
            door_stat[i] = 0
        }

        lstate = 1
        died = 0
        found_chest = 0
        carry = 0
        turns = 0
        lturns = 0
        current_loc = 1
        old_loc = 1
        deep = 0
        hits = STARTING_HITS
    }

    private fun printHelp() {
        val file = File(backupDirectory, "castle.hlp")
        if (!file.exists()) {
            myPrintf("Cannot read castle.hlp!\n")
            return
        }
        try {
            val content = file.readText()
            myPrintf(content)
        } catch (e: Exception) {
            GcLog.e("Error reading castle.hlp", e)
        }
    }

    private fun read_data_file() {
        val file = File(backupDirectory, "castlelocations.dat")
        if (!file.exists()) {
            myPrintf("Cannot read castlelocations.dat!\n")
            myExit(0)
            return
        }
        try {
            file.bufferedReader().use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    if (line.length >= 8 && line.startsWith("[") && line.contains("]-")) {
                        val idxStr = line.substring(1, 6)
                        val i = idxStr.toIntOrNull()
                        if (i != null && i in 0 until LOCS) {
                            val desc = line.substring(8).trimEnd()
                            place_description[i] = desc
                        }
                    }
                    line = reader.readLine()
                }
            }
        } catch (e: Exception) {
            GcLog.e("Error reading castlelocations.dat", e)
        }
    }

    private fun isdark(locn: Int): Boolean {
        if (locn < DARK) return false
        if (prop[0] == 0) return true
        if (location[0] == locn) return false
        if (location[0] == 0 && locn == current_loc) return false
        return true
    }

    private fun have_object(obj: Int): Boolean {
        if (!ishere(obj)) {
            message("I see no such object here.")
            return false
        }
        return true
    }

    private fun ishere(obj: Int): Boolean {
        return (location[obj] == 0) || (location[obj] == current_loc)
    }

    private fun look(locn: Int) {
        if (isdark(locn)) {
            message("It is now pitch dark - if you procede, you will likely fall into a pit!")
        } else {
            myPrintf("${place_description[locn]}\n")

            // describe doors if any
            for (i in 0 until 6) {
                if ((travel[locn][i] shr 13) == 1) {
                    describe_door((travel[locn][i] shr 8) and 0x1f)
                }
            }

            // describe dead monsters if any
            for (i in 0 until MONSTERS) {
                if (monster_loc[i] == current_loc && monster_hits[i] <= 0) {
                    message(dead_monster[i])
                }
            }

            // describe objects present
            for (i in 0 until OBJECTS) {
                if (location[i] == locn) {
                    describe(i, l_descriptions)
                }
            }
        }
    }

    private fun describe_door(d: Int) {
        val fullDesc = doors[d]
        val parts = fullDesc.split("|")
        val descToPrint = if (door_stat[d] != 0 && parts.size > 1) {
            parts[1]
        } else {
            parts[0]
        }
        myPrintf("$descToPrint.\n")
    }

    private fun describe(obj: Int, table: Array<String>) {
        val fullDesc = table[obj]
        if (fullDesc.isEmpty()) return
        val parts = fullDesc.split("|")
        val pvalue = prop[obj]
        val descToPrint = if (pvalue in parts.indices) {
            parts[pvalue]
        } else {
            parts.last()
        }
        myPrintf("$descToPrint.\n")
    }

    private suspend fun move(dir: Int): Int {
        val new_loc = travel[current_loc][dir]
        if (new_loc == 0) {
            message("You can not move in that direction.")
            return 0
        }

        old_loc = current_loc

        if ((new_loc and 0xff00) != 0) { // conditions on move
            val value = (new_loc shr 8) and 0x1f
            when (new_loc shr 13) {
                1 -> { // door
                    if (door_stat[value] == 0) {
                        message("You can not move in that direction.")
                        describe_door(value)
                        return 0
                    }
                }
                2 -> { // has to have an object
                    if (location[value] != 0) {
                        message("Something is preventing you from entering the passage.")
                        return 0
                    }
                }
                3 -> { // has to not have an object
                    if (location[value] == 0) {
                        message("Something your carrying won't fit through the passage.")
                        return 0
                    }
                }
                4 -> { // object must have prop 0
                    if (prop[value] != 0) {
                        message("Not implemented yet")
                        return 0
                    }
                }
                5 -> { // object must have prop 1
                    if (prop[value] != 1) {
                        message("Not implemented yet")
                        return 0
                    }
                }
                6 -> { // special condition code
                    if (!special_condition(value)) {
                        return 0
                    }
                }
                7 -> { // special action as he moves
                    current_loc = new_loc and 0xff // move him first incase he dies
                    if (!special_action(value)) {
                        look(current_loc)
                    }
                    return 1
                }
            }
        }

        // all ok, move him now
        current_loc = new_loc and 0xff
        look(current_loc)
        return 1
    }

    private suspend fun special_condition(cond: Int): Boolean {
        when (cond) {
            0 -> { // must be empty handed
                if (carry > 0) {
                    message("Something your carrying won't fit through the passage.")
                    return false
                }
            }
            1 -> { // 10 percent probable
                if (4000 < rand()) {
                    special_action(1) // lost message
                    return false
                }
            }
            2 -> { // needs rope at current loc
                if (location[2] != current_loc) {
                    message("The passage looks unsafe, you would surely fall.")
                    return false
                }
            }
            else -> {
                message("Bad special condition")
            }
        }
        return true
    }

    private suspend fun special_action(act: Int): Boolean {
        var temp: Int
        when (act) {
            0 -> { // forgets where he was
                old_loc = 0
            }
            1 -> { // display "lost" message
                message("You have crawled around the rocks, and found yourself back in the main passage.")
                return true
            }
            2 -> { // arrow trap
                temp = rand()
                if (18000 > temp) {
                    message("As you walk down the passage, tiny arrows fire at you from")
                    val hitCount = (temp and 15) + 1
                    myPrintf("little holes in the wall. $hitCount of them get you - OUCH.\n")
                    hits -= hitCount
                    if (hits <= 0) {
                        die()
                        return true
                    }
                }
            }
            3 -> { // enters wizards room
                monster_loc[11] = 174 // wizard will enter shortly
            }
            else -> {
                message("bad special action")
            }
        }
        return false
    }

    private fun isobject(noun: Int): Int {
        if (noun < 100 || noun > 199) {
            message("I don't know of such a thing.")
            return -1
        }
        return noun - 100
    }

    private fun message(string: String) {
        myPrintf("$string\n")
    }

    private fun ok() {
        message("Ok")
    }

    private fun not_here() {
        message("I see nothing to do that to here.")
    }

    private fun test_range(word: Int, r1: Int, r2: Int): Boolean {
        if (word == 0) {
            message("Please specifiy what you want to do it to.")
            return false
        }
        if (word < r1 || word > r2) {
            message("I don't know how to do that to the object you specified.")
            return false
        }
        return true
    }

    private suspend fun die() {
        for (i in 0 until OBJECTS) { // first, drop things when dead
            if (location[i] == 0) {
                location[i] = current_loc
            }
        }
        carry = 0

        for (i in 0 until MONSTERS) { // release monsters
            if (monster_loc[i] == -1 || monster_loc[i] == 4294967295.toInt()) {
                monster_loc[i] = monster_start[i]
            }
        }

        hits = STARTING_HITS

        get_line(diemsgs[died] + "?")
        if (inbuf.trim().startsWith("Y")) {
            message(resmsgs[died])
            died++
            if (died == 3) { // force end of game
                end_game()
            }
            look(current_loc)
        } else {
            end_game()
        }
    }

    private fun calculate_score(): Int {
        var score = 0
        for (i in 20 until OBJECTS) { // first give points for safely got treasure
            if (location[i] == 4) {
                score += 10
            }
        }
        for (i in 0 until MONSTERS) { // now give points for killing monsters
            if (monster_hits[i] <= 0) {
                score += (i + 1)
            }
        }
        score += deep
        score -= 10 * died
        if (location[35] == 4 && prop[35] == 0) { // give him more for not busting vase
            score += EXTRA_POINTS
        }
        return score
    }

    private fun end_game() {
        var max_score = ((10 * (OBJECTS - 20)) + (LOCS - 1)) + EXTRA_POINTS
        for (i in 0 until MONSTERS) {
            max_score += (i + 1)
        }
        val score = calculate_score()
        myPrintf("You have ended your game with a score of $score out of\n")
        myPrintf("a possible total of $max_score points, using $turns turns.\n")
        
        var rankIdx = (score * 9) / max_score
        if (rankIdx < 0) rankIdx = 0
        if (rankIdx > 9) rankIdx = 9
        message(ranks[rankIdx])
        myExit(0)
    }

    private fun save_game(): Int {
        val file = File(backupDirectory, "castlegame.castlesaved")
        try {
            file.bufferedWriter().use { writer ->
                writer.write("MISC $lstate $died $found_chest $carry $turns $lturns $current_loc $old_loc $deep $hits\n")
                for (i in 0 until OBJECTS) {
                    writer.write("OBJ  $i ${prop[i]} ${location[i]}\n")
                }
                for (i in 0 until MONSTERS) {
                    writer.write("MON  $i ${monster_hits[i]} ${monster_loc[i]}\n")
                }
                for (i in 0 until DOORS) {
                    writer.write("DOOR $i ${door_stat[i]}\n")
                }
            }
            message("Done.")
            return 0
        } catch (e: Exception) {
            message("Game could not be saved.")
            GcLog.e("Error saving game", e)
            return -1
        }
    }

    private fun restore_game(): Int {
        val file = File(backupDirectory, "castlegame.castlesaved")
        if (!file.exists()) {
            message("Game could not be restored. Game file not found")
            return -1
        }
        try {
            file.bufferedReader().use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    val tokens = line.split("\\s+".toRegex()).filter { it.isNotEmpty() }
                    if (tokens.isNotEmpty()) {
                        when (tokens[0]) {
                            "MISC" -> {
                                if (tokens.size != 11) {
                                    message("Game could not be restored. Game file corrupted")
                                    return -1
                                }
                                lstate = tokens[1].toInt()
                                died = tokens[2].toInt()
                                found_chest = tokens[3].toInt()
                                carry = tokens[4].toInt()
                                turns = tokens[5].toInt()
                                lturns = tokens[6].toInt()
                                current_loc = tokens[7].toInt()
                                old_loc = tokens[8].toInt()
                                deep = tokens[9].toInt()
                                hits = tokens[10].toInt()
                            }
                            "OBJ" -> {
                                if (tokens.size != 4) {
                                    message("Game could not be restored. Game file corrupted")
                                    return -1
                                }
                                val idx = tokens[1].toInt()
                                prop[idx] = tokens[2].toInt()
                                location[idx] = tokens[3].toInt()
                            }
                            "MON" -> {
                                if (tokens.size != 4) {
                                    message("Game could not be restored. Game file corrupted")
                                    return -1
                                }
                                val idx = tokens[1].toInt()
                                monster_hits[idx] = tokens[2].toInt()
                                monster_loc[idx] = tokens[3].toInt()
                            }
                            "DOOR" -> {
                                if (tokens.size != 3) {
                                    message("Game could not be restored. Game file corrupted")
                                    return -1
                                }
                                val idx = tokens[1].toInt()
                                door_stat[idx] = tokens[2].toInt()
                            }
                        }
                    }
                    line = reader.readLine()
                }
            }
            message("Done.")
            return 0
        } catch (e: Exception) {
            message("Game could not be restored. Game file corrupted")
            GcLog.e("Error restoring game", e)
            return -1
        }
    }

    private fun myExit(code: Int) {
        myPrintf("Game will restart in 2 seconds...\n")
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            // ignore
        }
        inputQueue.clear()
        start()
        throw InterruptedException("Game Restarted")
    }

    private suspend fun runGame() {
        initialize_game()

        message("ADVENTURE CASTLE\n")
        get_line("Welcome adventurer, would you like instructions?")
        if (inbuf.trim().startsWith("Y")) {
            printHelp()
        }

        message("One moment please...")
        read_data_file()
        myPrintf("\n")

        look(current_loc)

        val words = IntArray(10)

        while (true) {
            if (current_loc > deep) deep = current_loc

            get_line(">")
            var temp = 0
            reading_not_finished = true

            while (reading_not_finished) {
                val parsedCode = parse()
                if (parsedCode != 0) {
                    if (temp < 9) {
                        words[temp] = parsedCode
                        temp++
                    }
                }
            }
            words[temp] = 0

            var verb = 0
            var noun = 0

            for (i in 0 until temp) {
                if (words[i] < 50 && words[i] > verb) {
                    verb = words[i]
                }
                if (words[i] > 49) {
                    noun = words[i]
                }
            }

            if (verb == 0 && noun > 239) { // assume GO
                verb = 1
            }

            var door = -1
            for (i in 0 until 6) {
                if ((travel[current_loc][i] shr 13) == 1) {
                    door = (travel[current_loc][i] shr 8) and 0x1f
                }
            }

            // figure out what he wants
            when (verb) {
                1 -> { // movement
                    if (noun < 240) {
                        message("Give me a direction.")
                    } else {
                        move(noun - 240)
                    }
                }
                2 -> { // take an object
                    if (noun in 50..99) {
                        message("You can't move it.")
                    } else {
                        val obj = isobject(noun)
                        if (obj != -1) {
                            if (location[obj] == 0) {
                                message("Your already carrying it!")
                            } else if (location[obj] != current_loc) {
                                message("I don't see it here.")
                            } else {
                                val carryLimit = CARRY + (if (location[6] == 0) 5 else 0)
                                if (carry >= carryLimit) {
                                    message("You can't carry anything more, you will have to drop something first.")
                                } else if (obj == 5 && prop[obj] == 0) {
                                    message("The sword is stuck quite firmly, you can't budge it!")
                                } else {
                                    location[obj] = 0
                                    carry++
                                    if (obj == 30) {
                                        found_chest = -1
                                    }
                                    ok()
                                }
                            }
                        }
                    }
                }
                3 -> { // drop an object
                    val obj = isobject(noun)
                    if (obj != -1) {
                        if (location[obj] != 0) {
                            message("You are not carrying it!")
                        } else if (obj == 6 && carry > CARRY) {
                            message("You need it to carry some of the other stuff!")
                        } else {
                            location[obj] = current_loc
                            carry--
                            ok()
                            if (current_loc == 60 && obj == 20) {
                                location[obj] = 148
                                if (location[7] == 0) {
                                    carry--
                                }
                                location[7] = current_loc
                                prop[7] = 0
                                message("There are fresh batteries here!")
                            }
                            if (obj == 35 && location[13] != current_loc) {
                                prop[35] = 1
                                message("The vase drops with a delicate crash!")
                            }
                        }
                    }
                }
                4 -> { // dig
                    message("Digging without a shovel is impractical, progress seems unlikely.")
                }
                5 -> { // light lamp
                    if (test_range(noun, 100, 100)) {
                        if (have_object(0)) {
                            if (prop[0] != 0) {
                                message("Its already on.")
                            } else {
                                if (lstate == 0 && prop[7] == 0 && ishere(7)) {
                                    lstate = 1
                                    prop[7] = 1
                                    lturns = 0
                                    message("I'm takeing the liberty of changing the batteries.")
                                }
                                if (prop[0] == lstate) {
                                    ok()
                                    if (current_loc >= DARK) look(current_loc)
                                } else {
                                    message("The batteries are dead.")
                                }
                            }
                        }
                    }
                }
                6 -> { // turn off lamp
                    if (test_range(noun, 100, 100)) {
                        if (have_object(0)) {
                            if (prop[0] == 0) {
                                message("Its already off.")
                            } else {
                                prop[0] = 0
                                ok()
                                if (current_loc >= DARK) look(current_loc)
                            }
                        }
                    }
                }
                7 -> { // open a door
                    if (test_range(noun, 50, 50)) {
                        if (door == -1) {
                            not_here()
                        } else {
                            val key = door_keys[door].toInt()
                            if (key != 0 && location[key] == 0) {
                                door_stat[door] = 1
                                ok()
                            } else {
                                message("You can't, I think you will need something to do that.")
                            }
                        }
                    }
                }
                8 -> { // close a door
                    if (test_range(noun, 50, 50)) {
                        if (door == -1) {
                            not_here()
                        } else {
                            val key = door_keys[door].toInt()
                            if (key != 0 && location[key] == 0) {
                                door_stat[door] = 0
                                ok()
                            } else {
                                message("You can't, I think you will need something to do that.")
                            }
                        }
                    }
                }
                9 -> { // look around
                    message("I will repeat the description of your location:")
                    look(current_loc)
                }
                10 -> { // inventory
                    message("You are currently carrying the following:")
                    for (i in 0 until OBJECTS) {
                        if (location[i] == 0) {
                            describe(i, s_descriptions)
                        }
                    }
                }
                11 -> { // quit
                    get_line("Do you really want to quit now?")
                    if (inbuf.trim().startsWith("Y")) {
                        end_game()
                    }
                    ok()
                }
                12 -> { // read
                    if (noun == 110) { // scroll
                        if (have_object(10)) {
                            if (prop[10] != 0) {
                                message("Im afraid the paper is blank")
                            } else if (ishere(21) && current_loc == 33) {
                                prop[10] = 1
                                prop[5] = 1
                                message("As you pronounce the archane words, the sword jumps as if alive,\nand falls at your feet.")
                            } else {
                                prop[10] = 1
                                message("As you attempt to pronounce the strange words on the scroll,\nThe ink disappears in a wisp of orange smoke.")
                            }
                        }
                    } else {
                        message("I don't know how to read that!")
                    }
                }
                13 -> { // wave, rub, operate, use
                    val obj = isobject(noun)
                    if (obj != -1) {
                        if (have_object(obj)) {
                            if (obj == 9 && (current_loc == 88 || current_loc == 95)) {
                                if (door_stat[4] != 0) {
                                    door_stat[4] = 0
                                    message("The stone bridge has vanished!")
                                } else {
                                    door_stat[4] = 1
                                    message("A stone bridge now spans the fissure!")
                                }
                            } else if (obj == 16) {
                                message("Crackle... Buzz... Humm....")
                                if (current_loc == 6 && location[31] == -1) {
                                    message("BEEP BEEP BEEP: Detected something")
                                    location[31] = current_loc
                                } else {
                                    message("No metal here")
                                }
                            } else {
                                message("Nothing exciting happens.")
                            }
                        }
                    }
                }
                14 -> {
                    message("Good try.... but thats on old worn-out magic word.")
                }
                15 -> { // magic words
                    old_loc = 0
                    if (current_loc == 16 || current_loc == 133) {
                        current_loc = 4
                        look(current_loc)
                    } else if (current_loc == 4) {
                        current_loc = 16
                        look(current_loc)
                    } else if (current_loc == 102) {
                        current_loc = 10
                        look(current_loc)
                    } else if (current_loc == 10) {
                        current_loc = 102
                        look(current_loc)
                    } else {
                        message("Nothing unusual happens.")
                    }
                }
                16 -> { // save game
                    if (save_game() == 0) {
                        end_game()
                    }
                }
                17 -> { // resume game
                    if (turns == 0) {
                        if (restore_game() != 0) {
                            end_game()
                        }
                        look(current_loc)
                    } else {
                        message("You can only resume at the start of a game")
                    }
                }
                18 -> { // attack
                    if (test_range(noun, 103, 105)) {
                        val weaponIdx = noun - 100
                        if (location[weaponIdx] == 0) {
                            val wType = noun - 103
                            var isMonsterHere = false
                            message("You take a wild swing...")
                            for (i in 0 until MONSTERS) {
                                if (monster_loc[i] == -1) {
                                    isMonsterHere = true
                                    if (rand() > (8000 - (wType * 1500))) {
                                        myPrintf("and hit the ${monster_short[i]}!\n")
                                        monster_hits[i] -= damage[wType]
                                        if (monster_hits[i] <= 0) {
                                            monster_loc[i] = current_loc
                                            message("You killed it!!!")
                                        }
                                    }
                                }
                            }
                            if (!isMonsterHere) {
                                message("but your blade meets only air, be carefull not to hurt yourself!")
                            }
                        } else {
                            message("You don't have that weapon.")
                        }
                    }
                }
                19 -> { // drink
                    if (test_range(noun, 111, 112) && have_object(noun - 100)) {
                        if (noun == 111) {
                            if (prop[11] == 0) {
                                prop[11] = 1
                                hits = STARTING_HITS
                                message("That was quite refreshing, you feel stronger.")
                            } else {
                                message("The bottle is empty.")
                            }
                        } else {
                            if (prop[12] == 0) {
                                prop[12] = 1
                                message("That was awful... I don't feel well!")
                                hits -= 15
                                if (hits <= 0) {
                                    die()
                                }
                            } else {
                                message("The flask is empty.")
                            }
                        }
                    }
                }
                20 -> { // empty
                    if (test_range(noun, 111, 112) && have_object(noun - 100)) {
                        val obj = noun - 100
                        if (prop[obj] == 0) {
                            prop[obj] = 1
                            ok()
                        } else {
                            message("It's already empty.")
                        }
                    }
                }
                21 -> { // backup
                    if (old_loc == 0 || old_loc == current_loc) {
                        message("I can't quite seem to remember how it was we got here!")
                    } else {
                        current_loc = old_loc
                        look(current_loc)
                    }
                }
                22 -> { // lubricate
                    if (test_range(noun, 50, 50)) {
                        if (!ishere(12)) {
                            message("You have no oil!")
                        } else if (prop[12] != 0) {
                            message("The oil flask is empty!")
                        } else if (door == -1) {
                            not_here()
                        } else {
                            prop[12] = 1
                            if (current_loc == 123) {
                                door_stat[8] = 1
                                message("As you lubricate the hinges, the door slowly creaks open!")
                            }
                        }
                    }
                }
                23 -> { // score
                    myPrintf("If you quit now, you will have a score of ${calculate_score()} points.\n")
                }
                24 -> { // debug/bad words
                    message("Watch your language!!!")
                    myPrintf("[$current_loc], ")
                    get_line("New?")
                    val dest = inbuf.trim().toIntOrNull()
                    if (dest != null && dest in 0 until LOCS) {
                        current_loc = dest
                        look(current_loc)
                    }
                }
                else -> {
                    message("I don't quite understand you.")
                }
            }

            turns++

            // check the status of the lamp
            if (prop[0] != 0) {
                lturns++
                if (lturns >= LAMP_DIM_1 && location[7] == 0 && ishere(0) && prop[7] == 0) {
                    prop[7] = 1
                    prop[0] = 1
                    lstate = 1
                    lturns = 0
                    message("You lamp is going dim, Im takeing the liberty of changing the batteries.")
                } else if (lturns == LAMP_DIM_1) {
                    prop[0] = 2
                    lstate = 2
                    if (ishere(0)) {
                        message("Your lamp is growing dim... You should wrap this up soon, unless you")
                        message("can find some fresh batteries. I seem to recall there is a vending")
                        message("machine in the maze, bring some coins with you")
                    }
                } else if (lturns == LAMP_DIM_2) {
                    prop[0] = 3
                    lstate = 3
                    if (ishere(0)) {
                        message("You lamp is getting dimmer... You'd best get those batteries soon!")
                    }
                } else if (lturns == LAMP_DIE) {
                    prop[0] = 0
                    lstate = 0
                    if (ishere(0)) {
                        message("Your lamp has run out of power.")
                    }
                }
            }

            if (isdark(current_loc) && rand() < 8000 && verb == 1) {
                message("You fell into a pit... OUCH!!! ... That hurt!")
                hits -= 10
                if (hits <= 0) {
                    die()
                }
            }

            // check for wandering monsters
            for (i in 0 until MONSTERS) {
                if (monster_loc[i] == current_loc && monster_hits[i] > 0) {
                    monster_loc[i] = -1
                }

                if (monster_loc[i] == -1) {
                    if (current_loc >= DARK - 1) {
                        message(monsters[i])
                        val tempRand = rand()
                        if (tempRand < 1000) {
                            monster_loc[i] = current_loc + 1
                            message("It ran off!")
                        } else if (tempRand < 23000) {
                            var j = OBJECTS
                            if (i == 9 && found_chest == 0) {
                                while (--j > 19) {
                                    if (ishere(j)) {
                                        monster_loc[9] = 87
                                        location[30] = 87
                                        location[j] = 87
                                        carry--
                                        j = 1
                                        message("A thief stole a treasure and ran off!")
                                    }
                                }
                            }
                            if (j > 0) {
                                message(monster_attack[i])
                            }
                            if (tempRand < 12000) {
                                message("It hits you...")
                                hits -= (i + 2)
                                if (hits <= 0) {
                                    die()
                                    break
                                } else {
                                    message("OUCH!!! ... That hurt!!!")
                                }
                            }
                        }
                    }
                } else {
                    if (monster_hits[i] > 0) {
                        monster_loc[i]++
                        if (monster_loc[i] >= LOCS) {
                            monster_loc[i] = monster_start[i]
                        }
                    }
                }
            }
        }
    }
}

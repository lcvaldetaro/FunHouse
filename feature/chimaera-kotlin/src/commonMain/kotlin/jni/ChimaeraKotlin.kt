package jni

import club.gepetto.GcLog
import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.jni.TerminalDataCallback
import com.funhouse.shared.common.AppData
import java.io.File
import kotlinx.coroutines.*
import com.funhouse.shared.common.utils.GcInputQueue

private const val LOGGING = 1
private const val DISPWIDTH = 80
private const val DELAY = 1000000

private const val version = "Version C1.00A"

private const val MAXACT = 81
private const val MOVMOD = 60
private const val MAXOBJ = 47
private const val MAXHINT = 31
private const val MAXEXP = 13
private const val MAXFLAG = 51
private const val MAXVAL = 51
private const val MAXMON = 9
private const val MAXPSEU = 11
private const val MAXREST = 100
private const val MAXDISPLAY = 2048

        // Index pointers to objects
private const val FOOD = 1
private const val BOTTLE = 2
private const val LAMP = 3
private const val PLANT = 4
private const val KEYS = 5
private const val SWORD = 6
private const val ROD = 7
private const val ROPE = 8
private const val GARLIC = 9
private const val STAFF = 10
private const val BASKET = 11
private const val PEARL = 12
private const val COINS = 13
private const val NUGGET = 14
private const val SILVER = 15
private const val DIAMOND = 16
private const val JEWELS = 17
private const val VASE = 18
private const val CARPET = 19
private const val BANKNOTES = 20
private const val MANUSCRIPT = 21
private const val FLUTE = 22
private const val BOOK = 23
private const val MUSIC = 24
private const val VIOLETS = 25
private const val BOX = 26
private const val PYRAMID = 27
private const val CUSHION = 28
private const val DAGGER = 29
private const val GOBLET = 30
private const val SAPPHIRE = 31
private const val NECKLACE = 32
private const val SCEPTRE = 33
private const val TREASURE = 34
private const val RUBY = 35
private const val CHARM = 36
private const val BRACELET = 37
private const val OYSTER = 38
private const val CLAM = 39
private const val ELIXIR = 40
private const val MIRROR = 41
private const val BRICKBATS = 42
private const val GNOME = 43
private const val SERPENTINE = 44
private const val VENUS = 45
private const val GOLDRING = 46

        // Parameters for moves
private const val NORTH = 1
private const val SOUTH = 2
private const val EAST = 3
private const val WEST = 4
private const val SE = 5
private const val SW = 6
private const val NE = 7
private const val NW = 8
private const val UP = 9
private const val DOWN = 10
private const val IN = 11
private const val OUT = 12

        // Index pointers to values[]
private const val BASE = 6
private const val FIRSTX = 7
private const val FIRSTY = 8
private const val LASTX = 9
private const val LASTY = 10
private const val LASTZ = 11
private const val LUCK = 12
private const val MONTH = 13
private const val DAY = 14
private const val WEEKDAY = 15
private const val DAWN = 16
private const val DUSK = 17
private const val DAYTIME = 18

private const val HELD = 20
private const val LIGHT = 21
private const val POOL = 22
private const val MAGONE = 23
private const val DWFNUM = 24
private const val DWFNOW = 25
private const val MONCOUNT = 26
private const val GORLOC = 27
private const val SEED = 28
private const val BRKBOT = 29
private const val BRKVAS = 30
private const val BRKGOB = 31
private const val BRKMIR = 32
private const val THIRST = 33
private const val FLICK = 34
private const val RNDCOUNT = 35
private const val HINTPTR = 36
private const val RUNOUT = 37

        // Index pointers to animals and monsters
private const val LEOPARD = 1
private const val TIGER = 2
private const val LION = 3
private const val LYNX = 4

private const val ANTELOPE = 1
private const val WILDEBEEST = 2
private const val ZEBRA = 3
private const val GAZELLE = 4
private const val DEER = 5
private const val ELEPHANT = 6
private const val BUFFALO = 7

private const val BATS = 1
private const val DWARF = 2
private const val SNAKE = 3
private const val GORGON = 4
private const val ELF = 5
private const val TROLL = 6
private const val DRAGON = 7
private const val VAMPIRE = 8

        // Index pointers to flags[]
private const val WIZARD = 1
private const val NIGHT = 2
private const val DARK = 3
private const val GLOW = 4
private const val LAMPON = 5
private const val WAVER = 6
private const val FR13 = 7
private const val HALLOW = 8
private const val LIFE = 9
private const val BOXLOK = 10
private const val DORLOK = 11
private const val ROLING = 12
private const val HIGRAS = 13
private const val ANIMAL = 14
private const val HERD = 15
private const val CARNIV = 16
private const val SEEN = 17
private const val DDOT = 18
private const val TREE = 19
private const val NOTUP = 20
private const val HOLE = 21
private const val HOME = 22
private const val INTENT = 23
private const val SMALL = 24
private const val LARGE = 25
private const val SWITCH = 26
private const val LEAD = 27
private const val UNDEAD = 28
private const val GENIE = 29
private const val IMMORTAL = 30
private const val EMPTY = 31
private const val DRIP = 32
private const val COPRNT = 33
private const val SHAFT = 34
private const val HANDSFULL = 35
private const val AUTOHINT = 36
private const val RABBIT = 37
private const val EXCALIBER = 38
private const val RINGON = 39
private const val SHANGRI = 40

private val dicact = arrayOf(
            "",
            "N", "S", "E", "W", "NE",
            "NW", "SE", "SW", "UP", "DOWN",
            "IN", "OUT", "ON", "TAKE", "KILL",
            "free", "LOOK", "CHASE", "SHANGRI", "",
            "EAT", "WIZARD", "go", "THROW", "WAVE",
            "STAMP", "PUT", "DROP", "OFF", "RUB",
            "FILL", "PLAY", "READ", "FIND", "1$$$$",
            "INVENTORY", "HELP", "SCORE", "QUIT", "DRINK",
            "OPEN", "CLOSE", "LOCK", "UNLOCK", "CALL",
            "INSTRUCT", "PLUGH", "GAMIC", "SLEEP", "TIME",
            "WHERE", "ADVENTURE", "EXAMINE", "HINT", "",
            "", "", "SAVE", "RESTORE", "",
            "NORTH", "SOUTH", "EAST", "WEST", "NORTHEAST",
            "NORTHWEST", "SOUTHEAST", "SOUTHWEST", "CLIMB", "DESCEND",
            "ENTER", "EXIT", "LIGHT", "GET", "SLAY",
            "release", "DESCRIBE", "FOLLOW", "", ""
        )

private val verbs = arrayOf(
            "",
            "standing in",
            "lying in",
            "walking through",
            "walking along",
            "crawling through",
            "crawling along",
            "climbing"
        )

private val nouns = arrayOf(
            "",
            "oubliette",
            "crypt",
            "dungeon",
            "cell",
            "alcove",
            "cavern",
            "grotto",
            "cave",
            "tunnel",
            "antechamber",
            "passageway",
            "hall",
            "corridor",
            "chamber",
            "passage",
            "room",
            "well",
            "shaft"
        )

private val adject1 = arrayOf(
            "",
            "an impressive",
            "an ornate",
            "an arched",
            "an imposing",
            "a cramped",
            "a gigantic",
            "a tiny",
            "an enormous",
            "a low",
            "a high",
            "a narrow",
            "a wide",
            "a little",
            "a big",
            "a small",
            "a large"
        )

private val adject2 = arrayOf(
            "",
            " evil",
            " smelly",
            "",
            " ominous",
            "",
            " dank",
            "",
            " sandy",
            "",
            " damp",
            "",
            " dry"
        )

private val thing = arrayOf(
            "",
            "FOOD", "BOTTLE", "LAMP", "PLANT", "KEYS",
            "SWORD", "ROD", "ROPE", "GARLIC", "STAFF",
            "BASKET", "PEARL", "COINS", "NUGGET", "SILVER",
            "DIAMOND", "JEWELS", "VASE", "CARPET", "BANKNOTES",
            "MANUSCRIPT", "FLUTE", "BOOK", "MUSIC", "VIOLETS",
            "BOX", "PYRAMID", "CUSHION", "DAGGER", "GOBLET",
            "SAPPHIRE", "NECKLACE", "SCEPTRE", "TREASURE", "RUBY",
            "CHARM", "BRACELET", "OYSTER", "CLAM ", "ELIXIR",
            "MIRROR", "BRICKBATS", "GNOME", "SERPENTINE", "VENUS",
            "RING"
        )

private val thingpref = arrayOf(
            "",
            "some ",
            "a ",
            "a ",
            "a ",
            "some ",
            "a ",
            "a ",
            "some ",
            "some ",
            "a ",
            "a ",
            "a ",
            "some ",
            "a ",
            "some ",
            "a ",
            "some ",
            "a ",
            "a ",
            "some ",
            "a ",
            "a ",
            "a ",
            "some ",
            "some ",
            "a ",
            "a ",
            "a ",
            "a ",
            "a ",
            "a ",
            "a ",
            "a ",
            "some ",
            "a ",
            "a ",
            "a ",
            "an ",
            "a ",
            "the ",
            "a ",
            "some ",
            "a ",
            "a ",
            "a ",
            "a "
        )

private val thingdesc = arrayOf(
            "",
            "some tasty food",
            "a green glass bottle",
            "a tarnished brass lamp",
            "a little plant",
            "a bunch of keys",
            "a rusty bloodstained sword",
            "a polished black rod",
            "a coil of rope",
            "a clove of garlic",
            "a long wooden staff",
            "a small wicker basket",
            "a glistening pearl",
            "a handful of coins",
            "a golden nugget",
            "several bars of silver",
            "a brilliant diamond",
            "some precious jewels",
            "a delicate Ming vase",
            "an ornate Persian carpet",
            "some bundles of banknotes",
            "an illuminated manuscript",
            "a beautiful silver flute",
            "a dog-eared book",
            "some sheets of music",
            "a bunch of violets",
            "a gem encrusted box",
            "a platinum pyramid",
            "a richly embroidered cushion",
            "a jewelled dagger",
            "a crystal goblet",
            "a priceless sapphire",
            "a glittering necklace",
            "a princely sceptre",
            "a small chest of treasure",
            "a blood-red ruby",
            "a good luck charm",
            "an emerald bracelet",
            "an enormous oyster",
            "a giant clam",
            "the elixir of life",
            "a highly polished mirror",
            "some scattered brickbats",
            "a pottery gnome",
            "a block of serpentine",
            "a statue of the Venus de Milo",
            "a beautiful golden ring"
        )

private val pseudo = arrayOf(
            "",
            "camp", "tent", "base", "water", "servant",
            "", "", "", "", ""
        )

private val explet = arrayOf(
            "",
            "BLAST", "BUGGER", "DAMN", "SHIT", "SOD",
            "HELL", "FUCK", "PISS", "WANKER", "BOLLOCKS",
            "CUNT", "ARSEHOLE"
        )

private val months = arrayOf(
            "",
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )

private val mdays = intArrayOf(
            0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        )

private val dawn = intArrayOf(
            0, 17, 15, 12, 9, 7, 6, 7, 9, 12, 15, 17, 18
        )

private val dusk = intArrayOf(
            0, 31, 33, 36, 39, 41, 42, 41, 39, 36, 33, 31, 30
        )

private val weekdays = arrayOf(
            "",
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        )

private val cows = arrayOf(
            "*",
            "antelopes", "wildebeest", "zebras", "gazelles",
            "deer", "elephants", "buffalos"
        )

private val cats = arrayOf(
            "*",
            "leopard", "tiger", "lion", "lynx"
        )

private val beasts = arrayOf(
            "",
            "BAT", "DWARF", "SNAKE", "GORGON",
            "ELF", "TROLL", "DRAGON", "VAMPIRE"
        )

private val classes = arrayOf(
            "",
            "a novice", "an apprentice", "a student", "a graduate", "a third class",
            "a second class", "a first class", "a master", "a grand master", "a supreme champion"
        )

private val hints = arrayOf(
            "",
            "The only really safe place to sleep is in your tent",
            "When the carnivores have seen you they may attack at any time",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
        )

private val xinc = intArrayOf(0, 0, 0, 1, -1, 1, -1, 1, -1, 0, 0)
private val yinc = intArrayOf(0, 1, -1, 0, 0, 1, 1, -1, -1, 0, 0)
private val zinc = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1)
private val waybit = intArrayOf(0, 8, 8, 16, 16, 32, 64, 64, 32, 128, 128)

private val routes = arrayOf(
            "",
            "north", "south", "east", "west",
            "northeast", "northwest", "southeast", "southwest"
        )

private val numerals = arrayOf(
            "zero", "one", "two", "three", "four", "five"
        )

private val times = arrayOf(
            "",
            "Midnight", "00.30", "01.00", "01.30", "02.00", "02.30",
            "03.00", "03.30", "04.00", "04.30", "05.00", "05.30",
            "06.00", "06.30", "07.00", "07.30", "08.00", "08.30",
            "09.00", "09.30", "10.00", "10.30", "11.00", "11.30",
            "Midday", "12.30", "13.00", "13.30", "14.00", "14.30",
            "15.00", "15.30", "16.00", "16.30", "17.00", "17.30",
            "18.00", "18.30", "19.00", "19.30", "20.00", "20.30",
            "21.00", "21.30", "22.00", "22.30", "23.00", "23.30"
        )

class ChimaeraKotlin : BaseKotlinGame() {
    // Game properties and state
    private var x: Int = 0
    private var y: Int = 0
    private var z: Int = 0
    private var here: Int = 0
    private var described: Int = 0

    private var xdist: Int = 0
    private var ydist: Int = 0
    private var flagxy: Int = 0
    private var distance: Int = 0

    private var pseudorand: Int = 0

    private val objloc = IntArray(MAXOBJ)
    private val points = IntArray(MAXOBJ)
    private val ipt = IntArray(MAXOBJ)
    private val obhere = IntArray(MAXOBJ)
    private val secure = IntArray(MAXOBJ)
    private val gone = IntArray(MAXOBJ)

    private var moveno: Int = 0
    private var score: Int = 0
    private var delay: Int = 0
    private var helpno: Int = 0
    private var advno: Int = 0
    private var cow: Int = 0
    private var cat: Int = 0

    private var userpass = ""
    private val about = "Chimaera Text Adventure\nVersion C1.002\nWritten by Nicholas Perre-Wetherall\n           [aka Chris Newall]\n             Copyright 1984\n          All rights reserved\nPermission granted for personal (non-commercial) use only."

    private var inbuff = ""
    private var display = ""
    private var action = ""
    private var `object` = ""
    private var lastaction = ""
    private var lastobject = ""

    private val ways = IntArray(15)
    private val values = IntArray(MAXVAL)
    private val monstr = IntArray(MAXMON)
    private val flags = IntArray(MAXFLAG)
    private val hintsdone = IntArray(MAXHINT)

    private var packageFolder = AppData.packageFolder ?: ""
    private var gameFolder = AppData.gameFolder ?: ""
    private val backupDirectory get() = File(packageFolder, gameFolder).apply { if (!exists()) mkdirs() }

    private val inputQueue = GcInputQueue<String>()
    private var gameJob: Job? = null

    private var randomGenerator = java.util.Random()

    private fun srand(seed: Int) {
        randomGenerator = java.util.Random(seed.toLong())
    }

    private fun rand(): Int {
        return randomGenerator.nextInt(32768)
    }

    private fun rnd(i: Int): Int {
        if (i <= 0) return 1
        val r = randomGenerator.nextInt(i) + 1
        pseudorand++
        if (pseudorand > 5) pseudorand = 1
        values[RNDCOUNT]++
        return r
    }

    private fun Random(n: Int): Int {
        if (n <= 0) return 1
        return randomGenerator.nextInt(n) + 1
    }

    override fun start() {
        GcLog.d("ChimaeraKotlin.start() called")
        gameJob?.cancel()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                runGame()
            } catch (e: CancellationException) {
                GcLog.d("Chimaera game job cancelled")
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
            myPrintf("%s\n", about)
            return 0
        }
        inputQueue.put(command + "\n")
        return 0
    }

    private suspend fun getLine(): String {
        return inputQueue.take()
    }

    private suspend fun get_line(): Int {
        val raw = getLine()
        inbuff = raw.uppercase()
        if (inbuff.endsWith("\n")) {
            inbuff = inbuff.substring(0, inbuff.length - 1)
        }
        return inbuff.length
    }

    private suspend fun yesno(): Int {
        while (true) {
            val len = get_line()
            if (len == 0) continue
            if (inbuff == "YES" || inbuff == "Y") return 1
            if (inbuff == "NO" || inbuff == "N") return 2
        }
    }

    private suspend fun get_password(): Int {
        userpass = getLine().trim()
        if (userpass.endsWith("\n")) {
            userpass = userpass.substring(0, userpass.length - 1)
        }
        return userpass.length
    }

    private suspend fun cheat() {
        status()
        if (flags[WIZARD] == 0) return

        myPrintf("LFPVSOM> ")
        val cbuff = getLine().trim().uppercase()
        if (cbuff.contains("L")) {
            val x1 = values[FIRSTX]
            val y1 = values[FIRSTY]
            myPrintf("Location co-ordinates: x=%d y=%d z=%d, ", x, y, z)
            myPrintf("Home base x=%d, y=%d, z=1 ", x1, y1)
            myPrintf("Moves=%d\n", moveno)
            myPrintf("Can move : ")
            for (j in 1 until 15) {
                if (ways[j] == 1) myPrintf("%s ", dicact[j])
            }
            myPrintf(" last action=[%s] last object=[%s]\n", lastaction, lastobject)
        }
        if (cbuff.contains("F")) {
            myPrintf("Flags: Home=%d In tent=%d ", flags[HOME], flags[INTENT])
            myPrintf("Dark=%d, Night=%d, Lamp on=%d\n", flags[DARK], flags[NIGHT], flags[LAMPON])
        }
        if (cbuff.contains("P")) {
            myPrintf("Plain: Rolling=%d Grass=%d", flags[ROLING], flags[HIGRAS])
            myPrintf(" Animal=%d Herd=%d", flags[ANIMAL], flags[HERD])
            myPrintf(" Carnivores=%d Seen=%d\n", flags[CARNIV], flags[SEEN])
        }
        if (cbuff.contains("V")) {
            myPrintf("Values: ")
            myPrintf("Daytime=%d ", values[DAYTIME])
            myPrintf("Dawn=%d ", values[DAWN])
            myPrintf("Dusk=%d ", values[DUSK])
            myPrintf("Held=%d ", values[HELD])
            myPrintf("Dwarf num=%d ", values[DWFNUM])
            myPrintf("Dwarf now=%d ", values[DWFNOW])
            myPrintf("\n")
            for (i in 1 until MAXMON) myPrintf("%s=%d ", beasts[i], monstr[VAMPIRE])
            myPrintf("\n")
        }
        if (cbuff.contains("S")) {
            myPrintf("Set X, Y Z co-ordinates: ")
            val input = getLine().trim()
            val parts = input.split(Regex("\\s+")).filter { it.isNotEmpty() }
            if (parts.size >= 3) {
                x = parts[0].toIntOrNull() ?: x
                y = parts[1].toIntOrNull() ?: y
                z = parts[2].toIntOrNull() ?: z
                here = locate(x, y, z)
                describe(1)
            }
            return
        }
        if (cbuff.contains("O")) {
            while (true) {
                myPrintf("Object > ")
                val objName = getLine().trim().uppercase()
                if (objName.isEmpty()) return
                var found = false
                for (i in 1 until MAXOBJ) {
                    if (thing[i].take(3) == objName.take(3)) {
                        objloc[i] = 0
                        values[HELD]++
                        gone[i] = 0
                        secure[i] = 0
                        obhere[i] = 0
                        found = true
                        break
                    }
                }
                if (!found) {
                    myPrintf("No such object!\n")
                }
            }
        }
        if (cbuff.contains("M")) {
            while (true) {
                myPrintf("Monster > ")
                val monName = getLine().trim().uppercase()
                if (monName.isEmpty()) return
                var found = false
                for (i in 1 until MAXMON) {
                    if (beasts[i].take(3) == monName.take(3)) {
                        monstr[i] = 1
                        found = true
                        break
                    }
                }
                if (!found) {
                    myPrintf("No such monster!\n")
                }
            }
        }
        showtext()
    }
    private fun parse(): Int {
        action = ""
        `object` = ""
        val len = inbuff.length
        if (len == 0) return 0
        
        val rawTokens = inbuff.split(Regex("[ *!]")).filter { it.isNotEmpty() }
        for (tok in rawTokens) {
            swearbox(tok)
        }
        
        if (rawTokens.isEmpty()) return 0
        
        if (rawTokens[0] == "GO") {
            action = rawTokens.getOrNull(1) ?: ""
            `object` = rawTokens.getOrNull(2) ?: ""
        } else {
            action = rawTokens[0]
            `object` = rawTokens.getOrNull(1) ?: ""
        }
        return rawTokens.size
    }

    private fun myExit(code: Int) {
        myPrintf("\nChimaera adventure will restart in 2 seconds...\n")
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            // ignore
        }
        inputQueue.clear()
        start()
        throw InterruptedException("Game Restarted")
    }

    private fun showtext() {
        val lines = display.split("\n")
        for (line in lines) {
            val words = line.split(Regex("\\s+")).filter { it.isNotEmpty() }
            var currentLine = StringBuilder()
            for (word in words) {
                if (currentLine.length + word.length + 1 > DISPWIDTH - 15) {
                    myPrintf("%s\n", currentLine.toString())
                    currentLine = StringBuilder()
                }
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            }
            if (currentLine.isNotEmpty()) {
                myPrintf("%s\n", currentLine.toString())
            }
        }
        display = ""
    }

    private fun tnoua(line: String) {
        display += line
    }

    private fun tnou(line: String) {
        display += line + " \n "
    }

    private fun tnoint(n: Long) {
        display += n.toString()
    }

    private fun tnoint(n: Int) {
        display += n.toString()
    }

    private fun tnoulca(word: String) {
        display += word.lowercase()
    }

    private fun tonl(n: Int) {
        for (i in 0 until n) {
            display += " \n "
        }
    }

    private suspend fun sleep(i: Int) {
        for (j in 0 until i) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                // ignore
            }
            tnoua(".")
            showtext()
        }
    }

    // Save and Restore logic in pure Kotlin
    private suspend fun savegame() {
        tnoua("Enter the filename: ")
        showtext()
        var filename = getLine().trim()
        if (filename.endsWith("\n")) {
            filename = filename.substring(0, filename.length - 1).trim()
        }
        val cleanFilename = "$filename.chimaera"
        showtext()
        
        val savefile = File(backupDirectory, cleanFilename)
        try {
            savefile.bufferedWriter().use { writer ->
                writer.write(version)
                
                values[1] = moveno
                values[2] = score
                values[3] = x
                values[4] = y
                values[5] = z
                
                var sum: Long = 0
                
                writer.write("\nA: ")
                for (i in 1 until MAXVAL) {
                    writer.write("${values[i]} ")
                    sum += kotlin.math.abs(values[i])
                }
                
                writer.write("\nB: ")
                for (i in 1 until MAXFLAG) {
                    writer.write("${flags[i]} ")
                    sum += kotlin.math.abs(flags[i])
                }
                
                writer.write("\nC: ")
                for (i in 1 until MAXOBJ) {
                    writer.write("${objloc[i]} ")
                    sum += kotlin.math.abs(objloc[i])
                }
                
                writer.write("\nD: ")
                for (i in 1 until MAXOBJ) {
                    writer.write("${secure[i]} ")
                    sum += kotlin.math.abs(secure[i])
                }
                
                writer.write("\nE: ")
                for (i in 1 until MAXMON) {
                    writer.write("${monstr[i]} ")
                    sum += kotlin.math.abs(monstr[i])
                }
                
                writer.write("\nF: ")
                val remainder = (sum % 123).toInt()
                writer.write("$remainder ")
                for (i in 1 until 9) {
                    writer.write("${rnd(123) + remainder} ")
                }
            }
            tnoua("Game saved in file ")
            tnou(cleanFilename)
            showtext()
        } catch (e: Exception) {
            tnou("Unable to open the output file!")
            showtext()
        }
    }

    private suspend fun restore() {
        tnoua("Enter the filename: ")
        showtext()
        var filename = getLine().trim()
        if (filename.endsWith("\n")) {
            filename = filename.substring(0, filename.length - 1).trim()
        }
        val cleanFilename = "$filename.chimaera"
        showtext()
        
        val savefile = File(backupDirectory, cleanFilename)
        if (!savefile.exists()) {
            tnou("Unable to open the restore file!")
            showtext()
            return
        }
        myPrintf("Restoring...\n")
        try {
            savefile.bufferedReader().use { reader ->
                val line1 = reader.readLine()
                if (line1 != version) {
                    tnou("Sorry, the game was saved from a different version of Chimaera and cannot be restored.")
                    showtext()
                    return
                }
                
                var sum: Long = 0
                val rvalues = LongArray(MAXVAL)
                val rflags = IntArray(MAXFLAG)
                val robjloc = IntArray(MAXOBJ)
                val rsecure = IntArray(MAXOBJ)
                val rmonstr = IntArray(MAXMON)
                
                // Read values[] - line begins with A:
                val lineA = reader.readLine()
                if (lineA == null || !lineA.startsWith("A:")) {
                    tnou("Error 1 in file, cannot restore! ")
                    showtext()
                    return
                }
                val tokensA = lineA.substring(2).trim().split(Regex("\\s+"))
                for (i in 1 until MAXVAL) {
                    rvalues[i] = tokensA[i - 1].toLong()
                    sum += kotlin.math.abs(rvalues[i])
                }
                
                // Read flags[] - line begins with B:
                val lineB = reader.readLine()
                if (lineB == null || !lineB.startsWith("B:")) {
                    tnou("Error 2 in file, cannot restore! ")
                    showtext()
                    return
                }
                val tokensB = lineB.substring(2).trim().split(Regex("\\s+"))
                for (i in 1 until MAXFLAG) {
                    rflags[i] = tokensB[i - 1].toInt()
                    sum += kotlin.math.abs(rflags[i])
                }
                
                // Read objloc[] - line begins with C:
                val lineC = reader.readLine()
                if (lineC == null || !lineC.startsWith("C:")) {
                    tnou("Error 3 in file, cannot restore! ")
                    showtext()
                    return
                }
                val tokensC = lineC.substring(2).trim().split(Regex("\\s+"))
                for (i in 1 until MAXOBJ) {
                    robjloc[i] = tokensC[i - 1].toInt()
                    sum += kotlin.math.abs(robjloc[i])
                }
                
                // Read secure[] - line begins with D:
                val lineD = reader.readLine()
                if (lineD == null || !lineD.startsWith("D:")) {
                    tnou("Error 4 in file, cannot restore! ")
                    showtext()
                    return
                }
                val tokensD = lineD.substring(2).trim().split(Regex("\\s+"))
                for (i in 1 until MAXOBJ) {
                    rsecure[i] = tokensD[i - 1].toInt()
                    sum += kotlin.math.abs(rsecure[i])
                }
                
                // Read monstr[] - line begins with E:
                val lineE = reader.readLine()
                if (lineE == null || !lineE.startsWith("E:")) {
                    tnou("Error 5 in file, cannot restore! ")
                    showtext()
                    return
                }
                val tokensE = lineE.substring(2).trim().split(Regex("\\s+"))
                for (i in 1 until MAXMON) {
                    rmonstr[i] = tokensE[i - 1].toInt()
                    sum += kotlin.math.abs(rmonstr[i])
                }
                
                // Examine checksum - line begins with F:
                val lineF = reader.readLine()
                if (lineF == null || !lineF.startsWith("F:")) {
                    tnou("Error 6 in file, cannot restore! ")
                    showtext()
                    return
                }
                val tokensF = lineF.substring(2).trim().split(Regex("\\s+"))
                val checksum1 = tokensF[0].toInt()
                val checksum2 = (sum % 123).toInt()
                if (checksum1 != checksum2) {
                    tnou("Error 7 in file, cannot restore! ")
                    showtext()
                    return
                }
                
                // All is OK, restore the actual values
                for (i in 1 until MAXVAL) values[i] = rvalues[i].toInt()
                for (i in 1 until MAXFLAG) flags[i] = rflags[i]
                for (i in 1 until MAXOBJ) objloc[i] = robjloc[i]
                for (i in 1 until MAXOBJ) secure[i] = rsecure[i]
                for (i in 1 until MAXMON) monstr[i] = rmonstr[i]
                
                srand(values[SEED])
                val n = values[RNDCOUNT]
                values[RNDCOUNT] = 0
                for (i in 1 until n) rnd(2)
                
                moveno = values[1]
                score = values[2]
                x = values[3]
                y = values[4]
                z = values[5]
                here = locate(x, y, z).toInt()
            }
            tnoua("Game restored from ")
            tnou(cleanFilename)
            tonl(1)
            tnoua("Whilst you were away the elves may have moved a few things around a bit but ")
            tnou("apart from that everything should be as you left it. ")
            tonl(2)
            showtext()
            describe(1)
            showthings()
            mon_start()
            monsters()
        } catch (e: Exception) {
            tnou("Error in file, cannot restore! ")
            showtext()
        }
    }

    // --- GAME ENGINE LOGIC ---
private suspend fun runGame() {
        var i = 0; var j = 0; var k = 0; var len = 0; var iresp = 0; var itest = 0; var forever = 0
        var seed = 0                          /* Seed for srand()             */
        var itmp = 0; var jtmp = 0; var ktmp = 0              /* Temporary variables          */
        var apoint = 0                        /* Index to dicact[]            */
/*    int tpoint;                           Index to thing[]             */
        var opoint = 0                        /* Index to `object`              */
        var mpoint = 0                        /* Index to move                */
        var monstpoint = 0                    /* Index to monster             */

    forever = 1;              /* Dummy condition for eternal while loop */
    display = "";        /* Clear display buffer */



/*---- Display the welcome screen and initialise all arrays ----*/
    welcome();
    initialise();               /* Initialise arrays */
    worth();                    /* Initialise `object` values */
    news();                     /* Display the news screen at random 1:4 */
    tonl(1);

/*---- Display the instructions if asked to do so ----*/
    tnoua("Do you want additional instructions? ");
    showtext();
    if (yesno() == 1) {
        instructions(0);
        tnoua("Continue? ");
        showtext();
        while (yesno() != 1) {}
    }
    hint("");
    tonl(1);

/*---- Establish the starting point by setting advno ----*/
    tnou("There are eleven starting points, 1-10 are standard, 0 is random. ");
    showtext();
    do {
        itest = 1;
        tnoua("Choose one (0 - 10) ");
        showtext();
        len = get_line();        /* Get input line and convert it to upper case */
        if (len == 0) {
            itest = 0;
            continue;
        }
        if (!inbuff[0].isDigit()) {
            itest = 0;
            continue;
        }
        advno = inbuff.toIntOrNull() ?: 0;
        if (advno < 0) {
            itest = 0;
            continue;
        }
        if (advno > 10) {
            itest = 0;
            continue;
        }
    } while (itest == 0);

/*---- Seed the random number generator --------------------------------*/
/* Use the system time to calculate a seed for the random adventure     */

    if (advno == 0) {
        seed = ((System.currentTimeMillis() % 60) + 1).toInt();
    } else seed = advno;


    srand(seed);                  /* Seed random number generator  */
    //values[SEED] = rand();        /* use it to get a random number */
    values[SEED] = Random(99);    /* use it to get a random number */ // luiz
    srand(values[SEED]);          /* and reseed the generator      */
    dicact[35] = dicact[35].substring(0, 4) + dicact[38][1].toChar() + dicact[35].substring(4 + 1)

    locwun();         /* Initialise `object` locations */

    values[DWFNUM] = rnd(4) + 2;    /* Initialise the  dwarf population   */
    values[MONCOUNT] = 0;           /* Monsters not active yet            */

/*---- Get date and day of month and print them ------------------------*/

    values[MONTH] = rnd(12);                    /* Set the month,                */
    values[DAWN] = dawn[values[MONTH]];         /* Set the time of dawn          */
    values[DUSK] = dusk[values[MONTH]];         /* Set the time of dusk          */
    values[DAY] = rnd(mdays[values[MONTH]]);    /* Set the day                   */
    values[WEEKDAY] = rnd(7);                   /* and day of the week           */
    tonl(1);
    tnoua("It is ");
    itmp = rnd(20);
    when (itmp) {
            13 -> {
flags[FR13] = 1;
            values[DAY] = 13;
            values[WEEKDAY] = 5;
            tnoua("Friday 13th ");
            tnoua(months[values[MONTH]]);
            }
            10 -> {
flags[HALLOW] = 1;
            values[DAY] = 31;
            values[MONTH] = 10;
            tnoua(weekdays[values[WEEKDAY]]);
            tnoua(" 31st October, Halloween! ");
            }
            else -> {
tnoua(weekdays[values[WEEKDAY]]);
            tnoua(" ");
            tnoint(values[DAY]);
            when (values[DAY]) {
            1 -> {

            }
            21 -> {

            }
            31 -> {
tnoua("st");
            }
            2 -> {

            }
            22 -> {
tnoua("nd");
            }
            3 -> {

            }
            23 -> {
tnoua("rd");
            }
            else -> {
tnoua("th");
            }
        }
            tnoua(" ");
            tnoua(months[values[MONTH]]);
            }
        }
    tnou(". The sun shines blood red through the dawn! ");
    showtext();
    values[DAYTIME] = values[DAWN];
    dicact[35] = dicact[35].substring(0, 3) + dicact[15][1].toChar() + dicact[35].substring(3 + 1)
    flags[NIGHT] = 0;
/*---- Calculate the co-ordinates of the starting point and base camp --*/
    if (advno != 0) {
        x = advno * 10;
        y = 1500 / x;          /* Standard location */
    } else {
        x = rnd(15) * 15 + 15;
        y = 2000 / x;   /* Random location */
    }
    z = 1;
    here = locate(x, y, z);
    values[FIRSTX] = x + rnd(10) - 5;
    values[FIRSTY] = y + rnd(10) - 5;
    dicact[35] = dicact[35].substring(0, 1) + dicact[26][3].toChar() + dicact[35].substring(1 + 1)
    values[BASE] = locate(values[FIRSTX], values[FIRSTY], 1);

/*--- First move, set the move counter and print the first move text ---*/
    moveno = 0;
    dicact[35] = dicact[35].substring(0, 0) + dicact[47][3].toChar() + dicact[35].substring(0 + 1)
    values[HELD] = 0;
    values[MAGONE] = 1;
    values[LIGHT] = 100;
    cow = rnd(8);
    cat = rnd(3);
    dicact[35] = dicact[35].substring(0, 2) + dicact[18][2].toChar() + dicact[35].substring(2 + 1)
    values[LUCK] = rnd(40);
    if (flags[FR13] == 1) values[LUCK] = rnd(10);
    for (i in 1 until 15) {
        if (i < 9) ways[i] = 1; else ways[i] = 0;
    }
    tonl(1);
    tnoua("You are standing on a wide grassy plain, far off the snow clad ");
    tnoua("tops of distant mountains gleam in the rays of the rising sun. ");
    tnoua("Isolated trees are dotted about the landscape and groups ");
    tnou("of animals can dimly be seen moving about some way off. ");
    if (values[BASE] == here) {
        tonl(1);
        tnou("You are at base camp, your tent stands close by. ");
        flags[HOME] = 1;
    }
    showtext();

    values[HINTPTR] = 1;

/*---- All set up, now for the main loop -------------------------------*/
    while (forever == 1) {
        lastaction = ""
        lastobject = ""
        lastaction += action;                            /* Remember the last action verb */
        lastobject += `object`;                            /* Remember the last `object` noun */

        /*---- Make sure everything is set up correctly -----------------*/
        if (flags[INTENT] != 0) {
            for (i in 1 until 15) ways[i] = 0;
            ways[OUT] = 1;
        }

        if (flags[AUTOHINT] != 0) {
            if (rnd(5) == 1 && hintsdone[values[HINTPTR]] == 0) hint("");  /* Maybe show the current hint   */
        }
        tonl(1);
        tnoua("> ");                                       /* Get and parse an input line */
        showtext();
        len = get_line();
        if (len == 0) continue; else i = parse();             /* Loop if input is null */

        /*---- Check the input against the dictionary ----*/
        monstpoint = 0; opoint = 0; apoint = 0;
/*       tpoint = 0;  */
        if (action.length > 0) {
            for (i in 1 until MAXACT) {
                if (dicact[i].contains(action)) apoint = i;
                if (apoint > 0) break;
            }
/*             If action[] is an `object`[], reset `object`[] and nullify action[]                    */
/*          CODE COMMENTED OUT PRO TEM                                                            */
/*          if (`object`.length == 0)                                                              */
/*            {                                                                                   */
/*             for (i in 1 until MAXOBJ)                                                             */
/*               {                                                                                */
/*                if (thing[i].contains(action)) tpoint = i;                                */
/*                if (tpoint > 0 ) break;                                                         */
/*               }                                                                                */
/*             if (tpoint > 0)                                                                    */
/*               {                                                                                */
/*                `object` = "" `object` += action; action = ""  */
/*               }                                                                                */
/*            }                                                                                   */
        }
        if (`object`.length > 0) {
            for (i in 1 until MAXOBJ) {
                if (thing[i].contains(`object`)) opoint = i;
                if (opoint > 0) break;
            }
        } else opoint = 0;
        /* Is the `object` a monster? */
        if (`object`.length > 0) {
            for (i in 1 until MAXMON) {
                if (beasts[i].contains(`object`)) monstpoint = i;
                if (monstpoint > 0) break;
            }
        } else monstpoint = 0;
        apoint = apoint % MOVMOD;
        if (apoint + opoint + monstpoint == 0) {
            unknown();    /* Unknown response from the player */
        } else {
            mpoint = apoint;
            if (mpoint > 0 && mpoint < 13) {
                move(mpoint);
                if (described == 0) {
                    describe(mpoint);              /* Describe where we are             */
                    showthings();                  /* Describe any objects found here   */
                    mon_start();                   /* Activate monsters                 */
                    monsters();                    /* Display active monsters           */
                }
            } else {
                if (apoint != 15 && monstr[DRAGON] > 0) {
                    tnou("The dragon breathes a blast of fire, burning you to a frazzle! ");
                    showtext();
                    dead();
                    continue;
                }
                when (apoint) {
            13 -> {
onlamp();
            }
            14 -> {
take(opoint);
            }
            15 -> {
kill_it(monstpoint);
            }
            17 -> {
look();
            }
            18 -> {
chase();
            }
            19 -> {
shangrila();
            }
            21 -> {
eat(opoint);
            }
            22 -> {
cheat();
            }
            24 -> {
throw_obj(opoint);
            }
            25 -> {
wave(opoint);
            }
            26 -> {
stamp(monstpoint);
            }
            27 -> {
put(apoint, opoint);
            }
            28 -> {
put(apoint, opoint);
            }
            29 -> {
offlamp();
            }
            30 -> {
rub(opoint);
            }
            31 -> {
fill(opoint);
            }
            32 -> {
play(opoint);
            }
            33 -> {
readit(opoint);
            }
            34 -> {
find(opoint);
            }
            35 -> {
magic();
            }
            36 -> {
inventory();
            }
            37 -> {
help();
            }
            38 -> {
scores();
            }
            39 -> {
quit(1);
            }
            40 -> {
drink(opoint);
            }
            41 -> {
openit(opoint);
            }
            42 -> {
closeit(opoint);
            }
            43 -> {
lockit(opoint);
            }
            44 -> {
unlock(opoint);
            }
            45 -> {
callhim();
            }
            46 -> {
instructions(0);
            }
            47 -> {
plugh();
            }
            48 -> {
gamic();
            }
            49 -> {
slumber();
            }
            50 -> {
timdat(z);
            }
            51 -> {
lookwhere("your base camp");
            }
            52 -> {
adventure();
            }
            53 -> {
examine(opoint);
            }
            54 -> {
hint(`object`);
            }
            58 -> {
savegame();
            }
            59 -> {
restore();
            }
        }
            }
        }
    }
    return;

        myExit(0)

    }

private suspend fun describe(mpoint: Int) {
        var mpoint = mpoint
        var i = 0; var bit18 = 0          /* Mask for tree logic */
        var ddown = 0          /* Mask for hole logic */
        var below = 0         /* Test location underneath for UP */

/*---- Variables for establishing underground move directions -----*/
        var waynum = 0; var waytot = 0; var xnext = 0; var ynext = 0; var znext = 0         /* z of possible next direction              */
        var word1 = 0; var word2 = 0; var word3 = 0; var word4 = 0; var word5 = 0; var word6 = 0         /* Word pointers        */
        var sumxyz = 0; var count = 0; var wayless = 0
        var there = 0; var bitcheck = 0; var moveok = 0; var xyz = 0; var tenbit = 0       /* Bit pattern for matching                  */


    if (here == 0) /*---- First Shangri La, a special place ------------*/
    {
        tnoua("You go up the steps and through the doorway, inside is a small chamber. ");
        tnoua("A sign labelled \"To Shangri La\" points to a spiral staircase leading ");
        tnoua("to the top of the tower. As you climb the stairs all sense of time ");
        tnoua("and space vanishes like a dream. Mists arise and a dim blue light ");
        tnoua("surrounds you. Eventually you arrive at a mystic place, where everything ");
        tnoua("is pervaded by a sense of serenity and utter peace. You sit down ");
        tnou("in the lotus position and fall into a deep meditative trance. ");
        showtext();
        sleep(10);
        tonl(1);
        tnoua("At dawn the next day you awaken feeling refreshed and reinvigourated ");
        tnou("and are surprised to find yourself inside the tent at your base camp. ");
        x = values[FIRSTX];
        y = values[FIRSTY];
        z = 1;
        here = locate(x, y, z);
        flags[INTENT] = 1;
        flags[HOME] = 1;
        newday();                                     /* Increment the date */
        values[DAYTIME] = values[DAWN];               /* and set the time   */
        for (i in 1 until 15) ways[i] = 0;
        ways[OUT] = 1;
        score += 25;
        showtext();
        return;
    }

    when (z) {
            0 -> {
/*---- Up a tree --------------------------------------*/
            for (i in 1 until 15) ways[i] = 0;
            ways[DOWN] = 1;
            if (flags[DARK] != 0) {
                tnou("It is very dark, if you move about you are likely to fall. ");
            }
            if (flags[NIGHT] != 0) {
                tnoua("You are at the top of the tree but your lamp is not powerful ");
                tnou("enough for you to see anything except your immediate surroundings. ");
            } else {
                tnoua("From the top of the tree you can see miles in every direction. The ");
                tnoua("mountains are still a long way off and there is no chance of reaching ");
                tnoua("them before nightfall. ");
                lookwhere(" a large clearing in the forest");
                if (distance >= 3) {
                    tnoua(" A thin spiral of smoke rises from the clearing but you are too far");
                    tnou(" away to see anything else. ");
                } else {
                    tnou("You can see your base camp in the clearing. ");
                }
            }
            if (flags[SEEN] != 0 && flags[CARNIV] != 0) {
                when (cat) {
            LEOPARD -> {
tnoua(" A leopard climbs up the tree after you. You try to escape ");
                        tnoua("by crawling out along a branch but it is rotten and snaps. ");
                        tnou("You fall to the ground and break your neck! ");
                        showtext();
                        dead();
            }
            else -> {
tnoua(" The ");
                        tnoua(cats[cat]);
                        tnoua(" prowls by the foot of the tree without seeing you. It eventually wanders off. ");
                        flags[SEEN] = 0;
                        flags[CARNIV] = 0;
                        score++;                  /* Small reward for avoiding cat */
            }
        }
            }
            }
            1 -> {
/*---- At ground level --------------------------------*/
            if (flags[NIGHT] == 0) flags[DARK] = 0;
            for (i in 1 until 15) { if (i < 9) ways[i] = 1; else ways[i] = 0; }
            if (here == values[BASE]) flags[HOME] = 1; else flags[HOME] = 0;
            flags[TREE] = 0;
            if (mpoint == 0) return;  /* Skip text */

            /*---- Special case, you can go UP to Shangri La ----------------*/
            if (x == 0 && y == 0) {
                tnoua("You are standing in a small clearing in the centre of which stands a tall tower. ");
                tnoua("A flight of steps leads up to a doorway through which shines a dim blue ");
                tnou("light, illuminating the bottom of a spiral stair case. ");
                ways[UP] = 1;
                showtext();
                return;
            }
            /*---- Special case, at your base camp --------------------------*/
            if (flags[HOME] != 0) {
                if (flags[INTENT] == 0) {
                    tonl(1);
                    tnou("You are at base camp, your tent stands close by. ");
                    for (i in 1 until 9) ways[i] = 1;
                    ways[IN] = 1;
                    showtext();
                    return;
                } else {
                    tnoua("You are in the tent, a few torn and useless clothes lie scattered ");
                    tnou("about on your camp bed. ");
                    for (i in 1 until 15) ways[i] = 0;
                    ways[OUT] = 1;
                    showtext();
                    return;
                }
            }
            /*---- Is there a tree or hole in ground ------------------------*/
            bit18 = here  and  0x12;
            if (bit18 == 18 && flags[HOME] == 0)          /* There could be a tree here */
            {
                flags[TREE] = 1;
                ways[UP] = 1;
            }
            ddown = waybit[DOWN];                     /* Is there a hole here? */
            if ((here  and  ddown) == ddown) ways[DOWN] = 1;
            if (flags[HOME] != 0)                          /* No tree or hole at base camp */
            {
                flags[TREE] = 0;
                ways[UP] = 0;
                ways[DOWN] = 0;
            }
            if (ways[DOWN] != 0) {
                flags[TREE] = 0;
                ways[UP] = 0;
            }    /* No tree by a hole */

            /*---- Establish the terrain and whether there are animals about */
            if (rnd(8) == 5) flags[ANIMAL] = 1;  /* 1 in 8 chance of seeing an animal */
            flags[HERD] = 1;
            if (rnd(4) == 2) flags[HERD] = 0;
            flags[CARNIV] = rnd(5);              /* 1 in 5 chance of carnivore */
            flags[ROLING] = 0;
            if ((here  and  72) != 72) flags[ROLING] = 1;
            flags[HIGRAS] = 0;
            if ((here  and  40) == 40) {
                flags[HIGRAS] = 1;
                flags[ROLING] = 0;
            }
            if (flags[ANIMAL] == 0) /* Set animal species if none is active at present */
            {
                cow = rnd(7);   /* Herbivore */
                cat = rnd(4);   /* Carnivore */
            }

            /*---- Everything set up, display the text ----------------------*/

            if (flags[NIGHT] == 0) /*---- Day, the player can see everywhere ----*/
            {
                tnoua("You are standing on the plain");
                if (flags[ROLING] != 0) {
                    tnoua(", rolling grassland stretches to the horizon");
                }
                if (flags[HIGRAS] != 0) {
                    tnoua(", the grass is very high here");
                    if (flags[ANIMAL] != 0) {
                        tnoua(" and you can hear large animals moving about");
                        if (rnd(3) == 1) {
                            tnoua(". You have the feeling that you are being ");
                            if (rnd(3) == 1) tnoua("watched"); else tnoua("tracked ");
                            tnoua(" by something large and hungry");
                        }
                    } else {
                        if (ways[DOWN] != 0 && flags[RABBIT] != 0 && (rnd(3) == 2)) {
                            tnoua(". A white rabbit scurries out of some long grass, consults his pocket-watch, exclaims ");
                            tnoua("\"Oh my ears and whiskers!\" and disappears down a nearby hole");
                            flags[RABBIT] = 0;
                        }
                    }
                }
                if (flags[ANIMAL] != 0 && flags[HIGRAS] == 0) {
                    if (flags[HERD] != 0) {
                        tnoua(", herds of ");
                        when (cow) {
            ANTELOPE -> {
tnoua("antelope");
            }
            WILDEBEEST -> {
tnoua("wildebeest");
            }
            ZEBRA -> {
tnoua("zebra");
            }
            GAZELLE -> {
tnoua("gazelles");
            }
            DEER -> {
tnoua("deer");
            }
            ELEPHANT -> {
tnoua("elephants");
            }
            BUFFALO -> {
tnoua("buffalo");
            }
            else -> {
tnoua("animals");
            }
        }
                    } else {
                        tnoua(", isolated ");
                        when (cow) {
            ANTELOPE -> {
tnoua("antelope");
            }
            WILDEBEEST -> {
tnoua("wildebeest");
            }
            ZEBRA -> {
tnoua("zebra");
            }
            GAZELLE -> {
tnoua("gazelles");
            }
            DEER -> {
tnoua("deer");
            }
            ELEPHANT -> {
tnoua("elephants");
            }
            BUFFALO -> {
tnoua("buffalo");
            }
            else -> {
tnoua("animals");
            }
        }
                    }
                    tnoua(" are roaming about");
                }
                tnoua(". ");
                /*---- Are there carnivores about and do they attack? -----------*/
                if (rnd(5) == 1) flags[CARNIV] = 1;                                  /* ALREADY SET - DO WE NEED THIS? */
                if (rnd(8) == 3) flags[CARNIV] = 0;
                if (flags[UNDEAD] != 0) flags[CARNIV] = 0;      /* No cats if the player is undead */
                if (rnd(4) == 2) flags[ANIMAL] = 0;        /* All animals disappear           */
                if (flags[CARNIV] != 0)                         /* There is a carnivore here       */
                {
                    if (flags[SEEN] != 0)                        /* It has seen you                 */
                    {
                        if (rnd(5) == 1 && flags[HIGRAS] != 0)   /* and attacks (only in high grass */
                        {
                            tnoua(" A ");
                            when (cat) {
            LEOPARD -> {
tnoua("leopard");
            }
            TIGER -> {
tnoua("tiger");
            }
            LION -> {
tnoua("lion");
            }
            LYNX -> {
tnoua("lynx");
            }
        }
                            tnoua(" leaps at you from amongst the long grass. ");
                            if (cat == LYNX || values[LUCK] > 20) {
                                tnou("You avoid it adroitly and it slinks away.");
                                values[RUNOUT] = moveno;
                                values[LUCK] -= 5;
                            } else {
                                tnou("It gets you!");
                                showtext();
                                dead();
                                return;
                            }
                        } else {
                            if (rnd(5) == 2) {
                                flags[SEEN] = 1;
                                tnoua("A ");
                                when (cat) {
            LEOPARD -> {
tnoua("leopard");
            }
            TIGER -> {
tnoua("tiger");
            }
            LION -> {
tnoua("lion");
            }
            LYNX -> {
tnoua("lynx");
            }
        }
                                tnoua(" eyes you hungrily from some long grass.");
                            }
                        }
                    }
                }

                if (flags[TREE] != 0) {
                    tnoua(" There is a tall tree standing nearby. ");
                    if (rnd(5) == 3 && flags[SHANGRI] == 0) {
                        tnou("A notice pinned to the trunk reads: ");
                        tnou("\tFor a once in a lifetime experience visit Shangri La! ");
                        tnou("\t[For details call Shangri Enterprises] ");
                    }
                }
                if (ways[DOWN] != 0) {
                    below = locate(x, y, z + 1);
                    flags[NOTUP] = 1;
                    if ((below  and  waybit[UP]) == waybit[UP]) flags[NOTUP] = 0; /* He can get out again */
                    tnoua(" There is a deep hole at your feet");
                    if (flags[NOTUP] != 0) {
                        tnoua(", if you go down it you won't be able to get back up");
                    }
                    tnoua(". ");
                }
                tonl(1);
                if ((values[DUSK] - values[DAYTIME]) == 1) tnou("The sun is getting low.");
                if (values[DAYTIME] == values[DUSK]) tnou("The sun is sinking in the west.");
            } else  /*---- Night, can only see if the lamp is lit -------------*/
            {
                if ((values[DAYTIME] - values[DUSK]) == 1) tnoua("The sun has just set, it is night! ");
                if (flags[DARK] == 0) /*---- Night but not dark ------------------*/
                {
                    tnoua("You are standing on the plain. It is night and you can see nothing beyond ");
                    tnou("the small area illuminated by the rays of your lamp. ");
                } else /*---- Night and dark - bad news! -----------------------*/
                {
                    if (ways[DOWN] != 0)                /* Kill the player */
                    {
                        tnou("Stumbling about the plain in the dark, you have fallen into a deep hole and broken your neck! ");
                        showtext();
                        dead();
                        return;
                    } else {
                        tnoua("You are standing on the plain. It is very dark, if you move ");
                        tnou("about you are likely to fall into a pit.");
                    }
                }
            }

            if (moveno > 0 && values[DAYTIME] == values[DAWN]) tnou("The sun is rising in the east.");
            }
            else -> {
/*---- Underground ------------------------------------*/
            /* Calculate the the possible move directions */
            waynum = 0;
            waytot = 0;
            for (i in 1 until 15) ways[i] = 0;  /* First unset all directions */
            for (i in 1 until 11) {
                xnext = x + xinc[i];
                ynext = y + yinc[i];
                znext = z + zinc[i];
                there = locate(xnext, ynext, znext);
                bitcheck = waybit[i];
                moveok = here  and  there  and  bitcheck;
                if (moveok == bitcheck) {
                    ways[i] = 1;
                    waytot++;
                    if (i < 9) waynum++;
                }
            }
            if (waytot == 0) {
                tnoua("You were warned, there is no way out! ");
                tnoua("You are trapped and scrabble desperately at the walls but ");
                tnoua("there is no escape! The air becomes progressively stuffy and ");
                tnou("you begin to gasp for breath . . .");
                sleep(5);
                tnou(". eventually the air runs out and you are finished. ");
                showtext();
                dead();
                return;
            }
            /*---- Calculate the word pointers and display the location ----*/
            if (ways[DOWN] != 0 && flags[DARK] != 0) {
                tnou("Stumbling around in the dark you have fallen down a deep hole and broken your neck! ");
                showtext();
                dead();
                return;
            }
            if (flags[DARK] != 0) {
                tnou("It is pitch dark and you can't see a thing, if you move you are likely to fall into a pit! ");
                showtext();
                return;
            }
            if ((here % 200) <= 10) flags[DRIP] = 1;  /* Water drips */
            xyz = x * y * z;
            tenbit = xyz  and  1023;
            word2 = (kotlin.math.sqrt((tenbit / 4).toDouble()) + 1).toInt();
            word3 = (kotlin.math.sqrt((tenbit / 8).toDouble()) + 1).toInt();
            word4 = (kotlin.math.sqrt(((xyz  and  255).toDouble())) + 1).toInt();
            flags[SWITCH] = 0; flags[LARGE] = 0; flags[SMALL] = 0;

            if ((here  and  512) == 512) flags[SWITCH] = 1;
            if (word2 == 5) flags[SMALL] = 1;
            if (word2 == 7) flags[SMALL] = 1;
            if (word2 == 9) flags[SMALL] = 1;
            if (word2 == 11) flags[SMALL] = 1;
            if (word2 == 13) flags[SMALL] = 1;
            if (word2 == 15) flags[SMALL] = 1;
            if (word2 == 6) flags[LARGE] = 1;
            if (word2 == 8) flags[LARGE] = 1;
            if (word2 == 10) flags[LARGE] = 1;
            if (word2 == 12) flags[LARGE] = 1;
            if (word2 == 14) flags[LARGE] = 1;
            if (word2 == 16) flags[LARGE] = 1;

            word1 = 1;  /* Default value of word1 */
            flags[SHAFT] = 0;
            if (ways[UP] != 0 && ways[DOWN] != 0) flags[SHAFT] = 1;    /* In a shaft */
            if (flags[SHAFT] != 0) {
                word2 = (tenbit % 6) + 11;
                word4 = 17;
                if (flags[SWITCH] != 0) word4 = 18;
                tnoua("You are climbing ");
                tnoua(adject1[word2]);
                tnoua(" ");
                tnoua(nouns[word4]);
                if (flags[SWITCH] != 0) {
                    tnoua(". Stairs ");
                } else {
                    tnoua(". Steps ");
                }
                if (flags[SMALL] != 0) {
                    tnoua("go ");
                } else {
                    tnoua("lead ");
                }
                tnoua("up and down from here. ");
            }

            if (word4 < 5) /* Cells */
            {
                if (flags[SMALL] != 0 && flags[SWITCH] != 0) word1 = 2;
                if (flags[SMALL] != 0 && flags[SWITCH] == 0) word1 = 5;
            }

            if (word4 == 9 || word4 == 11 || word4 == 13 || word4 == 15) /* Passages */
            {
                word1 = 3;
                if (waytot < 2) word4 = word4 + 1;
                if (flags[SMALL] != 0 && flags[SWITCH] != 0) word1 = 5;
                if (flags[SMALL] != 0 && flags[SWITCH] == 0) word1 = 6;
                if (flags[LARGE] != 0 && flags[SWITCH] != 0) word1 = 4;
            }

            if (word4 == 12 || word4 == 14 || word4 == 16) /* Rooms */
            {
                if (flags[SMALL] != 0 && flags[SWITCH] != 0) word1 = 5;
                if (flags[LARGE] != 0 && flags[SWITCH] != 0) word1 = 3;
            }

            if (word4 == 5) if (flags[SMALL] != 0) word1 = 2; /* Alcove */

            /* Caves */
            if (flags[SMALL] != 0 && flags[SWITCH] != 0) word1 = 2;
            if (flags[SMALL] != 0 && flags[SWITCH] == 0) word1 = 6;
            if (flags[LARGE] != 0 && flags[SWITCH] != 0) word1 = 3;

            sumxyz = x + y + z;
            if ((sumxyz % 100) <= 10) flags[GLOW] = 1;
            if (flags[SHAFT] != 0) flags[GLOW] = 0;          /* No glow in shafts */
            flags[WAVER] = if (flags[GLOW] != 0 && (flags[WAVER] != 0 || (rnd(4) == 2))) 1 else 0;
            flags[DARK] = 1;
            if (flags[GLOW] != 0 || flags[LAMPON] != 0) flags[DARK] = 0;
            if (flags[UNDEAD] != 0) flags[DARK] = 0;                 /* The undead can always see! */
            if (flags[GLOW] != 0) tnoua("The whole scene is bathed in an eerie");
            if (flags[WAVER] != 0) tnoua(", wavering");
            if (flags[GLOW] != 0) tnoua(" glow. ");

            if (flags[SHAFT] == 0) {
                tnoua("You are ");
                tnoua(verbs[word1]);
                tnoua(" ");
                tnoua(adject1[word2]);
                tnoua(adject2[word3]);
                tnoua(" ");
                tnoua(nouns[word4]);
                tnoua(". ");
            }
            /*---- Now calculate and display where you can go next. On the level first, stairs later. ----*/
            count = 0;
            for (i in 1 until 9) {
                if (ways[i] != 0) {
                    count++;
                    xnext = kotlin.math.abs(x + xinc[i]);
                    ynext = kotlin.math.abs(y + yinc[i]);
                    znext = kotlin.math.abs(z + zinc[i]);
                    there = locate(xnext, ynext, znext);
                    xyz = xnext * ynext * znext;
                    tenbit = (xyz  and  1023);
                    word5 = (kotlin.math.sqrt((tenbit / 4).toDouble()) + 1).toInt();
                    word6 = (kotlin.math.sqrt((xyz  and  255).toDouble()) + 1).toInt();
                    if (count == 1) tnoua("There is ");
                    tnoua(adject1[word5]);

                    tnoua(" ");
                    tnoua(nouns[word6]);
                    tnoua(" to the ");
                    tnoua(routes[i]);
                    if (count == waynum) break;
                    {
                        wayless = waynum - 1;
                        if (count < wayless) tnoua(", ");
                        if (count == wayless) tnoua(" and ");
                    }
                }
            }
            if (count > 0) tnoua(". ");
            if (flags[SHAFT] == 0)  /* If in a shaft the text has already been displayed */
            {
                if (ways[UP] != 0 || ways[DOWN] != 0) {
                    if (flags[SMALL] == 0 && flags[LARGE] == 0) {
                        tnoua("A steep ladder");
                        flags[SWITCH] = 1;
                    }
                    if (flags[SMALL] != 0 || flags[LARGE] != 0) {
                        if (flags[SMALL] != 0 && flags[SWITCH] != 0) tnoua("A winding stair");
                        if (flags[SMALL] != 0 && flags[SWITCH] == 0) tnoua("Rickety steps");
                        if (flags[LARGE] != 0 && flags[SWITCH] != 0) tnoua("A spiral stair");
                        if (flags[LARGE] != 0 && flags[SWITCH] == 0) tnoua("Broad stairs");
                        if (flags[SWITCH] != 0 && (xyz  and  1024 == 1024)) tnoua("case");
                    }
                    if ((tenbit  and  256) == 256) flags[LEAD] = 1; else flags[LEAD] = 0;
                    if (flags[LEAD] != 0) {
                        tnoua(" lead");
                    } else {
                        tnoua(" disappear");
                    }
                    if (flags[SWITCH] != 0) tnoua("s ");
                    if (ways[UP] != 0) tnoua(" up");
                    if (ways[DOWN] != 0) tnoua(" down");
                    if ((xyz  and  2048) == 2048) tnoua("wards");
                    tnoua(". ");
                }
            }
            if (flags[DARK] == 0) {
                if (flags[DRIP] != 0) {
                    values[POOL] = x % 3;
                    if (flags[SHAFT] != 0) values[POOL] = 0;
                    tnoua("Water drips from above");
                    when (values[POOL]) {
            0 -> {
tnoua(". ");
            }
            1 -> {
tnoua(", disappearing into small fissures in the floor. ");
            }
            2 -> {
tnoua(", collecting in a small pool at your feet. ");
            }
        }
                }
            }
            }
        }
    if (values[BRKBOT] == here || values[BRKVAS] == here || values[BRKGOB] == here || values[BRKMIR] == here) {
        tnou("The ground is littered with tiny fragments of glass. ");
    }
    if (flags[LAMPON] != 0)                 /* Is the lamp nearly exhausted? */
    {
        if (values[LIGHT] < 20 && (values[LIGHT] % 4) == 3) {
            tonl(1);
            tnoua("Your lamp is getting dim. ");
        }
        if (values[LIGHT] < 1) {
            tonl(1);
            tnoua("Your lamp flickers and goes out! ");
            flags[LAMPON] = 0;
            values[FLICK]++;
        }
    }
    if (z > 1) {
        if ((here % 50) == 23 && rnd(3) == 1) {
            tonl(1);
            tnou("A hollow voice says \"Plugh!\"");
        }
    }
    if (values[THIRST] > 50 && rnd(6) == 4) {
        tonl(1);
        tnoua("You feel very thirsty");
        if (rnd(2) == 1) tnoua(", maybe a little drink will bring you luck");
        tnou(". ");
    }
    showtext();                       /* Display the text             */
    described = 1;

    }

private fun initialise() {
        var i = 0
    score = 0;
    pseudorand = 1;
    helpno = 0;               /* Help system not used yet */
    values[THIRST] = 0;      /* Not thirsty yet */
    values[BRKBOT] = -1;      /* Location of smashed bottle */
    values[BRKVAS] = -1;      /* Location of smashed vase */
    values[BRKGOB] = -1;      /* Location of smashed goblet */
    values[BRKMIR] = -1;      /* Location of smashed mirror */
    values[DWFNOW] = 0;      /* No dwarves active yet */
    values[GORLOC] = -1;      /* Gorgon has no location yet */
    values[SEED] = -1;      /* Initialise for use as flag first */
    values[FLICK] = 0;      /* Lamp not flickering yet */
    values[RNDCOUNT] = 0;     /* No calls to rnd(n) yet */

    flags[WIZARD] = 0;   /* The player is not a wizard              */
    flags[COPRNT] = 0;   /* ? not used ?                            */
    flags[FR13] = 0;   /* It is not Friday 13th                   */
    flags[HALLOW] = 0;   /* It is not Halloween                     */
    flags[LIFE] = 1;   /* ? not used ?                            */
    flags[HOME] = 0;   /* The player is not at base camp          */
    flags[DARK] = 0;   /* It is not dark                          */
    flags[NIGHT] = 0;   /* It is not night                         */
    flags[LAMPON] = 0;   /* The lamp is not on                      */
    flags[INTENT] = 0;   /* The player is not in his tent           */
    flags[HIGRAS] = 0;   /* The grass on the plain is not high      */
    flags[ANIMAL] = 0;   /* There are no animals in view            */
    flags[HERD] = 1;   /* The herbivores are in a herd            */
    flags[CARNIV] = 0;   /* The carnivores are not active yet       */
    flags[SEEN] = 0;   /* The carnivores have not seen the player */
    flags[BOXLOK] = 1;   /* Pandora's box is locked                 */
    flags[DORLOK] = 1;   /* The door is locked                      */
    flags[UNDEAD] = 0;   /* The player is not yet a vampire         */
    flags[GENIE] = 0;   /* The genie is still in the bottle        */
    flags[ELIXIR] = 0;   /* The elixir of life has not been drunk   */
    flags[EMPTY] = 1;   /* The water bottle is empty               */
    flags[GLOW] = 0;   /* There is no glow in the caves           */
    flags[WAVER] = 0;   /* The glow is not wavering                */
    flags[SHAFT] = 0;   /* The player is not in a shaft            */
    flags[HANDSFULL] = 0;   /* The player's hands are not full         */
    flags[AUTOHINT] = -1;  /* Set for first (free) hint               */
    flags[RABBIT] = 1;   /* The white rabbit has not been seen yet  */
    flags[EXCALIBER] = 1;   /* The first sword picked up is Excaliber  */
    flags[RINGON] = 0;   /* The player is not wearing the ring      */
    flags[SHANGRI] = 0;   /* Shangri La not visited yet in this life */

    for (i in 1 until MAXOBJ) {
        secure[i] = 0;       /* There are no objects in the tent, */
        gone[i] = 0;         /* none have been destroyed          */
        obhere[i] = 0;       /* and the player is holding none    */
    }
    /* The initial state of each monster is -1, this is exchanged for a */
    /* positive value when the monster is activated                     */
    for (i in 1 until MAXMON) monstr[i] = -1;

    /* Each hint can only be shown once */
    for (i in 0 until MAXHINT) hintsdone[i] = 0;

    }

private fun instructions(i: Int): Int {
        var i = i

    tonl(1);
    tnoua("Use simple commands with one or two words to direct me. Thus,  NORTH or GO ");
    tnoua("NORTH or N will move you to the North, assuming that you can go that way. ");
    tnoua("Similarly, GET or TAKE OBJECT will enable you to pick up an OBJECT ");
    tnoua("if it can be carried, provided that you have the strength to lift it. ");
    tnoua("Some other useful commands are INVENTORY, SCORE, TIME, QUIT, LOOK, ");
    tnoua("and WHERE. Most, but not all, commands can be abbreviated but don't ");
    tnoua("overdo this as the results can be unpredictable. Using the first four ");
    tnoua("letters is usually safe enough. The main directions are exceptions to ");
    tnou("the general rule and may be abbreviated to N, S, SE, SW, etc.");
    tonl(1);
    tnoua("Try to get all the treasure you find into the tent at your base camp, but ");
    tnou("be careful, there may be some nasty surprises for you. ");
    tonl(1);
    tnou("See how quickly you can become a Supreme Champion, there is no maximum score. ");
    tonl(1);
    tnou("Good luck, you'll need it!!!");
    if (i == 1) {
        i = 0;
        tonl(1);
        tnoua("n.b. Commands are not case sensitive and may be typed in");
        tnou("UPPER, lower or MiXeD case.");
        tonl(1);
    }
    showtext();
    return (i);

    }

private fun locate(xx: Int, yy: Int, zz: Int): Int {
        var xx = xx; var yy = yy; var zz = zz
        var xyz = 0
    xyz = (xx * yy * zz + xx * 17 + yy * 19 + zz * 23);
    return (xyz);

    }

private fun locwun() {

    /*---- The first ten (utility) objects are placed on the plain ----*/
        var i = 0; var a = 0; var b = 0; var c = 0; var p = 0
    for (i in 1 until 11) objloc[i] = i - 15; /* range -5 to -14 */
    for (i in 0 until 100)                    /* Now scramble them */
    {
        a = rnd(10);
        b = rnd(10);
        c = objloc[a];
        objloc[a] = objloc[b];
        objloc[b] = c;
    }
    /*---- The rest are placed underground ----*/
    for (i in 11 until MAXOBJ) objloc[i] = i - 66; /* range -54 to MAXOBJ-66 */

    p = MAXOBJ - 11;
    for (i in 0 until 20)     /* Scramble some of these (about 20) */
    {
        a = rnd(p) + 10;
        b = rnd(p) + 10;
        c = objloc[a];
        objloc[a] = objloc[b];
        objloc[b] = c;
    }

    for (i in 1 until MAXOBJ)  /* Ensure that objloc[x] is not zero */
    {
        if (objloc[i] == 0) objloc[i] = -rnd(20);
    }

/*    Special objects which do not exist yet  */
    objloc[DIAMOND] = -2000;   /*  Diamond  */
    objloc[BRICKBATS] = -2000;   /*  Brickbats  */
    objloc[GNOME] = -2000;   /*  Gnome  */
    objloc[SERPENTINE] = -2000;   /*  Serpentine  */
    objloc[VENUS] = -2000;   /*  Venus-de-Milo  */

    }

private fun mon_start() {
        var i = 0; var mflag = 0
    /* Do any start now?                 */
    if (flags[UNDEAD] != 0) return;           /* An undead player sees no monsters */
    if (z < 2) return;                   /* No monsters start above ground    */
    if (flags[DARK] != 0) return;             /* or in the dark                    */
    if (values[MONCOUNT] > 3) return;    /* Not more than three at any time   */
    for (i in 1 until MAXMON) {
        when (i) {
            BATS -> {
if (monstr[BATS] > 0) continue;     /* Already active         */
                if (flags[SMALL] == 0) continue;    /* Not in small places    */
                if (flags[GLOW] == 0) continue;     /* or with glowing lights */
                if (rnd(20) == 7) {
                    monstr[BATS] = kotlin.math.abs(monstr[BATS]);
                    mflag++;
                }
            }
            DWARF -> {
if (values[DWFNUM] == 0) continue; /* No dwarves left */
                if (rnd(20) == 9) {
                    monstr[DWARF] = kotlin.math.abs(monstr[DWARF]);
                    mflag++;
                }
            }
            SNAKE -> {
if (monstr[SNAKE] > 0) continue;     /* Already active */
                if (rnd(50) == 42) {
                    monstr[SNAKE] = kotlin.math.abs(monstr[SNAKE]);
                    mflag++;
                }
            }
            GORGON -> {
if (monstr[GORGON] == 0) continue;  /* The gorgon is dead */
                if (flags[LARGE] == 1) continue;    /* Not here */
                if (values[GORLOC] > 0) continue;   /* Got location already */
                if ((here % 100) == 87) {
                    values[GORLOC] = here;
                    monstr[GORGON] = kotlin.math.abs(monstr[GORGON]);
                    mflag++;
                }
            }
            ELF -> {
if (monstr[ELF] > 0) continue;     /* Already active */
                if (rnd(20) == 7) {
                    monstr[ELF] = kotlin.math.abs(monstr[ELF]);
                    mflag++;
                }
            }
            TROLL -> {
if (monstr[TROLL] > 0) continue;     /* Already active          */
                if (flags[GLOW] == 0) continue;      /* Not with glowing lights */
                if (rnd(40) == 23) {
                    monstr[TROLL] = kotlin.math.abs(monstr[TROLL]);
                    mflag++;
                }
            }
            DRAGON -> {
if (monstr[DRAGON] > 0) continue;     /* Already active      */
                if (flags[LARGE] == 0) continue;      /* Place must be large */
                if (monstr[DRAGON] == 1 && rnd(3) != 1) {
                    monstr[DRAGON] = -1;  /* Gives 1/30 chance of first encounter */
                }
                if (rnd(10) == 6) {
                    monstr[DRAGON] = kotlin.math.abs(monstr[DRAGON]);
                    mflag++;
                }
            }
            VAMPIRE -> {
if (monstr[VAMPIRE] > 0) continue;     /* Already active       */
                if (flags[NIGHT] == 0) continue;       /* Only active at night */
                if (rnd(50) == 23) {
                    monstr[VAMPIRE] = kotlin.math.abs(monstr[VAMPIRE]);
                    mflag++;
                }
            }
            else -> {

            }
        }
        if (mflag > 0) continue;
    }
    if (mflag > 0) values[MONCOUNT]++;

    }

private suspend fun monsters() {
        var monsterpoint = 0; var i = 0

    for (monsterpoint in 1 until MAXMON) {
        when (monsterpoint) {
            BATS -> {
if (monstr[BATS] <= 0) continue;            /* The bats are not active */
                if (monstr[VAMPIRE] > 0) continue;          /* and avoid the vampire   */
                if (monstr[DRAGON] > 0) continue;           /* and the dragon          */
                if (flags[NIGHT] == 0) continue;            /* The bats are nocturnal  */
                if (z < 2) continue;
                tonl(1);
                when (monstr[BATS]) {
            1 -> {
tnou("You have disturbed hundreds of roosting bats, they wheel and swoop around you. ");
            }
            2 -> {
tnou("Hundreds of large bats are flying about. ");
            }
            3 -> {
tnou("A cloud of bats sweeps past you and disappears into the darkness. ");
            }
        }
                monstr[BATS] = -rnd(3);
                values[MONCOUNT]--;
            }
            ELF -> {
if (monstr[ELF] <= 0) continue;
                if (z > 1)                     /* Elf only active underground      */
                {
                    tonl(1);
                    if (monstr[ELF] == 1) {
                        tnou("A slender elf strolls past as if looking for something and disappears round a corner. ");
                    } else {
        var objptr = 0
                        for (i in 1 until MAXOBJ) {
                            if (obhere[i] == 1) {
                                objptr = i;
                            }
                        }
                        if (objptr == 0) {
                            tnou("The elf appears, looks about and seeing nothing to interest him wanders off. ");
                        } else {
        var posloc = 0
                            tnoua("The elf appears, says \"Ha, just what I wanted!\" and runs out, taking the ");
                            tnoulca(thing[objptr]);
                            tnou(" with him.");
                            obhere[objptr] = 0;
                            posloc = rnd(60);
                            if ((here % 60) == posloc) posloc++;  /* Make sure it isn't put down here */
                            objloc[objptr] = -posloc;
                        }
                    }
                    monstr[ELF] = -2;
                    values[MONCOUNT]--;
                }
            }
            GORGON -> {
if (monstr[GORGON] <= 0) continue;           /* Gorgon is not active */
                if (values[GORLOC] != here) continue;        /* Gorgon is not here   */
                tonl(1);
                when (monstr[GORGON]) {
            1 -> {
tnoua("A sleeping woman lies chained to a rock, her hair is a seething mass ");
                        tnoua("of snakes. She stirs, awakens and slowly turns towards you. ");
                        monstr[GORGON] = 2;
            }
            else -> {
if (monstr[BATS] > 0) {
                            tnoua("The gorgon glares at the bats wheeling overhead, they instantly ");
                            tnoua("fall to the ground as a shower of brickbats. ");
                            monstr[BATS] = 0;
                            objloc[BRICKBATS] = here;
                            secure[BRICKBATS] = 0;
                            gone[BRICKBATS] = 0;
                            values[MONCOUNT]--;
                        }
                        if (monstr[DWARF] > 0) {
                            if (values[DWFNOW] == 1) {
                                tnoua("The gorgon glares at the dwarf");
                            } else {
                                tnoua("The gorgon glares at one of the dwarves");
                            }
                            tnoua(", turning it into a pottery gnome. ");
                            values[DWFNOW]--;
                            monstr[DWARF]--;
                            values[MONCOUNT]--;
                            objloc[GNOME] = here;
                            secure[GNOME] = 0;
                            gone[GNOME] = 0;
                        }
                        if (monstr[SNAKE] > 0) {
                            tnoua("The gorgon stares hard at the snake, which slowly turns into a block of serpentine. ");
                            monstr[SNAKE] = 0;
                            objloc[SERPENTINE] = here;
                            secure[SERPENTINE] = 0;
                            gone[SERPENTINE] = 0;
                            values[MONCOUNT]--;
                        }
                        if (objloc[MIRROR] == 0) {
                            tnoua("The gorgon catches her own gaze in the mirror and is instantly transformed ");
                            tnoua("into a statue of the Venus de Milo! ");
                            values[GORLOC] = -1;
                            monstr[GORGON] = 0;
                            score += 100;
                            objloc[VENUS] = here;
                            secure[VENUS] = 0;
                            gone[VENUS] = 0;
                            values[MONCOUNT]--;
                        }
                        tnoua("The gorgon glares at you and you are immediately turned into stone!! ");
                        showtext();
                        dead();
                        return;
            }
        }
            }
            SNAKE -> {
if (monstr[SNAKE] <= 0) continue;           /* Snake is not active    */
                if (monstr[VAMPIRE] > 0) continue;          /* and avoids the vampire */
                if (monstr[DRAGON] > 0) continue;           /* and the dragon         */
                tonl(1);
                when (monstr[SNAKE]) {
            1 -> {
tnoua("An enormous snake appears and hisses angrily at you. ");
            }
            2 -> {
tnoua("You are being followed by a large green snake. ");
            }
            3 -> {
tnoua("The snake is getting close and is trying to hypnotise you, I don't give ");
                        tnoua("much for your chances if it succeeds. ");
            }
            4 -> {
tnoua("The snake strikes at you but you leap back just in time. ");
            }
            else -> {
tnoua("The snake suddenly strikes at you, you spring back but two small marks ");
                        tnoua("on your arm show where its poison was injected. ");
                        if (objloc[CHARM] == 0) {
                            tnoua("By good luck there seem to be no ill effects - this time!");
                            if (flags[ELIXIR] == 0) values[LUCK] -= 10;
                            monstr[SNAKE] = -2;
                        } else {
                            sleep(5);
                            tonl(1);
                            tnoua("Your arm swells up and goes black, you feel muzzy from the effects of the poison!! ");
                            sleep(5);
                            if (flags[ELIXIR] != 0) {
                                tonl(1);
                                tnoua("After a while your head clears and within a few minutes the swelling has ");
                                tnoua("gone down and your arm is as good as new. Meanwhile, the snake has disappeared. ");
                                monstr[SNAKE] = -2;
                            } else {
                                showtext();
                                dead();
                                return;
                            }
                        }
            }
        }
                monstr[SNAKE] += rnd(2);
                if (rnd(5) == 3) monstr[SNAKE] = -rnd(4);
                if (monstr[SNAKE] < 0) values[MONCOUNT]--;
            }
            DWARF -> {
if (monstr[DWARF] <= 0) continue;           /* Dwarves are not active */
                if (monstr[VAMPIRE] > 0) continue;          /* and avoid the vampire  */
                if (monstr[DRAGON] > 0) continue;           /* and the dragon         */
                if (z < 2) continue;                        /* Only seen underground  */
                tonl(1);
                if (rnd(4) == 3) values[DWFNOW]++;
                if (rnd(5) == 1) values[DWFNOW]--;
                if (values[DWFNOW] > values[DWFNUM]) values[DWFNOW] = values[DWFNUM]; /* Can't exceed total population */
                if (values[DWFNOW] > 5) values[DWFNOW] = 5;                           /* No more than 5 at a time      */
                if (values[DWFNOW] == 0) continue;
                if (values[DWFNOW] == 1) {
                    when (monstr[DWARF]) {
            1 -> {
tnoua("There is an angry little dwarf in here with you. ");
            }
            2 -> {
tnoua("The little dwarf is furious. ");
            }
            else -> {
tnoua("The infuriated dwarf shoots a tiny dart ");
                            if (rnd(3) == 2) {
                                tnoua("at you but misses. ");
                            } else {
                                tnoua("which hits you and smarts painfully for a while. ");
                                values[LUCK]--;
                                if (values[LUCK] < 15) lucky();
                            }
            }
        }
                } else {
                    when (monstr[DWARF]) {
            1 -> {
tnoua("There are ");
                            tnoua(numerals[values[DWFNOW]]);
                            tnoua(" angry little dwarves in here with you. ");
            }
            2 -> {
tnoua("The dwarves are furious. ");
            }
            else -> {
tnoua("The infuriated dwarves fire a hail of tiny darts, some of them hit ");
                            tnoua("you and smart painfully like wasp stings.");
                            values[LUCK] -= values[DWFNOW];
                            if (values[LUCK] < 15) lucky();
            }
        }
                }
                if (rnd(3) == 2) monstr[DWARF]++;
                if (rnd(5) == 1) monstr[DWARF] = -1;
                if (monstr[DWARF] < 0) values[MONCOUNT]--;
            }
            TROLL -> {
if (monstr[TROLL] <= 0) continue;
                tonl(1);
                if (z < 2 && flags[NIGHT] == 0) {
                    tnoua("The sunlight catches the troll, it gives a piercing scream and tumbles ");
                    tnoua("to the ground as an inert lump of rock. ");
                    monstr[TROLL] = 0;
                    values[MONCOUNT]--;
                    score += 50;
                }
                when (monstr[TROLL]) {
            1 -> {
tnoua("A large troll steps out of the shadows and lumbers menacingly after you. ");
            }
            2 -> {
tnoua("The troll lumbers after you. ");
            }
            3 -> {
tnoua("The troll tries to corner you, but you manage to avoid him. ");
            }
            else -> {
if (monstr[DWARF] > 0) {
                            tnoua("The troll lumbers towards you, you try to dodge but trip over a little dwarf. ");
                            tnoua("The troll stamps both of you to death! ");
                        } else {
                            tnoua("The troll corners you, you try to escape but cannot squeeze past him. ");
                            tnoua("Slowly he crushes you to a pulp! ");
                        }
                        showtext();
                        dead();
                        return;
            }
        }
                if (rnd(2) == 2) monstr[TROLL]++;
                if (rnd(3) == 2) monstr[TROLL] = 2;
                if (rnd(15) == 6) monstr[TROLL] = 1;
                if (rnd(6) == 4) monstr[TROLL] = -monstr[TROLL];
                if (monstr[TROLL] < 0) values[MONCOUNT]--;
            }
            DRAGON -> {
var randno = 0
                if (monstr[DRAGON] <= 0) continue;
                randno = rnd(2) + 1;       /*  Range 2 - 3 */
                if (z < 2) monstr[DRAGON] = -randno;              /* Dragon is only active underground */
                if (flags[LARGE] == 0) monstr[DRAGON] = -randno;  /* Only active if location is large  */
                if (flags[SHAFT] == 1) monstr[DRAGON] = -randno;  /* Not active in shafts              */
                if (monstr[DRAGON] < 0) {
                    values[MONCOUNT]--;
                } else                   /* OK - we have a dragon problem here */
                {
                    tonl(1);
                    when (monstr[DRAGON]) {
            1 -> {
tnoua("A huge dragon lies before you. Feeling your presence it heaves itself to ");
                            tnou("its feet and prepares to attack. ");
            }
            2 -> {
tnoua("The dragon confronts you breathing fire and smoke. You had better get out of here fast! ");
            }
            3 -> {
tnoua("The dragon thunders after you! ");
            }
            4 -> {
tnoua("The dragon breathes a blast of fire");
                            randno = rnd(4);
                            when (randno) {
            1 -> {
tnou(" which burns you to a cinder!");
            }
            2 -> {
tnou(", you leap aside but are laid low by its lashing tail. It devours you in an instant!");
            }
            else -> {
tnou(", you dodge just in time.");
            }
        }
                            if (randno < 3) {
                                showtext();
                                dead();
                                return;
                            }
            }
        }
                    showtext();
                    monstr[DRAGON] = rnd(2) + 2;
                }
            }
            VAMPIRE -> {
var vampiredead = 0
        var vampiregetsyou = 0
                if (monstr[VAMPIRE] <= 0) continue;
                if (flags[NIGHT] == 0) {
                    monstr[VAMPIRE] = -kotlin.math.abs(monstr[VAMPIRE]);
                    values[MONCOUNT]--;
                }
                tonl(1);
                when (monstr[VAMPIRE]) {
            1 -> {
tnou("A cadaverous man with red eyes sidles up behind you. ");
                        monstr[VAMPIRE] = 2;
            }
            2 -> {
tnou("The red-eyed man is close behind you. ");
                        if (rnd(4) == 2) monstr[VAMPIRE] = 3;
            }
            3 -> {
tnoua("You turn to confront your follower, his red eyes gaze into yours and your ");
                        tnou("head swims. With a great effort of will you recover your senses. ");
                        monstr[VAMPIRE] = 5 - rnd(3);
            }
            4 -> {
tnoua("The cadaverous man leaps from behind and tries to bite your throat");
                        if (gone[GARLIC] == 1) {
                            vampiredead = 1;
                        } else {
                            if (rnd(3) == 2) {
                                vampiregetsyou = 1;
                            } else {
                                tnou(", you break free just in time. ");
                                if (rnd(3) == 1) monstr[VAMPIRE] = 5;
                            }
                        }
            }
            5 -> {
tnoua("The corpse-like man has disappeared but a large bat hovers nearby. ");
                        if (rnd(4) == 2) monstr[VAMPIRE] = 6;
            }
            6 -> {
tnoua("A large bat swoops down ");
                        if (gone[GARLIC] == 1) {
                            vampiredead = 1;
                        } else {
        var k = rnd(3)
                            when (k) {
            1 -> {
tnou(", you break free just in time. ");
            }
            2 -> {
tnoua(" and bites your throat. ");
            }
            3 -> {
monstr[VAMPIRE] = 4 + rnd(2);
            }
        }
                        }
            }
        }
                if (vampiredead == 1) {
                    tnoua(" but is repelled by the smell of your breath and vanishes in a cloud of dust ");
                    tnou("specks which dance before your eyes and slowly disappear. ");
                    monstr[VAMPIRE] = 0;
                    score += 50;
                    values[MONCOUNT]--;
                }
                if (vampiregetsyou == 1) {
                    tnoua(". Cold talons grasp you and you feel his sweet, foul breath as he draws the life-blood ");
                    tnoua("from you. ");
                    showtext();
                    sleep(5);
                    tonl(1);
                    tnoua("You pass out for a while, when you recover you have a raging thirst which cannot be satisfied. ");
                    tnou("Everything appears dim and misty and you are no longer interested in material things. ");
                    tnoua("");
                    score = 0;
                    flags[UNDEAD] = 1;
                    for (i in 1 until MAXMON) monstr[i] = 0;      /* Permanently deactivate all monsters */
                    for (i in 1 until MAXOBJ) objloc[i] = here;   /* Drop everything here                */
                    values[HELD] = 0;
                    values[DWFNUM] = 0;
                    values[DWFNOW] = 0;
                    values[MONCOUNT] = 0;
                }
                if (rnd(20) == 13) monstr[VAMPIRE] = -monstr[VAMPIRE];
                if (monstr[VAMPIRE] == -3) monstr[VAMPIRE] = -2;
                if (monstr[VAMPIRE] < 0) values[MONCOUNT]--;
            }
        }
    }
    showtext();

    }

private suspend fun move(mpoint: Int) {
        var mpoint = mpoint
        var i = 0
    if (ways[mpoint] == 0) {
        tnou("You cannot go in that direction!");
        showtext();
        return;
    }
    if (values[BASE] == here) /*---- At base camp -----------------------*/
    {
        flags[HOME] = 1;
        described = 0;
        if (mpoint == IN) {
            flags[INTENT] = 1;
            return;
        }
        if (mpoint == OUT) {
            flags[INTENT] = 0;
            return;
        }
    } else flags[HOME] = 0;
    x = kotlin.math.abs(x + xinc[mpoint] * ways[mpoint]);
    y = kotlin.math.abs(y + yinc[mpoint] * ways[mpoint]);
    z = kotlin.math.abs(z + zinc[mpoint] * ways[mpoint]);
    here = locate(x, y, z);
    described = 0;
    for (i in 1 until MAXOBJ) obhere[i] = 0;                  /* Unset all `object` here flags     */
    moveno++;                                              /* Increment the move number       */
    values[DAYTIME]++;                                     /* Increment the time              */
    if (values[DAYTIME] > 48) newday();                    /* Start a new day                 */
    values[POOL] = -1;
    flags[SHAFT] = 0; flags[DRIP] = 0; flags[NIGHT] = 0;
    if (values[DAYTIME] < values[DAWN]) flags[NIGHT] = 1;      /* It is night                          */
    if (values[DAYTIME] > values[DUSK]) flags[NIGHT] = 1;      /* It is night                          */
    if (flags[NIGHT] != 0 && flags[LAMPON] == 0) flags[DARK] = 1;       /* It is dark at night  and             */
    if (z > 1 && flags[LAMPON] == 0) flags[DARK] = 1;              /* underground with no light            */
    if (flags[UNDEAD] == 0) values[THIRST]++;                      /* Exploring is thirsty work            */
    if (objloc[LAMP] != 0) flags[LAMPON] = 0;                  /* The lamp is not lit unless held      */
    if (flags[LAMPON] != 0 && objloc[LAMP] == 0) flags[DARK] = 0;   /* Lamp on, therefore not dark          */
    if (flags[LAMPON] != 0) values[LIGHT]--;                        /* but the battery is being used up     */
    if (score < 0) score--;                                    /* Bank charge if account is in the red */
    if (flags[UNDEAD] != 0) {
        flags[DARK] = 0;                     /* The undead can always see     */
        if (flags[NIGHT] != 0)                    /* but must sleep during the day */
        {
            if (z < 2) tnoua("T"); else tnoua("Outside t");
            tnoua("he sun is rising and you fall into a deep slumber from which you do not awake until after nightfall. ");
            sleep(5);
            showtext();
            values[DAYTIME] = values[DUSK];
        }
    }

    }

private suspend fun news() {
        var r = 0
    r = rnd(4);
    if (r % 4 == 0) {
        tonl(1);
        tnou("==================================================");
        tonl(1);
        tnou("**** CHIMAERA NEWS FLASH ****");
        tonl(1);
        tnou("Having trouble with space and time? If so use");
        tnoua("WHERE and TIME to get a fix!");
        tonl(1);
        tnoua("The dwarves seem more aggressive these days,");
        tnou("you had better find out how to get rid of them!");
        tnoua("Flickering lamps cannot be relit indefinitely,");
        tnou("you must find a more permanent solution!");
        tonl(1);
        tnoua("Help is precious, away from your camp you can only");
        tnoua("use it twice. Save it for real emergencies only or you may ");
        tnoua("get completely stuck underground. There is also a magic");
        tnou("word to assist you, but few people have discovered it!!");
        tonl(1);

        tnoua("You can save the game by typing SAVE. You will be asked a file name so you can restore it later. ");
        tnou("To restore a game type RESTORE. You will be asked for a file name. ");
        tonl(1);

        showtext();
    }

    }

private fun pointr(n: Int) {
        var n = n
        var i = 0; var a = 0; var b = 0; var tmp = 0

    for (i in 1 until n) ipt[i] = i;       /* Preload index array */
    for (i in 1 until 1000)                   /* and scramble them   */
    {
        a = rnd(MAXOBJ);
        b = rnd(MAXOBJ);
        tmp = ipt[a];
        ipt[a] = ipt[b];
        ipt[b] = tmp;
    }

    }

private fun showthings() {
        var i = 0; var k = 0; var len = 0; var seeit = 0; var posloc = 0; var mod20 = 0; var mod60 = 0
        var kount = 0; var ptr = 0

    if (flags[DARK] != 0) return;  /* Can't see anything in the dark */
    if (flags[INTENT] == 0)   /* Not in the tent */
    {
        if (objloc[SWORD] == here && flags[EXCALIBER] > 1) {
            tnou("The hilt of a sword protrudes from a nearby rock. ");
            obhere[SWORD] = 1;
        }
        if (flags[SHAFT] == 0)  /* There are no objects in the shafts */
        {
            seeit = 0;
            mod20 = ((here % 20)).toInt();  /* Above ground objects, probability 1:20 */
            mod60 = ((here % 60)).toInt();  /* Below ground objects, probability 1:60 */
            for (i in 1 until MAXOBJ) {
                obhere[i] = 0;
                if (objloc[i] == 0) continue;  /* Object carried by player      */
                if (objloc[i] == here)         /* It was put here by the player */
                {
                    obhere[i] = 1;
                    if (i == SWORD && flags[EXCALIBER] > 1) continue;
                    seeit++;
                    if (i == PLANT) continue;   /* The plant remains virtual until picked */
                    objloc[i] = here;
                    continue;
                }
                if (i > 10 && z < 2) continue;  /* Can't see it here */
                posloc = -objloc[i];
                if ((i > 10) && (z > 1) && (mod20 == posloc)) {
                    seeit++;
                    obhere[i] = 1;
                    if (i == PLANT) continue;   /* The plant remains virtual until picked */
                    objloc[i] = here;
                    continue;
                }
                if (mod60 == posloc) {
                    seeit++;
                    obhere[i] = 1;
                    if (i == PLANT) continue;   /* The plant remains virtual until picked */
                    objloc[i] = here;
                    continue;
                }
            }
            /*---- Display the objects in pseudo random order         ----*/
            if (seeit > 0) {
                kount = 0;
                for (i in 1 until MAXOBJ) {
                    if (i == SWORD && flags[EXCALIBER] > 1) continue;
                    if (obhere[i] != 0) {
                        kount++;
                        if (kount == 1) {
                            tonl(1);
                            tnoua("Here you can see ");
                        }
                        tnoua(thingdesc[i]);
                        if (kount == seeit) {
                            tnou(".");
                        } else {
                            if ((kount + 1) == seeit) tnoua(" and "); else tnoua(", ");
                        }
                    }
                }
            }
        }
    } else   /* The player is in the tent, display things differently */
    {
        kount = 0;
        for (i in 1 until MAXOBJ) {
            if (secure[i] != 0) {
                kount++;
                if (kount == 1) {
                    tnoua("Your other possessions include ");
                }
                tnoua(thingpref[i]);
                tnoulca(thing[i]);
                tnoua(", ");
            }
        }
        if (kount > 0) tnou(" etc.");
    }
    showtext();

    }

private fun welcome() {

    tonl(1);
    tnou("Welcome to the world of Chimaera.");
    tnou("(Created by Nicholas Perre-Wetherall)");
    tnou("Version: C1.002");
    tonl(1);
    tnoua("Command me and I will be your guide. There is treasure to be ");
    tnoua("found but also much danger. Few who venture here escape ");
    tnou("unchanged however you may succeed where others have failed!!");
    showtext();

    }

private fun worth() {
        var i = 0
    for (i in 0 until 10) points[i] = 5;          /* Common objects (1-10) */
    for (i in 10 until MAXOBJ) points[i] = 20;    /* Uncommon objects      */
    /* Valuables                                         */
    points[NUGGET] = 25;     /* Gold nugget        */
    points[DIAMOND] = 30;     /* Diamond            */
    points[JEWELS] = 40;     /* Jewels             */
    points[PYRAMID] = 45;     /* Platinum pyramid   */
    points[SAPPHIRE] = 50;     /* Priceless sapphire */
    points[TREASURE] = 50;     /* Treasure           */
    points[VENUS] = 40;     /* Venus-de-Milo      */
    points[GOLDRING] = 30;     /* Golden ring        */
    /* Objects with a low intrinsic value                */
    points[GARLIC] = 2;     /* Garlic             */
    points[BOOK] = 2;     /* Book               */
    points[VIOLETS] = 2;     /* Violets            */
    points[CHARM] = 10;     /* Good luck charm    */
    points[OYSTER] = 2;     /* Oyster             */
    points[CLAM] = 2;     /* Clam               */
    points[ELIXIR] = 2;     /* Elixir of life     */
    points[BRICKBATS] = 0;     /* Brickbats          */
    points[GNOME] = 0;     /* Gnome              */

    }

private suspend fun dead() {
        var i = 0; var yn = 0
    tonl(2);
    tnoua("Sorry, you are ");
    if (values[LUCK] < 1) tnoua("permanently ");
    tnou("dead!");
    showtext();
    if (values[LUCK] >= 1) {
        tonl(1);
        tnoua("I may be able to reincarnate you, shall I try? ");
        showtext();
        yn = yesno();
        if (yn == 1) {
            tnou("O.K., this may hurt a little and will cost you 50 points!");
            tonl(5);
            tnou("There is a blinding flash!!!!");
            sleep(5);
            tonl(1);
            tnou("and");
            sleep(5);
            tonl(5);
            tnou("You recover to find yourself in the tent at your base camp.");
            tonl(1);
            showtext();
            values[LUCK] = values[LUCK] - 5;
            /* Put the player into the tent */
            for (i in 1 until 15) ways[i] = 0;
            ways[OUT] = 1;  /* Only way from here is OUT */
            x = values[FIRSTX];
            y = values[FIRSTY];
            z = 1;
            here = locate(x, y, z);
            flags[INTENT] = 1;
            flags[HOME] = 1;
            flags[SHANGRI] = 0;
            flags[LAMPON] = 0; flags[DARK] = 0; flags[NIGHT] = 0; flags[CARNIV] = 0; flags[ANIMAL] = 0;
            newday();  /* Increment the date */
            values[DAYTIME] = values[DAWN];
            flags[DRIP] = 0; flags[SHAFT] = 0; flags[WAVER] = 0; flags[GLOW] = 0;
            values[HELD] = 0;
            locwun();                /* Move all objects into a new set of starting positions */
            for (i in 1 until MAXOBJ)   /* and reset those in the tent or gone forever           */
            {
                if (secure[i] == 1) objloc[i] = -1000;
                if (gone[i] == 1) objloc[i] = -2000;
                if (objloc[i] == -2000) gone[i] = 1;  /* Set by locwun() ??? */
            }
            for (i in 1 until MAXMON) {
                if (monstr[i] != 0) monstr[i] = -1;  /* Reinitialise all monsters that still exist */
            }
        } else quit(0);
    } else quit(0);

    }

private fun newday() {

    values[DAYTIME] = 1;
    values[DAY]++;
    if (values[DAY] > mdays[values[MONTH]])        /* Start a new month */
    {
        values[DAY] = 1;
        values[MONTH]++;
        if (values[MONTH] > 12) values[MONTH] = 1;  /* Start a new year  */
        values[DAWN] = dawn[values[MONTH]];
        values[DUSK] = dusk[values[MONTH]];
    }
    values[WEEKDAY]++;
    if (values[WEEKDAY] > 7) values[WEEKDAY] = 1;  /* Start a new week  */

    }

private fun plumb(): Int {
        var i = 0; var j = 0; var znext = 0
        var thislevel = 0; var nextlevel = 0

    znext = z;
    for (i in 0 until 20) {
        thislevel = locate(x, y, znext);
        nextlevel = locate(x, y, znext + 1);
        if ((thislevel  and  nextlevel  and  128) != 128) /* Found bottom of shaft */
        {
            return (thislevel); /* return the address of the shaft bottom */
        }
        znext++;
    }
    return (rnd(30) - 50);

    }

private fun swearbox(token: String) {
        var token = token
        var i = 0
    for (i in 1 until MAXEXP)  /* Has a naughty word been used? */
    {
        if (explet[i].contains(token) && explet[i].length == token.length) {
            tnou("That sort of language is not permitted here, you have been fined 5 points!");
            score -= 5;
        }
    }

    }

private fun callhim() {

    tnoua("No one comes");
    if (rnd(3) == 1) tnoua(", servants are so difficult to find these days");
    tnou(". ");
    showtext();

    }

private fun chase() {
        var i = 0

    if (values[RUNOUT] != moveno) {
        tnou("Chase what? ");
        showtext();
        return;
    }

    when (z) {
            0 -> {
/* Can't chase from up a tree */
            tnou("Don't be stupid, you'll fall and break your neck. ");
            }
            1 -> {
/* Ground level               */
            tnoua("You set off in pursuit but trip over a tree root and fall heavily");
            if (values[HELD] > 0) tnoua(", scattering your possessions about you");
            tnoua("");
            tnou(". ");
            values[HELD] = 0;
            for (i in 1 until MAXOBJ) {
                if (objloc[i] == 0) objloc[i] = here;
            }
            }
            else -> {
when (rnd(4)) {
            1 -> {
tnoua("You step forward but are suddenly overcome with weariness ");
                    tnoua("and fall to the ground in a dead faint. After a time you ");
                    tnou("recover your senses and your strength rapidly returns. ");
                    values[DAYTIME] += rnd(10);
            }
            2 -> {
tnoua("You leap forward but some mysterious and irresistable power holds you back. ");
                    tnou("After a brief struggle you give up. ");
            }
            3 -> {
tnoua("A ghastly figure rises before you. \"Don't do it; I did and this is the ");
                    tnoua("result!\" it moans as it slowly crumbles into dust before your very eyes. ");
                    tnou("You decide that discretion is the better part of valour. ");
            }
            4 -> {
tnoua("You give chase. After a while you pause for breath and look about you. Realising ");
                    tnou("that you have run round in circles and are back where you started from you give up. ");
                    values[DAYTIME] += rnd(4);
            }
        }
            }
        }
    if (values[DAYTIME] > 48) newday();
    showtext();

    }

private fun closeit(objpoint: Int) {
        var objpoint = objpoint

    tnoua("I can't do that with the ");
    tnoulca(thing[objpoint]);
    tnou(" yet! ");
    showtext();

    }

private fun drink(objpoint: Int) {
        var objpoint = objpoint

    if (flags[DRIP] != 0) {
        if (values[POOL] == 2) {
            tnou("You drink cool, clear water from the pool and feel much refreshed.");
            values[THIRST] = 0;
            values[LUCK] += 10;
        } else {
            tnou("The water drip is too slow to quench your thirst but at least you can moisten your parched lips. ");
            values[THIRST] -= 10;
            values[LUCK] += 2;
        }
        showtext();
        return;
    }
    if (objloc[BOTTLE] != 0 && objloc[ELIXIR] != 0) {
        tnou("You have nothing to drink. ");
        showtext();
        return;
    }
    when (objpoint) {
            0 -> {
tnou("Drink what? ");
            showtext();
            return;
            }
            ELIXIR -> {
if (objloc[ELIXIR] != 0) {
                tnou("You haven't got it! ");
            } else {
                tnoua("You place the phial containing the elixir to your lips and drain every drop. A feeling of renewed ");
                tnou("strength courses through your veins. The empty phial falls to dust in your grasp. ");
                flags[ELIXIR] = 1;
                values[HELD]--;
                if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                objloc[ELIXIR] = -2000;
                secure[ELIXIR] = 0;
                gone[ELIXIR] = 1;
                score += 10;
                values[THIRST] = 0;
                values[LUCK] += 50;
            }
            }
            else -> {
if (objloc[BOTTLE] == 0) {
                if (flags[EMPTY] != 0) {
                    tnou("Your bottle is empty. ");
                } else {
                    tnou("You drink the contents of the bottle and feel much refreshed.");
                    flags[EMPTY] = 1;
                    values[THIRST] = 0;
                    values[LUCK] += 10;
                }
            }
            }
        }
    showtext();

    }

private suspend fun eat(objpoint: Int) {
        var objpoint = objpoint

    if (objloc[objpoint] != 0) {
        tnou("But you haven't got it! ");
        showtext();
        return;
    } else {
        when (objpoint) {
            0 -> {
tnou("Eat what? ");
                showtext();
                return;
            }
            FOOD -> {
tnou("Thank you, that was delicious!");
                score += 5;                         /* Give him a few points */
            }
            PLANT -> {
if (rnd(10) != 5) {
                    tnoua("It is sustaining but not particularly tasty. You notice a slight euphoria afterwards, ");
                    tnoua("possibly due to some alkaloid in the leaves, but there seem to be no permanent ");
                    tnou("ill effects. ");
                    score += 2;                         /* Give him a few points   */
                    if (rnd(10) != 8) {
                        objloc[PLANT] = -rnd(20);                  /* Generate another plant  */
                        values[HELD]--;                            /* decrement his inventory */
                        if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                        showtext();
                        return;
                    }
                } else {
                    tnoua("You eat the plant but are horrified to find the label \"Aconitum napellus (Monkshood)\" ");
                    tnoua("tied to the stem. You spit out what remains in your mouth but it is too late, ");
                    tnoua("gradually a numbness spreads over you, followed by a creeping paralysis beginning in your legs. ");
                    tnoua("Breathing is difficult and your pulse becomes slow, irregular and weak, although your ");
                    tnoua("mind remains perfectly clear. Suddenly you collapse! ");
                    showtext();
                    if (flags[ELIXIR] != 0) {
                        sleep(5);   /* Keep him in suspense for a while */
                        tnou("After a while you miraculously recover.");
                        score += 2;
                    } else {
                        dead();
                    }
                }
            }
            GARLIC -> {
tnoua("With some distaste you eat the garlic. Your breath smells terrible. ");
                score += 15;
            }
            VIOLETS -> {
tnoua("You nibble at the flowers and are delighted to find that they are crystallised ");
                tnoua("violets preserved in sugar. You eat the lot and the world seems a better ");
                tnoua("and happier place, for a time at least.");
                score += 10;
            }
            OYSTER -> {
if (values[MONTH] > 4 && values[MONTH] < 9) {
                    tnoua("Sorry, there is no \"r\" in the month and shellfish, are not in season.");
                    showtext();
                    return;
                } else {
                    if (objloc[DAGGER] != 0) {
                        tnoua("You have no knife to open it with!");
                        showtext();
                        return;
                    } else {
                        tnoua("You use the dagger to prise open the oyster, there ");
                        tnoua("is no pearl inside and you swallow the contents whole. ");
                        tnoua("A penguin dressed as a waiter (how else?) waddles in, takes ");
                        tnoua("the empty shell from you very politely and waddles out. ");
                        values[RUNOUT] = moveno;
                        score += 15;
                    }
                }
            }
            CLAM -> {
if (values[MONTH] > 4 && values[MONTH] < 9) {
                    tnoua("Sorry, there is no \"r\" in the month and shellfish, are not in season.");
                    showtext();
                    return;
                } else {
                    if (objloc[DAGGER] != 0) {
                        tnoua("You have no sword to open it with!");
                        showtext();
                        return;
                    } else {
                        tnoua("You force the sword blade between the two halves of the clam's shell and ");
                        tnoua("with a great effort prise it open. An evil looking little dwarf leaps out ");
                        tnoua("of the shell, curses angrily and vanishes out of sight round a corner. ");
                        tnoua("The clam snaps shut with a loud clang before you can eat it! ");
                        values[RUNOUT] = moveno;
                        values[DWFNUM]++;
                        showtext();
                        return;
                    }
                }
            }
            else -> {
tnoua("Not ");
                when (rnd(3)) {
            1 -> {
tnoua("bl**dy ");
            }
            2 -> {
tnoua("Pygmalion ");
            }
        }
                tnou("likely! ");
            }
        }
    }
    objloc[objpoint] = -2000;           /* Remove the `object` permanently */
    gone[objpoint] = 1;
    values[HELD]--;                     /* and decrement his inventory   */
    if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
    showtext();

    }

private fun examine(objpoint: Int) {
        var objpoint = objpoint
        var i = 0
    when (objpoint) {
            0 -> {
tnoua("Examinations will be held in June every year, candidates should submit their three ");
            tnoua("best scores, which must be countersigned by an acknowledged wizard, as evidence of their ");
            tnou("competence. The following awards are available: ");
            tonl(1);
            tnou("\tScore \t \tCategory");
            tnou("\t===== \t \t========");
            tnou("\t<100\t\ta rank amateur");
            for (i in 1 until 11) {
                tnoua("\t");
                if (i < 10) {
                    tnoint(i * 100);
                    tnoua(" - ");
                    tnoint(i * 100 + 99);
                } else {
                    tnoua(">1000 \t");
                }
                tnoua("\t");
                tnoua(classes[i]);
                tnou(" adventurer");
            }
            tonl(1);
            tnoua("We regret that we are unable to assist you in this matter, please refer to your ");
            tnou("local Examinations Board for further information. ");
            }
            else -> {
tnoua("Pardon? ");
            }
        }
    showtext();

    }

private fun fill(objpoint: Int) {
        var objpoint = objpoint
        var i = 0; var pickup = 0

    if (objloc[objpoint] != 0) {
        tnoua("You don't have the ");
        tnoulca(thing[objpoint]);
        tnou("! ");
        showtext();
        return;
    }
    when (objpoint) {
            0 -> {
tnou("Fill what? ");
            }
            BOTTLE -> {
if (flags[EMPTY] != 0) {
                if (flags[DRIP] != 0) {
                    if (values[POOL] == 2) {
                        tnou("Your bottle is now full of water. ");
                        flags[EMPTY] = 0;
                    }
                    if (values[POOL] >= 0) {
                        tnoua("The water drip is very slow, it would take for ever to ");
                        tnou("fill anything from it and you quickly give up the attempt.");
                    }
                } else {
                    tnou("There is no water to fill it with! ");
                }
            } else {
                tnou("It is already full. ");
            }
            }
            BASKET -> {
if (objloc[BASKET] != 0) {
                tnou("What basket? ");
            }
            if (values[HELD] > 8) {
                tnou("Your basket is already full! ");
            } else {
                for (i in 1 until MAXOBJ) {
                    if (obhere[i] == 1) {
                        pickup++;
                        tnoua("You put the ");
                        tnoulca(thing[i]);
                        tnou(" into the basket. ");
                        values[HELD]++;
                        objloc[i] = 0;
                        obhere[i] = 0;
                        if (values[HELD] == 9) break;
                    }
                    if (pickup == 0) tnou("There is nothing here to put in it. ");
                }
            }
            }
            VASE -> {
if (flags[DRIP] != 0) {
                if (values[POOL] == 2) {
                    tnou("You attempt to fill the vase with water from the pool, but it pours out of a hole in the bottom. ");
                }
                if (values[POOL] >= 0) {
                    tnoua("The water drip is very slow, it would take for ever to ");
                    tnou("fill anything from it and you quickly give up the attempt.");
                }
            } else {
                tnou("There is no water to fill it with! ");
            }
            }
            BOX -> {
if (flags[BOXLOK] != 0) {
                tnou("The box is locked! ");
            } else {
                tnou("Would that you could put all the ills of mankind back into it. ");
            }
            }
            GOBLET -> {
if (flags[DRIP] != 0) {
                if (values[POOL] == 2) {
                    tnou("You dip the goblet into the pool but it shatters on contact with the ice-cold water. ");
                    values[HELD]--;
                    if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                    objloc[GOBLET] = -2000;
                    secure[GOBLET] = 0;
                    gone[GOBLET] = 1;
                    score -= 5;
                }
                if (values[POOL] >= 0) {
                    tnoua("The water drip is very slow, it would take for ever to ");
                    tnou("fill anything from it and you quickly give up the attempt.");
                }
            } else {
                tnou("There is no water to fill it with! ");
            }
            }
            else -> {
tnou("But it can't hold anything! ");
            }
        }
    showtext();

    }

private fun find(objpoint: Int) {
        var objpoint = objpoint

    if (objpoint == 0) {
        tnou("Find what? ");
    } else {
        tnoua("If you can't find it I'm sure that I can't. It could be anywhere ");
        tnou("or may not even exist! ");
    }
    showtext();

    }

private fun take(objpt: Int) {
        var objpt = objpt
        var i = 0
    if (flags[UNDEAD] != 0) {
        tnou("Why bother, you no longer have any interest in these material things. ");
        showtext();
        return;
    }
    if (`object`.length == 0) {
        tnou("What do you want to get?");
        showtext();
        return;
    }
    if (objpt == 0) {
        tnou("I can't do that.");
        showtext();
        return;
    }
    if (flags[INTENT] != 0) {
        if (secure[objpt] == 0) {
            tnoua("There is no ");
            tnoulca(thing[objpt]);
            tnou(" here!");
            showtext();
            return;
        }
    } else {
        if (obhere[objpt] == 0) {
            tnoua("I see no ");
            tnoulca(thing[objpt]);
            tnou(" here!");
            showtext();
            return;
        }
    }
    if (objpt == SWORD && flags[EXCALIBER] > 1) {
        tnoua("You try to pull the sword from the stone without success, obviously you are ");
        tnou("not Arthur son of Uther Pendragon. ");
        showtext();
        return;
    }
    if (objpt == BASKET) flags[HANDSFULL] = 0; /* Now has the basket so can carry more */
    if (objpt != BASKET && flags[HANDSFULL] == 1) {
        if (rnd(2) == 1) {
            tnou("Your hands are full!");
        } else {
            tnou("You can't carry any more, you will have to drop something first!");
        }
        showtext();
        return;
    }
    values[HELD]++;
    if (flags[HANDSFULL] != 1) {
        if (objloc[BASKET] == 0) {
            if (values[HELD] > 8) flags[HANDSFULL] = 1;  /* Can carry nine objects with the basket */
        } else {
            if (values[HELD] > 5) flags[HANDSFULL] = 1;  /* Can carry six objects without the basket */
        }
    }
    if (objpt == CHARM) values[LUCK] += 100;                            /* the lucky charm */
    if (objloc[BASKET] == 0) {
        if (objpt == LAMP || objpt == SWORD || objpt == STAFF || objpt == CARPET) {
            tnoua("You pick up the ");
            tnoulca(thing[objpt]);
            if (objpt == SWORD && flags[EXCALIBER] == 0) {
                tnou(", it is lighter and flimsier than it looks but it will have to serve");
            }
            tnou(". ");
        } else {
            tnoua("You put the ");
            tnoulca(thing[objpt]);
            tnou(" into the basket. ");
        }
    } else {
        tnoua("You pick up the ");
        tnoulca(thing[objpt]);
        if (objpt == SWORD && flags[EXCALIBER] == 0) {
            tnou(", it is rather blunt but will have to do");
        }
        if (objpt == GOLDRING) tnoua(", it might look good on your finger");
        tnou(". ");
    }
    objloc[objpt] = 0;          /* The `object` is now held   */
    obhere[objpt] = 0;          /* and it is no longer here */
    secure[objpt] = 0;          /* or in the tent           */
    showtext();

    }

private suspend fun gamic() {
        var len = 0; var forever = 1

    if (action.take(5) != "GAMIC".take(5)) {
        tnou("I'm afraid I don't understand you.");
        showtext();
        return;
    }
    tnoua("Argg, ywyll slagwyll Gaimykk, ipf wazglytt apfglyjll sqydd 'swalguut.");
    tnou("Hyperrd makargyulaitt zligwik puddhamerr!");
    showtext();
    while (forever == 1) {
        tnoua("> ");
        showtext();
        len = get_line();
        if (len == 0) continue; else parse();             /* Loop if input is null */
        if (action.take(3) == "ENGLISH".take(3)) {
            tnou("OK, if you don't want to practise your Gamic we will communicate in English. ");
            showtext();
            forever--;
            return;
        }
        if (action.take(4) == "QUIT".take(4)) {
            quit(1);
        }
        when (rnd(5)) {
            1 -> {
tnou("Dog yogul wyshhg togi speko Eynglyschimuse? ");
            }
            2 -> {
tnou("Whotyg hlly yogul tolkien ibooto? ");
            }
            3 -> {
tnou("Ipf ywyll wallogg quitog, sayligg soww. ");
            }
            4 -> {
tnou("Ig cennocka makig hid norie tallie o' thyso. ");
            }
            5 -> {
tnou("Yogul talig o' lodo rybbig. ");
            }
            else -> {
tnou("Ifg doanutt unstlangg, plygickk splagwyll Gaimykk. ");
            }
        }
        showtext();
    }

    }

private suspend fun help() {
        var i = 0; var yn = 0

    if (flags[HOME] != 0) {
        tnoua("Would you like to see the instructions, for which there will be no charge? ");
        showtext();
        yn = yesno();
        if (yn == 1) instructions(0);
        return;
    } else {
        when (helpno) {
            0 -> {
tnoua("I can get you out of here, the charge will be 10 points, are you sure you need help? ");
            }
            1 -> {
tnoua("I can get you out of here, but it will cost you 20 points, do you really need help that much? ");
            }
            2 -> {
tnoua("I can rescue you one last time, but the price is now risen to 30 points, can you afford it? ");
                dicact[37] = ""  /* Delete the help command */
            }
        }
        showtext();
        yn = yesno();
        if (yn != 1) return;
        helpno++;
        score -= (helpno * 10);
        tonl(1);
        tnoua("A thick mist forms above you, from which a giant hand slowly descends. It lifts you ");
        if (values[HELD] > 0) {
            tnoua("and all your possessions ");
        }
        tnoua("into the air and you lose all sense of time and space. After a while the mists clear and ");
        showtext();
        sleep(5);
        values[DAYTIME] = values[DAWN];
        flags[DARK] = 0;
        for (i in 1 until MAXMON) { if (monstr[i] != 0) monstr[i] = -1; } /* Initialise all remaining monsters */
        values[DWFNOW] = 0;                                            /* Remove any current dwarves        */
        x = values[FIRSTX];
        y = values[FIRSTY];
        z = 1;                 /* Move to base camp                 */
        here = locate(x, y, z);
        flags[HOME] = 1;
        tnou(". you find yourself at your base camp. ");
        if (score < 0) {
            tonl(1);
            tnoua("Your account is in the red and a bank charge of 1 point will be made each time you move until you ");
            tnou("are back in credit. We respectfully request that you clear your overdraft as quickly as possible! ");
        }
        showtext();
    }

    }

private fun hint(setting: String) {
        var setting = setting
        var hinttext = ""

    if (flags[AUTOHINT] == -1) {
        tonl(1);
        tnoua("Context sensitive hints are available in certain situations. Each hint ");
        tnoua("costs one point and will be shown once only. The hint engine may be set ");
        tnoua("to show hints automatically (by typing 'HINT AUTO') or on demand (by typing ");
        tnoua("'HINT OFF'), the default setting is HINT AUTO. Hints may be requested at any ");
        tnoua("time by typing 'HINT'. If there is no hint relevant to your situation there ");
        tnou("will be no charge. ");
        flags[AUTOHINT] = 1;          /* Set to auto */
        showtext();
        return;
    }
    if (setting.length == 0)                   /* Give him the most relevant hint */
    {
        if (hintsdone[values[HINTPTR]] != 0) {
            tnou("You have already seen the relevant hint!");
            showtext();
            return;
        }
        if (values[HINTPTR] == 0) {
            tnou("Sorry, nothing relevant available. There has been no charge.");
            showtext();
            return;
        }
        if (flags[AUTOHINT] != 0) tnoua("\n Hint: ");
        hinttext = ""
        hinttext += hints[values[HINTPTR]];
        tnoua(hinttext);
        tnou(". ");
        hintsdone[values[HINTPTR]] = 1;   /* Flag it as shown */
    } else                                  /* Change the hint setting    */
    {
        if ("AUTO".contains(setting)) {
            flags[AUTOHINT] = 1;
            return;
        }
        if ("OFF".contains(setting)) {
            flags[AUTOHINT] = 0;
            return;
        }
        tnou("Pardon?");
    }
    showtext();

    }

private fun lockit(objpoint: Int) {
        var objpoint = objpoint

    tnoua("I can't do that with the ");
    tnoulca(thing[objpoint]);
    tnou(" yet! ");
    showtext();

    }

private fun play(objpt: Int) {
        var objpt = objpt

    if (objpt == 0) {
        tnou("Play what? ");
        showtext();
        return;
    }
    if (objpt != FLUTE && objpt != MUSIC) {
        tnou("You can't play that! ");
        showtext();
        return;
    }
    if (objpt == FLUTE || objpt == MUSIC) {
        if (objloc[FLUTE] != 0) {
            tnou("You have no flute! ");
        } else {
            if (objloc[MUSIC] != 0) {
                tnou("You have no music and cannot play by ear. ");
            } else                   /* He has flute and music */
            {
                tnoua("You play the silver flute very badly, ");
                if (monstr[SNAKE] > 0) {
                    tnoua("the snake is alarmed by the noise and slithers out of sight ");
                    tnou("through a crack in the floor, never to return. ");
                    values[RUNOUT] = moveno;
                    monstr[SNAKE] = 0;
                    values[MONCOUNT]--;
                    score += 50;
                } else {
                    tnou("nothing remarkable happens. ");
                }
            }
        }
    }
    showtext();

    }

private fun plugh() {

    if (action.take(5) != "PLUGH".take(5)) {
        tnou("Pardon? ");
    } else {
        tnou("I think you are in the wrong game, try ADVENTURE. ");
    }
    showtext();

    }

private fun put(apoint: Int, objpt: Int) {
        var apoint = apoint; var objpt = objpt
        var i = 0; var dropit = 0; var plural = 0
        var bottom = 0
    if (`object`.length == 0) {
        if (apoint == 27) tnoua("Put down ");
        if (apoint == 28) tnoua("Drop ");
        tnou("what? ");
        showtext();
        return;
    }
    if (objloc[objpt] != 0) {
        tnou("But you haven't got it!");
        showtext();
        return;
    }
    tnoua("You ");
    if (apoint == 27) tnoua("put down");
    if (apoint == 28) tnoua("drop");
    tnoua(" the ");
    tnoulca(thing[objpt]);
    if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;  /* Dropped something, hands no longer full */
    if (objpt == LAMP && flags[LAMPON] != 0) {
        tnoua(", it immediately goes out");
        flags[LAMPON] = 0;
        if (z > 1 || flags[NIGHT] != 0) flags[DARK] = 1;
    }
    if (objloc[CUSHION] != here && flags[INTENT] == 0) {
        if (objpt == BOTTLE || objpt == VASE || objpt == GOBLET || objpt == MIRROR) {
            tnoua(", it smashes into a thousand fragments. ");
            if (here == values[BASE]) {
                tnoua("A native servant rushes up, sweeps up the pieces and dashes away again. ");
                values[RUNOUT] = moveno;
            } else {
                if (objpt == BOTTLE) values[BRKBOT] = here;
                if (objpt == VASE) values[BRKVAS] = here;
                if (objpt == GOBLET) values[BRKGOB] = here;
                if (objpt == MIRROR) values[BRKMIR] = here;
            }
            if (objpt == MIRROR) {
                tnoua("Oh dear! You have broken the mirror, be prepared for seven year's bad luck!!");
                values[LUCK] -= 50;
            }
            score -= 10;
            values[HELD]--;
            if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
            objloc[objpt] = -1000;
            showtext();
            return;
        }
    }
    values[HELD]--;
    if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
    objloc[objpt] = here;
    if (objpt == CHARM) values[LUCK] -= 100;  /* No longer has the good luck charm */
    if (flags[INTENT] != 0) {
        secure[objpt] = 1;
        objloc[objpt] = -1000;
    }
    if (objpt == BASKET) {
        dropit = 0;
        for (i in 1 until MAXOBJ) {

            if (objloc[i] != 0) continue;
            if (i == LAMP || i == SWORD || i == STAFF || i == CARPET) continue;
            if (i == BASKET) continue; /* Dropped this already */
            dropit++;
            values[HELD]--;
            if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
            objloc[i] = here;
            if (objpt == CHARM) values[LUCK] -= 100;  /* No longer has the good luck charm */
            if (flags[INTENT] != 0) {
                secure[i] = 1;
                objloc[i] = -1000;
            }
        }
        if (dropit > 0) tnoua(" and everything in it");
    }
    tnoua(". ");
    if (flags[SHAFT] != 0) {
        plural = 0;
        if (objpt == KEYS || objpt == COINS || objpt == JEWELS) plural++;
        if (objpt == BANKNOTES || objpt == VIOLETS || objpt == BRICKBATS) plural++;
        bottom = plumb();
        if (plural == 0) {
            tnoua("It falls ");
        } else {
            tnoua("They fall ");
        }
        tnoua("out of sight down the shaft.");
        for (i in 1 until MAXOBJ)                           /* Locate dropped objects at bottom of shaft */
        {                                              /* or distribute them randomly if it is more */
            if (objloc[i] == here) objloc[i] = bottom;    /* than 20 levels deep                       */
        }
    }
    for (i in 1 until MAXOBJ) {
        obhere[i] = 0;
        if (objloc[i] == here) obhere[i] = 1;
        if (flags[INTENT] != 0 && secure[i] != 0) obhere[i] = 1;
    }
    showtext();

    }

private fun shangrila() {

    if (action.take(7) != "SHANGRI".take(7)) {
        tnou("Pardon? ");
        showtext();
        return;
    }
    if (z != 1) {
        tnou("I'm sorry, we don't pick up passengers here. Please make your ");
        tnou("own way to the plain, where we may be able to collect you. ");
        showtext();
        return;
    }
    if (flags[SHANGRI] == 0) {
        tnoua("A ramshackle mud-spattered bus appears, apparently from nowhere. The driver calls out ");
        tnoua("\"Last bus to Shangri La!\" and you climb on board. After a hair-raising drive across ");
        tnoua("the plain lasting a couple of hours the bus deposits you not far from the foot of the mountains. ");
        tnoua("\"You'll find it about an hour's walk south of here.\" shouts the driver before driving off ");
        tnou("at breakneck speed and disappearing into the distance. ");
        showtext();
        x = 0;
        y = 2;
        z = 1;
        here = locate(x, y, z);
        flags[SHANGRI] = 1;
        values[DAYTIME] += 4;
        if (values[DAYTIME] > 48) newday();                   /* Start a new day */
    } else {
        tnoua("Haven't you been there already? I have no idea where to find it and the ");
        tnou("travel company has gone out of business! ");
    }
    showtext();

    }

private fun throw_obj(objpt: Int) {
        var objpt = objpt
        var i = 0; var dropit = 0; var plural = 0
        var bottom = 0
    if (`object`.length == 0) {
        tnou("Throw what?");
        showtext();
        return;
    }
    if (objloc[objpt] != 0) {
        tnou("But you haven't got it!");
        showtext();
        return;
    }
    tnoua("You throw the ");
    tnoulca(thing[objpt]);
    if (objpt == BOTTLE || objpt == VASE || objpt == GOBLET || objpt == MIRROR) {
        tnoua(", it smashes into a thousand fragments. ");
        if (here == values[BASE]) {
            tnoua("A native servant rushes up, sweeps up the pieces and dashes away again. ");
            values[RUNOUT] = moveno;
        } else {
            if (objpt == BOTTLE) values[BRKBOT] = here;
            if (objpt == VASE) values[BRKVAS] = here;
            if (objpt == GOBLET) values[BRKGOB] = here;
            if (objpt == MIRROR) values[BRKMIR] = here;
        }
        if (objpt == MIRROR) {
            tnoua(" Oh dear! You have broken the mirror, be prepared for seven year's bad luck!!");
            values[LUCK] -= 50;
        }
        score -= 10;
        values[HELD]--;
        if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
        objloc[objpt] = -1000;
        showtext();
        return;
    }
    when (objpt) {
            SWORD -> {
if (flags[EXCALIBER] == 1) {
                if (z < 2) {
                    tnoua(", as if guided by some unseen power it flies straight as an arrow towards a shimmering lake ");
                    tnoua("which has suddenly appeared nearby. But before it hits the surface an arm clothed in white ");
                    tnoua("samite, mystic, wonderful rises and catches it by the hilts, brandishes it three times and ");
                    tnoua("disappears again below the surface. The lake shimmers briefly, becomes still and fades away. ");
                    tnou("You no longer have a sword, let's hope you don't encounter a dragon! ");
                    objloc[objpt] = -rnd(15);
                    flags[EXCALIBER] = 0;
                } else {
                    tnoua(", it strikes a rock point first and goes through it like a knife through ");
                    tnou(" butter leaving only the hilt protruding from the surface. ");
                    objloc[objpt] = here;
                    flags[EXCALIBER]++;
                }
                showtext();
                values[HELD]--;
                if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                return;
            }
            }
            DAGGER -> {
tnou(", it hits the ground and disappears in a shower of sparks. ");
            showtext();
            values[HELD]--;
            if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
            objloc[objpt] = -rnd(15);
            return;
            }
            ROD -> {

            }
            STAFF -> {
tnoua(", it sticks into the ground and immediately sprouts leaves and tiny, scented flowers. ");
            tnou("However, these rapidly shrivel away to nothing. ");
            showtext();
            objloc[objpt] = here;
            values[HELD]--;
            if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
            return;
            }
            ROPE -> {
if (z < 2 || flags[GLOW] == 0) {
                tnou(", it lands on the ground nearby. ");   /* Above ground or mysterious glow */
            } else {
                tnoua(", it stands straight up, unsupported!! A fakir scrambles nimbly down, gathers it up and ");
                tnoua("disappears into the shadows. As he goes a small, brilliant `object` falls from his loincloth ");
                tnou("and rolls away out of sight. ");
                values[RUNOUT] = moveno;
                showtext();
                score += 50;
                objloc[DIAMOND] = -rnd(5);               /* The diamond now exists somewhere nearby */
                gone[DIAMOND] = 0;
                objloc[ROPE] = -2000;
                values[HELD]--;
                if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                return;
            }
            }
            COINS -> {
if (values[DWFNUM] == 0) {
                tnou(", they land on the ground near you. ");              /* No dwarves to steal the coins */
            } else {
                tnoua(". A little dwarf scurries up, grabs the coins and disappears before you can catch him. ");
                values[RUNOUT] = moveno;
                showtext();
                score -= 5;
                values[HELD]--;
                if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                objloc[COINS] = -rnd(15);
                return;
            }
            }
            BOOK -> {
if (monstr[TROLL] == 0) {
                tnou(", but there is no-one here to throw the book at - except, perhaps, you! ");
                showtext();
                objloc[BOOK] = here;
                values[HELD]--;
                if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                return;
            } else {
                tnoua(" The troll catches it and retires to a corner to read it. Suddenly he chortles");
                tnoua(" \"At last I have found how to get rid of them pesky dwarves!\" He lumbers over, ");
                tnou("shakes you roughly by the hand and vanishes forever, taking the book with him. ");
                values[RUNOUT] = moveno;
                monstr[TROLL] = 0;
                score += 50;
                secure[BOOK] = 0;
                gone[BOOK] = 1;
                objloc[BOOK] = -2000;
            }
            }
            else -> {
objloc[objpt] = here;
            }
        }
    showtext();
    values[HELD]--;
    if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;

    }

private fun inventory() {
        var i = 0; var j = 0; var penult = 0
        var count = 0
        val notinbasket = intArrayOf(0, LAMP, SWORD, STAFF, CARPET) /* These don't go into the basket */

    if (values[HELD] == 0) {
        tnoua("You are holding nothing");
    } else {
        penult = values[HELD] - 1;
        tnoua("You are carrying ");
        /* He has the basket, list in the order lamp, staff, sword, carpet, basket, then the remainder in order */
        if (objloc[BASKET] == 0) {
            for (i in 1 until 5) {
                if (objloc[notinbasket[i]] == 0) {
                    tnoua(thingpref[i]);
                    tnoulca(thing[i]);
                    count++;
                    if (count == values[HELD]) break;
                    if (count < penult) tnoua(", ");
                }
            }
            if (count > 0) tnoua(" and ");
            tnoua("a small wicker basket");
            count++;
            if (count < values[HELD]) tnoua(" containing ");
            for (i in 1 until MAXOBJ) {
                if (objloc[i] == 0) {
                    if (i == LAMP || i == SWORD || i == STAFF || i == CARPET || i == BASKET) continue;
                    count++;
                    tnoua(thingpref[i]);
                    tnoulca(thing[i]);
                    if (count == penult) tnoua(" and ");
                    if (count < penult) tnoua(", ");
                }
            }
        } else /* No basket, list the objects in their normal order */
        {
            for (i in 1 until MAXOBJ) {
                if (objloc[i] == 0) {
                    count++;
                    tnoua(thingpref[i]);
                    tnoulca(thing[i]);
                    if (count == penult) tnoua(" and ");
                    if (count < penult) tnoua(", ");
                }
            }
        }
    }
    if (flags[RINGON] != 0) tnoua(". You are wearing a golden ring on your finger");
    tnou(". ");
    showtext();

    }

private fun kill_it(objpoint: Int) {
        var objpoint = objpoint

    if (monstr[objpoint] < 1) {
        tnoua("What ");
        tnoulca(beasts[objpoint]);
        tnou("? ");
        showtext();
        return;
    }
    if (objloc[STAFF] == 0) {
        tnou("You flail about you with the staff but hit nothing. ");
        showtext();
        return;
    }
    when (objpoint) {
            0 -> {
tnou("Kill what? ");
            }
            VAMPIRE -> {
tnou("You have nothing that could possibly hurt it! ");
            }
            DRAGON -> {
if (objloc[SWORD] == 0) {
                if (flags[EXCALIBER] == 1) {
                    tnoua("As the dragon rears up you plunge your sword into its breast. It disappears up to the ");
                    tnoua("hilt and there is a tremendous explosion which knocks you senseless. When you recover the ");
                    tnou("dragon and sword are nowhere to be seen. Wisps of smoke drift about but gradually disperse. ");
                    score += 50;
                    monstr[DRAGON] = 0;
                    values[HELD]--;
                    if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                    objloc[SWORD] = -30;   /* Create another, lesser sword for him to find */
                    secure[SWORD] = 0;
                    flags[EXCALIBER]++;
                } else {
                    tnoua("You strike at the dragon with your sword but it parries your blow with its iron claws. ");
                }
            } else {
                tnou("You lash out but it easily avoids your blow. ");
            }
            }
            TROLL -> {
if (objloc[DAGGER] == 0) {
                tnoua("You stab the troll with the dagger, shattering the blade on its rock hard hide. ");
                tnou("The troll snatches the haft from you and grinds it to dust.");
                values[HELD]--;
                if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                objloc[DAGGER] = -2000;
                secure[DAGGER] = 0;
                gone[DAGGER] = 1;
            }
            if (objloc[SWORD] == 0) {
                tnoua("You strike the troll with the sword but the blade bounces back from its stony hide, ");
                tnou("severely jarring your arm. The troll does not even appear to have noticed the blow. ");
            }
            }
        }
    showtext();

    }

private suspend fun look() {

    describe(1);                   /* Describe where we are             */
    showthings();                  /* Describe any objects found here   */
    monsters();                    /* Display active monsters           */

    }

private fun lucky() {

    if (values[LUCK] >= 30) return;
    else if (values[LUCK] >= 20) tnou(" It must be your lucky day. ");
    else if (values[LUCK] >= 10) tnou(" You are pushing your luck. ");
    else if (values[LUCK] >= 5) tnou(" Your luck won't hold out for ever. ");
    else tnou(" You are almost out of luck! ");
    showtext();

    }

private fun onlamp() {

    if (objloc[LAMP] == 0) {
        if (values[FLICK] > 3) {
            tnou("You cannot light it any more!");
            showtext();
            return;
        }
        if (flags[LAMPON] != 0) {
            tnou("It is already on!");
        } else {
            tnou("Your lamp is now lit.");
            flags[LAMPON] = 1;
            flags[DARK] = 0;
        }
    } else {
        if (objloc[GOLDRING] == 0) {
            tnou("You put the ring on your finger. ");
            flags[RINGON] = 1;
            values[HELD]--;
            if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
            objloc[GOLDRING] = -1000;
        } else {
            tnou("You haven't got it!");
        }
    }
    showtext();

    }

private fun offlamp() {

    if (objloc[LAMP] == 0) {
        if (flags[LAMPON] != 0) {
            tnou("Your lamp is now off.");
            flags[LAMPON] = 0;
        } else {
            tnou("The lamp is already off!");
        }
    } else {
        if (flags[RINGON] != 0) {
            if (flags[HANDSFULL] != 0) {
                tnou("You can't, your hands are full! ");
            } else {
                tnou("You take the ring off your finger. ");
                flags[RINGON] = 0;
                values[HELD]++;
                objloc[GOLDRING] = 0;
            }
        } else {
            tnou("You haven't got it!");
        }
    }
    showtext();

    }

private suspend fun openit(objpoint: Int) {
        var objpoint = objpoint


    if (objpoint == 0) {
        tnou("Open what? ");
        showtext();
        return;
    }
    if (objloc[objpoint] != 0) {
        tnou("But you haven't got it! ");
    } else {
        when (objpoint) {
            BOOK -> {
readit(objpoint);
                return;
            }
            BOX -> {
if (flags[BOXLOK] != 0) {
                    tnou("The box is locked and you have nothing to open it with! ");
                } else {
                    tnou("You open the box, inside the lid is a label addressed : ");
                    tnou("\t\"Pandora, c/o Zeus, Mount Olympus\"");
                    tnoua("There is a little Hope still left in the bottom but ");
                    tnoua("fortunately for you the box is otherwise empty! ");
                    tnou("You take some Hope and quickly close it again. ");
                }
            }
            OYSTER -> {

            }
            CLAM -> {
eat(objpoint);
                return;
            }
            else -> {
tnou("Don't be silly, I can't do that! ");
            }
        }
    }
    showtext();

    }

private suspend fun quit(act: Int) {
        var act = act
        var ipt = 0; var scoral = 0

    if (act == 1) {
        tonl(1);
        tnoua("Do you really want to quit now? ");
        showtext();
        if (yesno() == 0) return;
        tonl(1);
        tnoua("Very well. ");
    }
    while (true) {
        if (flags[UNDEAD] != 0) {
            tnou("Sorry, vampires don't score! ");
            break;
        }
        if (flags[WIZARD] != 0) {
            tnou("Wizards minds are on higher things and they don't bother to count the score!");
            break;
        }
        scoral = scorit();
        tnoua("You scored ");
        tnoint(scoral);
        tnoua(" points in ");
        tnoint(moveno);
        tnoua(" moves. ");
        if (scoral < 100) {
            tnoua("You are obviously a rank amateur");
            if (scoral < 0) {
                tnoua(" and a bankrupt to boot. If you don't settle your debts ");
                tnoua("promptly we will be obliged to send the boys round");
            }
            tnou("! ");
        } else {
            ipt = scoral / 100;
            if (ipt > 10) ipt = 10;
            tnoua("Your score qualifies you as ");
            tnoulca(classes[ipt]);
            tnoua(" adventurer.");
            if (ipt == 10) tnou(" Congratulations!! ");
        }
        break;
    }
    tonl(1);
    showtext();
    
    myExit(0);

    }

private fun readit(objpoint: Int) {
        var objpoint = objpoint

    if (objpoint == 0) {
        tnou("Read what?");
    } else {
        if (objloc[objpoint] != 0) {
            tnou("But you haven't got it!");
        } else {
            when (objpoint) {
            MANUSCRIPT -> {
tnoua("It is written in old English, apparently by some monk named ");
                    tnoua("Bede, but you can't make much of it. You leaf through it and ");
                    tnoua("find that someone has written a glowing description of ");
                    tnou("the Indian rope trick on a blank page. ");
            }
            BOOK -> {
tnoua("Unfortunately the book is written in the Gamic script used by ");
                    tnoua("the trolls and you can't understand a word of it. Someone ");
                    tnou("has scrawled \"Stamp 'em out!\" across the flyleaf. ");
            }
            MUSIC -> {
tnoua("How clever of you to be able to read music! Perhaps ");
                    tnou("you should try playing some too? ");
            }
        }
        }
    }
    showtext();

    }

private fun rub(objpoint: Int) {
        var objpoint = objpoint

    if (objpoint == 0) {
        tnou("What do you want to rub?");
    } else {
        if (objloc[objpoint] != 0) {
            tnou("But you don't have it!");
        } else {
            when (objpoint) {
            LAMP -> {
if (flags[GENIE] != 0) {
                        tnou("Nothing happens.");
                    } else {
                        tnoua("You rub the lamp until it gleams. Suddenly a genie appears. ");
                        tnoua("\"Free at last!\", he cries, \"you shall be rewarded with ");
                        tnoua("everlasting light.\" There is a flash and he disappears in a ");
                        tnoua("cloud of acrid smoke which tarnishes the gleaming surface of ");
                        tnou("the now brightly burning lamp.");
                        score += 10;
                        flags[GENIE] = 1;
                        flags[LAMPON] = 1;
                        values[LIGHT] = 1000000;
                        values[FLICK] = 0;
                    }
            }
            else -> {
tnou("Nothing happens.");
            }
        }
        }
    }
    showtext();

    }

private fun stamp(objpoint: Int) {
        var objpoint = objpoint

    if (`object` == "FOOT") {
        tnou("You accidentally stamp on your own foot. This makes you hopping mad. ");
        showtext();
        return;
    }
    if (objpoint == 0) {
        tnou("Stamp what? ");
        showtext();
        return;
    }
    if (values[DWFNOW] < 1 || objpoint != DWARF) {
        tnou("You stamp on the ground, but nothing happens. ");
        showtext();
        return;
    }
    when (rnd(3)) {
            1 -> {
tnoua("You try to stamp on ");
            if (values[DWFNOW] == 1) tnoua("the "); else tnoua("a ");
            tnou("little dwarf but he dodges out of the way in time. ");
            }
            else -> {
tnoua("You stamp on ");
            if (values[DWFNOW] == 0) {
                tnou("the ground but the little dwarf has gone. ");
            }
            if (values[DWFNOW] == 1) tnoua("the "); else tnoua("a ");
            tnoua("little dwarf and squash him flat. ");
            score += 10;
            values[DWFNOW]--;
            values[DWFNUM]--;
            if (values[DWFNOW] == 0) monstr[DWARF] = -monstr[DWARF];
            if (values[DWFNUM] == 0) monstr[DWARF] = 0;
            }
        }
    showtext();

    }

private fun timdat(n: Int) {
        var n = n
        var noon = 0; var hour = 0
    tnoua("It is ");
    tnoua(times[values[DAYTIME]]);
    tnoua(" on ");
    tnoua(weekdays[values[WEEKDAY]]);
    tnoua(" ");
    tnoint(values[DAY]);
    when (values[DAY]) {
            1 -> {

            }
            21 -> {

            }
            31 -> {
tnoua("st");
            }
            2 -> {

            }
            22 -> {
tnoua("nd");
            }
            3 -> {

            }
            23 -> {
tnoua("rd");
            }
            else -> {
tnoua("th");
            }
        }
    tnoua(" ");
    tnoua(months[values[MONTH]]);
    tnoua(".");
    if (values[DAYTIME] == 25 && n < 2) {
        tnou(" Mad dogs and Englishmen go out in the midday sun!");
    }
    showtext();

    }

private suspend fun magic() {

    if (action.take(5) != dicact[35].take(5)) /* Abbreviation is not allowed */
    {
        tnou("Pardon?");
        showtext();
        return;
    }
    if (values[MAGONE] > 7)                /* All used up */
    {
        tnou("Nothing happens!");
        showtext();
        return;
    }
    values[MAGONE]++;
    if (values[BASE] == here) {
        x = values[LASTX];
        y = values[LASTY];
        z = values[LASTZ]; /* Go to where you were last */
        here = locate(x, y, z);
    } else {
        values[LASTX] = x;
        values[LASTY] = y;
        values[LASTZ] = z; /* Remember where we are */
        x = values[FIRSTX];
        y = values[FIRSTY];
        z = 1;           /* and go to base camp   */
        here = locate(x, y, z);
    }
    describe(1);

    }

private fun scores() {
        var scoral = 0

    scoral = scorit();
    tnoua("Your current score is ");
    tnoint(scoral);
    tnoua(" in ");
    tnoint(moveno);
    tnoua(" move");
    if (moveno > 1) tnoua("s");
    tnou(". ");
    showtext();

    }

private fun scorit(): Int {
        var i = 0; var scoral = 0

    scoral = score;
    for (i in 1 until MAXOBJ) {
        if (secure[i] != 0) scoral += points[i];
    }
    return (scoral);

    }

private suspend fun slumber() {
        var i = 0

    if (flags[HOME] != 0 && flags[INTENT] == 0) {
        tnoua("You go into the tent. ");
        flags[INTENT] = 1;
        for (i in 1 until 15) ways[i] = 0;
        ways[OUT] = 1;
    }
    if (flags[INTENT] != 0) {
        tnoua("Yawning wearily, you lie down on the bed and fall into a deep, refreshing ");
        tnou("sleep. When you awake it is daybreak.");
        showtext();
        newday();                             /* Increment the date */
        values[DAYTIME] = values[DAWN];       /* and set the time   */
        flags[NIGHT] = 0;
        flags[DARK] = 0;
        return;
    }
    when (z) {
            0 -> {
/* Up a tree    */
            tnoua("Wedging yourself against the base of a large, forked branch you fall ");
            tnoua("into an uneasy sleep. ");
            if (rnd(4) == 1) {
                tnoua("During the night a leopard climbs up the tree and gets you! ");
                showtext();
                dead();
                return;
            } else {
                tnou("Fortunately a leopard doesn't visit your tree during the night. The birds arose you at dawn.");
                flags[NIGHT] = 0;
                flags[DARK] = 0;
                newday();                        /* Increment the date */
                values[DAYTIME] = values[DAWN];  /* and set the time   */
            }
            }
            1 -> {
/* On the plain */
            tnoua("You burrow into some long grass nearby and fall asleep, hoping that ");
            tnoua("none of the large cats find you. Whilst you slumber a deadly scorpion ");
            tnoua("crawls down the neck of your shirt. Feeling something scratching you ");
            tnoua("suddenly start up and the scorpion stings you before you can remove ");
            tnou("it. You have no antidote to the poison and rapidly succumb.");
            showtext();
            dead();
            return;
            }
            else -> {
/* Underground  */
            if (flags[SHAFT] != 0) {
                tnou("You are certain to 'drop off' if you fall asleep here, try somewhere else. ");
            } else {
                tnoua("You prop yourself against a wall and fall into a deep sleep. The elves carry you back to ");
                tnoua("ground level. They then steal all your belongings and run off. Fortunately they soon get ");
                tnou("bored and throw them away. If you hunt about you might find some of them. ");
                flags[NIGHT] = 0;
                flags[DARK] = 0;
                newday();                        /* Increment the date */
                values[DAYTIME] = values[DAWN];  /* and set the time */
                values[HELD] = 0;
                flags[LAMPON] = 0;
                for (i in 1 until MAXOBJ) {
                    if (objloc[i] == 0) objloc[i] = -rnd(10); /* Scatter his belongings about but not too far */
                }
                x = values[FIRSTX];
                y = values[FIRSTY];
                z = 1;
                here = locate(x, y, z);
                flags[HOME] = 1;
                for (i in 1 until 11) ways[i] = 1;
                ways[DOWN] = 0; ways[UP] = 0;
            }
            }
        }
    showtext();

    }

private suspend fun status() {
        var len = 0; var yn = 0
        var password = ""


    if (flags[WIZARD] != 0) return;   /* We already know that he is a wizard */
    tonl(1);
    tnoua("Are you in fact a genuine wizard? ");
    showtext();
    yn = yesno();
    if (yn != 1) return;         /* He doesn't claim to be              */
    tonl(1);
    tnoua("Prove it - say the magic word : ");
    showtext();
    len = get_password();
    if (len == 0) {
        tonl(1);
        tnou("Chicken!");
        showtext();
        return;
    }
    password = ""    /* Construct the password */
    val timePart = times[values[DAYTIME]].take(2)
    val monthPart = months[values[MONTH]].take(2)
    val weekdayPart = weekdays[values[WEEKDAY]].take(2)
    val dayPart = values[DAY].toString().padStart(2, '0')
    password = "$timePart$monthPart$weekdayPart$dayPart"
    if (password.take(8) == userpass.take(8)) {
        tnou("I am yours to command Oh Master! ");
        flags[WIZARD] = 1;
        password = ""  /* Destroy the password */
        dicact[22] = ""
        dicact[22] = "."
        tonl(1);
        tnoua("The 'WIZARD' command has been replaced by '.', which allows you to examine various parameters, ");
        tnoua("set your location co-ordinates, obtain objects at will and summon monsters. Typing '.' ");
        tnoua("actives the '[LFPVSOM]> ' prompt, following which typing any of the letters L, F, P, etc. ");
        tnoua("displays information or allows you to set or summon things. ");
        tonl(1);
        tnou("Briefly the wizard commands are: ");
        tnou("L[ocation]:  x,y,z co-ordinates, legal move directions and last action and `object` typed. ");
        tnou("F[lag] status: Home, In tent, Dark, Night, Lamp on ");
        tnou("P[lain] status flags: Rolling, High Grass, Animals, Animal Herd, Carnivores, Seen by carnivores. ");
        tnoua("V[alues]: Daytime, Dawn, Dusk, Luck, Objects held, Dwarf popluation, No. of dwarves active, ");
        tnoua("Activation status of bats, dwarves, snake, gorgon, elf, troll, dragon and vampire ");
        tnou("active=>0, inactive=<0, dead=0. ");
        tnou("S[et]: Set x,y,z co-ordinates. ");
        tnou("O[bject]: Fetch objects. ");
        tnou("M[onster]: Activate monsters. ");
        tonl(1);
        tnou("n.b. Wizards, having special powers, can cheat and thus do not get a final score. ");
    } else {
        tnou("Foo, you are just an imposter! That little piece of deception will cost you 10 points. ");
        score -= 10;
    }
    showtext();

    }

private suspend fun adventure() {
        var i = 0; var first = 1; var len = 0; var forever = 1; var match = 0
        var command = ""

    if (action.take(4) != "ADVE".take(4)) {
        tnou("Pardon?");
        showtext();
        return;
    }

    while (forever == 1) {
        if (first == 1) {
            tonl(1);
            tnoua("You are standing at the end of a road before a small brick building.");
            tnoua("Around you is a forest. A small stream flows out of the building and");
            tnou("down a gully and a wide path leads northwest.");
            tonl(1);
            showtext();
            first = 0;
        }
        tnoua("? ");
        showtext();
        command = "";
        match = 0;
        command = getLine().trim(); if (command.endsWith("\n")) command = command.substring(0, command.length - 1).trim(); len = command.length
        if (len == 0) continue;
        command = command.uppercase()
        if (command == "IN") match = 1;
        if (command == "ENTER") match = 1;
        if (command == "PLUGH") match = 2;
        when (match) {
            1 -> {
tonl(1);
                tnoua("You are inside the building, a well house for a large spring ");
                tonl(1);
                tnou("There are some keys on the ground here. ");
                tnou("There is a shiny brass lamp nearby. ");
                tonl(1);
                showtext();
            }
            2 -> {
adv_plugh();
                first = 1;
                continue;
            }
            else -> {
tonl(1);
                tnoua("A large cloud of green smoke appears in front of you. It clears away ");
                tnoua("to reveal a tall wizard, clothed in grey. He fixes you with a steely ");
                tnoua("glare and declares, \"This adventure has lasted too long.\" With that he ");
                tnoua("makes a single pass over you with his hands, and everything around you ");
                tnou("fades away into a grey nothingness. ");
                showtext();
                sleep(5);
                tonl(1);
                tnoua("You awake as from a bad dream to find yourself in your tent. The sun is rising outside. ");
                tnoua("Everything you were carrying has vanished, perhaps you left it behind when you started ");
                tnou("your adventure. ");
                showtext();
                for (i in 1 until MAXOBJ) {
                    if (objloc[i] == 0) objloc[i] = here; /* Drop everything here */
                }
                if (objloc[LAMP] == here) objloc[LAMP] = values[BASE]; /* If he has the lamp put it outside the tent */
                flags[HOME] = 1;
                flags[INTENT] = 1;
                flags[DARK] = 0;
                x = values[FIRSTX];
                y = values[FIRSTY];
                z = 1;
                here = locate(x, y, z);
                newday();                                /* Start a new day   */
                values[DAYTIME] = values[DAWN];          /* and set the time  */
                values[LIGHT] = 100;
                values[HELD] = 0;
                return;
            }
        }
        continue;
    }

    }

private suspend fun adv_plugh() {
        var i = 0; var len = 0; var match = 0; var kount = 0; var forever = 1
        var command = ""

    tonl(1);
    tnoua("There is a brilliant flash of light and a sudden fanfare of trumpets! ");
    tnou("When your eyes recover from the flash, you find that:");
    tnoua("You are in a large room, with a passage to the south, a passage to the ");
    tnoua("west, and a wall of broken rock to the east. There is a large \"Y2\" on ");
    tnoua("a rock in the room's centre.");
    tonl(1);
    showtext();

    while (forever != 0) {
        tnoua("? ");
        showtext();
        command = "";
        kount++;
        command = getLine().trim(); if (command.endsWith("\n")) command = command.substring(0, command.length - 1).trim(); len = command.length
        if (len == 0) continue;
        command = command.uppercase()
        if (command == "Y2") {
            tonl(1);
            tnou("That's where you are now!");
            showtext();
            continue;
        }
        if (command == "PLUGH") {
            tonl(1);
            tnou("OK!");
            showtext();
            return;
        }
        if (kount > 20) /* Obviously in trouble, drop a hint */
        {
            tonl(1);
            tnoua("You seem to be stuck here, but keep plugging away at ");
            tnou("it and you will eventually work out how to escape. ");
            showtext();
            kount -= 5;
            continue;
        }
        when (rnd(8)) {
            3 -> {
tnou("Sorry, I don't understand that here. ");
            }
            5 -> {
tnou("I'm afraid that I am temporarily suffering from partial amnesia. ");
            }
            7 -> {
tnou("If at first you don''t succeed, try, try, try again! ");
            }
        }
        showtext();
    }

    }

private fun lookwhere(text: String) {
        var text = text

    if (here == values[BASE]) {
        tonl(1);
        if (flags[INTENT] != 0) {
            tnou("You are in the tent at your base camp.");
        } else tnou("At your base camp.");
        showtext();
        return;
    }
    calcdist();
    if (z > 1) {
        tnou("You are in a complex system of underground chambers, passages and shafts.");
        showtext();
        return;
    }
    tnoua("You are ");
    if (distance == 1) tnoua("quite close to ");
    if (distance == 2) tnoua("some distance from ");
    if (distance == 3) tnoua("several miles away from ");
    if (distance > 3) tnoua("many miles away from ");
    tnoua(text);
    tnoua(", which ");

    if (flagxy == 0) {
        tnoua("lies due ");
        if (ydist > 0) tnoua("south");
        if (ydist < 0) tnoua("north");
        if (xdist > 0) tnoua("west");
        if (xdist < 0) tnoua("east");
    } else {
        tnoua("lies to the ");
        if (ydist > 0) tnoua("south");
        if (ydist < 0) tnoua("north");
        if (kotlin.math.abs(xdist) != kotlin.math.abs(ydist))tnoua(" and ");
        if (xdist > 0) tnoua("west");
        if (xdist < 0) tnoua("east");
    }
    tnou(". ");
    showtext();

    }

private fun calcdist() {

    xdist = x - values[FIRSTX];
    ydist = y - values[FIRSTY];
    flagxy = kotlin.math.abs(xdist * ydist);  /* If this is 0 then one of them is zero */
    distance = (kotlin.math.sqrt((xdist * xdist + ydist * ydist).toDouble())).toInt();

    }

private fun unknown() {

    when (pseudorand) {
            1 -> {
tnou("What?");
            }
            3 -> {
tnou("Sorry, I don't understand.");
            }
            else -> {
tnou("Pardon?");
            }
        }
    showtext();

    }

private fun unlock(objpoint: Int) {
        var objpoint = objpoint

    if (objpoint == 0) {
        tnou("Pardon? ");
        showtext();
        return;
    }
    if (objloc[KEYS] != 0) {
        tnou("You haven't any keys! ");
    } else {
        when (objpoint) {
            BOX -> {
if (objloc[BOX] != 0) {
                    tnou("You haven't got the box! ");
                }
                if (flags[BOXLOK] != 0) {
                    tnou("The box is now unlocked. ");
                    flags[BOXLOK] = 0;
                } else {
                    tnou("Why bother, it isn't locked. ");
                }
            }
            else -> {
tnou("You can't unlock that! ");
            }
        }
    }
    showtext();

    }

private fun wave(objpoint: Int) {
        var objpoint = objpoint

    if (objpoint == 0) {
        tnou("What do you want to wave?");
    } else {
        if (objloc[objpoint] != 0) {
            tnou("But you haven't got it!");
        } else {
            when (objpoint) {
            ROD -> {
if (flags[GLOW] == 0 && flags[WAVER] != 0) {
                        tnou("Nothing happens!");
                    } else {
                        tnoua("A tall elderly wizard dressed in shimmering white ");
                        tnoua("robes appears, takes the rod and thanks you politely ");
                        tnoua("for finding his missing magic wand. ");
                        tnoua("\"Gandalf's magic anagram is convenient\", he says, ");
                        tnoua("\"but you can only use it seven times.\" ");
                        tnou("He vanishes as suddenly as he came. ");
                        values[RUNOUT] = moveno;
                        objloc[ROD] = -2000;
                        gone[ROD] = 1;
                        score += 50;
                        values[HELD]--;
                        if (flags[HANDSFULL] == 1) flags[HANDSFULL] = 0;
                    }
            }
            else -> {
tnou("Nothing happens!");
            }
        }
        }
    }
    showtext();

    }

    }

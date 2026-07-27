package jni

import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.AppData
import club.gepetto.GcLog
import java.io.File
import java.util.Random
import kotlinx.coroutines.*
import com.funhouse.shared.common.utils.GcInputQueue

class LoadFailedException : Exception("Load failed")

class WizardsCastleKotlin : BaseKotlinGame() {

    class Room(val contents: String, val id: String)

    companion object {
        // Shared game constants / dictionaries
        val dungeon = arrayOf(
            Room("AN EMPTY ROOM", "."),
            Room("THE ENTRANCE", "E"),
            Room("STAIRS GOING UP", "U"),
            Room("STAIRS GOING DOWN", "D"),
            Room("A POOL", "P"),
            Room("A CHEST", "C"),
            Room("GOLD PIECES", "G"),
            Room("FLARES", "F"),
            Room("A WARP", "W"),
            Room("A SINKHOLE", "S"),
            Room("A CRYSTAL ORB", "O"),
            Room("A BOOK", "B"),
            Room("A KOBOLD", "M"),
            Room("AN ORC", "M"),
            Room("A GOBLIN", "M"),
            Room("AN OGRE", "M"),
            Room("A TROLL", "M"),
            Room("A HARPY", "M"),
            Room("A CYCLOPS", "M"),
            Room("A MINOTAUR", "M"),
            Room("A GARGOYLE", "M"),
            Room("A CHIMERA", "M"),
            Room("A BALROG", "M"),
            Room("A DRAGON", "M"),
            Room("A VENDOR", "V"),
            Room("THE RED RUBY", "T"),
            Room("THE NORN STONE", "T"),
            Room("THE PALE PEARL", "T"),
            Room("THE OPAL EYE", "T"),
            Room("THE GREEN GEM", "T"),
            Room("THE BLUE FLAME", "T"),
            Room("THE PALANTIR", "T"),
            Room("THE SILMARIL", "T"),
            Room("", "?")
        )

        val cause = arrayOf(
            "NO WEAPON", "DAGGER", "MACE", "SWORD",
            "NO ARMOR", "LEATHER", "CHAINMAIL", "PLATE"
        )

        val species = arrayOf("HOBBIT", "ELF", "HUMAN", "DWARF")

        const val bad_response = "     ** PLEASE!!!  CHOOSE A REASONABLE RESPONSE."

        val gold = arrayOf(
            "ENGLISH SOVEREIGNS", "ARGENTINE PESOS", "SWISS DUCATS", "MEXICAN ESCUDOS",
            "PERUVIAN SOLES", "AMERICAN EAGLES", "SPANISH DOUBLOONS", "FRENCH FRANCS",
            "GERMAN DUCATS", "TURKISH PIASTRES", "INDIAN MOHURS", "AUSTRIAN KRONES",
            "RUSSIAN ROUBLES", "POLISH ZLOTYCHS"
        )

        val treasure = arrayOf(
            "Red Ruby", "Norn Stone", "Pale Pearl", "Opal Eye",
            "Green Gem", "Blue Flame", "Palantir", "Silmaril",
            "RED RUBY", "NORN STONE", "PALE PEARL", "OPAL EYE",
            "GREEN GEM", "BLUE FLAME", "PALANTIR", "SILMARIL"
        )

        val frog = arrayOf(
            " CRICKET", " STRIPED CHORUS", " SPRING PEEPER", " COMMON TREE",
            " GOPHER", " BULL", " PICKEREL", " LEOPARD",
            " WOOD", " NARROW-MOUTHED", " PLAINS", " GREEN",
            " CANYON", "n AMERICAN", " GARDEN", " COMMON MUSK",
            " YELLOW MUD", "n ALLIGATOR SNAPPING", " COMMON SNAPPING", " MAP",
            " CAROLINA BOX", "n ORNATE BOX", " FALSE MAP", " WESTERN PAINTED",
            "n ELEGANT SLIDER", "n EARLESS", " COLLARED", " ROUGH-SCALED",
            " TEXAS HORNED", " SHORT-HORNED", " BROWN", " COAL",
            " FIVE-LINED", " SONORAN", " PRAIRIE",
            "FROG", "FROG", "TOAD", "TURTLE", "TURTLE", "LIZARD", "SKINK"
        )

        val hint = arrayOf(
            "Physical attacks rarely kill Gargoyles",
            "Lizards and Skinks have gold for you",
            "It's dangerous to keep too much gold",
            "Go chase someone who can give you a race",
            "Many thanks to Joseph Power & Verne Walrafen",
            "Dragons are magical creatures",
            "Report any problems to Ms. Bird",
            "Leslie Bird (nightflyte@gmail.com)",
            "Gems are much nicer than gold",
            "Copy this PUBLIC DOMAIN game for your friends",
            "Only the quick afoot survive",
            "Do I look like a FROG?  Put me down",
            "You might have to kiss a lot of frogs",
            "Watch out for WITCHES...wicked and good",
            "There are three royal siblings",
            "Knowledge of ZOT's small gift proves WIN"
        )

        val royal = arrayOf("UGLY", "5 YEAR OLD", "HANDSOME", "PLAIN", "OLD MAID", "BEAUTIFUL")

        val curse = arrayOf("LETHARGY.", "THE LEECH.", "FORGETFULNESS.", "lethargy.", "the leech.", "forgetfulness.")

        val sss = arrayOf("", "SS", "FEMALE ", "MALE ", "NORTH", "SOUTH", "EAST", "WEST", "GOOD", "EVIL")

        val monster = arrayOf("Harpy", "Gargoyle", "Dragon", "Frog", "Toad", "Lizard", "Skink")

        val noise = arrayOf(
            "a scream", "footsteps", "a CRAZY programmer", "frogs croaking",
            "faint rustling noises", "see a BAT fly by", "find a VERY dead frog"
        )

        val command = arrayOf(
            "*NO", "YYES", "*YES", "NNO", "*ELF", "DDWARF", "MHUMAN", "HHOBBIT",
            "*LEATHER", "CCHAINMAIL", "PPLATE", "*SWORD", "NNONE", "DDAGGER", "MMACE", "******",
            "NNORTH", "EEAST", "SSOUTH", "WWEST", "LLOOK", "UUP", "DDOWN", "MMAP",
            "HHELP", "FFLARE", "CCATCH", "KKISS", "IDRINK", "RRELEASE", "OOPEN", "GGAZE",
            "TTELEPORT", "ZZOT", "QQUIT", "/", "*IGNORE", "TTRADE", "AATTACK", "******",
            "AATTACK", "BBRIBE", "CCAST", "RRETREAT", "DDEATH", "FFIREBALL", "WWEBB", "******",
            "MMALE", "FFEMALE", "*NEW", "OOLD"
        )

        const val strength = "STRENGTH"
        const val intelligence = "INTELLIGENCE"
        const val dexterity = "DEXTERITY"

        val effect = arrayOf(
            " SANDWICH", " STEW", " SOUP", " BURGER",
            " ROAST", " FILET", " TACO", " PIE"
        )
    }

    // Game state variables
    private val level = IntArray(512)
    private val c = Array(3) { IntArray(4) }
    private val o = IntArray(3)
    private val p = IntArray(16)
    private val r = IntArray(3)
    private val t = IntArray(8)
    private val w = Array(15) { IntArray(3) }

    private var x_axis = 0
    private var y_axis = 0
    private var z_axis = 0
    private var temp = 0
    private var master_game = 0
    private var dd = 0
    private var ee = 0
    private var ff = 0
    private var fl = 0
    private var gg = 0
    private var hh = 0
    private var wc = 0
    private var new_game = 0

    private var ah = 0
    private var bf = 0
    private var ot = 0
    private var tt = 0
    private var vf = 0
    private var lf = 0
    private var tc = 0
    private var rf = 0
    private var of = 0
    private var bl = 0
    private var ss = 0
    private var pf = 0
    private var uu = 0
    private var vv = 0
    private var ww = 0

    private var attack = 0
    private var isfrog = 0
    private var gotfrog = 0
    private var startnewgame = 0
    private var alive = 0
    private var done = 0
    private var quit = 0
    private var escape = 0

    // Random configuration
    private var random = Random()

    // IO Buffer
    private val inputQueue = GcInputQueue<String>()
    private var inputBuffer = ""
    private var inputBufferPos = 0
    private var gameJob: Job? = null

    override fun start() {
        GcLog.d("WizardsCastleKotlin.start() called")
        gameJob?.cancel()
        inputQueue.clear()
        inputBuffer = ""
        inputBufferPos = 0
        greetings()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                try {
                    runGame()
                    break
                } catch (e: LoadFailedException) {
                    GcLog.d("Load failed, restarting game after 2 seconds")
                    try {
                        delay(2000)
                    } catch (ie: CancellationException) {
                        break
                    }
                } catch (e: CancellationException) {
                    GcLog.d("WizardsCastle game job cancelled")
                    break
                } catch (e: Exception) {
                    GcLog.e("Error running Wizards Castle", e)
                    break
                }
            }
        }
    }

    override fun stop() {
        super.stop()
        gameJob?.cancel()
    }

    override fun start(gameNickName: String) {
        start()
    }

    override fun sendCommand(command: String): Int {
        if (command.equals("about", ignoreCase = true)) {
            val aboutText = wizardscastlekotlin.utils.defaultAbout
            myPrintf("%s\n", aboutText)
            return 0
        }
        inputQueue.put(command + "\n")
        return 0
    }

    private fun sleep(m: Long) {
        try {
            Thread.sleep(m)
        } catch (e: Exception) {}
    }

    // Helper functions mapping to original C definitions
    private fun roll(s: Int): Int {
        if (s <= 0) return 0
        return random.nextInt(s) + 1
    }

    private fun below_nine(s: Int): Int {
        var v = s
        if (v == 9) v = 1
        if (v == 0) v = 8
        return v
    }

    private fun below_nineteen(s: Int): Int {
        return if (s > 18) 18 else s
    }

    private fun func_d(): Int {
        return ((64 * (z_axis - 1)) + (8 * (x_axis - 1)) + y_axis)
    }

    private fun func_e(): Int {
        val idx = func_d() - 1
        val i = if (level[idx] > 99) -1 else 0
        return level[idx] + 100 * i
    }

    private fun roll_12(): Int {
        return roll(12) + 12
    }

    private fun setup_xy(s: Int): Int {
        do {
            x_axis = roll(8)
            y_axis = roll(8)
        } while ((level[func_d() - 1] != 101) || ((x_axis == z_axis) && (y_axis == z_axis)))
        level[func_d() - 1] = s
        return 0
    }

    private suspend fun alloc_points(s: String): Int {
        var i = 0
        do {
            myPrintf("How many points added to %s? ", s)
            val buf = getLine()
            i = try { buf.toInt() } catch (e: Exception) { -1 }
            if (i < 0 || i > ot) {
                myPrintf("\n** ")
            }
        } while (i < 0 || i > ot)
        ot -= i
        return i
    }

    private fun ident() {
        myPrintf("\n--- You are a %s%s at (%d,%d) LEVEL %d ---\n", sss[ss], species[dd - 1], x_axis, y_axis, z_axis)
    }

    private fun splash() {
        myPrintf("\n          THE WIZARD'S CASTLE\n\n                 Reinvented: 11/20/09\n\n                 By: Leslie S. Bird\n\n")
    }

    private fun initialize() {
        master_game = 0
        for (n in 0 until 15) {
            for (m in 0 until 3) {
                w[n][m] = 0
            }
        }
    }

    private suspend fun peekChar(): Char {
        while (true) {
            if (inputBufferPos < inputBuffer.length) {
                return inputBuffer[inputBufferPos]
            }
            val raw = inputQueue.take()
            inputBuffer = raw
            inputBufferPos = 0
        }
    }

    private suspend fun getChar(): Char {
        while (true) {
            if (inputBufferPos < inputBuffer.length) {
                val c = inputBuffer[inputBufferPos]
                inputBufferPos++
                return c
            }
            val raw = inputQueue.take()
            inputBuffer = raw
            inputBufferPos = 0
        }
    }

    private suspend fun getLine(): String {
        while (true) {
            val c = peekChar()
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                getChar()
            } else {
                break
            }
        }
        val sb = StringBuilder()
        while (true) {
            val c = peekChar()
            if (c == '\n') break
            sb.append(getChar())
        }
        return sb.toString().trim()
    }

    private suspend fun fancy_input(p: Int): Int {
        var buf: Char
        while (true) {
            buf = getChar()
            if (buf != ' ' && buf != '\n' && buf != '\r' && buf != '\t') {
                break
            }
        }
        val c = buf.uppercaseChar()
        val o_val = p / 100
        var o = o_val
        val limit = p - o_val * 100
        for (n in o_val..limit) {
            if (c == command[n - 1][0]) {
                o = n
            }
        }
        for (n in 1 until command[o - 1].length) {
            myPrintf("%c", command[o - 1][n])
        }
        myPrintf("\n")
        return o
    }

    private suspend fun intro() {
        myPrintf("\n\nMany cycles ago, in the kingdom of N'Dic, the gromic\n")
        myPrintf("Wizard ZOT forged his great *ORB* of power.  He soon\n")
        myPrintf("vanished, leaving behind his vast subterranean cast-\n")
        myPrintf("le filled with esurient monsters, fabulous treasures\n")
        myPrintf("and the incredible ***ORB OF ZOT***.  From that time\n")
        myPrintf("hence many a bold youth has ventured into the castle\n")
        myPrintf("but, as of now, NONE has ever emerged victoriously!\n")
        myPrintf("\n                      BEWARE!!\n")
    }

    private suspend fun master_class() {
        startnewgame = 0
        alive = 1
        quit = 0

        for (n in 1..512) {
            if (level[n - 1] > 99) {
                level[n - 1] = level[n - 1] - 100
            }
        }

        for (n in 1..7) {
            do {
                x_axis = roll(8)
                y_axis = roll(8)
                z_axis = roll(8)
                var match = false
                for (m in 1..n) {
                    if (w[m - 1][0] == x_axis && w[m - 1][1] == y_axis && w[m - 1][2] == z_axis) {
                        x_axis = roll(8)
                        y_axis = roll(8)
                        z_axis = roll(8)
                        match = true
                        break
                    }
                }
            } while (match || (level[func_d() - 1] < 13 || level[func_d() - 1] > 24) || (x_axis == z_axis && y_axis == z_axis))

            w[n - 1][0] = x_axis
            w[n - 1][1] = y_axis
            w[n - 1][2] = z_axis
        }

        for (n in 8..15) {
            var match = false
            do {
                x_axis = roll(8)
                y_axis = roll(8)
                z_axis = roll(8)
                match = false
                for (m in 1..n) {
                    if (w[m - 1][0] == x_axis && w[m - 1][1] == y_axis && w[m - 1][2] == z_axis) {
                        x_axis = roll(8)
                        y_axis = roll(8)
                        z_axis = roll(8)
                        match = true
                        break
                    }
                }
            } while (match)
            w[n - 1][0] = x_axis
            w[n - 1][1] = y_axis
            w[n - 1][2] = z_axis
        }

        x_axis = 1
        y_axis = 4
        z_axis = 1
    }

    private suspend fun beginner_class() {
        startnewgame = 0
        alive = 1
        quit = 0

        for (i in 126..133) {
            z_axis = roll(8)
            setup_xy(i)
        }
        val i_val = roll_12() + 100
        z_axis = roll(8)
        setup_xy(i_val)

        r[0] = x_axis
        r[1] = y_axis
        r[2] = z_axis

        val i_val2 = 109
        z_axis = roll(8)
        setup_xy(i_val2)

        o[0] = x_axis
        o[1] = y_axis
        o[2] = z_axis

        val i_val3 = 101

        for (a in 1..3) {
            z_axis = roll(8)
            setup_xy(i_val3)
            c[a - 1][0] = x_axis
            c[a - 1][1] = y_axis
            c[a - 1][2] = z_axis
            c[a - 1][3] = 0
        }
        bf = 0
        ot = 8
        tt = 1
        vf = 0
        lf = 0
        tc = 0
        gg = 60
        rf = 0
        of = 0
        bl = 0
        vv = 8
        ss = 0
        pf = 0

        for (i in 1..8) {
            p[i - 1] = 0
            p[i + 7] = 0
            t[i - 1] = 0
        }

        myPrintf("\n\nIs this an (O)ld game continued or a (N)ew game? ")
        temp = fancy_input(5152)

        if (temp == 52) {
            myPrintf("Please enter filename of game.")
            val zotsav = getLine()
            getChar() // consume trailing return
            try {
                val file = File(zotsav)
                if (!file.exists()) {
                    myPrintf("\n\nError. Unable to load file!\n\n")
                    myPrintf("ORB OF ZOT will restart in 2 seconds...\n")
                    throw LoadFailedException()
                }
                file.bufferedReader().use { reader ->
                    for (i in 1..512) {
                        level[i - 1] = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    }
                    for (i in 1..8) {
                        t[i - 1] = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    }
                    for (i in 1..16) {
                        p[i - 1] = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    }
                    for (i in 1..3) {
                        r[i - 1] = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                        o[i - 1] = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                        for (a in 1..4) {
                            c[i - 1][a - 1] = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                        }
                        for (a in 1..15) {
                            w[a - 1][i - 1] = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                        }
                    }
                    ah = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    bf = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    bl = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    dd = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    ee = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    ff = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    fl = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    gg = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    hh = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    lf = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    master_game = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    new_game = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    of = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    pf = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    rf = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    ss = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    tt = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    tc = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    uu = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    vv = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    vf = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    ww = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    wc = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    x_axis = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    y_axis = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                    z_axis = reader.readLine()?.trim()?.toInt() ?: throw LoadFailedException()
                }
            } catch (e: LoadFailedException) {
                throw e
            } catch (e: Exception) {
                myPrintf("\n\nError. Unable to load file!\n\n")
                myPrintf("ORB OF ZOT will restart in 2 seconds...\n")
                throw LoadFailedException()
            }
        } else {
            splash()
            myPrintf("Only shaded questions require use of the RETURN key!\n")
            dd = 2
            uu = 6
            ww = 10
            myPrintf("\nAll right, bold one.\n")
            myPrintf("     You may be (D)warf, (E)lf, Hu(M)an or (H)obbit.\n")
            myPrintf("\nCommand #%d? ", tt)
            tt++
            temp = fancy_input(508)

            if (temp == 6) {
                dd = 4
                uu = 10
                ww = 6
            }
            if (temp == 7) {
                dd = 3
                uu = 8
                ww = 8
            }
            if (temp == 8) {
                dd = 1
                uu = 4
                ww = 12
                ot = 4
            }
            while (temp < 49) {
                myPrintf("\nWhich sex do you prefer? ")
                temp = fancy_input(4850)
                if (temp == 48) {
                    myPrintf("\n     ** CUTE %s, REAL CUTE. TRY M OR F.\n", species[dd - 1])
                }
            }
            if (temp == 50) ss = 2
            if (temp == 49) ss = 3

            myPrintf("\nOK, %s, you have the following attributes:\n", species[dd - 1])
            myPrintf("     %s = %d %s = %d %s = %d\n", strength, uu, intelligence, vv, dexterity, ww)
            myPrintf("     and %d other points to allocate as you wish.\n\n", ot)

            uu += alloc_points(strength)
            if (ot > 0) {
                vv += alloc_points(intelligence)
            }
            if (ot > 0) {
                ww += alloc_points(dexterity)
                if (ot > 0) {
                    ww += ot
                }
            }
            getChar() // consume trailing return
            myPrintf("\nOK, %s, you have 60 gold pieces.\n", species[dd - 1])
            fl = 0
            wc = 0
            myPrintf("\nThese are the types of ARMOR you can buy:\n")
            myPrintf("     (N)one (L)eather-10 (C)hainmail-20 (P)late-30\n")
            myPrintf("\nCommand #%d? ", tt)
            tt++
            temp = fancy_input(913)

            if (temp == 9) ee = 1
            if (temp == 10) ee = 2
            if (temp == 11) ee = 3
            if (temp == 13) ee = 0

            gg -= ee * 10
            ah = ee * 7
            myPrintf("\nOK, bold %s, you have %d gold pieces left.\n", species[dd - 1], gg)
            myPrintf("\nThese are the types of WEAPONS you can buy:\n")
            myPrintf("     (N)one (D)agger-10 (M)ace-20 (S)word-30\n")
            myPrintf("\nCommand #%d? ", tt)
            tt++
            temp = fancy_input(1215)

            if (temp == 12) ff = 3
            if (temp == 13) ff = 0
            if (temp == 14) ff = 1
            if (temp == 15) ff = 2

            gg -= ff * 10

            if (gg > 19) {
                myPrintf("\nDo you want a lamp for 20 gold pieces? ")
                tt = 8
                temp = fancy_input(102)
                if (temp == 2) {
                    lf = 1
                    gg -= 20
                }
            }
            if (gg >= 1) {
                myPrintf("\nOK, %s, you have %d gold pieces left.\n", species[dd - 1], gg)
                tt = 9
                var loop_flares = true
                while (loop_flares) {
                    myPrintf("\nFlares cost 1 gold piece each.\n")
                    myPrintf("          How many do you want? ")
                    val buf = getLine()
                    temp = try { buf.toInt() } catch (e: Exception) { 0 }
                    if (temp > gg) {
                        myPrintf("\n     ** YOU CAN ONLY AFFORD %d.\n", gg)
                    } else {
                        loop_flares = false
                    }
                }
                getChar() // consume trailing return
                if (temp < 0) temp = 0
                fl += temp
                gg -= temp
            }

            splash()
            x_axis = 1
            y_axis = 4
            z_axis = 1
            myPrintf("\n     OK, %s, You are now entering the castle!\n\n", species[dd - 1])

            myPrintf("\n   The HELP menu is reached using (H)elp command.\n")
            myPrintf("   Castle level numbers increase as you go DOWN!\n\n")

            myPrintf("\n   Press RETURN when ready to continue, %s. ", species[dd - 1])
            getChar()
        }
    }

    private suspend fun runGame() {
        // Initialize Random
        random = Random(System.currentTimeMillis())

        new_game = 0
        quit = 0
        while (quit == 0) {
            new_game++
            if (master_game >= 2) {
                myPrintf("\n     You are now entering the MASTER CLASS game!\n")
                myPrintf("\n     In the future, after selection of character,\n")
                myPrintf("     you may select MASTER CLASS by using commands\n")
                myPrintf("     (/) and then (ZOT) at the Entrance.")
                myPrintf(" WELL DONE!\n")
                myPrintf("\n   A MONSTER GUARDS THE FROG THAT IS THE PRINCE%s.\n", sss[ss - 2])
                myPrintf("\n   Press RETURN when ready to continue, %s. ", species[dd - 1])
                getChar()
            } else {
                if (new_game <= 1) {
                    splash()
                    intro()
                }
            }

            for (index in 1..512) {
                level[index - 1] = 101
            }
            level[3] = 2
            for (z in 1..7) {
                z_axis = z
                for (n in 1..2) {
                    val index_val = 104
                    setup_xy(index_val)
                    level[func_d() + 63] = 103
                }
            }

            for (z in 1..8) {
                z_axis = z
                for (index_val in 113..124) {
                    setup_xy(index_val)
                }
                for (n in 1..3) {
                    for (index_val in 105..112) {
                        if (index_val != 108 || master_game != 2) {
                            setup_xy(index_val)
                        }
                    }
                    val index_val2 = 125
                    setup_xy(index_val2)
                }
            }

            if (master_game != 0) {
                master_class()
            } else {
                beginner_class()
            }

            while (startnewgame == 0) {
                if (bl == 0) {
                    ident()
                }

                if (master_game >= 2 && vv + ww <= 35) {
                    for (n in 8..15) {
                        if (w[n - 1][0] == x_axis && w[n - 1][1] == y_axis && w[n - 1][2] == z_axis) {
                            if (t[n - 8] != 0) {
                                myPrintf("\n")
                                myPrintf("     %s just ripped off ", dungeon[roll_12() - 1].contents)
                                myPrintf("%s!\n", dungeon[17 + n].contents)
                                tc--
                                t[n - 8] = 0
                                break
                            }
                        }
                    }
                }

                if (gg >= 1 && (gg >= 20000 - master_game * 5000 || roll(80) == 1)) {
                    val n = roll(gg / 3)
                    gg -= n
                    myPrintf("\n")
                    myPrintf("   A THIEF just stole %d of your GOLD PIECES!!\n", n)
                }

                myPrintf("\n %s = %d", strength, uu)
                myPrintf(" %s = %d", intelligence, vv)
                myPrintf(" %s = %d\n", dexterity, ww)
                myPrintf(" Treasures = %d", tc)
                myPrintf("  Flares = %d", fl)
                myPrintf("  Gold pieces = %d\n", gg)
                myPrintf("    Weapon = %s", cause[ff])
                myPrintf("  Armor = %s", cause[ee + 4])
                if (lf != 0) {
                    myPrintf("  and a LAMP.\n")
                } else {
                    myPrintf("\n")
                }

                val n_curses = c[0][3] + c[1][3] + c[2][3]
                if (n_curses != 0) {
                    myPrintf("  Curses = ")
                    for (m in 1..3) {
                        var index2 = 0
                        if (t[m * 2 - 2] != 0) index2 = 3
                        if (c[m - 1][3] != 0) {
                            myPrintf(".%s", curse[m + index2 - 1])
                        }
                    }
                    myPrintf(".\n")
                }

                if (rf + of != 0) {
                    myPrintf("  Magic implements = ")
                    if (rf != 0) myPrintf("RUNESTAFF! ")
                    if (of != 0) myPrintf("***ORB OF ZOT***!")
                    myPrintf("\n")
                    if (pf != 0) {
                        myPrintf("  The %s PRINCE", royal[3 * (ss - 1) - 1])
                        myPrintf("%s is at your side!\n", sss[ss - 2])
                    }
                }

                if (bl == 0) {
                    val i_y = y_axis
                    val j_x = x_axis
                    myPrintf("\n")
                    for (n in i_y - 1..i_y + 1) {
                        y_axis = below_nine(n)
                        if (n == i_y) {
                            myPrintf("     Here you find:   ")
                        } else {
                            myPrintf("                      ")
                        }
                        var index2_val = 0
                        for (m in j_x - 1..j_x + 1) {
                            x_axis = below_nine(m)
                            var idx = level[func_d() - 1]
                            if (n == i_y && m == j_x) {
                                idx = func_e()
                                index2_val = idx
                            }
                            if (idx >= 34) {
                                idx = 34
                            }
                            myPrintf("%s  ", dungeon[idx - 1].id)
                        }
                        if (n == i_y) {
                            myPrintf(" %s.\n", dungeon[index2_val - 1].contents)
                        } else {
                            myPrintf("\n")
                        }
                    }
                    y_axis = i_y
                    x_axis = j_x
                }

                if (tt - (tt / 500) * 500 == 0) {
                    myPrintf("\n")
                    myPrintf("          You hear a voice whisper.....ZOT!\n")
                }

                wc = 0
                val index_room = func_e()
                level[func_d() - 1] = index_room
                isfrog = 0
                escape = 0

                if (index_room == 1 && roll(9) == 1) {
                    myPrintf("\n     There are some %s eggs here.\n", monster[roll(7) - 1])
                }

                if (((index_room <= 6) || (index_room >= 11)) && (index_room <= 12)) {
                    if (roll(6) == 1) {
                        val n_frog = roll(35)
                        isfrog = (n_frog + 4) / 5
                        myPrintf("\n     On the floor is a %s %s!\n", frog[n_frog - 1], frog[34 + isfrog])
                        myPrintf("    ")
                    }
                }

                if (index_room == 7) {
                    myPrintf("\n            They are %s.\n", gold[roll(14) - 1])
                    val old_idx = index_room
                    val money = roll(z_axis * 50)
                    gg += money
                    myPrintf("     You found %d gold pieces, you now have %d.\n", money, gg)
                    level[func_d() - 1] = 1
                }

                if (index_room == 8) {
                    val found_flares = roll(5)
                    fl += found_flares
                    myPrintf("\n     You found %d flares, you now have %d.\n", found_flares, fl)
                    level[func_d() - 1] = 1
                }

                if (index_room == 9) {
                    if (o[0] == x_axis && o[1] == y_axis && o[2] == z_axis && temp == 33 && of == 0) {
                        myPrintf("\n")
                        myPrintf("     YOU JUST FOUND ***THE ORB OF ZOT***!\n")
                        myPrintf("          The RUNESTAFF has disappeared!\n")
                        rf = 0
                        of = 1
                        o[0] = 0
                        level[func_d() - 1] = 1
                        check_events()
                        process_command()
                        continue
                    } else {
                        if (rf != 0) {
                            myPrintf("\n     You are protected by the RUNESTAFF!\n")
                        } else {
                            x_axis = roll(8)
                            y_axis = roll(8)
                            z_axis = roll(8)
                            sleep(3000)
                        }
                    }
                }

                if (index_room == 10) {
                    z_axis = below_nine(z_axis + 1)
                    sleep(3000)
                }

                if (index_room in 26..33) {
                    myPrintf("\n            %s is now yours!\n", dungeon[index_room - 1].contents)
                    t[index_room - 26] = 1
                    tc++
                    level[func_d() - 1] = 1
                }

                if (index_room in 13..24) {
                    wc = 0
                    attack = level[func_d() - 1] - 12
                    if (attack < 13 || vf == 1) {
                        go_fight()
                    }
                }

                if (index_room == 25 && vf == 0) {
                    myPrintf("\nYou may (I)gnore, (A)ttack or (T)rade with VENDOR.\n")
                    myPrintf("\nCommand #%d? ", tt)
                    tt++
                    temp = fancy_input(3739)
                    if (temp == 39) {
                        myPrintf("\n     YOU'LL BE SORRY THAT YOU DID THAT!\n")
                        vf = 1
                        go_fight()
                    } else {
                        if (temp == 38) {
                            go_buy()
                        }
                    }
                }

                if (escape == 0 && alive == 1) {
                    if (index_room != 10 && index_room != 9) {
                        check_events()
                    }
                    if (index_room != 10 && (index_room != 9 || (index_room == 9 && rf != 0))) {
                        process_command()
                    }
                }

                if (alive == 0) {
                    go_die()
                }
            }
        }
    }

    private suspend fun check_events() {
        if (c[1][3] > t[2]) {
            gg -= roll(5)
            if (gg < 0) gg = 0
        }
        if (c[2][3] > t[4]) {
            val xt = x_axis
            val yt = y_axis
            val zt = z_axis
            x_axis = roll(8)
            y_axis = roll(8)
            z_axis = roll(8)
            level[func_d() - 1] = func_e() + 100
            x_axis = xt
            y_axis = yt
            z_axis = zt
        }
        for (n in 1..3) {
            var m = 0
            if (x_axis == c[n - 1][0] && y_axis == c[n - 1][1] && z_axis == c[n - 1][2] && c[n - 1][3] == 0) {
                c[n - 1][3] = 1
                m = 1
            }
            if (m != 0) {
                myPrintf("\n")
                myPrintf("     You just received the curse of %s\n", curse[n - 1])
            }
        }

        if (master_game == 1 && tc == 8) {
            master_game = 2
            startnewgame = 1
        } else {
            if (roll(5) == 1) {
                myPrintf("\n     You ")
                if (bl != 0) {
                    myPrintf("stepped on ")
                    if (roll(6) == 1) {
                        myPrintf("some %s eggs", monster[roll(7) - 1])
                    } else {
                        val fn = roll(35)
                        myPrintf("a %s %s", frog[fn - 1], frog[34 + (fn + 4) / 5])
                    }
                } else {
                    val n = roll(10)
                    if (n < 6) myPrintf("hear ")
                    if (n < 8) {
                        myPrintf("%s", noise[n - 1])
                    } else {
                        if (n == 8) {
                            myPrintf("find some crushed %s eggs", monster[roll(7) - 1])
                        }
                    }
                    if (n > 8) {
                        myPrintf("smell ")
                        if (n == 9) {
                            myPrintf("%s", dungeon[roll_12() - 1].contents)
                        } else {
                            myPrintf("something dead...%s", dungeon[roll_12() - 1].contents)
                        }
                    }
                }
                myPrintf("!\n")
            }

            if (bl + t[3] == 2) {
                myPrintf("\n     %s CURES YOUR BLINDNESS!\n", dungeon[28].contents)
                bl = 0
            }
            if (bf + t[5] == 2) {
                myPrintf("\n     %s DISSOLVES THE BOOK!\n", dungeon[30].contents)
                bf = 0
            }
        }
    }

    private suspend fun process_command() {
        done = 0
        while (done == 0) {
            myPrintf("\nCommand #%d? ", tt)
            tt++
            temp = fancy_input(1636)
            if (temp == 29) take_a_drink()
            if (temp == 28) kiss_frog()
            if (temp == 30) release_frog()
            if (temp == 27) catch_frog()

            if (temp == 20) {
                if (level[func_d() - 1] == 2) {
                    are_you_leaving()
                } else {
                    go_move()
                }
            }

            if (temp == 17 || temp == 18 || temp == 19) {
                go_move()
            }

            if (temp == 22) {
                if (level[func_d() - 1] == 3) {
                    z_axis--
                    done = 1
                } else {
                    myPrintf("\n     ** THERE ARE NO STAIRS GOING UP FROM HERE!\n")
                    check_events()
                }
            }

            if (temp == 23) {
                if (level[func_d() - 1] == 4) {
                    z_axis++
                    done = 1
                } else {
                    myPrintf("\n     ** THERE ARE NO STAIRS GOING DOWN FROM HERE!\n")
                    check_events()
                }
            }

            if (temp == 24) show_map()
            if (temp == 26) flares()
            if (temp == 21) lamp()
            if (temp == 31) open_thing()
            if (temp == 32) gaze_orb()
            if (temp == 35) save_game()

            if (temp == 33) {
                if (rf == 0) {
                    myPrintf("\n     ** YOU CAN'T TELEPORT WITHOUT THE RUNESTAFF!\n")
                    check_events()
                } else {
                    var ok_teleport = false
                    while (!ok_teleport) {
                        myPrintf("Give the room number [ W}E N}S U}D ] as XYZ: ")
                        val buf = getLine()
                        val n = try { buf.toInt() } catch (e: Exception) { 0 }
                        x_axis = n / 100
                        y_axis = (n / 10) % 10
                        z_axis = n % 10
                        if (n < 111 || n > 888 || x_axis !in 1..8 || y_axis !in 1..8 || z_axis !in 1..8) {
                            myPrintf("\n     ** TRY NUMBERS FROM 111 TO 888.\n\n")
                        } else {
                            ok_teleport = true
                        }
                    }
                    getChar()
                    done = 1
                }
            }

            if (temp == 36) {
                of = 1
                tc = 8
                for (n in 1..8) {
                    t[n - 1] = 1
                }
                done = 1
            }

            if (temp == 34 && of == 1 && level[func_d() - 1] == 2) {
                myPrintf("\n")
                myPrintf("                     CONGRATULATIONS!\n")
                myPrintf("I thought you'd never find the blasted light switch!\n")
                myPrintf("     ALL CASTLE ROOMS ARE NOW NICE AND BRIGHT.\n")
                if (master_game == 0) master_game = 1
                myPrintf("     The RUNESTAFF just reappeared.  Lucky %s!\n", species[dd - 1])
                rf = 1
                sleep(3000)
                for (n in 1..512) {
                    if (level[n - 1] > 100) {
                        level[n - 1] = level[n - 1] - 100
                    }
                }
                if (master_game != 2) {
                    if (master_game == 1 && tc == 8) {
                        master_game = 2
                        done = 1
                        startnewgame = 1
                    } else {
                        myPrintf("\n")
                        myPrintf("   You must have ALL treasures to rescue PRINCE%s.\n", sss[ss - 2])
                        myPrintf("\n   Press RETURN when ready to continue, %s. ", species[dd - 1])
                        getChar()
                        done = 1
                    }
                } else {
                    done = 1
                }
            }

            if (temp == 25) {
                myPrintf("      THE %s***ORB OF POWER*** CAN REVEAL MANY THINGS!\n", if (of != 0) "***" else "")
                myPrintf("\nThe following commands are available:\n")
                myPrintf("   (C)atch  (G)aze   (L)ook   (Q)uit     (U)p\n")
                myPrintf("   (D)own   (H)elp   (M)ap    (R)elease  (W)est\n")
                myPrintf("   (E)ast   Dr(I)nk  (N)orth  (S)outh\n")
                myPrintf("   (F)lare  (K)iss   (O)pen   (T)eleport\n")
                myPrintf("\nThe contents of rooms are as follows:\n")
                myPrintf("   Empty Room(.)    (F)lares          (S)inkhole\n")
                myPrintf("  (B)ook            (G)old Pieces     (T)reasures\n")
                myPrintf("  (C)hest           (M)onster/STAFF    Stairs (U)p\n")
                myPrintf("   Stairs (D)own     Crystal (O)rb    (V)endor\n")
                myPrintf("  (E)ntrance/Exit    Magic (P)ool     (W)arp/ORB\n")
                myPrintf("\nThe magical properties of treasures are:\n")
                myPrintf(" %s.....No lethargy  %s...........None\n", treasure[8 * t[0]], treasure[8 * t[1] + 1])
                myPrintf(" %s......No leech  %s...Blindness cure\n", treasure[8 * t[2] + 2], treasure[8 * t[3] + 3])
                myPrintf(" %s..No forgetting  %s.Dissolve books\n", treasure[8 * t[4] + 4], treasure[8 * t[5] + 5])
                myPrintf(" %s............None  %s.............None\n", treasure[8 * t[6] + 6], treasure[8 * t[7] + 7])
                myPrintf("\n   You've got the treasures shown in CAPITAL letters!\n")
                myPrintf("\n   Press RETURN when ready to continue, %s. ", species[dd - 1])
                getChar()
                done = 1
            }

            if (temp !in 17..36 || temp == 34) {
                done = 1
            }
            if (alive == 0) {
                done = 1
            }
        }
    }

    private suspend fun take_a_drink() {
        isfrog = 0
        gotfrog = 0
        if (level[func_d() - 1] != 5) {
            myPrintf("\n     ** IF YOU WANT A DRINK, FIND A POOL!\n")
            check_events()
        } else {
            val q = roll(8)
            myPrintf("\n     You take a drink and ")
            if (q > 6) {
                ss++
                if (ss == 4) ss = 2
                dd = roll(4)
                myPrintf("turn into a %s%s!\n", sss[ss], species[dd - 1])
            }
            if (q <= 6) myPrintf("feel ")
            if (q == 1) {
                myPrintf("STRONGER.\n")
                uu = below_nineteen(uu + roll(3))
            }
            if (q == 2) {
                myPrintf("WEAKER.\n")
                uu -= roll(3)
                if (uu < 1) alive = 0
            }
            if (q == 3) {
                myPrintf("DUMBER.\n")
                vv -= roll(3)
                if (vv < 1) alive = 0
            }
            if (q == 4) {
                myPrintf("SMARTER.\n")
                vv = below_nineteen(vv + roll(3))
            }
            if (q == 5) {
                myPrintf("NIMBLER.\n")
                ww = below_nineteen(ww + roll(3))
            }
            if (q == 6) {
                myPrintf("CLUMSIER.\n")
                ww -= roll(3)
                if (ww < 1) alive = 0
            }
            if (alive == 1) check_events()
        }
    }

    private suspend fun kiss_frog() {
        if (gotfrog == 0 || isfrog == 0) {
            myPrintf("\n     You hold nothing kissable!\n")
            isfrog = 0
            gotfrog = 0
            return
        }

        if (isfrog == 3 && roll(3) > 1) {
            myPrintf("\n     You're weird!  You want a bad case of warts?\n")
            return
        }

        var k = 0
        if (isfrog <= 2) {
            for (n in 1..7) {
                if (w[n - 1][0] == x_axis && w[n - 1][1] == y_axis && w[n - 1][2] == z_axis) {
                    if (n >= 4) {
                        myPrintf("     YOU FOUND THE %s WITCH OF THE %s!\n", sss[n / 2 + 6], sss[n])
                        if (n < 6) {
                            myPrintf("\nYou get maximum %s, %s, %s...\n", strength, intelligence, dexterity)
                            uu = 18
                            vv = 18
                            ww = 18
                            ee = 3
                            ff = 3
                            gg += 5000
                            myPrintf("also SWORD, PLATE ARMOR, and 5,000 %s!\n", gold[roll(14) - 1])
                        } else {
                            myPrintf("\nYou lose the RUNESTAFF and all lights just went out!\n")
                            rf = 0
                            for (idx in 1..512) {
                                if (level[idx - 1] < 99) {
                                    level[idx - 1] = level[idx - 1] + 100
                                }
                            }
                        }
                    } else {
                        myPrintf("     YOU FOUND THE %s PRINCE%s.\n", royal[3 * (ss - 2) + n - 1], sss[ss - 2])
                        if (n == 3) pf = 1
                    }
                    level[3] = 2
                    w[n - 1][0] = 0
                    myPrintf("\n   Press RETURN when ready to continue, %s. ", species[dd - 1])
                    getChar()
                    done = 1
                    gotfrog = 0
                    isfrog = 0
                    return
                }
            }
            if (done == 0) {
                gotfrog = 0
                k = roll(5)
                if (k > 1) {
                    myPrintf("\n     Nothing happens!\n")
                    isfrog = 0
                }
            }
        }

        if (isfrog > 2 || k == 1) {
            val n = roll_12()
            myPrintf("\n     The %s just turned into %s!\n", frog[34 + isfrog], dungeon[n - 1].contents)
            level[func_d() - 1] = n
            attack = n - 12
            isfrog = 0
            go_fight()
        }
    }

    private suspend fun release_frog() {
        if (gotfrog != 1 || isfrog < 6 || roll(2) > 1) {
            myPrintf("\n     Nothing happens!\n")
        } else {
            val n = roll(1000)
            myPrintf("For releasing me I give you %d %s!\n", n, gold[roll(14) - 1])
            gg += n
            isfrog = 0
            gotfrog = 0
        }
    }

    private suspend fun catch_frog() {
        gotfrog = 0
        if (bl != 0) {
            myPrintf("\n     ** YOU CAN'T SEE ANYTHING, YOU DUMB %s!\n", species[dd - 1])
            check_events()
        } else {
            if (isfrog == 0) {
                myPrintf("\n     There is nothing here to catch!\n")
            } else {
                if (isfrog in 4..5) {
                    val n = if (master_game == 2) 16 else 11
                    myPrintf("\n     Not much challenge.  Written on the shell is...\n")
                    myPrintf("\n     %s!\n", hint[roll(n) - 1])
                    gotfrog = 1
                }

                if (ww > 15 && isfrog > 5 && roll(3) == 1) {
                    myPrintf("\n     You are left holding a tail in your hand!\n")
                    isfrog = 0
                } else {
                    if (ww > 17 && isfrog > 5 && roll(2) == 1) {
                        myPrintf("\n     Well, you caught me.....NOW WHAT?\n")
                        gotfrog = 1
                    }

                    if (gotfrog == 0 && (master_game < 2 || isfrog > 5)) {
                        myPrintf("\n     The critter simply eludes your grasp!\n")
                        isfrog = 0
                    } else {
                        if (isfrog > 0 && gotfrog == 0) {
                            myPrintf("\n     Well, you caught me.....NOW WHAT?\n")
                            gotfrog = 1
                        }
                    }
                }
            }
        }
    }

    private suspend fun show_map() {
        gotfrog = 0
        isfrog = 0
        val x = x_axis
        val y = y_axis

        if (bl != 0) {
            myPrintf("\n     ** YOU CAN'T SEE ANYTHING, YOU DUMB %s!\n", species[dd - 1])
            check_events()
            return
        }
        ident()
        myPrintf("\n     ")
        for (x_val in 1..8) {
            myPrintf(" %d   ", x_val)
        }
        myPrintf("\n\n")
        for (y_val in 1..8) {
            myPrintf("  %d  ", y_val)
            for (x_val in 1..8) {
                x_axis = x_val
                y_axis = y_val
                var q = level[func_d() - 1]
                if (q > 99) q = 34
                if (x == x_val && y == y_val) {
                    myPrintf("<%s>  ", dungeon[q - 1].id)
                } else {
                    myPrintf(" %s   ", dungeon[q - 1].id)
                }
            }
            myPrintf("\n\n")
        }
        x_axis = x
        y_axis = y
    }

    private fun go_move() {
        gotfrog = 0
        isfrog = 0
        if (temp == 17) y_axis = y_axis - 1
        if (temp == 19) y_axis = y_axis + 1
        y_axis = below_nine(y_axis)
        if (temp == 18) x_axis = x_axis + 1
        if (temp == 20) x_axis = x_axis - 1
        x_axis = below_nine(x_axis)
        done = 1
    }

    private suspend fun flares() {
        gotfrog = 0
        isfrog = 0
        if (bl != 0) {
            myPrintf("\n     ** YOU CAN'T SEE ANYTHING, YOU DUMB %s!\n", species[dd - 1])
            check_events()
            return
        }
        if (fl == 0) {
            myPrintf("\n     ** HEY, BRIGHT ONE, YOU'RE OUT OF FLARES!\n")
            check_events()
            return
        }
        fl--
        val y = y_axis
        val x = x_axis
        for (i in y - 1..y + 1) {
            myPrintf("                      ")
            y_axis = below_nine(i)
            for (j in x - 1..x + 1) {
                x_axis = below_nine(j)
                val q = func_e()
                level[func_d() - 1] = q
                myPrintf("%s  ", dungeon[q - 1].id)
            }
            myPrintf("\n")
        }
        y_axis = y
        x_axis = x
        check_events()
    }

    private suspend fun lamp() {
        gotfrog = 0
        isfrog = 0
        if (bl != 0) {
            myPrintf("\n     ** YOU CAN'T SEE ANYTHING, YOU DUMB %s!\n", species[dd - 1])
            check_events()
            return
        }
        if (lf == 0) {
            myPrintf("\n     ** YOU DON'T HAVE A LAMP, %s!\n", species[dd - 1])
            check_events()
            return
        }
        myPrintf("\nWhere do you want to shine the lamp (N,E,S,W)? ")
        val i = fancy_input(1620)
        val x = x_axis
        val y = y_axis
        y_axis = below_nine(y_axis - (if (i == 17) 1 else 0) + (if (i == 19) 1 else 0))
        x_axis = below_nine(x_axis - (if (i == 20) 1 else 0) + (if (i == 18) 1 else 0))
        if (x - x_axis + y - y_axis == 0) {
            myPrintf("\n** THAT'S NOT A DIRECTION, %s!\n", species[dd - 1])
            check_events()
            return
        }
        myPrintf("\n     The lamp shines into (%d,%d) LEVEL %d.\n", x_axis, y_axis, z_axis)
        level[func_d() - 1] = func_e()
        myPrintf("\nThere you will find %s.\n", dungeon[level[func_d() - 1] - 1].contents)
        x_axis = x
        y_axis = y
        check_events()
    }

    private suspend fun open_thing() {
        gotfrog = 0
        isfrog = 0
        if (level[func_d() - 1] != 6 && level[func_d() - 1] != 12) {
            myPrintf("\n     ** THE ONLY THING OPENED WAS YOUR BIG MOUTH!\n")
            check_events()
            return
        }
        if (level[func_d() - 1] == 12) {
            myPrintf("\n     You open the book and\n")
            val q = roll(6)
            when (q) {
                2 -> myPrintf("     It's another volume of Zot's poetry! - YECH!!\n")
                3 -> myPrintf("     It's an old copy of PLAY%s!\n", species[roll(4) - 1])
                4 -> {
                    myPrintf("     It's a MANUAL of DEXTERITY!\n")
                    ww = 18
                }
                5 -> {
                    myPrintf("     It's a MANUAL of STRENGTH!\n")
                    uu = 18
                }
                6 -> {
                    myPrintf("          the book sticks to your hands -\n")
                    myPrintf("          NOW YOU ARE UNABLE TO DRAW YOUR WEAPON!\n")
                    bf = 1
                }
                else -> {
                    myPrintf("     FLASH! OH NO! You are now a BLIND %s!\n", species[dd - 1])
                    bl = 1
                }
            }
            level[func_d() - 1] = 1
            check_events()
            return
        }
        myPrintf("\n     You open the chest and find\n")
        val q = roll(4)
        when (q) {
            2 -> {
                myPrintf("     KABOOM! IT EXPLODES!!\n")
                val i = roll(z_axis)
                check_damage(i)
                level[func_d() - 1] = 1
                if (uu < 1 || ww < 1) alive = 0
                if (alive != 0) check_events()
            }
            3 -> {
                myPrintf("     GAS! You stagger from the room!\n")
                sleep(3000)
                val i = roll(4) + 16
                level[func_d() - 1] = 1
                y_axis = below_nine(y_axis - (if (i == 17) 1 else 0) + (if (i == 19) 1 else 0))
                x_axis = below_nine(x_axis - (if (i == 20) 1 else 0) + (if (i == 18) 1 else 0))
                done = 1
            }
            else -> {
                val gold_amount = roll(z_axis * 250)
                gg += gold_amount
                myPrintf("    %d %s!  You now have %d.\n", gold_amount, gold[roll(14) - 1], gg)
                level[func_d() - 1] = 1
                check_events()
            }
        }
    }

    private suspend fun gaze_orb() {
        gotfrog = 0
        isfrog = 0
        if (bl != 0) {
            myPrintf("\n     ** YOU CAN'T SEE ANYTHING, YOU DUMB %s!\n", species[dd - 1])
            check_events()
            return
        }
        if (level[func_d() - 1] != 11) {
            myPrintf("\n     ** IT'S HARD TO GAZE WITHOUT AN ORB!\n")
            check_events()
            return
        }
        myPrintf("\n     You see ")
        if (master_game + of == 1) {
            myPrintf("a Vendor with a message!\n")
            check_events()
            return
        }
        val q = roll(6)
        when (q) {
            2 -> {
                var a = roll(8)
                var b = roll(8)
                var c = roll(8)
                if (roll(3) == 1) {
                    a = o[0]
                    b = o[1]
                    c = o[2]
                }
                if (master_game == 0) {
                    myPrintf("***THE ORB OF ZOT***\n")
                    myPrintf("     AT (%d,%d) LEVEL %d!\n", a, b, c)
                } else {
                    myPrintf("yourself drinking from a pool -\n")
                    myPrintf("     and becoming %s!\n", dungeon[roll_12() - 1].contents)
                }
            }
            3 -> {
                myPrintf("yourself drinking from a pool -\n")
                myPrintf("     and becoming %s!\n", dungeon[roll_12() - 1].contents)
            }
            4 -> {
                myPrintf(" %s gazing back at you!\n", dungeon[roll_12() - 1].contents)
            }
            5 -> {
                val a = x_axis
                val b = y_axis
                val c = z_axis
                x_axis = roll(8)
                y_axis = roll(8)
                z_axis = roll(8)
                val i = func_e()
                level[func_d() - 1] = i
                myPrintf("%s at (%d,%d) LEVEL %d!\n", dungeon[i - 1].contents, x_axis, y_axis, z_axis)
                x_axis = a
                y_axis = b
                z_axis = c
            }
            6 -> {
                myPrintf("a soap opera rerun!\n")
            }
            else -> {
                myPrintf("yourself in a BLOODY HEAP!\n")
                uu -= roll(2)
                if (uu < 1) alive = 0
            }
        }
        if (alive == 1) check_events()
    }

    private suspend fun save_game() {
        gotfrog = 0
        isfrog = 0
        myPrintf("\nDo you really want to quit now? ")
        val i = fancy_input(102)
        if (i != 2) {
            myPrintf("\n     ** THEN DON'T SAY THAT YOU DO!\n")
            check_events()
            return
        }
        splash()
        myPrintf("\nDo you want to save this game? ")
        val save_ans = fancy_input(102)
        if (save_ans == 2) {
            myPrintf("Please enter filename of game. ")
            val zotsav = getLine()
            getChar()
            myPrintf("\n")
            val file = File(zotsav)
            if (file.exists()) {
                myPrintf("Error. File exists, unable to save.\n")
            } else {
                file.printWriter().use { writer ->
                    for (idx in 1..512) {
                        writer.println(level[idx - 1])
                    }
                    for (idx in 1..8) {
                        writer.println(t[idx - 1])
                    }
                    for (idx in 1..16) {
                        writer.println(p[idx - 1])
                    }
                    for (idx in 1..3) {
                        writer.println(r[idx - 1])
                        writer.println(o[idx - 1])
                        for (a in 1..4) {
                            writer.println(c[idx - 1][a - 1])
                        }
                        for (a in 1..15) {
                            writer.println(w[a - 1][idx - 1])
                        }
                    }
                    writer.println(ah)
                    writer.println(bf)
                    writer.println(bl)
                    writer.println(dd)
                    writer.println(ee)
                    writer.println(ff)
                    writer.println(fl)
                    writer.println(gg)
                    writer.println(hh)
                    writer.println(lf)
                    writer.println(master_game)
                    writer.println(new_game)
                    writer.println(of)
                    writer.println(pf)
                    writer.println(rf)
                    writer.println(ss)
                    writer.println(tt)
                    writer.println(tc)
                    writer.println(uu)
                    writer.println(vv)
                    writer.println(vf)
                    writer.println(ww)
                    writer.println(wc)
                    writer.println(x_axis)
                    writer.println(y_axis)
                    writer.println(z_axis)
                }
            }
        }
        end_game()
    }

    private suspend fun are_you_leaving() {
        gotfrog = 0
        isfrog = 0
        var i = 0

        val leaving = "     Leaving without "
        if (master_game == 0) myPrintf("\n%sturning on the lights?\n", leaving)
        if (master_game < 2 && tc < 8) myPrintf("\n%sall the treasures?\n", leaving)
        if (pf == 0) {
            myPrintf("\n%sthe %s PRINCE%s? ", leaving, royal[(3 * (ss - 1)) - 1], sss[ss - 2])
            i = fancy_input(102)
        }
        if (i == 1) {
            check_events()
            return
        }
        if (pf == 0) {
            end_game()
        } else {
            myPrintf("\n")
            myPrintf("SO THAT ALL MAY KNOW OF YOUR SUCCESS.....\n")
            myPrintf("     Here is a small wedding gift.\n")
            myPrintf("          ...the CULLINAN diamond!\n")
            myPrintf("      Have a great life you two!\n")
            myPrintf("\nAre you and the %s PRINCE%s ready to leave? ", royal[(3 * (ss - 1)) - 1], sss[ss - 2])
            val leave_ans = fancy_input(102)
            if (leave_ans == 2) {
                end_game()
            } else {
                check_events()
            }
        }
    }

    private suspend fun end_game() {
        if (alive == 0) {
            myPrintf("\n     At the time you died you had:\n")
        } else {
            myPrintf("\n  You left the castle with")
            if (of == 0) myPrintf("out")
            myPrintf(" the ***ORB OF ZOT***.\n")
            if (of == 0) {
                myPrintf("\n     A LESS THAN AWE-INSPIRING DEFEAT.\n")
                myPrintf("\n     When you left the castle, you had:\n")
            } else {
                myPrintf("\n     AN INCREDIBLY GLORIOUS VICTORY!!\n")
                myPrintf("\n     In addition, you got out with the following:\n")
            }
        }
        if (alive == 1) myPrintf("          your miserable life!\n")
        for (i in 1..8) {
            if (t[i - 1] != 0) {
                myPrintf("          %s\n", dungeon[i + 24].contents)
            }
        }
        myPrintf("          %s and %s", cause[ff], cause[ee + 4])
        if (lf != 0) myPrintf(" and a lamp.")
        myPrintf("\n")
        myPrintf("\n     You also had %d FLARES and %d GOLD PIECES\n", fl, gg)
        if (rf != 0) myPrintf("          and the RUNESTAFF\n")
        myPrintf("          and it took you %d turns!\n", tt)
        myPrintf("\n  Are you foolish enough to re-enter the castle? ")
        val reenter = fancy_input(304)
        if (reenter == 4) {
            myPrintf("\n     MAYBE DUMB %s IS NOT SO DUMB AFTER ALL!\n", species[dd - 1])
            done = 1
            startnewgame = 1
            quit = 1
        } else {
            myPrintf("\n          SOME %sS NEVER LEARN!\n", species[dd - 1])
            myPrintf("\n     Please be patient while the castle is restocked.\n")
            initialize()
            done = 1
            startnewgame = 1
        }
    }

    private suspend fun go_die() {
        myPrintf("\n     A noble effort, oh formerly living %s!\n", species[dd - 1])
        myPrintf("\n     You died due to lack of")
        if (uu < 1) {
            myPrintf(" %s", strength)
            uu = 8
        }
        if (vv < 1) {
            myPrintf(" %s", intelligence)
            vv = 8
        }
        if (ww < 1) {
            myPrintf(" %s", dexterity)
            ww = 8
        }
        myPrintf(".\n")
        myPrintf("\nDo you want me to attempt to REINCARNATE you? ")
        val reincarnate = fancy_input(304)
        if (reincarnate == 4) {
            end_game()
            return
        }
        myPrintf("\n")
        myPrintf("     OK, don't blame me if it doesn't work!\n")
        if (roll(3) > 1) {
            myPrintf("\n        Well I'll be.....IT WORKED!\n")
            ss = roll(2) + 1
            dd = roll(4)
            x_axis = roll(8)
            y_axis = roll(8)
            z_axis = roll(8)
            bl = 1
            myPrintf("\n  Sorry, but you came back as a BLIND %s%s!\n", sss[ss], species[dd - 1])
            alive = 1
            check_events()
        } else {
            myPrintf("\n      Oh well, at least I tried!\n")
            end_game()
        }
    }

    private fun check_damage(q_val: Int) {
        var q = q_val
        if (ee > 0) {
            q -= ee
            ah -= ee
            if (q < 0) {
                ah -= q
                q = 0
            }
            if (ah < 0) {
                ah = 0
                ee = 0
                myPrintf("     ********** YOUR ARMOR HAS BEEN DESTROYED!\n")
                myPrintf("\n     GOOD LUCK!\n")
            }
        }
        if (ww > 0) ww -= q / 2
        uu -= q
    }

    private suspend fun go_fight() {
        var fighting = 1
        val q1 = 1 + attack / 2
        var q2 = attack + 4 + z_axis / 2 + 2 * master_game
        var q3 = 1
        val ms = dungeon[attack + 11].contents
        var jj = 2
        if (ms[2] == ' ') jj = 3

        if (c[0][3] > t[0] || bl != 0 || ww < roll(9) + roll(9)) {
            monster_attack(q1, q2, jj, ms)
        }

        while (fighting == 1) {
            myPrintf("\n     You're facing %s", ms)
            myPrintf("\n     You may (A)ttack or (R)etreat.\n")
            if (q3 == 1) myPrintf("     You can also attempt a (B)ribe.\n")
            if (vv > 14) myPrintf("     You can also (C)ast a spell.\n")
            myPrintf("\n     Your %s is %d and your %s is %d.\n", strength, uu, dexterity, ww)
            myPrintf("\nCommand #%d? ", tt)
            tt++
            val i = fancy_input(4044)

            when (i) {
                41 -> {
                    if (ff == 0) {
                        myPrintf("\n     ** POUNDING ON %s WON'T HURT IT!\n", ms)
                    } else {
                        if (bf != 0) {
                            myPrintf("\n      ** YOU CAN'T BEAT IT TO DEATH WITH A BOOK!\n")
                        } else {
                            if (ww < roll(20) + bl * 3) {
                                myPrintf("\n     You missed, too bad!\n")
                            } else {
                                myPrintf("\n     ********** YOU HIT THE EVIL ")
                                for (idx in jj until ms.length) {
                                    myPrintf("%c", ms[idx])
                                }
                                myPrintf("!\n")
                                q2 -= ff
                                if ((attack == 9 || attack == 12) && roll(8) == 1) {
                                    myPrintf("\n     ********** OH NO! YOUR %s BROKE!\n", cause[ff])
                                    ff = 0
                                }
                                if (q2 <= 0) {
                                    monster_death(ms, jj)
                                    fighting = 0
                                }
                            }
                        }
                    }
                    if (fighting == 1) {
                        monster_attack(q1, q2, jj, ms)
                    }
                }
                43 -> {
                    if (vv < 15) {
                        myPrintf("\n     ** YOU CAN'T CAST A SPELL NOW!\n")
                    } else {
                        myPrintf("\nWhich spell -\n")
                        myPrintf("     (W)eb, (F)ireball or (D)eathspell? ")
                        val j = fancy_input(4047)
                        if (j == 47) {
                            uu -= 1
                            wc = roll(8) + 1
                            if (uu < 1) alive = 0
                        }
                        if (j == 46) {
                            val q_dmg = roll(7) + roll(7)
                            uu -= 1
                            vv -= 1
                            if (uu < 1 || vv < 1) {
                                alive = 0
                            } else {
                                myPrintf("\n     It does %d POINTS worth of damage.\n", q_dmg)
                                q2 -= q_dmg
                                if (q2 <= 0) {
                                    monster_death(ms, jj)
                                    fighting = 0
                                }
                            }
                        }
                        if (j == 45) {
                            myPrintf("Death . . . ")
                            if (vv < roll(4) + 15) {
                                myPrintf("YOURS!\n")
                                vv = 0
                                alive = 0
                                sleep(3000)
                            } else {
                                myPrintf("HIS!\n")
                                q2 = 0
                                monster_death(ms, jj)
                                fighting = 0
                            }
                        }
                        if (alive == 1 && fighting == 1) {
                            monster_attack(q1, q2, jj, ms)
                        }
                    }
                }
                44 -> {
                    monster_attack(q1, q2, jj, ms)
                    if (alive == 1) {
                        myPrintf("\n              YOU HAVE ESCAPED!\n")
                        var ok_dir = false
                        var j_dir = 0
                        while (!ok_dir) {
                            myPrintf("\nDo you want to go -\n     (N)orth, (E)ast, (S)outh or (W)est? ")
                            j_dir = fancy_input(1620)
                            if (j_dir !in 17..20) {
                                myPrintf("\n     ** DON'T PRESS YOUR LUCK, %s!\n", species[dd - 1])
                            } else {
                                ok_dir = true
                            }
                        }
                        if (j_dir == 17) y_axis--
                        if (j_dir == 19) y_axis++
                        y_axis = below_nine(y_axis)
                        if (j_dir == 18) x_axis++
                        if (j_dir == 20) x_axis--
                        x_axis = below_nine(x_axis)
                        escape = 1
                    }
                    fighting = 0
                }
                else -> {
                    if (i == 40 || q3 > 1) {
                        myPrintf("\n%s\n", bad_response)
                    } else {
                        if (tc == 0) {
                            myPrintf("\n     ALL I WANT IS YOUR LIFE!\n")
                            monster_attack(q1, q2, jj, ms)
                        } else {
                            var ok_bribe = false
                            var bribe_val = 0
                            var q_tr = 0
                            while (!ok_bribe) {
                                do {
                                    q_tr = roll(8)
                                } while (t[q_tr - 1] == 0)
                                myPrintf("\nI want %s. Will you give it to me? ", dungeon[q_tr + 24].contents)
                                bribe_val = fancy_input(102)
                                if (bribe_val != 1 && bribe_val != 2) {
                                    myPrintf("\n%s\n", bad_response)
                                } else {
                                    ok_bribe = true
                                }
                            }
                            if (bribe_val == 2) {
                                t[q_tr - 1] = 0
                                tc--
                                myPrintf("\n     OK, just don't tell anyone else.\n")
                                if (level[func_d() - 1] == 25) {
                                    vf--
                                }
                                fighting = 0
                            } else {
                                monster_attack(q1, q2, jj, ms)
                            }
                        }
                    }
                }
            }
            if (alive == 0) fighting = 0
        }
    }

    private fun monster_attack(q1: Int, q2: Int, jj: Int, ms: String) {
        if (wc > 0) {
            wc--
            if (wc == 0) myPrintf("\n     THE WEB JUST BROKE!\n")
        }
        myPrintf("\n     The ")
        for (idx in jj until ms.length) {
            myPrintf("%c", ms[idx])
        }
        if (wc > 0) {
            myPrintf(" is stuck and can't attack now!\n")
            return
        }
        myPrintf(" attacks!\n")
        if (ww >= roll(7) + roll(7) + roll(7) + bl * 3) {
            myPrintf("\n          What luck, he missed you!\n")
            return
        }
        myPrintf("\n          ********** OUCH! HE HIT YOU!\n")
        check_damage(q1)
        if (uu < 1 || ww < 1) alive = 0
    }

    private fun monster_death(ms: String, jj: Int) {
        myPrintf("\n     %s lies dead at your feet!\n", ms)
        if (hh <= tt - 60) {
            myPrintf("\n     You spend an hour eating %s %s.\n", ms, effect[roll(8) - 1])
            hh = tt
        }
        if (x_axis == r[0] && y_axis == r[1] && z_axis == r[2] && rf != 1) {
            myPrintf("\n")
            myPrintf("       GREAT ZOT! YOU'VE FOUND THE RUNESTAFF!\n")
            rf = 1
            myPrintf("\n")
        } else {
            if (attack == 13) {
                myPrintf("\n     YOU GET ALL HIS WARES:\n")
                myPrintf("          Plate Armor\n")
                ee = 3
                ah = 21
                myPrintf("          A Sword\n")
                ff = 3
                myPrintf("          A Strength Potion\n")
                uu = below_nineteen(uu + roll(6))
                myPrintf("          An Intelligence Potion\n")
                vv = below_nineteen(vv + roll(6))
                myPrintf("          A Dexterity Potion\n")
                ww = below_nineteen(ww + roll(6))
                myPrintf("          A Lamp\n")
                lf = 1
            }
        }
        var gold_reward = roll(attack * 100 + z_axis * 50)
        if (attack == 9 || attack == 12) {
            gold_reward += 100 * (z_axis / 2)
        }
        myPrintf("     You get his hoard of %d %s!\n", gold_reward, gold[roll(14) - 1])
        gg += gold_reward
        if (master_game > 1) {
            val fn = roll(10)
            isfrog = (fn + 4) / 5
            myPrintf("\n     On the floor is a %s FROG!\n", frog[fn - 1])
            myPrintf("    ")
        }
        if (attack == 9 && master_game + of == 1) {
            myPrintf("\n")
            myPrintf("     This castle has electric power!\n")
        }
        if (attack == 12 && master_game + of == 1) {
            myPrintf("\n")
            myPrintf("     Where would YOU put the light switch?\n")
        }
        level[func_d() - 1] = 1
    }

    private suspend fun go_buy() {
        var done_buying = false
        if (master_game + of == 0) {
            myPrintf("\n     Old ZOT thought narcissism was a BRIGHT idea.\n")
        }
        if (tc > 0) {
            myPrintf("\nDo you want purchase offers on your treasures? ")
            val ans = fancy_input(102)
            if (ans == 2) {
                myPrintf("\n     Gold Highest  Last   Current Sell?\n")
                myPrintf("  Balance  Treasures    Offer   Offer   Offer   Y/N\n")
                myPrintf("  -------  ----------  ------- ------- ------ -----\n")
                for (q in 1..8) {
                    val a = roll(q * 1500)
                    if (t[q - 1] > 0) {
                        if (gg < 1000) {
                            myPrintf("    %5d", gg)
                        } else {
                            myPrintf("   %2d,%03d", gg / 1000, gg % 1000)
                        }
                        myPrintf("  %s  \t", treasure[q + 7])
                        if (p[q + 7] < 1000) {
                            myPrintf(" %5d", p[q + 7])
                        } else {
                            myPrintf("%2d,%03d", p[q + 7] / 1000, p[q + 7] % 1000)
                        }
                        if (p[q - 1] < 1000) {
                            myPrintf("   %5d", p[q - 1])
                        } else {
                            myPrintf("  %2d,%03d", p[q - 1] / 1000, p[q - 1] % 1000)
                        }
                        if (a < 1000) {
                            myPrintf("  %5d  ", a)
                        } else {
                            myPrintf(" %2d,%03d  ", a / 1000, a % 1000)
                        }
                        val sell_ans = fancy_input(102)
                        if (sell_ans == 2) {
                            tc--
                            t[q - 1] = 0
                            gg += a
                        }
                        p[q - 1] = a
                        if (a > p[q + 7]) {
                            p[q + 7] = a
                        }
                    }
                    if (gg > 20000) break
                }
            }
        }

        if (gg < 1000) {
            myPrintf("\n     YOU'RE TOO POOR TO BUY ANYTHING, %s.\n", species[dd - 1])
            return
        }

        if (gg >= 1250) {
            if ((gg < 1500 && ee == 0) || (gg in 1500..1999 && ee < 2) || (gg >= 2000 && ee < 3)) {
                while (!done_buying) {
                    myPrintf("\n     OK, %s, you have %d gold pieces\n     and %s armor.\n", species[dd - 1], gg, cause[ee + 4])
                    myPrintf("\nThese are the types of ARMOR you can buy:\n")
                    myPrintf("     (N)one ")
                    if (ee < 1) myPrintf("(L)eather-1250 ")
                    if (gg > 1499 && ee < 2) myPrintf("(C)hainmail-1500 ")
                    if (gg > 1999) myPrintf("(P)late-2000")
                    myPrintf("\n")

                    var inner_done = false
                    while (!inner_done) {
                        myPrintf("\nCommand #%d? ", tt)
                        tt++
                        val i = fancy_input(913)
                        if (i == 9 && ee < 1) {
                            gg -= 1250
                            ee = 1
                            ah = 7
                            done_buying = true
                            inner_done = true
                        }
                        if (i == 10 && gg < 1500) {
                            myPrintf("\n     ** YOU HAVEN'T GOT THAT MUCH CASH ON HAND!\n\n")
                        }
                        if (i == 10 && ee < 2 && gg > 1499) {
                            gg -= 1500
                            ee = 2
                            ah = 14
                            done_buying = true
                            inner_done = true
                        }
                        if (i == 11 && gg < 2000) {
                            myPrintf("\n     ** YOU CAN'T AFFORD PLATE ARMOR!\n\n")
                        }
                        if (i == 11 && gg > 1999) {
                            gg -= 2000
                            ee = 3
                            ah = 21
                            done_buying = true
                            inner_done = true
                        }
                        if (i == 13) {
                            done_buying = true
                            inner_done = true
                        }
                        if (i !in 9..13) {
                            myPrintf("\n     ** PLEASE!!!  CHOOSE A REASONABLE RESPONSE.\n")
                        }
                    }
                }
            }
            done_buying = false
            if ((gg < 1500 && ff == 0) || (gg in 1500..1999 && ff < 2) || (gg >= 2000 && ff < 3)) {
                while (!done_buying) {
                    myPrintf("\n     You have %d gold pieces left.\n     with %s in hand.\n", gg, cause[ff])
                    myPrintf("\nThese are the types of WEAPON you can buy:\n")
                    myPrintf("     (N)one ")
                    if (ff < 1) myPrintf("(D)agger-1250 ")
                    if (gg > 1499 && ff < 2) myPrintf("(M)ace-1500 ")
                    if (gg > 1999) myPrintf("(S)word-2000")
                    myPrintf("\n")

                    var inner_done = false
                    while (!inner_done) {
                        myPrintf("\nCommand #%d? ", tt)
                        tt++
                        val i = fancy_input(1215)
                        if (i == 14 && ff < 1) {
                            gg -= 1250
                            ff = 1
                            done_buying = true
                            inner_done = true
                        }
                        if (i == 15 && gg < 1500) {
                            myPrintf("\n     ** SORRY, I'M AFRAID I DON'T GIVE CREDIT!\n\n")
                        }
                        if (i == 15 && ff < 2 && gg > 1499) {
                            gg -= 1500
                            ff = 2
                            done_buying = true
                            inner_done = true
                        }
                        if (i == 12 && gg < 2000) {
                            myPrintf("\n     ** YOUR DUNGEON EXPRESS CARD -\n     YOU LEFT HOME WITHOUT IT!\n")
                        }
                        if (i == 12 && gg > 1999) {
                            gg -= 2000
                            ff = 3
                            done_buying = true
                            inner_done = true
                        }
                        if (i == 13) {
                            done_buying = true
                            inner_done = true
                        }
                        if (i !in 12..15) {
                            myPrintf("\n     ** PLEASE!!!  CHOOSE A REASONABLE RESPONSE.\n")
                        }
                    }
                }
            }
            done_buying = false
        }

        if (gg > 999 && uu + vv + ww < 54) {
            myPrintf("\nDo you want to buy some -\n")
        }
        if (gg > 999 && uu < 18) {
            var buy_strength = 2
            while (uu < 18 && gg > 999 && buy_strength == 2) {
                buy_strength = buy_points(strength, uu)
                if (buy_strength == 2) {
                    gg -= 1000
                    uu = below_nineteen(uu + roll(6))
                }
            }
        }
        if (gg > 999 && vv < 18) {
            var buy_intel = 2
            while (vv < 18 && gg > 999 && buy_intel == 2) {
                buy_intel = buy_points(intelligence, vv)
                if (buy_intel == 2) {
                    gg -= 1000
                    vv = below_nineteen(vv + roll(6))
                }
            }
        }
        if (gg > 999 && ww < 18) {
            var buy_dext = 2
            while (ww < 18 && gg > 999 && buy_dext == 2) {
                buy_dext = buy_points(dexterity, ww)
                if (buy_dext == 2) {
                    gg -= 1000
                    ww = below_nineteen(ww + roll(6))
                }
            }
        }
        if (gg > 999 && lf == 0) {
            myPrintf("\nDo you want a lamp for 1,000 gold pieces? ")
            val ans = fancy_input(102)
            if (ans == 2) {
                myPrintf("\n     It's guaranteed to outlive you!\n")
                gg -= 1000
                lf = 1
            }
        }
    }

    private suspend fun buy_points(z: String, n: Int): Int {
        myPrintf("     %s ( now %d )\t", z, n)
        myPrintf("for 1,000 (of %2d,%03d)? ", gg / 1000, gg % 1000)
        return fancy_input(102)
    }
}

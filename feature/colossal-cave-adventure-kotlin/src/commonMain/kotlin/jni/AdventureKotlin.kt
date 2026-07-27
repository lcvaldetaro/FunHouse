package jni
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import club.gepetto.GcLog
import com.funhouse.shared.common.utils.GcInputQueue
import colossalcaveadventurekotlin.utils.adventureAbout
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.BaseKotlinGame

import java.io.*
import kotlinx.coroutines.*
import kotlin.math.abs

class AdventureKotlin : BaseKotlinGame() {
    private val ABB = IntArray(186)
    private val ATAB = IntArray(331)
    private val ATLOC = IntArray(186)
    private var BLKLIN = true
    private var DFLAG = 0
    private val DLOC = IntArray(7)
    private val FIXED = IntArray(101)
    private var HOLDNG = 0
    private val KTAB = IntArray(331)
    private var LINES = IntArray(12501)
    private val LINK = IntArray(201)
    private var lnleng = 0
    private var lnposn = 1
    private val PARMS = IntArray(26)
    private val PLACE = IntArray(101)
    private val plac get() = PLACE
    private val fixd get() = FIXD
    private var openedFile: BufferedReader? = null
    private val PTEXT = IntArray(101)
    private val RTEXT = IntArray(278)
    private var SETUP = 0
    private val TABSIZ = 330
    private val inlineArr = IntArray(256)
    private val MAP1 = IntArray(256)
    private val MAP2 = IntArray(256)

    private var ABBNUM = 0
    private val ACTSPK = IntArray(36)
    private var AMBER = 0
    private var ATTACK = 0
    private var AXE = 0
    private var BACK = 0
    private var BATTER = 0
    private var BEAR = 0
    private var BIRD = 0
    private var BLOOD = 0
    private var BONUS = 0
    private var BOTTLE = 0
    private var CAGE = 0
    private var CAVE = 0
    private var CAVITY = 0
    private var CHAIN = 0
    private var CHASM = 0
    private var CHEST = 0
    private var CHLOC = 0
    private var CHLOC2 = 0
    private var CLAM = 0
    private var CLOCK1 = 0
    private var CLOCK2 = 0
    private var CLOSED = false
    private var CLOSNG = false
    private var CLSHNT = false
    private var CLSMAX = 12
    private var CLSSES = 0
    private var COINS = 0
    private val COND = IntArray(186)
    private var CONDS = 0
    private val CTEXT = IntArray(13)
    private val CVAL = IntArray(13)
    private var DALTLC = 0
    private var DETAIL = 0
    private var DKILL = 0
    private var DOOR = 0
    private var DPRSSN = 0
    private var DRAGON = 0
    private val DSEEN = IntArray(7)
    private var DTOTAL = 0
    private var DWARF = 0
    private var EGGS = 0
    private var EMRALD = 0
    private var ENTER = 0
    private var ENTRNC = 0
    private var FIND = 0
    private var FISSUR = 0
    private val FIXD = IntArray(101)
    private var FOOBAR = 0
    private var FOOD = 0
    private var GRATE = 0
    private var HINT = 0
    private val HINTED = BooleanArray(21)
    private val HINTLC = IntArray(21)
    private val HINTS = Array(21) { IntArray(5) }
    private var HNTMAX = 0
    private var HNTSIZ = 20
    private var I = 0
    private var INVENT = 0
    private var IGO = 0
    private var IWEST = 0
    private var J = 0
    private var JADE = 0
    private var Kbuffer = 0
    private var K2 = 0
    private val KEY = IntArray(186)
    private var KEYS = 0
    private var KK = 0
    private var KNFLOC = 0
    private var KNIFE = 0
    private var KQ = 0
    private var L = 0
    private var LAMP = 0
    private var LIMIT = 0
    private var LINSIZ = 12500
    private var LINUSE = 0
    private var LL = 0
    private var LMWARN = false
    private var LOC = 0
    private var LOCK = 0
    private var LOCSIZ = 185
    private val LOCSND = IntArray(186)
    private var LOOK = 0
    private val LTEXT = IntArray(186)
    private var MAGZIN = 0
    private var MAXDIE = 0
    private var MAXTRS = 0
    private val MESH = 123456789
    private var MESSAG = 0
    private var MIRROR = 0
    private var MXSCOR = 0
    private var NEWLOC = 0
    private var NOVICE = false
    private var NUGGET = 0
    private var NUL = 0
    private var NUMDIE = 0
    private var OBJ = 0
    private val OBJSND = IntArray(101)
    private val OBJTXT = IntArray(101)
    private val ODLOC = IntArray(7)
    private var OGRE = 0
    private var OIL = 0
    private var OLDLC2 = 0
    private var OLDLOC = 0
    private var OLDOBJ = 0
    private var OYSTER = 0
    private var PANIC = false
    private var PEARL = 0
    private var PILLOW = 0
    private val PLAC = IntArray(101)
    private var PLANT = 0
    private var PLANT2 = 0
    private val PROP = IntArray(101)
    private var PYRAM = 0
    private var RESER = 0
    private var ROD = 0
    private var ROD2 = 0
    private var RTXSIZ = 277
    private var RUBY = 0
    private var RUG = 0
    private var SAPPH = 0
    private var SAVED = 0
    private var SAY = 0
    private var SCORE = 0
    private var SECT = 0
    private var SIGN = 0
    private var SNAKE = 0
    private var SPK = 0
    private var STEPS = 0
    private val STEXT = IntArray(186)
    private var STICK = 0
    private var STREAM = 0
    private var TABNDX = 0
    private var TALLY = 0
    private var THRESH = 0
    private var THROW = 0
    private val TK = IntArray(21)
    private val TRAVEL = IntArray(886)
    private var TRIDNT = 0
    private var TRNDEX = 0
    private var TRNLUZ = 0
    private var TRNSIZ = 5
    private val TRNVAL = IntArray(6)
    private var TRNVLS = 0
    private var TROLL = 0
    private var TROLL2 = 0
    private var TRVS = 0
    private var TRVSIZ = 885
    private val TTEXT = IntArray(6)
    private var TURNS = 0
    private var URN = 0
    private var V1 = 0
    private var V2 = 0
    private var VASE = 0
    private var VEND = 0
    private var VERB = 0
    private var VOLCAN = 0
    private var VRBSIZ = 35
    private var VRSION = 25
    private var WATER = 0
    private var WD1 = 0
    private var WD1X = 0
    private var WD2 = 0
    private var WD2X = 0
    private var WZDARK = false
    private var ZZWORD = 0

    // Thread communication queue
    private val inputQueue = GcInputQueue<String>()
    private var gameJob: Job? = null
    private var gameSuspended = 0
    private var packageFolder = AppData.packageFolder ?: ""
    private var gameFolder = AppData.gameFolder ?: ""
    private var backupDirectory = ""
    private var externalStorage = ""

    // SaveIO operations
    private var saveFileName = ""
    private var lastSavedFile = ""
    private var saveFileReader: BufferedReader? = null
    private var saveFileWriter: BufferedWriter? = null

    // MPINIT state variables
    private var splitting = -1

    // lookup verb mappings
    private val intransitiveVerbMap = intArrayOf(
        8010, 8000, 8000, 8040, 2009, 8040, 8070, 8080, 8000,
        8000, 2011, 9120, 9130, 8140, 9150, 8000, 8000, 8180,
        8000, 8200, 8000, 9220, 9230, 8240, 8250, 8260, 8270,
        8000, 8000, 8300, 8310, 8320, 8330, 8340
    )

    private val transitiveVerbMap = intArrayOf(
        9010, 9020, 9030, 9040, 2009, 9040, 9070, 9080, 9090,
        2011, 2011, 9120, 9130, 9140, 9150, 9160, 9170, 2011,
        9190, 9190, 9210, 9220, 9230, 2011, 2011, 2011, 9270,
        9280, 9290, 2011, 2011, 9320, 2011, 8340
    )

    init {
        GcLog.d("init")
        if (packageFolder.isNotEmpty()) {
            val folder = File(packageFolder, gameFolder)
            if (!folder.exists()) {
                folder.mkdirs()
            }
        }
        backupDirectory = "$packageFolder/$gameFolder"
        externalStorage = "$packageFolder/$gameFolder" // fallback path
        mpInit()
    }

    private fun toting(obj: Int): Boolean = PLACE[obj] == -1
    private fun at(obj: Int): Boolean = PLACE[obj] == LOC || FIXED[obj] == LOC
    private fun here(obj: Int): Boolean = at(obj) || toting(obj)
    private fun liq2(pbotl: Int): Int = (1 - pbotl) * WATER + (pbotl / 2) * (WATER + OIL)
    private fun liq(dummy: Int): Int {
        val propBot = if (PROP[BOTTLE] < 0) -1 - PROP[BOTTLE] else PROP[BOTTLE]
        return liq2(propBot)
    }
    private fun liqLoc(loc: Int): Int {
        val condLoc = COND[loc]
        return liq2(((condLoc / 2 * 2) % 8 - 5) * ((condLoc / 4) % 2) + 1)
    }
    private fun cndbit(l: Int, n: Int): Boolean = tstBit(COND[l], n)
    private fun forced(loc: Int): Boolean = COND[loc] == 2
    private fun dark(dummy: Int): Boolean = (!cndbit(LOC, 0)) && (PROP[LAMP] == 0 || !here(LAMP))
    private fun pct(n: Int): Boolean = ran(100) < n
    private fun gstone(obj: Int): Boolean = obj == EMRALD || obj == RUBY || obj == AMBER || obj == SAPPH
    private fun forest(loc: Int): Boolean = loc in 145..166
    private fun vocwrd(letters: Int, sect: Int): Int = vocab(makeWd(letters.toLong()), sect)
    private fun outsid(loc: Int): Boolean = loc <= 8 || forest(loc) || loc == PLACE[SAPPH] || loc == 180 || loc == 182
    private fun indeep(loc: Int): Boolean = loc >= 15 && !outsid(loc) && loc != 179

    override fun start() {
        GcLog.d("Adventure.start() called")
        gameJob?.cancel()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            runGame()
        }
        greetings()
    }

    override fun start(gameNickName: String) {
        GcLog.d("start")
        start()
    }

    override fun stop() {
        super.stop()
        gameJob?.cancel()
    }

    override fun sendCommand(command: String): Int {
        if (command.equals("about", ignoreCase = true)) {
            callback?.onNewTerminalDataReceived(adventureAbout)
            return 0
        }
        val parts = command.trim().split(Regex("\\s+"))
        if (parts.size == 2) {
            val cmd = parts[0].lowercase()
            val arg = parts[1]
            if (cmd == "save" || cmd == "resume") {
                saveFileName = "$backupDirectory/$arg.advgame"
                lastSavedFile = saveFileName
                inputQueue.put(cmd + "\n")
                return 0
            } else if (cmd == "backup") {
                var gameName = arg
                if (arg != "adventure.data") {
                    gameName += ".advgame"
                }
                miscSaveGameFileToExtStorage(gameName)
                myPrintf(AppData.applicationContext?.getString(R.string.adventure_backup_success, arg) ?: "")
                return 0
            } else if (cmd == "restore") {
                val gameName = "$arg.advgame"
                miscCopyGameFileFromExtStorage(gameName)
                myPrintf(AppData.applicationContext?.getString(R.string.adventure_restore_success, arg) ?: "")
                return 0
            }
        } else if (parts.size == 1) {
            val cmd = parts[0].lowercase()
            if (cmd == "save" || cmd == "resume") {
                saveFileName = ""
            }
        }
        inputQueue.put(command + "\n")
        return 0
    }

    private suspend fun runGame() {
        try {
            inputQueue.clear()
            gameSuspended = 0
            initialise()
            gameLoop()
        } catch (e: InterruptedException) {
            GcLog.d("colossalcaveadventurekotlin.Adventure game thread interrupted/suspended")
        } catch (e: Exception) {
            GcLog.e("Error running colossalcaveadventurekotlin.Adventure: ", e)
            System.err.println("Error running colossalcaveadventurekotlin.Adventure: ")
            e.printStackTrace()
        }
    }

    private fun score(mode: Int) {
        SCORE = 0
        MXSCOR = 0
        for (i in 50..MAXTRS) {
            if (PTEXT[i] == 0) continue
            Kbuffer = 12
            if (i == CHEST) Kbuffer = 14
            if (i > CHEST) Kbuffer = 16
            if (PROP[i] >= 0) SCORE += 2
            if (PLACE[i] == 3 && PROP[i] == 0) SCORE += Kbuffer - 2
            MXSCOR += Kbuffer
        }

        SCORE += (MAXDIE - NUMDIE) * 10
        MXSCOR += MAXDIE * 10
        if (mode == 0) SCORE += 4
        MXSCOR += 4
        if (DFLAG != 0) SCORE += 25
        MXSCOR += 25
        if (CLOSNG) SCORE += 25
        MXSCOR += 25
        if (CLOSED) {
            if (BONUS == 0) SCORE += 10
            if (BONUS == 135) SCORE += 25
            if (BONUS == 134) SCORE += 30
            if (BONUS == 133) SCORE += 45
        }
        MXSCOR += 45
        if (PLACE[MAGZIN] == 108) SCORE += 1
        MXSCOR += 1
        SCORE += 2
        MXSCOR += 2

        for (i in 1..HNTMAX) {
            if (HINTED[i]) SCORE -= HINTS[i][2]
        }
        if (NOVICE) SCORE -= 5
        if (CLSHNT) SCORE -= 10
        SCORE -= TRNLUZ + SAVED

        if (mode < 0) return

        if (SCORE + TRNLUZ + 1 >= MXSCOR && TRNLUZ != 0) rSpeak(242)
        if (SCORE + SAVED + 1 >= MXSCOR && SAVED != 0) rSpeak(143)
        setPrm(1, SCORE, MXSCOR)
        setPrm(3, TURNS, TURNS)
        rSpeak(262)
        var iVal = 1
        while (iVal <= CLSSES) {
            if (CVAL[iVal] >= SCORE) break
            iVal++
        }
        if (iVal > CLSSES) {
            rSpeak(265)
            myExit(0)
            return
        }
        speak(CTEXT[iVal])
        SPK = 264
        if (iVal >= CLSSES) {
            rSpeak(SPK)
            myExit(0)
            return
        }
        val pointsNeeded = CVAL[iVal] + 1 - SCORE
        setPrm(1, pointsNeeded, pointsNeeded)
        SPK = 263
        rSpeak(SPK)
        myExit(0)
    }

    private fun myExit(code: Int) {
        myPrintf(AppData.applicationContext?.getString(R.string.adventure_restart_hint) ?: "")
        try {
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            // ignore
        }
        inputQueue.clear()
        start()
        throw InterruptedException("Game Restarted")
    }

    private suspend fun initialise() {
        GcLog.d("Initializing colossalcaveadventurekotlin.Adventure Game...")
        myPrintf(AppData.applicationContext?.getString(R.string.adventure_game_welcome) ?: "")
        val firstTime = !quickInit()
        if (firstTime) {
            myPrintf(AppData.applicationContext?.getString(R.string.adventure_init_started) ?: "")
            rawInit()
            report()
            quickSave()
            myPrintf(AppData.applicationContext?.getString(R.string.adventure_init_complete) ?: "")
        }
        finishInit()
    }

    private suspend fun quickInit(): Boolean {
        return quickIO(isReading = true)
    }

    private suspend fun rawInit() {
        for (i in 1..300) {
            if (i <= 100) PTEXT[i] = 0
            if (i <= RTXSIZ) RTEXT[i] = 0
            if (i <= CLSMAX) CTEXT[i] = 0
            if (i <= 100) OBJSND[i] = 0
            if (i <= 100) OBJTXT[i] = 0
            if (i > LOCSIZ) continue
            STEXT[i] = 0
            LTEXT[i] = 0
            COND[i] = 0
            KEY[i] = 0
            LOCSND[i] = 0
        }
        LINUSE = 1
        TRVS = 1
        CLSSES = 0
        TRNVLS = 0

        var label = 1002
        while (label != 0) {
            when (label) {
                1002 -> {
                    SECT = getNum(1)
                    OLDLOC = -1
                    label = when (SECT) {
                        0 -> 0 // return
                        1 -> 1004
                        2 -> 1004
                        3 -> 1030
                        4 -> 1040
                        5 -> 1004
                        6 -> 1004
                        7 -> 1050
                        8 -> 1060
                        9 -> 1070
                        10 -> 1004
                        11 -> 1080
                        12 -> 1002 // break -> switch loop
                        13 -> 1090
                        14 -> 1004
                        else -> {
                            bug(9)
                            0
                        }
                    }
                }
                1004 -> {
                    KK = LINUSE
                    label = 1005
                }
                1005 -> {
                    LINUSE = KK
                    LOC = getNum(1)
                    if (lnleng >= lnposn + 70) bug(0)
                    if (LOC == -1) {
                        label = 1002
                    } else {
                        if (lnleng < lnposn) bug(1)
                        label = 1006
                    }
                }
                1006 -> {
                    KK++
                    if (KK >= LINSIZ) bug(2)
                    LINES[KK] = getTxt(false, false, false, KK)
                    if (LINES[KK] != -1) {
                        label = 1006
                    } else {
                        LINES[LINUSE] = KK
                        if (LOC == OLDLOC) {
                            label = 1005
                        } else {
                            OLDLOC = LOC
                            LINES[LINUSE] = -KK
                            label = when (SECT) {
                                14 -> 1014
                                10 -> 1012
                                6 -> 1011
                                5 -> 1010
                                else -> {
                                    if (LOC > LOCSIZ) bug(10)
                                    if (SECT == 1) 1008 else 1009
                                }
                            }
                        }
                    }
                }
                1008 -> {
                    LTEXT[LOC] = LINUSE
                    label = 1005
                }
                1009 -> {
                    STEXT[LOC] = LINUSE
                    label = 1005
                }
                1010 -> {
                    if (LOC in 1..100) PTEXT[LOC] = LINUSE
                    label = 1005
                }
                1011 -> {
                    if (LOC > RTXSIZ) bug(6)
                    RTEXT[LOC] = LINUSE
                    label = 1005
                }
                1012 -> {
                    CLSSES++
                    if (CLSSES > CLSMAX) bug(11)
                    CTEXT[CLSSES] = LINUSE
                    CVAL[CLSSES] = LOC
                    label = 1005
                }
                1014 -> {
                    TRNVLS++
                    if (TRNVLS > TRNSIZ) bug(11)
                    TTEXT[TRNVLS] = LINUSE
                    TRNVAL[TRNVLS] = LOC
                    label = 1005
                }
                1030 -> {
                    LOC = getNum(1)
                    if (LOC == -1) {
                        label = 1002
                    } else {
                        NEWLOC = getNum(0)
                        if (KEY[LOC] != 0) {
                            TRVS--
                            TRAVEL[TRVS] = -TRAVEL[TRVS]
                            TRVS++
                        } else {
                            KEY[LOC] = TRVS
                        }
                        label = 1035
                    }
                }
                1035 -> {
                    L = getNum(0)
                    if (L == 0) {
                        TRVS--
                        TRAVEL[TRVS] = -TRAVEL[TRVS]
                        TRVS++
                        label = 1030
                    } else {
                        TRAVEL[TRVS] = NEWLOC * 1000 + L
                        TRVS++
                        if (TRVS == TRVSIZ) bug(3)
                        label = 1035
                    }
                }
                1040 -> {
                    var jVal = 10000
                    TABNDX = 1
                    while (TABNDX <= TABSIZ) {
                        KTAB[TABNDX] = getNum(1)
                        if (KTAB[TABNDX] == -1) {
                            label = 1002
                            break
                        }
                        jVal += 7
                        ATAB[TABNDX] = getTxt(true, true, true, 0) + jVal * jVal
                        TABNDX++
                    }
                    if (label != 1002) bug(4)
                }
                1050 -> {
                    OBJ = getNum(1)
                    if (OBJ == -1) {
                        label = 1002
                    } else {
                        PLAC[OBJ] = getNum(0)
                        FIXD[OBJ] = getNum(0)
                        label = 1050
                    }
                }
                1060 -> {
                    VERB = getNum(1)
                    if (VERB == -1) {
                        label = 1002
                    } else {
                        ACTSPK[VERB] = getNum(0)
                        label = 1060
                    }
                }
                1070 -> {
                    Kbuffer = getNum(1)
                    if (Kbuffer == -1) {
                        label = 1002
                    } else {
                        label = 1071
                    }
                }
                1071 -> {
                    LOC = getNum(0)
                    if (LOC == 0) {
                        label = 1070
                    } else {
                        if (tstBit(COND[LOC], Kbuffer)) bug(8)
                        COND[LOC] += setBit(Kbuffer)
                        label = 1071
                    }
                }
                1080 -> {
                    HNTMAX = 0
                    label = 1081
                }
                1081 -> {
                    Kbuffer = getNum(1)
                    if (Kbuffer == -1) {
                        label = 1002
                    } else {
                        if (Kbuffer <= 0 || Kbuffer > HNTSIZ) bug(7)
                        for (idx in 1..4) {
                            HINTS[Kbuffer][idx] = getNum(0)
                        }
                        HNTMAX = maxOf(HNTMAX, Kbuffer)
                        label = 1081
                    }
                }
                1090 -> {
                    Kbuffer = getNum(1)
                    if (Kbuffer == -1) {
                        label = 1002
                    } else {
                        KK = getNum(0)
                        I = getNum(0)
                        if (I == 0) {
                            LOCSND[Kbuffer] = KK
                        } else {
                            OBJSND[Kbuffer] = if (KK > 0) KK else 0
                            OBJTXT[Kbuffer] = if (I > 0) I else 0
                        }
                        label = 1090
                    }
                }
            }
        }
    }

    private fun report() {
        // empty report in original C
    }

    private suspend fun quickSave() {
        quickIO(isReading = false)
    }

    private suspend fun quickIO(isReading: Boolean): Boolean {
        val file = File(backupDirectory, "adventure.data")
        if (isReading && !file.exists()) return false

        try {
            var reader: BufferedReader? = null
            var tokenizer: StreamTokenizer? = null
            var writer: BufferedWriter? = null

            if (isReading) {
                reader = BufferedReader(FileReader(file))
                tokenizer = StreamTokenizer(reader)
                tokenizer.resetSyntax()
                tokenizer.whitespaceChars(0, 32)
                tokenizer.wordChars(33, 255)
            } else {
                writer = BufferedWriter(FileWriter(file))
            }

            var initCksum = 1L
            var recCnt = 0L

            fun ioItem(getValue: () -> Int, setValue: (Int) -> Unit) {
                val v: Int
                if (isReading) {
                    tokenizer!!.nextToken()
                    v = tokenizer.sval.toInt()
                    setValue(v)
                } else {
                    v = getValue()
                    writer!!.write("$v ")
                }
                initCksum = ((initCksum * 13 + v) % 60000000)
                recCnt++
            }

            fun ioArray(arr: IntArray, size: Int) {
                for (i in 0 until size) {
                    val v: Int
                    if (isReading) {
                        tokenizer!!.nextToken()
                        v = tokenizer.sval.toInt()
                        arr[i] = v
                    } else {
                        v = arr[i]
                        writer!!.write("$v ")
                    }
                    initCksum = ((initCksum * 13 + v) % 60000000)
                    recCnt++
                }
            }

            // Item properties
            ioItem({ LINUSE }, { LINUSE = it })
            ioItem({ TRVS }, { TRVS = it })
            ioItem({ CLSSES }, { CLSSES = it })
            ioItem({ TRNVLS }, { TRNVLS = it })
            ioItem({ TABNDX }, { TABNDX = it })
            ioItem({ HNTMAX }, { HNTMAX = it })

            // Arrays
            ioArray(PTEXT, 100)
            ioArray(RTEXT, 277)
            ioArray(CTEXT, 12)
            ioArray(OBJSND, 100)
            ioArray(OBJTXT, 100)
            ioArray(STEXT, 185)
            ioArray(LTEXT, 185)
            ioArray(COND, 185)
            ioArray(KEY, 185)
            ioArray(LOCSND, 185)
            ioArray(LINES, 12500)
            ioArray(CVAL, 12)
            ioArray(TTEXT, 5)
            ioArray(TRNVAL, 5)
            ioArray(TRAVEL, 885)
            ioArray(KTAB, 330)
            ioArray(ATAB, 330)
            ioArray(PLAC, 100)
            ioArray(FIXD, 100)
            ioArray(ACTSPK, 35)

            // HINTS 2D Array: (HNTMAX+1)*5 - 1 elements = 104 elements
            val targetHints = (20 + 1) * 5 - 1
            var hintsCount = 0
            for (i in 0..20) {
                for (j in 0..4) {
                    if (hintsCount >= targetHints) break
                    val v: Int
                    if (isReading) {
                        tokenizer!!.nextToken()
                        v = tokenizer.sval.toInt()
                        HINTS[i][j] = v
                    } else {
                        v = HINTS[i][j]
                        writer!!.write("$v ")
                    }
                    initCksum = ((initCksum * 13 + v) % 60000000)
                    recCnt++
                    hintsCount++
                }
            }

            // Read or write checksum & rec count at the end of the file
            var fileCksum = -1L
            var fileRec = -1L
            if (isReading) {
                tokenizer!!.nextToken()
                fileCksum = tokenizer.sval.toLong()
                tokenizer!!.nextToken()
                fileRec = tokenizer.sval.toLong()

                reader!!.close()
                return initCksum == fileCksum && recCnt == fileRec
            } else {
                writer!!.write("$initCksum $recCnt ")
                writer.close()
                return true
            }

        } catch (e: Exception) {
            GcLog.e("quickIO error during ${if (isReading) "read" else "write"}: ", e)
            return false
        }
    }

    private fun finishInit() {
        for (i in 1..100) {
            PLACE[i] = 0
            PROP[i] = 0
            LINK[i] = 0
            LINK[i + 100] = 0
        }
        for (i in 1..LOCSIZ) {
            ABB[i] = 0
            if (LTEXT[i] != 0 && KEY[i] != 0) {
                val kIdx = KEY[i]
                if ((abs(TRAVEL[kIdx]) % 1000) == 1) {
                    COND[i] = 2
                }
            }
            ATLOC[i] = 0
        }
        for (i in 1..100) {
            val kIdx = 101 - i
            if (FIXD[kIdx] > 0) {
                drop(kIdx + 100, FIXD[kIdx])
                drop(kIdx, PLAC[kIdx])
            }
        }
        for (i in 1..100) {
            val kIdx = 101 - i
            FIXED[kIdx] = FIXD[kIdx]
            if (PLAC[kIdx] != 0 && FIXD[kIdx] <= 0) {
                drop(kIdx, PLAC[kIdx])
            }
        }
        MAXTRS = 79
        TALLY = 0
        for (i in 50..MAXTRS) {
            if (PTEXT[i] != 0) PROP[i] = -1
            TALLY -= PROP[i]
        }
        for (i in 1..HNTMAX) {
            HINTED[i] = false
            HINTLC[i] = 0
        }
        AXE = vocwrd(12405, 1)
        BATTER = vocwrd(201202005, 1)
        BEAR = vocwrd(2050118, 1)
        BIRD = vocwrd(2091804, 1)
        BOTTLE = vocwrd(215202012, 1)
        CAGE = vocwrd(3010705, 1)
        CAVE = vocwrd(3012205, 0)
        CHAIN = vocwrd(308010914, 1)
        CHASM = vocwrd(308011913, 1)
        CHEST = vocwrd(308051920, 1)
        COINS = vocwrd(315091419, 1)
        DOOR = vocwrd(4151518, 1)
        DRAGON = vocwrd(418010715, 1)
        DWARF = vocwrd(423011806, 1)
        EMRALD = vocwrd(513051801, 1)
        ENTER = vocwrd(514200518, 0)
        FISSUR = vocwrd(609191921, 1)
        FOOD = vocwrd(6151504, 1)
        GRATE = vocwrd(718012005, 1)
        KEYS = vocwrd(11052519, 1)
        LAMP = vocwrd(12011316, 1)
        LIMIT = 330
        LOCK = vocwrd(12150311, 2)
        LOOK = vocwrd(12151511, 0)
        NUGGET = vocwrd(7151204, 1)
        NUL = vocwrd(14211212, 0)
        OIL = vocwrd(150912, 1)
        OYSTER = vocwrd(1525192005, 1)
        PEARL = vocwrd(1605011812, 1)
        PILLOW = vocwrd(1609121215, 1)
        PLANT = vocwrd(1612011420, 1)
        PLANT2 = PLANT + 1
        ROD = vocwrd(181504, 1)
        ROD2 = ROD + 1
        RUBY = vocwrd(18210225, 1)
        RUG = vocwrd(182107, 1)
        SAY = vocwrd(190125, 2)
        SIGN = vocwrd(19090714, 1)
        SNAKE = vocwrd(1914011105, 1)
        STEPS = vocwrd(1920051619, 1)
        STREAM = vocwrd(1920180501, 0)
        THROW = vocwrd(2008181523, 2)
        TROLL = vocwrd(2018151212, 1)
        TROLL2 = TROLL + 1
        VASE = vocwrd(22011905, 1)
        VEND = vocwrd(1755140409, 1)
        WATER = vocwrd(1851200518, 1)
        ZZWORD = 0 // placeholder for magic word, recomputed at run
        AMBER = vocwrd(113020518, 1)
        SAPPH = vocwrd(1901161608, 1)
        JADE = vocwrd(10010405, 1)
        URN = vocwrd(211814, 1)
        CAVITY = vocwrd(301220920, 1)
        BLOOD = vocwrd(212151504, 1)
        MESSAG = vocwrd(1305191901, 1)
        OGRE = vocwrd(15071805, 1)
        BACK = vocwrd(2010311, 0)
        KNIFE = vocwrd(1114090605, 1)
        MAGZIN = vocwrd(1301070126, 1)
        MIRROR = vocwrd(1309181815, 1)
        RESER = vocwrd(1805190518, 1)
        VOLCAN = vocwrd(1765120301, 1)
        EGGS = vocwrd(5070719, 1)
        PYRAM = vocwrd(1625180113, 1)
        TRIDNT = vocwrd(2018090405, 1)
        CHLOC = 114
        CHLOC2 = 140
        DALTLC = 18
        MAXDIE = 3
        NUMDIE = 0
        SAVED = 0
        CLSHNT = false
        DETAIL = 0
        ABBNUM = 5
        CLOCK1 = 30
        CLOCK2 = 50
        PANIC = false
        CLOSED = false
        CLOSNG = false
        DKILL = 0
        FOOBAR = 0
        BONUS = 0
        IWEST = 0
        IGO = 0
        LIMIT = 330
        if (NOVICE) LIMIT = 1000
    }

    private suspend fun gameLoop() {
        var label = 1 // Start at L1 in main.c
        while (true) {
            try {
                when (label) {
                    1 -> {
                        SETUP = -1
                        I = ran(-1).toInt()
                        ZZWORD = rndVoc(3, 0) + MESH * 2
                        NOVICE = yes(65, 1, 0)
                        NEWLOC = 1
                        LOC = 1
                        LIMIT = 330
                        if (NOVICE) LIMIT = 1000
                        label = 2
                    }
                    2 -> {
                        if (!outsid(NEWLOC) || NEWLOC == 0 || !CLOSNG) {
                            label = 71
                        } else {
                            rSpeak(130)
                            NEWLOC = LOC
                            if (!PANIC) CLOCK2 = 15
                            PANIC = true
                            label = 71
                        }
                    }
                    71 -> {
                        if (NEWLOC == LOC || forced(LOC) || cndbit(LOC, 3)) {
                            label = 74
                        } else {
                            var blocked = false
                            for (idx in 1..5) {
                                if (ODLOC[idx] == NEWLOC && DSEEN[idx] != 0) {
                                    NEWLOC = LOC
                                    rSpeak(2)
                                    blocked = true
                                    break
                                }
                            }
                            label = 74
                        }
                    }
                    74 -> {
                        LOC = NEWLOC
                        if (LOC == 0 || forced(LOC) || cndbit(NEWLOC, 3)) {
                            label = 2000
                        } else if (DFLAG != 0) {
                            label = 6000
                        } else {
                            if (indeep(LOC)) DFLAG = 1
                            label = 2000
                        }
                    }
                    6000 -> {
                        if (DFLAG != 1) {
                            label = 6010
                        } else {
                            if (!indeep(LOC) || (pct(95) && (!cndbit(LOC, 4) || pct(85)))) {
                                label = 2000
                            } else {
                                DFLAG = 2
                                for (idx in 1..2) {
                                    val jVal = 1 + ran(5).toInt()
                                    if (pct(50)) DLOC[jVal] = 0
                                }
                                for (idx in 1..5) {
                                    if (DLOC[idx] == LOC) DLOC[idx] = DALTLC
                                    ODLOC[idx] = DLOC[idx]
                                }
                                rSpeak(3)
                                drop(AXE, LOC)
                                label = 2000
                            }
                        }
                    }
                    6010 -> {
                        DTOTAL = 0
                        ATTACK = 0
                        STICK = 0
                        for (idx in 1..6) {
                            if (DLOC[idx] == 0) continue
                            var jVal = 1
                            var kkVal = DLOC[idx]
                            kkVal = KEY[kkVal]
                            if (kkVal != 0) {
                                while (true) {
                                    val travelVal = TRAVEL[kkVal]
                                    val newLocVal = (abs(travelVal) / 1000) % 1000
                                    val xIdx = jVal - 1
                                    if (newLocVal > 300 || !indeep(newLocVal) || newLocVal == ODLOC[idx] ||
                                        (jVal > 1 && newLocVal == TK[xIdx]) || jVal >= 20 || newLocVal == DLOC[idx] ||
                                        forced(newLocVal) || (idx == 6 && cndbit(newLocVal, 3)) ||
                                        (abs(travelVal) / 1000000) == 100
                                    ) {
                                        // skip
                                    } else {
                                        TK[jVal] = newLocVal
                                        jVal++
                                    }
                                    kkVal++
                                    if (TRAVEL[kkVal - 1] < 0) break
                                }
                            }
                            TK[jVal] = ODLOC[idx]
                            if (jVal >= 2) jVal--
                            jVal = 1 + ran(jVal).toInt()
                            ODLOC[idx] = DLOC[idx]
                            DLOC[idx] = TK[jVal]
                            val isSeen = (DSEEN[idx] != 0 && indeep(LOC)) || (DLOC[idx] == LOC || ODLOC[idx] == LOC)
                            DSEEN[idx] = if (isSeen) 1 else 0
                            if (DSEEN[idx] == 0) continue
                            DLOC[idx] = LOC
                            if (idx != 6) {
                                DTOTAL++
                                if (ODLOC[idx] == DLOC[idx]) {
                                    ATTACK++
                                    if (KNFLOC >= 0) KNFLOC = LOC
                                    if (ran(1000) < 95 * (DFLAG - 2)) STICK++
                                }
                            } else {
                                if (LOC == CHLOC || PROP[CHEST] >= 0) continue
                                var kVal = 0
                                for (jIdx in 50..MAXTRS) {
                                    if (jIdx == PYRAM && (LOC == PLAC[PYRAM] || LOC == PLAC[EMRALD])) continue
                                    if (toting(jIdx)) {
                                        kVal = 1
                                        break
                                    }
                                    if (here(jIdx)) kVal = 1
                                }
                                if (TALLY == 1 && kVal == 0 && PLACE[CHEST] == 0 && here(LAMP) && PROP[LAMP] == 1) {
                                    rSpeak(186)
                                    move(CHEST, CHLOC)
                                    move(MESSAG, CHLOC2)
                                    DLOC[6] = CHLOC
                                    ODLOC[6] = CHLOC
                                    DSEEN[6] = 0
                                } else {
                                    if (ODLOC[6] != DLOC[6] && pct(20)) rSpeak(127)
                                    var stoleAny = false
                                    for (jIdx in 50..MAXTRS) {
                                        if (jIdx == PYRAM && (LOC == PLAC[PYRAM] || LOC == PLAC[EMRALD])) continue
                                        if (at(jIdx) && FIXED[jIdx] == 0) carry(jIdx, LOC)
                                        if (toting(jIdx)) {
                                            drop(jIdx, CHLOC)
                                            stoleAny = true
                                        }
                                    }
                                    if (stoleAny) {
                                        if (PLACE[CHEST] == 0) {
                                            move(CHEST, CHLOC)
                                            move(MESSAG, CHLOC2)
                                        }
                                        rSpeak(128)
                                        DLOC[6] = CHLOC
                                        ODLOC[6] = CHLOC
                                        DSEEN[6] = 0
                                    }
                                }
                            }
                        }

                        if (DTOTAL == 0) {
                            label = 2000
                        } else {
                            setPrm(1, DTOTAL, 0)
                            rSpeak(4 + 1 / DTOTAL)
                            if (ATTACK == 0) {
                                label = 2000
                            } else {
                                if (DFLAG == 2) DFLAG = 3
                                setPrm(1, ATTACK, 0)
                                Kbuffer = if (ATTACK > 1) 250 else 6
                                rSpeak(Kbuffer)
                                setPrm(1, STICK, 0)
                                rSpeak(Kbuffer + 1 + 2 / (1 + STICK))
                                if (STICK == 0) {
                                    label = 2000
                                } else {
                                    OLDLC2 = LOC
                                    label = 99
                                }
                            }
                        }
                    }
                    2000 -> {
                        if (LOC == 0) {
                            label = 99
                            continue
                        }
                        var kkVal = STEXT[LOC]
                        if ((ABB[LOC] % ABBNUM) == 0 || kkVal == 0) kkVal = LTEXT[LOC]
                        GcLog.d("Entering 2000: LOC=$LOC, STEXT=${STEXT[LOC]}, ABB=${ABB[LOC]}, LTEXT=${LTEXT[LOC]}, kkVal=$kkVal")
                        println("Entering 2000: LOC=$LOC, STEXT=${STEXT[LOC]}, ABB=${ABB[LOC]}, LTEXT=${LTEXT[LOC]}, kkVal=$kkVal")
                        if (!forced(LOC) && dark(0)) {
                            if (WZDARK && pct(35)) {
                                label = 90
                                continue
                            }
                            kkVal = RTEXT[16]
                        }
                        if (toting(BEAR)) rSpeak(141)
                        speak(kkVal)
                        Kbuffer = 1
                        if (forced(LOC)) {
                            label = 8
                            continue
                        }
                        if (LOC == 33 && pct(25) && !CLOSNG) rSpeak(7)

                        if (dark(0)) {
                            label = 2012
                            continue
                        }
                        ABB[LOC]++
                        var iVal = ATLOC[LOC]
                        while (true) {
                            if (iVal == 0) {
                                label = 2012
                                break
                            }
                            OBJ = iVal
                            if (OBJ > 100) OBJ -= 100
                            if (OBJ == STEPS && toting(NUGGET)) {
                                iVal = LINK[iVal]
                                continue
                            }
                            if (PROP[OBJ] < 0) {
                                if (CLOSED) {
                                    iVal = LINK[iVal]
                                    continue
                                }
                                PROP[OBJ] = 0
                                if (OBJ == RUG || OBJ == CHAIN) PROP[OBJ] = 1
                                TALLY--
                            }
                            var kkProps = PROP[OBJ]
                            if (OBJ == STEPS && LOC == FIXED[STEPS]) kkProps = 1
                            pSpeak(OBJ, kkProps)
                            iVal = LINK[iVal]
                        }
                    }
                    2009 -> {
                        Kbuffer = 54
                        label = 2010
                    }
                    2010 -> {
                        SPK = Kbuffer
                        label = 2011
                    }
                    2011 -> {
                        rSpeak(SPK)
                        label = 2012
                    }
                    2012 -> {
                        VERB = 0
                        OLDOBJ = OBJ
                        OBJ = 0
                        label = 2600
                    }
                    2600 -> {
                        if (COND[LOC] >= CONDS) {
                            var hintIdx = 1
                            while (hintIdx <= HNTMAX) {
                                if (!HINTED[hintIdx]) {
                                    if (!cndbit(LOC, hintIdx + 10)) HINTLC[hintIdx] = -1
                                    HINTLC[hintIdx]++
                                    if (HINTLC[hintIdx] >= HINTS[hintIdx][1]) {
                                        HINT = hintIdx
                                        label = 40000
                                        break
                                    }
                                }
                                hintIdx++
                            }
                            if (label == 40000) continue
                        }
                        // KICK THE RANDOM GENERATOR
                        if (CLOSED) {
                            if (PROP[OYSTER] < 0 && toting(OYSTER)) pSpeak(OYSTER, 1)
                            for (idx in 1..100) {
                                if (toting(idx) && PROP[idx] < 0) PROP[idx] = -1 - PROP[idx]
                            }
                        }
                        WZDARK = dark(0)
                        if (KNFLOC > 0 && KNFLOC != LOC) KNFLOC = 0
                        ran(1)
                        val word1 = IntArray(1)
                        val word1x = IntArray(1)
                        val word2 = IntArray(1)
                        val word2x = IntArray(1)
                        getIn(word1, word1x, word2, word2x)
                        WD1 = word1[0]
                        WD1X = word1x[0]
                        WD2 = word2[0]
                        WD2X = word2x[0]
                        label = 2607
                    }
                    2607 -> {
                        FOOBAR = if (FOOBAR > 0) -FOOBAR else 0
                        TURNS++
                        if (TURNS == THRESH) {
                            speak(TTEXT[TRNDEX])
                            TRNLUZ += TRNVAL[TRNDEX] / 100000
                            TRNDEX++
                            THRESH = -1
                            if (TRNDEX <= TRNVLS) THRESH = (TRNVAL[TRNDEX] % 100000) + 1
                        }
                        if (VERB == SAY && WD2 > 0) VERB = 0
                        if (VERB == SAY) {
                            label = 4090
                            continue
                        }
                        if (TALLY == 0 && indeep(LOC) && LOC != 33) CLOCK1--
                        if (CLOCK1 == 0) {
                            label = 10000
                            continue
                        }
                        if (CLOCK1 < 0) CLOCK2--
                        if (CLOCK2 == 0) {
                            label = 11000
                            continue
                        }
                        if (PROP[LAMP] == 1) LIMIT--
                        if (LIMIT <= 30 && here(BATTER) && PROP[BATTER] == 0 && here(LAMP)) {
                            label = 12000
                            continue
                        }
                        if (LIMIT == 0) {
                            label = 12400
                            continue
                        }
                        if (LIMIT <= 30) {
                            label = 12200
                            continue
                        }
                        label = 19999
                    }
                    19999 -> {
                        Kbuffer = 43
                        if (liqLoc(LOC) == WATER) Kbuffer = 70
                        V1 = vocab(WD1, -1)
                        V2 = vocab(WD2, -1)
                        if (V1 == ENTER && (V2 == STREAM || V2 == 1000 + WATER)) {
                            label = 2010
                            continue
                        }
                        if (V1 == ENTER && WD2 > 0) {
                            label = 2800
                            continue
                        }
                        if ((V1 == 1000 + WATER || V1 == 1000 + OIL) && (V2 == 1000 + PLANT || V2 == 1000 + DOOR)) {
                            val xVal = V2 - 1000
                            if (at(xVal)) WD2 = makeWd(16152118L)
                        }
                        if (V1 == 1000 + CAGE && V2 == 1000 + BIRD && here(CAGE) && here(BIRD)) {
                            WD1 = makeWd(301200308L)
                        }
                        if (WD1 == makeWd(23051920L)) {
                            IWEST++
                            if (IWEST == 10) rSpeak(17)
                        }
                        if (WD1 == makeWd(715L) && WD2 != 0) {
                            IGO++
                            if (IGO == 10) rSpeak(276)
                        }
                        I = vocab(WD1, -1)
                        if (I == -1) {
                            label = 3000
                            continue
                        }
                        Kbuffer = I % 1000
                        KQ = I / 1000 + 1
                        label = when (KQ - 1) {
                            0 -> 8
                            1 -> 5000
                            2 -> 4000
                            3 -> 2010
                            else -> {
                                bug(22)
                                2600
                            }
                        }
                    }
                    2800 -> {
                        WD1 = WD2
                        WD1X = WD2X
                        WD2 = 0
                        label = 2620
                    }
                    2620 -> {
                        label = 19999
                    }
                    3000 -> {
                        setPrm(1, WD1, WD1X)
                        rSpeak(254)
                        label = 2600
                    }
                    4000 -> {
                        val actRes = action(4000)
                        label = routeActionDest(actRes)
                    }
                    4090 -> {
                        val actRes = action(4090)
                        label = routeActionDest(actRes)
                    }
                    5000 -> {
                        val actRes = action(5000)
                        label = routeActionDest(actRes)
                    }
                    8 -> {
                        var kkVal = KEY[LOC]
                        var llVal = 0
                        NEWLOC = LOC
                        if (kkVal == 0) bug(26)
                        if (Kbuffer == NUL) {
                            label = 2
                            continue
                        }
                        if (Kbuffer == BACK) {
                            label = 20
                            continue
                        }
                        if (Kbuffer == LOOK) {
                            label = 30
                            continue
                        }
                        if (Kbuffer == CAVE) {
                            label = 40
                            continue
                        }
                        OLDLC2 = OLDLOC
                        OLDLOC = LOC
                        while (true) {
                            llVal = abs(TRAVEL[kkVal])
                            if ((llVal % 1000) == 1 || (llVal % 1000) == Kbuffer) {
                                llVal /= 1000
                                while (true) {
                                    NEWLOC = llVal / 1000
                                    Kbuffer = NEWLOC % 100
                                    if (NEWLOC <= 300 || PROP[Kbuffer] == (NEWLOC / 100 - 3)) {
                                        break
                                    }
                                    while (true) {
                                        if (TRAVEL[kkVal] < 0) bug(25)
                                        kkVal++
                                        NEWLOC = abs(TRAVEL[kkVal]) / 1000
                                        if (NEWLOC != llVal) break
                                    }
                                    llVal = NEWLOC
                                }
                                break
                            }
                            if (TRAVEL[kkVal] < 0) {
                                label = 50
                                break
                            }
                            kkVal++
                        }
                        if (label == 50) continue
                        // check bounds
                        if (NEWLOC <= 100) {
                            if (NEWLOC != 0 && !pct(NEWLOC)) {
                                while (true) {
                                    if (TRAVEL[kkVal] < 0) bug(25)
                                    kkVal++
                                    NEWLOC = abs(TRAVEL[kkVal]) / 1000
                                    if (NEWLOC != llVal) break
                                }
                                llVal = NEWLOC
                                label = 9 // loop back to L9 logic in C
                                continue
                            }
                            NEWLOC = llVal % 1000
                            if (NEWLOC <= 300) {
                                label = 2
                                continue
                            }
                            if (NEWLOC <= 500) {
                                label = 30000
                                continue
                            }
                            rSpeak(NEWLOC - 500)
                            NEWLOC = LOC
                            label = 2
                        } else {
                            if (toting(Kbuffer) || (NEWLOC > 200 && at(Kbuffer))) {
                                NEWLOC = llVal % 1000
                                if (NEWLOC <= 300) {
                                    label = 2
                                    continue
                                }
                                if (NEWLOC <= 500) {
                                    label = 30000
                                    continue
                                }
                                rSpeak(NEWLOC - 500)
                                NEWLOC = LOC
                                label = 2
                            } else {
                                while (true) {
                                    if (TRAVEL[kkVal] < 0) bug(25)
                                    kkVal++
                                    NEWLOC = abs(TRAVEL[kkVal]) / 1000
                                    if (NEWLOC != llVal) break
                                }
                                llVal = NEWLOC
                                label = 9 // loop back to L9 logic in C
                            }
                        }
                    }
                    9 -> {
                        // helper mapping state for L9 in main.c
                        var kkVal = KEY[LOC]
                        // we need to re-find key from loop
                        label = 8
                    }
                    30000 -> {
                        NEWLOC -= 300
                        label = when (NEWLOC) {
                            1 -> 30100
                            2 -> 30200
                            3 -> 30300
                            else -> {
                                bug(20)
                                2
                            }
                        }
                    }
                    30100 -> {
                        NEWLOC = 99 + 100 - LOC
                        if (HOLDNG == 0 || (HOLDNG == 1 && toting(EMRALD))) {
                            label = 2
                        } else {
                            NEWLOC = LOC
                            rSpeak(117)
                            label = 2
                        }
                    }
                    30200 -> {
                        drop(EMRALD, LOC)
                        label = 9 // loop back L9
                    }
                    30300 -> {
                        if (PROP[TROLL] != 1) {
                            NEWLOC = plac[TROLL] + fixd[TROLL] - LOC
                            if (PROP[TROLL] == 0) PROP[TROLL] = 1
                            if (!toting(BEAR)) {
                                label = 2
                            } else {
                                rSpeak(162)
                                PROP[CHASM] = 1
                                PROP[TROLL] = 2
                                drop(BEAR, NEWLOC)
                                FIXED[BEAR] = -1
                                PROP[BEAR] = 3
                                OLDLC2 = NEWLOC
                                label = 99
                            }
                        } else {
                            pSpeak(TROLL, 1)
                            PROP[TROLL] = 0
                            move(TROLL2, 0)
                            move(TROLL2 + 100, 0)
                            move(TROLL, plac[TROLL])
                            move(TROLL + 100, fixd[TROLL])
                            juggle(CHASM)
                            NEWLOC = LOC
                            label = 2
                        }
                    }
                    20 -> {
                        Kbuffer = OLDLOC
                        if (forced(Kbuffer)) Kbuffer = OLDLC2
                        OLDLC2 = OLDLOC
                        OLDLOC = LOC
                        K2 = 0
                        if (Kbuffer == LOC) K2 = 91
                        if (cndbit(LOC, 4)) K2 = 274
                        if (K2 != 0) {
                            rSpeak(K2)
                            label = 2
                        } else {
                            var kkVal = KEY[LOC]
                            while (true) {
                                val llVal = (abs(TRAVEL[kkVal]) / 1000) % 1000
                                if (llVal == Kbuffer) {
                                    Kbuffer = abs(TRAVEL[kkVal]) % 1000
                                    kkVal = KEY[LOC]
                                    label = 9
                                    break
                                }
                                if (llVal <= 300) {
                                    val jIdx = KEY[llVal]
                                    if (forced(llVal) && ((abs(TRAVEL[jIdx]) / 1000) % 1000) == Kbuffer) {
                                        K2 = kkVal
                                    }
                                }
                                if (TRAVEL[kkVal] < 0) {
                                    kkVal = K2
                                    if (kkVal != 0) {
                                        Kbuffer = abs(TRAVEL[kkVal]) % 1000
                                        kkVal = KEY[LOC]
                                        label = 9
                                    } else {
                                        rSpeak(140)
                                        label = 2
                                    }
                                    break
                                }
                                kkVal++
                            }
                        }
                    }
                    30 -> {
                        if (DETAIL < 3) rSpeak(15)
                        DETAIL++
                        WZDARK = false
                        ABB[LOC] = 0
                        label = 2
                    }
                    40 -> {
                        Kbuffer = 58
                        if (outsid(LOC) && LOC != 8) Kbuffer = 57
                        rSpeak(Kbuffer)
                        label = 2
                    }
                    50 -> {
                        SPK = 12
                        if (Kbuffer in 43..50) SPK = 52
                        if (Kbuffer == 29 || Kbuffer == 30) SPK = 52
                        if (Kbuffer == 7 || Kbuffer == 36 || Kbuffer == 37) SPK = 10
                        if (Kbuffer == 11 || Kbuffer == 19) SPK = 11
                        if (VERB == FIND || VERB == INVENT) SPK = 59
                        if (Kbuffer == 62 || Kbuffer == 65) SPK = 42
                        if (Kbuffer == 17) SPK = 80
                        rSpeak(SPK)
                        label = 2
                    }
                    90 -> {
                        rSpeak(23)
                        OLDLC2 = LOC
                        label = 99
                    }
                    99 -> {
                        if (CLOSNG) {
                            rSpeak(131)
                            NUMDIE++
                            score(0)
                            return
                        }
                        NUMDIE++
                        if (!yes(79 + NUMDIE * 2, 80 + NUMDIE * 2, 54) || NUMDIE == MAXDIE) {
                            score(0)
                            return
                        }
                        PLACE[WATER] = 0
                        PLACE[OIL] = 0
                        if (toting(LAMP)) PROP[LAMP] = 0
                        for (idx in 1..100) {
                            val iVal = 101 - idx
                            if (toting(iVal)) {
                                Kbuffer = if (iVal == LAMP) 1 else OLDLC2
                                drop(iVal, Kbuffer)
                            }
                        }
                        LOC = 3
                        OLDLOC = LOC
                        label = 2000
                    }
                    40000 -> {
                        label = when (HINT - 1) {
                            0 -> 40100
                            1 -> 40200
                            2 -> 40300
                            3 -> 40400
                            4 -> 40500
                            5 -> 40600
                            6 -> 40700
                            7 -> 40800
                            8 -> 40900
                            9 -> 41000
                            else -> {
                                bug(27)
                                2602
                            }
                        }
                    }
                    40010 -> {
                        HINTLC[HINT] = 0
                        if (!yes(HINTS[HINT][3], 0, 54)) {
                            label = 2602
                            continue
                        }
                        setPrm(1, HINTS[HINT][2], HINTS[HINT][2])
                        rSpeak(261)
                        HINTED[HINT] = yes(175, HINTS[HINT][4], 54)
                        if (HINTED[HINT] && LIMIT > 30) {
                            LIMIT += 30 * HINTS[HINT][2]
                        }
                        HINTLC[HINT] = 0
                        label = 2602
                    }
                    40020 -> {
                        HINTLC[HINT] = 0
                        label = 2602
                    }
                    40030 -> {
                        label = 2602
                    }
                    2602 -> {
                        // Hint check loop mapping
                        label = 2600
                    }
                    40100 -> {
                        if (PROP[GRATE] == 0 && !here(KEYS)) {
                            label = 40010
                        } else {
                            label = 40020
                        }
                    }
                    40200 -> {
                        if (PLACE[BIRD] == LOC && toting(ROD) && OLDOBJ == BIRD) {
                            label = 40010
                        } else {
                            label = 40030
                        }
                    }
                    40300 -> {
                        if (here(SNAKE) && !here(BIRD)) {
                            label = 40010
                        } else {
                            label = 40020
                        }
                    }
                    40400 -> {
                        if (ATLOC[LOC] == 0 && ATLOC[OLDLOC] == 0 && ATLOC[OLDLC2] == 0 && HOLDNG > 1) {
                            label = 40010
                        } else {
                            label = 40020
                        }
                    }
                    40500 -> {
                        if (PROP[EMRALD] != -1 && PROP[PYRAM] == -1) {
                            label = 40010
                        } else {
                            label = 40020
                        }
                    }
                    40600 -> {
                        label = 40010
                    }
                    40700 -> {
                        if (DFLAG == 0) {
                            label = 40010
                        } else {
                            label = 40020
                        }
                    }
                    40800 -> {
                        if (ATLOC[LOC] == 0 && ATLOC[OLDLOC] == 0 && ATLOC[OLDLC2] == 0) {
                            label = 40010
                        } else {
                            label = 40030
                        }
                    }
                    40900 -> {
                        val dwrfIdx = atDwrf(LOC)
                        if (dwrfIdx < 0) {
                            label = 40020
                        } else if (here(OGRE) && dwrfIdx == 0) {
                            label = 40010
                        } else {
                            label = 40030
                        }
                    }
                    41000 -> {
                        if (TALLY == 1 && PROP[JADE] < 0) {
                            label = 40010
                        } else {
                            label = 40020
                        }
                    }
                    10000 -> {
                        PROP[GRATE] = 0
                        PROP[FISSUR] = 0
                        for (idx in 1..6) {
                            DSEEN[idx] = 0
                            DLOC[idx] = 0
                        }
                        move(TROLL, 0)
                        move(TROLL + 100, 0)
                        move(TROLL2, plac[TROLL])
                        move(TROLL2 + 100, fixd[TROLL])
                        juggle(CHASM)
                        if (PROP[BEAR] != 3) dstroy(BEAR)
                        PROP[CHAIN] = 0
                        FIXED[CHAIN] = 0
                        PROP[AXE] = 0
                        FIXED[AXE] = 0
                        rSpeak(129)
                        CLOCK1 = -1
                        CLOSNG = true
                        label = 2607
                    }
                    11000 -> {
                        PROP[BOTTLE] = put(BOTTLE, 115, 1)
                        PROP[PLANT] = put(PLANT, 115, 0)
                        PROP[OYSTER] = put(OYSTER, 115, 0)
                        OBJTXT[OYSTER] = 3
                        PROP[LAMP] = put(LAMP, 115, 0)
                        PROP[ROD] = put(ROD, 115, 0)
                        PROP[DWARF] = put(DWARF, 115, 0)
                        LOC = 115
                        OLDLOC = 115
                        NEWLOC = 115

                        put(GRATE, 116, 0)
                        put(SIGN, 116, 0)
                        OBJTXT[SIGN]++
                        PROP[SNAKE] = put(SNAKE, 116, 1)
                        PROP[BIRD] = put(BIRD, 116, 1)
                        PROP[CAGE] = put(CAGE, 116, 0)
                        PROP[ROD2] = put(ROD2, 116, 0)
                        PROP[PILLOW] = put(PILLOW, 116, 0)

                        PROP[MIRROR] = put(MIRROR, 115, 0)
                        FIXED[MIRROR] = 116

                        for (idx in 1..100) {
                            if (toting(idx)) dstroy(idx)
                        }

                        rSpeak(132)
                        CLOSED = true
                        label = 2
                    }
                    12000 -> {
                        rSpeak(188)
                        PROP[BATTER] = 1
                        if (toting(BATTER)) drop(BATTER, LOC)
                        LIMIT += 2500
                        LMWARN = false
                        label = 2607
                    }
                    12200 -> {
                        if (LMWARN || !here(LAMP)) {
                            label = 2607
                        } else {
                            LMWARN = true
                            SPK = 187
                            if (PLACE[BATTER] == 0) SPK = 183
                            if (PROP[BATTER] == 1) SPK = 189
                            rSpeak(SPK)
                            label = 2607
                        }
                    }
                    12400 -> {
                        LIMIT = -1
                        PROP[LAMP] = 0
                        if (here(LAMP)) rSpeak(184)
                        label = 2607
                    }
                    18999 -> {
                        rSpeak(SPK)
                        label = 19000
                    }
                    19000 -> {
                        rSpeak(136)
                        score(0)
                        return
                    }
                }
            } catch (e: InterruptedException) {
                GcLog.d("colossalcaveadventurekotlin.Adventure loop thread interrupted/suspended")
                return
            }
        }
    }

    private fun routeActionDest(res: Int): Int {
        return when (res) {
            2 -> 2
            8 -> 8
            2000 -> 2000
            2009 -> 2009
            2010 -> 2010
            2011 -> 2011
            2012 -> 2012
            2600 -> 2600
            2607 -> 2607
            2630 -> 19999 // L2630 is mapped directly to vocwrd verification in L19999 in our state machine
            2800 -> 2800
            8000 -> 8000
            18999 -> 18999
            19000 -> 19000
            else -> 2600
        }
    }

    private suspend fun action(startAt: Int): Int {
        var label = startAt
        while (true) {
            when (label) {
                4000 -> {
                    VERB = Kbuffer
                    SPK = ACTSPK[VERB]
                    if (WD2 > 0 && VERB != SAY) return 2800
                    if (VERB == SAY) OBJ = WD2
                    if (OBJ > 0) {
                        label = 4090
                        continue
                    }
                    // Intransitive verb mapping
                    val verbIdx = VERB - 1
                    if (verbIdx in 0..33) {
                        label = intransitiveVerbMap[verbIdx]
                        continue
                    }
                    bug(23)
                    return 8000
                }
                4090 -> {
                    // Transitive verb mapping
                    val verbIdx = VERB - 1
                    if (verbIdx in 0..33) {
                        label = transitiveVerbMap[verbIdx]
                        continue
                    }
                    bug(24)
                    return 2011
                }
                5000 -> {
                    OBJ = Kbuffer
                    if (!here(Kbuffer)) {
                        label = 5100
                        continue
                    }
                    if (WD2 > 0) return 2800
                    if (VERB != 0) {
                        label = 4090
                        continue
                    }
                    setPrm(1, WD1, WD1X)
                    rSpeak(255)
                    return 2600
                }
                5100 -> {
                    if (Kbuffer == GRATE) {
                        if (LOC == 1 || LOC == 4 || LOC == 7) Kbuffer = DPRSSN
                        if (LOC in 10..14) Kbuffer = ENTRNC
                        if (Kbuffer != GRATE) return 8
                    }
                    if (Kbuffer == DWARF && atDwrf(LOC) > 0) {
                        label = 5010
                        continue
                    }
                    if ((liq(0) == Kbuffer && here(BOTTLE)) || Kbuffer == liqLoc(LOC)) {
                        label = 5010
                        continue
                    }
                    if (OBJ == OIL && here(URN) && PROP[URN] != 0) {
                        OBJ = URN
                        label = 5010
                        continue
                    }
                    if (OBJ == PLANT && at(PLANT2) && PROP[PLANT2] != 0) {
                        OBJ = PLANT2
                        label = 5010
                        continue
                    }
                    if (OBJ == KNIFE && KNFLOC == LOC) {
                        KNFLOC = -1
                        SPK = 116
                        return 2011
                    }
                    if (OBJ == ROD && here(ROD2)) {
                        OBJ = ROD2
                        label = 5010
                        continue
                    }
                    if ((VERB == FIND || VERB == INVENT) && WD2 <= 0) {
                        label = 5010
                        continue
                    }
                    setPrm(1, WD1, WD1X)
                    rSpeak(256)
                    return 2012
                }
                5010 -> {
                    if (WD2 > 0) return 2800
                    if (VERB != 0) {
                        label = 4090
                        continue
                    }
                    setPrm(1, WD1, WD1X)
                    rSpeak(255)
                    return 2600
                }
                8010 -> {
                    if (ATLOC[LOC] == 0 || LINK[ATLOC[LOC]] != 0 || atDwrf(LOC) > 0) return 8000
                    OBJ = ATLOC[LOC]
                    label = 9010
                }
                9010 -> return carry()
                9020 -> return discard(0)
                9030 -> {
                    setPrm(1, WD2, WD2X)
                    if (WD2 <= 0) setPrm(1, WD1, WD1X)
                    if (WD2 > 0) WD1 = WD2
                    I = vocab(WD1, -1)
                    if (I == 62 || I == 65 || I == 71 || I == 2025 || I == 2034) {
                        WD2 = 0
                        OBJ = 0
                        return 2630
                    }
                    rSpeak(258)
                    return 2012
                }
                8040 -> {
                    SPK = 28
                    if (here(CLAM)) OBJ = CLAM
                    if (here(OYSTER)) OBJ = OYSTER
                    if (at(DOOR)) OBJ = DOOR
                    if (at(GRATE)) OBJ = GRATE
                    if (OBJ != 0 && here(CHAIN)) return 8000
                    if (here(CHAIN)) OBJ = CHAIN
                    if (OBJ == 0) return 2011
                    label = 9040
                }
                9040 -> {
                    if (OBJ == CLAM || OBJ == OYSTER) {
                        var kVal = if (OBJ == OYSTER) 1 else 0
                        SPK = 124 + kVal
                        if (toting(OBJ)) SPK = 120 + kVal
                        if (!toting(TRIDNT)) SPK = 122 + kVal
                        if (VERB == LOCK) SPK = 61
                        if (SPK != 124) return 2011
                        dstroy(CLAM)
                        drop(OYSTER, LOC)
                        drop(PEARL, 105)
                        return 2011
                    }
                    if (OBJ == DOOR) SPK = 111
                    if (OBJ == DOOR && PROP[DOOR] == 1) SPK = 54
                    if (OBJ == CAGE) SPK = 32
                    if (OBJ == KEYS) SPK = 55
                    if (OBJ == GRATE || OBJ == CHAIN) SPK = 31
                    if (SPK != 31 || !here(KEYS)) return 2011
                    if (OBJ == CHAIN) {
                        if (VERB == LOCK) {
                            SPK = 172
                            if (PROP[CHAIN] != 0) SPK = 34
                            if (LOC != PLAC[CHAIN]) SPK = 173
                            if (SPK != 172) return 2011
                            PROP[CHAIN] = 2
                            if (toting(CHAIN)) drop(CHAIN, LOC)
                            FIXED[CHAIN] = -1
                            return 2011
                        }
                        SPK = 171
                        if (PROP[BEAR] == 0) SPK = 41
                        if (PROP[CHAIN] == 0) SPK = 37
                        if (SPK != 171) return 2011
                        PROP[CHAIN] = 0
                        FIXED[CHAIN] = 0
                        if (PROP[BEAR] != 3) PROP[BEAR] = 2
                        FIXED[BEAR] = 2 - PROP[BEAR]
                        return 2011
                    }
                    if (!CLOSNG) {
                        Kbuffer = 34 + PROP[GRATE]
                        PROP[GRATE] = 1
                        if (VERB == LOCK) PROP[GRATE] = 0
                        Kbuffer += 2 * PROP[GRATE]
                        return 2010
                    }
                    Kbuffer = 130
                    if (!PANIC) CLOCK2 = 15
                    PANIC = true
                    return 2010
                }
                8070 -> {
                    if (here(LAMP) && PROP[LAMP] == 0 && LIMIT >= 0) OBJ = LAMP
                    if (here(URN) && PROP[URN] == 1) OBJ = OBJ * 100 + URN
                    if (OBJ == 0 || OBJ > 100) return 8000
                    label = 9070
                }
                9070 -> {
                    if (OBJ == URN) {
                        SPK = 38
                        if (PROP[URN] == 0) return 2011
                        SPK = 209
                        PROP[URN] = 2
                        return 2011
                    }
                    if (OBJ != LAMP) return 2011
                    SPK = 184
                    if (LIMIT < 0) return 2011
                    PROP[LAMP] = 1
                    rSpeak(39)
                    if (WZDARK) return 2000
                    return 2012
                }
                8080 -> {
                    if (here(LAMP) && PROP[LAMP] == 1) OBJ = LAMP
                    if (here(URN) && PROP[URN] == 2) OBJ = OBJ * 100 + URN
                    if (OBJ == 0 || OBJ > 100) return 8000
                    label = 9080
                }
                9080 -> {
                    if (OBJ == URN) {
                        PROP[URN] /= 2
                        SPK = 210
                        return 2011
                    }
                    if (OBJ == LAMP) {
                        PROP[LAMP] = 0
                        rSpeak(40)
                        if (dark(0)) rSpeak(16)
                        return 2012
                    }
                    if (OBJ == DRAGON || OBJ == VOLCAN) SPK = 146
                    return 2011
                }
                9090 -> {
                    if (!toting(OBJ) && (OBJ != ROD || !toting(ROD2))) SPK = 29
                    if (OBJ != ROD || !toting(OBJ) || (!here(BIRD) && (CLOSNG || !at(FISSUR)))) {
                        return 2011
                    }
                    if (here(BIRD)) SPK = 206 + (PROP[BIRD] % 2)
                    if (SPK == 206 && LOC == PLACE[STEPS] && PROP[JADE] < 0) {
                        drop(JADE, LOC)
                        PROP[JADE] = 0
                        TALLY--
                        SPK = 208
                        return 2011
                    }
                    if (CLOSED) return 18999
                    if (CLOSNG || !at(FISSUR)) return 2011
                    if (here(BIRD)) rSpeak(SPK)
                    PROP[FISSUR] = 1 - PROP[FISSUR]
                    pSpeak(FISSUR, 2 - PROP[FISSUR])
                    return 2012
                }
                9120 -> return attack()
                9130 -> {
                    if (OBJ == BOTTLE || OBJ == 0) OBJ = liq(0)
                    if (OBJ == 0) return 8000
                    if (!toting(OBJ)) return 2011
                    SPK = 78
                    if (OBJ != OIL && OBJ != WATER) return 2011
                    if (here(URN) && PROP[URN] == 0) {
                        OBJ = URN
                        label = 9220
                        continue
                    }
                    PROP[BOTTLE] = 1
                    PLACE[OBJ] = 0
                    SPK = 77
                    if (!(at(PLANT) || at(DOOR))) return 2011
                    if (at(DOOR)) {
                        PROP[DOOR] = 0
                        if (OBJ == OIL) PROP[DOOR] = 1
                        SPK = 113 + PROP[DOOR]
                        return 2011
                    }
                    SPK = 112
                    if (OBJ != WATER) return 2011
                    pSpeak(PLANT, PROP[PLANT] + 3)
                    PROP[PLANT] = (PROP[PLANT] + 1) % 3
                    PROP[PLANT2] = PROP[PLANT]
                    Kbuffer = NUL
                    return 8
                }
                8140 -> {
                    if (!here(FOOD)) return 8000
                    dstroy(FOOD)
                    SPK = 72
                    return 2011
                }
                9140 -> {
                    if (OBJ == FOOD) {
                        dstroy(FOOD)
                        SPK = 72
                        return 2011
                    }
                    if (OBJ == BIRD || OBJ == SNAKE || OBJ == CLAM || OBJ == OYSTER ||
                        OBJ == DWARF || OBJ == DRAGON || OBJ == TROLL || OBJ == BEAR ||
                        OBJ == OGRE
                    ) {
                        SPK = 71
                    }
                    return 2011
                }
                9150 -> {
                    if (OBJ == 0 && liqLoc(LOC) != WATER && (liq(0) != WATER || !here(BOTTLE))) {
                        return 8000
                    }
                    if (OBJ == BLOOD) {
                        dstroy(BLOOD)
                        PROP[DRAGON] = 2
                        OBJSND[BIRD] += 3
                        SPK = 240
                        return 2011
                    }
                    if (OBJ != 0 && OBJ != WATER) SPK = 110
                    if (SPK == 110 || liq(0) != WATER || !here(BOTTLE)) return 2011
                    PROP[BOTTLE] = 1
                    PLACE[WATER] = 0
                    SPK = 74
                    return 2011
                }
                9160 -> {
                    if (OBJ != LAMP) SPK = 76
                    if (OBJ != URN || PROP[URN] != 2) return 2011
                    dstroy(URN)
                    drop(AMBER, LOC)
                    PROP[AMBER] = 1
                    TALLY--
                    drop(CAVITY, LOC)
                    SPK = 216
                    return 2011
                }
                9170 -> return throwObj()
                8180 -> {
                    if (yes(22, 54, 54)) score(1)
                    return 2012
                }
                9190 -> {
                    if (at(OBJ) || (liq(0) == OBJ && at(BOTTLE)) || Kbuffer == liqLoc(LOC) ||
                        (OBJ == DWARF && atDwrf(LOC) > 0)
                    ) {
                        SPK = 94
                    }
                    if (CLOSED) SPK = 138
                    if (toting(OBJ)) SPK = 24
                    return 2011
                }
                8200 -> {
                    SPK = 98
                    for (idx in 1..100) {
                        if (idx == BEAR || !toting(idx)) continue
                        if (SPK == 98) rSpeak(99)
                        BLKLIN = false
                        pSpeak(idx, -1)
                        BLKLIN = true
                        SPK = 0
                    }
                    if (toting(BEAR)) SPK = 141
                    return 2011
                }
                9210 -> return feed()
                9220 -> return fill()
                9230 -> {
                    if (PROP[ROD2] < 0 || !CLOSED) return 2011
                    BONUS = 133
                    if (LOC == 115) BONUS = 134
                    if (here(ROD2)) BONUS = 135
                    rSpeak(BONUS)
                    score(0)
                    return 2012
                }
                8240 -> {
                    score(-1)
                    setPrm(1, SCORE, MXSCOR)
                    setPrm(3, TURNS, TURNS)
                    rSpeak(259)
                    return 2012
                }
                8250 -> {
                    Kbuffer = vocab(WD1, 3)
                    SPK = 42
                    if (FOOBAR == 1 - Kbuffer) {
                        FOOBAR = Kbuffer
                        if (Kbuffer != 4) return 2009
                        FOOBAR = 0
                        if (PLACE[EGGS] == plac[EGGS] || (toting(EGGS) && LOC == plac[EGGS])) {
                            return 2011
                        }
                        if (PLACE[EGGS] == 0 && PLACE[TROLL] == 0 && PROP[TROLL] == 0) {
                            PROP[TROLL] = 1
                        }
                        var kVal = 2
                        if (here(EGGS)) kVal = 1
                        if (LOC == plac[EGGS]) kVal = 0
                        move(EGGS, plac[EGGS])
                        pSpeak(EGGS, kVal)
                        return 2012
                    }
                    if (FOOBAR != 0) SPK = 151
                    return 2011
                }
                8260 -> {
                    SPK = 156
                    ABBNUM = 10000
                    DETAIL = 3
                    return 2011
                }
                8270 -> {
                    for (idx in 1..100) {
                        if (here(idx) && OBJTXT[idx] != 0 && PROP[idx] >= 0) {
                            OBJ = OBJ * 100 + idx
                        }
                    }
                    if (OBJ > 100 || OBJ == 0 || dark(0)) return 8000
                    label = 9270
                }
                9270 -> {
                    if (dark(0)) return 5190
                    if (OBJTXT[OBJ] == 0 || PROP[OBJ] < 0) return 2011
                    if (OBJ == OYSTER && !CLSHNT) {
                        CLSHNT = yes(192, 193, 54)
                        return 2012
                    }
                    pSpeak(OBJ, OBJTXT[OBJ] + PROP[OBJ])
                    return 2012
                }
                9280 -> {
                    if (OBJ == MIRROR) SPK = 148
                    if (OBJ == VASE && PROP[VASE] == 0) {
                        SPK = 198
                        if (toting(VASE)) drop(VASE, LOC)
                        PROP[VASE] = 2
                        FIXED[VASE] = -1
                        return 2011
                    }
                    if (OBJ != MIRROR || !CLOSED) return 2011
                    SPK = 197
                    return 18999
                }
                9290 -> {
                    if (OBJ != DWARF || !CLOSED) return 2011
                    SPK = 199
                    return 18999
                }
                8300 -> {
                    SPK = 201
                    rSpeak(260)
                    if (!yes(200, 54, 54)) return 2012
                    SAVED += 5
                    KK = -1
                    label = 8305
                }
                8305 -> {
                    val dArr = IntArray(1)
                    val tArr = IntArray(1)
                    datime(dArr, tArr)
                    Kbuffer = dArr[0] + 650 * tArr[0]
                    Kbuffer = saveWrd(KK, Kbuffer)
                    Kbuffer = VRSION
                    Kbuffer = saveWrd(0, Kbuffer)
                    if (Kbuffer != VRSION) {
                        setPrm(1, Kbuffer / 10, Kbuffer % 10)
                        setPrm(3, VRSION / 10, VRSION % 10)
                        rSpeak(269)
                        return 2000
                    }
                    // SAVWDS variables
                    saveWds(KK)
                    saveArr(ABB, LOCSIZ)
                    saveArr(ATLOC, LOCSIZ)
                    saveArr(DLOC, 6)
                    saveArr(DSEEN, 6)
                    saveArr(FIXED, 100)
                    saveArr(HINTED_IntArray(), HNTSIZ)
                    saveArr(HINTLC, HNTSIZ)
                    saveArr(LINK, 200)
                    saveArr(ODLOC, 6)
                    saveArr(PLACE, 100)
                    saveArr(PROP, 100)
                    Kbuffer = saveWrd(KK, Kbuffer)
                    if (Kbuffer != 0) {
                        rSpeak(270)
                        myExit(0)
                        return 2000
                    }
                    Kbuffer = NUL
                    ZZWORD = rndVoc(3, ZZWORD - MESH * 2) + MESH * 2
                    if (KK > 0) return 8
                    rSpeak(266)
                    miscBackupSavedFile()
                    myExit(0)
                    return 2000
                }
                8310 -> {
                    KK = 1
                    if (LOC == 1 && ABB[1] == 1) {
                        label = 8305
                        continue
                    }
                    rSpeak(268)
                    if (!yes(200, 54, 54)) return 2012
                    label = 8305
                }
                8320 -> {
                    if (PROP[RUG] != 2) SPK = 224
                    if (!here(RUG)) SPK = 225
                    if (SPK / 2 == 112) return 2011
                    OBJ = RUG
                    label = 9320
                }
                9320 -> {
                    if (OBJ != RUG) return 2011
                    SPK = 223
                    if (PROP[RUG] != 2) return 2011
                    OLDLC2 = OLDLOC
                    OLDLOC = LOC
                    NEWLOC = PLACE[RUG] + FIXED[RUG] - LOC
                    SPK = 226
                    if (PROP[SAPPH] >= 0) SPK = 227
                    rSpeak(SPK)
                    return 2
                }
                8330 -> {
                    SPK = 228
                    Kbuffer = LOCSND[LOC]
                    if (Kbuffer != 0) {
                        rSpeak(abs(Kbuffer))
                        if (Kbuffer < 0) return 2012
                        SPK = 0
                    }
                    setPrm(1, ZZWORD - MESH * 2, 0)
                    for (idx in 1..100) {
                        if (here(idx) && OBJSND[idx] != 0 && PROP[idx] >= 0) {
                            pSpeak(idx, OBJSND[idx] + PROP[idx])
                            SPK = 0
                            if (idx == BIRD && OBJSND[idx] + PROP[idx] == 8) {
                                dstroy(BIRD)
                            }
                        }
                    }
                    return 2011
                }
                8340 -> {
                    if (!at(RESER) && LOC != FIXED[RESER] - 1) return 2011
                    pSpeak(RESER, PROP[RESER] + 1)
                    PROP[RESER] = 1 - PROP[RESER]
                    if (at(RESER)) return 2012
                    OLDLC2 = LOC
                    NEWLOC = 0
                    rSpeak(241)
                    return 2
                }
            }
        }
    }

    private fun HINTED_IntArray(): IntArray {
        val arr = IntArray(HNTSIZ + 1)
        for (i in 0..HNTSIZ) {
            arr[i] = if (HINTED[i]) 1 else 0
        }
        return arr
    }

    private suspend fun carry(): Int {
        if (toting(OBJ)) return 2011
        SPK = 25
        if (OBJ == PLANT && PROP[PLANT] <= 0) SPK = 115
        if (OBJ == BEAR && PROP[BEAR] == 1) SPK = 169
        if (OBJ == CHAIN && PROP[BEAR] != 0) SPK = 170
        if (OBJ == URN) SPK = 215
        if (OBJ == CAVITY) SPK = 217
        if (OBJ == BLOOD) SPK = 239
        if (OBJ == RUG && PROP[RUG] == 2) SPK = 222
        if (OBJ == SIGN) SPK = 196
        if (OBJ == MESSAG) {
            SPK = 190
            dstroy(MESSAG)
        }
        if (FIXED[OBJ] != 0) return 2011
        if (OBJ == WATER || OBJ == OIL) {
            Kbuffer = OBJ
            OBJ = BOTTLE
            if (here(BOTTLE) && liq(0) == Kbuffer) {
                // skip to L9017 in C
            } else {
                if (toting(BOTTLE) && PROP[BOTTLE] == 1) return fill()
                SPK = if (PROP[BOTTLE] != 1) 105 else 104
                return 2011
            }
        }
        SPK = 92
        if (HOLDNG >= 7) return 2011
        if (OBJ == BIRD && PROP[BIRD] != 1 && -1 - PROP[BIRD] != 1) {
            if (PROP[BIRD] == 2) {
                SPK = 238
                dstroy(BIRD)
                return 2011
            }
            if (!toting(CAGE)) SPK = 27
            if (toting(ROD)) SPK = 26
            if (SPK / 2 == 13) return 2011
            PROP[BIRD] = 1
        }
        if ((OBJ == BIRD || OBJ == CAGE) && (PROP[BIRD] == 1 || -1 - PROP[BIRD] == 1)) {
            carry(BIRD + CAGE - OBJ, LOC)
        }
        carry(OBJ, LOC)
        Kbuffer = liq(0)
        if (OBJ == BOTTLE && Kbuffer != 0) PLACE[Kbuffer] = -1
        if (!gstone(OBJ) || PROP[OBJ] == 0) return 2009
        PROP[OBJ] = 0
        PROP[CAVITY] = 1
        return 2009
    }

    private suspend fun discard(justDoIt: Int): Int {
        if (justDoIt != 0) {
            Kbuffer = liq(0)
            if (Kbuffer == OBJ) OBJ = BOTTLE
            if (OBJ == BOTTLE && Kbuffer != 0) PLACE[Kbuffer] = 0
            if (OBJ == CAGE && PROP[BIRD] == 1) drop(BIRD, LOC)
            drop(OBJ, LOC)
            if (OBJ != BIRD) return 2012
            PROP[BIRD] = 0
            if (forest(LOC)) PROP[BIRD] = 2
            return 2012
        }
        if (toting(ROD2) && OBJ == ROD && !toting(ROD)) OBJ = ROD2
        if (!toting(OBJ)) return 2011
        if (OBJ == BIRD && here(SNAKE)) {
            rSpeak(30)
            if (CLOSED) return 19000
            dstroy(SNAKE)
            PROP[SNAKE] = 1
            Kbuffer = liq(0)
            if (Kbuffer == OBJ) OBJ = BOTTLE
            if (OBJ == BOTTLE && Kbuffer != 0) PLACE[Kbuffer] = 0
            if (OBJ == CAGE && PROP[BIRD] == 1) drop(BIRD, LOC)
            drop(OBJ, LOC)
            if (OBJ != BIRD) return 2012
            PROP[BIRD] = 0
            if (forest(LOC)) PROP[BIRD] = 2
            return 2012
        }
        if (gstone(OBJ) && at(CAVITY) && PROP[CAVITY] != 0) {
            rSpeak(218)
            PROP[OBJ] = 1
            PROP[CAVITY] = 0
            if (here(RUG) && ((OBJ == EMRALD && PROP[RUG] != 2) || (OBJ == RUBY && PROP[RUG] == 2))) {
                SPK = 219
                if (toting(RUG)) SPK = 220
                if (OBJ == RUBY) SPK = 221
                rSpeak(SPK)
                if (SPK == 220) {
                    // fall to L9021 in C
                } else {
                    Kbuffer = 2 - PROP[RUG]
                    PROP[RUG] = Kbuffer
                    if (Kbuffer == 2) Kbuffer = PLAC[SAPPH]
                    move(RUG + 100, Kbuffer)
                }
            }
            Kbuffer = liq(0)
            if (Kbuffer == OBJ) OBJ = BOTTLE
            if (OBJ == BOTTLE && Kbuffer != 0) PLACE[Kbuffer] = 0
            if (OBJ == CAGE && PROP[BIRD] == 1) drop(BIRD, LOC)
            drop(OBJ, LOC)
            if (OBJ != BIRD) return 2012
            PROP[BIRD] = 0
            if (forest(LOC)) PROP[BIRD] = 2
            return 2012
        }
        if (OBJ == COINS && here(VEND)) {
            dstroy(COINS)
            drop(BATTER, LOC)
            pSpeak(BATTER, 0)
            return 2012
        }
        if (OBJ == BIRD && at(DRAGON) && PROP[DRAGON] == 0) {
            rSpeak(154)
            dstroy(BIRD)
            PROP[BIRD] = 0
            return 2012
        }
        if (OBJ == BEAR && at(TROLL)) {
            rSpeak(163)
            move(TROLL, 0)
            move(TROLL + 100, 0)
            move(TROLL2, plac[TROLL])
            move(TROLL2 + 100, fixd[TROLL])
            juggle(CHASM)
            PROP[TROLL] = 2
            Kbuffer = liq(0)
            if (Kbuffer == OBJ) OBJ = BOTTLE
            if (OBJ == BOTTLE && Kbuffer != 0) PLACE[Kbuffer] = 0
            if (OBJ == CAGE && PROP[BIRD] == 1) drop(BIRD, LOC)
            drop(OBJ, LOC)
            if (OBJ != BIRD) return 2012
            PROP[BIRD] = 0
            if (forest(LOC)) PROP[BIRD] = 2
            return 2012
        }
        if (OBJ == VASE && LOC != plac[PILLOW]) {
            PROP[VASE] = 2
            if (at(PILLOW)) PROP[VASE] = 0
            pSpeak(VASE, PROP[VASE] + 1)
            if (PROP[VASE] != 0) FIXED[VASE] = -1
            Kbuffer = liq(0)
            if (Kbuffer == OBJ) OBJ = BOTTLE
            if (OBJ == BOTTLE && Kbuffer != 0) PLACE[Kbuffer] = 0
            if (OBJ == CAGE && PROP[BIRD] == 1) drop(BIRD, LOC)
            drop(OBJ, LOC)
            if (OBJ != BIRD) return 2012
            PROP[BIRD] = 0
            if (forest(LOC)) PROP[BIRD] = 2
            return 2012
        }
        rSpeak(54)
        Kbuffer = liq(0)
        if (Kbuffer == OBJ) OBJ = BOTTLE
        if (OBJ == BOTTLE && Kbuffer != 0) PLACE[Kbuffer] = 0
        if (OBJ == CAGE && PROP[BIRD] == 1) drop(BIRD, LOC)
        drop(OBJ, LOC)
        if (OBJ != BIRD) return 2012
        PROP[BIRD] = 0
        if (forest(LOC)) PROP[BIRD] = 2
        return 2012
    }

    private suspend fun attack(): Int {
        I = atDwrf(LOC)
        if (OBJ == 0) {
            if (I > 0) OBJ = DWARF
            if (here(SNAKE)) OBJ = OBJ * 100 + SNAKE
            if (at(DRAGON) && PROP[DRAGON] == 0) OBJ = OBJ * 100 + DRAGON
            if (at(TROLL)) OBJ = OBJ * 100 + TROLL
            if (at(OGRE)) OBJ = OBJ * 100 + OGRE
            if (here(BEAR) && PROP[BEAR] == 0) OBJ = OBJ * 100 + BEAR
            if (OBJ > 100) return 8000
            if (OBJ == 0) {
                if (here(BIRD) && VERB != THROW) OBJ = BIRD
                if (here(VEND) && VERB != THROW) OBJ = OBJ * 100 + VEND
                if (here(CLAM) || here(OYSTER)) OBJ = 100 * OBJ + CLAM
                if (OBJ > 100) return 8000
            }
        }
        if (OBJ == BIRD) {
            SPK = 137
            if (CLOSED) return 2011
            dstroy(BIRD)
            PROP[BIRD] = 0
            SPK = 45
            return 2011
        }
        if (OBJ == VEND) {
            pSpeak(VEND, PROP[VEND] + 2)
            PROP[VEND] = 3 - PROP[VEND]
            return 2012
        }
        if (OBJ == 0) SPK = 44
        if (OBJ == CLAM || OBJ == OYSTER) SPK = 150
        if (OBJ == SNAKE) SPK = 46
        if (OBJ == DWARF) {
            SPK = 49
            if (CLOSED) return 19000
        }
        if (OBJ == DRAGON) SPK = 167
        if (OBJ == TROLL) SPK = 157
        if (OBJ == OGRE) {
            SPK = 203
            if (I > 0) {
                rSpeak(SPK)
                rSpeak(6)
                dstroy(OGRE)
                Kbuffer = 0
                for (idx in 1..5) {
                    if (DLOC[idx] == LOC) {
                        Kbuffer++
                        DLOC[idx] = 61
                        DSEEN[idx] = 0
                    }
                }
                SPK += 1 + 1 / Kbuffer
                return 2011
            }
        }
        if (OBJ == BEAR) SPK = 165 + (PROP[BEAR] + 1) / 2
        if (OBJ != DRAGON || PROP[DRAGON] != 0) return 2011

        rSpeak(49)
        VERB = 0
        OBJ = 0
        val word1 = IntArray(1)
        val word1x = IntArray(1)
        val word2 = IntArray(1)
        val word2x = IntArray(1)
        getIn(word1, word1x, word2, word2x)
        WD1 = word1[0]
        WD1X = word1x[0]
        WD2 = word2[0]
        WD2X = word2x[0]
        if (WD1 != makeWd(25L) && WD1 != makeWd(250519L)) return 2607
        pSpeak(DRAGON, 3)
        PROP[DRAGON] = 1
        PROP[RUG] = 0
        Kbuffer = (plac[DRAGON] + fixd[DRAGON]) / 2
        move(DRAGON + 100, -1)
        move(RUG + 100, 0)
        move(DRAGON, Kbuffer)
        move(RUG, Kbuffer)
        drop(BLOOD, Kbuffer)
        for (jIdx in 1..100) {
            if (PLACE[jIdx] == plac[DRAGON] || PLACE[jIdx] == fixd[DRAGON]) {
                move(jIdx, Kbuffer)
            }
        }
        LOC = Kbuffer
        Kbuffer = NUL
        return 8
    }

    private suspend fun throwObj(): Int {
        if (toting(ROD2) && OBJ == ROD && !toting(ROD)) OBJ = ROD2
        if (!toting(OBJ)) return 2011
        if (OBJ in 50..MAXTRS && at(TROLL)) {
            SPK = 159
            drop(OBJ, 0)
            move(TROLL, 0)
            move(TROLL + 100, 0)
            drop(TROLL2, plac[TROLL])
            drop(TROLL2 + 100, fixd[TROLL])
            juggle(CHASM)
            return 2011
        }
        if (OBJ == FOOD && here(BEAR)) {
            OBJ = BEAR
            return feed()
        }
        if (OBJ != AXE) return discard(0)
        I = atDwrf(LOC)
        if (I > 0) {
            SPK = 48
            if (ran(7) < DFLAG) {
                // fall to L9175
            } else {
                DSEEN[I] = 0
                DLOC[I] = 0
                SPK = 47
                DKILL++
                if (DKILL == 1) SPK = 149
            }
            rSpeak(SPK)
            drop(AXE, LOC)
            Kbuffer = NUL
            return 8
        }
        SPK = 152
        if (at(DRAGON) && PROP[DRAGON] == 0) {
            rSpeak(SPK)
            drop(AXE, LOC)
            Kbuffer = NUL
            return 8
        }
        SPK = 158
        if (at(TROLL)) {
            rSpeak(SPK)
            drop(AXE, LOC)
            Kbuffer = NUL
            return 8
        }
        SPK = 203
        if (at(OGRE)) {
            rSpeak(SPK)
            drop(AXE, LOC)
            Kbuffer = NUL
            return 8
        }
        if (here(BEAR) && PROP[BEAR] == 0) {
            SPK = 164
            drop(AXE, LOC)
            FIXED[AXE] = -1
            PROP[AXE] = 1
            juggle(BEAR)
            return 2011
        }
        OBJ = 0
        return attack()
    }

    private suspend fun feed(): Int {
        if (OBJ == BIRD) {
            SPK = 100
            return 2011
        }
        if (OBJ == SNAKE || OBJ == DRAGON || OBJ == TROLL) {
            SPK = 102
            if (OBJ == DRAGON && PROP[DRAGON] != 0) SPK = 110
            if (OBJ == TROLL) SPK = 182
            if (OBJ != SNAKE || CLOSED || !here(BIRD)) return 2011
            SPK = 101
            dstroy(BIRD)
            PROP[BIRD] = 0
            return 2011
        }
        if (OBJ == DWARF) {
            if (!here(FOOD)) return 2011
            SPK = 103
            DFLAG += 2
            return 2011
        }
        if (OBJ == BEAR) {
            if (PROP[BEAR] == 0) SPK = 102
            if (PROP[BEAR] == 3) SPK = 110
            if (!here(FOOD)) return 2011
            dstroy(FOOD)
            PROP[BEAR] = 1
            FIXED[AXE] = 0
            PROP[AXE] = 0
            SPK = 168
            return 2011
        }
        if (OBJ == OGRE) {
            if (here(FOOD)) SPK = 202
            return 2011
        }
        SPK = 14
        return 2011
    }

    private suspend fun fill(): Int {
        if (OBJ == VASE) {
            SPK = 29
            if (liqLoc(LOC) == 0) SPK = 144
            if (liqLoc(LOC) == 0 || !toting(VASE)) return 2011
            rSpeak(145)
            PROP[VASE] = 2
            FIXED[VASE] = -1
            return discard(1)
        }
        if (OBJ == URN) {
            SPK = 213
            if (PROP[URN] != 0) return 2011
            SPK = 144
            Kbuffer = liq(0)
            if (Kbuffer == 0 || !here(BOTTLE)) return 2011
            PLACE[Kbuffer] = 0
            PROP[BOTTLE] = 1
            if (Kbuffer == OIL) PROP[URN] = 1
            SPK = 211 + PROP[URN]
            return 2011
        }
        if (OBJ != 0 && OBJ != BOTTLE) return 2011
        if (OBJ == 0 && !here(BOTTLE)) return 8000
        SPK = 107
        if (liqLoc(LOC) == 0) SPK = 106
        if (here(URN) && PROP[URN] != 0) SPK = 214
        if (liq(0) != 0) SPK = 105
        if (SPK != 107) return 2011
        PROP[BOTTLE] = (COND[LOC] % 4) / 2 * 2
        Kbuffer = liq(0)
        if (toting(BOTTLE)) PLACE[Kbuffer] = -1
        if (Kbuffer == OIL) SPK = 108
        return 2011
    }

    private fun speak(n: Int) {
        if (n == 0) return
        var blank = BLKLIN
        var kVal = n
        var nparms = 1
        while (true) {
            val lVal = abs(LINES[kVal]) - 1
            kVal++
            lnleng = 0
            lnposn = 1
            var stateVal = intArrayOf(0)
            for (iVal in kVal..lVal) {
                putTxt(LINES[iVal], stateVal, 2, iVal)
            }
            lnposn = 0
            while (true) {
                lnposn++
                if (lnposn > lnleng) break
                if (inlineArr[lnposn] != 63) continue // 63 is '%'
                val prmtyp = inlineArr[lnposn + 1]
                if (prmtyp == 1) return
                if (prmtyp == 29) { // S
                    shfTxt(lnposn + 2, -1)
                    inlineArr[lnposn] = 55
                    if (PARMS[nparms] == 1) {
                        shfTxt(lnposn + 1, -1)
                    }
                    nparms++
                    continue
                }
                if (prmtyp == 30) { // T
                    shfTxt(lnposn + 2, -2)
                    var stateValT = intArrayOf(0)
                    var caseVal = 2
                    while (true) {
                        if (PARMS[nparms] < 0) {
                            nparms++
                            break
                        }
                        if (PARMS[nparms + 1] < 0) caseVal = 0
                        putTxt(PARMS[nparms], stateValT, caseVal, 0)
                        nparms++
                    }
                    continue
                }
                if (prmtyp == 12) { // B
                    val blankCount = PARMS[nparms]
                    shfTxt(lnposn + 2, blankCount - 2)
                    if (blankCount > 0) {
                        for (iIdx in 1..blankCount) {
                            inlineArr[lnposn] = 0
                            lnposn++
                        }
                    }
                    nparms++
                    continue
                }
                if (prmtyp == 33 || prmtyp == 22 || prmtyp == 31 || prmtyp == 13) { // W, L, U, C
                    shfTxt(lnposn + 2, -2)
                    var stateValW = intArrayOf(0)
                    var caseVal = -1
                    if (prmtyp == 31) caseVal = 1
                    if (prmtyp == 33) caseVal = 0
                    val startPos = lnposn
                    putTxt(PARMS[nparms], stateValW, caseVal, 0)
                    putTxt(PARMS[nparms + 1], stateValW, caseVal, 0)
                    if (prmtyp == 13 && inlineArr[startPos] in 37..62) {
                        inlineArr[startPos] -= 26
                    }
                    nparms += 2
                    continue
                }
                val digitCount = prmtyp - 64
                if (digitCount in 1..9) {
                    shfTxt(lnposn + 2, digitCount - 2)
                    lnposn += digitCount
                    var parmValue = abs(PARMS[nparms])
                    var negVal = 0
                    if (PARMS[nparms] < 0) negVal = 9
                    for (iIdx in 1..digitCount) {
                        lnposn--
                        inlineArr[lnposn] = (parmValue % 10) + 64
                        if (iIdx == 1 || parmValue != 0) {
                            // keep
                        } else {
                            inlineArr[lnposn] = negVal
                            negVal = 0
                        }
                        parmValue /= 10
                    }
                    lnposn += digitCount
                    nparms++
                }
            }
            if (blank) type0()
            blank = false
            type()
            kVal = lVal + 1
            if (LINES[kVal] < 0) break
        }
    }

    private fun pSpeak(msg: Int, skip: Int) {
        var mVal = PTEXT[msg]
        if (skip >= 0) {
            for (idx in 0..skip) {
                while (true) {
                    mVal = abs(LINES[mVal])
                    if (LINES[mVal] < 0) break
                }
            }
        }
        speak(mVal)
    }

    private fun rSpeak(i: Int) {
        if (i != 0) speak(RTEXT[i])
    }

    private fun setPrm(first: Int, p1: Int, p2: Int) {
        if (first >= 25) bug(29)
        PARMS[first] = p1
        PARMS[first + 1] = p2
    }

    private suspend fun getIn(word1: IntArray, word1x: IntArray, word2: IntArray, word2x: IntArray) {
        while (true) {
            if (BLKLIN) type0()
            mapLin(false)
            word1[0] = getTxt(true, true, true, 0)
            if (BLKLIN && word1[0] < 0) {
                continue
            }
            word1x[0] = getTxt(false, true, true, 0)
            while (true) {
                val junk = getTxt(false, true, true, 0)
                if (junk <= 0) break
            }
            word2[0] = getTxt(true, true, true, 0)
            if (word2[0] < 0) {
                word2x[0] = 0
                return
            }
            word2x[0] = getTxt(false, true, true, 0)
            while (true) {
                val junk = getTxt(false, true, true, 0)
                if (junk <= 0) break
            }
            return
        }
    }

    private suspend fun yes(x: Int, y: Int, z: Int): Boolean {
        while (true) {
            rSpeak(x)
            val reply = IntArray(1)
            val junk1 = IntArray(1)
            val junk2 = IntArray(1)
            val junk3 = IntArray(1)
            getIn(reply, junk1, junk2, junk3)
            val rep = reply[0]
            GcLog.d("yes() rep = $rep, expected yes = ${makeWd(250519L)}, expected y = ${makeWd(25L)}")
            if (rep == makeWd(250519L) || rep == makeWd(25L)) {
                rSpeak(y)
                return true
            }
            if (rep == makeWd(1415L) || rep == makeWd(14L)) {
                rSpeak(z)
                return false
            }
            rSpeak(185)
        }
    }

    private fun vocab(id: Int, init: Int): Int {
        var jVal = 10000
        for (iIdx in 1..TABSIZ) {
            if (KTAB[iIdx] == -1) {
                break
            }
            jVal += 7
            if (init >= 0 && (KTAB[iIdx] / 1000) != init) {
                continue
            }
            if (ATAB[iIdx] == id + jVal * jVal) {
                var v = KTAB[iIdx]
                if (init >= 0) {
                    v = v % 1000
                }
                return v
            }
        }
        if (init < 0) {
            return -1
        }
        bug(5)
        return -1
    }

    private fun dstroy(obj: Int) {
        move(obj, 0)
    }

    private fun juggle(obj: Int) {
        val iVal = PLACE[obj]
        val jVal = FIXED[obj]
        move(obj, iVal)
        move(obj + 100, jVal)
    }

    private fun move(obj: Int, where: Int) {
        val fromLoc = if (obj > 100) FIXED[obj - 100] else PLACE[obj]
        if (fromLoc > 0 && fromLoc <= LOCSIZ) {
            // Remove obj from fromLoc list
            val tempObj = obj
            var ptr = ATLOC[fromLoc]
            if (ptr == tempObj) {
                ATLOC[fromLoc] = LINK[tempObj]
            } else if (ptr != 0) {
                while (LINK[ptr] != tempObj && LINK[ptr] != 0) {
                    ptr = LINK[ptr]
                }
                if (LINK[ptr] == tempObj) {
                    LINK[ptr] = LINK[tempObj]
                }
            }
        }
        if (obj > 100) {
            FIXED[obj - 100] = where
        } else {
            PLACE[obj] = where
        }
        if (where > 0) {
            LINK[obj] = ATLOC[where]
            ATLOC[where] = obj
        }
    }

    private fun put(obj: Int, where: Int, pval: Int): Int {
        move(obj, where)
        PROP[obj] = pval
        return pval
    }

    private fun carry(obj: Int, where: Int) {
        if (obj <= 100) {
            if (PLACE[obj] == -1) return
            PLACE[obj] = -1
            HOLDNG++
        }
        // Remove from list
        val tempObj = obj
        var ptr = ATLOC[where]
        if (ptr == tempObj) {
            ATLOC[where] = LINK[tempObj]
        } else if (ptr != 0) {
            while (LINK[ptr] != tempObj && LINK[ptr] != 0) {
                ptr = LINK[ptr]
            }
            if (LINK[ptr] == tempObj) {
                LINK[ptr] = LINK[tempObj]
            }
        }
    }

    private fun drop(obj: Int, where: Int) {
        if (obj <= 100) {
            if (PLACE[obj] == -1) HOLDNG--
            PLACE[obj] = where
        }
        if (where > 0) {
            LINK[obj] = ATLOC[where]
            ATLOC[where] = obj
        }
    }

    private fun atDwrf(where: Int): Int {
        if (DFLAG < 2) return 0
        for (idx in 1..5) {
            if (DLOC[idx] == where) return idx
        }
        return 0
    }

    private fun setBit(bit: Int): Int {
        return 1 shl bit
    }

    private fun tstBit(mask: Int, bit: Int): Boolean {
        return (mask and (1 shl bit)) != 0
    }

    private var rSeed = 0L
    private var dSeed = 1L

    private fun ran(range: Int): Long {
        if (range < 0) {
            val now = System.currentTimeMillis()
            rSeed = (now % 1048576L)
            dSeed = 1000 + (now % 1000)
            return 0
        }
        if (rSeed == 0L) {
            val now = System.currentTimeMillis()
            rSeed = ((now + 5) % 1048576L)
            dSeed = 1000 + (now % 1000)
        }
        for (t in 1..dSeed) {
            rSeed = (rSeed * 1093L + 221587L) % 1048576L
        }
        return (range * rSeed) / 1048576L
    }

    private fun rndVoc(charVal: Int, force: Int): Int {
        var rndVocValue = force
        if (rndVocValue == 0) {
            for (i in 1..5) {
                var jVal = (11 + ran(26)).toInt()
                if (i == 2) jVal = charVal
                rndVocValue = rndVocValue * 64 + jVal
            }
        }
        var jVal2 = 10000
        val div = 64 * 64 * 64
        for (i in 1..TABSIZ) {
            jVal2 += 7
            val hashVal = ATAB[i] - jVal2 * jVal2
            if ((hashVal / div) % 64 == charVal) {
                ATAB[i] = rndVocValue + jVal2 * jVal2
                return rndVocValue
            }
        }
        bug(5)
        return rndVocValue
    }

    private fun bug(num: Int) {
        myPrintf(AppData.applicationContext?.getString(R.string.adventure_fatal_error, num) ?: "")
        myExit(0)
    }

    private suspend fun mapLin(fil: Boolean) {
        if (MAP2[1] == 0) mpInit()

        if (fil) {
            if (openedFile == null) {
                val file = File(backupDirectory, "adventure.text")
                if (!file.exists()) {
                    myPrintf(AppData.applicationContext?.getString(R.string.adventure_cant_read_text) ?: "")
                    myExit(0)
                    return
                }
                openedFile = BufferedReader(FileReader(file))
            }
            val line = openedFile?.readLine()
            if (line == null) {
                score(1)
                return
            }
            val trimmedLine = line.trimEnd()
            for (i in 0..255) {
                inlineArr[i] = 0
            }
            for (i in 0 until trimmedLine.length) {
                if (i < 100) {
                    inlineArr[i + 1] = trimmedLine[i].code
                }
            }
        } else {
            val line = inputQueue.take()
            val trimmedLine = line.trimEnd { it == '\n' || it == '\r' || it == ' ' }
            for (i in 0..255) {
                inlineArr[i] = 0
            }
            for (i in 0 until trimmedLine.length) {
                if (i < 80) {
                    inlineArr[i + 1] = trimmedLine[i].code
                }
            }
        }

        lnleng = 0
        for (i in 1..100) {
            if (inlineArr[i] == 0) break
            val charVal = inlineArr[i]
            val mapped = if ((charVal + 1) in 0..255) MAP1[charVal + 1] else -1
            inlineArr[i] = mapped
            if (mapped != 0) {
                lnleng = i
            }
        }
        lnposn = 1
    }

    private fun type() {
        if (lnleng == 0) {
            myPrintf("\n\n")
            return
        }
        if (MAP2[1] == 0) mpInit()
        val sb = StringBuilder()
        for (i in 1..lnleng) {
            val mappedVal = inlineArr[i]
            if ((mappedVal + 1) in 0..255) {
                val origChar = MAP2[mappedVal + 1].toChar()
                sb.append(origChar)
            }
        }
        myPrintf("%s\n", sb.toString())
    }

    private fun type0() {
        val temp = lnleng
        lnleng = 0
        type()
        lnleng = temp
    }

    private fun mpInit() {
        for (i in 0..255) {
            MAP1[i] = -1
        }
        val runs = arrayOf(
            intArrayOf(32, 34),
            intArrayOf(39, 46),
            intArrayOf(65, 90),
            intArrayOf(97, 122),
            intArrayOf(37, 37),
            intArrayOf(48, 57),
            intArrayOf(0, 126)
        )
        var valIdx = 0
        for (run in runs) {
            val first = run[0]
            val last = run[1]
            if (first <= last) {
                for (j in first..last) {
                    val idx = j + 1
                    if (idx in 0..255) {
                        if (MAP1[idx] >= 0) continue
                        MAP1[idx] = valIdx
                        valIdx++
                    }
                }
            }
        }
        MAP1[128] = MAP1[10]
        MAP1[10] = MAP1[33]
        MAP1[11] = MAP1[33]

        for (i in 0..126) {
            val idx = i + 1
            val mappedVal = MAP1[idx] + 1
            if (mappedVal in 0..255) {
                MAP2[mappedVal] = i * ('B' - 'A')
                if (i >= 64) {
                    MAP2[mappedVal] = (i - 64) * ('B' - 'A') + '@'.code
                }
            }
        }
    }

    private suspend fun getNum(k: Int): Int {
        if (k != 0) mapLin(k > 0)
        var getNumValue = 0
        while (true) {
            if (lnposn > lnleng) return getNumValue
            if (inlineArr[lnposn] != 0) break
            lnposn++
        }
        var sign = 1
        if (inlineArr[lnposn] == 9) {
            sign = -1
            lnposn++
        }
        while (true) {
            if (lnposn > lnleng || inlineArr[lnposn] == 0) {
                getNumValue *= sign
                lnposn++
                return getNumValue
            }
            val digit = inlineArr[lnposn] - 64
            if (digit < 0 || digit > 9) {
                getNumValue = 0
                getNumValue *= sign
                lnposn++
                return getNumValue
            }
            getNumValue = getNumValue * 10 + digit
            lnposn++
        }
    }

    private fun getTxt(skip: Boolean, oneWrd: Boolean, upper: Boolean, hash: Int): Int {
        if (lnposn != splitting) splitting = -1
        var getTxtValue = -1
        if (lnposn > lnleng) return getTxtValue

        var tempPos = lnposn
        if (skip) {
            while (tempPos <= lnleng && inlineArr[tempPos] == 0) {
                tempPos++
            }
        }
        lnposn = tempPos
        if (lnposn > lnleng) return -1

        getTxtValue = 0
        for (i in 1..5) {
            getTxtValue *= 64
            if (lnposn > lnleng || (oneWrd && inlineArr[lnposn] == 0)) {
                continue
            }
            var charVal = inlineArr[lnposn]
            if (charVal >= 63) {
                if (splitting == lnposn) {
                    getTxtValue += charVal - 63
                    splitting = -1
                    lnposn++
                } else {
                    getTxtValue += 63
                    splitting = lnposn
                }
            } else {
                splitting = -1
                if (upper && charVal >= 37) {
                    charVal -= 26
                }
                getTxtValue += charVal
                lnposn++
            }
        }

        if (hash != 0) {
            val hashPart = ((hash * 13579L + 5432L) % 97531L) * 12345L + hash
            getTxtValue = (getTxtValue + hashPart).toInt()
        }
        return getTxtValue
    }

    private fun makeWd(lettrs: Long): Int {
        var makeWdValue = 0
        var iVal = 1
        var lVal = lettrs
        while (true) {
            makeWdValue += (iVal * ((lVal % 50) + 10)).toInt()
            iVal *= 64
            if ((lVal % 100) > 50) {
                makeWdValue += iVal * 5
            }
            lVal /= 100
            if (lVal == 0L) break
        }
        val target = 64 * 64 * 64 * 64 * 64L / iVal
        makeWdValue = (makeWdValue * target).toInt()
        return makeWdValue
    }

    private fun putTxt(word: Int, state: IntArray, caseVal: Int, hash: Int) {
        val alph1 = 13 * caseVal + 24
        val alph2 = 26 * abs(caseVal) + alph1
        val checkRange = if (abs(caseVal) > 1) alph2 else alph1

        var div = 64 * 64 * 64 * 64
        var w = word
        if (hash != 0) {
            val hashPart = ((hash * 13579L + 5432L) % 97531L) * 12345L + hash
            w = (w - hashPart).toInt()
        }

        for (i in 1..5) {
            if (w <= 0 && state[0] == 0 && abs(caseVal) <= 1) return
            val byteVal = w / div
            if (state[0] != 0 || byteVal != 63) {
                shfTxt(lnposn, 1)
                state[0] += byteVal
                if (state[0] < alph2 && state[0] >= checkRange) {
                    state[0] -= 26 * caseVal
                }
                inlineArr[lnposn] = state[0]
                lnposn++
                state[0] = 0
            } else {
                state[0] = 63
            }
            w = (w - byteVal * div) * 64
        }
    }

    private fun shfTxt(from: Int, delta: Int) {
        if (lnleng < from || delta == 0) {
            lnleng += delta
            return
        }
        for (i in from..lnleng) {
            val ii = if (delta > 0) from + lnleng - i else i
            val jj = ii + delta
            if (jj in 0..255 && ii in 0..255) {
                inlineArr[jj] = inlineArr[ii]
            }
        }
        lnleng += delta
    }

    private suspend fun saveWrd(op: Int, word: Int): Int {
        val arr = IntArray(1)
        arr[0] = word
        fSAVWRD(op, arr)
        return arr[0]
    }

    private var savWrdState = 0
    private var savWrdBuf = IntArray(250)
    private var savWrdN = 0
    private var savWrdHash = 0L
    private var savWrdCkSum = 0L

    private suspend fun fSAVWRD(op: Int, wordWrapper: IntArray) {
        var word = wordWrapper[0]
        if (op != 0) {
            if (savWrdState != 0) {
                // finish writing/reading
            } else {
                savWrdState = op
                saveIo(0, savWrdState > 0, savWrdBuf)
                savWrdN = 1
                if (savWrdState > 0) {
                    // read first block
                    saveIo(1, true, savWrdBuf)
                    savWrdHash = (1234L * 5678L - savWrdBuf[0]) % 1048576L
                    savWrdCkSum = savWrdBuf[0].toLong()
                    return
                } else {
                    savWrdHash = (word % 1048576L).toLong()
                    savWrdBuf[0] = (1234L * 5678L - savWrdHash).toInt()
                    savWrdCkSum = savWrdBuf[0].toLong()
                    return
                }
            }
        }

        if (savWrdState == 0) return

        if (op == 0) { // read/write single word
            if (savWrdN == 250) {
                saveIo(1, savWrdState > 0, savWrdBuf)
            }
            savWrdN = (savWrdN % 250) + 1
            val h1 = (savWrdHash * 1093L + 221573L) % 1048576L
            savWrdHash = (h1 * 1093L + 221573L) % 1048576L
            val h1Enc = ((h1 % 1234) * 765432 + (savWrdHash % 123)).toInt()
            savWrdN--
            if (savWrdState > 0) {
                word = savWrdBuf[savWrdN] + h1Enc
            }
            savWrdBuf[savWrdN] = word - h1Enc
            savWrdN++
            savWrdCkSum = (savWrdCkSum * 13 + word) % 1000000000L
            wordWrapper[0] = word
            return
        }

        // op is final write/close
        if (savWrdN == 250) {
            saveIo(1, savWrdState > 0, savWrdBuf)
        }
        savWrdN = (savWrdN % 250) + 1
        if (savWrdState > 0) {
            savWrdN--
            word = (savWrdBuf[savWrdN] - savWrdCkSum).toInt()
            savWrdN++
            saveIo(-1, true, savWrdBuf)
            savWrdState = 0
            wordWrapper[0] = word
            return
        }
        savWrdN--
        savWrdBuf[savWrdN] = savWrdCkSum.toInt()
        savWrdN++
        saveIo(1, false, savWrdBuf)
        saveIo(-1, false, savWrdBuf)
        savWrdState = 0
    }

    private suspend fun saveWds(op: Int) {
        // port of SAVWDS in actions1.c
        // It saves or reads 7 variables.
        val wrapper = IntArray(1)
        suspend fun ioW(v: Int): Int {
            wrapper[0] = v
            fSAVWRD(0, wrapper)
            return wrapper[0]
        }
        suspend fun ioBool(v: Boolean): Boolean {
            wrapper[0] = if (v) 1 else 0
            fSAVWRD(0, wrapper)
            return wrapper[0] != 0
        }
        if (op > 0) {
            ABBNUM = ioW(ABBNUM)
            BLKLIN = ioBool(BLKLIN)
            BONUS = ioW(BONUS)
            CLOCK1 = ioW(CLOCK1)
            CLOCK2 = ioW(CLOCK2)
            CLOSED = ioBool(CLOSED)
            CLOSNG = ioBool(CLOSNG)

            DETAIL = ioW(DETAIL)
            DFLAG = ioW(DFLAG)
            DKILL = ioW(DKILL)
            DTOTAL = ioW(DTOTAL)
            FOOBAR = ioW(FOOBAR)
            HOLDNG = ioW(HOLDNG)
            IWEST = ioW(IWEST)

            KNFLOC = ioW(KNFLOC)
            LIMIT = ioW(LIMIT)
            LL = ioW(LL)
            LMWARN = ioBool(LMWARN)
            LOC = ioW(LOC)
            NEWLOC = ioW(NEWLOC)
            NUMDIE = ioW(NUMDIE)

            OBJ = ioW(OBJ)
            OLDLC2 = ioW(OLDLC2)
            OLDLOC = ioW(OLDLOC)
            OLDOBJ = ioW(OLDOBJ)
            PANIC = ioBool(PANIC)
            SAVED = ioW(SAVED)
            SETUP = ioW(SETUP)

            SPK = ioW(SPK)
            TALLY = ioW(TALLY)
            THRESH = ioW(THRESH)
            TRNDEX = ioW(TRNDEX)
            TRNLUZ = ioW(TRNLUZ)
            TURNS = ioW(TURNS)
            OBJTXT[OYSTER] = ioW(OBJTXT[OYSTER])

            VERB = ioW(VERB)
            WD1 = ioW(WD1)
            WD1X = ioW(WD1X)
            WD2 = ioW(WD2)
            WZDARK = ioBool(WZDARK)
            ZZWORD = ioW(ZZWORD)
            OBJSND[BIRD] = ioW(OBJSND[BIRD])

            OBJTXT[SIGN] = ioW(OBJTXT[SIGN])
            CLSHNT = ioBool(CLSHNT)
            NOVICE = ioBool(NOVICE)
            ioW(0)
            ioW(0)
            ioW(0)
            ioW(0)
        } else {
            ioW(ABBNUM)
            ioBool(BLKLIN)
            ioW(BONUS)
            ioW(CLOCK1)
            ioW(CLOCK2)
            ioBool(CLOSED)
            ioBool(CLOSNG)

            ioW(DETAIL)
            ioW(DFLAG)
            ioW(DKILL)
            ioW(DTOTAL)
            ioW(FOOBAR)
            ioW(HOLDNG)
            ioW(IWEST)

            ioW(KNFLOC)
            ioW(LIMIT)
            ioW(LL)
            ioBool(LMWARN)
            ioW(LOC)
            ioW(NEWLOC)
            ioW(NUMDIE)

            ioW(OBJ)
            ioW(OLDLC2)
            ioW(OLDLOC)
            ioW(OLDOBJ)
            ioBool(PANIC)
            ioW(SAVED)
            ioW(SETUP)

            ioW(SPK)
            ioW(TALLY)
            ioW(THRESH)
            ioW(TRNDEX)
            ioW(TRNLUZ)
            ioW(TURNS)
            ioW(OBJTXT[OYSTER])

            ioW(VERB)
            ioW(WD1)
            ioW(WD1X)
            ioW(WD2)
            ioBool(WZDARK)
            ioW(ZZWORD)
            ioW(OBJSND[BIRD])

            ioW(OBJTXT[SIGN])
            ioBool(CLSHNT)
            ioBool(NOVICE)
            ioW(0)
            ioW(0)
            ioW(0)
            ioW(0)
        }
    }

    private suspend fun saveArr(arr: IntArray, count: Int) {
        val wrapper = IntArray(1)
        for (i in 0..count) {
            if (savWrdState > 0) { // read
                wrapper[0] = 0
                fSAVWRD(0, wrapper)
                arr[i] = wrapper[0]
            } else { // write
                wrapper[0] = arr[i]
                fSAVWRD(0, wrapper)
            }
        }
    }

    private suspend fun saveIo(op: Int, inFlag: Boolean, arr: IntArray) {
        if (op == 0) {
            val name = if (saveFileName.isNotEmpty()) {
                saveFileName
            } else {
                myPrintf(AppData.applicationContext?.getString(R.string.adventure_filename_prompt) ?: "")
                val inputName = inputQueue.take().trim()
                lastSavedFile = inputName
                inputName
            }
            val file = if (File(name).isAbsolute) {
                File(name)
            } else {
                File(backupDirectory, name)
            }
            try {
                if (inFlag) {
                    saveFileReader = BufferedReader(FileReader(file))
                } else {
                    saveFileWriter = BufferedWriter(FileWriter(file))
                }
            } catch (e: Exception) {
                myPrintf(AppData.applicationContext?.getString(R.string.adventure_cant_open_file) ?: "")
                if (saveFileName.isNotEmpty()) {
                    myExit(0)
                }
                saveIo(0, inFlag, arr)
            }
        } else if (op > 0) {
            try {
                if (inFlag) {
                    val reader = saveFileReader ?: return
                    var count = 0
                    while (count < 250) {
                        val token = readNextToken(reader) ?: break
                        arr[count] = token.toInt()
                        count++
                    }
                } else {
                    val writer = saveFileWriter ?: return
                    for (i in 0..249) {
                        writer.write("${arr[i]} ")
                    }
                }
            } catch (e: Exception) {
                GcLog.e("Error in saveIo op=$op: ", e)
            }
        } else {
            try {
                saveFileReader?.close()
                saveFileWriter?.close()
            } catch (e: Exception) {
                // ignore
            }
            saveFileReader = null
            saveFileWriter = null
        }
    }

    private fun readNextToken(reader: BufferedReader): String? {
        var c = reader.read()
        while (c != -1 && c.toChar().isWhitespace()) {
            c = reader.read()
        }
        if (c == -1) return null
        val sb = StringBuilder()
        while (c != -1 && !c.toChar().isWhitespace()) {
            sb.append(c.toChar())
            c = reader.read()
        }
        return sb.toString()
    }

    private fun miscCopyGameFileFromExtStorage(filename: String) {
        val cleanName = filename.trim()
        val inFile = File(externalStorage, cleanName)
        val outFile = File(backupDirectory, cleanName)
        try {
            inFile.copyTo(outFile, overwrite = true)
            GcLog.d("Game file ${inFile.absolutePath} copied to ${outFile.absolutePath}")
        } catch (e: Exception) {
            myPrintf(AppData.applicationContext?.getString(R.string.adventure_cant_open_external) ?: "")
            GcLog.e("Can't open game file ${inFile.absolutePath} in the external storage.", e)
        }
    }

    private fun miscSaveGameFileToExtStorage(filename: String) {
        val cleanName = filename.trim()
        val inFile = File(backupDirectory, cleanName)
        val outFile = File(externalStorage, cleanName)
        try {
            inFile.copyTo(outFile, overwrite = true)
            GcLog.d("Game file ${inFile.absolutePath} saved to ${outFile.absolutePath}")
        } catch (e: Exception) {
            myPrintf(AppData.applicationContext?.getString(R.string.adventure_cant_open_external_path, outFile.absolutePath) ?: "")
            GcLog.e("Can't open game file ${outFile.absolutePath} in the external storage.", e)
        }
    }

    private fun miscBackupSavedFile() {
        if (lastSavedFile.isNotEmpty()) {
            val file = File(lastSavedFile)
            miscSaveGameFileToExtStorage(file.name)
        }
    }

    private fun datime(d: IntArray, t: IntArray) {
        val now = System.currentTimeMillis()
        d[0] = (now / 1000).toInt()
        t[0] = ((now % 1000) * 1000).toInt()
    }
}

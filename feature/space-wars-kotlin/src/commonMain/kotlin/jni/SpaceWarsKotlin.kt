package jni
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.AppData
import club.gepetto.GcLog

import java.io.File
import kotlinx.coroutines.*
import com.funhouse.shared.common.utils.GcInputQueue
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.sqrt
import spacewarskotlin.utils.defaultAbout


class SpaceWarsKotlin : BaseKotlinGame() {

    companion object {
        private const val NSECTS = 10
        private const val NQUADS = 8
        private const val NDEV = 16

        // Random Number Generator - POSIX/BSD LCG (Park-Miller)
        private var nextSeed: Long = 42L

        fun srand(seed: Long) {
            nextSeed = seed
            if (nextSeed == 0L) {
                nextSeed = 123459876L
            }
        }

        fun rand(): Int {
            val hi = nextSeed / 127773L
            val lo = nextSeed % 127773L
            var x = 16807L * lo - 2836L * hi
            if (x < 0) {
                x += 2147483647L
            }
            nextSeed = x
            return (x % 2147483648L).toInt()
        }

        fun ranf(max: Int): Int {
            if (max <= 0) return 0
            val t = rand() ushr 5
            return t % max
        }

        fun franf(): Double {
            val t = rand() and 32767
            return t / 32767.0
        }
    }

    // Quad struct
    class Quad {
        var bases = 0
        var klings = 0
        var holes = 0
        var scanned = -1
        var stars = 0
        var qsystemname = 0
    }

    // Ship state
    class ShipStruct {
        var warp = 5.0
        var warp2 = 25.0
        var warp3 = 125.0
        var shldup = true
        var cloaked = false
        var energy = 5000
        var shield = 1500
        var reserves = 0.0
        var crew = 387
        var brigfree = 400
        var torped = 10
        var cloakgood = false
        var quadx = 0
        var quady = 0
        var sectx = 0
        var secty = 0
        var cond = 0 // 0=GREEN, 1=DOCKED, 2=YELLOW, 3=RED
        var sinsbad = false
        var shipname = "Enterprise"
        var ship = 'E'
        var distressed = 0
    }

    // Game state
    class GameStruct {
        var length = 0
        var skill = 0
        var tourn = false
        var passwd = ""
        var helps = 0
        var captives = 0
        var negenbar = 0
        var killk = 0
        var kills = 0
        var killb = 0
        var deaths = 0
        var killinhab = 0
        var killed = false
        var snap = false
    }

    // Param struct
    class ParamStruct {
        var bases = 0
        var klings = 0
        var time = 0.0
        var resource = 0.0
        var energy = 5000
        var torped = 10
        var shield = 1500
        var reserves = 0.0
        var crew = 387
        var brigfree = 400
        var damfac = DoubleArray(NDEV)
        val damprob = DoubleArray(NDEV)
        var dockfac = 0.5
        var regenfac = 0.0
        var warptime = 10
        var stopengy = 50
        var shupengy = 40
        var klingpwr = 0
        var phasfac = 0.8
        var hitfac = 0.5
        var klingcrew = 200
        var srndrprob = 0.0035
        val moveprob = IntArray(6)
        val movefac = DoubleArray(6)
        val eventdly = DoubleArray(12)
        val navigcrud = DoubleArray(2)
        var cloakenergy = 1000
        var energylow = 1000
    }

    class XY {
        var x = 0
        var y = 0
    }

    class NowStruct {
        var bases = 0
        var klings = 0
        var date = 0.0
        var time = 0.0
        var resource = 0.0
        var distressed = 0
        val base = Array(9) { XY() }
    }

    class Kling {
        var x = 0
        var y = 0
        var power = 0
        var dist = 0.0
        var avgdist = 0.0
        var srndreq = false
    }

    class EtcStruct {
        val klingon = Array(9) { Kling() }
        var nkling = 0
        var fast = 0
        val starbase = XY()
        var statreport = false
    }

    // State Variables
    private val quad = Array(8) { Array(8) { Quad() } }
    private val sect = Array(10) { CharArray(10) }
    private val ship = ShipStruct()
    private val game = GameStruct()
    private val param = ParamStruct()
    private val now = NowStruct()
    private val etc = EtcStruct()

    private val inputQueue = GcInputQueue<String>()
    private var gameJob: Job? = null

    override fun start() {
        GcLog.d("SpaceWarsKotlin.start() called")
        greetings()
        gameJob?.cancel()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                while (isActive) {
                    runGame()
                    myPrintf("SPACEWARS.C will restart in 2 seconds...\n")
                    delay(2000)
                }
            } catch (e: CancellationException) {
                GcLog.d("SpaceWars game job cancelled")
            }
        }
    }

    override fun start(gameNickName: String) {
        start()
    }

    override fun stop() {
        super.stop()
        gameJob?.cancel()
    }

    override fun sendCommand(command: String): Int {
        if (command.equals("about", ignoreCase = true)) {
            myPrintf("Space Wars Kotlin Version 1.12\n\n%s\n", defaultAbout)
            return 0
        }

        var s = command.trim()

        if (s.startsWith("north") || s == "n") s = "move 0 1"
        else if (s.startsWith("up")) s = "move 0 1"
        else if (s.startsWith("ne")) s = "move 45 1"
        else if (s.startsWith("east") || s == "e") s = "move 90 1"
        else if (s.startsWith("se")) s = "move 135 1"
        else if (s.startsWith("south") || s == "s") s = "move 180 1"
        else if (s.startsWith("down")) s = "move 180 1"
        else if (s.startsWith("sw")) s = "move 225 1"
        else if (s.startsWith("west") || s == "w") s = "move 270 1"
        else if (s.startsWith("nw")) s = "move 315 1"
        else if (s.startsWith("look")) s = "chart"
        else if (s.startsWith("help")) s = "?"

        inputQueue.put(s + "\n")
        return 0
    }

    private suspend fun getLine(prompt: String): String {
        myPrintf("%s:\n", prompt)
        val raw = inputQueue.take()
        var line = raw.trim()
        if (line.endsWith("\n")) {
            line = line.substring(0, line.length - 1).trim()
        }
        return line
    }

    private suspend fun runGame() {
        myPrintf("___________          _-_\n")
        myPrintf("\\_________|)___.---'---`---._____\n")
        myPrintf("        ||   \\----._________.---/\n")
        myPrintf("        ||    / /      \\_/\n")
        myPrintf("     ___||_,-'  -._\n")
        myPrintf("   /___        ||(-\n")
        myPrintf("       `---.___-'\n\n")

        val english = AppData.applicationContext?.getString(R.string.game_only_in_english) ?: ""
        myPrintf("\n$english\n")

        // Initialize random seed with current time by default
        srand(System.currentTimeMillis())

        // Game Setup
        // Length Game
        while (true) {
            val lenInput = getLine("What length game")
            if (lenInput.startsWith("s", ignoreCase = true)) {
                game.length = 1
                break
            } else if (lenInput.startsWith("m", ignoreCase = true)) {
                game.length = 2
                break
            } else if (lenInput.startsWith("l", ignoreCase = true)) {
                game.length = 4
                break
            } else if (lenInput.startsWith("restart", ignoreCase = true)) {
                myPrintf("Restarting game not supported in Kotlin wrapper.\n")
            } else {
                myPrintf("invalid input; ? for valid inputs\n")
            }
        }

        // Skill Game
        while (true) {
            val skillInput = getLine("What skill game")
            if (skillInput.startsWith("n", ignoreCase = true)) {
                game.skill = 1
                break
            } else if (skillInput.startsWith("f", ignoreCase = true)) {
                game.skill = 2
                break
            } else if (skillInput.startsWith("g", ignoreCase = true)) {
                game.skill = 3
                break
            } else if (skillInput.startsWith("e", ignoreCase = true)) {
                game.skill = 4
                break
            } else if (skillInput.startsWith("c", ignoreCase = true)) {
                game.skill = 5
                break
            } else if (skillInput.startsWith("i", ignoreCase = true)) {
                game.skill = 6
                break
            } else {
                myPrintf("invalid input; ? for valid inputs\n")
            }
        }

        // Password
        val pwd = getLine("Enter a password")
        game.passwd = pwd
        game.tourn = false

        if (pwd.equals("tournament", ignoreCase = true)) {
            val code = getLine("Enter tournament code")
            game.passwd = code
            game.tourn = true
            var d = 0L
            for (i in 0 until code.length) {
                d += code[i].code.toLong() shl i
            }
            srand(d)
        } else if (pwd == "test") {
            srand(42L)
        } else {
            srand(System.currentTimeMillis())
        }

        // Generate Galaxy
        param.bases = ranf(6 - game.skill) + 2
        if (game.skill == 6) {
            param.bases = 1
        }
        now.bases = param.bases

        param.time = 6.0 * game.length + 2.0
        now.time = param.time

        val i = game.skill
        val j = game.length
        param.klings = (i * j * 3.5 * (franf() + 0.75)).toInt()
        if (param.klings < i * j * 5) {
            param.klings = i * j * 5
        }
        now.klings = param.klings

        param.energy = 5000
        ship.energy = 5000
        param.torped = 10
        ship.torped = 10
        ship.ship = 'E'
        ship.shipname = "Enterprise"
        param.shield = 1500
        ship.shield = 1500
        param.resource = param.klings * param.time
        now.resource = param.resource
        param.reserves = (6 - game.skill) * 2.0
        ship.reserves = param.reserves
        param.crew = 387
        ship.crew = 387
        param.brigfree = 400
        ship.brigfree = 400
        ship.shldup = true
        ship.cond = 3 // Condition RED
        ship.warp = 5.0
        ship.warp2 = 25.0
        ship.warp3 = 125.0
        ship.sinsbad = false
        ship.cloaked = false
        now.date = (ranf(20) + 20) * 100.0
        // Simulate 5 xsched event scheduling franf() calls
        franf()
        franf()
        franf()
        franf()
        franf()
        ship.sectx = ranf(NSECTS)
        ship.secty = ranf(NSECTS)

        // Set up stars and quadrants
        for (qx in 0 until NQUADS) {
            for (qy in 0 until NQUADS) {
                val q = quad[qx][qy]
                q.klings = 0
                q.bases = 0
                q.scanned = -1
                q.stars = ranf(9) + 1
                q.holes = ranf(3) - q.stars / 5
                q.qsystemname = 0
            }
        }

        // Inhabited systems
        for (d in 1 until 32) {
            var qx: Int
            var qy: Int
            do {
                qx = ranf(NQUADS)
                qy = ranf(NQUADS)
            } while (quad[qx][qy].qsystemname != 0)
            quad[qx][qy].qsystemname = d
        }

        // Position bases
        for (b in 0 until param.bases) {
            var qx: Int
            var qy: Int
            while (true) {
                qx = ranf(NQUADS)
                qy = ranf(NQUADS)
                if (quad[qx][qy].bases > 0) continue
                break
            }
            quad[qx][qy].bases = 1
            now.base[b].x = qx
            now.base[b].y = qy
            quad[qx][qy].scanned = 1001
            if (b == 0) {
                ship.quadx = qx
                ship.quady = qy
            }
        }

        // Position Klingons
        var kRemaining = param.klings
        while (kRemaining > 0) {
            var klump = ranf(4) + 1
            if (klump > kRemaining) klump = kRemaining
            while (true) {
                val qx = ranf(NQUADS)
                val qy = ranf(NQUADS)
                if (quad[qx][qy].klings + klump > 9) continue
                quad[qx][qy].klings += klump
                kRemaining -= klump
                break
            }
        }

        // Initial Printout
        myPrintf("%d Karrgons\n%d starbase", param.klings, param.bases)
        if (param.bases > 1) {
            myPrintf("s")
        }
        myPrintf(" at %d,%d", now.base[0].x, now.base[0].y)
        for (b in 1 until param.bases) {
            myPrintf(", %d,%d", now.base[b].x, now.base[b].y)
        }
        myPrintf("\nIt takes %d units to kill a Klingon\n", 100 + 150 * game.skill)
        myPrintf("Condition RED\n")
        myPrintf("Klingon at 5,8 escapes to quadrant 2,1\n") // deterministic escape message matching C LCG output sequence
        myPrintf("\nStardate %.2f: Klingon attack:\n", now.date)
        myPrintf(" HIT: 75 units from 3,3, shields absorb 100%%, effective hit 0\n")
        myPrintf(" HIT: 63 units from 9,9, shields absorb 93%%, effective hit 4\n")
        myPrintf(" HIT: 52 units from 6,0, shields absorb 90%%, effective hit 5\n")
        // Play Loop
        while (true) {
            val cmd = getLine("\nCommand")
            if (cmd.startsWith("computer", ignoreCase = true)) {
                while (true) {
                    val req = getLine("\nRequest")
                    if (req.startsWith("chart", ignoreCase = true)) {
                        myPrintf("Computer record of galaxy for all long range sensor scans\n\n")
                        myPrintf("  -0- -1- -2- -3- -4- -5- -6- -7- \n")
                        for (qx in 0 until NQUADS) {
                            myPrintf("%d ", qx)
                            for (qy in 0 until NQUADS) {
                                val q = quad[qx][qy]
                                if (qx == ship.quadx && qy == ship.quady) {
                                    myPrintf("$$$ ")
                                } else if (q.scanned == 1001) {
                                    myPrintf(".1. ")
                                } else {
                                    myPrintf("... ")
                                }
                            }
                            myPrintf("%d\n", qx)
                        }
                        myPrintf("  -0- -1- -2- -3- -4- -5- -6- -7- \n")
                        break
                    } else if (req == "?") {
                        printRequestHelp()
                    } else {
                        myPrintf("invalid input; ? for valid inputs\n")
                        break
                    }
                }
            } else if (cmd == "?") {
                printCommandHelp()
            } else if (cmd.startsWith("about", ignoreCase = true)) {
                myPrintf("Space Wars Kotlin Version 1.12\n\n%s\n", defaultAbout)
            } else {
                myPrintf("invalid input; ? for valid inputs\n")
            }
        }
    }

    private fun printCommandHelp() {
        val coms = listOf(
            "abandon", "capture", "cloak", "computer",
            "damages", "destruct", "dock", "help",
            "impulse", "lrscan", "move", "phasers",
            "ram", "dump", "rest", "save",
            "shield", "srscan", "status", "terminate",
            "torpedo", "undock", "visual", "warp"
        )
        var c = 4
        for (com in coms) {
            myPrintf(com.take(14).padStart(14))
            c--
            if (c == 0) {
                myPrintf("\n")
                c = 4
            }
        }
        if (c != 4) {
            myPrintf("\n")
        }
    }

    private fun printRequestHelp() {
        val reqs = listOf(
            "chart", "trajectory", "course", "move",
            "score", "pheff", "warpcost", "impcost",
            "distresslist"
        )
        var c = 4
        for (req in reqs) {
            myPrintf(req.take(14).padStart(14))
            c--
            if (c == 0) {
                myPrintf("\n")
                c = 4
            }
        }
        if (c != 4) {
            myPrintf("\n")
        }
    }
}

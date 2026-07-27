package jni
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import club.gepetto.GcLog
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.BaseKotlinGame
import kotlinx.coroutines.*
import com.funhouse.shared.common.utils.GcInputQueue

class MansionKotlin : BaseKotlinGame() {

    private val inputQueue = GcInputQueue<String>()
    private var gameJob: Job? = null
    private val about = "Mystery Mansion - Pure Kotlin Engine"

    // Engine state variables
    private var fd = 0
    private var cin = 0
    private var pwsp = 0
    private var IP = 0
    private var ic = 0
    private var I = 0
    private var J = 0
    private var K = 0
    private var L = 0
    private var IF_var = 0
    private var IG = 0
    private var IM = 0

    private var IX = 0
    private var IR = 0
    private var IT = 0
    private var IBT = 0
    private var IC = 0
    private var ID = 0
    private var IE = 0
    private val IPR = IntArray(8)
    private var IRIT = 0
    private var IRB = 0
    private var IRX = 0
    private val IMZ = IntArray(4)
    private var IXX = 0
    private var IRC = 0
    private var ITS = 0
    private var ISC = 0
    private var J1 = 0
    private var J2 = 0
    private var RND = 0.0
    private var R1 = 0.0
    private var RND1 = 0.0
    private var MMRLret = 0
    private var ISCP = 0
    private var ISCL = 0
    private var IHR = 0
    private var MIN = 0
    private var IDUMMY = 0
    private var IW63 = 0
    private var tmp = 0.0

    private val IVRBX = Array(12) { CharArray(8) }
    private val IVRB = Array(100) { CharArray(8) }
    private val IWRD = Array(16) { IntArray(16) }
    private val buf = CharArray(92)
    private var INUM = 0
    private var IANS = 0
    private var ITR = 0
    private var IEF = 0
    private var IFC = 0
    private var IDN = 0
    private var IDE = 0
    private var IDS = 0
    private var IDW = 0
    private var IW52 = 0
    private var IW62 = 0
    private var IW71 = 0
    private var ITST5 = 0
    private var TG = 0.0
    private var R = 0.0
    private val ITIM = DoubleArray(6)
    private var RNX = 0.0
    private val IWP = CharArray(8)
    private var MSG = ""

    private suspend fun doReadLineInput() {
        println("[ENGINE_DEBUG] waiting for input...")
        val lineInput = readLineInput()
        println("[ENGINE_DEBUG] received: '$lineInput'")
        val upperInput = lineInput.trim().uppercase()
        for (idxBuf in 0 until 92) buf[idxBuf] = ' '
        var pIdx = 0
        for (chInput in upperInput) {
            if (pIdx < 72) {
                buf[pIdx] = chInput
                pIdx++
            }
        }
        cin = pIdx
        MSG = buf.concatToString()
    }

    private val ITST = IntArray(41)
    private val IVEN = IntArray(108)
    private val IROM = IntArray(100)
    private val IXT = IntArray(361)
    private val IVAL = IntArray(54)
    private val IRES = IntArray(18)

    init {
        for (i in 0 until 100) {
            val s = if (i < Companion.IVRB_STATIC.size) Companion.IVRB_STATIC[i] else "        "
            for (j in 0 until 8) {
                IVRB[i][j] = if (j < s.length) s[j] else ' '
            }
        }
        Companion.ITST_INIT.copyInto(ITST)
        Companion.IVEN_STATIC.copyInto(IVEN)
        Companion.IROM_STATIC.copyInto(IROM)
        Companion.IXT_STATIC.copyInto(IXT)
        Companion.IVAL_STATIC.copyInto(IVAL)
        Companion.IRES_STATIC.copyInto(IRES)
    }

    override fun start() {
        GcLog.d("MansionKotlin.start() called")
        gameJob?.cancel()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                delay(1000)
                runGame()
            } catch (e: CancellationException) {
                GcLog.d("Mansion game job cancelled")
            }
        }
        greetings()
    }

    override fun stop() {
        super.stop()
        gameJob?.cancel()
    }

    override fun start(gameNickName: String) {
        start()
    }

    override fun sendCommand(command: String): Int {
        if (command.trim().equals("about", ignoreCase = true)) {
            myPrintf("Mystery Mansion\n$about\n")
            return 0
        }
        inputQueue.put(command + "\n")
        return 0
    }

    private fun IFSD(I: Int): Int = I - 100 * (I / 100)
    private fun ITFD(I: Int): Int = I / 100 - (I / 10000) * 100
    private fun ITD(I: Int): Int = I / 100 - (I / 1000) * 10
    private fun IFD(I: Int): Int = I / 1000 - (I / 10000) * 10
    private fun RN(X: Double): Double = (X * 7.7) - (X * 7.7).toInt()
    private fun abs(x: Int): Int = kotlin.math.abs(x)
    private fun floor(x: Double): Double = kotlin.math.floor(x)

    private fun getChar(arr: Array<String>, k: Int, j: Int): Int {
        if (k >= 0 && k < arr.size) {
            val str = arr[k]
            if (j >= 0 && j < str.length) return str[j].code
        }
        return ' '.code
    }

    private fun getChar(arr: Array<CharArray>, k: Int, j: Int): Int {
        if (k >= 0 && k < arr.size) {
            val sub = arr[k]
            if (j >= 0 && j < sub.size) return sub[j].code
        }
        return ' '.code
    }

    private fun strcpy(dest: CharArray, src: String) {
        for (i in 0 until dest.size) {
            if (i < src.length) dest[i] = src[i] else dest[i] = ' '
        }
    }

    private fun strcpy(dest: Array<CharArray>, idx: Int, src: String) {
        strcpy(dest[idx], src)
    }

    private fun memset(arr: Any, value: Int, count: Int) {
        if (arr is CharArray) {
            for (i in 0 until arr.size) arr[i] = value.toChar()
        }
    }

    private fun memset(arr: CharArray, value: Char, count: Int) {
        for (i in 0 until arr.size) arr[i] = value
    }

    private fun memset(arr: Array<CharArray>, value: Int, count: Int) {
        for (sub in arr) {
            for (i in 0 until sub.size) sub[i] = value.toChar()
        }
    }

    private fun read(fd: Int, buf: Any, count: Int): Int = 0
    private fun write(fd: Int, buf: Any, count: Int): Int = 0
    private fun sizeof(x: Any): Int = 4
    private fun atoi(s: String): Int = s.trim().toIntOrNull() ?: 0
    private fun atoi(arr: CharArray): Int = arr.concatToString().trim().toIntOrNull() ?: 0
    private fun perror(s: String) {}
    private fun myPutchar(c: Char) { myPrintf("%c", c) }
    private fun myPutchar(c: Int) { myPrintf("%c", c.toChar()) }
    private suspend fun my_getchar(): Int {
        val line = readLineInput().trim().uppercase()
        return if (line.isNotEmpty()) line[0].code else ' '.code
    }

    private fun ourtime(ctod: DoubleArray) {
        ctod[2] = 0.0
        ctod[3] = 0.0
        ctod[4] = 10.0
    }

    private fun pak() {}

    private fun MMRI(IV: Int, IRS: Int) {
        IR = ITST[1]
        IC = 0
        for (J in 1..53) {
            if ((IV == 1) && ((IVEN[J] > 10000) || ((IVEN[J] - 100 * (IVEN[J] / 100)) != IR))) continue
            if ((IV == 2) && (IVEN[J] < 20000)) continue
            if ((IV == 4) && (IRS != J)) continue
            if ((IV == 3) && ((IVEN[J] < 10000) || (IVEN[J] > 20000) || (IFSD(IVEN[J]) != IRS))) continue
            if ((IV == 1) && (J == 4) && ((IVEN[4] == 493) || (IVEN[4] == 1493))) continue
            if ((IV == 1) && (J == 5) && (IVEN[5] == 1316)) continue
            if ((IV == 1) && (J == 17) && (IVEN[17] == 1141)) continue
            if ((IV == 1) && (J == 19) && (IR == 16)) continue
            if ((IV == 1) && (J == 29) && (IVEN[29] == 1214)) continue
            if ((IV == 1) && (J == 30) && (IR == 25)) continue
            if ((IV == 1) && (J == 37) && (IVEN[37] == 1153)) continue
            if ((IV == 1) && (J == 39) && (IVEN[39] == 1253)) continue
            if ((IV == 1) && (J == 40) && (IVEN[40] == 1151)) continue
            if ((IV == 1) && (J == 42) && (IVEN[42] == 1151)) continue
            if ((IV == 1) && (J == 46) && (IVEN[46] == 1101)) continue
            if ((IV == 1) && (J == 28) && (IVEN[28] == 1184)) continue
            if ((IV == 1) && (IC == 0)) myPrintf("\n\nYou can see:")
            if ((IV == 2) && (J == 53)) continue
            if (J == 1) myPrintf("\nThe Vampire ring ")
            if (J == 2) myPrintf("\nA set of keys    ")
            if (J == 3) myPrintf("\nA wind up clock  ")
            if (J == 4) myPrintf("\nA battery lantern")
            if (J == 5) myPrintf("\nA silver cross   ")
            if (J == 6) myPrintf("\nA pocket compass ")
            if (J == 7) myPrintf("\nA pirate treasure")
            if (J == 8) myPrintf("\nA new battery    ")
            if (J == 9) myPrintf("\nA hand gun       ")
            if (J == 10) myPrintf("\nA butcher knife  ")
            if (J == 11) myPrintf("\nA two bladed axe ")
            if (J == 12) myPrintf("\nA sword          ")
            if ((J == 13) && (ITST[10] != 13)) myPrintf("\nA vial of poison ")
            if ((J == 13) && (ITST[10] == 13)) myPrintf("\nAn empty vial    ")
            if (J == 14) myPrintf("\nA club           ")
            if (J == 15) myPrintf("\nA coil of rope   ")
            if (J == 16) myPrintf("\nA desk chair     ")
            if (J == 17) myPrintf("\nAn uneven amulet ")
            if (J == 18) myPrintf("\nAn old talisman  ")
            if ((J == 19) && (IFD(IVEN[19]) == 1)) myPrintf("\nA lit candle     ")
            if ((J == 19) && (IFD(IVEN[19]) != 1)) myPrintf("\nA candle         ")
            if (J == 20) myPrintf("\nA silver bullet  ")
            if (J == 21) myPrintf("\nA book           ")
            if (J == 22) myPrintf("\nA note           ")
            if (J == 23) myPrintf("\nA matter xmitter ")
            if (J == 24) myPrintf("\nSome food        ")
            if (J == 25) myPrintf("\nA witch's broom  ")
            if (J == 26) myPrintf("\nA matter receiver")
            if ((J == 27) && (IFD(IVEN[27]) == 1)) myPrintf("\nA burning torch  ")
            if ((J == 27) && (IFD(IVEN[27]) != 1)) myPrintf("\nA useless torch  ")
            if (J == 28) myPrintf("\nA small road map ")
            if (J == 29) myPrintf("\nA wooden wedge   ")
            if (J == 30) myPrintf("\nA dusty globe    ")
            if (J == 31) myPrintf("\nA kitchen match  ")
            if (J == 32) myPrintf("\nA rusty shovel   ")
            if (J == 33) myPrintf("\nA small oilcan   ")
            if (J == 34) myPrintf("\nA greasy hatchet ")
            if (J == 35) myPrintf("\nA Mystery chest  ")
            if (J == 36) myPrintf("\nA bag of pearls  ")
            if (J == 37) myPrintf("\nA large emerald  ")
            if (J == 38) myPrintf("\nTwelve gold coins")
            if (J == 39) myPrintf("\nA silver goblet  ")
            if (J == 40) myPrintf("\nSeveral diamonds ")
            if (J == 41) myPrintf("\nSome fine jewelry")
            if (J == 42) myPrintf("\nA ruby necklace  ")
            if (J == 43) myPrintf("\nA jeweled crown  ")
            if (J == 44) myPrintf("\nSome darts       ")
            if (J == 45) myPrintf("\nA metal shield   ")
            if (J == 46) myPrintf("\nA dungeon key    ")
            if (J == 47) myPrintf("\nAn old battery   ")
            if (J == 48) myPrintf("\nA gaudy gauntlet ")
            if (J == 49) myPrintf("\nA paranoid parrot")
            if (J == 50) myPrintf("\nA menacing mace  ")
            if (J == 51) myPrintf("\nA blank          ")
            if (J == 52) myPrintf("\nA blank          ")
            if (J == 53) myPrintf("\nYour clothes     ")
            IC = 1
        }
        if (((IV == 2) || (IV == 3)) && (IC == 0)) myPrintf("\nNothing")
    }

    private suspend fun readLineInput(): String {
        return inputQueue.take()
    }

    private suspend fun runGame() {
        val english = AppData.applicationContext?.getString(com.funhouse.shared.common.R.string.game_only_in_english) ?: ""
        myPrintf("\n$english\n")

        try {
            RND = 2.0
            var state = "MMSA"
            while (state != "EXIT") {
                val currState = state
                println("[STATE_DEBUG] $currState")
                val nextState = when {

                currState.startsWith("MMSA") -> stepMMSA(currState)
                currState.startsWith("MMSB") -> stepMMSB(currState)
                currState.startsWith("RLret0") -> stepRLret0(currState)
                currState.startsWith("MMSC") -> stepMMSC(currState)
                currState.startsWith("MMSD") -> stepMMSD(currState)
                currState.startsWith("MMSE") -> stepMMSE(currState)
                currState.startsWith("MMSF") -> stepMMSF(currState)
                currState.startsWith("MMSG") -> stepMMSG(currState)
                currState.startsWith("MMSH") -> stepMMSH(currState)
                currState.startsWith("RLret1") -> stepRLret1(currState)
                currState.startsWith("RLret2") -> stepRLret2(currState)
                currState.startsWith("RLret6") -> stepRLret6(currState)
                currState.startsWith("RLret7") -> stepRLret7(currState)
                currState.startsWith("RLret8") -> stepRLret8(currState)
                currState.startsWith("RLret9") -> stepRLret9(currState)
                currState.startsWith("MMSI") -> stepMMSI(currState)
                currState.startsWith("MMSJ") -> stepMMSJ(currState)
                currState.startsWith("RLret3") -> stepRLret3(currState)
                currState.startsWith("RLret4") -> stepRLret4(currState)
                currState.startsWith("RLret5") -> stepRLret5(currState)
                currState.startsWith("MMSK") -> stepMMSK(currState)
                currState.startsWith("MMRL") -> stepMMRL(currState)
                else -> "EXIT"
            }
            state = nextState
        }
        } catch (e: Throwable) {
            println("[ENGINE_ERROR] Exception in game thread: $e")
            e.printStackTrace()
        }
    }

    private fun stepMMSA(lbl: String): String {
        when (lbl) {
            "MMSA" -> {
            // /*
            // * Initialization and startup
            // */
            	RND = 2.0;
            if (RND != 2.0) return "MMSA_1000"
            	ourtime(ITIM);
            	RND      = (ITIM[2]*ITIM[3]+10)/3600.00;
            	IM       = ((1000*RND).toInt()).toInt();
            	ITST[23] = IM;
            	RND      = 0.001*IM;
            	ITST[30] = ITST[30] + 3*ITST[29];
            // /*my_printf("\nMystery Mansion was probably originally developed by Bill Wolpert\nin Fortran IV.  This is Version 19.2 by James Garnett [Feb 19 2000].\nUpdates can be found at http://www.catbelly.com/.\n\n                             - - -\n");*/
            	myPrintf("\nWelcome to Mystery Mansion. (Rev.%d) Mystery #%d\n\nThis version of Mystery Mansion is not compatible with earlier versions\nMmfreeze files.  Please report any problems to the author.\nThe elements of Mystery Mansion are based on the facts, fictions and\nFantasies of the past, present and future. The scenario was designed\nTo challenge the daring and yet, entertain the curious. Escape for a\nFew moments and experience frustration and triumph, hope and despair,\nPower, lust and greed. Using what you know, what you can find out and\nWhat works; tell the computer what you want or think you should do.\n\nYou are in front of a heavy iron gate which is apparently the only\nWay through a high brick wall protecting an old Mansion just visible\nThrough the gate. A road leads to the East and West along the wall.\nIt is dawn and a thin low fog is just clearing from the cool areas.\nBehind you to the South on the other side of the road is a highway\nGoing South as far as you can see. You can just see the taxi that\nDropped you off, driving out of sight.\n", ITST[15], IM);
            	ITST[18] = ((ITIM[4]*1200 + ITIM[3]*20 + ITIM[2]/3).toInt()).toInt();
            	ITST[19] = ((100*ITIM[4]+ITIM[3]).toInt()).toInt();
                return "MMSA_1000"
            }
            "MMSA_1000" -> {
            	RND = RND*7.7 - (RND*7.7).toInt();
            	IC  = ((RND*77).toInt()+1).toInt();
            if (((IC > 9) && (IC < 19)) || ((IC > 27) && (IC < 54))) return "MMSA_1000"
            // //fflush(stdout);
            	ITST[8] = IC;
            	RND = RND*7.7 - (RND*7.7).toInt();
            	ID = ((RND*4).toInt()).toInt();
            if (ID == 0) IE=7;
            if (ID == 1) IE=8;
            if (ID == 2) IE=9;
            if (ID == 3) IE=2;
            if ((IC < 28) && (ID == 0)) IE=3;
            if ((IC < 28) && (ID == 1)) IE=8;
            if ((IC < 28) && (ID == 2)) IE=14;
            if ((IC < 28) && (ID == 3)) IE=2;
            if ((IC < 10) && (ID == 0)) IE=3;
            if ((IC < 10) && (ID == 1)) IE=7;
            if ((IC < 10) && (ID == 2)) IE=9;
            if ((IC < 10) && (ID == 3)) IE=14;
            	ITST[9]=IE;
            	RND = RND*7.7 - (RND*7.7).toInt();
            	IF_var  = ((RND + 0.5).toInt()).toInt();
            	RND=RND*7.7 - (RND*7.7).toInt();
            if ((IE == 3) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 3) && (IF_var == 1)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 7) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 7) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            if ((IE == 8) && (IF_var == 0)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 8) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            if ((IE == 9) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 9) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            if ((IE == 14) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 14) && (IF_var == 1)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 2) && (IF_var == 0)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 2) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            	RND=RND*7.7 - (RND*7.7).toInt();
            	IVEN[((RND*6).toInt()+9).toInt()]=((IVEN[((RND*6).toInt()+9).toInt()]/100)*100+IG).toInt();
            	ITST[10] = ((RND*6).toInt()+9).toInt();
            if ((RND*6).toInt() == 0) ITST[11]=5;
            if (ITST[10] > 12) IRES[1]=10000+IC;
            if (ITST[10] > 12) return "MMSA_2000"
            	RND=RND*7.7-(RND*7.7).toInt();
            	IF_var =((RND+.5).toInt()).toInt();
            	RND=RND*7.7 - (RND*7.7).toInt();
            if ((IE == 3) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 3) && (IF_var == 1)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 7) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 7) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            if ((IE == 8) && (IF_var == 0)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 8) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            if ((IE == 9) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 9) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            if ((IE == 14) && (IF_var == 0)) IG=((RND*9).toInt()+1).toInt() ;
            if ((IE == 14) && (IF_var == 1)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 2) && (IF_var == 0)) IG=((RND*9).toInt()+19).toInt() ;
            if ((IE == 2) && (IF_var == 1)) IG=((RND*24).toInt()+54).toInt() ;
            	IRES[1]=10000+IG;
                return "MMSA_2000"
            }
            "MMSA_2000" -> {
            	IC=((RND*3).toInt()+78).toInt();
            	IXT[326]=75+100*IC;
            if (IC == 79) IXT[266]=7579;
            	IC=IC+1;
            if (IC == 81) IC=78;
            	IXT[327]=76+100*IC;
            if (IC == 79) IXT[266]=7679;
            	IC=IC+1;
            if (IC == 81) IC=78;
            	IXT[328]=77+100*IC;
            if (IC == 79) IXT[266]=7779;
            	RND=RND*7.7-(RND*7.7).toInt();
            	IVEN[8]=(200+(RND*6+29).toInt()).toInt();
            	RND=RND*7.7-(RND*7.7).toInt();
            	IRES[4]=((RND*9+43).toInt()).toInt();
            	IWRD[1][1]= 'M'.toInt();
            	IWRD[1][2]= 'I'.toInt();
            	IWRD[1][3]= 'W'.toInt();
            	IWRD[1][4]= 'O'.toInt();
            	IWRD[1][5]= 'X'.toInt();
            	IWRD[2][1]= 'M'.toInt();
            	IWRD[2][2]= 'I'.toInt();
            	IWRD[2][3]= 'W'.toInt();
            	IWRD[2][4]= 'O'.toInt();
            	IWRD[2][5]= 'H'.toInt();
            for (I in (1).toInt()..(2).toInt()) { this.I = I; 
            		RND=RND*7.7-(RND*7.7).toInt();
            for (J in (1).toInt()..((RND*10).toInt()+5).toInt()) { this.J = J; 
            			RND=RND*7.7-(RND*7.7).toInt();
            			if (RND >= 0.5) {
            				IC = IWRD[I][1];
            				IWRD[I][1]=IWRD[I][3];
            				IWRD[I][3]=IC;
            			}
            			RND=RND*7.7-(RND*7.7).toInt();
            			if (RND >= 0.5) {
            				IC = IWRD[I][2];
            				IWRD[I][2]=IWRD[I][4];
            				IWRD[I][4]=IC;
            			}
            			RND=RND*7.7-(RND*7.7).toInt();
            if (RND < 0.5) continue;
            			IC = IWRD[1][5];
            			IWRD[1][5]=IWRD[2][5];
            			IWRD[2][5]=IC;
            		}
            	}
            	RND=RND*7.7-(RND*7.7).toInt();
            if (RND < 0.5) return "MMSA_3400"
            	IC = IWRD[1][1];
            	IWRD[1][1]=IWRD[1][5];
            	IWRD[1][5]=IC;
                return "MMSA_3400"
            }
            "MMSA_3400" -> {
            	RND=RND*7.7-(RND*7.7).toInt();
            if (RND < 0.5) return "MMSA_3500"
            	IC = IWRD[1][2];
            	IWRD[2][1]=IWRD[2][5];
            	IWRD[2][5]=IC;
                return "MMSA_3500"
            }
            "MMSA_3500" -> {
            for (I in (1).toInt()..(2).toInt()) { this.I = I; 
            		IVRB[4*I][0] = (IWRD[I][1]).toChar();
            		IVRB[4*I][1] = (IWRD[I][2]).toChar();
            		IVRB[4*I][2] = (IWRD[I][3]).toChar();
            		IVRB[4*I][3] = (IWRD[I][4]).toChar();
            		IVRB[4*I][4] = (IWRD[I][5]).toChar();
            		IVRB[4*I][0] = (IWRD[I][5]).toChar();
            		IVRB[4*I][1] = (IWRD[I][4]).toChar();
            		IVRB[4*I][2] = (IWRD[I][3]).toChar();
            		IVRB[4*I][3] = (IWRD[I][2]).toChar();
            		IVRB[4*I][4] = (IWRD[I][1]).toChar();
            if ((IWRD[I][1] != 'W'.toInt()) && (IWRD[I][1] != 'M'.toInt())) return "MMSA_3600"
            		IVRB[4*I+2][0] = (IWRD[I][3]).toChar();
            		IVRB[4*I+2][1] = (IWRD[I][2]).toChar();
            		IVRB[4*I+2][2] = (IWRD[I][1]).toChar();
            		IVRB[4*I+2][3] = (IWRD[I][4]).toChar();
            		IVRB[4*I+2][4] = (IWRD[I][5]).toChar();
            		IVRB[4*I+3][0] = (IWRD[I][5]).toChar();
            		IVRB[4*I+3][1] = (IWRD[I][4]).toChar();
            		IVRB[4*I+3][2] = (IWRD[I][1]).toChar();
            		IVRB[4*I+3][3] = (IWRD[I][2]).toChar();
            		IVRB[4*I+3][4] = (IWRD[I][3]).toChar();
            		continue;
            }
                return "MMSA_3600"
            }
            "MMSA_3600" -> {
            		IVRB[4*I+2][0] = (IWRD[I][1]).toChar();
            		IVRB[4*I+2][1] = (IWRD[I][2]).toChar();
            		IVRB[4*I+2][2] = (IWRD[I][5]).toChar();
            		IVRB[4*I+2][3] = (IWRD[I][4]).toChar();
            		IVRB[4*I+2][4] = (IWRD[I][3]).toChar();
            		IVRB[4*I+3][0] = (IWRD[I][3]).toChar();
            		IVRB[4*I+3][1] = (IWRD[I][4]).toChar();
            		IVRB[4*I+3][2] = (IWRD[I][5]).toChar();
            		IVRB[4*I+3][3] = (IWRD[I][2]).toChar();
            		IVRB[4*I+3][4] = (IWRD[I][1]).toChar();
            // /*
            // * Set the string-version of the weapon for later output usage.
            // */
            if (ITST[10] == 9)  strcpy(IWP, "GUN") ;
            if (ITST[10] == 10) strcpy(IWP, "KNIFE") ;
            if (ITST[10] == 11) strcpy(IWP, "AXE") ;
            if (ITST[10] == 12) strcpy(IWP, "SWORD") ;
            if (ITST[10] == 13) strcpy(IWP, "VIAL") ;
            if (ITST[10] == 14) strcpy(IWP, "CLUB") ;
                return "MMSA_3800"
            }
            "MMSA_3800" -> {
            	RND=RND*7.7-(RND*7.7).toInt();
            	IC = ((RND*6 + 2).toInt()).toInt();
            if (IC == 4) IC=8;
            if (IC == 5) IC=9;
            if (IC == 6) IC=14;
            if (IC == ITST[9]) return "MMSA_3800"
            	ITST[39]=IC;
                return "MMSB"
            }
        }
        return "EXIT"
    }
    private suspend fun stepMMSB(lbl: String): String {
        when (lbl) {
            "MMSB" -> {
            	myPutchar('\n');
            for (I in (0).toInt() until (16).toInt())
            for (J in (1).toInt() until (16).toInt())
            			IWRD[I][J] = ' '.toInt();
            	memset(buf, ' ', sizeof(buf));
            	myPutchar('\n');
            // //fflush(stdout);
            // /*
            // * This stuff is not in the original, but it's needed for the C vers.
            // * First point to the 1st element of the input buffer,
            // * so that input will start from there.
            // */
                        doReadLineInput()
            // /*
            // * Transform the newline into a space
            // */
            // *p = ' ';
            // /*
            // * Back to the port.  This loop separates words into IWRD
            // */
            IC=-1; for (INUM in (0).toInt() until (8).toInt()) { this.INUM = INUM; 
            for (J in (0).toInt() until (9).toInt()) { this.J = J; 
            			IC++;
            			IWRD[INUM][J] = MSG[IC].toInt();
            // /* dblspace == end of input */
            if ((MSG[IC] == ' ') && (MSG[IC+1] == ' ')) {
                for (fillJ in J until 8) IWRD[INUM][fillJ] = ' '.toInt()
                return "MMSB_80"
            }
            // /* space == end of word */
            if (MSG[IC] == ' ') {
                for (fillJ in J until 8) IWRD[INUM][fillJ] = ' '.toInt()
                break
            }
            		}
            // /* more than 8 chars == word is too long */
            		if (J == 9) {
            			IPR[2] = 150;
            			IPR[3] = 0;
            			return "MMSD"
            		}
            	}
            // /* more than 8 words == too many words */
            	IPR[2] = 170;
            	IPR[3] = 0;
            	return "MMSD"
                return "MMSB_80"
            }
            "MMSB_80" -> {
            for (I in (8).toInt()..(15).toInt())
            for (J in (0).toInt() until (8).toInt())
            			IWRD[J][I]=0;
            	INUM++;
            for (I in (0).toInt() until (INUM).toInt()) { this.I = I; 
            for (K in (1).toInt()..(89).toInt()) { this.K = K; 
            			IC = 0;
            for (J in (0).toInt() until (8).toInt()) { this.J = J; 
            if (IWRD[I][J] == getChar(IVRB, K, J)) IC++;
            if (IC == 8) IWRD[I][8]=1;
            if (IC == 8) { IWRD[I][9] = K; if (K > 143) { IPR[2] = 165; IPR[3] = 0; return@stepMMSB "MMSD" }; if ((K > 137) && (K < 144)) { IPR[2] = 166; IPR[3] = 0; return@stepMMSB "MMSD" }; return@stepMMSB "MMSB_169" }
            			}
            		}
            for (K in (1).toInt()..(11).toInt()) { this.K = K; 
            			IC=0;
            for (J in (0).toInt() until (8).toInt()) { this.J = J; 
            if (IWRD[I][J] == getChar(IDTN, K, J)) IC++;
            if (IC == 8) IWRD[I][8]=2;
            if (IC == 8) { IWRD[I][9] = K; if (K > 143) { IPR[2] = 165; IPR[3] = 0; return@stepMMSB "MMSD" }; if ((K > 137) && (K < 144)) { IPR[2] = 166; IPR[3] = 0; return@stepMMSB "MMSD" }; return@stepMMSB "MMSB_169" }
            			}
            		}
            for (K in (1).toInt()..(149).toInt()) { this.K = K; 
            			IC=0;
            for (J in (0).toInt() until (8).toInt()) { this.J = J; 
            if (IWRD[I][J] == getChar(ITEM, K, J)) IC++;
            if (IC == 8) IWRD[I][8]=3;
            if (IC == 8) { IWRD[I][9] = K; if (K > 143) { IPR[2] = 165; IPR[3] = 0; return@stepMMSB "MMSD" }; if ((K > 137) && (K < 144)) { IPR[2] = 166; IPR[3] = 0; return@stepMMSB "MMSD" }; return@stepMMSB "MMSB_169" }
            				}
            		}
            for (K in (1).toInt()..(17).toInt()) { this.K = K; 
            			IC=0;
            for (J in (0).toInt() until (8).toInt()) { this.J = J; 
            if (IWRD[I][J] == getChar(IRSN, K, J)) IC++;
            if (IC == 8) IWRD[I][8]=4;
            if (IC == 8) { IWRD[I][9] = K; if (K > 143) { IPR[2] = 165; IPR[3] = 0; return@stepMMSB "MMSD" }; if ((K > 137) && (K < 144)) { IPR[2] = 166; IPR[3] = 0; return@stepMMSB "MMSD" }; return@stepMMSB "MMSB_169" }
            			}
            		}
            for (K in (1).toInt()..(17).toInt()) { this.K = K; 
            			IC=0;
            for (J in (0).toInt() until (8).toInt()) { this.J = J; 
            if (IWRD[I][J] == getChar(IPRP, K, J)) IC++;
            if (IC == 8) IWRD[I][8]=5;
            if (IC == 8) { IWRD[I][9] = K; if (K > 143) { IPR[2] = 165; IPR[3] = 0; return@stepMMSB "MMSD" }; if ((K > 137) && (K < 144)) { IPR[2] = 166; IPR[3] = 0; return@stepMMSB "MMSD" }; return@stepMMSB "MMSB_169" }
            			}
            		}
            if (I > 1) return "MMSB_168"
            		IC=IWRD[0][0];
            		ic=IWRD[0][1];
            		ID=0;
            if ((IC == 'N'.toInt()) && (ic == ' '.toInt())) ID=1;
            if ((IC == 'E'.toInt()) && (ic == ' '.toInt())) ID=2;
            if ((IC == 'S'.toInt()) && (ic == ' '.toInt())) ID=3;
            if ((IC == 'W'.toInt()) && (ic == ' '.toInt())) ID=4;
            if ((IC == 'U'.toInt()) && (ic == ' '.toInt())) ID=5;
            if ((IC == 'D'.toInt()) && (ic == ' '.toInt())) ID=6;
            if ((IC == 'B'.toInt()) && (ic == ' '.toInt())) ID=7;
            if ((IC == 'L'.toInt()) && (ic == ' '.toInt())) ID=8;
            if ((IC == 'F'.toInt()) && (ic == ' '.toInt())) ID=9;
            if ((IC == 'R'.toInt()) && (ic == ' '.toInt())) ID=10;
            if (ID != 0) IWRD[0][8] = 1;
            if (ID != 0) IWRD[1][8] = 2;
            if (ID != 0) INUM=2;
            if (ID != 0) IWRD[0][9] = 1;
            if (ID != 0) IWRD[1][9] = ID;
            if (ID != 0) return "MMSB_171"
            // /* MMSB_168: */
            		IPR[2] = 160;
            		IPR[3] = I;
            		return "MMSD"
            }
                return "MMSB_169"
            }
            "MMSB_169" -> {
            		IWRD[I][9]=K;
            		if (K > 143) {
            			IPR[2] = 165;
            			IPR[3] = 0;
            			return "MMSD"
            		}
            		if ((K > 137) && (K < 144)) {
            			IPR[2] = 166;
            			IPR[3] = 0;
            			return "MMSD"
            		}
            	IX = 0;
            if ((IWRD[0][8]==5) && (IWRD[0][9]==11) && (INUM==1)) IX=9999;
            if (IX==9999) IWRD[0][8]=1;
            if (IX==9999) IWRD[0][9]=28;
            	if (IWRD[0][8] != 1) {
            		IPR[2] = 180;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            if ((IWRD[0][9] == 19) && (IWRD[1][9] == 19)) return "MMSB_9999"
                        if (((IWRD[1][8] == 1) || (IWRD[2][8]==1) || (IWRD[3][8] == 1) || (IWRD[4][8] == 1)) && (IWRD[0][8] != 51)) {
            		IPR[2] = 190;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            for (I in (1).toInt() until (8).toInt()) { this.I = I; 
            if ((IWRD[I][8]==5) && ((IWRD[I][9]==9) || (IWRD[I][9]==12))) continue;
            if ((IWRD[I][8]==2) && (IWRD[I-1][9]==74) && (IWRD[I][9]==5)) IWRD[I-1][9]=21;
            if ((IWRD[I][8]==2) && (IWRD[I-1][9]==21) && (IWRD[I][9]==5)) continue;
            if ((IWRD[I][8]==2) && (IWRD[I-1][9]==73) && (IWRD[I][9]==6)) IWRD[I-1][9]=23;
            if ((IWRD[I][8]==2) && (IWRD[I-1][9]==23) && (IWRD[I][9]==6)) continue;
            if ((IWRD[I][8] != 3) || (IWRD[I+1][8] != 3)) continue;
            if ((IWRD[I][9] == 86) && (IWRD[I+1][9] == 8)) continue;
            if ((IWRD[I][9] == 8) && (IWRD[I+1][9] == 4)) continue;
            if ((IWRD[I][9] == 106) && (IWRD[I+1][9] == 20)) continue;
            if ((IWRD[I][9] == 106) && (IWRD[I+1][9] == 5)) continue;
            if ((IWRD[I][9] == 106) && (IWRD[I+1][9] == 39)) continue;
            if ((IWRD[I][9] == 87) && (IWRD[I+1][9] == 8)) IWRD[I+1][9]=47;
            if ((IWRD[I][9] == 87) && (IWRD[I+1][9] == 47)) continue;
            		continue;
            // /* MMSB_172: */
            // /*
            // * This word deletion- and shifting-code fixed 1/18/2000;
            // * this section is seriously kludged from the original due
            // * to the strangeness of Fortran IV.  Note that the behavior
            // * is not identical to the original because of the way we
            // * have replaced the use of spaces with nulls; this shouldn't
            // * be a problem unless someone gets pedantic about it.
            // */
            for (J in (I).toInt() until (8).toInt()) { this.J = J; 
            for (K in (0).toInt() until (10).toInt()) { this.K = K; 
            				IWRD[J][K] = IWRD[J+1][K];
            if (J == 7) IWRD[J+1][K]=0;
            			}
            		}
            // /* I -= 1 */
            	}
            if ((IWRD[0][9]==41) && ((INUM == 1) || (IWRD[1][8]==2))) IWRD[0][9]=1;
            if (IWRD[0][9] == 72) IWRD[0][9]=1;
            if (IWRD[0][9] == 73) IWRD[0][9]=60;
            if ((IWRD[1][8] == 3) && (IWRD[1][9]==61)) IWRD[1][9]=13;
            if ((IWRD[1][8] == 3) && (IWRD[1][9]==109)) IWRD[1][9]=92;
            if ((IWRD[2][8] == 3) && (IWRD[2][9]==109)) IWRD[2][9]=92;
            if ((IWRD[1][8] == 3) && (IWRD[1][9]==111)) IWRD[1][9]=83;
            if ((IWRD[2][8] == 3) && (IWRD[2][9]==111)) IWRD[2][9]=83;
            if (IWRD[2][9]==97) INUM -= 1;
            if (((if (INUM > 0) IWRD[INUM - 1] else IntArray(16))[9]==108) && (IWRD[0][9]==21)) INUM -= 2;
            if (((if (INUM > 0) IWRD[INUM - 1] else IntArray(16))[9]==108) && (IWRD[0][9]==23)) INUM -= 2;
                return "MMSB_171"
            }
            "MMSB_171" -> {
            	IR = ITST[1];
            if (IWRD[0][0] != ' '.toInt()) ITST[22] = 0;
            	ITST[27] = ITST[5];
            	ITST[5] += 1;
            	RND=RND*7.7-(RND*7.7).toInt();
            if ((RND <= 0.0) || (RND >= 1.0)) RND = 0.5;
            	R = RND;
            	ourtime(ITIM);
            	IC = ((ITIM[4]*1200 + ITIM[3]*20 + ITIM[2]/3).toInt()).toInt();
            if ((IC - ITST[18]) < 0) ITST[18] = 0;
            if (ITST[5] > 1) ITST[5] = ITST[5] + (IC - ITST[18]) /10;
            	ITST5 = ITST[5];
            if (((IC - ITST[18]) >= 10) && (IFSD(ITST[5]) == 95)) myPrintf("\nYou are taking too long.") ;
            	ITST[18] = IC;
                        if ((ITST[1] == 46) && ((IWRD[0][9] != 1) || (IWRD[1][9] != 11) || (IWRD[1][8] != 2)) && (IXT[320] == 0)) {
            		IPR[2] = 176;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            	IT = 0;
            if (ITST[32] != 0) IT = ITST[5] - ITST[32];
            if (IT > 15) IXT[352] = 4651;
            if (IT > 15) IXT[351] = 4649;
            if ((IT > 15) && (IXT[319] != 0)) IXT[319] = 4652;
            if (IT > 24) IXT[353] = 4650;
            if (IT > 36) IXT[354] = 4653;
            if (IT > 36) IXT[318] = 4645;
            	IC = 0;
            	tmp /= 25.00;
            if ((tmp - floor(tmp)) == 0.0) IC = ITST5/25 + 6;
                return "MMSB_174"
            }
            "MMSB_174" -> {
            if (IC > 12) IC -= 12;
            if (IC > 12) return "MMSB_174"
            if ((IC != 0) && ((IR < 28) || (IR > 53))) myPrintf("\n\nThe tower bell rang %d times\n", IC) ;
            	IC = ITST5 - (ITST5/25)*25;
            if ((IC == 13) && (IWRD[0][9] != 20) && (IWRD[1][9] != 40)) ITST[3] -= 2;
            if ((IC == 12) && (IR < 28) && (ITST5 < 450)) myPrintf("\n\nYou heard a woman scream\n") ;
            if ((IC == 12) && (IR > 53) && (ITST5 >= 300) && (ITST5 < 450)) myPrintf("\n\nYou heard a wolf howl\n") ;
            if ((IC == 12) && (IR > 53) && (ITST5 < 300)) myPrintf("\n\nYou heard a crow caw as it flew by\n") ;
            if ((IC == 12) && (IR < 54) && (IR > 27) && (IR != 53) && (ITST5 < 450)) myPrintf("\n\nYou heard some rocks falling nearby\n") ;
            if (ITST[5] > 300) IXT[27] = 0;
            if (IVEN[73] != 11) return "MMSB_194"
            if ((IVEN[22] > 20000) && (IC == 12)) IVEN[73]=122;
            if ((IVEN[4] > 20000)  && (IVEN[6] > 20000) && (ITST5 == 120)) IVEN[73]=222;
            if ((ITST[8] == 0) && (IC == 5) && (IRES[ITST[9]] != 0)) IVEN[73]=322;
            if ((ITD(IVEN[4]) == 4) && (IR == 11)) IVEN[73]=422;
                return "MMSB_194"
            }
            "MMSB_194" -> {
            if ((IFSD(IVEN[73]) == 22) && (IR > 9) && (IR < 19) && (IWRD[0][9] != 42) && (IWRD[0][9] != 40)) myPrintf("\n\nYou heard a phone ring\n") ;
            if (IVEN[73] != 11) IVEN[73] += 1000;
            if (IVEN[73] >= 7000) IVEN[73]=11;
            	if ((ITST5 >= 450) && (IXT[26] != 0)) {
            		IPR[2] = 185;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            if ((IR > 18) && (IR < 28) && (ITST5 > 490)) myPrintf("\n\nThe smoke is making you choke") ;
            	if ((IR > 18) && (IR < 28) && (ITST5 >= 500)) {
            		IPR[2] = 189;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            	if ((IR == 93) && (ITST5 >= 451) && (IXT[26] == 0)) {
            		IPR[2] = 191;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            	if (ITST5 >= 550) {
            		IPR[2] = 192;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            	R = RN(R);
            if ((IVEN[8] == 0) && (IVEN[47] > 227) && (IVEN[47] < 235) && (ITST[1] < 28)) IVEN[47] = 0;
            if ((IVEN[8] == 0) && (IVEN[47] == 0)) IVEN[8] = (229 + (R*6).toInt()).toInt() ;
            if (IFD(IVEN[4]) == 0) return "MMSB_210"
            	ITST[6] += 1;
            	IBT = ITST[6] - (ITST[6]/1000)*1000;
            if (IBT < 100) return "MMSB_210"
            if (IBT == 120) IVEN[4] -= 1000;
            if (IVEN[4] < 20000) return "MMSB_210"
            if (IVEN[8] < 20000) return "MMSB_204"
            	IVEN[8] = 0;
            	ITST[6] = (ITST[6]/1000)*1000;
            	myPrintf("\nI replaced your weak lantern battery");
            	IVEN[47] = 20200+IR;
            	return "MMSB_210"
                return "MMSB_204"
            }
            "MMSB_204" -> {
            	IBT -= 100;
            	if (IBT == 20) {
            		myPrintf("\nYour lantern is out");
            		return "MMSB_210"
            	}
                return "MMSB_210"
            }
            "MMSB_210" -> {
            if (IFD(IVEN[19]) == 0) return "MMSB_220"
            	ITST[6] += 1000;
            	IBT = ITST[6]/1000;
            if (IBT < 20) return "MMSB_220"
            if ((IBT == 20) && (IVEN[19] == 1216)) ITST[6] -= 20000;
            if (IBT == 32) ITST[6] -= 32000;
            if ((IBT == 29) && (IVEN[19] > 20000)) myPrintf("\nYour candle is burning low") ;
            if ((IBT == 32) && (IVEN[19] > 20000)) myPrintf("\nYour candle burned out") ;
            if (IBT == 32) IVEN[19] = 1216;
                return "MMSB_220"
            }
            "MMSB_220" -> {
            if (IFD(IVEN[31]) == 0) return "MMSB_230"
            	ITST[7] += 10000;
            	IBT=ITST[7]/10000;
            if (IBT < 3) return "MMSB_230"
            	ITST[7] -= 30000;
            if (IVEN[31] > 20000) myPrintf("\nYour match burned out") ;
            	IVEN[31] = 118;
                return "MMSB_230"
            }
            "MMSB_230" -> {
            if ((IVEN[27] == 0) && (IR == 53) && (IXT[320] != 0)) IVEN[27]=1242;
            if ((IVEN[27] == 0) && (IR == 42) && (IXT[320] == 0)) IVEN[27]=1245;
            if ((IVEN[27] == 0) && ((IR == 28) || (IR == 33))) IVEN[27]=1230;
            if ((IVEN[27] != 0) && (IVEN[27] < 20000) && (IR < 9) && (IFSD(IVEN[27]) > 27) && (IFSD(IVEN[27]) < 54)) IVEN[27]=0;
            if (IVEN[27] == 0) ITST[7] = ITST[7] - 100*ITFD(ITST[7]) ;
            if (IFD(IVEN[27]) == 0) return "MMSB_250"
            	ITST[7] += 100;
            	IBT = ITFD(ITST[7]);
            if (((IR < 28) || (IR > 53)) && (IVEN[27] > 21000)) myPrintf("\nYour torch burned out") ;
            if ((((IR < 28) || (IR > 53)) && (IFD(IVEN[27]) == 1))) IVEN[27] -= 1000;
            if (IBT < 21) return "MMSB_250"
            if ((IBT < 25) && (IVEN[27] > 21000)) myPrintf("\nYour torch is flickering and getting dim") ;
            if ((IBT == 25) && (IVEN[27] > 21000)) myPrintf("\nYour torch burned out") ;
            if ((IBT == 25) && (IFD(IVEN[27]) == 1)) IVEN[27] -= 1000;
                return "MMSB_250"
            }
            "MMSB_250" -> {
            	IXT[16] = (IXT[16]/10000)*10000+2724;
            	IXT[57]=0;
            	IVEN[68]=27;
            	IC=ITST5-(ITST5/25)*25;
            if (IC > 21) IXT[16] = (IXT[16]/10000) *10000+8824;
            if (IC < 1) IXT[57]=8988;
            if (IC > 21) IVEN[68]=88;
            	IXT[271]=0;
            if (IVEN[16] == 519) IXT[271]=8719;
            for (I in (1).toInt()..(17).toInt()) { this.I = I; 
            		RND=RND*7.7-(RND*7.7).toInt();
            if ((IFD(IRES[I]) == 1) && (RND < 0.3)) IRES[I] -= 1000;
            if ((IFD(IRES[I]) == 2) && (RND < 0.3)) IRES[I] -= 1000;
            if ((IFD(IRES[I]) == 2) && (RND > 0.7)) IRES[I] += 1000;
            if ((IFD(IRES[I]) == 9) && (RND < 0.3)) IRES[I] += 1000;
            if ((IFD(IRES[I]) == 8) && (RND < 0.3)) IRES[I] += 1000;
            if ((IFD(IRES[I]) == 8) && (RND > 0.7)) IRES[I] -= 1000;
            	}
            if ((ITST[31] == 1) && (RND < 0.3)) ITST[31]=0;
            if ((ITST[31] == 2) && (RND < 0.3)) ITST[31]=1;
            if ((ITST[31] == 2) && (RND > 0.7)) ITST[31]=3;
                        if ((ITST[8] == 0) && (IR == IFSD(IRES[ITST[9]])) && (IRES[ITST[9]] < 10000) && ((IVEN[10] == 10200+ITST[9]) || (IVEN[11] == 10300+ITST[9]) || (IVEN[12] == 10300+ITST[9])) && ((ITST5-1) != ITST[13])) {
            		IPR[2] = 251;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            if ((IFSD(IRES[13]) != IR) || (ITST[13] == (ITST5-1)) || (IRES[13] > 10000)) return "MMSB_253"
            	R=RN(R);
            if ((R < 0.8) && (IVEN[34] == 17213)) myPrintf("\nThe dwarf threw a hatchet at you and missed. He cursed, ran around\nYou and picked it up") ;
            	if ((R > 0.8) && (IVEN[34] == 17213)) {
            		IPR[2] = 254;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            // /*
            // * IWRD transformations can stop here
            // */
                return "MMSB_253"
            }
            "MMSB_253" -> {
            if ((IR == 92) && (IRES[16] == 0)) IRES[16]=995;
            	R=RN(R);
            if ((IFSD(IRES[16]) != IR) || (IRES[16] > 1999) || ((IWRD[0][9] != 22) && (R > 0.33))) return "MMSB_260"
            	IWRD[0][10] = 2;
            	IWRD[0][9] = 46;
            	IWRD[1][0] = 'W'.toInt();
            	IWRD[1][1] = 'A'.toInt();
            	IWRD[1][2] = 'R'.toInt();
            	IWRD[1][3] = 'R'.toInt();
            	IWRD[1][4] = 'I'.toInt();
            	IWRD[1][5] = 'O'.toInt();
            	IWRD[1][6] = 'R'.toInt();
            	IWRD[1][8] = 4;
            	IWRD[1][9] = 16;
            	myPrintf("\nThe warrior just gave a fearsome yell and attacked.");
            	IPR[2] = 0;
            	IPR[3] = 0;
            	return "MMSH"
                return "MMSB_260"
            }
            "MMSB_260" -> {
                        if ((IFSD(IRES[5]) == IR) && (IVEN[5] < 20000) && (ITST[13] != (ITST5-1))) {
            		IPR[2] = 262;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            if ((IRES[5] == 300) && (ITST5 >= 50)) IRES[5]=5;
            if ((IFSD(IRES[11]) != IR) || (ITST[13] == (ITST5-1)) || (IRES[11] > 10000)) return "MMSB_270"
            	R=RN(R);
            	IC=0;
            for (J in (R.toInt()*52+1).toInt()..(52).toInt()) { this.J = J; 
            if (IVEN[J] > 20000) IC=J;
            if (IC != 0) return "MMSB_264"
            	}
            for (J in (1).toInt()..(R.toInt()*52+1).toInt()) { this.J = J; 
            if (IVEN[J] > 20000) IC=J;
            if (IC != 0) return "MMSB_264"
            	}
            	return "MMSB_266"
                return "MMSB_264"
            }
            "MMSB_264" -> {
            for (J in (1).toInt()..(52).toInt()) { this.J = J; 
            if (((IVEN[J]/10000) == 1) && (IFSD(IVEN[J]) == 11)) IVEN[J] = IVEN[J]-10011+79;
            	}
                return "MMSB_266"
            }
            "MMSB_266" -> {
            if (IC != 0) IVEN[IC]=IVEN[IC]-10000+11;
            if (IR < 62) IRES[11]=870;
            if (IR > 61) IRES[11]=854;
            if (IC == 0) myPrintf("\nThe elf kicked you in the shin and ran off") ;
            	if (IC != 0) {
            		myPrintf("\nThe elf ran off after stealing your ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(ITEM[IC][L]);
            	}
                return "MMSB_270"
            }
            "MMSB_270" -> {
            if ((IRES[ITST[39]] > 10000) || (IRES[ITST[39]] == 0) || (ITST[5] < 300)) return "MMSB_280"
            if ((IROM[IFSD(IRES[ITST[39]])] < 20000) && (IFSD(IRES[ITST[39]]) < 54)) return "MMSB_280"
            	IRES[10]=IRES[ITST[39]];
            	IRES[ITST[39]]=0;
            	if (IR == IFSD(IRES[10])) {
            		myPrintf("\nIn a matter of seconds and before you can do anything, the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[ITST[39]][L]);
            		myPrintf("\nGrows hair, fangs and claws grotesquely and transforms into a werewolf.");
            	}
                return "MMSB_280"
            }
            "MMSB_280" -> {
                        if ((IR == IFSD(IRES[10])) && ((ITST5-1) != ITST[13]) && (IRES[10] < 10000)) {
            		IPR[2] = 285;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            	R=RN(R);
            	IC=((R*3).toInt()).toInt();
            	R=RN(R);
            	ID=((R*8+2).toInt()).toInt();
            	if ((ITD(IVEN[35]) == 5) && (IC != 0) && (IWRD[0][9] == 1)) {
            		IPR[2]=287;
            		IPR[3]=ID;
            		return "MMSD"
            	}
            if ((IR == 52) && (IFSD(IRES[4]) == 52)) IXT[258]=0;
            if ((IVEN[53] > 20000) || (IR == 1) || (IR == 20) || (IR == 25)) return "MMSB_295"
            for (I in (2).toInt() until (17).toInt()) { this.I = I; 
            if ((I == 4) || (I == 15) || (IFSD(IRES[I]) != IR)) continue;
            		myPrintf("\nYou noticed that you were being laughed at by the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[I][L]);
            		myPutchar('\n');
            		return "MMSB_295"
            	}
                return "MMSB_295"
            }
            "MMSB_295" -> {
                        if ((IWRD[0][9] == 21) || (IWRD[0][9] == 22) || (IWRD[0][9] == 24) || (IWRD[0][9] == 25) || (IWRD[0][9] == 26) || (IWRD[0][9] == 27)) {
            		MMRLret=0;
            		return "MMRL"
            	}
                return "RLret0"
            }
            "MMSB_9999" -> {
            	IPR[2]=11111;
            	IPR[3]=0;
            	return "MMSD"
                return "MMSC"
            }
        }
        return "EXIT"
    }
    private fun stepRLret0(lbl: String): String {
        when (lbl) {
            "RLret0" -> {
            	IWRD[0][10] = INUM;
            if ((INUM == 1) && (IWRD[0][9] == 25)) return "MMSC"
            if (IWRD[0][9] < 18) return "MMSC"
            if ((IWRD[0][9] > 17) && (IWRD[0][9] < 36)) return "MMSF"
            if ((IWRD[0][9] > 35) && (IWRD[0][9] < 54)) return "MMSH"
            if (IWRD[0][9] > 53) return "MMSJ"
                return "MMSB_9999"
            }
        }
        return "EXIT"
    }
    private fun stepMMSC(lbl: String): String {
        when (lbl) {
            "MMSC" -> {
            	if ((ITST[5] > 300) && (IWRD[0][9] > 3) && (IWRD[0][9] < 12)) {
            		IPR[2]=290;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if ((IWRD[0][8] == 1) && (IWRD[0][9] == 25)) return "MMSC_1142"
            if (IWRD[0][9] == 1) return "MMSC_1000"
            if (IWRD[0][9] == 2) return "MMSC_300"
            if (IWRD[0][9] == 3) return "MMSC_400"
            if (IWRD[0][9] == 4) return "MMSC_600"
            if (IWRD[0][9] == 5) return "MMSC_500"
            if (IWRD[0][9] == 6) return "MMSC_700"
            if (IWRD[0][9] == 7) return "MMSC_600"
            if (IWRD[0][9] == 8) return "MMSC_600"
            if (IWRD[0][9] == 9) return "MMSC_500"
            if (IWRD[0][9] == 10) return "MMSC_700"
            if (IWRD[0][9] == 11) return "MMSC_600"
            if (IWRD[0][9] == 12) return "MMSC_900"
            if (IWRD[0][9] == 13) return "MMSC_970"
            if (IWRD[0][9] == 14) return "MMSC_950"
                return "MMSC_300"
            }
            "MMSC_300" -> {
            if (IWRD[0][10] != 1) return "MMSC_310"
            	IWRD[0][10] = 2;
            	IWRD[1][8] = 2;
            	IWRD[1][9] = 2;
            	return "MMSC_1000"
                return "MMSC_310"
            }
            "MMSC_310" -> {
            if (IWRD[0][10] == 2) return "MMSC_330"
            	if ((IWRD[0][10] > 3) || (IWRD[1][8] != 5) || (IWRD[1][9] != 5)) {
            		IPR[2]=320;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IWRD[1][8] = IWRD[2][8];
            	IWRD[1][9] = IWRD[2][9];
                return "MMSC_330"
            }
            "MMSC_330" -> {
            	if (IWRD[1][8] != 3) {
            		IPR[2]=320;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if ((IVEN[15] < 20000) || (IWRD[1][9] != 77) || ((IR != 93) && (IR != 94) && (IR != 95) && (IR != 96) && (IR != 70) && (IR != 66) && (IR != 67) && (IR != 92))) return "MMSC_335"
            	IRB=ITST[1];
            	ITST[14]=IRB;
            if (ITST[1] == 93) IR=92;
            if (ITST[1] == 94) IR=66;
            if (ITST[1] == 95) IR=70;
            if (ITST[1] == 96) IR=67;
            if (ITST[1] == 67) IR=96;
            if (ITST[1] == 70) IR=95;
            if (ITST[1] == 66) IR=94;
            if (ITST[1] == 92) IR=93;
            	ITST[1]=IR;
            	return "MMSC_1141"
                return "MMSC_335"
            }
            "MMSC_335" -> {
                        if ((IWRD[1][9] == 77) && ((IR == 66) || (IR == 67) || (IR == 70) || (IR == 92) || (IR == 93) || (IR == 94) || (IR == 95) || (IR == 96))) {
            		IPR[2]=340;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (IWRD[1][9] == 77) {
            		IPR[2]=350;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if ((IWRD[1][9] == 78) && (IR != 97) && (IR != 98)) {
            		IPR[2]=360;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (IWRD[1][9] == 78) {
            		IPR[2]=370;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if ((IWRD[1][9] == 93) && (IR != 93)) {
            		IPR[2]=390;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (IWRD[1][9] == 93) {
            		IPR[2]=385;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IPR[2]=320;
            	IPR[3]=0;
            	return "MMSD"
                return "MMSC_400"
            }
            "MMSC_400" -> {
            	if ((IWRD[0][10] > 1) && (IWRD[1][8] != 3) && (IWRD[1][9] != 93)) {
            		IPR[2]=320;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IWRD[0][10]=2;
            	IWRD[0][9]=1;
            	IWRD[0][8]=2;
            if ((IR == 93) || (IR == 91)) IWRD[1][9]=1;
            if (IR == 95) return "MMSC_1000"
            	IPR[2]=410;
            	IPR[3]=0;
            	return "MMSD"
                return "MMSC_500"
            }
            "MMSC_500" -> {
            if (IR == 17) return "MMSC_800"
            	if ((IR < 55) || (IR > 83)) {
            		IPR[2]=520;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	ITST[1]=54;
            	IR=54;
            	myPrintf("\nYou are enveloped in a cloud of smoke that quickly disappears\n");
            	return "MMSC_1141"
                return "MMSC_600"
            }
            "MMSC_600" -> {
            	if (IR == 98) {
            		IPR[2]=520;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	ITST[1]=98;
            	IR=98;
            	myPrintf("\nYou are enveloped in a cloud of smoke that quickly disappears\n");
            	return "MMSC_1141"
                return "MMSC_700"
            }
            "MMSC_700" -> {
            if (IR == 17) return "MMSC_800"
            	if ((IR < 54) || (IR > 83)) {
            		IPR[2]=520;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if (IWRD[0][9] == 6) IR=81;
            if (IWRD[0][9] == 10) IR=82;
            	ITST[1]=IR;
            	myPrintf("\nYou are enveloped in a cloud of smoke that quickly disappears\n");
            	return "MMSC_1141"
                return "MMSC_800"
            }
            "MMSC_800" -> {
                        if (((IWRD[0][9] == 5) && (IVEN[88] != 17)) || ((IWRD[0][9] == 6) && (IVEN[88] != 117)) || ((IWRD[0][9] == 9) && (IVEN[88] != 217)) || ((IWRD[0][9] == 10) && (IVEN[88] != 317))) {
            		IPR[2]=520;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (IVEN[88] == 1017) {
            		IPR[2]=520;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IVEN[88] += 100;
            	if (IVEN[88] == 417) {
            		IPR[2]=810;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IPR[2]=1040;
            	IPR[3]=0;
            	return "MMSD"
                return "MMSC_900"
            }
            "MMSC_900" -> {
            	if (IWRD[0][10] != 1) {
            		IPR[2]=320;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if ((IR < 28) || ((IR > 80) && (IR < 97))) {
            		IPR[2]=910;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (IVEN[32] < 20000) {
            		IPR[2]=920;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	R = RN(R);
                        if ((IR < 62) || ((IR > 80) && (IR < 97)) || ((IR > 55) && (IR < 78)) || (R < 0.667)) {
            		IPR[2]=1040;
            		IPR[3]=0;
            		return "MMSD"
            	}
            // /*
            // * Hmm.  Note that some mapping changes here will never happen.
            // * This bug comes straight from the original... should we fix it?
            // * Leaving it here for now.
            // */
            if (IR == 99) IR = 28;
            if (IR == 98) IR = 33;
            if (IR == 97) IR = 34;
            if (IR == 62) IR = 43;
            if (IR == 62) IXT[341]=4362;
            if (IR == 63) IR=45;
            if (IR == 63) IXT[342]=4563;
            if (IR == 64) IR=48;
            if (IR == 64) IXT[343]=4864;
            if (IR == 65) IR=50;
            if (IR == 65) IXT[344]=5065;
            if (IR == 78) IR=29;
            if (IR == 78) IXT[345]=2978;
            if (IR == 79) IR=33;
            if (IR == 79) IXT[346]=3379;
            if (IR == 80) IR=29;
            if (IR == 80) IXT[347]=2980;
            	ITST[1]=IR;
            	ITST[14]=IR;
            	myPrintf("\nYou dug and fell into an underground tunnel");
            	return "MMSC_1141"
                return "MMSC_950"
            }
            "MMSC_950" -> {
            	ITST[22] += 1;
            	if (ITST[38] != 0) {
            		IPR[2]=0;
            		IPR[3]=0;
            		return "MMSB"
            	}
            	if (ITST[22] == 1) {
            		IPR[2]=960;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IPR[2]=970;
            	IPR[3]=0;
            	return "MMSD"
                return "MMSC_970"
            }
            "MMSC_970" -> {
            	if ((IWRD[1][8] != 4) || (IWRD[0][10] != 10)) {
            		IPR[2]=320;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (IFSD(IRES[IWRD[1][9]]) != IR) {
            		IPR[2]=980;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	/* goto MMSC_1105 */
                return "MMSC_1000"
            }
            "MMSC_1000" -> {
            	if (IWRD[0][10] > 2) {
            		IPR[2]=320;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IC = IWRD[1][9];
            if (IWRD[0][10] == 1) IC = 9;
            	IRX=0;
            if ((IWRD[1][9] == 110) && ((IR == 97) || (IR == 98))) IRX = 84;
            	if (IRX==84) {
            		IR=84;
            		ITST[1]=84;
            		return "MMSC_1025"
            	}
            	if ((IWRD[0][10]==2) && (IWRD[1][8] != 2)) {
            		IPR[2]=1050;
            		IPR[3]=0;
            		return "MMSD"
            	}
                        if ((IC == 11) &&  (((ITST[5]-1) != (ITST[4]/1000)*1000) || (IR == 97) || (IR == 98))) {
            		IPR[2]=1002;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (IC==11) {
            		ITST[1]=ITST[14];
            		ITST[14]=IR;
            		IR=ITST[1];
            		return "MMSC_1025"
            	}
                        if ((IC < 5) && (((IR < 54) && (IR != 35)) || (IR == 98)) && (IVEN[6] < 20000)) {
            		IPR[2]=1005;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if (IC < 7) return "MMSC_1007"
            	IC = IC-9+ITST[4]/1000;
            if (IC > 4) IC -= 4;
            if (IC < 1) IC += 4;
                return "MMSC_1007"
            }
            "MMSC_1007" -> {
            	IFC = ITST[4]/1000;
            if (IC < 5) ITST[4] = ITST[4] - (ITST[4]/1000) *1000 + IC*1000;
            for (J in (IC*60 - 59).toInt()..((IC*60)).toInt()) { this.J = J; 
            if (IFSD(IXT[J]) == IR) return "MMSC_1015"
            	}
            	IPR[2]=1105;
            	IPR[3]=0;
            	return "MMSD"
                return "MMSC_1015"
            }
            "MMSC_1015" -> {
            if ((J != 93) && (J != 212) && (J != 281) && (J != 351)) return "MMSC_1020"
            if (IR < 88) return "MMSC_1020"
            for (K in (1).toInt()..(53).toInt())
            		if ((IVEN[K] > 20000) && ((J == 281) || (ITD(IVEN[K]) > 1))) {
            			IPR[2]=1017;
            			IPR[3]=0;
            			return "MMSD"
            		}
                return "MMSC_1020"
            }
            "MMSC_1020" -> {
            	if (((IR < 55) || (IR == 90)) && (IXT[J] > 20000)) {
            		IPR[2]=1021;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IX=0;
            if ((IR == 91) && ((ITST[5]-1) != ITST[13])) IX=99;
            	if (IX == 99) {
            		ITST[1]=2;
            		IR=2;
            		myPrintf("\nYou just fell through the loose boards.");
            		return "MMSC_1025"
            	}
            	R=RN(R);
            if ((R > 0.5) || (IR != 39)) return "MMSC_1024"
            if (R > 0.25) IR=41;
            	ITST[1]=IR;
            	myPrintf("\nYou slipped on the ladder.");
            	return "MMSC_1025"
                return "MMSC_1024"
            }
            "MMSC_1024" -> {
            	if ((IR > 54) && (IXT[J] > 20000)) {
            		IPR[2]=1023;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	IRB=ITST[1];
            	ITST[14]=IRB;
            	ITST[1]=ITFD(IXT[J]);
            	IR=ITST[1];
                return "MMSC_1025"
            }
            "MMSC_1025" -> {
            	ITST[4]=(ITST[4]/1000)*1000+ITST[5];
            	if (IR == 0) {
            		IPR[2]=1026;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if ((IR == 46) && (IXT[320] == 0)) {
            		IPR[2]=1027;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if ((IVEN[22]==0) && ((ITST[5]-(ITST[5]/50)*50) < 10) && (IR != 35)) IVEN[22] = 100+IR;
            if ((IVEN[22] != 0) && (IVEN[22] < 20000) && ((ITST[5]-(ITST[5]/50)*50) > 40) && (IFSD(IVEN[22]) != IR)) IVEN[22]=0;
            if ((((IR != 97) && (IR != 98)) || (IWRD[0][9] == 25))) return "MMSC_1045"
            for (I in (1).toInt()..(2).toInt()) { this.I = I; 
            		R=RN(R);
            for (J in (1).toInt()..((R*10+10).toInt()).toInt()) { this.J = J; 
            			R1=RN(R);
            			IC=(IXT[(60*(R1*4).toInt()+58+I).toInt()]).toInt();
            if ((IR==98) && (ITST[17] > 1) && (IC==9798)) IC=3598;
            if ((IR==98) && (ITST[17] < 2) && (IC==3598)) IC=9798;
            			R=RN(R1);
            			IXT[(60*(R1*4).toInt()+58+I).toInt()]=IXT[(60*(R*4).toInt()+58+I).toInt()];
            			IXT[(60*(R*4).toInt()+58+I).toInt()]=IC;
            		}
            	}
            for (J in (1).toInt()..(35).toInt()) { this.J = J; 
            if ((IVEN[J] >= 10000) || ((IFSD(IVEN[J]) != IR) && (IFSD(IVEN[J]) != 0)) || IVEN[J] == 0) continue;
            if (IFSD(IVEN[J]) == IR) IVEN[J] = IVEN[J] - IR;
            if (IFSD(IVEN[J]) > 0) continue;
            		R=RN(R);
            if (R > 0.1) continue;
            		IVEN[J] += IR;
            	}
            if ((IXT[59] == 9497) && (IXT[179] == 9897)) IXT[59]=9597;
            if ((IXT[119] == 9297) || (IXT[119] == 9597)) IXT[119]=9497;
            if ((IXT[179] == 9497) && (IXT[59] == 9997)) IXT[179]=9297;
            if ((IXT[239] == 9297) || (IXT[239] == 9597)) IXT[239]=9497;
            if ((IRES[6] == 893) && (IRES[12] == 993)) IXT[54]=29293;
            if ((IRES[6] == 892) && (IRES[12] == 992)) IXT[54]=29293;
            if (IXT[54] == 29293) IXT[234]=29392;
            	R=RN(R);
            if (R > 0.2) return "MMSC_1045"
            	R=RN(R);
            for (J in ((R*35).toInt()+1).toInt()..(35).toInt()) { this.J = J; 
            if (J == 22) continue;
            if ((IVEN[J] > 10000) || (IFSD(IVEN[J]) != 0) || (IVEN[J] == 0)) continue;
            		myPrintf("\nA wood nymph ran by carrying ");
            		MMRI(4, J);
            		return "MMSC_1045"
            	}
                return "MMSC_1045"
            }
            "MMSC_1045" -> {
            	IC=1;
            if ((IR < 42) || (IR > 53) || (IRES[4] >= 10000) || (IFSD(IRES[4]) == 52)) return "MMSC_1080"
            	IMZ[1]=0;
            	IMZ[2]=0;
            	IMZ[3]=0;
            	IC=0;
            	RND=RND*7.7-floor(RND*7.7);
            	IX=((3*RND+1).toInt()).toInt();
            for (I in (1).toInt()..(360).toInt()) { this.I = I; 
            if ((IFSD(IXT[I]) != IFSD(IRES[4])) || (ITFD(IXT[I]) < 42) || (ITFD(IXT[I]) > 51)) continue;
            		IC++; IMZ[IC]=I;
            if (IC == IX) break;
            		return "MMSC_1080"
            	}
            	RND=RND*7.7-(RND*7.7).toInt();
            	IX=((360*RND+1).toInt()).toInt();
            for (J in (IX).toInt()..(360).toInt()) { this.J = J; 
            if ((IXT[J] < 4100) || (IFSD(IXT[J]) != 0)) continue;
            if ( (((IMZ[1]-1)/60) == ((J-1)/60)) || (((IMZ[2]-1)/60) == ((J-1)/60)) || (((IMZ[3]-1)/60) == ((J-1)/60)) ) continue;
            		IXX=IXT[J];
            		IXT[J]=IXT[I];
            		IXT[I]=IXX;
            		return "MMSC_1075"
            	}
            for (J in (1).toInt()..(IX).toInt()) { this.J = J; 
            if ((IXT[J] < 4100) || (IFSD(J) != 0)) continue;
            for (K in (1).toInt()..(3).toInt()) { this.K = K; 
            if ( (((IMZ[1]-1)/60) == ((J-1)/60)) || (((IMZ[2]-1)/60) == ((J-1)/60)) || (((IMZ[3]-1)/60) == ((J-1)/60)) ) continue;
            			IXX=IXT[J];
            			IXT[J]=IXT[I];
            			IXT[I]=IXX;
            			return "MMSC_1075"
            		}
            	}
            	return "MMSC_1080"
                return "MMSC_1075"
            }
            "MMSC_1075" -> {
            	RND=RND*7.7-(RND*7.7).toInt();
            if ((IC < 2) || (RND > 0.3)) return "MMSC_1080"
            for (I in (1).toInt()..(360).toInt()) { this.I = I; 
            if ( ((IFSD(IXT[I])) == (ITFD(IXT[J]))) && ((ITFD(IXT[I])) == (IFSD(IXT[J])))) IXT[I]=100*(IXT[I]/100) ;
            	}
            	IXT[J]=100*(IXT[J]/100);
                return "MMSC_1080"
            }
            "MMSC_1080" -> {
            if ((IR > 35) && (IR < 41)) ITST[12]=ITST[12]-ITST[5]+ITST[13];
            if ((IR > 35) && (IR < 41) && (ITST[12] > 0)) myPrintf("\nThe corridor walls are closing in\n") ;
                        if ((IR > 35) && (IR < 40) && (IVEN[17] > 20000) && (ITST[12] <= 0)) {
            		IPR[2]=1084;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if ((IR == 40) && (ITST[12] <= 0)) {
            		IPR[2]=1086;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if ((IR > 35) && (IR < 40) && (ITST[12] <= 0)) {
            		IPR[2]=1088;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if ((IR == 4) || (IR == 41)) myPrintf("\nThe corridor walls have opened\n") ;
            if ((IR == 4) || (IR == 41)) ITST[12]=4;
            if (IR == 4) IXT[140]=3640;
            if (IR == 4) IXT[314]=3940;
            if ((ITST[5] <= 300) || (IR < 55) || (IR > 82)) return "MMSC_1097"
            	R=RN(R);
            if ((R > 0.5) && (R < 0.8)) myPrintf("\nYou just noticed a snake slither by.") ;
            if ((R > 0.8) && (R < 0.95)) myPrintf("\nYou were just bitten by a snake that raced by.") ;
            	if (R > 0.95) {
            		IPR[2]=1093;
            		IPR[3]=0;
            		return "MMSD"
            	}
                return "MMSC_1097"
            }
            "MMSC_1097" -> {
            if (((IR == 71) || (IR == 72)) && (IRES[15] < 10000)) myPrintf("\nA wolf is running towards you") ;
                        if (((IR == 71) || (IR == 72)) && (IVEN[24] < 20000) && (IRES[15] < 10000)) {
            		IPR[2]=1097;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if (((IR == 71) || (IR == 72)) && (IRES[15] < 10000)) {
            		IPR[2]=1096;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if (IR != 1) IXT[1]=0;
            if (IR != 5) IXT[183]=0;
            if (IR != 7) IXT[123]=0;
            if (IR != 10) IXT[5]=0;
            if (IR != 14) IXT[187]=0;
            if (IR != 16) IXT[128]=0;
            if (IR != 19) IXT[11]=0;
            if (IR != 23) IXT[191]=0;
            if (IR != 25) IXT[134]=0;
            if (IR == 93) ITST[17]=0;
            if ((IR == 35) && (IWRD[1][9] == 3)) ITST[17] += 1;
            if ((IR == 35) && (IWRD[1][9] == 1)) ITST[17] -= 1;
            if ((IR == 35) && (((IFC+IWRD[1][9]) == 10) || ((IFC+IWRD[1][9])==14))) ITST[17] -= 1;
            if ((IR == 35) && (((IFC+IWRD[1][9]) == 8) || ((IFC+IWRD[1][9])==12))) ITST[17] += 1;
            if (ITST[17] == 1) IXT[47]=9335;
            if (ITST[17] == 2) IXT[47]=3535;
            	IC = 100-ITST[17]/10;
            	ID = ITST[17]/10;
            if ((IR == 35) && ((ID.toDouble() - floor(ID.toDouble())) == 0.0) && (ITST[5] < 300)) myPrintf("\nYou passed a mileage sign that read: Big city %d   Mystery Mansion %d\n", IC, ID) ;
            	if ((IFD(IVEN[35]) == 2) && (ITD(IVEN[35]) == 6)) {
            		IVEN[35] -= 100;
            		IPR[2]=10210;
            		IPR[3]=0;
            		return "MMSG"
            	}
            for (J in (2).toInt()..(16).toInt()) { this.J = J; 
            if (J == 15) continue;
            if ((IFSD(IRES[J]) == 0) || (IRES[J] > 10000)) continue;
            if (IFSD(IRES[J]) == IRB) continue;
            // /* MMSC_1105: */
            		R=RN(R);
            for (K in ((R*6).toInt()*60+1).toInt()..((R*6).toInt()*60+60).toInt()) { this.K = K; 
            if (IFSD(IXT[K]) != IFSD(IRES[J])) continue;
            if (IXT[K] > 20000) break;
            			IRC=IFSD(IRES[J]);
            			IRES[J]=(IRES[J]/100)*100+ITFD(IXT[K]);
            			continue;
            		}
            		continue;
            // /* MMSC_1120: */
            		IRES[J]=IRES[J]-IRB+IR;
            		IRC=IRB;
            // /* MMSC_1130: */
            		IC=IRES[J]-(IRES[J]/100)*100;
            if (((J == 3) || (J == 14)) && ((IC ==26) && (((ITST[5] < 400) || (IFSD(J) != IR)) || (IC == 0)))) IRES[J]=100*(IRES[J]/100) +IRC;
            if ((J == 4) && ((IC > 51) || (IC < 43))) IRES[4]=100*(IRES[4]/100) +IRC;
            if ((J == 5) && ((IC == 16) || ((IROM[IC] >= 20000) && (ITST[5] < 300)) || (IC > 27) || (IC  == 21))) IRES[5]=100*(IRES[5]/100) +IRC;
            if (((J ==6) || (J == 12)) && ((IC < 92) || (IC == 98))) IRES[J]=100*(IRES[J]/100) +IRC;
            if (((J == 8) || (J == 2)) && ((IC < 9) || (IC > 77))) IRES[J]=100*(IRES[J]/100) +IRC;
            if (((J == 9) || (J == 7)) && (((IC > 18) && (IC < 54)) || (IC > 77))) IRES[J]=100*(IRES[J]/100) +IRC;
            if ((J == 10) && ((IC == 98) || (IC == 78) || (IC == 80) || (IC == 88) || (IC == 83) || (IC == 0))) IRES[10]=100*(IRES[10]/100) +IRC;
            if ((J == 13) && ((IC < 29) || (IC > 31))) IRES[13]=100*(IRES[13]/100) +IRC;
            if ((J == 11) && ((IC < 55) || (IC > 77))) IRES[11]=100*(IRES[11]/100) +IRC;
            if ((J == 16) && ((IC < 93) || (IC > 96))) IRES[16]=100*(IRES[16]/100) +IRC;
            	}
                return "MMSC_1141"
            }
            "MMSC_1141" -> {
            	ITST[13]=ITST[5];
            if (IWRD[0][9] == 13) IR=IFSD(IRES[IWRD[1][9]]) ;
            	ITST[1]=IR;
                        if ((IR == ITST[8]) && (IFSD(IRES[ITST[9]]) == IR) && ( (IVEN[ITST[10]] > 20000) || ((IFSD(IVEN[ITST[10]]) == IR) && (IVEN[ITST[10]] < 10000)))) {
            		IPR[2]=1145;
            		IPR[3]=0;
            		return "MMSD"
            	}
                return "MMSC_1142"
            }
            "MMSC_1142" -> {
            for (I in (1).toInt()..(3).toInt()) { this.I = I; 
            if (I == 1) K=4;
            if (I == 2) K=19;
            if (I == 3) K=27;
            if ((IFD(IVEN[K]) == 1) && (((IVEN[K] < 10000) && (IFSD(IVEN[K]) == IR)) || (IVEN[K] > 20000) || ((IVEN[K] > 10000) && (IVEN[K] < 20000) && (IFSD(IRES[IVEN[K]]) == IR)))) return "MMSC_1190"
            	}
            if ((IR == 10) && (IVEN[45] > 1000)) return "MMSC_1190"
            if ((IR == 14) && (IVEN[89] > 1000)) return "MMSC_1190"
            if (IVEN[31] > 21000) return "MMSC_1190"
            if ((IR > 53) && (ITST[5] < 300)) return "MMSC_1190"
            	if (IR > 53) {
            		IPR[2]=1150;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if ((IROM[IR] > 20000) && (ITST[5] < 300)) return "MMSC_1190"
            	if (IROM[IR] > 20000) {
            		IPR[2]=1150;
            		IPR[3]=0;
            		return "MMSD"
            	}
            	if ((ITST[5] < 300) && (IROM[IR] > 10000)) {
            		IPR[2]=1150;
            		IPR[3]=2;
            		return "MMSD"
            	}
            	IPR[2]=1150;
            	IPR[3]=0;
            	return "MMSD"
                return "MMSC_1190"
            }
            "MMSC_1190" -> {
            	ISC=ITFD(IROM[IR]);
            	ITST[2] = ITST[2]+ISC-50;
            	IROM[IR]=IROM[IR]-(ISC*100)+5000;
            if (ISC < 50) ITST[3]=ITST[3]-50+ISC;
            	IC=1;
            if ((IR == 35) || (IR == 54) || (IR == 71) || (IR == 72) || (IR == 81) || (IR == 82) || (IR == 85) || (IR == 94) || (IR == 96) || (IR == 86) || (IR == 92) || (IR == 91)) IC=2;
            if (((IR >= 73) && (IR <= 77)) || (IR == 99)) IC=3;
            if (((IR >= 55) && (IR <= 57)) || (IR == 93) || (IR == 95)) IC=4;
            	myPrintf("\nYou are %c%c the ", IPRP[IC][0], IPRP[IC][1]);
            for (L in (0).toInt() until (16).toInt())
            		myPutchar(IRNM[IFSD(IROM[IR])][L]);
                        if ((IVEN[19] > 21000) && ((IR == 21) || (IR == 36) || (IR == 54) || (IR == 92))) {
            		IPR[2]=213;
            		IPR[3]=0;
            		return "MMSD"
            	}
            if ((ITST[8] == IR) && (ITST[10] > 8) && (ITST[10] < 13)) myPrintf("\nThere is blood here") ;
            	MMRI(1, 0);
            	IC=0;
            for (J in (1).toInt()..(17).toInt()) { this.J = J; 
            if (IR != IFSD(IRES[J])) continue;
            		if (IC == 0) {
            			myPrintf("\n\nYou are here with: ");
            for (L in (0).toInt() until (8).toInt())
            				myPutchar(IRSN[J][L]);
            		}
            		if (IC == 1) {
            			myPrintf("\n                   ");
            for (L in (0).toInt() until (8).toInt())
            				myPutchar(IRSN[J][L]);
            		}
            		IC=1;
            	}
            if ((ITST[5] < 450) && ((IR < 28) || (IR > 64) || ((IR > 34) && (IR < 42)) || ((IR > 52) && (IR < 58)))) return "MMSC_1620"
            	J2=((R*240+1).toInt()).toInt();
                return "MMSC_1585"
            }
            "MMSC_1585" -> {
            for (J in (J2).toInt()..(360).toInt()) { this.J = J; 
            if (IFSD(IXT[J]) != IR) continue;
            		J1=(J-1)/60+1;
            		if (!(((IR > 53) && (IR != 98)) || (IVEN[6] > 20000) || (IR == 35) || (J1 > 4))) {
            			J1=9+J1-ITST[4]/1000;
            if (J1 > 10) J1=J1-4;
            if (J1 < 7) J1=J1+4;
            		}
            		myPrintf("\n\nThere is a way to go ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[J1][L]);
            		return "MMSC_1620"
            	}
            if (J == 1) return "MMSC_1620"
            	J2=1;
            	return "MMSC_1585"
                return "MMSC_1620"
            }
            "MMSC_1620" -> {
            	if ((ISC != 50) || (IWRD[0][9] == 25)) {
            		IPR[2]=IR;
            		IPR[3]=0;
            		return "MMSE"
            	}
            	IPR[2]=0;
            	IPR[3]=0;
            	return "MMSB"
                return "MMSD"
            }
        }
        return "EXIT"
    }
    private suspend fun stepMMSD(lbl: String): String {
        when (lbl) {
            "MMSD" -> {
            	IRIT = IPR[2];
            if (IRIT == 11111) return "MMSD_99999"
            if ((ITST[9] != 2) && (IRES[2] >= 10000)) ITST[24]=1;
            if ((ITST[9] != 6) && (IRES[6] >= 10000)) ITST[24]=1;
            if ((ITST[9] != 7) && (IRES[7] >= 10000)) ITST[24]=1;
            if ((ITST[9] != 8) && (IRES[8] >= 10000)) ITST[24]=1;
            if ((ITST[9] != 9) && (IRES[8] >= 10000)) ITST[24]=1;
            if ((ITST[9] != 12) && (IRES[12] >= 10000)) ITST[24]=1;
            if ((ITST[9] != 14) && (IRES[14] >= 10000)) ITST[24]=1;
            if (IRES[5] == 0) IRES[5] = 100;
            if ((ITD(IRES[5]) != 1) && (ITST[5] > 300)) ITST[3]=ITST[3]-75;
            if (IRIT == 6005) return "MMSD_80000"
            if (IRIT == 110) myPrintf("\nYou clumsy fool! You tripped and dropped everything.") ;
            if (IRIT == 150) myPrintf("\nA word is too long") ;
            if (IRIT == 170) myPrintf("\nThere are too many words") ;
            	if (IRIT == 160) {
            		myPrintf("\nMy input vocabulary doesn't include the word ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[IPR[3]][L]);
            	}
            if (IRIT == 165) myPrintf("\nWatch your language!") ;
            if (IRIT == 166) myPrintf("\nI will not answer any questions. I only respond to your actions.") ;
            	if (IRIT == 176) {
            		myPrintf("\nYou could not hold your breath any longer and drowned.\nYou should have gone back while you could.");
            		return "MMSD_80000"
            	}
            if (IRIT == 180) myPrintf("\nI expect to see a verb as the first word") ;
            if (IRIT != 185) return "MMSD_187"
            	myPrintf("\nYou hear an explosion and a low rumbling as if_var some of the passages under\nThe Mansion and grounds have caved-in.You are badly shaken and there is\nDestruction all around you. You can smell smoke.");
            for (J in (301).toInt()..(360).toInt())
            		IXT[J]=0;
            	IXT[173]=0;
            	IXT[26]=0;
            	IXT[198]=0;
            	IXT[137]=0;
            	IXT[46]=0;
            	IXT[237]=0;
            	IXT[197]=0;
                return "MMSD_187"
            }
            "MMSD_187" -> {
            	if (IRIT == 189) {
            		myPrintf("\nYou have suffocated from the smoke and your body is slowly being\nConsumed by flames as all of Mystery Mansion goes up in smoke.");
            		return "MMSD_90020"
            	}
            if (IRIT == 1017) myPrintf("\nYou cannot quite get through the small door.") ;
            if (IRIT == 1027) myPrintf("\nYou entered a tunnel filled over your head with the water rushing\nIn from the pressure of the incoming tide.") ;
            	if (IRIT == 1093) {
            		myPrintf("\nYou just stumbled into the middle of a bunch of poisonous snakes and\nMany of them bit you and are crawling all over your dead body.");
            		return "MMSD_80000"
            	}
            if (IRIT != 191) return "MMSD_1199"
            	myPrintf("\nYou have beaten the odds and have done the impossible. You have survived\nMystery Mansion and you can now see it going up in smoke before you.");
            	IC=0;
            for (I in (1).toInt()..(52).toInt())
            if (IVEN[I] > 20000) IC += IVAL[I];
            	myPrintf("\nYour score includes %d points for the items you have with you.", IC);
            	ITST[2] += IC;
            	if (IXT[52] == 0) {
            		ITST[2] += 10;
            		myPrintf("\nYour score also includes 10 points because the taxi is waiting.");
            	}
            if (ITST[2] > 999) ITST[2] = 999;
            if (IXT[52] != 0) myPrintf("\nNow you have to walk to the big city.") ;
            	return "MMSD_90020"
                return "MMSD_1199"
            }
            "MMSD_1199" -> {
            if (IRIT == 190) myPrintf("\nI cannot decipher two verb type words in one sentence") ;
            	if (IRIT == 192) {
            		myPrintf("\nI give up on you. You have gotten yourself into a situation I cannot get\nYou out of.");
            		return "MMSD_90020"
            	}
            	if (IRIT == 213) {
            		myPrintf("\nA slight breeze blew out your candle.");
            		IVEN[19]=20200;
            	}
            	if (IRIT == 244) {
            		myPrintf("\nThe bomb has exploded and blown you to bits");
            		return "MMSD_80000"
            	}
            	if (IRIT == 251) {
            		myPrintf("\nThe murderer was afraid you would call the police and has killed you.");
            		return "MMSD_80000"
            	}
            	if (IRIT == 254) {
            		myPrintf("\nThe dwarf just threw a hatchet at you and split your head open");
            		return "MMSD_80000"
            	}
            	if (IRIT == 262) {
            		myPrintf("\nThe Vampire has attacked and sucked the blood out of you");
            		return "MMSD_80000"
            	}
            	if (IRIT == 285) {
            		myPrintf("\nThe werewolf has attacked you and torn you to bits.");
            		return "MMSD_80000"
            	}
            if (IRIT == 287) myPrintf("\nThere are %d screaming demons in the way.", IPR[3]) ;
            if (IRIT == 290) myPrintf("\nAbsolutely nothing happens!") ;
            if (IRIT == 320) myPrintf("\nI cannot figure out what you are trying to say") ;
            if (IRIT == 340) myPrintf("\nThe wall is too high") ;
            if (IRIT == 350) myPrintf("\nI don't see a wall around here") ;
            if (IRIT == 360) myPrintf("\nYou have to be in the woods to climb trees") ;
            if (IRIT == 370) myPrintf("\nYou would probably just fall out of the tree. I lose more players\nLike that. So I cannot let you climb any trees.") ;
            if (IRIT == 380) myPrintf("\nThe gardener has caught you and forcefully, has taken you\nTo the veranda and has told you to leave if_var you are going to act\nLike that") ;
            if (IRIT == 385) myPrintf("\nThe gate is covered with all kinds of points and barbs and you\nWould never make it over alive.") ;
            if (IRIT == 390) myPrintf("\nYou are not by the gate.") ;
            if (IRIT == 410) myPrintf("\nI didn't expect you would use enter here.") ;
            if (IRIT == 510) myPrintf("\nYou are suddenly surrounded by smoke and when it clears you see\nThat you are on the veranda") ;
            if (IRIT == 520) myPrintf("\nNothing happens") ;
            if (IRIT == 610) myPrintf("\nYou are suddenly surrounded by smoke and when it clears you see\nThat you are in the woods") ;
            if (IRIT == 710) myPrintf("\nYou are suddenly surrounded by smoke and when it clears you see\nThat you are on a bridge") ;
            	if (IRIT == 810) {
            		myPrintf("\nThe scroll magically unrolls on the table and you read that the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[ITST[9]][L]);
            		myPrintf("\nIs the murderer and that the scene of crime is the ");
            for (L in (0).toInt() until (16).toInt())
            			myPutchar(IRNM[IROM[ITST[8]]][L]);
            		myPrintf("\nAnd that the murder weapon is the %s", IWP);
            	}
            if (IRIT == 910) myPrintf("\nYou cannot dig here.") ;
            if (IRIT == 920) myPrintf("\nYou won't get very far without a shovel.") ;
            if (IRIT == 960) myPrintf("\nLet that happen again and the game will abort") ;
            	if (IRIT == 970) {
            		myPrintf("\nGame aborted due to no input");
            		return "MMSD_90020"
            	}
            	if (IRIT == 980) {
            		myPrintf("\nFirst you have to find the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[(IWRD[1][9]).toInt()][L]);
            	}
            if (IRIT == 1002) myPrintf("\nI forgot where you were") ;
            if (IRIT == 1005) myPrintf("\nYou need a compass to tell cardinal points here") ;
            if (IRIT == 1021) myPrintf("\nThe door is locked") ;
            if (IRIT == 1023) myPrintf("\nThe gate is closed") ;
            	if (IRIT == 1026) {
            		myPrintf("\nYou just fell down the 500 foot cliff onto the rocks below.");
            		return "MMSD_80000"
            	}
            if (IRIT == 1032) return "MMSD_90060"
            if (IRIT == 1040) myPrintf("\nOkay.") ;
            	if (IRIT == 1050) {
            		myPrintf("\nYou cannot go ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 1065) myPrintf("\nProgram error at line %d", IPR[3]) ;
            	if (IRIT == 1084) {
            		myPrintf("\nThe amulet saved you from being crushed by the walls");
            		ITST[1]=4;
            	}
            	if (IRIT == 1086) {
            		myPrintf("\nThe closing corridor walls have blocked the exits and have trapped you here\n");
            		IXT[314]=0;
            		IXT[140]=0;
            	}
            	if (IRIT == 1088) {
            		myPrintf("\nThe closing walls have crushed you\n");
            		return "MMSD_80000"
            	}
            	if (IRIT == 1096) {
            		myPrintf("\nThe wolf attacked you and is busy eating your food");
            		IVEN[24]=0;
            		IRES[15]=ITST[1];
            	}
            	if (IRIT == 1097) {
            		myPrintf("\nThe wolf attacked you ravenously and killed you");
            		return "MMSD_80000"
            	}
            if (IRIT == 1105) myPrintf("\nYou cannot go that way.") ;
            	if (IRIT == 1145) {
            		myPrintf("\nCongratulations! You solved the Mystery by having the %s", IWP);
            		myPrintf("\nWhich is the murder weapon, at the scene of the crime with the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[ITST[9]][L]);
            		myPrintf("\nWho is the murderer");
            		ITST[2] += 200;
            		ITST[8]  = 0;
            	}
            	if (IRIT == 1150) {
            		myPrintf("\nIt is dark here\n");
            if (IPR[3] == 1) myPrintf("\nIt is after sunset") ;
            if (IPR[3] == 2) myPrintf("\nA curtain blocks the light") ;
            	}
            if (IRIT == 10030) return "MMSD_80000"
            	if (IRIT == 13010) {
            		myPrintf("\nThe poison did its job and you died in a fit of agony.");
            		return "MMSD_80000"
            	}
            // /*
            	/* goto MMSD_999 */
            // */
            	IRIT=0;
            	IPR[2] = 0; IPR[3] = 0;
            	return "MMSB"
                return "MMSD_80000"
            }
            "MMSD_80000" -> {
            	IC=0;
            if (IVEN[1] > 20000) IC=1;
            if (IVEN[18] > 20000) IC=18;
            if (IC != 0) IVEN[IC]=0;
            if ((ITST[2] < 100) && (IC == 0)) return "MMSD_90000"
                return "MMSD_80010"
            }
            "MMSD_80010" -> {
            	myPrintf("\nDo you want me to reincarnate you?  ");
            if (IC == 0) myPrintf("\nIt will cost you 100 points?  ") ;
            	else {
            		myPrintf("\nIt will cost you no points since you had the ");
            if (IC == 1) myPrintf("Ring.") ;
            if (IC == 18) myPrintf("Talisman.") ;
            	}
            	IANS = my_getchar();
            if (IANS > 'a'.toInt()) IANS -= ' '.toInt() ;
            if (IANS == 'N'.toInt()) return "MMSD_90020"
            if (IANS == 'Y'.toInt()) return "MMSD_80050"
            	myPrintf("\nWhat? Please answer yes or no");
            	return "MMSD_80010"
                return "MMSD_80050"
            }
            "MMSD_80050" -> {
            if (IVEN[4] > 20000) IVEN[4] = IVEN[4]-20000+193;
            if ((IVEN[4] == 493) && (ITST[5] > 295)) IVEN[4] = 1493;
            for (J in (1).toInt()..(52).toInt())
            if (IVEN[J] > 20000) IVEN[J] = IVEN[J]-20000+ITST[1];
            if (IRES[16] != 0) IRES[16] = IRES[16]-IFSD(IRES[16]) +95;
            	ITST[1] = 93;
            	if (IC == 0) {
            		ITST[2] -= 100;
            		ITST[3] -= 100;
            	}
            	myPrintf("\nOkay. I moved you out of harm. Enter look to see where you are.");
            	return "MMSB"
                return "MMSD_90000"
            }
            "MMSD_90000" -> {
            	myPrintf("\nYou don't have enough points to reincarnate");
                return "MMSD_90020"
            }
            "MMSD_90020" -> {
            	myPrintf("\n\nYou scored %d points which rates you as a ", ITST[2]);
            for (L in (0).toInt() until (8).toInt())
            		myPutchar(ICLS[1+ITST[2]/85][L]);
            	myPrintf("\nSleuth.");
            	ourtime(ITIM);
            	ITR=((60*ITIM[4]+ITIM[3]-60*(ITST[19]/100).toInt()-IFSD(ITST[19]))).toInt();
            if (ITR < 0) ITR += 1440;
            	TG = ITST[5]/25.00;
            if (ITR/ITST[5] <= 163) return "MMSD_90031"
            	IEF = 99;
            	return "MMSD_90037"
                return "MMSD_90031"
            }
            "MMSD_90031" -> {
            	IEF=200*ITR/ITST[5];
            if (IEF > 99) IEF=99;
                return "MMSD_90037"
            }
            "MMSD_90037" -> {
            	myPrintf("\n\nYou played %d minutes real time and %.1f hours game time or %d %% utilization.", ITR, TG, IEF);
            if ((IRIT != 192) && (ITST[2] < 700)) myPrintf("\nBetter luck next time.") ;
            if (IRIT == 192) myPrintf("\nHope you will play again real soon.") ;
                return "MMSD_90060"
            }
            "MMSD_90060" -> {
            	myPrintf("\nBye bye\n");
                return "MMSD_99999"
            }
            "MMSD_99999" -> {
            	pak();
            	return "EXIT"
                return "MMSE"
            }
            "MMSD_5100" -> {
            	if ((IVEN[IWRD[2][9]] < 20000) && (IWRD[2][9] < 54)) {
            		IPR[2] = 5110;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IXT[320] == 0) && ((IR == 51) || (IR == 53))) IVEN[79]=IR;
            	if ((IFSD(IVEN[IWRD[2][9]]) != IR) && (IWRD[2][9] > 53)) {
            		IPR[2] = 8030;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 30) {
            		IPR[2] = 5130;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 15) {
            		IPR[2] = 5140;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[2][9] != 28) return "MMSF_5200"
            	IC = 0; ID = 0;
            for (J in (1).toInt()..(360).toInt()) { this.J = J; 
            if (IFSD(IXT[J]) != IR) continue;
            if ((ITFD(IXT[J]) < 92) && (ITFD(IXT[J]) != 35) && (ITFD(IXT[J]) != 0)) continue;
            		IC = (J-1)/60+1;
            if (ID == 0) myPrintf("\nThe map shows that it is:") ;
            		ID=1;
            		if (ITFD(IXT[J]) != 0) {
            			myPutchar('\n');
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IDTN[IC][L]); }
            			myPrintf("To the ");
            for (L in (0).toInt() until (16).toInt())
            				myPutchar(IRNM[IFSD(IROM[ITFD(IXT[J])])][L]);
            		}
            		if (ITFD(IXT[J]) == 0) {
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IDTN[IC][L]); }
            			myPrintf("To the 500 foot sheer cliff");
            		}
            	}
            	if (IC == 0) {
            		IPR[2] = 5170;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IPR[2] = 0;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_5200"
            }
        }
        return "EXIT"
    }
    private suspend fun stepMMSE(lbl: String): String {
        when (lbl) {
            "MMSE" -> {
            	IR = IPR[2];
            	IFC = ITST[4]/1000;
            	IDN = 1;
            	IDE = 2;
            	IDS = 3;
            	IDW = 4;
            if (IVEN[6] < 20000) IDN = 10-IFC;
            if (IDN == 6) IDN=10;
            if (IVEN[6] < 20000) IDE = 11-IFC;
            if (IVEN[6] < 20000) IDS = 12-IFC;
            if (IDS == 11) IDS=7;
            if (IVEN[6] < 20000) IDW = 13-IFC;
            if (IDW > 10) IDW -= 4;
            	myPutchar('\n');
            	myPutchar('\n');
            	if ((ITST[5] > 450) && (IR < 84)) {
            		myPrintf("\nEverything is in ruin after the explosion and cave-in. Smoke is everywhere\nAnd getting thicker. Some things are still crashing down around you.");
            		return "MMSE_2000"
            	}
            	if (IR == 1) {
            		myPrintf("The room is furnished with just a cot, a stool and a bench with a\nBunch of bananas on it. There is a pet monkey tied in one corner.");
            if (IVEN[46] != 1101) myPrintf(" There is an empty hook for the dungeon key on the wall.") ;
            		else
            			myPrintf(" There is a dungeon key hanging from a hook on the wall.");
            		myPrintf(" If_var you cannot find any other way out you might be able to go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nOut the door.");
            	}
            	if (IR == 2) {
            		myPrintf("The furnace is warm and there is plenty of coal nearby.\nThere is soot everywhere with foot prints going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nAcross the room from door to door and also going ");
            for (J in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nOut another door.");
            	}
            	if (IR == 3) {
            		myPrintf("There is a stairway leading up and some well worn steps leading down into\nThe darkness where you can hear strange sounds. There is also a door leading\n");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            	}
            	if (IR == 4) {
            		myPrintf("There is an awkward stairway leading up to at least two other levels.\nYou can go any direction but down here");
            	}
            	if (IR == 5) {
            		myPrintf("There are many coffins here containing the remains of the ancestors of\nMystery Mansion. All the coffins appear to be undisturbed and sealed except\nFor one. Unless you can open a secret panel,you have to go");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            		myPrintf("\nTo get out of here.");
            	}
            	if (IR == 6) {
            		myPrintf("The wine racks are completely empty, probably finished by previous players.\nAll that is left is an empty barrel and broken bottles. A door going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDN][I]);
            		myPrintf("\nIs the only way out of here.");
            	}
            	if (IR == 7) {
            		myPrintf("The room is cluttered with mostly useless junk. There is a large interesting\nLooking crate here. You can go up a well worn wood ladder or ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nThrough a heavy wood door with a small window with iron bars.");
            if (IXT[320] != 0) myPrintf("\nThere are also some steps cut in the rock leading down.") ;
            		else
            			myPrintf("\nA cave-in blocks the steps leading down.");
            	}
            	if (IR == 8) {
            		myPrintf("The skeletal remains of two former players hang by shackles on the far\nWall. Between them is an empty set of shackles. You can hear the squeaking of\nSeveral rats as they scurry about. The only way out is ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            		myPrintf("\nWhere you can hear someone on the other side of the door they just closed");
            if (IXT[184] == 0) myPrintf(" And locked.") ;
            	}
            	if (IR == 9) {
            		myPrintf("It looks like the rats have gotten into most of the food. You can go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            		myPrintf("\nThrough a door labeled wine cellar or up the ladder you came down from the\nKitchen.");
            	}
            	if (IR == 10) {
            		myPrintf("It is a large and comfortable room, with a large oil lamp on a stand in\nOne corner. There is a large picture on one wall. Seems like going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nIs the only way out.");
            	}
            	if (IR == 11) {
            		myPrintf("There is an old crank type telephone on the wall by the front door.\nA large window over looks the grounds in front of the Mansion. There\nAre four doors so that you can go left, right, forward, or backward.");
            	}
            	if (IR == 12) {
            		myPrintf("The room is dilapidated and has the remains of various old style games such\nAs darts, duckpins, several card games and an old radio. There is a stairway\nGoing up and down, as well as a door you can exit by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            	}
            	if (IR == 13) {
            		myPrintf("There is an awkward stairway leading up and down to other levels.\nYou can go in any direction but ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            	}
            	if (IR == 14) {
            		myPrintf("The room is huge and empty. The slightest sound echoes throughout the\nHall. A large fireplace with some firewood by it is the only thing of\nInterest. There is a stairway leading up as well as a door on each side\nOf the room except for one going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            	}
            	if (IR == 15) {
            		myPrintf("You have to go around rows of tables to get anywhere. There\nAre hundreds of wooden indians, with drawn bows, standing on\nPlatforms mounted high on the walls all around the hall.\nThere are two interior doors you can use by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDN][I]);
            		myPrintf("\nOr ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            	}
            	IC = 9 - ITST[6]/3600;
            	if (IR == 16) {
            		myPrintf("There is an altar in front of the chapel with some communion wine on it.");
            if (IVEN[19] == 1216) myPrintf("\nThere is also a burning candle on altar about %d inches long.", IC) ;
            if (IVEN[5] == 1316) myPrintf("\nA silver cross about a foot long hangs on the wall behind the altar.") ;
            		else
            			myPrintf("\nYou can see the outline of the cross that use to hang on the wall.");
            		myPrintf("\nThere is a stairwell leading down into the darkness and a way to go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nThrough an open arch where you can see part of a library.");
            	}
            	if (IR == 17) {
            if (IVEN[88] != 1017) myPrintf("You are standing next to a small stand with an old papyrus scroll on it.") ;
            		else
            			myPrintf("You are next to the stand with the crumbled remains of the scroll on it.");
            		myPrintf("\nThere are rows of bookshelves where someone could hide. You can exit ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            		myPrintf("\nThrough an open arch where you can see part of a chapel, or ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDN][I]);
            		myPrintf("\nThrough one of the two matching doors at both ends of the library.");
            	}
            	if (IR == 18) {
            		myPrintf("It looks like a galley designed to fix meals for a large group of people.\nThere is a ladder going down and a door you can exit by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            	}
            	if (IR == 19) {
            		myPrintf("This is a small confining closet. It seems you can only go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nThrough the door which opens by itself when not locked. The walls,\nThe floors and even the ceiling look like they might have a secret\nPanel or a way through them.");
            	}
            	if (IR == 20) {
            		myPrintf("The room is very masculine. There are several large animal heads\nMounted on the walls as well as other things. You can go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            		myPrintf("\nInto a small closet, or you can exit either by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDN][I]);
            		myPrintf("\nTo the hallway or outside on the fire escape by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            	}
            	if (IR == 21) {
            		myPrintf("There is a large bell in the center of the tower with a knotted cord\nAttached to the clapper that you can't reach. You can go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDN][I]);
            		myPrintf("\nThrough a long sloping passage, or down a circular stairway, or ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            		myPrintf("\nOut a large window overlooking the ocean below.");
            	}
            	if (IR == 22) {
            		myPrintf("There is an awkward stairway leading down to at least two other levels.\nYou can go in any direction but up or ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            	}
            	if (IR == 23) {
            		myPrintf("There are many piles of dirt on the floor. You could go in any\nDirection but up. It is a very long hallway with a small window\nAt the far end you can see by looking ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDN][I]);
            	}
            	if (IR == 24) {
            		myPrintf("This is the bedroom of an older woman, with a sewing machine, a large\nMirror and a dress form, among other things. You can go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDN][I]);
            		myPrintf("\nInto a closet, or you can exit the room by going either ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            		myPrintf("\nOr ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            	}
            	if (IR == 25) {
            		myPrintf("The room is very feminine with frilly curtains. Right in front of ");
            if (IVEN[30] != 325) myPrintf("\nYou is a soft comfortable canopy bed with a small indentation right") ;
            		else if (IVEN[30] == 325)
            			myPrintf("\nYou is a soft comfortable canopy bed with the globe resting gently");
            		myPrintf("\nIn the middle. The only way you can see to exit is going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            	}
            	if (IR == 26) {
            		myPrintf("There is a small window here but it would get dark if_var you were to go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            		myPrintf("\nTo the other end of the hall. You can enter a door by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            		myPrintf("\nOr ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDE][I]);
            	}
            	if ((IR == 27) || (IR == 88)) {
            		myPrintf("This closet is large and featureless, except for a pile of rags in one\nCorner and a small door in the wall on the left as you entered. You\nCan see no other way out except the door going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            	}
            	if ((IR > 27) && (IR < 35)) {
            		myPrintf("You are in a maze of twisty little passages. The passages are low\nAnd you have to stoop to get around. There is evidence that somebody or\nSomething spends a lot of time here.");
            	}
            	if ((IR == 35) && (ITST[17] < 10)) {
            		myPrintf("The highway goes straight South as far as you can see between the dense\nForest to the West and a 500 foot sheer cliff to the East.");
            	}
            	IC=ITST[17]*200;
            if ((IR == 35) && (ITST[17] < 5)) myPrintf("\nThe Mansion gate is about %d yards to the North.",IC) ;
            if ((IR == 35) && (ITST[17] > 4) && (ITST[17] < 10)) myPrintf("\nYou can just see the gate to the North.") ;
            if ((IR == 35) && (ITST[17] >= 10)) myPrintf("\nIt goes straight North and South as far as you can see.") ;
            if (IR == 36) myPrintf("The walls and floor are damp and covered with a slimy kind of moss making\nIt slippery and slow going. The wall to the left as you look down the corridor\nSeems to be moving toward you and there is a door to the right. A cold\nBreeze blows out into the secret passage.") ;
            if (IR == 37) myPrintf("Almost all you can see are the slimy walls and floor. The corridor quickly\nTurns to a gloomy darkness going down but you can see the secret passage. It\nIs quiet except for an occasional drop of water dripping from the ceiling.") ;
            if (IR == 38) myPrintf("You must be over halfway down the corridor because you can barely see a\n Ladder at the lower end and the secret passage at the other end.") ;
            if (IR == 39) myPrintf("You are at the lower end of the corridor. A ladder leads up and down.\nThe rungs of the ladder are covered with the same slimy moss.") ;
            if (IR == 40) myPrintf("You are in a long passage parrallel to the corridor. The floor is clean\nAnd dry here, making it easy to get around; especially to the other end.") ;
            	if ((IR == 40) && (IXT[140] != 0)) {
            		myPrintf("\nYou can go down a ladder at the other end or you can go ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDS][I]);
            		myPrintf("\nOut the door next to you.");
            	}
            if ((IR == 40) && (IXT[140] == 0)) myPrintf("\nThe corridor walls have closed and blocked all the exits.") ;
            	if (IR == 41) {
            		myPrintf("You are in a large treasure room. It looks like there used to be a lot\nOf valuable articles here once. You notice a small cavity in the middle\nOf a drawing on the wall. There is a ladder going up, which is all slimy.");
            if (IXT[320] == 0) myPrintf(" There are steps carved in the sides of a huge shaft leading down.") ;
            	}
            	if ((IR > 41) && (IR < 52) && (IXT[320] != 0)) {
            		myPrintf("You are in a maze of large passages. Some of the passages look\nFreshly dug. There are large odd looking foot prints everywhere\nAnd a few bones here and there.");
            	}
            	if ((IR > 41) && (IR < 51) && (IXT[320] == 0)) {
            		myPrintf("You are in a cavern deep below the Mansion. A lot of work has\nGone into carving steps and tunnels in the rock walls and floor.\nThere are loose rocks everywhere.");
            	}
            	if ((IR == 52) && (IXT[320] != 0)) {
            		myPrintf("There are many bones scattered about, some of which look human.\nThere is a message written in blood on one wall that says the\nMurder was committed with the %s", IWP);
            	}
            	if ((IR > 50) && (IR < 54) && (IXT[320] == 0)) {
            		myPrintf("The room was carved out of solid rock a long long time ago.");
            	}
            	if (((IR == 51) || (IR == 53)) && (IXT[320] == 0)) {
            		myPrintf("There is a troll sleeping fitfully in one corner and is obviously\nToo large to fit down the only exit in the middle of the room.");
            	}
            	if ((IR == 51) && (IVEN[42] == 1151)) {
            		myPrintf(" It has a ruby necklace dangling from one hand and a bludgeon in the other.");
            	}
            	if ((IR == 51) && (IVEN[40] == 1151)) {
            		myPrintf(" Next to the troll is an animated statue of a beautiful naked woman that\nIs part bird and is singing peacefully and seductively while she entices\nYou with several diamonds cupped in her outstretched hands.");
            	}
            	if ((IR == 51) && (IXT[320] == 0)) {
            		myPrintf(" Across from the troll is a gold figurine of a bull on a pile of gold bars.");
            	}
            	if ((IR == 53) && (IVEN[39] == 1253)) {
            		myPrintf(" It has a silver goblet resting in one hand and a bludgeon in the other.");
            	}
            	if ((IR == 53) && (IVEN[37] == 1153)) {
            		myPrintf(" Next to the troll is a stone idol with a large emerald for one eye.");
            	}
            	if ((IR == 53) && (IXT[320] == 0)) {
            		myPrintf(" Across from the troll is a sacrificial stone slab with a dagger in it.");
            	}
            	if ((IR == 53) && (IXT[320] != 0)) {
            		myPrintf("This cave is above the domain of the giant mole. As long as you are\nNear, he will constantly change his tunnels trying to confuse you if_var he can.\nThere is a path leading up and down.");
            	}
            	if ((IR == 52) && (IXT[320] == 0)) {
            		myPrintf(" It is empty except for several boulders and some skeletons covered with\nCrabs. The floor is uneven, being full of craters.");
            	}
            	if ((IR == 52) && (IXT[320] == 0) && (IXT[319] != 0)) {
            		myPrintf("The only way out is down the tunnel that is in the middle of the den.");
            	}
            	if (IR == 54) {
            		myPrintf("A large garden with high hedges stretches around you. The main path\nGoes North with a oneway exit gate on each side. A sign says that\nThe garden closes at sunset for your safety.");
            	}
            	if (IR == 55) {
            		myPrintf("A sign says: Don't pick the flowers,no swimming,and stay off the grass.\nYou can go South to the veranda or East or West.");
            	}
            	if ((IR == 56) || (IR == 57)) {
            		myPrintf("A sign says: Hope you enjoyed your visit. You can go North into the garden\nOr you can go onto the veranda.");
            	}
            	if ((IR >= 58) && (IR <= 63)) {
            		myPrintf("High hedges block the view around you but you can see some of the\nGargoyles along the roof of the Mansion. There are paths going in several\nDirections.");
            	}
            	if ((IR == 64) || (IR == 65)) {
            		myPrintf("You are on the southern edge of a grassy meadow.");
            	}
            	if (IR == 66) {
            		myPrintf("You are on the western edge of the grassy meadow.");
            	}
            	if (IR == 67) {
            		myPrintf("You are on the eastern edge of the grassy meadow.The wall\nIs to the East but paths go every other way.");
            	}
            	if (IR == 68) {
            		myPrintf("You are on the northern edge of the grassy meadow and the western edge\nOf a pond. As you look up you can see the word ");
            for (L in (0).toInt() until (6).toInt())
            			myPutchar(IVRB[7][L]);
            		myPrintf("Written underneath\nA bridge crossing the pond.");
            	}
            	if (IR == 69) {
            		myPrintf("You are on the northern edge of a grassy meadow and the\nEastern edge of a pond. As you look up you can see the word\n");
            for (L in (0).toInt() until (6).toInt())
            			myPutchar(IVRB[11][L]);
            		myPrintf("Written underneath a bridge crossing the pond.");
            	}
            	if (IR == 70) {
            		myPrintf("You are on the northern shore of the pond. The wall is to the North.");
            	}
            	if ((IR == 71) || (IR == 72)) {
            		myPrintf("The grass is tall and there are many bushes around.");
            	}
            if ((IR > 63) && (IR < 73) && (IR != 67) && (IR != 70)) myPrintf("\nYou can go in any direction from here.") ;
            if ((IR == 73) || (IR == 74)) myPrintf("The fountain has not been used in years and is dry. There is a path in\nEach direction but North.") ;
            if (IR == 75) myPrintf("The well is deep and dark. Paths go East and West.") ;
            if (IR == 76) myPrintf("You are on the eastern edge of a grassy meadow. Paths go North and West.") ;
            if (IR == 77) myPrintf("You are on the western edge of a grassy meadow. Paths go North and East.") ;
                        if ((IRES[15] < 10000) && (((IR > 63) && (IR < 70)) || ((IR == 76)|| (IR ==77)))) {
            		myPrintf("\nAs you look out over the grassy meadow you can see something moving\nToward you in the grass.");
            	}
            if ((IR == 78) || (IR == 80)) myPrintf("The walls are slippery and you cannot get out.") ;
            if (IR == 79) myPrintf("It is dry here. The walls are made with uneven bricks you can easily climb.") ;
            	if ((IR == 81) || (IR == 82)) {
            		myPrintf("You are over a small pond. As you cross you can see the\nWord ");
            if (L in (0).toInt() until (6).toInt()) { this.L = L; myPutchar(IVRB[10][L]) ; }
            if (L in (0).toInt() until (6).toInt()) { this.L = L; myPutchar(IVRB[6][L]) ; }
            		myPrintf("Reflected in the water. The bridge goes North and South.");
            	}
            	if (IR == 83) {
            		myPrintf("You are waist deep in water. You can see two bridges with the words\n");
            for (L in (0).toInt() until (6).toInt())
            			myPutchar(IVRB[8][L]);
            		myPrintf("And ");
            for (L in (0).toInt() until (6).toInt())
            			myPutchar(IVRB[4][L]);
            		myPrintf("Written underneath them. The banks are steep and you\nCannot get out.");
            	}
            	if (IR == 84) {
            		myPrintf("You are standing in the middle of a small cottage on a plateau on\nThe side of the canyon. As you look around, you can see a\n");
            if (IVEN[28] == 1184) myPrintf("Small road map lightly tacked to one wall. Also, there are some\n") ;
            		else
            			myPrintf("Message on the wall where the map used to be that says: Go crazy\nIn the woods to get quick asylum here. Also, there are some\n");
            		myPrintf("Steps going out the back door to a path continuing up the\nEmbankment. There is also the way you came in, but that just\nLeads back down to the stream by the woods.");
            	}
            	if ((IR == 85) || (IR == 86)) {
            		myPrintf("It is made of rusty wrought iron. The steps are hinged so that once\nYou go down you cannot go back up. But the hinge has a habit of rusting\nSo it cannot move.");
            		if (IR == 86) {
            			myPrintf(" The ladder in the attic was destroyed when you climbed\nUp it and you cannot get back down it.");
            			IXT[265] = 0;
            			ITST[14] = 86;
            		}
            	}
            	if (IR == 87) {
            		myPrintf("You are in a dusty room thick with cobwebs. You must stoop to get around.");
            if (ITST[5] < 300) myPrintf("\nA small grate lets in just enough light to see.") ;
            if (IXT[265] != 0) myPrintf("\nThere is a half rotted ladder leading up to a fire escape.") ;
            	}
            if (IR == 89) myPrintf("It is the laboratory of the mad scientist. There are several pieces of\nEquipment here which are all humming ready to work. It sort of looks like\nThe transporter room out of a star trek movie. Large windows overlook the\nGrounds. There is a door to the South and a small door on the floor.") ;
            	if (IR == 90) {
            		myPrintf("You can see all the usual fixtures; especially a very nice shower.\nThere is a small door in the wall in front of you as you entered and\nAnother small door in the ceiling. A small window overlooks the\nGrounds far below.  Seems the only other way out is by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IDW][I]);
            	}
            if (IR == 91) myPrintf("You are on a screened in porch in front of the Mansion. There is\nA small plaque on the door with writing on it. You can either\nGo North into the Mansion or go West to the driveway.") ;
            if (IR == 92) myPrintf("You are on a long drive that leads easterly from the gate to the\nMansion. A sign along the driveway reads: Stop look and listen.\nThe wall is to the South and trees line the northern side.") ;
            	if (IR == 93) {
            		myPrintf("You are South of the heavy iron gate. A sign says enter at your own risk.");
            if (IVEN[4] == 493) myPrintf("\nThere is a shiny brass lantern hanging on the gate post by the sign.") ;
            if (IVEN[4] == 1493) myPrintf("\nThere is a brightly shining lantern hanging on on a brass hook next to the sign.") ;
            if ((IVEN[4] != 493)  && (IVEN[4] != 1493)) myPrintf("\nThere is an empty shiny brass hook on the gate post next to the sign.") ;
            		myPrintf("\nYou can see the road turn North along the wall about a quarter mile to\nThe East and to the West. You are also at the end of the highway\nWhich goes South to the big city.");
            	}
            if (IR == 94) myPrintf("You are outside the western wall of the Mansion. The road ends here\nBut the wall continues North into a dense forest. The forest is\nAlso to the West of the road but has been partially cleared. You\nCan see the road turn East about a quarter mile to the South.") ;
            if (IR == 95) myPrintf("You are on the road at the back gate of the Mansion. The hinges have\nRusted through and the gate hangs half open. There is a path leading\nSouth through the gate and between two rows of hedges. To the North\nOf the road is a 500 foot sheer cliff, overlooking the sea shore. The\nRoad ends here but the wall continues West into the dense forest.") ;
            if (IR == 96) /**/ myPrintf("You are outside the eastern wall of the Mansion. The wall forms\nPart of the Mansion to the South where the road goes under it\nBefore turning West. The road also turns to the West along\nThe wall about a quarter mile to the North. There is a 500\nFoot sheer cliff to the East of the road.") ;
            if (IR == 97) myPrintf("You are in a lightly wooded part of the forest. There are no paths\nAnd you cannot go straight in any direction very long.") ;
            if (IR == 98) myPrintf("You are in a densely wooded part of the forest. You can no longer tell\nCardinal directions. There is no way to tell if_var you are going\nStraight and you could easily go in circles and not know it.") ;
            if (IR == 99) myPrintf("The stream flows North to South in a deep canyon. There are\nSome steps cut in the rock that go down into a cave and up the\nSide of the canyon to a cottage built on a small plateau.") ;
                return "MMSE_2000"
            }
            "MMSE_2000" -> {
            	return "MMSB"
                return "MMSF"
            }
        }
        return "EXIT"
    }
    private suspend fun stepMMSF(lbl: String): String {
        when (lbl) {
            "MMSF" -> {
            	IR=ITST[1];
            	R=RND;
            if (IWRD[0][9] <= 20) return "MMSF_1000"
            if (IWRD[0][9] == 21) return "MMSF_2000"
            if (IWRD[0][9] == 22) return "MMSF_2000"
            if (IWRD[0][9] == 23) return "MMSF_3000"
            if (IWRD[0][9] == 24) return "MMSF_4000"
            if (IWRD[0][9] == 25) return "MMSF_5000"
            if (IWRD[0][9] == 26) return "MMSF_5000"
            if (IWRD[0][9] == 27) return "MMSF_6000"
            if (IWRD[0][9] == 28) return "MMSF_7000"
            if (IWRD[0][9] == 29) return "MMSF_8000"
            if (IWRD[0][9] == 30) return "MMSF_8000"
            if (IWRD[0][9] == 31) return "MMSF_9000"
            if (IWRD[0][9] == 32) return "MMSF_10000"
            if (IWRD[0][9] == 33) return "MMSF_10000"
            if (IWRD[0][9] == 34) return "MMSF_11000"
            if (IWRD[0][9] == 35) return "MMSF_11000"
            if (IWRD[0][9] == 36) return "MMSF_12000"
                return "MMSF_1000"
            }
            "MMSF_1000" -> {
            if (((ITST[5] - (ITST[5]/25)*25) == 13) && (IWRD[0][9] == 20) && (IWRD[1][8] == 3) && (IWRD[1][9] == 71) && (ITST[5] < 450)) ITST[2] += 2;
                        if (((ITST[5]-(ITST[5]/25)*25) == 13) && (IWRD[0][9] == 20) && (IWRD[1][8] == 3) && (IWRD[1][9] == 71) && (ITST[5] < 450)) {
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[0][9] == 20) && (IWRD[1][8] == 3) && (IWRD[1][9] == 71)) {
            		IPR[2] = 520;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[0][10] > 1) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	ISC = ITST[2];
            	ISCP = ITST[3];
            	ISCL = (ISC/85+1)*85-ISC;
            	myPrintf("\nSo far you have scored %d points. You can still reach %d points", ISC, ISCP);
            	if (IWRD[0][9] == 20) {
            		IPR[2] = 0;
            		IPR[3] = 0;
            		return "MMSB"
            	}
            	myPrintf("\nOf the original 999 if_var you continue. This rates you as a ");
            for (L in (0).toInt() until (8).toInt())
            		myPutchar(ICLS[ISC/81+1][L]);
            	myPrintf("\nSleuth. You need %d more points to reach the next level of skill.\nEnter score next time if_var that is what you want.",
            			ISCL);
            	ourtime(ITIM);
            	ITR=((60*ITIM[4]+ITIM[3]-60*(ITST[19]/100).toInt()-IFSD(ITST[19]))).toInt();
            if (ITR < 0) ITR += 1440;
            	TG = ITST[5]/25.00;
            if ((ITR/ITST[5]) <= 163) return "MMSF_1012"
            	IEF=99;
            	return "MMSF_1013"
                return "MMSF_1012"
            }
            "MMSF_1012" -> {
            	IEF=200*ITR/ITST[5];
            if (IEF > 99) IEF=99;
                return "MMSF_1013"
            }
            "MMSF_1013" -> {
            	myPrintf("\n\nYou played %d minutes real time and %5.1f hours game time or %d %% utilization.", ITR, TG, IEF);
                return "MMSF_1021"
            }
            "MMSF_1021" -> {
            	myPrintf("\n\nDo you really want to quit now?  ");
            	IANS = my_getchar();
            if (IANS >= 'a'.toInt()) IANS -= ' '.toInt() ;
            	if (IANS == 'N'.toInt()) {
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IANS == 'Y'.toInt()) {
            		IPR[2] = 1032;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            	myPrintf("\nWhat? Please answer yes or no");
            	return "MMSF_1021"
                return "MMSF_2000"
            }
            "MMSF_2000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if ((IWRD[0][10] > 2) && ((IWRD[2][8] != 5) || (IWRD[2][9] != 7) || (IWRD[1][8] != 3) || (IWRD[3][8] != 4))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[1][8] != 3) {
            		IPR[2] = 2011;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IWRD[1][9] == 99) && (IWRD[0][9] == 22)) return "MMSF_2100"
            	if (IVEN[IWRD[1][9]] > 20000) {
            		IPR[2] = 2020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if ((IWRD[0][10] > 2) && (IFSD(IVEN[IWRD[1][9]]) != IWRD[3][9]) && (IVEN[IWRD[1][9]] < 10000)) {
            		IPR[2] = 2025;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IVEN[IWRD[1][9]] == 0) && (IWRD[1][9] > 53)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if (((IFSD(IVEN[IWRD[1][9]])  != IR) && (IVEN[IWRD[1][9]] < 10000)) || ((IVEN[IWRD[1][9]] >= 10000) && (IRES[IVEN[IWRD[1][9]]-(IVEN[IWRD[1][9]]/100)*100]- (IRES[IVEN[IWRD[1][9]]-(IVEN[IWRD[1][9]]/100)*100]/100)*100!=IR))|| ((IVEN[IWRD[1][9]] > 10000) && (IWRD[0][9] == 21))) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[1][9] == 88) {
            		IPR[2] = 2037;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[1][9] > 99) && (IWRD[1][9] < 103)) {
            		IPR[2] = 2041;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[1][9] == 103) {
            		IPR[2] = 2042;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[1][9] == 29) && (ITST[5] > 50) && (IVEN[29] == 1214)) {
            		IPR[2] = 2035;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[1][9] > 53) {
            		IPR[2] = 2040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IC = ITD(IVEN[IWRD[1][9]]);
            for (J in (1).toInt()..(53).toInt()) { this.J = J; 
            if (IVEN[J] < 20000) continue;
            		IC = IC+ITD(IVEN[J]);
            	}
            	if ((IC > 16) && (IVEN[48] < 20000)) {
            		IPR[2] = 2060;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IC > 24) {
            		IPR[2] = 2060;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	K = IWRD[1][9];
            if ((K != 5) && (K != 17) && (K != 29) && (K != 36) && (K != 37) && (K != 39) && (K != 40) && (K != 42) && (K != 46) && (K != 4) && (K != 28)) return "MMSF_2052"
            if ((IFD(IVEN[K]) == 1) && (K != 4)) IVEN[K] -= 1000;
            if ((ITD(IVEN[4]) == 4) && (K == 4)) IVEN[4] -= 100;
                return "MMSF_2052"
            }
            "MMSF_2052" -> {
            	R=RN(R);
            if (IWRD[1][9] == 21) ITST[16]=((R*89+1).toInt()).toInt() ;
            if ((IWRD[1][9] == 21) && (IVRB[ITST[16]][0].toInt() == ' '.toInt().toInt())) return "MMSF_2052"
            	IVEN[IWRD[1][9]]=(IVEN[IWRD[1][9]]/100)*100-(IVEN[IWRD[1][9]]/10000)*
            		10000+20000;
            if ((ITST[5] < 100) || (ITST[5] > 400)) return "MMSF_2095"
            for (J in (2).toInt()..(14).toInt()) { this.J = J; 
            if ((IRES[J] > 10000) || (ITD(IRES[J]) == 0) || (IFSD(IRES[J]) == 0)) continue;
            for (K in (1).toInt()..(53).toInt()) { this.K = K; 
            if ((IVEN[K] > 10000) || (IFSD(IRES[J]) != IFSD(IVEN[K]))) continue;
            			R=RN(R);
            if ((R > 0.2) && (ITST[5] < 250)) continue;
            if ((R > 0.1) && (ITST[5] > 250)) continue;
            			IC=ITD(IVEN[K]);
            for (L in (1).toInt()..(53).toInt()) { this.L = L; 
            if ((IVEN[L] < 10000) || (IVEN[L] > 20000) || (IFSD(IVEN[L]) != J)) continue;
            				IC=IC+ITD(IVEN[L]);
            			}
            			R=RN(R);
            if (R > 0.3) continue;
            if (IC <= ITD(IRES[J])) IVEN[K] = 10000+(IVEN[K]/100) *100+J;
                        if ((IC <= ITD(IRES[J])) && (IFSD(IRES[J]) == IR)) {
            				myPrintf("\nYou just saw ");
            for (I in (0).toInt() until (8).toInt())
            					myPutchar(IRSN[J][I]);
            				myPrintf("\nPick up ");
            			}
            if ((IC <= ITD(IRES[J])) && (IFSD(IRES[J]) == IR)) MMRI(4, K) ;
            		}
            	}
                return "MMSF_2095"
            }
            "MMSF_2095" -> {
            	IPR[2] = 2070;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_2100"
            }
            "MMSF_2100" -> {
            	if (IR != 90) {
            		IPR[2] = 2110;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            for (I in (1).toInt()..(52).toInt()) { this.I = I; 
            		if (IVEN[I] > 20000) {
            			IPR[2] = 2120;
            			IPR[3] = 0;
            			return "MMSG"
            		}
            	}
            	if (IVEN[53] > 20000) {
            		IPR[2] = 2140;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	ITST[32]=1;
            	IPR[2] = 2150;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_3000"
            }
            "MMSF_3000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IWRD[0][10] == 2) && (IWRD[1][8] == 5) && (IWRD[1][9] == 11)) return "MMSF_3200"
            	if ((IWRD[0][10] > 2) || (IWRD[1][8] != 3)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IVEN[IWRD[1][9]] < 20000) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IR == 35) IVEN[IWRD[1][9]] = ITD(IVEN[IWRD[1][9]]) +98;
            	if (IR == 35) {
            		IPR[2] = 3015;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IR < 75) || (IR > 77)) return "MMSF_3100"
            for (J in (326).toInt()..(328).toInt()) { this.J = J; 
            if (IFSD(IXT[J]) != IR) continue;
            		IVEN[IWRD[1][9]]=IVEN[IWRD[1][9]]-20000+IXT[J]/100;
            		if ((IXT[J]/100) == 79) {
            			IPR[2] = 3030;
            			IPR[3] = 0;
            			return "MMSG"
            		}
            		if ((IXT[J]/100) != 79) {
            			IPR[2] = 3040;
            			IPR[3] = 0;
            			return "MMSG"
            		}
            	}
                return "MMSF_3100"
            }
            "MMSF_3100" -> {
            	if ((IWRD[1][9] == 30) && (IR != 25)) {
            		IVEN[30] = 0;
            		IPR[2]   = 3045;
            		IPR[3]   = 0;
            		return "MMSG"
            	}
            	IVEN[IWRD[1][9]]=IVEN[IWRD[1][9]]-20000+IR;
            for (J in (2).toInt()..(14).toInt()) { this.J = J; 
            if ((ITD(IRES[J]) == 0) || (IRES[J] > 10000)) continue;
            		R=RN(R);
            if ((R > 0.1) && (ITST[5] < 250)) return "MMSF_3130"
            if ((R > 0.2) && (ITST[5] > 250)) return "MMSF_3130"
            for (K in (1).toInt()..(53).toInt()) { this.K = K; 
            if ((IVEN[K] < 10000) || (IVEN[K] > 20000) || (IFSD(IVEN[K]) != J)) continue;
            			R=RN(R);
            if (R > 0.3) continue;
            			IVEN[K] = IVEN[K]-10000-J+IFSD(IRES[J]);
            			if (IFSD(IRES[J]) == IR) {
            				myPrintf("\nYou just saw ");
            for (L in (1).toInt() until (8).toInt())
            					myPutchar(IRSN[J][L]);
            				myPrintf("Put down ");
            				MMRI(4, K);
            			}
            		}
            	}
                return "MMSF_3130"
            }
            "MMSF_3130" -> {
            	IPR[2] = 3110;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_3200"
            }
            "MMSF_3200" -> {
            	if (IR == 35) {
            		IPR[2] = 3210;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IC = 0; IX = 0;
            for (I in (1).toInt()..(52).toInt()) { this.I = I; 
            if ((I == 30) && (IVEN[I] > 20000)) IX=1;
            if (IVEN[I] < 20000) continue;
            		IVEN[I]=IVEN[I]-20000+IR;
            		IC=1;
            	}
            	if (IC == 0) {
            		IPR[2] = 520;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IX == 1) && (IR != 25)) {
            		IVEN[30] = 0;
            		IPR[2]   = 3045;
            		IPR[3]   = 0;
            		return "MMSG"
            	}
            // /*
            // * Hmm, interesting bug here.  Clearly, this should be assigned
            // * the value 325, but my "original" source gave it as 1325, which
            // * which caused extremely whacked-out behavior.  This does not
            // * inspire great confidence in my original source!  Fixed 2/6/2000.
            // */
            if (IX == 1) IVEN[30]=325;
            	IPR[2] = 1040;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_4000"
            }
            "MMSF_4000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if ((IWRD[0][10] != 4) || (IWRD[1][8] != 3) || (IWRD[3][8] != 4) || (IWRD[2][8] != 5) || (IWRD[2][9] != 5)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IVEN[IWRD[1][9]] < 20000) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IFSD(IRES[IWRD[3][9]]) != IR) {
            		IPR[2] = 4030;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IC=ITD(IVEN[IWRD[1][9]]);
            for (J in (1).toInt()..(53).toInt()) { this.J = J; 
            if (((IVEN[J]/10000) != 1) || (IWRD[3][9] != IFSD(IVEN[J]))) continue;
            		IC=IC+ITD(IVEN[J]);
            	}
            	if (IC > ITFD(IRES[IWRD[3][9]])) {
            		IPR[2] = 4030;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IVEN[IWRD[1][9]] = IVEN[IWRD[1][9]]-10000+IWRD[3][9];
            	IPR[2] = 3110;
            	IPR[3] = 1;
            	return "MMSG"
                return "MMSF_5000"
            }
            "MMSF_5000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IWRD[0][9] == 25) && (IWRD[1][8] == 5) && (IWRD[1][9] == 1)  && (IWRD[2][9] == 64)) IWRD[1][9]=4;
                        if ((IWRD[0][9] == 26) && ((IWRD[1][8] != 3) || ((IWRD[1][9] != 3) && (IWRD[1][9] != 6) && (IWRD[1][9] != 18) && (IWRD[1][9] != 21) && (IWRD[1][9] != 22) && (IWRD[1][9] != 28) && (IWRD[1][9] != 90) && (IWRD[1][9] != 88)))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[0][9] == 26) {
            		IWRD[2][8]=IWRD[1][8];
            		IWRD[2][9]=IWRD[1][9];
            		IWRD[1][8]=5;
            		IWRD[1][9]=4;
            	}
            	if ((IWRD[1][8] != 5) || (IWRD[1][9] != 4)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][8] != 3) && (IWRD[2][8] != 4)) {
            		IPR[2] = 5010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[2][8] == 3) return "MMSD_5100"
            	if (IFSD(IRES[IWRD[2][9]]) != IR) {
            		IPR[2] = 5020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IC=0;
            	myPrintf("\nYou are looking at the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IRSN[IWRD[2][9]][L]); }
            	myPrintf("\nWho has:");
            	MMRI(3,IWRD[2][9]);
            	IPR[2] = 5070;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSD_5100"
            }
            "MMSF_5200" -> {
            if (IWRD[2][9] != 19) return "MMSF_5300"
            	IWRD[2][9] = 9 - ITST[6]/3600;
            	IPR[2] = 5210;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_5300"
            }
            "MMSF_5300" -> {
            	if (IWRD[2][9] == 27) {
            		IPR[2] = 5310;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 9) {
            		IPR[2] = 5410;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[2][9] != 3) return "MMSF_5600"
            	IHR=6+ITST[5]/25;
            if (IHR > 12) IHR -= 12;
            if (IHR > 12) IHR -= 12;
            	MIN=(((ITST[5] - (ITST[5]/25).toInt()*25)*2.4)).toInt();
            	IDUMMY=IHR*100+MIN;
            	if (IWRD[2][9] != 9) {
            		IPR[2] = 5510;
            		IPR[3] = IDUMMY;
            		return "MMSG"
            	}
                return "MMSF_5600"
            }
            "MMSF_5600" -> {
            	if (IWRD[2][9] == 21) {
            		IPR[2] = 5610;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 6) {
            		IPR[2] = 5710;
            		IPR[3] = ITST[4]/1000;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 13) && (ITST[10] == 13)) {
            		IPR[2] = 5720;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 13) {
            		IPR[2] = 5730;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] > 9) && (IWRD[2][9] < 13) && (IWRD[2][9] == ITST[10])){
            		IPR[2] = 5740;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 22) && (IVEN[22] < 21000)) {
            		IPR[2] = 5750;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 22) && (IVEN[22] > 21000)) {
            		IPR[2] = 5755;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 20) {
            		IPR[2] = 5780;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 18) {
            		IPR[2] = 5781;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 35) {
            		IPR[2] = 5782;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[2][9] != 64) return "MMSF_5820"
            	IC=0;
            for (I in (2).toInt()..(14).toInt()) { this.I = I; 
            if (I == 5) continue;
            if (IFSD(IRES[I]) == IR) IC=I;
            	}
            	IPR[2] = 5812;
            	IPR[3] = IC;
            	return "MMSG"
                return "MMSF_5820"
            }
            "MMSF_5820" -> {
            	if (IWRD[2][9] == 17) {
            		IPR[2] = 5830;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 67) {
            		IPR[2] = 5860;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 29) {
            		IPR[2] = 5870;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 90) {
            		IPR[2] = 5880;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 88) && (IVEN[88] == 1017)) {
            		IPR[2] = 2037;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 88) {
            		IPR[2] = 5885;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 65) && (IWRD[2][9] == 66)){
            		IPR[2] = 5886;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 79) && (IR == 51)) {
            		IPR[2] = 5888;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 79) && (IR == 53)) {
            		IPR[2] = 5890;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 49) {
            		IPR[2] = 5891;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[2][9] == 48) {
            		IPR[2] = 5892;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[2][9] == 97) && (IWRD[2][9] == 98)){
            		IPR[2] = 5893;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IPR[2] = 5910;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_6000"
            }
            "MMSF_6000" -> {
            if (IWRD[1][8] == 3) return "MMSF_6100"
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[1][8] != 4) && (IWRD[0][10] != 2)){
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IFSD(IRES[IWRD[1][9]]) == IR) {
            		IPR[2] = 6005;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IVEN[30] < 20000) 	{
            		IPR[2] = 6010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IC=IFSD(IRES[IWRD[1][9]]);
            	if (IC == 0) {
            		IPR[2] = 520;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IC=IFSD(IROM[IC]);
            	IPR[2] = 6020;
            	IPR[3] = IC;
            	return "MMSG"
                return "MMSF_6100"
            }
            "MMSF_6100" -> {
            	if (IWRD[0][10] != 2) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IVEN[IWRD[1][9]] > 20000) {
            		IPR[2] = 2020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IVEN[IWRD[1][9]] > 10000) && (IFSD(IVEN[IWRD[1][9]]) == IR)) {
            		IPR[2] = 6005;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IVEN[49] < 20000) {
            		IPR[2] = 6030;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IFSD(IVEN[IWRD[1][9]]) == 0) {
            		IPR[2] = 6035;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IPR[2] = 6040;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_7000"
            }
            "MMSF_7000" -> {
                        if ((IWRD[0][10] != 1) && ((IWRD[0][10] != 2) || (IWRD[1][8] != 5) || (IWRD[1][9] != 11))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	myPrintf("\nYour booty contains:");
            	MMRI(2, 0);
            	IPR[2] = 0;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_8000"
            }
            "MMSF_8000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IWRD[0][9] == 30) && (((IWRD[1][8] == 5) && (IWRD[1][9] != 2)) || ((IWRD[2][8] == 5) && (IWRD[2][9] != 2)) || ((IWRD[1][8] != 5) && (IWRD[2][8] != 5)))) return "MMSF_11000"
                        if(((IWRD[1][8] != 3) && (IWRD[2][8] != 3)) || (IWRD[0][10] > 3) || (IWRD[0][10] == 1) || ((IWRD[0][10] == 2) && (IWRD[0][9] == 30))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[1][8] == 5) IWRD[1][9] = IWRD[2][9];
            	if ((IWRD[1][9] < 54) && (IVEN[IWRD[1][9]] < 20000)) {
            		IPR[2] = 3012;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if ((IWRD[1][9] > 47) && ((IVEN[IWRD[1][9]] - (IVEN[IWRD[1][9]]/100)*100) != IR)) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if ((IWRD[1][9] != 4) && (IWRD[1][9] != 19) && (IWRD[1][9] != 26) && (IWRD[1][9] != 27) && (IWRD[1][9] != 31) && (IWRD[1][9] != 75) && (IWRD[1][9] != 76) && (IWRD[1][9] != 89)) {
            		IPR[2] = 8010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IVEN[IWRD[1][9]]/1000 - (IVEN[IWRD[1][9]]/10000)*10) == 1) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[1][9] == 27) {
            		IPR[2] = 8060;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IWRD[1][9] != 19) && (IWRD[1][9] != 76) && (IWRD[1][9] != 89)) return "MMSF_8010"
                        if ((IVEN[31] < 20000) && (IVEN[19] < 21000) && ((IR != 10) || (IVEN[76] < 1000)) && ((IR != 14) || (IVEN[89] < 1000))) {
            		IPR[2] = 8050;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((((IWRD[1][9] == 76)||(IWRD[1][9] == 89)) && (IVEN[19] < 21000)) || ((IWRD[1][9] == 19) && ((IR != 10) || (IVEN[76] < 1000)) && ((IR != 14) || (IVEN[89] <  1000)))) IVEN[31]=21100;
                return "MMSF_8010"
            }
            "MMSF_8010" -> {
                        if ((IWRD[1][9] == 4) && ((ITST[6]-(ITST[6]/1000)*1000) > 119) && (IVEN[8] < 20000)) {
            		IPR[2] = 8035;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IWRD[1][9] == 4) && ((ITST[6]-(ITST[6]/1000)*1000) > 119)) ITST[6] -= 2;
            	IVEN[IWRD[1][9]] = IVEN[IWRD[1][9]] + 1000;
            	if ((IWRD[1][9] == 75) && (ITST[8] != 0)) {
            		IPR[2] = 8040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[1][9] == 75) && (ITST[8] == 0)) {
            		IPR[2] = 8045;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (ITFD(IROM[IR]) == 50) {
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IWRD[0][10] = 1;
            	IWRD[0][9]  = 25;
            	IPR[2]=0;
            	IPR[3]=0;
            	return "MMSC"
                return "MMSF_9000"
            }
            "MMSF_9000" -> {
            	if (IWRD[0][10] > 1)  {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	R = RN(R);
            	IC=((R*4+9040).toInt()).toInt();
            if ((IR == 93) && (ITST[14] == 93)) IC = 9044;
            if ((IR == 98) || (IR == 97)) IC = 9045;
            if (IR == 83) IC=9046;
            if (IR == 19) IC=9047;
            if ((IR == 40) && (IXT[141] == 0)) IC = 9048;
            if ((IR == 78) || (IR == 80)) IC = 9049;
            if (IR == 86) IC=9050;
            if (((IR < 35) && (IR > 27)) || ((IR < 52) && (IR > 41))) IC = 9051;
            if ((IR == 8) && (IXT[84] == 0)) IC = 9048;
            if (IC > 9043) myPrintf("\nHints cost 20 points each. Do you want one?  ") ;
            if (IC < 9044) myPrintf("\nHints waste 20 points each. Do you want one?  ") ;
                return "MMSF_9020"
            }
            "MMSF_9020" -> {
            	IANS = my_getchar();
            if (IANS > 'a'.toInt()) IANS -= ' '.toInt() ;
            	if (IANS == 'N'.toInt()) {
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IANS != 'Y'.toInt()) {
            		myPrintf("\nWhat? Please answer yes or no");
            		return "MMSF_9020"
            	}
            	if (ITST[2] < 20) {
            		IPR[2] = 9027;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	ITST[2] -= 20;
            	ITST[3] -= 20;
            	IPR[2] = IC;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_10000"
            }
            "MMSF_10000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IWRD[0][10] != 2) || (IWRD[1][8] != 3)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IR == 19) && ((IWRD[0][9] == 33) && (IWRD[1][9] == 92))) {
            		IXT[11]=2219;
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSG"
            	}
            	if ((IR == 5) && (IWRD[0][9] == 32) && (IWRD[1][9] == 70)) {
            		IXT[183]=405;
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSG"
            	}
            	if ((IR == 7) && (IWRD[0][9] == 32) && (IWRD[1][9] == 69)) {
            		IXT[123]=407;
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSG"
            	}
            	if ((IWRD[1][9] == 88) && (IR == 17)) {
            		IPR[2] = 2037;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[1][9] != 92) return "MMSF_10050"
                        if (((IR > 27) && (IR != 91) && (IR != 54) && (IR != 36) && (IR != 90)) || (IR == 3) || (IR == 4) || (IR == 13) || (IR == 16) || (IR == 21) || (IR == 22)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            for (J in (1).toInt()..(240).toInt()) { this.J = J; 
            if (IFSD(IXT[J]) != IR) continue;
            if (((J-1)/60+1) != (ITST[4]/1000)) continue;
            		IC = ITFD(IXT[J]);
                        if ((IC == 4) || (IC == 13) || (IC == 22) || (J == 62) || (J == 70) || (J == 133) || (J == 182) || (J == 190)) {
            			IPR[2] = 15030;
            			IPR[3] = 0;
            			return "MMSG"
            		}
            		if ((IXT[J] > 20000) && (IWRD[0][9] == 32)) {
            			IPR[2] = 10035;
            			IPR[3] = 0;
            			return "MMSG"
            		}
            		if (IWRD[0][9] == 32) {
            			IPR[2] = 10045;
            			IPR[3] = 0;
            			return "MMSG"
            		}
            		IPR[2] = 10046;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IPR[2] = 15030;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_10050"
            }
            "MMSF_10050" -> {
            if (IWRD[1][9] != 91) return "MMSF_10100"
            	if ((IR > 26) || (IR == 21) || (IROM[IR] < 10000)) {
            		IPR[2] = 10010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if (((IROM[IR] > 20000) && (IWRD[0][9] == 32)) || ((IROM[IR] > 20000) && (IWRD[0][9] == 33))) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[0][9] == 32) IROM[IR] += 10000;
            if (IWRD[0][9] == 33) IROM[IR] -= 10000;
            if ((IWRD[0][9] != 32) || (IFSD(IRES[5]) != IR) || (ITST[1] > 300)) return "MMSF_10090"
            for (J in (1).toInt()..(360).toInt()) { this.J = J; 
            if (IFSD(IXT[J]) != IR) continue;
            if (IFSD(IXT[J]) > 20000) continue;
            if ((IFSD(IXT[J]) == 16) || (ITFD(IXT[J]) > 27)) continue;
            if (IROM[ITFD(IXT[J])] > 20000) continue;
            		IRES[5] = IRES[5] - IR + ITFD(IXT[J]);
            		return "MMSF_10090"
            	}
            	IRES[5]=0;
            	IVEN[1] = 100+IR;
            	ITST[2] += 75;
            	IPR[2] = 10085;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_10090"
            }
            "MMSF_10090" -> {
            if (ITFD(IROM[IR]) == 50) return "MMSF_10095"
            	IWRD[0][10] = 1;
            	IWRD[0][9] = 25;
            	IPR[2]=0; IPR[3]=0;
            	return "MMSC"
                return "MMSF_10095"
            }
            "MMSF_10095" -> {
            	IPR[2]=1040; IPR[3]=0;
            	return "MMSG"
                return "MMSF_10100"
            }
            "MMSF_10100" -> {
            if (IWRD[1][9] != 93) return "MMSF_10200"
                        if ((IR != 93) && (IR != 92) && (IR != 95) && (IR != 54) && (IR != 55)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IXT[27] == 0) && ((IR == 54) || (IR == 55))) {
            		IPR[2] = 10105;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (((IR == 95) || (IR == 55)) && (IWRD[0][9] == 32)) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (((IR == 95) || (IR == 55)) && (IWRD[0][9] == 33)) {
            		IPR[2] = 10110;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if (((IWRD[0][9] == 33) && (IXT[54] > 20000)) || ((IWRD[0][9] == 32) && (IXT[54] < 20000))) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            for (J in (1).toInt()..(14).toInt()) { this.J = J; 
            if (((IFSD(IRES[J]) != IR) || (IRES[J] > 10000)) && (IVEN[48] < 20000)) continue;
            if (IWRD[0][9] == 32) IXT[54]=19293;
            if (IWRD[0][9] == 32) IXT[234]=19392;
            if (IWRD[0][9] == 33) IXT[54]=29293;
            if (IWRD[0][9] == 33) IXT[234]=29392;
            		if (J > 1) {
            			IPR[2] = 10115;
            			IPR[3] = J;
            			return "MMSG"
            		}
            		IPR[2] = 10116;
            		IPR[3] = J;
            		return "MMSG"
            	}
            	IPR[2] = 10130;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_10200"
            }
            "MMSF_10200" -> {
            	if ((IWRD[1][9] < 54) && (IVEN[IWRD[1][9]] < 20000)) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[1][9] != 35) return "MMSF_10300"
                        if (((IWRD[0][9] == 32) && (IFD(IVEN[35]) == 2)) || ((IWRD[0][9] == 33) && (IFD(IVEN[35]) < 2))) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[0][9] == 33) {
            		IVEN[35] -= 1000;
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IFD(IVEN[35]) == 0) {
            		IPR[2] = 5783;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IFD(IVEN[35]) == 5) {
            		IVEN[35] += 1000;
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IVEN[35] += 1000;
            for (J in (1).toInt()..(360).toInt()) { this.J = J; 
            if ((IFSD(IXT[J]) != IR) || (IXT[J] > 20000)) continue;
            		IVEN[35] -= 100;
            		IPR[2] = 10210;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IPR[2] = 10230;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_10300"
            }
            "MMSF_10300" -> {
            	IPR[2] = 10910;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_11000"
            }
            "MMSF_11000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if ((IWRD[0][9] == 30) && (((IWRD[1][8] == 5) && (IWRD[1][9] != 10)) || ((IWRD[2][8] == 5) && (IWRD[2][9] != 10)) || ((IWRD[1][8] != 5) && (IWRD[2][8] != 5)))) return "MMSF_12000"
                        if ((IWRD[0][9] == 35) && (((IWRD[1][9] == 2) && (IWRD[1][8] == 5) && (IWRD[2][9] == 88) && (IWRD[2][8] == 3) && (IWRD[0][10] == 3)) || ((IWRD[0][10] == 2) && (IWRD[1][9] == 88) && (IWRD[1][8] == 3)))) {
            		IPR[2] = 2038;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if (((IWRD[0][10]  ==  2)  &&  (IWRD[0][9]  !=  34)) || ((IWRD[0][10]  ==  3)  &&  (IWRD[1][8]  !=  5)  && (IWRD[2][8]  !=  5))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if (((IWRD[1][8] == 5)  &&  (IWRD[1][9]  !=  10)  && (IWRD[1][9]  !=  8)) || ((IWRD[2][8] == 5)  && (IWRD[2][9]  !=  10)  &&  (IWRD[2][9]  !=  12))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IWRD[0][10] > 3) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            if (IWRD[1][8] == 5) IWRD[1][9]=IWRD[2][9];
            if (IWRD[1][8] == 5) IWRD[1][0]=IWRD[2][0];
            if (IWRD[1][8] == 5) IWRD[1][1]=IWRD[2][1];
            if (IWRD[1][8] == 5) IWRD[1][2]=IWRD[2][2];
            if (IWRD[1][8] == 5) IWRD[1][3]=IWRD[2][3];
            if (IWRD[1][8] == 5) IWRD[1][8]=IWRD[2][8];
            	IC=IWRD[1][9];
            	if ((IC < 36) && (IVEN[IC] < 20000)) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IC > 35) && (IFSD(IVEN[IC]) != IR)) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if ((IC != 4) && (IC != 19) && (IC != 26) && (IC != 27) && (IC != 31) && (IC != 44) && (IC != 45)) {
            		IPR[2] = 8010;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (((IVEN[IC]/1000) - (IVEN[IC]/10000)*10) == 0) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	IVEN[IC] -= 1000;
            	IPR[2] = 1040;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_12000"
            }
            "MMSF_12000" -> {
                        if ((IR == 10) && ((IWRD[1][8] == 3) && (IWRD[1][9] == 72) && (IWRD[0][10] == 2))) {
            		IXT[5] = 1310;
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSG"
            	}
            if ((IWRD[1][8] == 3) && (IWRD[1][9] == 17) && (IWRD[0][10] == 2)) return "MMSF_12100"
            	IC=IWRD[1][9];
                        if ((IWRD[0][10] != 2) || (IWRD[1][8] != 2) || (IC == 5) || (IC == 6) || (IC > 10)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if ((IC < 5) && (IVEN[6] < 20000) && (IR < 54)) {
            		IPR[2] = 1005;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	if (IC < 5) {
            		ITST[4] = ITST[4] - (ITST[4]/1000)*1000 + IC*1000;
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            	ITST[4] = ITST[4]+1000*(IC-9);
            if (ITST[4] < 1000) ITST[4] += 4000;
            if (ITST[4] > 5000) ITST[4] -= 4000;
            	IPR[2] = 1040;
            	IPR[3] = 0;
            	return "MMSG"
                return "MMSF_12100"
            }
            "MMSF_12100" -> {
                        if ((IVEN[17] > 20000) || ((IFSD(IVEN[17]) == IR) && (IFD(IVEN[17]) != 1) && (IVEN[17] < 10000))) {
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSG"
            	}
                        if (((IFSD(IVEN[17]) != IR) && (IVEN[17] < 10000)) || (IVEN[17] > 10000)) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSG"
            	}
            for (I in (1).toInt()..(360).toInt()) { this.I = I; 
            if (((ITFD(IXT[I]) > 41) && (ITFD(IXT[I]) < 54)) || ((IFSD(IXT[I]) > 41) && (IFSD(IXT[I]) < 54))) IXT[I]=0;
            	}
            	IROM[42]=5153;
            for (I in (43).toInt()..(50).toInt()) { this.I = I; IROM[I]=5053; }
            	IROM[51]=6555;
            	IROM[52]=6554;
            	IROM[53]=6555;
            for (I in (1).toInt()..(53).toInt()) { this.I = I; 
            if ((IVEN[I] < 10000) && (IFSD(IVEN[I]) > 41) && (IFSD(IVEN[I]) < 54)) IVEN[I] = 0;
            	}
            	IVEN[40]=1151; IVEN[42]=1151; IVEN[37]=1153; IVEN[39]=1253;
            	IRES[4]=10000;
            	ITST[32]=ITST[5];
            	IXT[22]=4243; IXT[23]=4244; IXT[82]=4442; IXT[83]=4445; IXT[84]=4544;
            	IXT[85]=4750; IXT[86]=4948; IXT[87]=5049;
            	IXT[142]=4344; IXT[143]=4443; IXT[202]=4342; IXT[203]=4345;
            	IXT[204]=4543; IXT[205]=5047; IXT[254]=4142; IXT[255]=4547;
            	IXT[256]=5148; IXT[257]=5249; IXT[258]=5350; IXT[315]=4241;
            	IXT[316]=4648; IXT[317]=4647; IXT[318]=4745; IXT[351]=4849;
            	IXT[352]=4851; IXT[353]=4950; IXT[319]=4952; IXT[354]=5053;
            	IPR[2]=12140;
            	IPR[3]=0;
            	return "MMSG"
            	IPR[2]=0;
            	IPR[3]=0;
            	return "MMSB"
                return "MMSG"
            }
        }
        return "EXIT"
    }
    private suspend fun stepMMSG(lbl: String): String {
        when (lbl) {
            "MMSG" -> {
            	IRIT=IPR[2];
            	IW63=IWRD[2][9];
            	R=RND;
            	IFC=ITST[4]/1000;
            	IDN=1;
            	IDE=2;
            	IDS=3;
            	IDW=4;
            if (IVEN[6] < 20000) IDN=10-IFC;
            if (IDN == 6) IDN=10;
            if (IVEN[6] < 20000) IDE=11-IFC;
            if (IVEN[6] < 20000) IDS=12-IFC;
            if (IDS == 11) IDS=7;
            if (IVEN[6] < 20000) IDW=13-IFC;
            if (IDW > 10) IDW=IDW-4;
            if (IRIT == 320) myPrintf("\nI cannot figure out what you are trying to say") ;
            if (IRIT == 520) myPrintf("\nNothing happens") ;
            	if (IRIT == 1000) {
            		myPrintf("\nI am not a mindreader, you will have to tell me  what to ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[0][L]);
            	}
            if (IRIT == 1040) myPrintf("\nOkay") ;
            if (IRIT == 1065) myPrintf("\nProgram error at line %d", IPR[3]) ;
            if ((IPR[3] == 7) || (IPR[3] == 16) || (IPR[3] == 25)) IC = IDS;
            if ((IPR[3] == 5) || (IPR[3] == 14) || (IPR[3] == 23)) IC = IDW;
            if ((IPR[3] == 1) || (IPR[3] == 10) || (IPR[3] == 19)) IC = IDN;
            	if (IRIT == 1095) {
            		myPrintf("\nYour action opened a panel into the secret passage that you can\nEnter by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IC][I]);
            	}
            if ((IRIT == 1095) && (ITST[1] == 5) && (ITST[5] > 50)) myPrintf("\nThe coffin is empty and the lid will not stay open") ;
            if ((IRIT == 1095) && (ITST[1] == 5) && (ITST[5] == 300)) IRES[5]=5;
            if ((IRIT == 1095) && (ITST[1] == 5) && (ITST[5] <= 50) && (IRES[5] == 5)) myPrintf("\nThe Vampire is in the coffin and is waking up.") ;
            if (IRIT == 1150) myPrintf("\nIt is dark here\n") ;
            if ((IRIT == 1150) && (IPR[3] == 1)) myPrintf("\nIt is after sunset") ;
            if ((IRIT == 1150) && (IPR[3] == 2)) myPrintf("\nA curtain blocks the light") ;
            	if (IRIT == 2010) {
            		myPrintf("\nYou cannot ");
            for (L in (0).toInt() until (4).toInt())
            			myPutchar(IWRD[0][L]);
            		myPrintf(" The ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 2011) {
            		myPrintf("\nYou cannot ");
            for (L in (0).toInt() until (4).toInt())
            			myPutchar(IWRD[0][L]);
            		myPrintf(" The ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 2020) {
            		myPrintf("\nYou already have the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 2025) {
            		myPrintf("\nIt is not in the possession of the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[3][L]);
            	}
            	if (IRIT == 2030) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 2035) {
            		myPrintf("\nThe wood is stacked too high.");
            	}
            if (IRIT == 2037) myPrintf("\nThe delicate papyrus of the scroll disintegrated when you touched it.") ;
            if (IRIT == 2038) myPrintf("\nI knew you would have to try that. Well you just blew pieces\nOf papyrus all over the library.") ;
            if ((IRIT == 2037) || (IRIT == 2038)) IVEN[88]=1017;
            	if (IRIT == 2040) {
            		myPrintf("\nYou cannot carry the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 2041) myPrintf("\nThe gold bull figurine and bars were delicately balanced and fell.") ;
            if (IRIT == 2042) myPrintf("\nThe dagger made a loud grating sound when you pulled it out of the slab.") ;
            	if ((IRIT == 2041) || (IRIT == 2042))  {
            		myPrintf(" The noise woke the troll and he bludgeoned you to death.");
            		IPR[2] = 10030;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            if (IRIT == 2060) myPrintf("\nYou will have to drop something first") ;
            	if (IRIT == 2070) {
            		myPrintf("\nYour booty now contains the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 2110) myPrintf("\nI would recommend that you take your showers in the bathroom.") ;
            if (IRIT == 2120) myPrintf("\nYou better drop your booty first.") ;
            if (IRIT == 2140) myPrintf("\nYou would just get your clothes wet.") ;
            if (IRIT == 2150) myPrintf("\nYou are now refreshed and smell a lot better also.") ;
            	if (IRIT == 3010) {
            		myPrintf("\nYou do not have the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 3012) myPrintf("\nYou do not have it.") ;
            	if (IRIT == 3015) {
            		myPrintf("\nA wood nymph just ran out from the forest and ran back in after picking\nUp the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 3030) myPrintf("\nYou hear a thud as it hits the bottom of the well") ;
            if (IRIT == 3040) myPrintf("\nYou hear a splash as it hits the water in the well") ;
            if (IRIT == 3045) myPrintf("\nThe globe just broke into a thousand pieces, momentarily releasing\nA cloud of hazy smoke that quickly surrounds you with visions of\nThe inhabitants of Mystery Mansion laughing at you hideously.") ;
            	if (IRIT == 3110) {
            		myPrintf("\nYou no longer have the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 3210) myPrintf("\nI wouldn't do that. The nymph will take it all.") ;
            	if (IRIT == 4010) {
            		myPrintf("\nFirst you have to find the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[3][L]);
            	}
            	if (IRIT == 4030) {
            		myPrintf("\nYou cannot give it to the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[3][L]);
            	}
            	if ((IRIT == 3110) && (IPR[3] == 1)) {
            		myPrintf("\nYou gave it to the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[3][L]);
            	}
            if (IRIT == 4050) myPrintf("\nThanks. It needs that around here.") ;
            if (IRIT == 5010) myPrintf("\nYou can only look at items and inhabitants") ;
            	if (IRIT == 5020) {
            		myPrintf("\nI do not see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[2][L]);
            	}
            if (IRIT != 5070) return "MMSG_5100"
            if ((IRIT == 5070) && (IRES[IW63] > 10000)) myPrintf("\nAnd is dead") ;
            if ((IRES[IW63]/1000) == 9) myPrintf("\nIs severely wounded and will soon be dead.") ;
            if ((IRES[IW63]/1000) == 8) myPrintf("\nIs badly wounded and might die.") ;
            if ((IRES[IW63]/1000) == 7) myPrintf("\nIs wounded and has passed out.") ;
            if ((IRES[IW63]/1000) == 6) myPrintf("\nAnd has been knocked unconscious.") ;
            if ((IRES[IW63]/1000) == 5) myPrintf("\nAnd has been shot in the leg.") ;
            if ((IRES[IW63]/1000) == 4) myPrintf("\nAnd is wounded.") ;
            if ((IRES[IW63]/1000) == 3) myPrintf("\nAnd is wounded but might recover.") ;
            if ((IRES[IW63]/1000) == 2) myPrintf("\nIs wounded and will be better soon.") ;
            if ((IRES[IW63]/1000) == 1) myPrintf("\nIs hurt and moving slowly.") ;
            if ((IRIT == 5070) && (IRES[IW63] > 1000) && (IW63 != 1)) return "MMSG_5100"
            if ((IRIT == 5070) && (IW63 == 2)) myPrintf("\n\nShe is an older thin woman wearing a long black dress.") ;
            if ((IRIT == 5070) && (IW63 == 3)) myPrintf("\n\nHe is a handsome young man and is smartly dressed.") ;
            if ((IRIT == 5070) && (IW63 == 4)) myPrintf("\n\nIt is a huge ugly thing, that is slow and blind.") ;
            if ((IRIT == 5070) && (IW63 == 5)) myPrintf("\n\nHe is pale, has a long black cape, and is breathing heavily through\nHis long canines.") ;
            if ((IRIT == 5070) && (IW63 == 6)) myPrintf("\n\nHe is a friendly looking husky chap.") ;
            if ((IRIT == 5070) && (IW63 == 7)) myPrintf("\n\nHe is a small man with dirty clothes and hands.") ;
            if ((IRIT == 5070) && (IW63 == 8)) myPrintf("\n\nShe is a sexy young woman with long blond hair.") ;
            if ((IRIT == 5070) && (IW63 == 9)) myPrintf("\n\nShe is dressed in white and has blood on her apron.") ;
            if ((IRIT == 5070) && (IW63 == 10)) myPrintf("\n\nHe is a hairy beast and his eyes are looking directly at you.") ;
            if ((IRIT == 5070) && (IW63 == 11)) myPrintf("\n\nHe is a little fellow with funny clothes and sneaky eyes.") ;
            if ((IRIT == 5070) && (IW63 == 12)) myPrintf("\n\nHe is a healthy looking man with a full beard.") ;
            if ((IRIT == 5070) && (IW63 == 13)) myPrintf("\n\nHe is a mean looking little thing with glowing eyes.") ;
            if ((IRIT == 5070) && (IW63 == 14)) myPrintf("\n\nHe is an older gentleman with gray hair and thick glasses.") ;
            if ((IRIT == 5070) && (IW63 == 15)) myPrintf("\n\nIt is a large hungry looking animal.") ;
            if ((IRIT == 5070) && (IW63 == 16)) myPrintf("\n\nHe is a large muscular man with hate in his eyes.") ;
            if ((IRIT == 5070) && (IW63 == 1)) myPrintf("\n\nFrom the look of the body it has not been dead very long.") ;
            if ((IRIT == 5070) && (IW63 == 1) && (ITST[10] < 13)) myPrintf("\n\nThe victim's arms are outstretched as if_var he was dragged here.\nHis shirt is torn and all bloody.") ;
            if ((IRIT == 5070) && (IW63 == 1) && (ITST[10] == 13)) myPrintf("\n\nIt is all doubled over with his arms covering his stomach.") ;
            if ((IRIT == 5070) && (IW63 == 1) && (ITST[10] == 14)) myPrintf("\n\nIt is all beaten and his head is bashed in.") ;
            if ((IRIT == 5070) && (IW63 == ITST[9])) myPrintf("\n\nYou notice a look of apprehension.") ;
                return "MMSG_5100"
            }
            "MMSG_5100" -> {
            	if (IRIT == 5110) {
            		myPrintf("\nYou do not have the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[2][L]);
            	}
            if (IRIT == 5130) myPrintf("\nOn closer inspection it appears as though the globe is really a dusty\nCrystal ball with magical powers to help you find inhabitants") ;
            if (IRIT == 5140) myPrintf("\nThe rope is about 50 feet long with a grapling hook on one end") ;
            if (IRIT == 5170) myPrintf("\nThe map is only of the woods and the road which you are not near.") ;
            if (IRIT == 5210) myPrintf("\nThe candle is about %d inches long", IW63) ;
            if ((IRIT == 5310) && (IVEN[27] > 21000)) myPrintf("\nThe torch is lit") ;
            if ((IRIT == 5310) && (IVEN[27] < 21000)) myPrintf("\nThe torch is all burnt out") ;
            if (IRIT == 5410) myPrintf("\nYour gun has %d shots left", ITST[11]) ;
            if (IRIT == 5510) myPrintf("\nThe time is %d:%d", IPR[3]/100, IPR[3]-((IPR[3]/100)*100)) ;
            	if (IRIT == 5610) {
            		myPrintf("\nThe book contains words I know like the verb ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IVRB[ITST[16]][L]);
            	}
            	if (IRIT == 5710) {
            		myPrintf("\nYou are facing ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IPR[3]][L]);
            	}
            if (IRIT == 5720) myPrintf("\nThe vial is empty but smells like poison") ;
            if (IRIT == 5730) myPrintf("\nThe vial is full of poison") ;
            if (IRIT == 5740) myPrintf("\nThere is blood on it") ;
            	if (IRIT == 5750) {
            		IVEN[22] += 1000;
            		myPrintf("\nThe note says: ");
            if (ITST[5] <= 50) myPrintf(" I can help you open the gate") ;
            if ((ITST[5] > 50) && (ITST[5] <= 100)) myPrintf(" You can go in any direction by just entering the first letter") ;
            if ((ITST[5] > 100) && (ITST[5] <= 150) && (ITST[8] != 0)) myPrintf(" If_var you have the murder weapon and can get the murderer to return to\nThe scene of the crime with you, you will score points") ;
            if ((ITST[5] > 100) && (ITST[5] <= 150) && (ITST[8] == 0)) myPrintf(" Congratulations for solving the murder so quickly.") ;
            if ((ITST[5] > 150) && (ITST[5] <= 200) && (IRES[5] != 0)) myPrintf(" Watch out for the Vampire. If_var you can destroy him, you will score points") ;
            		if ((ITST[5] > 150) && (ITST[5] <= 200) && (IRES[5] == 0)) {
            			myPrintf("\nLast month, in this Mystery, a player left a curtain open and the ");
            for (L in (0).toInt() until (8).toInt())
            				myPutchar(IRSN[ITST[39]][L]);
            			myPrintf("\nSaw the full moon through it and transformed into a werewolf.");
            		}
            if ((ITST[5] > 200) && (ITST[5] <= 250)) myPrintf(" You will have to go down into the dark pit to find a battery for the lantern") ;
            if ((ITST[5] > 250) && (ITST[5] <= 300)) myPrintf(" Beware: The moon is full tonight and someone here is a werewolf.\nKill it and you will score points.") ;
            	}
            	RND1=RND*7.7-(RND*7.7).toInt();
            	IC=((RND1*3).toInt()).toInt();
            	if (IRIT == 5750) {
                        if ((IC == 0) && (ITST[5] > 300) && (ITST[5] <= 350) && (ITST[8] != 0)) {
            			myPrintf(" In case you haven't guessed, the scene of the crime is the ");
            for (L in (0).toInt() until (8).toInt())
            				myPutchar(IRNM[IFSD(IROM[ITST[8]])][L]);
            		}
                        if ((IC == 1) && (ITST[5] > 300) && (ITST[5] <= 350) && (ITST[8] != 0)) {
            			myPrintf(" In case you haven't guessed, the murder was committed with the %s", IWP);
            		}
                        if ((IC == 2) && (ITST[5] > 300) && (ITST[5] <= 350) && (ITST[8] != 0)) {
            			myPrintf(" In case you haven't guessed, the murderer is the ");
            for (L in (0).toInt() until (8).toInt())
            				myPutchar(IRSN[ITST[9]][L]);
            		}
            if ((ITST[5] > 300) && (ITST[5] <= 350) && (ITST[8] == 0)) myPrintf("\nMagic words do not work after the garden closes.") ;
            if ((ITST[5] > 350) && (ITST[5] <= 400)) myPrintf(" You might be able to score points when you hear something") ;
            if ((ITST[5] > 400) && (ITST[5] <= 450)) myPrintf(" Someone has taken a lot of dynamite into the passages below the Mansion") ;
            if (ITST[5] > 450) myPrintf(" I'd get out of here if_var I were you") ;
            		myPrintf("\nSigned: A friend");
            	}
            if (IRIT == 5775) myPrintf("\nThe note says the same as it did before") ;
            if (IRIT == 5780) myPrintf("\nThe bullet is old and tarnished") ;
            if (IRIT == 5781) myPrintf("\nThe talisman says: Having this can help you get reincarnated if_var you die") ;
            if (IRIT == 5782) myPrintf("\nThe chest is very old with mysterious writing on it;") ;
            if (((IRIT == 5782) || (IRIT == 5783)) && (IFD(IVEN[35]) == 0)) myPrintf("\nThere is a padlock locking an unusual hasp of knotted wire.") ;
            if ((IRIT == 5782) && (IFSD(IRES[8]) == ITST[1]) && (IRES[8] < 10000)) myPrintf("\nThis chest contains a clue, reads the maid ") ;
            if ((IRIT == 5782) && (IFSD(IRES[14]) == ITST[1]) && (IRES[14] < 10000)) myPrintf("\nDon't open this chest in an unlocked room, reads the master behind you") ;
            if ((IRIT == 5782) && (IFSD(IRES[2]) == ITST[1]) && (IRES[2] < 10000)) myPrintf("\nBe sure to close the chest as quickly as possible, reads the\nLady in front of you") ;
            	if (IRIT == 5810) {
            		myPrintf("\nYou can see your reflection and things in the room behind you");
            		if (IPR[3] > 0) {
            for (L in (0).toInt() until (8).toInt())
            				myPutchar(IRSN[IPR[3]][L]);
            			myPrintf("\nAlso, beside you in the mirror is the reflection of the ");
            		}
            if (IPR[3] == 2) myPrintf("\nShe is an older thin woman wearing a long black dress.") ;
            if (IPR[3] == 3) myPrintf("\nHe is a handsome young man and is smartly dressed.") ;
            if (IPR[3] == 8) myPrintf("\nShe is a sexy young woman with long blond hair.") ;
            if (IPR[3] == 10) myPrintf("\nHe is a hairy beast and his eyes are looking directly at you.") ;
            	}
            if (IRIT == 5830) myPrintf("\nThe amulet is a large odd shaped jewel with eight facets, four being hexagons.") ;
            if (IRIT == 5860) myPrintf("\nThe wood is a stack of several large logs and a couple of small\nLogs wedged against the wall by the fireplace.") ;
            if ((IRIT == 5860) && (ITST[5] > 50)) myPrintf("The logs on the top\nLook like they were just put there.") ;
            if (IRIT == 5870) myPrintf("\nThe wedge is a pointed piece of wood about a foot long and is used to\nKeep the logs from rolling.") ;
            if (IRIT == 5880) myPrintf("\nThe plaque is inscribed:\nDo not stay here long.The boards are loose and you might fall through.") ;
            if (IRIT == 5885) myPrintf("\nThe scroll is rolled up and you cannot see the writing.") ;
            if (IRIT == 5886) myPrintf("\nYou are looking at a drawing of some kind of a lever system centered around\nA triangular cavity cut in the wall.") ;
            if ((IRIT == 5888) || (IRIT == 5890)) myPrintf("\nThe troll is a huge ugly thing sleeping fitfully on a pile of bones.") ;
            if ((IRIT == 5888) && (IVEN[36] == 1151)) myPrintf(" It has a big bludgeon in one hand and a string of pearls in the other.") ;
            if ((IRIT == 5890) && (IVEN[39] == 1253)) myPrintf(" It has a large mace in one hand and a silver goblet in the other.") ;
            if (IRIT == 5891) myPrintf("\nIt is a colorful bird with a band on its leg that says 'I can find things.'") ;
            if (IRIT == 5892) myPrintf("\nThe gauntlet is a soft metal glove that seems to give you extra strength.") ;
            if (IRIT == 5893) myPrintf("\nThe tables are in 8 rows and some of the tables have figures carved in them.\nThe two nearest you are made of different kinds of wood and have the figures\nOf a king and queen on them.") ;
            if (IRIT == 5910) myPrintf("\nI don't know what you expect to see") ;
            	if (IRIT == 6005) {
            		myPrintf("\nIf you look you can see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 6010) myPrintf("\nYou would need a crystal ball to see anyone not with you") ;
            	IC=1;
            if ((IFSD(IRES[IWRD[1][9]]) == 54) || (IFSD(IRES[IWRD[1][9]]) == 71) || (IFSD(IRES[IWRD[1][9]]) == 72) || (IFSD(IRES[IWRD[1][9]]) == 81) || (IFSD(IRES[IWRD[1][9]]) == 82) || (IFSD(IRES[IWRD[1][9]]) == 85) || (IFSD(IRES[IWRD[1][9]]) == 86) || (IFSD(IRES[IWRD[1][9]]) == 92) || (IFSD(IRES[IWRD[1][9]]) == 91)) IC=2;
            if (((IFSD(IRES[IWRD[1][9]]) >= 73) && (IFSD(IRES[IWRD[1][9]]) <= 77)) || (IFSD(IRES[IWRD[1][9]]) == 99)) IC=3;
            if ((IFSD(IRES[IWRD[1][9]]) ==  55)  || (IFSD(IRES[IWRD[1][9]]) ==  57)  || (IFSD(IRES[IWRD[1][9]]) ==  93)  || (ITST[1] ==  95)) IC=4;
            	if (IRIT == 6020) {
            		myPrintf("\nAs you gaze into the crystal ball, the figure of the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            		myPrintf("\nComes into focus ");
            		myPutchar(IPRP[IC][0]);
            		myPutchar(IPRP[IC][1]);
            		myPrintf(" The ");
            for (L in (0).toInt() until (16).toInt())
            			myPutchar(IRNM[IPR[3]][L]);
            	}
            if (IRIT == 6030) myPrintf("\nYou do not yet have the means to find items not near you.") ;
            if ((IRIT == 6040) || (IRIT == 6035)) myPrintf("\nThe parrot flew away for a few minutes and came back saying:") ;
            	if ((IRIT == 6040) && (IVEN[IWRD[1][9]] < 10000))  {
            		myPrintf("\nBaaaaaakkk: ");
            for (L in (0).toInt() until (16).toInt())
            			myPutchar(IRNM[IFSD(IROM[IFSD(IVEN[IWRD[1][9]])])][L]);
            	}
            if (IRIT == 6035) myPrintf("\nBaaaaaakkk: Sorry charlie!") ;
            	if ((IRIT == 6040) && (IVEN[IWRD[1][9]] > 10000))  {
            		myPrintf("\nBaaaaaakkk: ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[IFSD(IVEN[IWRD[1][9]])][L]);
            	}
            if (IRIT == 7050) myPrintf(" Nothing") ;
            if (IRIT == 8010) myPrintf("\nI don't know what you expect to happen") ;
            	if (IRIT == 8015) {
            		myPrintf("\nYour food has been eaten by the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 8020) myPrintf("\nIt already is") ;
            	if (IRIT == 8030) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[2][L]);
            	}
            	if (IRIT == 8031) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 8035) myPrintf("\nThe battery in your lantern is dead") ;
            	if (IRIT == 8040) {
            		myPrintf("\nAfter the radio warms up and the static dies down you hear\nA news report that the police are looking for the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[ITST[9]][L]);
            		myPrintf("\nOf Mystery Mansion for several murders");
            	}
            if (IRIT == 8045) myPrintf("\nThe radio plays soft music after it warms up.") ;
            if (IRIT == 8050) myPrintf("\nYou need a match or something.") ;
            if (IRIT == 8060) myPrintf("\nThe torch is all burned out and you cannot light it") ;
            if (IRIT == 9027) myPrintf("\nYou don't have enough points") ;
            if (IRIT == 9040) myPrintf("\nDon't ask for help anymore") ;
            if (IRIT == 9041) myPrintf("\nQuit while you are still alive") ;
            if (IRIT == 9042) myPrintf("\nYou will never figure it all out") ;
            if (IRIT == 9043) myPrintf("\nRead what I say carefull.") ;
            if (IRIT == 9044) myPrintf("\nA good way to start is by saying 'go West' or 'go East'") ;
            if (IRIT == 9045) myPrintf("\nKeep moving and you will get out of the woods sooner or later") ;
            if (IRIT == 9046) myPrintf("\nTry saying a magic word") ;
            if (IRIT == 9047) myPrintf("\nTry bringing a chair in here or closing the door") ;
            if (IRIT == 9048) myPrintf("\nUnless you know a magic word, there is nothing I can do for you") ;
            if (IRIT == 9049) myPrintf("\nThere is nothing you can do unless you know a magic word or have a shovel.") ;
            if (IRIT == 9050) myPrintf("\nUnless you know a magic word or can oil the hinge on the fire escape,\nThere is nothing I can do for you.") ;
            if (IRIT == 9051) myPrintf("\nTry dropping things to use as a point of reference.") ;
            if (IRIT == 10020) myPrintf("\nYou have not figured out how to do that yet.") ;
            if (IRIT == 10035) myPrintf("\nThe door is locked") ;
            if (IRIT == 10045) myPrintf("\nThe door is open") ;
            if (IRIT == 10046) myPrintf("\nThe door is closed") ;
            if (IRIT == 10010) myPrintf("\nThere is no curtain here") ;
            if (IRIT == 10085) myPrintf("\nYou trapped the Vampire in the daylight and he shriveled into nothing.") ;
            if (IRIT == 10105) myPrintf("\nThe garden gate is now closed permanently.") ;
            if (IRIT == 10110) myPrintf("\nThe gate is stuck open") ;
            	if (IRIT == 10115) {
            		myPrintf("\nWith a mighty shove, you move the gate with the help of the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[IPR[3]][L]);
            	}
            if (IRIT == 10116) myPrintf("\nThe gauntlet allows you to easily move the gate.") ;
            if (IRIT == 10130) myPrintf("\nThe gate is too heavy for you to open alone.") ;
            if (IRIT == 10210) myPrintf("\n1000 Screaming demons flee the confines of the chest after centuries of\nImprisonment and scatter about the Mansion and grounds to torment you and\nImpede your every move") ;
            	if (IRIT == 10230) {
            		myPrintf("\n1000 Screaming demons flee the confines of the and scatter about\nThe room looking for a way out. Finding no way out they return to the chest\nMurmuring something about murder and the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRNM[IFSD(IROM[ITST[8]])][L]);
            	}
            	if (IRIT == 10910) {
            		myPrintf("\nYou cannot open the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 12140) myPrintf("\nThe floor slid halfway into the wall. As you look down, you can\nSee a deep shaft with a spiral walkway carved into the rim\nDescending into the vast darkness.") ;
            if (IRIT == 15020) myPrintf("\nThe door you are facing has no lock") ;
            if (IRIT == 15030) myPrintf("\nYou are not facing a door") ;
            if (IRIT == 15040) myPrintf("\nYou need a key") ;
            if (IRIT == 15110) myPrintf("\nYour key will not fit the padlock") ;
            if (IRIT == 16010) myPrintf("\nYou need a gun to shoot anyone") ;
            if (IRIT == 16020) myPrintf("\nYour gun is out of bullets") ;
            if (IRIT == 16030) myPrintf("\nYou missed") ;
            	if (IRIT == 16035) {
            		myPrintf("\nYou just smashed the padlock to pieces");
            		IVEN[35] += 1000;
            	}
            	if (IRIT == 16040) {
            		myPrintf("\nYou just wasted a shot. It did you no good to shoot the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 16045) {
            		myPrintf("\nYou killed the dwarf and he disappeared in a cloud of greasy black smoke");
            		IRES[13] += 100;
            	}
            	if (IRIT == 16050) {
            		myPrintf("\nYou just shot and killed the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 20030) myPrintf("\nDon't be silly") ;
            	IPR[2]=0;
            	IPR[3]=0;
            	return "MMSB"
                return "MMSH"
            }
        }
        return "EXIT"
    }
    private suspend fun stepMMSH(lbl: String): String {
        when (lbl) {
            "MMSH" -> {
            	IR=ITST[1];
            	IW71=IWRD[0][10];
            	IW52=IWRD[1][8];
            	IW62=IWRD[1][9];
            	R=RND;
            if (IWRD[0][9] - 35 == 1) return "MMSH_1000"
            if (IWRD[0][9] - 35 == 2) return "MMSH_2000"
            if (IWRD[0][9] - 35 == 3) return "MMSH_3000"
            if (IWRD[0][9] - 35 == 4) return "MMSH_4000"
            if (IWRD[0][9] - 35 == 5) return "MMSH_5000"
            if (IWRD[0][9] - 35 == 6) return "MMSH_6000"
            if (IWRD[0][9] - 35 == 7) return "MMSH_7000"
            if (IWRD[0][9] - 35 == 8) return "MMSH_8000"
            if (IWRD[0][9] - 35 == 9) return "MMSH_9000"
            if (IWRD[0][9] - 35 == 10 || IWRD[0][9] - 35 == 11) return "MMSH_10000"
            if (IWRD[0][9] - 35 == 12) return "MMSH_11000"
            if (IWRD[0][9] - 35 == 13) return "MMSH_12000"
            if (IWRD[0][9] - 35 == 14) return "MMSH_13000"
            if (IWRD[0][9] - 35 == 15) return "MMSH_14000"
            if (IWRD[0][9] - 35 == 16) return "MMSH_16000"
            if (IWRD[0][9] - 35 == 17) return "MMSH_16000"
            if (IWRD[0][9] - 35 == 18) return "MMSH_17000"
            	myPrintf("\nMmsh error: %d\n", IWRD[0][9]-35);
            	pak();
            	return "EXIT"
                return "MMSH_1000"
            }
            "MMSH_1000" -> {
            	if ((IW71 != 1) && ((IW52 != 3) || (IW62 > 35))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            if (IW71 == 1) IW62=24;
            	if (IVEN[IW62] < 20000) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
                        if ((IW62 != 22) && (IW62 != 24) && (IW62 != 28) && (IW62 != 29) && (IW62 != 31)) {
            		IPR[2] = 2010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IVEN[IW62]=0;
            	if (IW62 == 24) {
            		IPR[2] = 1010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 1020;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_2000"
            }
            "MMSH_2000" -> {
                        if (((IW52 != 3) && (IW52 != 4)) || ((IW52 == 4) && (IW62 != 8)) || ((IW52 == 3) && (IVEN[IW62] == 0))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	MMRLret=1;
            	return "MMRL"
                return "RLret1"
            }
            "MMSH_3000" -> {
            	if ((IW71 != 2) || (IW52 != 2) || ((IW62 != 5) && (IW62 != 6))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 5) && (IVEN[23] < 20000)) {
            		IPR[2] = 3050;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 == 5) {
            		ITST[1] = 89;
            		IPR[2] = 3060;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IR != 89) {
            		IPR[2] = 3070;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IC = IFSD(IVEN[26]);
                        if ((IVEN[26] == 789) || (IVEN[26] > 20000) || ((IVEN[26] < 20000) && (IVEN[26] > 10000) && (IFSD(IRES[IC]) == 89))) {
            		IPR[2] = 3080;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            if (IVEN[26] > 10000) IC=IFSD(IRES[IC]) ;
            	if (IC == 0) {
            		IPR[2] = 520;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	ITST[1] = IC;
            	IPR[2] = 3090;
            	IPR[3] = IC;
            	return "MMSI"
                return "MMSH_4000"
            }
            "MMSH_4000" -> {
            	if (IW71 != 1) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IVEN[25] < 20000) {
            		IPR[2] = 4055;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	MMRLret=2;
            	return "MMRL"
                return "RLret2"
            }
            "MMSH_5000" -> {
            	if ((IW52 != 3) || (IW62 < 55) || (IW62 > 58)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IR != 11) {
            		IPR[2] = 8032;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IC=0;
                        if ((IW62 == 55) && (ITST[8] == 0) && (IXT[54] < 20000) && (IFSD(IRES[ITST[9]]) == IR)) {
            		IC=1;
            		IRES[ITST[9]] = 0;
            		ITST[2] = ITST[2]+50;
            		IPR[2] = 5001;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            if ((IW62 == 58) && (IXT[52] != 0) && (ITST[5] > 400)) IXT[52]=0;
            	if ((IXT[52] == 0) && (IW62 == 58)) {
            		IPR[2] = 5002;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 5009;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_6000"
            }
            "MMSH_6000" -> {
            	if ((IW71 != 2) || (IW52 != 3) || (IVEN[IW62] == 0)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	MMRLret=6;
            	return "MMRL"
                return "RLret6"
            }
            "MMSH_7000" -> {
            	if ((IW52 != 3) || (IW62 != 73) || (IW71 != 2)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	MMRLret=7;
            	return "MMRL"
                return "RLret7"
            }
            "MMSH_8000" -> {
            	if ((IW71 != 2) || ((IW52 != 4) && (IW62 != 63))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	MMRLret=8;
            	return "MMRL"
                return "RLret8"
            }
            "MMSH_9000" -> {
            	IPR[2] = 320;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_10000"
            }
            "MMSH_10000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IWRD[0][10] != 2) || (IW52 != 4)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	MMRLret=9;
            	return "MMRL"
                return "RLret9"
            }
            "MMSH_10050" -> {
            for (J in (1).toInt()..(360).toInt()) { this.J = J; 
            if ((IFSD(IXT[J]) != IR) || (IXT[J] > 20000)) continue;
            		IRES[IC] = IRES[IC]-IR+ITFD(IXT[J]);
            		IPR[2] = 10060;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	R=RN(R);
            	if (R < 0.3) {
            		IPR[2] = 10030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IRES[IC] = 10000+IR;
            	IPR[2] = 10040;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_11000"
            }
            "MMSH_11000" -> {
            	if (IW71 > 1) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            for (I in (1).toInt()..(8).toInt()) { this.I = I; 
            for (J in (0).toInt() until (8).toInt()) { this.J = J; 
            			IVRBX[I][J]=IVRB[3+I][J];
            		}
            	}
            if (false) {
            		IPR[2] = 11040;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            // /*
            // * Ignoring return values == Bad.  Fix asap.
            // */
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            	IPR[2] = 11050;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_12000"
            }
            "MMSH_12000" -> {
            	if (IW71 > 1) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (ITST[5] > 1) {
            		IPR[2] = 12050;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IXX = ITST[19];
            if (false) {
            		IPR[2] = 12070;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            // save/load operation stub
            for (I in (1).toInt()..(8).toInt()) { this.I = I; 
            for (J in (0).toInt() until (8).toInt()) { this.J = J; 
            			IVRB[3+I][J]=IVRBX[I][J];
            		}
            	}
            	ourtime(ITIM);
            	ITST[19]=IXX;
            	ITST[18]=((ITIM[4]*1200+ITIM[3]*20+ITIM[2]/3).toInt()).toInt();
            	ITST[20]=ITST[20]+1;
            	IPR[2] = 12040;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_13000"
            }
            "MMSH_13000" -> {
            	if ((IW71 != 1) && (IW62 != 13) && (IW62 != 60) && (IW62 != 62)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW71 > 2) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            if (IW71 == 1) IW62 = 62;
            	if ((IW62 == 60) && (IR != 16)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 == 60) {
            		IXT[128] = 1316;
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSI"
            	}
            	if ((IW62 == 13) && ((IVEN[13] < 20000) || (ITST[10] == 13))) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 == 13) {
            		IPR[2] = 13010;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            	if ((IW62 == 62) && ((IR < 68) || (IR > 83)) && (IR != 99)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 == 62) {
            		IPR[2] = 1010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 320;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_14000"
            }
            "MMSH_14000" -> {
            if (IW71 > 1) return "MMSH_14100"
            	if (ITST[5] > 2) {
            		IPR[2] = 14010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
                return "MMSH_14020"
            }
            "MMSH_14020" -> {
            	myPrintf("\nMystery #? ");
                        doReadLineInput()
            	buf[buf.size - 1] = '\u0000';
            	IC = buf.concatToString().trim().toIntOrNull() ?: 0;
            if (IC == 0 || (IC < 0)) return "MMSH_14020"
            if (IC > 999) return "MMSH_14020"
            	RND = 0.001*IC;
            	myPrintf("\nThis game is now set up for Mystery #%d\n", IC);
            // //fflush(stdout);
            	ITST[23]=IC;
            	IPR[2]=0;
            	IPR[3]=0;
            	return "MMSA"
                return "MMSH_14100"
            }
            "MMSH_14100" -> {
            	if ((IWRD[0][10] > 2) || (IWRD[1][9] != 107)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 14110;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_15000"
            }
            "MMSH_15000" -> {
            	if ((IW71 != 2) || (IW52 != 4)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IFSD(IRES[IW62]) != IR) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IVEN[7] < 20000) {
            		IPR[2] = 15200;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IRES[IW62] > 10000) {
            		IPR[2] = 15400;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IC=0;
            	IG=IW62;
            	IM=ITST[9];
            	IR=ITST[8];
            if ((IM == 2) && (IR > 53) && ((IG == 7) || (IG == 8) || (IG == 9))) IC=1;
            if ((IM == 2) && (IR < 28) && ((IG == 3) || (IG == 8) || (IG == 14))) IC=1;
            if ((IM == 3) && (IR > 18) && ((IG == 2) || (IG == 8) || (IG == 14))) IC=1;
            if ((IM == 3) && (IR < 10) && ((IG == 7) || (IG == 9) || (IG == 14))) IC=1;
            if ((IM == 7) && (IR > 53) && ((IG == 2) || (IG == 8) || (IG == 9))) IC=1;
            if ((IM == 7) && (IR < 10) && ((IG == 3) || (IG == 9) || (IG == 14))) IC=1;
            if ((IM == 8) && (IR > 53) && ((IG == 2) || (IG == 7) || (IG == 9))) IC=1;
            if ((IM == 8) && (IR < 28) && ((IG == 2) || (IG == 3) || (IG == 14))) IC=1;
            if ((IM == 9) && (IR > 53) && ((IG == 2) || (IG == 7) || (IG == 8))) IC=1;
            if ((IM == 9) && (IR < 10) && ((IG == 3) || (IG == 7) || (IG == 14))) IC=1;
            if ((IM == 14) && (IR > 9) && ((IG == 2) || (IG == 3) || (IG == 8))) IC=1;
            if ((IM == 14) && (IR < 10) && ((IG == 3) || (IG == 7) || (IG == 9))) IC=1;
            	if (IC == 0) {
            		IPR[2] = 15400;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IVEN[7] = 0;
            	IRES[IG] = 0;
            	IPR[2] = 15500;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_16000"
            }
            "MMSH_16000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IWRD[0][10] != 2) || (IWRD[1][8] != 4)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IFSD(IRES[IWRD[1][9]]) != IR) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
                        if ((IRES[IWRD[1][9]] >= 1000) || (IWRD[1][9] == 4) || (IWRD[1][9] == 15)) {
            		IPR[2] = 20030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	R=RN(R);
            	if (((ITST[5]-2) > ITST[13]) && (R < 0.5)) {
            		IPR[2] = 23120;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((ITST[5]-4) > ITST[13]) {
            		IPR[2] = 23145;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((ITST[5]-2) > ITST[13]) {
            		IPR[2] = 23140;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IWRD[1][9] == 5) {
            		IPR[2] = 23010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IWRD[1][9] == 10) {
            		IPR[2] = 23015;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IWRD[1][9] == 11) || (IWRD[1][9] == 13)) {
            		IPR[2] = 23020;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            if ((IWRD[1][9] != 6) && (IWRD[1][9] != 12)) return "MMSH_23130"
            	if ((IXT[54] > 20000) && (IR != 93) && (R < 0.3)) {
            		IPR[2] = 23030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IXT[54] > 20000) && (IR != 93) && (R < 0.3)) {
            		IPR[2] = 23040;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IXT[54] < 20000) && (R < 0.3)) {
            		IPR[2] = 23050;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.4) {
            		IPR[2] = 23060;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.5) {
            		IPR[2] = 23070;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.58) {
            		IPR[2] = 23080;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.66) {
            		IPR[2] = 23090;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.74) {
            		IPR[2] = 23100;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.82) {
            		IPR[2] = 23110;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23120;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_23130"
            }
            "MMSH_23130" -> {
            if (IWRD[1][9] != ITST[9]) return "MMSH_23160"
            	if (R < 0.33) {
            		IPR[2] = 23120;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.67) {
            		IPR[2] = 23140;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23150;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_23160"
            }
            "MMSH_23160" -> {
            if ((IWRD[1][9] != 3) && (IWRD[1][9] != 14)) return "MMSH_23250"
            if (R < 0.3) return "MMSH_23200"
            	if ((ITST[8] < 28) && (R < 0.15)) {
            		IPR[2] = 23170;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (ITST[8] < 28) {
            		IPR[2] = 23180;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IVEN[ITST[10]] < 10000) && (IFSD(IVEN[ITST[10]]) < 28)) {
            		IPR[2] = 23190;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23150;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_23200"
            }
            "MMSH_23200" -> {
            	if (R < 0.4) {
            		IPR[2] = 23210;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.5) {
            		IPR[2] = 23220;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.6) {
            		IPR[2] = 23230;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.7) {
            		IPR[2] = 23240;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.8) {
            		IPR[2] = 23140;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23120;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_23250"
            }
            "MMSH_23250" -> {
            if ((IWRD[1][9] != 8) && (IWRD[1][9] != 2)) return "MMSH_23310"
            if (R < 0.3) return "MMSH_23260"
            	if ((ITST[8] > 9) && (R < 0.15)) {
            		IPR[2] = 23170;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (ITST[8] > 9) {
            		IPR[2] = 23180;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IVEN[ITST[10]] < 10000) && (IFSD(IVEN[ITST[10]]) > 9)) {
            		IPR[2] = 23190;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23150;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_23260"
            }
            "MMSH_23260" -> {
            	if (R < 0.4) {
            		IPR[2] = 23270;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.5) {
            		IPR[2] = 23280;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.6) {
            		IPR[2] = 23290;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.7) {
            		IPR[2] = 23300;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.8) {
            		IPR[2] = 23140;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23150;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_23310"
            }
            "MMSH_23310" -> {
            if (R < 0.3) return "MMSH_23320"
            	if (((ITST[8] < 10) || (ITST[8] > 27)) && (R < 0.15)) {
            		IPR[2] = 23170;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((ITST[8] < 10) || (ITST[8] > 27)) {
            		IPR[2] = 23180;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IVEN[ITST[10]] < 10000) && (IFSD(IVEN[ITST[10]]) > 9)) {
            		IPR[2] = 23190;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23150;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_23320"
            }
            "MMSH_23320" -> {
            	if (R < 0.4) {
            		IPR[2] = 23330;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.5) {
            		IPR[2] = 23340;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.6) {
            		IPR[2] = 23350;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.7) {
            		IPR[2] = 23360;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (R < 0.8) {
            		IPR[2] = 23140;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 23150;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_17000"
            }
            "MMSH_17000" -> {
            	if (IWRD[0][10] != 1) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IX = 0; IC = 0;
            	ID=24000+IR;
            if (IR > 53) ID=24100;
            if ((IVEN[3] > 20000) || (IVEN[3] == (200+IR)) || ((IVEN[3] > 10000) && (IFSD(IVEN[3]) == IR))) IX=24108;
            for (I in (2).toInt()..(15).toInt()) { this.I = I; 
            if ((IRES[I] > 10000) || (IFSD(IRES[I]) != IR)) continue;
            		IX=24110;
            if ((I != 4) && (I != 10) && (I != 15)) IC += 1;
            if (IC == 1) IX=24120;
            if (IC < 2) continue;
            if (IC == 2) IX=24130;
            if (IC == 3) IX=24140;
            	}
            if (((IRES[6]+IRES[12]) < 10000) && (IFSD(IRES[6]) == IR) && (IFSD(IRES[12]) == IR)) IX=24150;
            if (((IRES[3]+IRES[14]) < 10000) && (IFSD(IRES[3]) == IR) && (IFSD(IRES[14]) == IR) && (ITST[9] != 3) && (ITST[9] != 14) && (ITST[8] < 28)) IX=24160;
            	RNX=77.7*IR/253.0-(77.7*IR/253.0);
            if ((IX == 24160) && (RNX > 0.8)) IX=24164;
            if ((IX == 24160) && (RNX > 0.6)) IX=24162;
            if (((IRES[7]+IRES[9]) < 10000) && (IFSD(IRES[7]) == IR) && (IFSD(IRES[9]) == IR) && (ITST[9] != 7) && (ITST[9] != 9) && ((ITST[8] < 10) || (ITST[8] > 27)) ) IX=24170;
            if ((IX == 24170) && (RNX > 0.8)) IX=24174;
            if ((IX == 24170) && (RNX > 0.6)) IX=24172;
            if (((IRES[2]+IRES[8]) < 10000) && (IFSD(IRES[2]) == IR) && (IFSD(IRES[8]) == IR) && (ITST[9] != 2) && (ITST[9] != 8) && (ITST[8] > 27)) IX=24180;
            if ((IX == 24180) && (RNX > 0.8)) IX=24184;
            if ((IX == 24180) && (RNX > 0.6)) IX=24182;
            if ((ID == 24100) && (IX != 0)) ID=24105;
            	IPR[2] = ID;
            	IPR[3] = IX;
            	return "MMSI"
                return "MMSI"
            }
        }
        return "EXIT"
    }
    private fun stepRLret1(lbl: String): String {
        when (lbl) {
            "RLret1" -> {
            	if ((IWRD[1][9] == 88) && (IR == 17)) {
            		IPR[2] = 2037;
            		IPR[3] = 0;
            		return "MMSI"
            	}
                        if (((IW52 == 4) && (IFSD(IRES[8]) != IR)) || ((IW52 == 3) && (IW62 > 35) && (IVEN[IW62] != IR))) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW52 == 4) && (IR != 25)) {
            		IPR[2] = 2080;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            if (IW52 == 4) IXT[134] = 2225;
            	if (IW52 == 4) {
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSI"
            	}
            	if ((IW62 < 36) && (IVEN[IW62] < 20000)) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 != 45) {
            		IPR[2] = 520;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 2090;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_3000"
            }
        }
        return "EXIT"
    }
    private fun stepRLret2(lbl: String): String {
        when (lbl) {
            "RLret2" -> {
            	if (IR != 23) {
            		IPR[2] = 4050;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IXT[191] = 2223;
            	IPR[2] = 1095;
            	IPR[3] = IR;
            	return "MMSI"
                return "MMSH_5000"
            }
        }
        return "EXIT"
    }
    private fun stepRLret6(lbl: String): String {
        when (lbl) {
            "RLret6" -> {
            	if (((IW62 == 94) || (IW62 == 97) || (IW62 == 98)) && (IR == 15)) {
            		IPR[2] = 6005;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 95) && (IR == 15) && (IVEN[48] == 0)) {
            		IPR[2] = 6015;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IWRD[1][9] == 88) && (IR == 17)) {
            		IPR[2] = 2037;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 68) && (IR == 27)) {
            		IPR[2] = 6030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 68) && (IR == 88)) {
            		IPR[2] = 6040;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 84) && ((IR < 42) || (IR > 50))) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 84) && (IXT[320] != 0)) {
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	R = RN(R);
            	if ((IW62 == 84) && (R < 0.333) && (IR > 42) && (IR < 47))  {
            		IVEN[38] = 100+IR;
            		IPR[2] = 6010;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 == 84) {
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 83) && ((IXT[320] != 0) || (IR != 52))) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 83) && (IVEN[48] < 20000)) {
            		IPR[2] = 6041;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 83) && (IVEN[36] != 0) && (IXT[319] == 4952)) {
            		IPR[2] = 6042;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 == 83) && (R > 0.67) && (IXT[319] != 0))  {
            		IVEN[36]=152;
            		IPR[2] = 6043;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 == 83) {
            		IPR[2] = 6044;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IVEN[IW62] != IR) && (IW62 > 47)) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IW62 < 54) && (IVEN[IW62] < 20000)) {
            		IPR[2] = 8005;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IW62 == 67) {
            		IXT[187]=1314;
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSI"
            	}
            	IPR[2] = 8010;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_7000"
            }
        }
        return "EXIT"
    }
    private fun stepRLret7(lbl: String): String {
        when (lbl) {
            "RLret7" -> {
            	if (IR != 11) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IC = ITD(IVEN[73]);
            	if (IC != 0) {
            		IVEN[73] = 1000;
            		IPR[2] = 7010;
            		IPR[3] = IC;
            		return "MMSI"
            	}
            	IPR[2] = 7090;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_8000"
            }
        }
        return "EXIT"
    }
    private fun stepRLret8(lbl: String): String {
        when (lbl) {
            "RLret8" -> {
            	if ((IR != 1) && (IW62 == 63)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IR == 1) && (IW62 == 63)) {
            		IXT[1] = 401;
            		IPR[2] = 1095;
            		IPR[3] = IR;
            		return "MMSI"
            	}
            	if (IVEN[24] < 20000) {
            		IPR[2] = 8006;
            		IPR[3] = IR;
            		return "MMSI"
            	}
            	if (IFSD(IRES[IW62]) != IR) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IVEN[24]=309;
            	IPR[2] = 8015;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_9000"
            }
        }
        return "EXIT"
    }
    private fun stepRLret9(lbl: String): String {
        when (lbl) {
            "RLret9" -> {
            	IC=IW62;
            	if (IFSD(IRES[IC]) != IR) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IRES[IC] > 10000) {
            		IPR[2] = 20030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
                        if ((IC == 5) && (IR == 5) && (IRES[5] == 5) && (IVEN[29] >= 20000) && ((IVEN[11] > 20000) || (IVEN[14] > 20000)) && (ITST[5] < 50) && (IXT[183] == 405)) {
            		IPR[2] = 10011;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IC == 5) || (IC == 10)) {
            		IPR[2] = 10020;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if ((IC == 15) || (IC == 10)) {
            		IPR[2] = 10030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IP = IRES[IC]/1000 - ITST[31];
            for (I in (1).toInt()..(89).toInt()) { this.I = I; 
            if ((I != 10) && (I != 11) && (I != 12) && (I != 14) && (I != 34) && (I != 45) && (I != 48) && (I != 49)) continue;
            if (IVEN[I] > 20000) IP += IFD(IVEN[I]) ;
            if ((IFSD(IVEN[I]) == IC) && (IVEN[I] > 10000) && (IVEN[I] < 20000)) IP -= IFD(IVEN[J]) ;
            	}
            	R=RN(R);
            	IP = ((IP+5*R-2).toInt()).toInt();
            if (IP == 6) IP=8;
            if (IP == 7) IP=9;
            if (IP == -6) IP = -8;
            if (IP == -7) IP = -9;
            if (IP > 9) IP = 10;
            if (IP < -9) IP=10;
            if (IP < 0) ITST[31] = -1*IP;
            if (IP > 0) IRES[IC] = IRES[IC] - 1000*(IRES[IC]/1000) +1000*IP;
            if ((abs(IP) < 2) && (IC != 16)) return "MMSH_10050"
            	if (IP < -9) {
            		IPR[2] = 10030;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            if ((IP > 9) && (IC == 13) && (IR == 33)) IRES[13]=534;
            if ((IP > 9) && (IC == 13) && (IR != 33)) IRES[13]=533;
            	if ((IP > 9) && (IC == 13)) {
            		IPR[2] = 16045;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IP > 9) {
            		IPR[2] = 10040;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	if (IP > 0) {
            		IPR[2] = 10041;
            		IPR[3] = 0;
            		return "MMSI"
            	}
            	IPR[2] = 10031;
            	IPR[3] = 0;
            	return "MMSI"
                return "MMSH_10050"
            }
        }
        return "EXIT"
    }
    private fun stepMMSI(lbl: String): String {
        when (lbl) {
            "MMSI" -> {
            	IRIT=IPR[2];
            	IW63=IWRD[2][9];
            	IFC=ITST[4]/1000;
            	IDN=1;
            	IDE=2;
            	IDS=3;
            	IDW=4;
            if (IVEN[6] < 20000) IDN=10-IFC;
            if (IDN == 6) IDN=10;
            if (IVEN[6] < 20000) IDE=11-IFC;
            if (IVEN[6] < 20000) IDS=12-IFC;
            if (IDS == 11) IDS=7;
            if (IVEN[6] < 20000) IDW=13-IFC;
            if (IDW > 10) IDW=IDW-4;
            if (IRIT == 320) myPrintf("\nI cannot figure out what you are trying to say") ;
            if (IRIT == 520) myPrintf("\nNothing happens") ;
            	if (IRIT == 1000) {
            		myPrintf("\nPlease try again. I need to know who or what to ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[0][L]);
            	}
            if (IRIT == 1010) myPrintf("\nThanks. That tasted good.") ;
            if (IRIT == 1020) myPrintf("\nUgh! That tasted awful.") ;
            if (IRIT == 1040) myPrintf("\nOkay") ;
            if (IRIT == 1065) myPrintf("\nProgram error at line %d", IPR[3]) ;
            if ((IPR[3] == 7) || (IPR[3] == 16) || (IPR[3] == 25)) IC = IDS;
            if ((IPR[3] == 5) || (IPR[3] == 14) || (IPR[3] == 23)) IC = IDW;
            if ((IPR[3] == 1) || (IPR[3] == 10) || (IPR[3] == 19)) IC = IDN;
            	if (IRIT == 1095) {
            		myPrintf("\nYour action opened a panel into the secret passage that you can\nEnter by going ");
            for (I in (0).toInt() until (8).toInt())
            			myPutchar(IDTN[IC][I]);
            	}
            	if (IRIT == 1150) {
            		myPrintf("\nIt is dark here\n");
            if (IPR[3] == 1) myPrintf("\nIt is after sunset") ;
            if (IPR[3] == 2) myPrintf("\nA curtain blocks the light") ;
            	}
            	if (IRIT == 2010) {
            		myPrintf("\nYou cannot ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[0][L]);
            		myPrintf(" The ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 2030) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 2037) myPrintf("\nYou must realize by now that the delicate papyrus will crumble when\nYou so much as even blow on it.") ;
            if (IRIT == 2080) myPrintf("\nYou just got slapped by the maid") ;
            if (IRIT == 2090) myPrintf("\nYou are not alladin. Besides, you know how much trouble he got into") ;
            	if (IRIT == 3010) {
            		myPrintf("\nYou do not have the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 9040) ITST[34]=((500.1*RND).toInt()).toInt() ;
            if (IRIT == 3050) myPrintf("\nYou need a matter transmitter to beam up") ;
            if (IRIT == 3060) myPrintf("\nYou dematerialize in seconds and rematerialize in the laboratory") ;
            if (IRIT == 3070) myPrintf("\nYou have to be in the laboratory to beam down") ;
            if (IRIT == 3080) myPrintf("\nThe matter receiver has to be somewhere else to beam down") ;
            	if (IRIT == 3090) {
            		myPrintf("\nYou dematerialize in seconds and rematerialize in the ");
            for (L in (0).toInt() until (16).toInt())
            			myPutchar(IRNM[IFSD(IROM[IPR[3]])][L]);
            	}
            	if (IRIT == 3110) {
            		myPrintf("\nYou no longer have the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 4010) {
            		myPrintf("\nFirst you have to find the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[3][L]);
            	}
            if (IRIT == 4050) myPrintf("\nThanks. It needs that around here.") ;
            if (IRIT == 4055) myPrintf("\nYou don't have a broom or anything to sweep with") ;
            	if (IRIT == 5020) {
            		myPrintf("\nI do not see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[2][L]);
            	}
            	if (IRIT == 5001) {
            		myPrintf("\nThe police came and arrested the murderer");
            		if (ITST[24] != 0) {
            			ITST[1] = 8;
            if (IVEN[2] > 20000) IVEN[2] = 0;
            if (IVEN[9] > 20000) IVEN[9] = 0;
            			myPrintf("\nSince you killed an innocent bystander, the police locked you in\nThe dungeon.");
            		}
            	}
            if (IRIT == 5002) myPrintf("\nThe dispatcher said that a taxi would be there in about 2 hours.") ;
            if (IRIT == 5009) myPrintf("\nAll you can get is a busy signal") ;
            	if (IRIT == 6005) {
            		myPrintf("\nA dozen or so of the indians aiming in your direction, shot their\nArrows and you were hit by several of them and died a painful death.");
            		IPR[2] = 6005;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            if (IRIT == 6010) myPrintf("\nYou just uncovered some gold coins.") ;
            	if (IRIT == 6015) {
            		myPrintf("\nAfter moving the table, a trap door opened revealing a gaudy gauntlet.");
            		IVEN[48]=9115;
            	}
            if (IRIT == 6030) myPrintf("\nUnder the rags is a strange drawing of a compass with the needle bent\nIn a u-shape and a clock with two minute hands, one on the 10 and the other\nOn the 12") ;
            if (IRIT == 6040) myPrintf("\nUnder the rags is a strange drawing of a compass with the needle\nPointing North and a clock with only a minute hand which is on the 12") ;
            if (IRIT == 6041) myPrintf("\nYou cannot quite move any of the boulders.") ;
            	if (IRIT == 6042) {
            		myPrintf("\nIn your greed to find more loot, you managed to move one of the boulders\nIn the wrong direction and it rolled into and blocked the only exit.");
            		IXT[319]=0;
            	}
            if (IRIT == 6043) myPrintf("\nWith a heavy push, you moved one of the boulders a few feet and\nUncovered a bag of pearls in one of the craters that was under it.") ;
            if (IRIT == 6044) myPrintf("\nThanks to the gauntlet, you were able to move a boulder a few feet\nBut there was nothing under it.") ;
            	if (IRIT == 7010) {
            		myPrintf("\nA voice on the line says:");
            		IVEN[42]=1000;
            if (IPR[3] == 1) myPrintf("\nWhy don't you drop the note? You don't need it anymore.") ;
            if (IPR[3] == 2) myPrintf("\nWhy don't you look at some of the items you have") ;
            if (IPR[3] == 3) myPrintf("\nIf I were you, I would watch the murderer and call the police.") ;
            if (IPR[3] == 4) myPrintf("\nIt would probably be wise to go back to the main gate and get\nThe lantern before you go much farther.") ;
            		myPrintf(" And hangs up");
            	}
            if (IRIT == 7090) myPrintf("\nThere is no one on the line") ;
            if (IRIT == 7050) myPrintf(" Nothing") ;
            	if (IRIT == 8005) {
            		myPrintf("\nYou do not have the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[3][L]);
            	}
            if (IRIT == 8006) myPrintf("\nYou do not have any food.") ;
            if (IRIT == 8010) myPrintf("\nI don't know what you expect to happen") ;
            	if (IRIT == 8015) {
            		myPrintf("\nYour food has been eaten by the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 8020) myPrintf("\nIt already is") ;
            	if (IRIT == 8030) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[2][L]);
            	}
            	if (IRIT == 8031) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 8032) myPrintf("\nI don't see how you can do that.") ;
            if (IRIT == 8035) myPrintf("\nThe battery in your lantern is dead") ;
            if (IRIT == 9027) myPrintf("\nYou don't have enough points") ;
            	if (IRIT == 9035) {
            		ITST[25] = 10;
            		myPrintf("\nGive up.\nYou will never figure it out.");
            	}
            if (IRIT == 10020) myPrintf("\nYou have not figured out how to do that yet.") ;
            	if (IRIT == 10030) {
            		myPrintf("\nYou were killed in the struggle");
            		IPR[2] = 10030;
            		IPR[3] = 0;
            		return "MMSD"
            	}
            if (IRIT == 10031) myPrintf("\nYou were wounded in the fight.") ;
            if (IRIT == 10035) myPrintf("\nThe door is locked") ;
            	if (IRIT == 10040) {
            		myPrintf("\n\nYou killed the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            	if (IRIT == 10041) {
            		myPrintf("\n\nYou wounded the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 10045) myPrintf("\nThe door is open") ;
            if (IRIT == 10046) myPrintf("\nThe door is closed") ;
            if (IRIT == 10060) myPrintf("\nYour opponent fled during the struggle") ;
            	if (IRIT == 10011) {
            		myPrintf("\nYou just drove a stake into the Vampire's heart and he has aged\nA thousand years and disappeared.");
            		IRES[5] = 0;
            		IVEN[1] = 105;
            		IVEN[29] = 0;
            		ITST[2] += 75;
            	}
            // save/load operation stub
            if (IRIT == 11050) myPrintf("\nYour current situation has been saved. You may resume this Mystery\nAt a later time by entering restore during your first move of a future game.") ;
            if ((IRIT == 12040) && (ITST[15] == 17)) myPrintf("\nYou are now in the same situation you were in when you suspended\n") ;
            if ((ITST[15] != 17) && (IRIT == 12040)) myPrintf("\nThe data you loaded is for a different revision") ;
            if (IRIT == 12060) myPrintf("\nError %d while restoring frozen game", IPR[3]) ;
            if (IRIT == 14010) myPrintf("\nYou can set the Mystery on the first move. You are in Mystery #%d", ITST[23]) ;
            if (IRIT == 12070) myPrintf("\nI was unable to find your old game file!") ;
            if (IRIT == 14110) myPrintf("\nThat is the name of the game.") ;
            if (IRIT == 15110) myPrintf("\nYour key will not fit the padlock") ;
            	if (IRIT == 15200) {
            		myPrintf("\nYou do not have anything of interest to the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[IWRD[1][9]][L]);
            	}
            	if (IRIT == 15400) {
            		myPrintf("\nYou can find out nothing from the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[IWRD[1][9]][L]);
            	}
            	if ((IRIT == 15500) && (ITST[8] != 0)) {
            		myPrintf("\nAfter a slight pause but taking your treasure, the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[IWRD[1][9]][L]);
            		myPrintf("\nTells you that the scene of the murder is the ");
            for (L in (0).toInt() until (16).toInt())
            			myPutchar(IRNM[IFSD(IROM[ITST[8]])][L]);
            		myPrintf("\nAnd that the murder was committed by the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[ITST[9]][L]);
            		myPrintf("\nWith the %s", IWP);
            	}
            	if ((IRIT == 15500) && (ITST[8] == 0)) {
            		myPrintf("\nAfter a slight pause but taking your treasure, the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[IWRD[1][9]][L]);
            		myPrintf("\nTells you that you might have to exit through the attic.");
            	}
            	if (IRIT == 16035) {
            		myPrintf("\nYou just smashed the padlock to pieces.");
            		IVEN[35] += 1000;
            	}
            	if (IRIT == 16045) {
            		myPrintf("\nYou killed the dwarf and he disappeared in a cloud of greasy black smoke");
            		IRES[13] += 100;
            	}
            if (IRIT == 20010) myPrintf("\nYou need an oilcan or something") ;
            if (IRIT == 20020) myPrintf("\nYour oilcan is empty") ;
            if (IRIT == 20030) myPrintf("\nDon't be silly") ;
            if (IRIT == 22010) myPrintf("\nThe chest is not where you can break the padlock") ;
            if (IRIT == 22020) myPrintf("\nYou do not have anything that will break the padlock") ;
            if (IRIT == 23010) myPrintf("\nThe Vampire says that he is count dracula as he walks slowly towards you,\nHolding you motionless in his wide eyed gaze.") ;
            if (IRIT == 23015) myPrintf("\nThe big ugly thing just grunts as he walks towards you.") ;
            if (IRIT == 23020) myPrintf("\nThe evil little thing just looks up at you and laughs.") ;
            if (IRIT == 23030) myPrintf("\nHe says that he will follow you to the main gate and help you open it, if_var that is what you want.") ;
            if (IRIT == 23040) myPrintf("\nHe says that the gate is heavy and he will help you open it,\nIf you tell your invisible guide: Open the gate.") ;
            if (IRIT == 23050) myPrintf("\nHe says that he always closes the front gate when he is\nThere with a friend.") ;
            if (IRIT == 23060) myPrintf("\nHe says that you can read the map that is in the cottage.") ;
            if (IRIT == 23070) myPrintf("\nHe says to watch out for the 500 foot sheer cliff around the\nEastern side of the Mansion.") ;
            if (IRIT == 23080) myPrintf("\nHe says that he is afraid to go into the cottage.") ;
            if (IRIT == 23090) myPrintf("\nHe says that he knows better than to go into the dense woods.") ;
            if (IRIT == 23100) myPrintf("\nHe says that he is not allowed to go into the garden.") ;
            if (IRIT == 23110) myPrintf("\nThe response is that the front porch is dangerous.") ;
            	if (IRIT == 23120) {
            		myPrintf("\nYour questions are starting to irritate the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if (IRIT == 23140) myPrintf("\nYour last question fell on deaf ears.") ;
            	if (IRIT == 23145) {
            		myPrintf("\nYour questions are really bothering the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[IWRD[1][9]][L]);
            	}
            	if (IRIT == 23150) {
            		myPrintf("\nI know nothing about the murder. Answers the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IWRD[1][L]);
            	}
            if ((IRIT >= 23170) && (IRIT <= 23190)) myPrintf("\nYour question is answered with a hesitant:") ;
            	if (IRIT == 23170) {
            		myPrintf("\nI think the murderer is the ");
            for (L in (0).toInt() until (8).toInt())
            			myPutchar(IRSN[ITST[9]][L]);
            	}
            	if (IRIT == 23180) {
            		myPrintf("\nI think the murder was committed in the ");
            for (L in (0).toInt() until (16).toInt())
            			myPutchar(IRNM[IFSD(IROM[ITST[8]])][L]);
            	}
            if (IRIT == 23190) myPrintf("\nI think the murder weapon is the %s", IWP) ;
            if (IRIT == 23210) myPrintf("\nHe says that he doesn't want to go out of the Mansion.") ;
            if (IRIT == 23220) myPrintf("\nHe says that there is an entrace to a secret laboratory off the large bedroom.") ;
            if (IRIT == 23230) myPrintf("\nHe says that a secret passage connects all three levels of the Mansion.") ;
            if (IRIT == 23240) myPrintf("\nHe says that the maid can be very friendly.") ;
            if (IRIT == 23270) myPrintf("\nShe says she is afraid to go below the main floor of the Mansion.") ;
            if (IRIT == 23280) myPrintf("\nShe says that she will not leave the Mansion and garden.") ;
            if (IRIT == 23290) myPrintf("\nShe says that she can read the writing on the chest.") ;
            if (IRIT == 23300) myPrintf("\nShe says that the elf hides things in the well.") ;
            if (IRIT == 23330) myPrintf("\nHe says that there is treasure at the end of the corridor.") ;
            if (IRIT == 23340) myPrintf("\nHe says that you can distract the wolf with food.") ;
            if (IRIT == 23350) myPrintf("\nHe says that you can tell the wells apart by dropping items near them.") ;
            if (IRIT == 23360) myPrintf("\nHe says that you can read the scroll by using magic words.") ;
            if (IRIT == 24001) myPrintf("\nYou can hear the chatter of the monkey.") ;
            if (IRIT == 24002) myPrintf("\nYou can hear the roar of the fire in the furnace.") ;
            if (IRIT == 24003) myPrintf("\nYou can hear strange sounds coming up from below you.") ;
            if ((IRIT == 24004) || (IRIT == 24013) || (IRIT == 24022)) myPrintf("\nYou can hear the creaking of wood as you move about.") ;
            if (IRIT == 24005) myPrintf("\nIt is deathly quiet here.") ;
            if (IRIT == 24006) myPrintf("\nYou can hear someone walking in the room above you.") ;
            if (IRIT == 24007) myPrintf("\nYou can only hear the noise you make bumping into things.") ;
            if (IRIT == 24008) myPrintf("\nYou can hear the squeaking of several rats as they scurry about.") ;
            if (IRIT == 24009) myPrintf("\nYou can hear some kind of animal scratching to get in.") ;
            if (IRIT == 24010) myPrintf("\nYou can hear but really sense the soft comfortable surroundings.") ;
            if (IRIT == 24011) myPrintf("\nYou can hear a slight echo high above you.") ;
            if ((IRIT == 24012) && (IFD(IVEN[75]) == 1)) myPrintf("\nYou can hear the radio playing.") ;
            if ((IRIT == 24012) && (IFD(IVEN[75]) != 1)) myPrintf("\nYou can hear what sounds like thunder in the mountains that are nearby.") ;
            if ((IRIT == 24014) && (IFD(IVEN[89]) == 1)) myPrintf("\nYou can hear the roar of the fire in the fireplace.") ;
            if ((IRIT == 24014) && (IFD(IVEN[89]) != 1)) myPrintf("\nYou can hear your echo as you walk about the room.") ;
            if (IRIT == 24015) myPrintf("\nYou can hear someone making noise below the floor.") ;
            if (IRIT == 24016) myPrintf("\nYou can hear soft organ music.") ;
            if (IRIT == 24017) myPrintf("\nYou can hear the faint sound of organ music.") ;
            if (IRIT == 24018) myPrintf("\nYou can hear a faucet dripping.") ;
            if (IRIT == 24019) myPrintf("\nYou can hear the sound of your heartbeat.") ;
            if ((IRIT == 24020) || (IRIT == 24024) || (IRIT == 24025)) myPrintf("\nYou can hear the wind blowing outside the window to the ") ;
            if (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IDTN[IDS][L]) ; }
            if (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IDTN[IDE][L]) ; }
            if (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IDTN[IDN][L]) ; }
            if (IRIT == 24021) myPrintf("\nYou can hear the wind whistling between your ears.") ;
            if ((IRIT == 24023) || (IRIT == 24026)) myPrintf("\nYou can hear the sounds of chains rattling above you.") ;
            if (IRIT == 24027) myPrintf("\nYou can hear yourself breathing.") ;
            if (IRIT == 24028) myPrintf("\nYou can hear six distinct echoes for each sound you make.") ;
            if (IRIT == 24029) myPrintf("\nYou can hear five distinct echoes for each sound you make.") ;
            if (IRIT == 24030) myPrintf("\nYou can hear four distinct echoes for each sound you make.") ;
            if (IRIT == 24031) myPrintf("\nYou can hear three distinct echoes for each sound you make.") ;
            if (IRIT == 24032) myPrintf("\nYou can hear two distinct sounds for each sound you make.") ;
            if ((IRIT == 24033) || (IRIT == 24034)) myPrintf("\nYou can hear an echo for each sound you make.") ;
            if ((IRIT > 24035) && (IRIT < 24041)) myPrintf("\nYou can hear the sound of the wall closing.") ;
            if (IRIT == 24041) myPrintf("\nYou can hear water flowing below you.") ;
            if ((IRIT > 24041) && (IRIT < 24054) && (IRES[4] != 0)) myPrintf("\nYou can hear something digging nearby.") ;
            if ((IRIT > 24041) && (IRIT < 24054) && (IRES[4] == 0)) myPrintf("\nYou can hear creaking like the tunnel is going to cave in.") ;
            if (IRIT == 24088) myPrintf("\nYou can hear the faint sound of machinery.") ;
            if (IRIT == 24100) myPrintf("\nYou can hear nothing of interest.") ;
            if (IRIT == 24105) myPrintf("") ;
            if (IPR[3] == 24108) myPrintf("\nYou can hear a clock ticking.") ;
            if (IPR[3] == 24110) myPrintf("\nYou can hear something making noise nearby.") ;
            if (IPR[3] == 24120) myPrintf("\nYou can hear someone making noise nearby.") ;
            if (IPR[3] == 24130) myPrintf("\nYou can hear someone whispering nearby.") ;
            if (IPR[3] == 24140) myPrintf("\nYou can hear someone talking nearby.") ;
            if (IPR[3] == 24150) myPrintf("\nYou can hear the woodsman and hunter laughing and saying something\nAbout closing the gate.") ;
            	if (IPR[3] == 24160) {
            		myPrintf("\nYou can hear the master and the butler saying something about the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IRSN[ITST[9]][L]); }
            		myPrintf(" And murder.");
            	}
            	if (IPR[3] == 24162) {
            		myPrintf("\nYou can hear the master and the butler saying something about the\nScene of the murder being the ");
            for (L in (0).toInt() until (16).toInt()) { this.L = L; myPutchar(IRNM[IFSD(IROM[ITST[8]])][L]); }
            	}
            if (IPR[3] == 24164) myPrintf("\nYou hear the master telling the butler about a murder with the %s", IWP) ;
            	if (IPR[3] == 24170) {
            		myPrintf("\nYou can hear the cook and the gardener saying something about the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IRSN[ITST[9]][L]); }
            		myPrintf("\nAnd murder.");
            	}
            	if (IPR[3] == 24172) {
            		myPrintf("\nYou can hear the cook and the gardener saying something about the\nScene of the murder being the ");
            for (L in (0).toInt() until (16).toInt()) { this.L = L; myPutchar(IRNM[IFSD(IROM[ITST[8]])][L]); }
            	}
            if (IPR[3] == 24174) myPrintf("\nYou hear the cook telling the gardener about a murder with the %s", IWP) ;
            	if (IPR[3] == 24180) {
            		myPrintf("\nYou can hear the lady and the maid saying something about the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IRSN[ITST[9]][L]); }
            		myPrintf("\nAnd murder.");
            	}
            	if (IPR[3] == 24182) {
            		myPrintf("\nYou can hear the lady and the maid saying something about the\nScene of the murder being the ");
            for (L in (0).toInt() until (16).toInt()) { this.L = L; myPutchar(IRNM[IFSD(IROM[ITST[8]])][L]); }
            		myPrintf("\nAnd murder.");
            	}
            if (IPR[3] == 24184) myPrintf("\nYou hear the lady telling the maid about a murder with the %s", IWP) ;
            	IPR[2] = 0;
            	IPR[3] = 0;
            	if (IRIT == 12040) {
            		IWRD[0][9] = 25;
            		return "MMSC"
            	}
            	return "MMSB"
                return "MMSJ"
            }
        }
        return "EXIT"
    }
    private fun stepMMSJ(lbl: String): String {
        when (lbl) {
            "MMSJ" -> {
            	IR=ITST[1];
            	R=RND;
            if (IWRD[0][9] - 53 == 1 || IWRD[0][9] - 53 == 2) return "MMSJ_1000"
            if (IWRD[0][9] - 53 == 3) return "MMSJ_2000"
            if (IWRD[0][9] - 53 == 4) return "MMSJ_3000"
            if (IWRD[0][9] - 53 == 5) return "MMSJ_4000"
            if (IWRD[0][9] - 53 == 6) return "MMSJ_5000"
            if (IWRD[0][9] - 53 == 7) return "MMSJ_6000"
            if (IWRD[0][9] - 53 == 8) return "MMSJ_7000"
                return "MMSJ_1000"
            }
            "MMSJ_1000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[0][10] == 1) || (IWRD[1][8] != 3)) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            if (IWRD[1][9] != 92) return "MMSJ_15100"
                        if (((IR > 27) && (IR != 36) && (IR != 54) && (IR != 91) && (IR != 90)) || (IR == 3) || (IR == 4) || (IR == 13) || (IR == 16) || (IR == 21) || (IR == 22)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            for (J in (1).toInt()..(240).toInt()) { this.J = J; 
            if (IFSD(IXT[J]) != IR) continue;
            if (((J-1)/60+1) != (ITST[4]/1000)) continue;
            		IC=ITFD(IXT[J]);
                        if ((IC == 4) || (IC == 13) || (IC == 22) || (J == 62) || (J == 70) || (J == 133) || (J == 182) || (J == 190)) {
            			IPR[2] = 15030;
            			IPR[3] = 0;
            			return "MMSK"
            		}
            		if (IXT[J] < 10000) {
            			IPR[2] = 15020;
            			IPR[3] = 0;
            			return "MMSK"
            		}
                        if (((IXT[J] < 20000) && (IWRD[0][9] == 54)) || ((IXT[J] > 20000) && (IWRD[0][9] == 55))) {
            			IPR[2] = 8020;
            			IPR[3] = 0;
            			return "MMSK"
            		}
            		if ((IVEN[46] < 20000) && (J == 64)) {
            			IPR[2] = 15041;
            			IPR[3] = 0;
            			return "MMSK"
            		}
            		if ((IVEN[2] < 20000) && (J != 64)) {
            			IPR[2] = 15040;
            			IPR[3] = 0;
            			return "MMSK"
            		}
            if (IWRD[0][9] == 54) IXT[J] = IXT[J] - 10000;
            if (IWRD[0][9] == 55) IXT[J] = IXT[J] + 10000;
            if ((J < 121) && (IWRD[0][9] == 54)) IXT[J+120] = IXT[J+120] - 10000;
            if ((J > 120) && (IWRD[0][9] == 54)) IXT[J-120] = IXT[J-120] - 10000;
            if ((J < 121) && (IWRD[0][9] == 55)) IXT[J+120] = IXT[J+120] + 10000;
            if ((J > 120) && (IWRD[0][9] == 55)) IXT[J-120] = IXT[J-120] + 10000;
            		IPR[2] = 1040;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IPR[2] = 15030;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_15100"
            }
            "MMSJ_15100" -> {
            if ((IWRD[1][9] != 54) && (IWRD[1][9] != 35)) return "MMSJ_15200"
            	if ((IVEN[35] < 20000) && (IVEN[35] != (600+IR))) {
            		IPR[2] = 8030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[0][9] == 55) && (IFD(IVEN[35]) == 0)) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IWRD[0][9] == 55) {
            		IPR[2] = 2010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IVEN[2] < 20000) {
            		IPR[2] = 15040;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IPR[2] = 15110;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_15200"
            }
            "MMSJ_15200" -> {
            	IPR[2] = 8010;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_2000"
            }
            "MMSJ_2000" -> {
            	if (IWRD[0][10] == 1) {
            		IPR[2] = 1000;
            		IPR[3] = 0;
            		return "MMSK"
            	}
                        if ((IWRD[0][10] != 2) || ((IWRD[1][8] != 4) && (IWRD[1][8] != 3)) || ((IWRD[1][8] == 3) && (IWRD[1][9] != 54))) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IVEN[9] > 20000) {
            		IPR[2] = 16010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	MMRLret=3;
            	return "MMRL"
                return "RLret3"
            }
            "MMSJ_16020" -> {
            if (IWRD[1][9] == 10) IRES[ITST[39]] = 10000+IR;
            if (IWRD[1][9] == 10) ITST[2] = ITST[2]+75;
                        if ((IWRD[1][9] == 16) && (R < 0.8) && (IVEN[45] == 14416) && (IRES[16]/1000 < 4)) {
            		IPR[2] = 16046;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IRES[IWRD[1][9]] = 10000+IR;
            if (IWRD[1][9] == 10) IRES[10] = 0;
            	IPR[2] = 16050;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_3000"
            }
            "MMSJ_3000" -> {
            	if ((IWRD[0][10] != 2) || (IWRD[1][8] != 3)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	MMRLret=4;
            	return "MMRL"
                return "RLret4"
            }
            "MMSJ_4000" -> {
            	IPR[2] = 320;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_5000"
            }
            "MMSJ_5000" -> {
            	if ((IWRD[0][10] != 2) || (IWRD[1][8] != 3) || (IWRD[1][9] != 54)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	MMRLret=5;
            	return "MMRL"
                return "RLret5"
            }
            "MMSJ_6000" -> {
                        if ((IWRD[0][10] != 4) || (IWRD[1][8] != 3) || (IWRD[2][8] != 5) || (IWRD[3][8] != 3)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            if ((IWRD[1][9] != 17) || (IWRD[2][9] != 1) || (IWRD[3][9] != 66)) return "MMSJ_6100"
            	if (IVEN[17] < 20000) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IR != 41) {
            		IPR[2] = 6010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IVEN[17] = 1141;
            	IPR[2] = 6020;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_6100"
            }
            "MMSJ_6100" -> {
            if ((IWRD[1][9] != 46) || (IWRD[2][9] != 2) || (IWRD[3][9] != 85)) return "MMSJ_6200"
            	if (IVEN[46] < 20000) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IR != 1) {
            		IPR[2] = 6010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IVEN[46] = 1101;
            	IPR[2] = 1040;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_6200"
            }
            "MMSJ_6200" -> {
            if ((IWRD[1][9] != 5) || (IWRD[2][9] != 2) || (IWRD[3][9] != 77)) return "MMSJ_6300"
            	if (IVEN[5] < 20000) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IR != 16) {
            		IPR[2] = 6010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IVEN[5] = 1316;
            	IPR[2] = 1040;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_6300"
            }
            "MMSJ_6300" -> {
            if ((IWRD[1][9] != 4) || (IWRD[2][9] != 2) || (IWRD[3][9] != 85)) return "MMSJ_6900"
            	if (IVEN[4] < 20000) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IR != 93) {
            		IPR[2] = 6010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            if (IFD(IVEN[4]) == 1) IVEN[4]=1493;
            if (IFD(IVEN[4]) != 1) IVEN[4]=493;
            	IPR[2] = 1040;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_6900"
            }
            "MMSJ_6900" -> {
            	IPR[2] = 320;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_7000"
            }
            "MMSJ_7000" -> {
            if (IWRD[0][10] == 1) return "MMSJ_7500"
                        if ((IWRD[0][10] != 3) || (IWRD[1][8] != 5) || (IWRD[1][9] != 13) || (IWRD[2][8] != 4)) {
            		IPR[2] = 320;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IFSD(IRES[IWRD[2][9]]) != IR) {
            		IPR[2] = 8031;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[2][9] == 1) || (IWRD[2][9] == 15) || (IWRD[2][9] == 4)) {
            		IPR[2] = 7010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IR != 1) && (IR != 20) && (IR != 24) && (IR != 25)) {
            		IPR[2] = 7020;
            		IPR[3] = 0;
            		return "MMSK"
            	}
                        if ((ITST[32] != 1) || ((IR == 1) && (IXT[61] < 20000)) || ((IR == 20) && ((IXT[12] < 20000) || (IROM[20] > 20000))) || ((IR == 25) && ((IXT[73] < 20000) || (IROM[25] > 20000))) || (IR == 24)) {
            		IPR[2] = 7030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            for (I in (1).toInt()..(53).toInt()) { this.I = I; 
            		if (IVEN[I] >= 20000) {
            			IPR[2] = 7030;
            			IPR[3] = 0;
            			return "MMSK"
            		}
            	}
            for (I in (1).toInt()..(17).toInt()) { this.I = I; 
            		if ((IFSD(IRES[I]) == IR) && (IWRD[2][9] != I)) {
            			IPR[2] = 7030;
            			IPR[3] = 0;
            			return "MMSK"
            		}
            	}
            if ((IWRD[2][9] == 3) || (IWRD[2][9] == 7) || (IWRD[2][9] == 14)) ITST[32]=2;
            if ((IWRD[2][9] == 2) || (IWRD[2][9] == 8) || (IWRD[2][9] == 9)) ITST[32]=3;
            	ITST[5] = ITST[5]+25;
            	IPR[2] = 7040;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_7500"
            }
            "MMSJ_7500" -> {
            	if ((IR != 1) && (IR != 10) && (IR != 20) && (IR != 24) && (IR != 25)) {
            		IPR[2] = 7510;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            for (I in (2).toInt()..(17).toInt()) { this.I = I; 
            		if (IFSD(IRES[I]) == I) {
            			IPR[2] = 7530;
            			IPR[3] = 0;
            			return "MMSK"
            		}
            	}
            	ITST[5] = ITST[5]+25;
            	IPR[2] = 7540;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSK"
            }
        }
        return "EXIT"
    }
    private fun stepRLret3(lbl: String): String {
        when (lbl) {
            "RLret3" -> {
            	if ((IFSD(IRES[IWRD[1][9]]) != IR) && (IWRD[1][8] != 3)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[1][8] == 3) && (IFD(IVEN[35]) != 0)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IC=0;
            if (IVEN[20] > 20000) IC=1;
            if (IWRD[1][9] == 10) return "MMSJ_16020"
            	if ((ITST[11] == 0) && (IVEN[20] < 20000)) {
            		IPR[2] = 16020;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            if ((ITST[11] == 0) && (IWRD[1][9] == 10) && (IVEN[20] <  20000)) IVEN[20] = 0;
            if (((ITST[11] > 0) && (IWRD[1][9] != 10)) || ((IWRD[1][9] == 10) && (IVEN[20] < 20000))) ITST[11] -= 1;
            	R=RN(R);
            	if (R < 0.2) {
            		IPR[2] = 16030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((R < 0.4) && (IRES[IWRD[1][9]]/1000 == 0)) {
            		IRES[IWRD[1][9]] = IRES[IWRD[1][9]] + 5000;
            		IPR[2] = 16031;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IWRD[1][8] == 3) {
            		IPR[2] = 16035;
            		IPR[3] = 0;
            		return "MMSK"
            	}
                        if ((IWRD[1][9] == 1) || (IWRD[1][9] == 5) || ((IWRD[1][9] == 10) && (IC == 0)) || (IRES[IWRD[1][9]] > 10000)) {
            		IPR[2] = 16040;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            if ((IWRD[1][9] == 13) && (IR != 33)) IRES[13] = 533;
            if ((IWRD[1][9] == 13) && (IR == 33)) IRES[13] = 534;
            	if (IWRD[1][9] == 13) {
            		IPR[2] = 16045;
            		IPR[3] = 0;
            		return "MMSK"
            	}
                return "MMSJ_16020"
            }
        }
        return "EXIT"
    }
    private fun stepRLret4(lbl: String): String {
        when (lbl) {
            "RLret4" -> {
            	if (IVEN[33] < 20000) {
            		IPR[2] = 20010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if (IVEN[33] > 20000) {
            		IPR[2] = 20020;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[1][9] < 54) && (IVEN[IWRD[1][9]] < 20000)) {
            		IPR[2] = 3010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
                        if ((IWRD[1][9] > 47) && (IFSD(IVEN[IWRD[1][9]]) != IR) && (IFSD(IVEN[IWRD[1][9]]) != 0)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[1][9] > 45) && (IWRD[1][9] < 51)) {
            		IPR[2] = 20030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[1][9] == 91) && (IROM[IR] < 10000)) {
            		IPR[2] = 2030;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IWRD[1][9] == 92) || (IWRD[1][9] == 93)) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            if ((IWRD[1][9] == 74) && (IR == 85)) IXT[322] = 9185;
            if ((IWRD[1][9] == 74) && (IR == 86)) IXT[322] = 8686;
            	IVEN[74] = IVEN[74] + 1000;
            	IPR[2] = 1040;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_4000"
            }
        }
        return "EXIT"
    }
    private fun stepRLret5(lbl: String): String {
        when (lbl) {
            "RLret5" -> {
            	if (IFD(IVEN[35]) == 1) {
            		IPR[2] = 8020;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	if ((IFSD(IVEN[35]) != 1) || (IVEN[35] > 10000)) {
            		IPR[2] = 22010;
            		IPR[3] = 0;
            		return "MMSK"
            	}
                        if ((IVEN[11] < 20000) && (IVEN[12] < 20000) && (IVEN[14] < 20000) && (IVEN[32] < 20000) && (IVEN[34] < 20000)) {
            		IPR[2] = 22020;
            		IPR[3] = 0;
            		return "MMSK"
            	}
            	IPR[2] = 16035;
            	IPR[3] = 0;
            	return "MMSK"
                return "MMSJ_6000"
            }
        }
        return "EXIT"
    }
    private fun stepMMSK(lbl: String): String {
        when (lbl) {
            "MMSK" -> {
            	IRIT=IPR[2];
            	IW63=IWRD[2][9];
            	IFC=ITST[4]/1000;
            	IDN=1;
            	IDE=2;
            	IDS=3;
            	IDW=4;
            if (IVEN[6] < 20000) IDN=10-IFC;
            if (IDN == 6) IDN=10;
            if (IVEN[6] < 20000) IDE=11-IFC;
            if (IVEN[6] < 20000) IDS=12-IFC;
            if (IDS == 11) IDS=7;
            if (IVEN[6] < 20000) IDW=13-IFC;
            if (IDW > 10) IDW=IDW-4;
            if (IRIT == 320) myPrintf("\nI cannot figure out what you are trying to say") ;
            if (IRIT == 520) myPrintf("\nNothing happens") ;
            	if (IRIT == 1000) {
            		myPrintf("\nI am not a mind reader, you will have to tell me what to ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[0][L]); }
            	}
            if (IRIT == 1040) myPrintf("\nOkay") ;
            if (IRIT == 1065) myPrintf("\nProgram error at line %d", IPR[3]) ;
            if ((IPR[3] == 7) || (IPR[3] == 16) || (IPR[3] == 25)) IC=IDS;
            if ((IPR[3] == 5) || (IPR[3] == 14) || (IPR[3] == 23)) IC=IDW;
            if ((IPR[3] == 1) || (IPR[3] == 10) || (IPR[3] == 19)) IC=IDN;
            	if (IRIT == 1095) {
            		myPrintf("\nYour action opened a panel into the secret passage that you can\nEnter by going ");
            for (I in (0).toInt() until (8).toInt()) { this.I = I; myPutchar(IDTN[IC][I]); }
            	}
            	if (IRIT == 2010) {
            		myPrintf("\nYou cannot ");
            for (L in (0).toInt() until (4).toInt()) { this.L = L; myPutchar(IWRD[0][L]); }
            		myPrintf(" The ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            	if (IRIT == 2020) {
            		myPrintf("\nYou already have the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            	if (IRIT == 2025) {
            		myPrintf("\nIt is not in the possession of the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[3][L]); }
            	}
            	if (IRIT == 2030) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            	if (IRIT == 3010) {
            		myPrintf("\nYou do not have the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            	if (IRIT == 3110) {
            		myPrintf("\nYou no longer have the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            	if (IRIT == 4010) {
            		myPrintf("\nFirst you have to find the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[3][L]); }
            	}
            if (IRIT == 4050) myPrintf("\nThanks. It needs that around here.") ;
            	if (IRIT == 5020) {
            		myPrintf("\nI do not see the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[2][L]); }
            	}
            if (IRIT == 6010) myPrintf("\nYou must not be paying attention.") ;
            if (IRIT == 6020) myPrintf("\nThe floor under you moved slightly as if_var the amulet activated something.") ;
            if (IRIT == 7010) myPrintf("\nYou must be some kind of pervert.") ;
            	if (IRIT == 7020) {
            		myPrintf("\n'This is not the time or place.' Says the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[2][L]); }
            	}
            	if (IRIT == 7030) {
            		myPrintf("\n'Not now. Something is not right.' Says the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[2][L]); }
            	}
            	if (IRIT == 7040) {
            		myPrintf("\nYou feel yourself getting aroused as you watch a sexy strip by the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[2][L]); }
            		myPrintf("\nBefore pulling you into bed. Eagerly, you caress each other and .........\n\nAn hour later, after many sexual delights and a nap; you see the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[2][L]); }
            		myPrintf("\nFinish dressing, giving you a thankful smile as you wake up.");
            	}
            if (IRIT == 7050) myPrintf("\nNothing") ;
            if (IRIT == 7510) myPrintf("\nYou cannot get comfortable here.") ;
            if (IRIT == 7530) myPrintf("\nSomeone in the room is keeping you awake.") ;
            if (IRIT == 7540) myPrintf("\nYou sleep for an hour and wake-up refreshed.") ;
            if (IRIT == 8010) myPrintf("\nI don't know what you expect to happen") ;
            	if (IRIT == 8015) {
            		myPrintf("\nYour food has been eaten by the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[2][L]); }
            	}
            if (IRIT == 8020) myPrintf("\nIt already is") ;
            	if (IRIT == 8030) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            	if (IRIT == 8031) {
            		myPrintf("\nI don't see the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[2][L]); }
            	}
            if (IRIT == 15030) myPrintf("\nYou are not facing a door") ;
            if (IRIT == 15020) myPrintf("\nThe door you are facing has no lock") ;
            if (IRIT == 15040) myPrintf("\nYou need a door key") ;
            if (IRIT == 15041) myPrintf("\nYou need a dungeon key") ;
            if (IRIT == 15110) myPrintf("\nYour key will not fit the padlock") ;
            if (IRIT == 16010) myPrintf("\nYou need a gun to shoot anyone") ;
            if (IRIT == 16020) myPrintf("\nYour gun is out of bullets") ;
            if (IRIT == 16030) myPrintf("\nYou missed") ;
            	if (IRIT == 16031) {
            		myPrintf("\nYou wounded the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            if (IRIT == 16035) myPrintf("\nYou just smashed the padlock to pieces") ;
            if (IRIT == 16035) IVEN[35] += 1000;
            	if (IRIT == 16040) {
            		myPrintf("\nYou just wasted a shot. It did you no good to shoot the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            if (IRIT == 16045) myPrintf("\nYou killed the dwarf and he disappeared in a cloud of greasy black smoke") ;
            if (IRIT == 16046) myPrintf("\nThe bullet bounced off the warrior's shield.") ;
            if (IRIT == 16045) IRES[13] += 100;
            	if (IRIT == 16050) {
            		myPrintf("\nYou just shot and killed the ");
            for (L in (0).toInt() until (8).toInt()) { this.L = L; myPutchar(IWRD[1][L]); }
            	}
            if (IRIT == 20010) myPrintf("\nYou need an oilcan or something") ;
            if (IRIT == 20020) myPrintf("\nYour oilcan is empty") ;
            if (IRIT == 20030) myPrintf("\nDon't be silly") ;
            if (IRIT == 21020) myPrintf("\nLu recording not supported") ;
            if (IRIT == 22010) myPrintf("\nThe chest is not where you can break the padlock") ;
            if (IRIT == 22020) myPrintf("\nYou do not have anything that will break the padlock") ;
            	IPR[2] = 0; IPR[3] = 0;
            	return "MMSB"
                return "MMRL"
            }
        }
        return "EXIT"
    }
    private fun stepMMRL(lbl: String): String {
        when (lbl) {
            "MMRL" -> {
            	IR=ITST[1];
            for (I in (1).toInt()..(3).toInt()) { this.I = I; 
            if (I == 1) K=4;
            if (I == 2) K=19;
            if (I == 3) K=27;
                        if ((IFD(IVEN[K]) == 1) && (((IVEN[K] < 10000) && (IFSD(IVEN[K]) == IR)) || (IVEN[K] > 20000) || ((IVEN[K] > 10000) && (IVEN[K] < 20000) && (IFSD(IRES[IFSD(IVEN[K])]) == IR)))) {
            			return "MMRL_return"
            		}
            	}
            if ((IR == 10) && (IVEN[76] > 1000)) return "MMRL_return"
            if ((IR == 14) && (IVEN[89] > 1000)) return "MMRL_return"
            if (IVEN[31] > 21000) return "MMRL_return"
            if ((IR > 53) && (ITST[5] < 300)) return "MMRL_return"
            	if (IR > 53) {
            		IPR[2]=1150;
            		IPR[3]=1;
            		return "MMSD"
            	}
            if ((IROM[IR] > 20000) && (ITST[5] < 300)) return "MMRL_return"
            	if (IROM[IR] > 20000) {
            		IPR[2]=1150;
            		IPR[3]=1;
            		return "MMSD"
            	}
            	if ((ITST[5] < 300) && (IROM[IR] > 10000)) {
            		IPR[2]=1150;
            		IPR[3]=2;
            		return "MMSD"
            	}
            	IPR[2]=1150;
            	IPR[3]=0;
            	return "MMSD"
                return "MMRL_return"
            }
            "MMRL_return" -> {
            if (MMRLret == 0) return "RLret0"
            if (MMRLret == 1) return "RLret1"
            if (MMRLret == 2) return "RLret2"
            if (MMRLret == 3) return "RLret3"
            if (MMRLret == 4) return "RLret4"
            if (MMRLret == 5) return "RLret5"
            if (MMRLret == 6) return "RLret6"
            if (MMRLret == 7) return "RLret7"
            if (MMRLret == 8) return "RLret8"
            if (MMRLret == 9) return "RLret9"
            	myPrintf("ack! unknown rl-ret: %d!!\n  This is a bug, send it to garnett@catbelly.com!\n", MMRLret);
            	myPrintf("Sorry, gotta die now, I don't know what to do");
            	pak();
            	return "EXIT"
            	return "EXIT"
                return "EXIT"
            }
        }
        return "EXIT"
    }

    companion object {
        val IRNM = arrayOf(
            "",
            "BUTLER'S ROOM    ", "FURNACE ROOM    ",
            "DARK PIT         ", "SECRET PASSAGE  ", "CREEPY CRYPT    ",
            "WINE CELLAR      ", "STORAGE ROOM    ", "DAMP DUNGEON    ",
            "FOOD CELLAR      ", "LIVING ROOM     ", "ENTRANCE HALL   ",
            "GAME ROOM        ", "BALL ROOM       ", "DINING HALL     ",
            "CHARMING CHAPEL  ", "LIBRARY         ", "KITCHEN         ",
            "CLOSET           ", "MASTER BEDROOM  ", "BELL TOWER      ",
            "HAUNTED HALLWAY  ", "LARGE BEDROOM   ", "MAID'S ROOM     ",
            "TWISTY MAZE      ", "COLD CORRIDOR   ", "SHORT CUT       ",
            "TRICKY TREASURY  ", "MOLE MAZE       ", "MOLE'S VAULT    ",
            "MOLE CAVE        ", "VEXING VERANDA  ", "GARDEN GATE     ",
            "GARDEN EXIT      ", "GARGOYLE GARDEN ", "GRASSY MEADOW   ",
            "FOUL FOUNTAIN    ", "WITCHING WELL   ", "BROKEN BRIDGE   ",
            "POLLUTED POND    ", "COZY COTTAGE    ", "FIRE ESCAPE     ",
            "STRANGE STREAM   ", "AWKWARD ATTIC   ", "LABORATORY      ",
            "HIDEOUS HIGHWAY  ", "FRONT PORCH     ", "DREARY DRIVEWAY ",
            "MAIN GATE        ", "ROADWAY         ", "BACK GATE       ",
            "DENSE WOODS      ", "WOODS           ", "TREASURE TREK   ",
            "DEN OF DEATH     ", "TROLL TRAP      ", "BATTY BATHROOM  "
        )

        val IROM_STATIC = intArrayOf(
            0,
            5201, 5202, 5203, 5804, 5205, 5206, 5207, 5208, 5209, 15210,
            25211, 15212, 5804, 5213, 15214, 5215, 15816, 15217, 5218, 15219, 25220,
            5804, 5221, 15222, 15223, 15221, 5218, 5824, 5024, 5024, 5124, 5024, 5124,
            5024, 25245, 5225, 5125, 3025, 5225, 5826, 8027, 5828, 5028, 5028, 5028,
            5128, 5028, 5028, 5028, 5028, 5028, 8029, 5830, 5231, 5232, 5233, 5233,
            5034, 5034, 5034, 5034, 5034, 5034, 5234, 5234, 5234, 5234, 5234, 5234,
            5234, 6535, 6535, 5236, 5236, 5237, 5237, 5237, 37, 8037, 37, 5238, 5238,
            39, 6540, 5241, 8041, 6543, 6518, 8044, 5156, 5246, 6547, 5248, 5249,
            5250, 5249, 5152, 5151, 5842
        )

        val IRSN = arrayOf(
            "        ", "CORPSE  ", "LADY    ", "BUTLER  ", "MOLE    ", "VAMPIRE ",
            "WOODSMAN", "GARDENER", "MAID    ", "COOK    ", "WEREWOLF", "ELF     ",
            "HUNTER  ", "DWARF   ", "MASTER  ", "WOLF    ", "WARRIOR ", "        "
        )

        val IRES_STATIC = intArrayOf(
            0,
            19, 424, 911, 43, 300, 894, 763, 525, 718, 0,
            1870, 996, 330, 920, 71, 0, 0
        )

        val IPRP = arrayOf(
            "        ", "IN      ", "ON      ", "BY      ", "AT      ", "TO      ",
            "OVER    ", "FROM    ", "OUT     ", "THE     ", "OFF     ", "BOOTY   ",
            "A       ", "WITH    ", "        ", "        ", "        ", "        "
        )

        val IDTN = arrayOf(
            "        ", "NORTH   ", "EAST    ", "SOUTH   ", "WEST    ", "UP      ",
            "DOWN    ", "BACKWARD", "LEFT    ", "FORWARD ", "RIGHT   ", "BACK    "
        )

        val IVEN_STATIC = intArrayOf(
            0,
            0, 121, 220, 493, 1316, 126, 441, 233, 217, 5218, 9396, 7312,
            125, 5308, 295, 520, 152, 105, 1216, 114, 217, 193, 789, 309, 319, 789, 0,
            1184, 1214, 312, 118, 394, 202, 17213, 625, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 14416, 1101, 0, 0, 10216, 14316, 0, 0, 20100, 0, 0, 0, 0, 0, 0,
            16, 0, 0, 0, 24, 41, 41, 14, 27, 7, 5, 0,
            10, 11, 0, 12, 10, 0, 0, 0, 0, 0, 0, 52, 43, 93, 0, 0, 17, 14,
            91, 0, 0, 0, 15, 15, 15, 15, 15, 0, 51, 51, 51, 53, 0, 0, 0, 0
        )

        val IXT_STATIC = intArrayOf(
            0,
            401, 502, 704, 20906, 1310, 1411, 9562, 1613, 11714, 1815,
            2219, 22320, 2421, 2522, 2623, 22724, 328, 3429, 2085, 24036,
            2831, 4342, 4645, 4647, 4849, 15417, 5554, 5856, 5100, 4600,
            0, 0, 6157, 6458, 7359, 7460, 6561, 7164, 7265, 6866,
            6967, 6268, 6369, 6871, 6972, 95, 9335, 8176, 8277, 6281,
            6382, 1191, 9792, 29293, 9894, 9596, 0, 9899, 9897, 9798,
            20201, 302, 504, 20807, 1110, 1211, 1413, 1514, 22019, 1716,
            2322, 22423, 22625, 35, 2829, 2930, 2931, 436, 3637, 3738,
            3839, 5342, 4443, 4644, 4445, 4446, 4548, 4350, 4200, 4700,
            29026, 0, 8890, 0, 6055, 5456, 5958, 5559, 6160, 7062,
            6963, 7364, 6765, 7166, 8368, 6769, 6370, 7671, 6772, 7573,
            6574, 7475, 7277, 9192, 9693, 21, 9695, 96, 9497, 9898,
            104, 205, 407, 20609, 1013, 1114, 6295, 1316, 11417, 1518,
            1922, 22023, 2124, 2225, 2326, 22427, 8520, 3228, 3229, 3640,
            3033, 4344, 4847, 5150, 4251, 11754, 5455, 5658, 4300, 4800,
            0, 0, 5761, 8162, 8263, 5864, 6165, 6466, 6567, 7168,
            7269, 8370, 6471, 6572, 5973, 6074, 3593, 3535, 7681, 7782,
            2488, 8889, 9111, 0, 0, 9394, 9799, 9396, 9897, 9898,
            20102, 203, 405, 20708, 1011, 1112, 1314, 1415, 21920, 1617,
            2223, 22324, 22526, 2928, 3330, 2932, 9766, 3604, 3736, 3837,
            3938, 5043, 4845, 5247, 4948, 5149, 4950, 4951, 4400, 4900,
            22690, 9088, 0, 5955, 5457, 5859, 5560, 6061, 6862, 7063,
            6664, 7465, 7267, 6668, 8369, 6270, 6671, 7772, 6473, 7574,
            7375, 7176, 9291, 29392, 9493, 9794, 9895, 9835, 9997, 9898,
            1203, 1304, 1607, 1809, 2112, 2213, 2314, 3128, 3129, 2832,
            0, 4039, 3941, 4544, 4746, 4748, 5051, 4752, 4500, 5000,
            0, 0, 0, 753, 8687, 0, 8499, 9884, 2934, 0,
            0, 0, 9933, 3839, 3738, 3637, 3130, 0, 0, 0,
            8990, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            312, 413, 716, 918, 1221, 1322, 1423, 2803, 3028, 3029,
            2830, 3031, 4139, 3940, 5142, 4243, 4546, 5049, 4253, 5307,
            9984, 0, 0, 0, 0, 0, 0, 0, 5100, 5100,
            0, 1987, 3399, 3938, 3837, 3736, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            9089, 0, 0, 0, 0, 0, 0, 0, 0, 0
        )

        val IVRB_STATIC = arrayOf(
            "        ", "GO      ", "CLIMB   ", "ENTER   ", "XIMOW   ", "XIWOM   ",
            "MOWIX   ", "WOMIX   ", "WIMOX   ", "WOXIM   ", "XIMOW   ", "MOXIW   ",
            "DIG     ", "FOLLOW  ", "        ", "        ", "        ", "        ",
            "QUIT    ", "STOP    ", "SCORE   ", "GET     ", "TAKE    ", "DROP    ",
            "GIVE    ", "LOOK    ", "READ    ", "FIND    ", "LIST    ", "LIGHT   ",
            "TURN    ", "HELP    ", "OPEN    ", "CLOSE   ", "SNUFF   ", "BLOW    ",
            "EAT     ", "RUB     ", "BEAM    ", "SWEEP   ", "CALL    ", "MOVE    ",
            "ANSWER  ", "FEED    ", "DISPLAY ", "ATTACK  ", "KILL    ", "SUSPEND ",
            "RESTORE ", "DRINK   ", "MYSTERY ", "BRIBE   ", "QUESTION", "LISTEN  ",
            "UNLOCK  ", "LOCK    ", "SHOOT   ", "OIL     ", "RECORD  ", "BREAK   ",
            "PLACE   ", "SLEEP   ", "        ", "        ", "        ", "        ",
            "        ", "        ", "        ", "        ", "        ", "        ",
            "WALK    ", "PUT     ", "PICK    ", "        ", "        ", "        ",
            "        ", "        ", "        ", "        ", "        ", "        ",
            "        ", "        ", "        ", "        ", "        ", "        "
        )

        val ICLS = arrayOf(
            "        ", "PITIFUL ", "POOR    ", "UNLUCKY ", "NOVICE  ",
            "FAIR    ", "AMATEUR ", "GOOD    ", "LUCKY   ",
            "SKILLFUL", "SUPERB  ", "MASTER  ", "SUPER   "
        )

        val ITST_INIT = intArrayOf(
            0,
            93, 45, 999, 1000, 0, 0, 0, 0, 3, 0,
            6, 0, 0, 93, 17, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0, 29, 12,
            0, 0, 0, 0, 0, 0, 0, 1, 0, 0
        )

        val ITEM = arrayOf(
            "        ",
            "RING    ", "KEYS    ", "CLOCK   ", "LANTERN ", "CROSS   ",
            "COMPASS ", "TREASURE", "BATTERY ", "GUN     ", "KNIFE   ",
            "AXE     ", "SWORD   ", "VIAL    ", "CLUB    ", "ROPE    ",
            "CHAIR   ", "AMULET  ", "TALISMAN", "CANDLE  ", "BULLET  ",
            "BOOK    ", "NOTE    ", "XMITTER ", "FOOD    ", "BROOM   ",
            "RECEIVER", "TORCH   ", "MAP     ", "WEDGE   ", "GLOBE   ",
            "MATCH   ", "SHOVEL  ", "OILCAN  ", "HATCHET ", "CHEST   ",
            "PEARLS  ", "EMERALD ", "COINS   ", "GOBLET  ", "DIAMONDS",
            "JEWELRY ", "NECKLACE", "CROWN   ", "DARTS   ", "SHIELD  ",
            "KEY     ", "BATTERY ", "GAUDY GAUNTLET", "PARROT  ", "MACE    ",
            "        ", "        ",
            "CLOTHES ", "PADLOCK ", "POLICE  ", "OPERATOR", "FIREMEN ",
            "TAXI    ", "        ", "WINE    ", "POISON  ", "WATER   ",
            "MONKEY  ", "MIRROR  ", "DRAWING ", "CAVITY  ", "WOOD    ",
            "RAGS    ", "CRATE   ", "COFFIN  ", "POINTS  ", "PICTURE ",
            "PHONE   ", "HINGE   ", "RADIO   ", "LAMP    ", "WALL    ",
            "TREE    ", "TROLL   ", "STATUE  ", "FIGURINE", "IDOL    ",
            "BOULDERS", "ROCKS   ", "HOOK    ", "NEW     ", "OLD     ",
            "SCROLL  ", "FIRE    ", "PLAQUE  ", "CURTAIN ", "DOOR    ",
            "GATE    ", "QUEEN   ", "KING    ", "INDIANS ", "TABLE   ",
            "TABLES  ", "SHOWER  ", "FIGURINE", "BULL    ", "BARS    ",
            "DAGGER  ", "PRINTER ", "CASSETTE", "SILVER  ", "MANSION ",
            "AND     ", "CURTAINS", "CRAZY   ", "BOULDER ", "        ",
            "        ", "        ", "        ", "        ", "        ",
            "        ", "        ", "        ", "        ", "        ",
            "        ", "        ", "        ", "        ", "        ",
            "        ", "        ", "        ", "        ", "        ",
            "        ", "        ", "        ", "        ", "        ",
            "WHERE   ", "WHAT    ", "WHO     ", "HOW     ", "WHY     ",
            "        ", "        ", "        ", "        ", "        ",
            "        ", "        "
        )

        val IVAL_STATIC = intArrayOf(
            0,
            3, 2, 1, 2, 5, 2, 10, 2, 2, 1, 1, 1, 1, 1, 1, 1, 4, 4, 1, 2, 1, 1, 10, 1, 1,
            10, 1, 1, 1, 4, 1, 1, 1, 1, 10, 10, 10, 10, 10, 10, 10, 10, 10, 2, 5, 1, 1, 10, 5, 1, 0, 0
        )
    }
}

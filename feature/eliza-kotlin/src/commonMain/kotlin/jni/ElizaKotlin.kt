package jni.utils
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import club.gepetto.GcLog
import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.models.currentSettings


import elizakotlin.utils.elizaGame

class ElizaKotlin : BaseKotlinGame() {
    override fun start() {
        GcLog.d("Eliza.kt start called")
        greetings()
        myPrintf("\n\n")
        myPrintf(getString(R.string.eliza_title))
        myPrintf(getString(R.string.eliza_stop_instructions))
        myPrintf("\n\n")
        myPrintf(getString(R.string.eliza_hello_problem))

    }

    override fun start(gameNickName: String) {
        start()
    }

    companion object {
        private const val NUMKEYWORDS = 37
        private const val NUMSWAPS = 14

        private val keywords = arrayOf(
            "CAN YOU","CAN I","YOU ARE","YOURE","I DONT","I FEEL",
            "WHY DONT YOU","WHY CANT I","ARE YOU","I CANT","I AM","IM ",
            "YOU ","I WANT","WHAT","HOW","WHO","WHERE",
            "WHEN","WHY",
            "NAME","CAUSE","SORRY","DREAM","HELLO","HI ","MAYBE",
            " NO","YOUR","ALWAYS","THINK","ALIKE","YES","FRIEND",
            "COMPUTER","CAR","NOKEYFOUND"
        )

        private val swaps = arrayOf(
            arrayOf("ARE", "AM"),
            arrayOf("WERE", "WAS"),
            arrayOf("YOU", "I"),
            arrayOf("YOUR", "MY"),
            arrayOf("IVE", "YOU'VE"),
            arrayOf("IM", "YOU'RE"),
            arrayOf("YOU", "ME"),
            arrayOf("ME", "YOU"),
            arrayOf("AM", "ARE"),
            arrayOf("WAS", "WERE"),
            arrayOf("I", "YOU"),
            arrayOf("MY", "YOUR"),
            arrayOf("YOUVE", "I'VE"),
            arrayOf("YOURE", "I'M")
        )

        private val responsesPerKeyword = intArrayOf(
            3,2,4,4,4,3,
            3,2,3,3,4,4,
            3,5,9,9,9,9,
            9,9,
            2,4,4,4,1,1,5,
            5,2,4,3,7,3,6,
            7,5,6
        )

        private val responses = arrayOf(
            arrayOf("DON'T YOU BELIEVE THAT I CAN*", "PERHAPS YOU WOULD LIKE TO BE ABLE TO*", "YOU WANT ME TO BE ABLE TO*"),
            arrayOf("PERHAPS YOU DON'T WANT TO*", "DO YOU WANT TO BE ABLE TO*"),
            arrayOf("WHAT MAKES YOU THINK I AM*", "DOES IT PLEASE YOU TO BELIEVE I AM*", "PERHAPS YOU WOULD LIKE TO BE*", "DO YOU SOMETIMES WISH YOU WERE*"),
            arrayOf("WHAT MAKES YOU THINK I AM*", "DOES IT PLEASE YOU TO BELIEVE I AM*", "PERHAPS YOU WOULD LIKE TO BE*", "DO YOU SOMETIMES WISH YOU WERE*"),
            arrayOf("DON'T YOU REALLY*", "WHY DON'T YOU*", "DO YOU WISH TO BE ABLE TO*", "DOES THAT TROUBLE YOU?"),
            arrayOf("TELL ME MORE ABOUT SUCH FEELINGS.", "DO YOU OFTEN FEEL*", "DO YOU ENJOY FEELING*"),
            arrayOf("DO YOU REALLY BELIEVE I DON'T*", "PERHAPS IN GOOD TIME I WILL*", "DO YOU WANT ME TO*"),
            arrayOf("DO YOU THINK YOU SHOULD BE ABLE TO*", "WHY CAN'T YOU*"),
            arrayOf("WHY ARE YOU INTERESTED IN WHETHER OR NOT I AM*", "WOULD YOU PREFER IF I WERE NOT*", "PERHAPS IN YOUR FANTASIES I AM*"),
            arrayOf("HOW DO YOU KNOW YOU CAN'T*", "HAVE YOU TRIED?", "PERHAPS YOU CAN NOW*"),
            arrayOf("DID YOU COME TO ME BECAUSE YOU ARE*", "HOW LONG HAVE YOU BEEN*", "DO YOU BELIEVE IT IS NORMAL TO BE*", "DO YOU ENJOY BEING*"),
            arrayOf("DID YOU COME TO ME BECAUSE YOU ARE*", "HOW LONG HAVE YOU BEEN*", "DO YOU BELIEVE IT IS NORMAL TO BE*", "DO YOU ENJOY BEING*"),
            arrayOf("WE WERE DISCUSSING YOU-- NOT ME.", "OH, I*", "YOU'RE NOT REALLY TALKING ABOUT ME, ARE YOU?"),
            arrayOf("WHAT WOULD IT MEAN TO YOU IF YOU GOT*", "WHY DO YOU WANT*", "SUPPOSE YOU SOON GOT*", "WHAT IF YOU NEVER GOT*", "I SOMETIMES ALSO WANT*"),
            arrayOf("WHY DO YOU ASK?", "DOES THAT QUESTION INTEREST YOU?", "WHAT ANSWER WOULD PLEASE YOU THE MOST?", "WHAT DO YOU THINK?", "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?", "WHAT IS IT THAT YOU REALLY WANT TO KNOW?", "HAVE YOU ASKED ANYONE ELSE?", "HAVE YOU ASKED SUCH QUESTIONS BEFORE?", "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"),
            arrayOf("WHY DO YOU ASK?", "DOES THAT QUESTION INTEREST YOU?", "WHAT ANSWER WOULD PLEASE YOU THE MOST?", "WHAT DO YOU THINK?", "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?", "WHAT IS IT THAT YOU REALLY WANT TO KNOW?", "HAVE YOU ASKED ANYONE ELSE?", "HAVE YOU ASKED SUCH QUESTIONS BEFORE?", "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"),
            arrayOf("WHY DO YOU ASK?", "DOES THAT QUESTION INTEREST YOU?", "WHAT ANSWER WOULD PLEASE YOU THE MOST?", "WHAT DO YOU THINK?", "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?", "WHAT IS IT THAT YOU REALLY WANT TO KNOW?", "HAVE YOU ASKED ANYONE ELSE?", "HAVE YOU ASKED SUCH QUESTIONS BEFORE?", "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"),
            arrayOf("WHY DO YOU ASK?", "DOES THAT QUESTION INTEREST YOU?", "WHAT ANSWER WOULD PLEASE YOU THE MOST?", "WHAT DO YOU THINK?", "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?", "WHAT IS IT THAT YOU REALLY WANT TO KNOW?", "HAVE YOU ASKED ANYONE ELSE?", "HAVE YOU ASKED SUCH QUESTIONS BEFORE?", "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"),
            arrayOf("WHY DO YOU ASK?", "DOES THAT QUESTION INTEREST YOU?", "WHAT ANSWER WOULD PLEASE YOU THE MOST?", "WHAT DO YOU THINK?", "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?", "WHAT IS IT THAT YOU REALLY WANT TO KNOW?", "HAVE YOU ASKED ANYONE ELSE?", "HAVE YOU ASKED SUCH QUESTIONS BEFORE?", "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"),
            arrayOf("WHY DO YOU ASK?", "DOES THAT QUESTION INTEREST YOU?", "WHAT ANSWER WOULD PLEASE YOU THE MOST?", "WHAT DO YOU THINK?", "ARE SUCH QUESTIONS ON YOUR MIND OFTEN?", "WHAT IS IT THAT YOU REALLY WANT TO KNOW?", "HAVE YOU ASKED ANYONE ELSE?", "HAVE YOU ASKED SUCH QUESTIONS BEFORE?", "WHAT ELSE COMES TO MIND WHEN YOU ASK THAT?"),
            arrayOf("NAMES DON'T INTEREST ME.", "I DON'T CARE ABOUT NAMES-- PLEASE GO ON."),
            arrayOf("IS THAT THE REAL REASON?", "DON'T ANY OTHER REASONS COME TO MIND?", "DOES THAT REASON EXPLAIN ANY THING ELSE?", "WHAT OTHER REASONS MIGHT THERE BE?"),
            arrayOf("PLEASE DON'T APOLOGIZE.", "APOLOGIES ARE NOT NECESSARY.", "WHAT FEELINGS DO YOU HAVE WHEN YOU APOLOGIZE?", "DON'T BE SO DEFENSIVE!"),
            arrayOf("WHAT DOES THAT DREAM SUGGEST TO YOU?", "DO YOU DREAM OFTEN?", "WHAT PERSONS APPEAR IN YOUR DREAMS?", "ARE YOU DISTURBED BY YOUR DREAMS?"),
            arrayOf("HOW DO YOU DO--PLEASE STATE YOUR PROBLEM."),
            arrayOf("HOW DO YOU DO--PLEASE STATE YOUR PROBLEM."),
            arrayOf("YOU DON'T SEEM QUITE CERTAIN.", "WHY THE UNCERTAIN TONE?", "CAN'T YOU BE MORE POSITIVE?", "YOU AREN'T SURE?", "DON'T YOU KNOW?"),
            arrayOf("ARE YOU SAYING NO JUST TO BE NEGATIVE?", "YOU ARE BEING A BIT NEGATIVE.", "WHY NOT?", "ARE YOU SURE?", "WHY NO?"),
            arrayOf("WHY ARE YOU CONCERNED ABOUT MY*", "WHAT ABOUT YOUR OWN*"),
            arrayOf("CAN YOU THINK OF A SPECIFIC EXAMPLE?", "WHEN?", "WHAT ARE YOU THINKING OF?", "REALLY, ALWAYS?"),
            arrayOf("DO YOU REALLY THINK SO?", "BUT YOU ARE NOT SURE YOU*", "DO YOU DOUBT YOU*"),
            arrayOf("IN WHAT WAY?", "WHAT RESEMBLANCE DO YOU SEE?", "WHAT DOES THE SIMILARITY SUGGEST TO YOU?", "WHAT OTHER CONNECTIONS DO YOU SEE?", "COULD THERE REALLY BE SOME CONNECTION?", "HOW?"),
            arrayOf("YOU SEEM QUITE POSITIVE.", "ARE YOU SURE?", "I SEE.", "I UNDERSTAND."),
            arrayOf("WHY DO YOU BRING UP THE TOPIC OF FRIENDS?", "DO YOUR FRIENDS WORRY YOU?", "DO YOUR FRIENDS PICK ON YOU?", "ARE YOU SURE YOU HAVE ANY FRIENDS?", "DO YOU IMPOSE ON YOUR FRIENDS?", "PERHAPS YOUR LOVE FOR FRIENDS WORRIES YOU?"),
            arrayOf("DO COMPUTERS WORRY YOU?", "ARE YOU TALKING ABOUT ME IN PARTICULAR?", "ARE YOU FRIGHTENED BY MACHINES?", "WHY DO YOU MENTION COMPUTERS?", "WHAT DO YOU THINK MACHINES HAVE TO DO WITH YOUR PROBLEM?", "DON'T YOU THINK COMPUTERS CAN HELP PEOPLE?", "WHAT IS IT ABOUT MACHINES THAT WORRIES YOU?"),
            arrayOf("OH, DO YOU LIKE CARS?", "MY FAVORITE CAR IS A LAMBORGHINI COUNTACH. WHAT IS YOUR FAVORITE CAR?", "MY FAVORITE CAR COMPANY IS FERRARI.  WHAT IS YOURS?", "DO YOU LIKE PORSCHES?", "DO YOU LIKE PORSCHE TURBO CARRERAS?"),
            arrayOf("SAY, DO YOU HAVE ANY PSYCHOLOGICAL PROBLEMS?", "WHAT DOES THAT SUGGEST TO YOU?", "I SEE.", "I'M NOT SURE I UNDERSTAND YOU FULLY.", "COME, COME ELUCIDATE YOUR THOUGHTS.", "CAN YOU ELABORATE ON THAT?", "THAT IS QUITE INTERESTING.")
        )
    }

    private val whichReply = IntArray(NUMKEYWORDS) { 0 }
    private var lastinput = ""

    override fun sendCommand(command: String): Int {
        if (command.equals("about", ignoreCase = true)) {
            myPrintf("${elizaGame.about}\n")
            return 0
        }

        if (command.startsWith("unlock secrets", ignoreCase = true)) {
            val words = command.split(" ")
            var enabled = false
            if (words.size >= 3) {
                try { val value = words[2].toInt(); enabled = value == 1263 } catch(e: Exception) { }
            }

            if (enabled)
                myPrintf(getString(R.string.eliza_secrets_unlocked))
            else
                myPrintf(getString(R.string.eliza_secrets_safe))

            currentSettings.secretGames = enabled
            currentSettings.save()
        }

        myPrintf(getString(R.string.eliza_you_prefix, command))

        val inputUpper = command.uppercase().trim()
        if (inputUpper == "BYE") {
            myPrintf(getString(R.string.eliza_goodbye))
            return 0
        }

        if (inputUpper == lastinput) {
            myPrintf(getString(R.string.eliza_dont_repeat))
            return 0
        }
        lastinput = inputUpper

        var k = 0
        var keywordIdx = -1
        while (k < NUMKEYWORDS - 1) {
            keywordIdx = inputUpper.indexOf(keywords[k])
            if (keywordIdx != -1) break
            k++
        }

        val baseResponse = responses[k][whichReply[k]]
        var reply = ""

        if (!baseResponse.endsWith("*")) {
            reply = baseResponse
        } else {
            reply = baseResponse.substring(0, baseResponse.length - 1)
            var rest = ""
            if (keywordIdx != -1) {
                rest = inputUpper.substring(keywordIdx + keywords[k].length).trim()
            }

            val words = rest.split(" ").filter { it.isNotEmpty() }.toMutableList()
            for (w in words.indices) {
                for (s in 0 until NUMSWAPS) {
                    if (words[w] == swaps[s][0]) {
                        words[w] = swaps[s][1]
                        break
                    }
                }
            }
            if (words.isNotEmpty()) {
                reply += " " + words.joinToString(" ")
            }
            reply += "?"
        }

        myPrintf(getString(R.string.eliza_reply_prefix, reply))

        whichReply[k]++
        if (whichReply[k] >= responsesPerKeyword[k]) {
            whichReply[k] = 0
        }

        return 0
    }

}

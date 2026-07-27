package jni
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString


import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.models.currentSettings
import com.funhouse.shared.common.utils.lettersOnly
import club.gepetto.GcLog
import kotlinx.coroutines.*
import java.io.IOException
import java.util.Random
import com.funhouse.shared.common.utils.GcInputQueue
import hangmankotlin.utils.defaultAbout

class HangmanKotlin : BaseKotlinGame() {

    private var gameJob: Job? = null
    private val commandQueue = GcInputQueue<String>()

    private var totalWins = 0
    private var totalLosses = 0
    private var totalDesists = 0
    private var totalErrors = 0
    private var totalLiras = 0.0f
    private var playerName = ""

    private var randomizedWord = ""
    private var randomizedWordDefinition = ""
    private var dictionary: List<String> = listOf("randomized")
    private val minSize = 4
    private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "hangman"

    companion object {
        private const val LIRAS_TO_BEGIN_WORD = 10.00f
        private const val LIRAS_TO_WIN = 3.00f
        private const val LIRAS_TO_LOSE = 10.00f
        private const val LIRAS_TO_GIVEUP = 10.00f
        private const val LIRAS_FOR_CLUE = 7.00f
        private const val LIRAS_PER_ERROR = 1.00f
    }

    override fun start() {
        GcLog.d("HangmanKotlin.start() called")
        gameJob?.cancel()
        commandQueue.clear()
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            if (dictionary.size == 1) {
                loadDictionary()
            }
            restoreStats()
            showGreetings()
            runGameLoop()
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
        if (command.trim().equals("about", ignoreCase = true)) {
            myPrintf("$defaultAbout\n")
            return 0
        }

        if (!command.startsWith("word ")) {
            myPrintf("${command}\n")
        }

        commandQueue.put(command)
        return 0
    }

    private fun showGreetings() {
        greetings()
        myPrintf(AppData.applicationContext?.getString(com.funhouse.shared.common.R.string.game_only_in_english) ?: "")
        myPrintf("\n\n")
    }

    private suspend fun getLine(): String {
        return commandQueue.take()
    }

    private suspend fun readCleanLine(): String {
        val raw = getLine()
        return raw.filter { it.isLetter() || it.isWhitespace() }.uppercase()
    }

    private fun isNewScoreCard(): Boolean {
        return totalWins == 0 && totalLosses == 0 && totalDesists == 0
    }

    private fun restoreStats() {
        val stringMap = getStringMap(key)
        if (stringMap.isNotEmpty()) {
            val parts = stringMap.split(" ")
            if (parts.size == 5) {
                totalWins = parts[0].toIntOrNull() ?: 0
                totalLosses = parts[1].toIntOrNull() ?: 0
                totalDesists = parts[2].toIntOrNull() ?: 0
                totalErrors = parts[3].toIntOrNull() ?: 0
                totalLiras = parts[4].toFloatOrNull() ?: 0.0f
            }
        }
        playerName = currentSettings.playerNickName
    }

    private fun saveStats() {
        val string = "$totalWins $totalLosses $totalDesists $totalErrors $totalLiras"
        updateStringMap(key, string)
        updateWallet(key, totalLiras.toString())
    }

    private fun printScore() {
        if (isNewScoreCard()) {
            myPrintf("%s, you are starting a new scorecard and will begin to earn Liras...\n", playerName)
        } else {
            myPrintf("%s, ", playerName)
            if (totalLiras > 0.0f) {
                myPrintf("you have earned %02.02f Liras.\n", totalLiras)
            } else if (totalLiras == 0.0f) {
                myPrintf("you have earned no Liras.\n")
            } else {
                myPrintf("you have lost %02.02f Liras.\n", totalLiras)
            }

            myPrintf(
                "You won %d times, lost %d times, gave up %d times and have %d total error attempts.\n",
                totalWins, totalLosses, totalDesists, totalErrors
            )
        }
    }

    private fun loadDictionary(): Boolean {
        try {
            val text = com.funhouse.shared.common.utils.readAssetFile("dictionary.txt")
            if (text != null) {
                dictionary = text.split("\n").map { it.trim() }.filter { line ->
                    line.isNotBlank() &&
                            !line.startsWith("#") &&
                            line.length >= minSize &&
                            line.lettersOnly()
                }
                GcLog.d("Dictionary loaded with ${dictionary.size} words.")
                return false
            } else {
                GcLog.e("pickRandomWord could not open stream")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    private fun pickRandomWord(): String {
        myPrintf(AppData.applicationContext?.getString(R.string.randomizing_word) ?: "")

        if (dictionary.size == 1) {
            if (loadDictionary()) {
                randomizedWord = "randomize"
                return randomizedWord
            }
        }

        val random = Random()
        var randomIndex = random.nextInt(dictionary.size)
        var count = 0
        randomizedWordDefinition = ""

        while (++count < 10) {
            val rWord = dictionary[randomIndex]
            val definition = getWordDefinition(rWord)
            if (definition == null) {
                GcLog.d("Randomized word $rWord does not have a dictionary description. Will try again. Count = $count")
                randomIndex = random.nextInt(dictionary.size)
                continue
            }
            GcLog.d("Word '$rWord' picked. Description = '$definition'")
            randomizedWordDefinition = definition
            break
        }
        randomizedWord = dictionary[randomIndex]
        GcLog.v("pickRandomWord picked word ('$randomizedWord', meaning = '$randomizedWordDefinition'")
        return randomizedWord
    }

    private fun printWordMeaning(meaning: String?) {
        val context = AppData.applicationContext
        if (meaning != null) {
            myPrintf(context?.getString(R.string.word_meaning, randomizedWord) ?: "")
            myPrintf("$meaning\n")
        } else {
            myPrintf(context?.getString(R.string.meaning_not_found) ?: "")
        }
    }

    private fun findWordMeaning() {
        if (randomizedWordDefinition.isNotEmpty()) {
            printWordMeaning(randomizedWordDefinition)
        } else {
            val meaning = getWordDefinition(randomizedWord)
            printWordMeaning(meaning)
        }
    }

    private suspend fun runGameLoop() {
        var maxAttempts = 12
        var result = -1
        var original = ""
        var modified = ""
        var attemptsLeft = 0
        var errors = 0
        var lirasUsed = 0.0f

        if (playerName.isEmpty() || playerName.equals("Unknown", ignoreCase = true)) {
            myPrintf("What is your name?\n")
            playerName = getLine().trim()
            if (playerName.isNotEmpty()) {
                updatePlayerName(playerName)
            }
        }

        if (!isNewScoreCard()) {
            printScore()
        }

        while (randomizedWord.isEmpty()) {
            pickRandomWord()
        }

        while (true) {
            if (result != 4) {
                totalLiras += LIRAS_TO_BEGIN_WORD
                original = randomizedWord.uppercase()

                val l = original.length
                val firstChar = original.firstOrNull()
                val lastChar = original.lastOrNull()
                val sb = StringBuilder()
                for (i in 0 until l) {
                    val c = original[i]
                    if (i > 0 && i < l - 1 && c.isLetter() && c != firstChar && c != lastChar) {
                        sb.append('_')
                    } else {
                        sb.append(c)
                    }
                }
                modified = sb.toString()
                attemptsLeft = maxAttempts
                errors = 0
                lirasUsed = 0.0f

                myPrintf("Hangman now has a new word that you have to guess...\n")
            }

            result = 0
            while (true) {
                if (attemptsLeft == 0) {
                    myPrintf("You lost!\n")
                    myPrintf("The word was : %s\n", original)
                    result = 3
                    break
                }
                if (original == modified) {
                    myPrintf("you won! |%s| !\n", original)
                    result = 2
                    break
                }

                if (queryVoice() == 0) {
                    printHangman(attemptsLeft)
                }

                myPrintf("|%s| - Give me a letter:\n", modified)

                val buffer = readCleanLine()

                if (buffer.startsWith("SCORE", ignoreCase = true)) {
                    printScore()
                    continue
                }

                if (buffer.startsWith("WORD ", ignoreCase = true)) {
                    val parts = buffer.split(Regex("\\s+"))
                    if (parts.size >= 2) {
                        val word = parts[1]
                        val na = if (parts.size >= 3) parts[2].toIntOrNull() ?: 12 else 12
                        randomizedWord = word
                        randomizedWordDefinition = ""
                        maxAttempts = na
                        result = 4
                        break
                    } else {
                        myPrintf("Invalid command ('%s')\n", buffer)
                    }
                } else if (buffer.startsWith("QUIT", ignoreCase = true) || buffer.startsWith("NEW", ignoreCase = true)) {
                    result = 0
                    break
                } else if (buffer.startsWith("EXIT", ignoreCase = true)) {
                    result = 1
                    break
                } else if (buffer.startsWith("MEANING", ignoreCase = true)) {
                    lirasUsed = LIRAS_FOR_CLUE
                    if (randomizedWordDefinition.isNotEmpty()) {
                        myPrintf("Word meaning: '%s'\n", randomizedWordDefinition)
                    } else {
                        findWordMeaning()
                    }
                } else {
                    if (buffer.isNotEmpty()) {
                        val c = buffer[0]
                        if (modified.contains(c)) {
                            myPrintf("Letter already exists in the word\n")
                        } else if (original.contains(c)) {
                            val sb = StringBuilder()
                            for (i in 0 until original.length) {
                                if (original[i] == c) {
                                    sb.append(c)
                                } else {
                                    sb.append(modified[i])
                                }
                            }
                            modified = sb.toString()
                            myPrintf("Good one!\n")
                        } else {
                            errors++
                            attemptsLeft--
                            myPrintf("Wrong answer. Number of wrong attempts left : %d\n", attemptsLeft)
                        }
                    }
                }
            }

            when (result) {
                0 -> {
                    totalDesists++
                    totalErrors += errors
                    totalLiras = totalLiras - (LIRAS_PER_ERROR * errors.toFloat()) - lirasUsed - LIRAS_TO_GIVEUP
                }
                1 -> {
                    break
                }
                2 -> {
                    totalWins++
                    totalErrors += errors
                    totalLiras = totalLiras - (LIRAS_PER_ERROR * errors.toFloat()) - lirasUsed + LIRAS_TO_WIN
                    findWordMeaning()
                }
                3 -> {
                    totalLosses++
                    totalErrors += attemptsLeft
                    totalLiras = totalLiras - (LIRAS_PER_ERROR * errors.toFloat()) - lirasUsed - LIRAS_TO_LOSE
                    findWordMeaning()
                }
                4 -> {
                    // Start game with custom word
                }
            }

            if (result == 1) {
                break
            }

            myPrintf("Game ended with %d error attempts.\n", errors)
            printScore()
            saveStats()

            if (result != 0 && result != 4) {
                delay(2000)
                myPrintf("Hit enter to start a new game\n")
                getLine()
            }

            if (result != 4) {
                pickRandomWord()
            }
        }
    }

    private fun printZero() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (_)\n")
        myPrintf("|   /|\\\n")
        myPrintf("|    |\n")
        myPrintf("|   | |\n")
        myPrintf("|\n")
    }

    private fun printOne() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (_)\n")
        myPrintf("|   /|\\\n")
        myPrintf("|    |\n")
        myPrintf("|   |\n")
        myPrintf("|\n")
    }

    private fun printTwo() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (_)\n")
        myPrintf("|   /|\\\n")
        myPrintf("|    |\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printThree() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (_)\n")
        myPrintf("|   /|\\\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printFour() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (_)\n")
        myPrintf("|   /|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printFive() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (_)\n")
        myPrintf("|    |\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printSix() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (_)\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printSeven() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   ( )\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printEight() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|   (\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printNine() {
        myPrintf("_____\n")
        myPrintf("|/   |\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printEmpty() {
        myPrintf("_____\n")
        myPrintf("|/\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
        myPrintf("|\n")
    }

    private fun printHangman(errorAttemptsLeft: Int) {
        when (errorAttemptsLeft) {
            10 -> printEmpty()
            9 -> printNine()
            8 -> printEight()
            7 -> printSeven()
            6 -> printSix()
            5 -> printFive()
            4 -> printFour()
            3 -> printThree()
            2 -> printTwo()
            1 -> printOne()
            0 -> printZero()
        }
    }
}

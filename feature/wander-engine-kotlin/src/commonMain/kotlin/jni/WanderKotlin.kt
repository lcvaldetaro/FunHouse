package jni

import club.gepetto.GcLog
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.GAMES_FOLDER
import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.jni.TerminalDataCallback
import com.funhouse.shared.common.models.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
data class WanderSaveState(
    val currentLoc: Int,
    val locStates: Map<String, Int>,
    val locSeen: List<Int>,
    val variables: List<Int>,
    val items: Map<String, Int>
)

class WanderKotlin(
    val library: String = "gepetto.wan",
    val settingsParam: Settings = Settings(),
    val gameFolderParam: String = GAMES_FOLDER,
    val aboutText: String = "",
    val gameNickName: String = "wandera3",
    callbackParam: TerminalDataCallback? = null
) : BaseKotlinGame() {

    init {
        registerTerminalCallback(callbackParam ?: object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {}
        })
    }

    data class Item(
        val fullName: String,
        val primaryName: String,
        var location: Int, // -1 = carried, 0 = nowhere, >0 = location ID
        val isSynonym: Boolean = false,
        val isPlural: Boolean = false
    )

    data class Condition(
        val type: String, // "t?", "t~", "o?", "o~", "s?", "s~", "v?", "v~", "v<", "v>"
        val target: String,
        val value: Int = 0
    )

    data class Effect(
        val type: String, // "m", "s=", "s+", "s-", "t+", "t-", "o+", "o-", "v=", "v+", "v-"
        val target: String = "",
        val val1: Int = 0,
        val val2: Int = 0,
        val strVal: String = ""
    )

    data class Action(
        val triggers: List<String>,
        val targetLoc: Int = 0,
        val conditions: List<Condition> = emptyList(),
        val effects: List<Effect> = emptyList(),
        val isNoContinue: Boolean = true
    )

    data class Location(
        val loc: Int,
        val state: Int,
        val title: String,
        val description: String,
        val actions: List<Action>
    )

    private val locations = mutableMapOf<Pair<Int, Int>, Location>()
    private val preActions = mutableListOf<Action>()
    private val postActions = mutableListOf<Action>()
    private val itemsList = mutableListOf<Item>()
    private val itemLookup = mutableMapOf<String, Item>()
    private val locStates = mutableMapOf<Int, Int>()
    private val locSeen = IntArray(300)
    private val variables = IntArray(130)

    private var currentLoc: Int
        get() = variables[100]
        set(value) {
            variables[100] = value
        }

    private var introMessage = ""
    private var gamePrefix = "a3"
    private var lastCommandWords = emptyList<String>()


    private fun cleanItemName(raw: String): String {
        return raw.replace("\\", "").trim().lowercase()
    }

    private fun cleanEscapes(str: String): String {
        val cleanedLines = str.lines().joinToString("\n") { line ->
            if (line.startsWith("\\")) line.substring(1) else line
        }
        return cleanedLines.replace("\\\"", "\"").replace("\\\t", "\t").replace("\\n", "\n")
    }

    private fun getFileContent(fileName: String): String? {
        val fileContent = com.funhouse.shared.common.utils.readTextFromFile(fileName)
        if (fileContent != null) {
            return fileContent
        }
        val fileInFolderContent = com.funhouse.shared.common.utils.readTextFromFile("$GAMES_FOLDER/$fileName")
        if (fileInFolderContent != null) {
            return fileInFolderContent
        }
        try {
            return com.funhouse.shared.common.utils.readAssetFile(fileName)
        } catch (e: Exception) {
            GcLog.e("Failed to load asset $fileName", e)
        }
        return null
    }

    private fun loadGameData(nick: String) {
        locations.clear()
        preActions.clear()
        postActions.clear()
        itemsList.clear()
        itemLookup.clear()
        locStates.clear()
        locSeen.fill(0)
        variables.fill(0)
        variables[100] = 1 // CUR_LOC
        variables[101] = 1 // PREV_LOC
        variables[109] = 8 // MAX_CARRY
        variables[118] = 5 // BREVITY
        currentLoc = 1
        lastCommandWords = emptyList()

        gamePrefix = when {
            nick.contains("castle", ignoreCase = true) -> "castle"
            nick.contains("library", ignoreCase = true) -> "library"
            nick.contains("tut", ignoreCase = true) || nick.contains("logic", ignoreCase = true) -> "tut"
            else -> "a3"
        }

        val miscContent = getFileContent("$gamePrefix.misc")
        val wrldContent = getFileContent("$gamePrefix.wrld")

        if (miscContent != null) parseMiscContent(miscContent)
        if (wrldContent != null) parseWrldContent(wrldContent)
    }


    private fun parseMiscContent(content: String) {
        val lines = splitLogicalLines(content)

        var inObjects = false
        var inPreActions = false
        var inPostActions = false
        var inVariables = false
        var readingIntro = true
        var currentPrimary = ""
        val introLines = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith(":")) continue

            if (trimmed.startsWith("words (verbs)") || (trimmed.startsWith("words") && !trimmed.startsWith("words (objects)"))) {
                readingIntro = false
                inObjects = false
                inPreActions = false
                inPostActions = false
                inVariables = false
                continue
            }
            if (trimmed.startsWith("words (objects)")) {
                readingIntro = false
                inObjects = true
                inPreActions = false
                inPostActions = false
                inVariables = false
                continue
            }
            if (trimmed.startsWith("pre actions")) {
                readingIntro = false
                inObjects = false
                inPreActions = true
                inPostActions = false
                inVariables = false
                continue
            }
            if (trimmed.startsWith("post actions")) {
                readingIntro = false
                inObjects = false
                inPreActions = false
                inPostActions = true
                inVariables = false
                continue
            }
            if (trimmed.startsWith("variables")) {
                readingIntro = false
                inObjects = false
                inPreActions = false
                inPostActions = false
                inVariables = true
                continue
            }

            if (readingIntro && trimmed.isNotEmpty()) {
                introLines.add(line)
            }

            if (inObjects && trimmed.isNotEmpty()) {
                val parts = splitTokens(trimmed)
                if (parts.size >= 2 && (parts[1] == "1" || parts[1] == "2" || parts[1] == "3") && currentPrimary.isNotEmpty()) {
                    // Synonym entry
                    val synName = cleanItemName(parts[0])
                    val isPlur = parts[1] == "3"
                    val primaryItem = itemLookup[currentPrimary]
                    if (primaryItem != null) {
                        val synItem = Item(synName, currentPrimary, primaryItem.location, isSynonym = true, isPlural = isPlur)
                        itemsList.add(synItem)
                        itemLookup[synName] = synItem
                    }
                } else {
                    // Primary object entry
                    val rawName = cleanItemName(parts[0])
                    var loc = 0
                    if (parts.size >= 3) {
                        loc = parts[2].toIntOrNull() ?: 0
                    } else if (parts.size == 2 && parts[1].matches(Regex("-?\\d+"))) {
                        loc = parts[1].toIntOrNull() ?: 0
                    }
                    currentPrimary = rawName
                    val isPlur = rawName.endsWith("s") || rawName.endsWith("papers") || rawName.endsWith("notes") || rawName.endsWith("tokens") || rawName.endsWith("strawberries") || rawName.endsWith("potatoes")
                    val item = Item(rawName, rawName, loc, isSynonym = false, isPlural = isPlur)
                    itemsList.add(item)
                    itemLookup[rawName] = item
                }
            }

            if (inPreActions && trimmed.isNotEmpty()) {
                val actDef = parseActionLine(line)
                if (actDef != null) preActions.add(actDef)
            }

            if (inPostActions && trimmed.isNotEmpty()) {
                val actDef = parseActionLine(line)
                if (actDef != null) postActions.add(actDef)
            }

            if (inVariables && trimmed.isNotEmpty()) {
                val parts = trimmed.split(Regex("\\s+"))
                if (parts.size >= 2) {
                    val idx = getVariableIndex(parts[0])
                    val value = parts[1].toIntOrNull() ?: 0
                    if (idx in variables.indices) {
                        variables[idx] = value
                    }
                }
            }
        }

        var intro = introLines.joinToString("\n").trim()
        if (intro.startsWith("\"") && intro.endsWith("\"") && intro.length >= 2) {
            intro = intro.substring(1, intro.length - 1).trim()
        }
        introMessage = cleanEscapes(intro)
    }


    private fun parseWrldContent(content: String) {
        val allLines = splitLogicalLines(content)
        var currentBlock = mutableListOf<String>()
        val blocks = mutableListOf<List<String>>()
        for (line in allLines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#") && trimmed.substring(1).trim().firstOrNull()?.isDigit() == true) {
                if (currentBlock.isNotEmpty()) {
                    blocks.add(currentBlock)
                }
                currentBlock = mutableListOf()
            }
            currentBlock.add(line)
        }
        if (currentBlock.isNotEmpty()) {
            blocks.add(currentBlock)
        }

        for (blockLines in blocks) {
            if (blockLines.isEmpty()) continue
            val header = blockLines[0].trim()
            if (!header.startsWith("#")) continue

            val headerParts = header.substring(1).trim().split(Regex("\\s+"), limit = 2)
            val locStateStr = headerParts[0]
            val title = if (headerParts.size > 1) headerParts[1] else ""

            val loc: Int
            val state: Int
            if (locStateStr.contains(".")) {
                val parts = locStateStr.split(".")
                loc = parts[0].toIntOrNull() ?: continue
                state = parts[1].toIntOrNull() ?: 0
            } else {
                loc = locStateStr.toIntOrNull() ?: continue
                state = 0
            }

            val descLines = mutableListOf<String>()
            val actionLines = mutableListOf<String>()

            for (i in 1 until blockLines.size) {
                val line = blockLines[i]
                if (line.startsWith("\t") || line.startsWith(" ")) {
                    actionLines.add(line)
                } else if (actionLines.isEmpty()) {
                    descLines.add(line)
                } else {
                    actionLines.add(line)
                }
            }

            var descText = descLines.joinToString("\n").trim()
            if (descText.startsWith("\"") && descText.endsWith("\"") && descText.length >= 2) {
                descText = descText.substring(1, descText.length - 1).trim()
            }

            val actionsList = mutableListOf<Action>()
            for (actLine in actionLines) {
                val actDef = parseActionLine(actLine)
                if (actDef != null) actionsList.add(actDef)
            }

            locations[Pair(loc, state)] = Location(loc, state, cleanEscapes(title), cleanEscapes(descText), actionsList)
        }
    }


    private fun parseActionLine(line: String): Action? {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return null

        val triggerEnd = getTriggerEndIndex(trimmed)
        val rawTrigger = trimmed.substring(0, triggerEnd)
        val rest = trimmed.substring(triggerEnd).trim()

        val rawTriggers = cleanItemName(rawTrigger)
        val rawList = rawTriggers.split("|")
        val triggers = mutableListOf<String>()
        for (t in rawList) {
            triggers.add(t)
            when (t) {
                "n" -> triggers.add("north")
                "s" -> triggers.add("south")
                "e" -> triggers.add("east")
                "w" -> triggers.add("west")
                "u" -> triggers.add("up")
                "d" -> triggers.add("down")
                "north" -> triggers.add("n")
                "south" -> triggers.add("s")
                "east" -> triggers.add("e")
                "west" -> triggers.add("w")
                "up" -> triggers.add("u")
                "down" -> triggers.add("d")
            }
        }

        var targetLoc = 0
        val conditions = mutableListOf<Condition>()
        val effects = mutableListOf<Effect>()
        var isNoContinue = true

        if (rest.contains("m=")) {
            val msgPart = rest.substringAfter("m=")
            effects.add(Effect("m", strVal = cleanMessageString(msgPart)))
        }

        if (rest.contains(",,,") || rest.contains("..")) {
            isNoContinue = false
        }

        val restBeforeMsg = if (rest.contains("m=")) rest.substringBefore("m=").trim() else rest
        val tokens = restBeforeMsg.split(Regex("\\s+")).filter { it.isNotEmpty() }
        for (token in tokens) {
            if (token.matches(Regex("\\d+"))) {
                targetLoc = token.toIntOrNull() ?: 0
            } else if (token.startsWith("t?")) {
                conditions.add(Condition("t?", cleanItemName(token.substring(2))))
            } else if (token.startsWith("t~")) {
                conditions.add(Condition("t~", cleanItemName(token.substring(2))))
            } else if (token.startsWith("o?")) {
                conditions.add(Condition("o?", cleanItemName(token.substring(2))))
            } else if (token.startsWith("o~")) {
                conditions.add(Condition("o~", cleanItemName(token.substring(2))))
            } else if (token.startsWith("s?")) {
                val sVal = token.substring(2)
                val partsS = sVal.split(".")
                val l = partsS[0].toIntOrNull() ?: 0
                val st = if (partsS.size > 1) partsS[1].toIntOrNull() ?: 0 else 0
                conditions.add(Condition("s?", l.toString(), st))
            } else if (token.startsWith("v?")) {
                val vVal = token.substring(2)
                val partsV = vVal.split(".")
                val vIdx = getVariableIndex(partsV[0])
                val vValInt = if (partsV.size > 1) {
                    val p = partsV[1]
                    if (p.startsWith("%") && p.endsWith("%")) {
                        getVariableIndex(p.substring(1, p.length - 1)) or 0x8000
                    } else {
                        p.toIntOrNull() ?: 0
                    }
                } else 0
                conditions.add(Condition("v?", vIdx.toString(), vValInt))
            } else if (token.startsWith("v<")) {
                val vVal = token.substring(2)
                val partsV = vVal.split(".")
                val vIdx = getVariableIndex(partsV[0])
                val vValInt = if (partsV.size > 1) partsV[1].toIntOrNull() ?: 0 else 0
                conditions.add(Condition("v<", vIdx.toString(), vValInt))
            } else if (token.startsWith("v>")) {
                val vVal = token.substring(2)
                val partsV = vVal.split(".")
                val vIdx = getVariableIndex(partsV[0])
                val vValInt = if (partsV.size > 1) partsV[1].toIntOrNull() ?: 0 else 0
                conditions.add(Condition("v>", vIdx.toString(), vValInt))
            } else if (token.startsWith("s=")) {
                val sVal = token.substring(2)
                val partsS = sVal.split(".")
                val l = partsS[0].toIntOrNull() ?: 0
                val st = if (partsS.size > 1) partsS[1].toIntOrNull() ?: 0 else 0
                effects.add(Effect("s=", target = l.toString(), val1 = st))
            } else if (token.startsWith("t+")) {
                effects.add(Effect("t+", target = cleanItemName(token.substring(2))))
            } else if (token.startsWith("t-")) {
                effects.add(Effect("t-", target = cleanItemName(token.substring(2))))
            } else if (token.startsWith("o+")) {
                val oVal = token.substring(2)
                if (oVal.contains("@")) {
                    val partsO = oVal.split("@")
                    val item = cleanItemName(partsO[0])
                    val specificLoc = partsO[1].toIntOrNull() ?: 0
                    effects.add(Effect("o+", target = item, val1 = specificLoc))
                } else {
                    effects.add(Effect("o+", target = cleanItemName(oVal)))
                }
            } else if (token.startsWith("o-")) {
                effects.add(Effect("o-", target = cleanItemName(token.substring(2))))
            } else if (token.startsWith("v=")) {
                val vVal = token.substring(2)
                val partsV = vVal.split(".")
                val vIdx = getVariableIndex(partsV[0])
                val vValInt = if (partsV.size > 1) partsV[1].toIntOrNull() ?: 0 else 0
                effects.add(Effect("v=", target = vIdx.toString(), val1 = vValInt))
            } else if (token.startsWith("v+")) {
                val vVal = token.substring(2)
                val partsV = vVal.split(".")
                val vIdx = getVariableIndex(partsV[0])
                val vValInt = if (partsV.size > 1) {
                    val p = partsV[1]
                    if (p.startsWith("%") && p.endsWith("%")) {
                        getVariableIndex(p.substring(1, p.length - 1)) or 0x8000
                    } else {
                        p.toIntOrNull() ?: 0
                    }
                } else 0
                effects.add(Effect("v+", target = vIdx.toString(), val1 = vValInt))
            } else if (token.startsWith("v-")) {
                val vVal = token.substring(2)
                val partsV = vVal.split(".")
                val vIdx = getVariableIndex(partsV[0])
                val vValInt = if (partsV.size > 1) partsV[1].toIntOrNull() ?: 0 else 0
                effects.add(Effect("v-", target = vIdx.toString(), val1 = vValInt))
            }
        }

        return Action(triggers, targetLoc, conditions, effects, isNoContinue)
    }

    private fun getArticle(name: String, isPlural: Boolean): String {
        if (isPlural) return "some"
        val lower = name.lowercase()
        val firstChar = lower.firstOrNull() ?: ' '
        return if (firstChar in listOf('a', 'e', 'i', 'o', 'u')) "an" else "a"
    }

    fun prloc(isLook: Boolean = false) {
        val seen = if (currentLoc in locSeen.indices) locSeen[currentLoc] else 0
        if (currentLoc in locSeen.indices) {
            locSeen[currentLoc]++
        }

        val currentState = locStates[currentLoc] ?: 0
        val key = Pair(currentLoc, currentState)
        val fallbackKey = Pair(currentLoc, 0)
        val locDef = locations[key] ?: locations[fallbackKey]

        if (locDef != null) {
            if (isLook || seen == 0 || locDef.title.isEmpty()) {
                if (locDef.description.isNotEmpty()) {
                    myPrintf(formatMsg(locDef.description) + "\n")
                } else if (locDef.title.isNotEmpty()) {
                    myPrintf(formatMsg(locDef.title) + "\n")
                }
            } else {
                myPrintf(formatMsg(locDef.title) + "\n")
            }
        } else {
            myPrintf("Location $currentLoc\n")
        }

        // Print primary items present at current location
        val primaryItemsAtLoc = itemsList.filter { !it.isSynonym && getItemLocation(it.fullName) == currentLoc }
        for (item in primaryItemsAtLoc) {
            val article = getArticle(item.fullName, item.isPlural)
            myPrintf("There is $article ${item.fullName} here.\n")
        }
    }

    private fun getItemLocation(name: String): Int {
        val clean = cleanItemName(name)
        val item = itemLookup[clean] ?: return 0
        val primary = itemLookup[item.primaryName] ?: return item.location
        return primary.location
    }

    private fun setItemLocation(name: String, loc: Int) {
        val clean = cleanItemName(name)
        val item = itemLookup[clean] ?: return
        item.location = loc
        val primary = itemLookup[item.primaryName]
        if (primary != null) primary.location = loc
    }

    override fun start() {
        start(gameNickName)
    }

    override fun start(gameNickName: String) {
        GcLog.d("WanderKotlin starting game: $gameNickName")
        myPrintf("\n\n\nWelcome to a Wander adventure game!\n")
        greetings()
        loadGameData(gameNickName)
        if (introMessage.isNotEmpty()) {
            myPrintf(formatMsg(introMessage) + "\n\n")
        }
        prloc(isLook = true)
    }

    override fun sendCommand(command: String): Int {
        val trimmed = command.trim()
        if (trimmed.isEmpty()) return 0

        if (trimmed.equals("about", ignoreCase = true)) {
            myPrintf(aboutText + "\n")
            return 0
        }

        myPrintf("> $trimmed\n")
        carryOut(trimmed)
        return 0
    }

    private fun carryOut(cmd: String) {
        val trimmed = cmd.trim()
        lastCommandWords = splitTokens(trimmed)
        val inputNorm = normalizeInput(trimmed)

        // 1. Pre actions
        for (action in preActions) {
            if (matchesTrigger(inputNorm, action.triggers) && checkConditions(action.conditions)) {
                executeEffects(action.effects)
                if (action.targetLoc > 0) {
                    currentLoc = action.targetLoc
                }
                if (action.isNoContinue) {
                    prloc(isLook = false)
                    return
                }
            }
        }

        // 2. Local actions
        val currentState = locStates[currentLoc] ?: 0
        val key = Pair(currentLoc, currentState)
        val fallbackKey = Pair(currentLoc, 0)
        val locDef = locations[key] ?: locations[fallbackKey]

        if (locDef != null) {
            for (action in locDef.actions) {
                if (matchesTrigger(inputNorm, action.triggers) && checkConditions(action.conditions)) {
                    executeEffects(action.effects)
                    if (action.targetLoc > 0) {
                        currentLoc = action.targetLoc
                    }
                    if (action.isNoContinue) {
                        prloc(isLook = false)
                        return
                    }
                }
            }
        }

        // 3. Post actions fallback
        for (action in postActions) {
            if (matchesTrigger(inputNorm, action.triggers) && checkConditions(action.conditions)) {
                executeEffects(action.effects)
                if (action.targetLoc > 0) {
                    currentLoc = action.targetLoc
                }
                if (action.isNoContinue) {
                    prloc(isLook = false)
                    return
                }
            }
        }

        // Built-in commands fallback
        when (inputNorm) {
            "look", "l" -> {
                prloc(isLook = true)
                return
            }
            "inventory", "inv", "i" -> {
                doInventory()
                return
            }
        }

        if (inputNorm.startsWith("save")) {
            val parts = inputNorm.split(Regex("\\s+"))
            val saveName = if (parts.size > 1 && parts[1].isNotEmpty()) parts[1] else "$gamePrefix.save"
            doSave(saveName)
            return
        }

        if (inputNorm.startsWith("restore") || inputNorm.startsWith("load")) {
            val parts = inputNorm.split(Regex("\\s+"))
            val saveName = if (parts.size > 1 && parts[1].isNotEmpty()) parts[1] else "$gamePrefix.save"
            doRestore(saveName)
            return
        }

        if (inputNorm.startsWith("take ") || inputNorm.startsWith("get ")) {
            val rawItemName = cmd.trim().substringAfter(" ").trim()
            takeItem(rawItemName)
            return
        }

        if (inputNorm.startsWith("drop ")) {
            val rawItemName = cmd.trim().substringAfter(" ").trim()
            dropItem(rawItemName)
            return
        }

        myPrintf("You can't do that here.\n")
    }

    private fun matchesTrigger(input: String, triggers: List<String>): Boolean {
        val inputWords = splitTokens(input)
        if (inputWords.isEmpty()) return false
        for (t in triggers) {
            if (t == "*") return true
            val triggerWords = splitTokens(t)
            if (triggerWords.isEmpty()) continue
            
            var inputIdx = 0
            var matchedAll = true
            for (tw in triggerWords) {
                var found = false
                while (inputIdx < inputWords.size) {
                    if (inputWords[inputIdx] == tw) {
                        found = true
                        inputIdx++
                        break
                    }
                    inputIdx++
                }
                if (!found) {
                    matchedAll = false
                    break
                }
            }
            if (matchedAll) return true
        }
        return false
    }

    private fun getVariableOrValue(value: Int): Int {
        return if (value and 0x8000 != 0) {
            val idx = value and 0x7FFF
            if (idx in variables.indices) variables[idx] else 0
        } else {
            value
        }
    }

    private fun checkConditions(conditions: List<Condition>): Boolean {
        for (cond in conditions) {
            when (cond.type) {
                "t?" -> {
                    val loc = getItemLocation(cond.target)
                    if (loc != -1) return false
                }
                "t~" -> {
                    val loc = getItemLocation(cond.target)
                    if (loc == -1) return false
                }
                "o?" -> {
                    val loc = getItemLocation(cond.target)
                    if (loc != currentLoc && loc != -1) return false
                }
                "o~" -> {
                    val loc = getItemLocation(cond.target)
                    if (loc == currentLoc || loc == -1) return false
                }
                "s?" -> {
                    val loc = cond.target.toIntOrNull() ?: 0
                    val expectedState = cond.value
                    if ((locStates[loc] ?: 0) != expectedState) return false
                }
                "v?" -> {
                    val vIdx = cond.target.toIntOrNull() ?: 0
                    val expected = getVariableOrValue(cond.value)
                    if (vIdx in variables.indices && variables[vIdx] != expected) return false
                }
                "v<" -> {
                    val vIdx = cond.target.toIntOrNull() ?: 0
                    val expected = getVariableOrValue(cond.value)
                    if (vIdx in variables.indices && variables[vIdx] >= expected) return false
                }
                "v>" -> {
                    val vIdx = cond.target.toIntOrNull() ?: 0
                    val expected = getVariableOrValue(cond.value)
                    if (vIdx in variables.indices && variables[vIdx] <= expected) return false
                }
            }
        }
        return true
    }

    private fun executeEffects(effects: List<Effect>) {
        for (eff in effects) {
            when (eff.type) {
                "m" -> {
                    if (eff.strVal.isNotEmpty()) {
                        myPrintf(formatMsg(eff.strVal) + "\n")
                    }
                }
                "s=" -> {
                    val loc = eff.target.toIntOrNull() ?: currentLoc
                    val oldState = locStates[loc] ?: 0
                    if (oldState != eff.val1) {
                        locStates[loc] = eff.val1
                        if (loc in locSeen.indices) {
                            locSeen[loc] = 0
                        }
                    }
                }
                "t+" -> {
                    val prevLoc = getItemLocation(eff.target)
                    if (prevLoc != -1) {
                        setItemLocation(eff.target, -1)
                        variables[108]++
                    }
                }
                "t-" -> {
                    val prevLoc = getItemLocation(eff.target)
                    if (prevLoc == -1) {
                        variables[108]--
                    }
                    setItemLocation(eff.target, 0)
                }
                "o+" -> {
                    val targetLoc = if (eff.val1 > 0) eff.val1 else currentLoc
                    val prevLoc = getItemLocation(eff.target)
                    if (prevLoc == -1) {
                        variables[108]--
                    }
                    setItemLocation(eff.target, targetLoc)
                }
                "o-" -> {
                    val prevLoc = getItemLocation(eff.target)
                    if (prevLoc == -1) {
                        variables[108]--
                    }
                    setItemLocation(eff.target, 0)
                }
                "v=" -> {
                    val vIdx = eff.target.toIntOrNull() ?: 0
                    val value = getVariableOrValue(eff.val1)
                    if (vIdx in variables.indices) variables[vIdx] = value
                }
                "v+" -> {
                    val vIdx = eff.target.toIntOrNull() ?: 0
                    val value = getVariableOrValue(eff.val1)
                    if (vIdx in variables.indices) variables[vIdx] += value
                }
                "v-" -> {
                    val vIdx = eff.target.toIntOrNull() ?: 0
                    val value = getVariableOrValue(eff.val1)
                    if (vIdx in variables.indices) {
                        variables[vIdx] -= value
                        if (variables[vIdx] < 0) variables[vIdx] = 0
                    }
                }
            }
        }
    }

    private fun takeItem(name: String) {
        val itemLoc = getItemLocation(name)
        if (itemLoc == currentLoc) {
            if (variables[108] >= variables[109]) {
                myPrintf("You can't carry anything more; perhaps you should drop something.\n")
                return
            }
            setItemLocation(name, -1)
            variables[108]++
            myPrintf("Done\n")
        } else if (itemLoc == -1) {
            val item = itemLookup[cleanItemName(name)]
            val displayName = item?.primaryName ?: name
            myPrintf("You're already carrying the $displayName.\n")
        } else {
            myPrintf("I don't see any $name here.\n")
        }
    }

    private fun dropItem(name: String) {
        val itemLoc = getItemLocation(name)
        if (itemLoc == -1) {
            setItemLocation(name, currentLoc)
            variables[108]--
            myPrintf("Done\n")
        } else {
            myPrintf("You aren't carrying that.\n")
        }
    }

    private fun doInventory() {
        val carried = itemsList.filter { !it.isSynonym && getItemLocation(it.fullName) == -1 }
        if (carried.isEmpty()) {
            myPrintf("You're empty-handed\n")
        } else {
            for (i in carried.indices) {
                val item = carried[i]
                val article = getArticle(item.fullName, item.isPlural)
                val prefix = if (article.isNotEmpty()) "$article " else ""
                if (i == 0) {
                    myPrintf("You are carrying $prefix${item.fullName}\n")
                } else {
                    myPrintf("             and $prefix${item.fullName}\n")
                }
            }
        }
    }

    private fun doSave(fileName: String) {
        myPrintf("Saving the current environment under the name \"$fileName\" ...\n")
        try {
            val state = WanderSaveState(
                currentLoc = currentLoc,
                locStates = locStates.mapKeys { it.key.toString() },
                locSeen = locSeen.toList(),
                variables = variables.toList(),
                items = itemsList.filter { !it.isSynonym }.associate { it.fullName to getItemLocation(it.fullName) }
            )
            val jsonText = Json.encodeToString(state)
            com.funhouse.shared.common.utils.saveTextToFile(fileName, jsonText)
        } catch (e: Exception) {
            GcLog.e("Error saving game state", e)
            myPrintf("Can't open \"$fileName\", sorry\n")
        }
    }

    private fun doRestore(fileName: String) {
        myPrintf("Restoring from the file \"$fileName\" ..\n")
        val jsonText = com.funhouse.shared.common.utils.readTextFromFile(fileName)
        if (jsonText == null) {
            myPrintf("Can't open \"$fileName\", sorry\n")
            return
        }

        try {
            val state = Json.decodeFromString<WanderSaveState>(jsonText)
            currentLoc = state.currentLoc
            locStates.clear()
            state.locStates.forEach { (key, value) ->
                key.toIntOrNull()?.let { locStates[it] = value }
            }
            state.locSeen.forEachIndexed { i, value ->
                if (i < locSeen.size) locSeen[i] = value
            }
            state.variables.forEachIndexed { i, value ->
                if (i < variables.size) variables[i] = value
            }
            state.items.forEach { (key, value) ->
                setItemLocation(key, value)
            }

            prloc(isLook = true)
        } catch (e: Exception) {
            GcLog.e("Error restoring game state", e)
            myPrintf("Can't open \"$fileName\", sorry\n")
        }
    }

    private fun splitLogicalLines(content: String): List<String> {
        val lines = mutableListOf<String>()
        val sb = StringBuilder()
        var quote = false
        var escape = false
        var i = 0
        var startOfPhysicalLine = true
        while (i < content.length) {
            val c = content[i]
            if (startOfPhysicalLine && c == ':') {
                while (i < content.length && content[i] != '\n') {
                    i++
                }
                startOfPhysicalLine = true
                i++
                continue
            }
            startOfPhysicalLine = (c == '\n')

            if (escape) {
                sb.append('\\').append(c)
                escape = false
            } else {
                if (c == '\\') {
                    escape = true
                } else if (c == '"') {
                    quote = !quote
                    sb.append(c)
                } else if (c == '\n') {
                    if (quote) {
                        sb.append('\n')
                    } else {
                        lines.add(sb.toString())
                        sb.setLength(0)
                    }
                } else {
                    sb.append(c)
                }
            }
            i++
        }
        if (sb.isNotEmpty()) {
            lines.add(sb.toString())
        }
        return lines
    }

    private fun splitTokens(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var escape = false
        var quote = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (escape) {
                sb.append(c)
                escape = false
            } else {
                if (c == '\\') {
                    escape = true
                } else if (c == '"') {
                    quote = !quote
                    sb.append(c)
                } else if ((c == ' ' || c == '\t') && !quote) {
                    if (sb.isNotEmpty()) {
                        tokens.add(sb.toString())
                        sb.setLength(0)
                    }
                } else {
                    sb.append(c)
                }
            }
            i++
        }
        if (sb.isNotEmpty()) {
            tokens.add(sb.toString())
        }
        return tokens
    }

    private fun getTriggerEndIndex(line: String): Int {
        var escape = false
        var quote = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (escape) {
                escape = false
            } else {
                if (c == '\\') {
                    escape = true
                } else if (c == '"') {
                    quote = !quote
                } else if ((c == ' ' || c == '\t') && !quote) {
                    return i
                }
            }
            i++
        }
        return line.length
    }

    private fun cleanMessageString(raw: String): String {
        var s = raw.trim()
        if (s.startsWith("\\")) {
            s = s.substring(1).trim()
        }
        if (s.startsWith("\"") && s.endsWith("\"") && s.length >= 2) {
            s = s.substring(1, s.length - 1)
        }
        return cleanEscapes(s)
    }

    private fun getVariableIndex(name: String): Int {
        val clean = name.trim().uppercase()
        return when (clean) {
            "CUR_LOC" -> 100
            "PREV_LOC" -> 101
            "INP_W1" -> 102
            "INP_W2" -> 103
            "INP_W3" -> 104
            "INP_W4" -> 105
            "INP_W5" -> 106
            "INP_WC" -> 107
            "NUM_CARRY" -> 108
            "MAX_CARRY" -> 109
            "NOW_YEAR" -> 110
            "NOW_MONTH" -> 111
            "NOW_DOM" -> 112
            "NOW_DOW" -> 113
            "NOW_HOUR" -> 114
            "NOW_MIN" -> 115
            "NOW_SEC" -> 116
            "NOW_ET" -> 117
            "BREVITY" -> 118
            "LOC_VIEW" -> 119
            "OBJ_VIEW" -> 120
            "INP_N1" -> 121
            "INP_N2" -> 122
            "NUM_MOVES" -> 123
            "NUM_PLACES" -> 124
            else -> name.toIntOrNull() ?: -1
        }
    }

    private fun formatMsg(text: String): String {
        if (!text.contains("%")) return text
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (c == '%') {
                if (i + 1 < text.length && text[i + 1] == '%') {
                    sb.append('%')
                    i += 2
                    continue
                }
                val end = text.indexOf('%', i + 1)
                if (end != -1) {
                    val varName = text.substring(i + 1, end).trim()
                    val idx = getVariableIndex(varName)
                    if (idx in variables.indices) {
                        if (idx in 102..106) {
                            val wIdx = idx - 102
                            if (wIdx in lastCommandWords.indices) {
                                sb.append(lastCommandWords[wIdx])
                            }
                        } else {
                            sb.append(variables[idx])
                        }
                    } else {
                        sb.append("%").append(varName).append("%")
                    }
                    i = end + 1
                } else {
                    sb.append('%')
                    i++
                }
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }

    private fun normalizeInput(input: String): String {
        var normalized = input.trim().lowercase()
        val itemsByLength = itemsList.filter { !it.isSynonym }.sortedByDescending { it.fullName.length }
        for (primary in itemsByLength) {
            val synonyms = itemsList.filter { it.primaryName == primary.fullName }
            val shortestSynonym = synonyms.minByOrNull { it.fullName.length }?.fullName ?: primary.fullName
            
            if (primary.fullName != shortestSynonym) {
                normalized = replaceWord(normalized, primary.fullName, shortestSynonym)
            }
            for (syn in synonyms) {
                if (syn.fullName != shortestSynonym) {
                    normalized = replaceWord(normalized, syn.fullName, shortestSynonym)
                }
            }
        }
        return normalized
    }

    private fun replaceWord(text: String, target: String, replacement: String): String {
        val pattern = Regex("\\b${Regex.escape(target)}\\b")
        return text.replace(pattern, replacement)
    }
}

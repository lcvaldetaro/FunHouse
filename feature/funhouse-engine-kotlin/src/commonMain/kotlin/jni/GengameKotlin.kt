package jni

import club.gepetto.GcLog
import com.funhouse.shared.common.jni.BaseKotlinGame
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.models.Settings
import com.funhouse.feature.funhouseenginekotlin.models.Place
import com.funhouse.feature.funhouseenginekotlin.models.GameObject
import com.funhouse.feature.funhouseenginekotlin.models.Goal
import com.funhouse.feature.funhouseenginekotlin.models.PlayerInstance
import com.funhouse.feature.funhouseenginekotlin.util.DatabaseLoader
import com.funhouse.feature.funhouseenginekotlin.net.NetworkMessage
import com.funhouse.feature.funhouseenginekotlin.util.*
import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.funhouse.feature.funhouseenginekotlin.net.GameSocketClient
import com.funhouse.feature.funhouseenginekotlin.net.GameSocketServer
import com.funhouse.feature.funhouseenginekotlin.net.DiscoveryHelper
import com.funhouse.shared.common.jni.TerminalDataCallback
@Serializable
data class GameSaveState(
    val objPosition: List<Int>,
    val objOwner: List<Int>,
    val objStatus: List<Int>,
    val objTaken: List<Int>,
    val placesStatus: Map<Int, Int>,
    val placesDisc: Map<Int, Int>,
    val placesM: Map<Int, List<Int>>,
    val players: List<PlayerInstance>
)

class GengameKotlin : BaseKotlinGame() {

    companion object {
        private const val _SOUTH = 0
        private const val _NORTH = 1
        private const val _WEST = 2
        private const val _EAST = 3
        private const val _SW = 4
        private const val _SE = 5
        private const val _NW = 6
        private const val _NE = 7
        private const val _UP = 8
        private const val _DOWN = 9
        private const val _IN = 10
        private const val _OUT = 11
    }

    // Verbs, nouns, pronouns, conjunctions tables
    private val verbTable = arrayOf(
        "",
        "N", "S", "E", "W", "LOOK",
        "GET", "DROP", "DIG", "OPEN", "CLOSE",
        "UNLOCK", "read", "LOAD", "SAVE", "START",
        "TURN", "TAKE", "LEAVE", "RESTORE", "?",
        "HELP", "INVENTORY", "WHERE", "ENTER", "QUIT",
        "NORTH", "SOUTH", "EAST", "WEST", "IN",
        "OUT", "UP", "DOWN", "SW", "SE",
        "NW", "NE", "SCORE", "ASK", "LIST",
        "GRAB", "INITIALIZE", "KILL", "FIGHT", "ARREST",
        "REPAIR", "FIX", "BATTLE", "MOVE", "GO",
        "INSIDE", "OUTSIDE", "GOAL", "GOALS", "PLAYERS", "PLACES"
    )

    private val conjuTable = arrayOf(
        "",
        "THE", "AN", "A", "ON", "OF",
        "OFF", "FROM", "TO"
    )

    private val pronTable = arrayOf(
        "",
        "HEAVY", "GOLDEN", "IRON", "PLASTIC", "WOODEN",
        "SMALL", "OLD", "YELLOW", "ANCIENT"
    )

    private val nounTable = arrayOf(
        "",
        "NAME", "NAMES", "GAME", "YES",
        "NO", "AM", "I", "EXIT", "VOCABULARY",
        "PLEASE"
    )

    // Game Definitions and Master Config
    private var gameTitle = "Game Collection"
    private var placeFile = ""
    private var objsFile = ""
    private var goalFile = ""
    private var saveFile = "funhouse"
    private var helpFile = ""
    private var gameDescription = ""
    private var maximumObjectsCarried = 5
    private var gameStarts = mutableListOf<Int>()
    private var autoSave = 0
    private var autoRestore = 0

    // Database Collections
    private val places = mutableMapOf<Int, Place>()
    private val objects = mutableMapOf<Int, GameObject>()
    private val goals = mutableListOf<Goal>()

    // Authoritative Shared Game State
    private val objPosition = IntArray(2048)
    private val objOwner = IntArray(2048) { -1 }
    private val objStatus = IntArray(2048)
    private val objTaken = IntArray(2048)

    var isUnitTest = false
    var networkPort = 8082

    // ThreadLocal for the executing player index (1 = MAIN_PLAYER/Host, 2, 3... = Clients)
    private val activePlayerIndex = gcThreadLocal { 1 }

    // Registry of active players
    internal val players = GcConcurrentMap<Int, PlayerInstance>()
    private val playerInputQueues = GcConcurrentMap<Int, GcQueue<String>>()
    private val playerThreads = GcConcurrentMap<Int, GcThreadRef>()

    private val activePlayerIdx: Int
        get() = activePlayerIndex.get() ?: 1

    private fun getPlayer(): PlayerInstance {
        val idx = activePlayerIdx
        return players.getOrPut(idx) {
            PlayerInstance(
                index = idx,
                use = 1,
                online = 1,
                handle = if (idx == 1) {
                    val s = com.funhouse.shared.common.models.currentSettings
                    s.playerHandle.ifEmpty { s.playerNickName.ifEmpty { "Host" } }
                } else "Player_$idx"
            )
        }
    }

    private var myPosition: Int
        get() = getPlayer().myPosition
        set(value) { getPlayer().myPosition = value }

    private var myPoints: Int
        get() = getPlayer().myPoints
        set(value) { getPlayer().myPoints = value }

    private var mySavedPoints: Int
        get() = getPlayer().mySavedPoints
        set(value) { getPlayer().mySavedPoints = value }

    private var myPlacePoints: Int
        get() = getPlayer().myPlacePoints
        set(value) { getPlayer().myPlacePoints = value }

    private var numMovements: Int
        get() = getPlayer().numMovements
        set(value) { getPlayer().numMovements = value }

    private var numObjsCarried: Int
        get() = getPlayer().numObjsCarried
        set(value) { getPlayer().numObjsCarried = value }

    private var gameover: Int
        get() = getPlayer().gameover
        set(value) { getPlayer().gameover = value }

    private var helped: Int
        get() = getPlayer().helped
        set(value) { getPlayer().helped = value }

    private var goalIndex: Int
        get() = getPlayer().goal
        set(value) { getPlayer().goal = value }

    private var sand: Int
        get() = getPlayer().sand
        set(value) { getPlayer().sand = value }

    private var alertedLost: Int
        get() = getPlayer().alertedLost
        set(value) { getPlayer().alertedLost = value }

    private val placeVisited: IntArray
        get() = getPlayer().placeVisited

    // Parser State
    private var iverb: Int
        get() = getPlayer().iverb
        set(value) { getPlayer().iverb = value }

    private var iobj: Int
        get() = getPlayer().iobj
        set(value) { getPlayer().iobj = value }

    private var ipron: Int
        get() = getPlayer().ipron
        set(value) { getPlayer().ipron = value }

    private var inoun: Int
        get() = getPlayer().inoun
        set(value) { getPlayer().inoun = value }

    private var iconju: Int
        get() = getPlayer().iconju
        set(value) { getPlayer().iconju = value }

    private var myErr: Int
        get() = getPlayer().myErr
        set(value) { getPlayer().myErr = value }

    private var errpos: Int
        get() = getPlayer().errpos
        set(value) { getPlayer().errpos = value }

    private var argv: List<String>
        get() = getPlayer().argv
        set(value) { getPlayer().argv = value }

    private var argc: Int
        get() = getPlayer().argc
        set(value) { getPlayer().argc = value }

    // Network states
    private var isClient = false
    private var networkActive = false
    private var clientSocket: GameSocketClient? = null
    private val clientSessions = GcConcurrentMap<Int, String>()
    private var serverInstance: GameSocketServer? = null
    private var nsdHelper: DiscoveryHelper? = null

    // Threading & Sockets
    private var gameNickName = "funhouse"
    private val packageFolder get() = AppData.packageFolder ?: ""
    private val gameFolder get() = AppData.gameFolder ?: ""

    private var totalGoals = 0
    private var hostTerminalCallback: TerminalDataCallback? = null

    // Shadowing Print Method to redirect outputs by intercepting terminal callbacks
    override fun registerTerminalCallback(callback: TerminalDataCallback) {
        this.hostTerminalCallback = callback
        super.registerTerminalCallback(object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(text: String) {
                val idx = activePlayerIdx
                if (idx == 1) {
                    hostTerminalCallback?.onNewTerminalDataReceived(text)
                } else {
                    sendToClient(idx, text)
                }
            }
        })
    }

    private fun sendToClient(playerIndex: Int, message: String) {
        val session = clientSessions[playerIndex]
        if (session != null) {
            try {
                val update = NetworkMessage.TerminalUpdate(message)
                serverInstance?.sendToSession(session, Json.encodeToString<NetworkMessage>(update))
            } catch (e: Exception) {
                GcLog.e("Failed to send update to client $playerIndex", e)
            }
        }
    }

    private fun broadcastToRoom(roomNum: Int, message: String) {
        players.forEach { (idx, player) ->
            if (player.use == 1 && player.myPosition == roomNum) {
                if (idx == 1) {
                    hostTerminalCallback?.onNewTerminalDataReceived(message)
                } else {
                    sendToClient(idx, message)
                }
            }
        }
    }

    override fun start() {
        start("funhouse")
    }

    override fun stop() {
        GcLog.d("GengameKotlin.stop() called")
        stopNetwork()
    }

    internal fun stopNetwork() {
        networkActive = false
        GcLog.d("GengameKotlin.stopNetwork() called to clean up sockets/servers")
        try {
            nsdHelper?.unregisterService()
        } catch (e: Exception) {
            GcLog.e("Failed to unregister NSD service", e)
        }
        nsdHelper = null

        clientSessions.forEach { (idx, session) ->
            try {
                serverInstance?.closeSession(session)
            } catch (e: Exception) {
                GcLog.e("Failed to close client session $idx", e)
            }
        }
        clientSessions.clear()

        playerThreads.forEach { (idx, thread) ->
            try {
                thread.interrupt()
            } catch (e: Exception) {
                GcLog.e("Failed to interrupt player thread $idx", e)
            }
        }
        playerThreads.clear()

        try {
            serverInstance?.stop()
        } catch (e: Exception) {
            GcLog.e("Failed to stop server instance", e)
        }
        serverInstance = null

        try {
            clientSocket?.close()
        } catch (e: Exception) {
            GcLog.e("Failed to close client socket", e)
        }
        clientSocket = null

        players.clear()
        playerInputQueues.clear()
    }

    var isMultiplayer: Boolean = true

    override fun start(gameNickName: String, isMultiplayer: Boolean) {
        this.isMultiplayer = isMultiplayer
        start(gameNickName)
    }

    override fun start(gameNickName: String) {
        GcLog.d("GengameKotlin.start() called for game $gameNickName (isMultiplayer=$isMultiplayer)")
        stopNetwork()
        networkActive = true
        this.gameNickName = gameNickName

        gcThread(name = "GameNetworkInitThread") {
            val settings = com.funhouse.shared.common.models.currentSettings
            if (settings.playerHandle.trim().isEmpty() && !isUnitTest) {
                myPrintf("Please enter your player handle:\n")
                var handleInput = ""
                while (handleInput.isEmpty()) {
                    handleInput = getLineForPlayer(1).trim()
                }
                settings.playerHandle = handleInput
                val genre = com.funhouse.feature.funhouseenginekotlin.utils.getGenreForNickname(gameNickName)
                val randomType = com.funhouse.feature.funhouseenginekotlin.net.HandleType.random(genre)
                val randomDesc = com.funhouse.feature.funhouseenginekotlin.net.HandleDescription.random(genre)
                settings.handleType = randomType.displayName
                settings.handleDescription = randomDesc.text
                settings.save()
                myPrintf("Welcome, %s the %s!\n(%s)\n\n", settings.playerHandle, settings.handleType, settings.handleDescription)
            } else if (!isUnitTest) {
                val genre = com.funhouse.feature.funhouseenginekotlin.utils.getGenreForNickname(gameNickName)
                val savedTypeEnum = com.funhouse.feature.funhouseenginekotlin.net.HandleType.entries.firstOrNull { 
                    it.displayName.equals(settings.handleType, ignoreCase = true) 
                }
                val savedDescEnum = com.funhouse.feature.funhouseenginekotlin.net.HandleDescription.entries.firstOrNull { 
                    it.text.equals(settings.handleDescription, ignoreCase = true) 
                }
                val typeMatches = savedTypeEnum != null && savedTypeEnum.genre == genre
                val descMatches = savedDescEnum != null && savedDescEnum.genre == genre

                if (settings.handleType.isEmpty() || !typeMatches) {
                    settings.handleType = com.funhouse.feature.funhouseenginekotlin.net.HandleType.random(genre).displayName
                }
                if (settings.handleDescription.isEmpty() || !descMatches) {
                    settings.handleDescription = com.funhouse.feature.funhouseenginekotlin.net.HandleDescription.random(genre).text
                }
                settings.save()
            }

            if (!isMultiplayer) {
                GcLog.d("Single-player mode active for $gameNickName. Bypassing networking and server setup.")
                isClient = false
                gcThread(name = "Player_1_Thread") {
                    try {
                        runGameForPlayer(1)
                    } catch (e: Exception) {
                        GcLog.e("Host player thread crash", e)
                    }
                }
                return@gcThread
            }

            // Scan for host
            var hostFound = false
            var hostAddress = ""
            var hostPort = 0

            if (isWebPlatform()) {
                myPrintf("Enter host IP address (default 127.0.0.1):\n")
                val ipInput = getLineForPlayer(1).trim()
                hostAddress = ipInput.ifEmpty { "127.0.0.1" }

                myPrintf("Enter host port (default %d):\n", networkPort)
                val portInput = getLineForPlayer(1).trim()
                hostPort = portInput.toIntOrNull() ?: networkPort
                hostFound = true
            } else if (!isUnitTest) {
                val ctx = AppData.applicationContext
                if (ctx != null) {
                    GcLog.d("Starting NSD Host discovery for gameNickName=$gameNickName")
                    val nsd = DiscoveryHelper()
                    nsd.onHostDiscovered = { ip, port ->
                        GcLog.d("NSD host discovered! ip=$ip, port=$port")
                        if (!hostFound) {
                            hostFound = true
                            hostAddress = ip
                            hostPort = port
                        }
                    }
                    nsd.discoverServices(gameNickName)
                    gcSleep(2000)
                    nsd.stopDiscovery()
                } else {
                    GcLog.w("Application Context is null, skipping NSD discovery")
                }

                if (!hostFound) {
                    GcLog.d("NSD discovery found nothing. Checking candidates and local subnet...")
                    if (isHostPortAvailable("127.0.0.1", networkPort)) {
                        hostFound = true
                        hostAddress = "127.0.0.1"
                        hostPort = networkPort
                    } else if (isHostPortAvailable("10.0.2.2", networkPort)) {
                        hostFound = true
                        hostAddress = "10.0.2.2"
                        hostPort = networkPort
                    } else {
                        val localIps = getLocalIps()
                        localIps.forEach { myIp ->
                            if (!hostFound) {
                                scanSubnetForHost(myIp, networkPort) { ip ->
                                    if (!hostFound) {
                                        hostFound = true
                                        hostAddress = ip
                                        hostPort = networkPort
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!networkActive) {
                GcLog.d("Network initialization cancelled: networkActive is false")
                return@gcThread
            }

            if (hostFound) {
                GcLog.d("Starting client connection to host=$hostAddress:$hostPort")
                isClient = true
                startClient(hostAddress, hostPort)
            } else {
                GcLog.d("No host found. Starting server...")
                isClient = false
                startServer()
            }
        }
    }

    override fun sendCommand(command: String): Int {
        if (command.trim().equals("about", ignoreCase = true)) {
            val about = when (gameNickName) {
                "funhouse" -> "Fun House Adventure\nVersion 2.0\nEscape if you can!"
                else -> "Lost Island Adventure\nVersion 2.0\nFind the treasure and escape!"
            }
            myPrintf("%s\n", about)
            return 0
        }

        if (isClient) {
            val socket = clientSocket
            if (socket != null) {
                gcThread(name = "ClientSendCommandThread") {
                    try {
                        val cmd = NetworkMessage.SubmitCommand(command)
                        socket.send(Json.encodeToString<NetworkMessage>(cmd))
                    } catch (e: Exception) {
                        GcLog.e("Failed to send command to server", e)
                    }
                }
            } else {
                GcLog.w("Client socket is null when sending command: $command")
            }
            return 0
        }

        playerInputQueues.getOrPut(1) { GcQueue() }.put(command)
        return 0
    }


    private suspend fun getLineForPlayer(playerIndex: Int): String {
        val q = playerInputQueues.getOrPut(playerIndex) { GcQueue() }
        return q.take().trim()
    }

    private fun startClient(hostIp: String, port: Int) {
        myPrintf("Connecting to host at $hostIp:$port...\n")
        var joinedSuccessfully = false
        val socket = GameSocketClient()
        clientSocket = socket
        socket.connect(
            host = hostIp,
            port = port,
            onOpen = {
                val settings = Settings.restore()
                val handle = settings.playerHandle.ifEmpty { settings.playerNickName.ifEmpty { "Player_" + Random.nextInt(1000) } }
                val genre = com.funhouse.feature.funhouseenginekotlin.utils.getGenreForNickname(gameNickName)
                val savedTypeEnum = com.funhouse.feature.funhouseenginekotlin.net.HandleType.entries.firstOrNull { 
                    it.displayName.equals(settings.handleType, ignoreCase = true) 
                }
                val savedDescEnum = com.funhouse.feature.funhouseenginekotlin.net.HandleDescription.entries.firstOrNull { 
                    it.text.equals(settings.handleDescription, ignoreCase = true) 
                }
                val typeMatches = savedTypeEnum != null && savedTypeEnum.genre == genre
                val descMatches = savedDescEnum != null && savedDescEnum.genre == genre

                val hType = if (settings.handleType.isNotEmpty() && typeMatches) {
                    settings.handleType
                } else {
                    com.funhouse.feature.funhouseenginekotlin.net.HandleType.random(genre).displayName
                }
                val hDesc = if (settings.handleDescription.isNotEmpty() && descMatches) {
                    settings.handleDescription
                } else {
                    com.funhouse.feature.funhouseenginekotlin.net.HandleDescription.random(genre).text
                }

                val joinReq = NetworkMessage.JoinRequest(
                    nickname = handle,
                    gender = hType,
                    description = hDesc,
                    gameNickName = gameNickName,
                    playerHandle = handle,
                    handleType = hType,
                    handleDescription = hDesc
                )
                socket.send(Json.encodeToString<NetworkMessage>(joinReq))
            },
            onMessage = { msgText ->
                try {
                    val msg = Json.decodeFromString<NetworkMessage>(msgText)
                    when (msg) {
                        is NetworkMessage.JoinResponse -> {
                            if (msg.success) {
                                myPrintf("Connected! Welcome to %s.\n", msg.gameTitle)
                                joinedSuccessfully = true
                            } else {
                                myPrintf("Connection rejected: ${msg.errorMessage}\n")
                                socket.close()
                            }
                        }
                        is NetworkMessage.TerminalUpdate -> {
                            callback?.onNewTerminalDataReceived(msg.text)
                        }
                        is NetworkMessage.BroadcastUpdate -> {
                            callback?.onNewTerminalDataReceived(msg.text)
                        }
                        else -> {}
                    }
                } catch (e: Exception) {
                    GcLog.e("Client JSON decode error", e)
                }
            },
            onClose = {
                myPrintf("Disconnected from server.\n")
                clientSocket = null
                if (networkActive && !joinedSuccessfully) {
                    GcLog.d("Client failed to join. Falling back to startServer()")
                    isClient = false
                    startServer()
                }
            }
        )
    }

    private fun startServer() {
        if (!networkActive) {
            GcLog.d("startServer() aborted: networkActive is false")
            return
        }
        val ips = getLocalIps()
        val ipStr = if (ips.isEmpty()) "localhost" else ips.joinToString(", ")
        myPrintf("Starting host server at IP(s): %s on port: %d\n", ipStr, networkPort)

        val settings = Settings.restore()
        val nickname = settings.playerNickName.ifEmpty { "Host" }
        val ctx = AppData.applicationContext
        if (ctx != null) {
            nsdHelper = DiscoveryHelper().apply {
                registerService(gameNickName, nickname, networkPort)
            }
        }

        gcThread(name = "GameServerThread") {
            try {
                val server = GameSocketServer()
                serverInstance = server
                server.start(
                    port = networkPort,
                    onMessage = { sessionId, text ->
                        try {
                            val msg = Json.decodeFromString<NetworkMessage>(text)
                            when (msg) {
                                is NetworkMessage.JoinRequest -> {
                                    if (msg.gameNickName != gameNickName) {
                                        val response = NetworkMessage.JoinResponse(
                                            success = false,
                                            playerIndex = -1,
                                            gameTitle = gameTitle,
                                            gameDescription = gameDescription,
                                            playerGoal = "",
                                            errorMessage = "No server found for game $gameNickName."
                                        )
                                        server.sendToSession(sessionId, Json.encodeToString<NetworkMessage>(response))
                                        server.closeSession(sessionId)
                                        return@start
                                    }
                                    val pHandle = msg.playerHandle.ifEmpty { msg.nickname.ifEmpty { "Player" } }
                                    val pType = msg.handleType.ifEmpty { msg.gender.ifEmpty { "Wanderer" } }
                                    val pDesc = msg.handleDescription.ifEmpty { msg.description.ifEmpty { "A mysterious wanderer" } }

                                    var isReconnect = false
                                    var playerIndex = -1
                                    gcSynchronized(this@GengameKotlin) {
                                        var idx = players.values.find { it.handle == pHandle }?.index ?: -1
                                        if (idx == -1) {
                                            idx = 2
                                            while (players.containsKey(idx)) { idx++ }
                                            val startPos = gameStarts.getOrNull(Random.nextInt(gameStarts.size)) ?: 1
                                            val startGoal = Random.nextInt(totalGoals) + 1
                                            val newPlayer = PlayerInstance(
                                                index = idx,
                                                use = 1,
                                                online = 1,
                                                handle = pHandle,
                                                gender = pType,
                                                description = pDesc,
                                                infoGiven = 1,
                                                myPosition = startPos,
                                                goal = startGoal
                                            )
                                            players[idx] = newPlayer
                                        } else {
                                            players[idx]?.online = 1
                                            isReconnect = true
                                        }
                                        playerIndex = idx
                                        clientSessions[idx] = sessionId
                                    }

                                    val response = NetworkMessage.JoinResponse(
                                        success = true,
                                        playerIndex = playerIndex,
                                        gameTitle = gameTitle,
                                        gameDescription = gameDescription,
                                        playerGoal = goals.getOrNull(players[playerIndex]?.goal ?: 1)?.name ?: ""
                                    )
                                    server.sendToSession(sessionId, Json.encodeToString<NetworkMessage>(response))

                                    gcSynchronized(this@GengameKotlin) {
                                        playerThreads[playerIndex]?.let { oldThread ->
                                            try {
                                                oldThread.interrupt()
                                            } catch (e: Exception) {
                                                GcLog.e("Failed to interrupt old player thread", e)
                                            }
                                        }
                                    }

                                    val newThread = gcThread(name = "Player_${playerIndex}_Thread") {
                                        try {
                                            runGameForPlayer(playerIndex, isReconnect)
                                        } catch (e: Exception) {
                                            if (e.message?.contains("Interrupt", ignoreCase = true) == true) {
                                                GcLog.d("Player thread interrupted for reconnect")
                                            } else {
                                                GcLog.e("Player thread crash", e)
                                            }
                                        } finally {
                                            gcSynchronized(this@GengameKotlin) {
                                                val currentThreadRef = playerThreads[playerIndex]
                                                if (currentThreadRef != null && currentThreadRef.isCurrentThread()) {
                                                    playerThreads.remove(playerIndex)
                                                }
                                            }
                                        }
                                    }
                                    playerThreads[playerIndex] = newThread

                                    if (isReconnect) {
                                        broadcastToRoom(players[playerIndex]?.myPosition ?: 1, "${pHandle} the ${pType} has entered the room.\n")
                                    } else {
                                        broadcastToRoom(players[playerIndex]?.myPosition ?: 1, "${pHandle} the ${pType} (${pDesc}) has entered the room.\n")
                                    }
                                }
                                is NetworkMessage.SubmitCommand -> {
                                    val idx = clientSessions.entries.find { it.value == sessionId }?.key ?: -1
                                    if (idx != -1) {
                                        playerInputQueues.getOrPut(idx) { GcQueue() }.put(msg.text)
                                    }
                                }
                                else -> {}
                            }
                        } catch (e: Exception) {
                            GcLog.e("Server connection handler error", e)
                        }
                    },
                    onConnect = { sessionId ->
                        // Connection opened
                    },
                    onClose = { sessionId ->
                        val idx = clientSessions.entries.find { it.value == sessionId }?.key ?: -1
                        if (idx != -1) {
                            gcSynchronized(this@GengameKotlin) {
                                players[idx]?.online = 0
                                clientSessions.remove(idx)
                            }
                            broadcastToRoom(players[idx]?.myPosition ?: 1, "${players[idx]?.handle} has vanished.\n")
                        }
                    }
                )
            } catch (e: Exception) {
                GcLog.e("GameServerThread server bind/start failed", e)
                myPrintf("Error starting server: ${e.message}. Port $networkPort might already be in use.\n")
            }
        }

        val hostThread = gcThread(name = "Player_1_Thread") {
            try {
                runGameForPlayer(1)
            } catch (e: Exception) {
                GcLog.e("Host player thread crash", e)
            } finally {
                gcSynchronized(this@GengameKotlin) {
                    playerThreads.remove(1)
                }
            }
        }
        playerThreads[1] = hostThread
    }

    private fun loadDatabase() {
        val gameObj = com.funhouse.shared.common.models.currentSettings.currentGame
        val baseFileName = when {
            gameObj != null && gameObj.mainGameFile.fileName.isNotEmpty() -> {
                gameObj.mainGameFile.fileName.removeSuffix(".csv")
            }
            gameObj != null && gameObj.saveFilePrefix.isNotEmpty() -> {
                gameObj.saveFilePrefix
            }
            gameNickName.startsWith("island") -> "island"
            gameNickName.startsWith("funhouse") -> "funhouse"
            else -> gameNickName
        }

        var currentBaseFolder = gameFolder
        var masterContent = com.funhouse.shared.common.utils.readAssetFile("$gameFolder/$baseFileName.csv")
        if (masterContent == null) {
            currentBaseFolder = ""
            masterContent = com.funhouse.shared.common.utils.readAssetFile("$baseFileName.csv")
        }

        if (masterContent == null) {
            myPrintf("Error: Master game definition file not found at $gameFolder/$baseFileName.csv\n")
            return
        }

        val masterMap = DatabaseLoader.loadMaster(masterContent)
        gameTitle = masterMap["title"] ?: "Adventure Game"
        placeFile = masterMap["places file"] ?: "${baseFileName}places"
        objsFile = masterMap["objects file"] ?: "${baseFileName}objects"
        goalFile = masterMap["goals file"] ?: "${baseFileName}goals"
        saveFile = masterMap["savefile"] ?: baseFileName
        helpFile = masterMap["helpfile"] ?: "${baseFileName}.md"
        gameDescription = masterMap["description"] ?: ""
        maximumObjectsCarried = masterMap["maximum objects carried"]?.toIntOrNull() ?: 5

        val startsStr = masterMap["start places"] ?: "1"
        gameStarts.clear()
        startsStr.replace("\"", "").split(",").map { it.trim().toIntOrNull() }.forEach {
            if (it != null) gameStarts.add(it)
        }
        if (gameStarts.isEmpty()) gameStarts.add(1)

        val settings = Settings.restore()
        autoSave = if (settings.autoSave) 1 else 0
        autoRestore = if (settings.autoRestore) 1 else 0

        places.clear()
        val placesPath = if (currentBaseFolder.isNotEmpty()) "$currentBaseFolder/$placeFile.csv" else "$placeFile.csv"
        val placesContent = com.funhouse.shared.common.utils.readAssetFile(placesPath) ?: ""
        DatabaseLoader.loadPlaces(placesContent).forEach { places[it.num] = it }

        objects.clear()
        val objsPath = if (currentBaseFolder.isNotEmpty()) "$currentBaseFolder/$objsFile.csv" else "$objsFile.csv"
        val objsContent = com.funhouse.shared.common.utils.readAssetFile(objsPath) ?: ""
        DatabaseLoader.loadObjects(objsContent).forEach {
            objects[it.num] = it
            objPosition[it.num] = it.location
            objStatus[it.num] = it.status
            objOwner[it.num] = -1
            objTaken[it.num] = 0
        }

        goals.clear()
        goals.add(Goal(0, "", 0, 0, 0, 0, 0, 0, "", IntArray(16), 0, IntArray(16), IntArray(16), "", "", ""))
        val goalsPath = if (currentBaseFolder.isNotEmpty()) "$currentBaseFolder/$goalFile.csv" else "$goalFile.csv"
        val goalsContent = com.funhouse.shared.common.utils.readAssetFile(goalsPath) ?: ""
        DatabaseLoader.loadGoals(goalsContent).forEach { goals.add(it) }
        totalGoals = goals.size - 1
    }

    private suspend fun runGameForPlayer(playerIndex: Int, isReconnect: Boolean = false) {
        activePlayerIndex.set(playerIndex)

        if (playerIndex == 1) {
            loadDatabase()

            var restored = false
            if (autoRestore == 1) {
                restored = restoreGame(silent = true)
            }

            if (!restored) {
                myPosition = gameStarts[Random.nextInt(gameStarts.size)]
                goalIndex = Random.nextInt(totalGoals) + 1
                myPoints = 0
                mySavedPoints = 0
                myPlacePoints = 0
                numMovements = 0
                numObjsCarried = 0
                gameover = 0
                helped = 0
                sand = 0
                placeVisited.fill(0)
                placeVisited[myPosition] = 1
            }
        } else {
            // Client initialization
            if (!isReconnect) {
                if (myPosition == 0) {
                    myPosition = gameStarts.getOrNull(Random.nextInt(gameStarts.size)) ?: 1
                }
                if (goalIndex == 0) {
                    goalIndex = Random.nextInt(totalGoals) + 1
                }
                myPoints = 0
                mySavedPoints = 0
                myPlacePoints = 0
                numMovements = 0
                numObjsCarried = 0
                gameover = 0
                helped = 0
                sand = 0
                placeVisited.fill(0)
                placeVisited[myPosition] = 1
            }
        }

        myPrintf("Welcome to %s!\n\r%s\n", gameTitle, gameDescription)
        myPrintf("Your goal is to %s\n", goals[goalIndex].name)

        if (helped == 0) {
            myPrintf("\nWould you like instructions?\nSay 'Yes' or 'No'.\nYou can say 'help' at anytime.\n")
            val resp = getLineForPlayer(playerIndex).lowercase()
            if (resp == "yes" || resp == "y" || resp == "help") {
                printInstructions()
            }
            helped = 1
        }

        describeRoom()

        while (true) {
            if (gameover == 1) {
                myPrintf("Would you like to start a new game?\n")
                val resp = getLineForPlayer(playerIndex).lowercase()
                if (resp == "yes" || resp == "y") {
                    startNewGame()
                }
                continue
            }

            myPrintf("\n>")
            val cmd = getLineForPlayer(playerIndex)
            if (cmd.isBlank()) continue

            // Handle NEWHANDLE command
            val trimmedCmd = cmd.trim()
            val lowerCmd = trimmedCmd.lowercase()
            if (lowerCmd == "newhandle" || lowerCmd.startsWith("newhandle ")) {
                val newHandle = if (lowerCmd.startsWith("newhandle ")) trimmedCmd.substring(10).trim() else ""
                if (newHandle.isNotEmpty()) {
                    gcSynchronized(this) {
                        val player = players[playerIndex]
                        val genre = com.funhouse.feature.funhouseenginekotlin.utils.getGenreForNickname(gameNickName)
                        val randomType = com.funhouse.feature.funhouseenginekotlin.net.HandleType.random(genre)
                        val randomDesc = com.funhouse.feature.funhouseenginekotlin.net.HandleDescription.random(genre)

                        val pHandle: String
                        val pType: String
                        val pDesc: String
                        val pPos: Int

                        val oldHandle: String

                        if (player != null) {
                            oldHandle = player.handle
                            val oldType = player.gender
                            player.handle = newHandle
                            player.gender = randomType.displayName
                            player.description = randomDesc.text

                            pHandle = player.handle
                            pType = player.gender
                            pDesc = player.description
                            pPos = player.myPosition

                            // Broadcast name change to the room
                            broadcastToRoom(pPos, "$oldHandle the $oldType has changed their handle to $pHandle the $pType.\n")
                        } else {
                            oldHandle = com.funhouse.shared.common.models.currentSettings.playerHandle
                            pHandle = newHandle
                            pType = randomType.displayName
                            pDesc = randomDesc.text
                            pPos = myPosition
                        }

                        // Update settings if it is player 1 (host/local player)
                        if (playerIndex == 1) {
                            val settings = com.funhouse.shared.common.models.currentSettings
                            settings.playerHandle = pHandle
                            settings.handleType = pType
                            settings.handleDescription = pDesc
                            settings.save()
                        }

                        if (oldHandle.trim().isNotEmpty()) {
                            myPrintf("Your handle was changed from %s to %s\n", oldHandle, pHandle)
                        } else {
                            myPrintf("Your handle is now %s\n", pHandle)
                        }
                        myPrintf("(%s)\n", pDesc)
                    }
                } else {
                    myPrintf("Usage: newhandle <your_new_handle>\n")
                }
                continue
            }

            numMovements++
            myErr = 0
            errpos = 0

            gcSynchronized(this) {
                activePlayerIndex.set(playerIndex)
                parseCommand(cmd)
                recognizeWords()
                checkForErrors()

                if (myErr == 0) {
                    executeCommand()
                } else {
                    printError()
                }
            }

            if (autoSave == 1) {
                saveGame(silent = true)
            }
        }
    }

    private fun printInstructions() {
        myPrintf(
            "I will be your eyes and hands. Direct me with commands of 1 or 2 words.\n" +
            "You have to input some orders like LOOK, OPEN, and so on.\n" +
            "To move, type commands like 'north' or their abbreviation such as 'N'.\n" +
            "You can also move using southwest, northeast and so on.\n" +
            "You can also use words as 'up', 'down', 'in', or 'out'.\n" +
            "To get an object say 'get' or 'take'. To no longer carry it, use 'drop'.\n" +
            "Save the game by saying 'save', restore it by saying 'restore'.\n" +
            "To see what you are carrying say 'Inventory'.\n" +
            "To repeat a description of a room, say 'look'.\n" +
            "To check the score, say 'score'. To quit say 'quit'.\n\n" +
            "Available commands:\n" +
            "N, S, E, W, LOOK, GET, DROP, DIG, OPEN, CLOSE, UNLOCK, READ, LOAD, SAVE, START, TURN, TAKE, LEAVE, RESTORE, ?, HELP, INVENTORY, WHERE, ENTER, QUIT, NORTH, SOUTH, EAST, WEST, IN, OUT, UP, DOWN, SW, SE, NW, NE, SCORE, ASK, LIST, GRAB, INITIALIZE, KILL, FIGHT, ARREST, REPAIR, FIX, BATTLE, MOVE, GO, INSIDE, OUTSIDE, GOAL, GOALS, PLAYERS, PLACES\n"
        )
    }

    private fun parseCommand(command: String) {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var quoteChar = ' '
        var i = 0
        while (i < command.length) {
            val c = command[i]
            if (!inQuotes && (c == '"' || c == '\'')) {
                inQuotes = true
                quoteChar = c
            } else if (inQuotes && c == quoteChar) {
                inQuotes = false
            } else if (c.isWhitespace() && !inQuotes) {
                if (current.isNotEmpty()) {
                    result.add(current.toString())
                    current = StringBuilder()
                }
            } else {
                current.append(c)
            }
            i++
        }
        if (current.isNotEmpty()) {
            result.add(current.toString())
        }
        argv = result
        argc = result.size
    }

    private val wordtype = IntArray(20)
    private val TYPE_VERB = 1
    private val TYPE_NOUN = 2
    private val TYPE_PRON = 3
    private val TYPE_CONJ = 4

    private fun recognizeWords() {
        wordtype.fill(0)
        for (j in 0 until argc) {
            val word = argv[j]
            // Check hardcoded verbs
            for (i in 1 until verbTable.size) {
                if (word.equals(verbTable[i], ignoreCase = true)) {
                    wordtype[j] = TYPE_VERB
                }
            }
            // Check objects
            for (i in 1..objects.size) {
                val obj = objects[i]
                if (obj != null && word.equals(obj.nickname, ignoreCase = true)) {
                    wordtype[j] = TYPE_NOUN
                }
            }
            // Check nouns
            for (i in 1 until nounTable.size) {
                if (word.equals(nounTable[i], ignoreCase = true)) {
                    wordtype[j] = TYPE_NOUN
                }
            }
            // Check pronouns
            for (i in 1 until pronTable.size) {
                if (word.equals(pronTable[i], ignoreCase = true)) {
                    wordtype[j] = TYPE_PRON
                }
            }
            // Check conjunctions
            for (i in 1 until conjuTable.size) {
                if (word.equals(conjuTable[i], ignoreCase = true)) {
                    wordtype[j] = TYPE_CONJ
                }
            }
        }
    }

    private fun checkVerbSynonym(v: Int): Int {
        return when (v) {
            11 -> 9      // unlock -> open
            24 -> 30     // enter -> in
            17 -> 6      // take -> get
            41 -> 6      // grab -> get
            26 -> 1      // north -> n
            27 -> 2      // south -> s
            28 -> 3      // east -> e
            29 -> 4      // west -> w
            47 -> 46     // fix -> repair
            48 -> 44     // battle -> fight
            51 -> 30     // inside -> in
            52 -> 31     // outside -> out
            54 -> 53     // goals -> goal
            else -> v
        }
    }

    private fun checkForErrors() {
        iverb = 0
        iobj = 0
        ipron = 0
        inoun = 0
        iconju = 0

        if (argc == 0) {
            myErr = 2
            return
        }

        var isverb = 0
        for (i in 1 until verbTable.size) {
            if (argv[0].equals(verbTable[i], ignoreCase = true)) {
                isverb = i
            }
        }

        isverb = checkVerbSynonym(isverb)

        // GO / MOVE modifier
        if (isverb == 49 || isverb == 50) {
            if (argc < 2) {
                myErr = 2
                return
            }
            argv = argv.drop(1)
            argc--
            isverb = 0
            for (i in 1 until verbTable.size) {
                if (argv[0].equals(verbTable[i], ignoreCase = true)) {
                    isverb = i
                }
            }
            isverb = checkVerbSynonym(isverb)
        }

        // Single-word action verbs (where / in / out / up / down / load / save / restore / goal / players / places)
        if (isverb == 23 || isverb == 30 || isverb == 31 || isverb == 32 || isverb == 33 || isverb == 14 || isverb == 19 || isverb == 53 || isverb == 55 || isverb == 56) {
            iverb = isverb
            argc = 1
            return
        }

        // Check for unrecognized words
        for (i in 0 until argc) {
            if (wordtype[i] == 0) {
                myErr = 1
                errpos = i
                return
            }
        }

        if (wordtype[0] != TYPE_VERB) {
            myErr = 3
            return
        }

        iverb = isverb

        // Verbs that must have no second word
        if (iverb < 5 || iverb == 13 || (iverb in 19..24) || (iverb in 30..38) || iverb == 53 || iverb == 55 || iverb == 56) {
            if (argc > 1) myErr = 3
            return
        }

        // Help command
        if (iverb == 5) { // LOOK
            if (argc == 1) return
        }

        // Resolve object
        for (i in 1..objects.size) {
            if (argc < 2) {
                myErr = 3
                return
            }
            if (argv[1].equals(objects[i]?.nickname, ignoreCase = true)) {
                iobj = i
                return
            }
        }

        // Resolve pronoun + object
        for (i in 1 until pronTable.size) {
            if (argc < 2) {
                myErr = 3
                return
            }
            if (argv[1].equals(pronTable[i], ignoreCase = true)) {
                ipron = i
                for (o in 1..objects.size) {
                    if (argc < 3) {
                        myErr = 3
                        return
                    }
                    if (argv[2].equals(objects[o]?.nickname, ignoreCase = true)) {
                        iobj = o
                        return
                    }
                }
                myErr = 3
                return
            }
        }

        // Resolve noun
        for (i in 1 until nounTable.size) {
            if (argc < 2) {
                myErr = 3
                return
            }
            if (argv[1].equals(nounTable[i], ignoreCase = true)) {
                inoun = i
                return
            }
        }
    }

    private fun executeCommand() {
        when (iverb) {
            1 -> move(_NORTH)
            2 -> move(_SOUTH)
            3 -> move(_EAST)
            4 -> move(_WEST)
            5 -> look()
            6 -> getObj()
            7 -> dropObj()
            8 -> dig()
            9 -> openDoor()
            10 -> closeContainer()
            12 -> readText()
            14 -> saveGame()
            15 -> startAction()
            16 -> turnAction()
            19 -> restoreGame()
            20, 21 -> printInstructions()
            22 -> printInventory()
            23 -> {
                argc = 1
                look()
            }
            25, 38 -> printScore(true)
            30 -> move(_IN)
            31 -> move(_OUT)
            32 -> move(_UP)
            33 -> move(_DOWN)
            34 -> move(_SW)
            35 -> move(_SE)
            36 -> move(_NW)
            37 -> move(_NE)
            25 -> printScore(true)
            53 -> printGoal()
            55 -> listPlayers()
            56 -> listPlaces()
            else -> myPrintf("Command not implemented yet.\n")
        }
    }

    private fun printError() {
        val cerr = arrayOf(
            "",
            "I don't understand the word",
            "What next ??",
            "Please be more specific.",
            "Oh really ?",
            "I can't see THAT thing anywhere!"
        )
        if (myErr == 1 && errpos < argv.size) {
            myPrintf("\n%s '%s'.\n", cerr[1], argv[errpos])
        } else if (myErr in 1..5) {
            myPrintf("\n%s\n", cerr[myErr])
        }
    }

    private fun move(dir: Int) {
        val p = places[myPosition] ?: return
        val dest = p.m[dir]
        if (dest < 0) {
            cantGo(true)
        } else if (dest == 0) {
            cantGo(false)
        } else {
            val oldPos = myPosition
            myPosition = dest
            broadcastToRoom(oldPos, "${getPlayer().handle} has left the room.\n")
            broadcastToRoom(dest, "${getPlayer().handle} has entered the room.\n")
            arrived()
            awardPlacePoints()
        }
    }

    private fun cantGo(locked: Boolean) {
        val p = places[myPosition]
        if (locked || !p?.message.isNullOrBlank()) {
            myPrintf("%s\n", p?.message)
        } else {
            myPrintf("You cannot go in that direction\n")
        }
    }

    private fun arrived() {
        val p = places[myPosition] ?: return
        var describe = true
        if (p.status == 1) { // Dark room
            if (!checkForLight()) {
                myPrintf("You are in the %s\n", p.name)
                if (p.altdescr.isNotBlank()) {
                    myPrintf("%s\n", p.altdescr)
                } else {
                    myPrintf("It is pitch dark here!\n")
                }
                describe = false
            }
        }

        if (describe) {
            describeRoom()
            if (placeVisited[myPosition] == 0) {
                placeVisited[myPosition] = 1
                myPrintf("\n%s\n", p.descr)
            }
        }
    }

    private fun awardPlacePoints() {
        val p = places[myPosition] ?: return
        val disc = p.points != 0 && placeVisited[myPosition] == 1 && myPlacePoints == 0 // simple tracking
        if (p.points != 0) {
            // Check if visited first time
            if (placeVisited[myPosition] == 1 && myPlacePoints < p.points) { // simplified disc
                myPlacePoints += p.points
                if (p.points == -255) {
                    printScore(false)
                    gameover = 1
                    myPrintf("\nGAME OVER. You have died!\n")
                }
            }
        }
    }

    private fun describeRoom() {
        val p = places[myPosition] ?: return
        if (p.status == 1 && !checkForLight()) {
            if (p.altdescr.isNotBlank()) {
                myPrintf("%s\n", p.altdescr)
            } else {
                myPrintf("It is pitch dark here!\n")
            }
            myPrintf("You are %s\n", p.name)
            return
        }

        myPrintf("You are %s\n", p.name)
        for (i in 1..objects.size) {
            if (objPosition[i] == myPosition) {
                myPrintf("  There is %s here.\n", objects[i]?.name)
            }
        }

        // List other players
        var first = true
        players.forEach { (idx, player) ->
            if (player.use == 1 && player.myPosition == myPosition && idx != activePlayerIdx) {
                if (first) {
                    myPrintf("You are not alone here.\n")
                    first = false
                }
                describePlayer(player)
            }
        }
    }

    private fun describePlayer(player: PlayerInstance) {
        val message = StringBuilder()
        if (player.infoGiven == 0) {
            message.append("There is an individual here. No description except there is a '${player.index}' tattooed in.\n")
        } else {
            message.append("${player.handle} is here. A ${player.gender}")
            if (player.description.isNotEmpty()) {
                message.append(", ${player.description}")
            }
            message.append(" is here. ")
        }

        // Inventory
        val carries = mutableListOf<String>()
        for (o in 1..objects.size) {
            if (objOwner[o] == player.index) {
                objects[o]?.nickname?.let { carries.add(it) }
            }
        }
        if (carries.isNotEmpty()) {
            val prefix = if (player.infoGiven == 0) "The individual is carrying " else "The ${player.gender} is carrying "
            message.append(prefix)
            if (carries.size == 1) {
                message.append("a ${carries[0]}")
            } else {
                message.append(carries.dropLast(1).joinToString(", ") { "a $it" })
                message.append(" and a ${carries.last()}")
            }
            message.append(". ")
        }

        if (player.online == 0) {
            message.append("${player.handle} (a ${player.gender}) is sleeping. ")
        }
        if (player.gameover == 1) {
            message.append("However, the ${player.gender}'s game is over. ")
        }

        myPrintf("%s\n", message.toString())
    }

    private fun checkForLight(): Boolean {
        if (numObjsCarried == 0) return false
        val p = places[myPosition] ?: return false
        val o = p.obj_dep
        if (o != 0 && objPosition[o] == 0 && objStatus[o] == 0) { // 0 = ON
            val obj = objects[o]
            if (obj != null && obj.turnable) {
                return true
            }
        }
        return false
    }

    private fun look() {
        if (argc == 1) {
            describeRoom()
            myPrintf("\n%s\n", places[myPosition]?.descr)
        } else {
            if (objPosition[iobj] == myPosition || objOwner[iobj] == activePlayerIdx) {
                myPrintf("%s\n", objects[iobj]?.description)
            } else {
                myPrintf("You can't see that here!\n")
            }
        }
    }

    private fun calcWeight(playerIndex: Int): Int {
        var weight = 0
        for (i in 1..objects.size) {
            if (objOwner[i] == playerIndex) {
                weight += objects[i]?.weight ?: 0
            }
        }
        return weight
    }

    private fun fight(ins: Int, p: Int, o: Int): Int {
        val myweight = calcWeight(ins)
        val otherweight = calcWeight(p)
        val obj = objects[o] ?: return -1

        if (p == ins) {
            return 0
        }

        val defender = players[p]
        if (defender == null || defender.gameover == 1 || defender.online == 0) {
            return 1
        }

        if (myweight == otherweight) {
            if (Random.nextInt(2) == 1) return 1
            return -1
        }

        if (myweight > otherweight) {
            val d = myweight - otherweight
            val i = Random.nextInt(5 * d)
            if (i == 1) { // lost even though odds were in favor
                val penalty = obj.pointsTaken + d * 5
                players[ins]?.myPoints = (players[ins]?.myPoints ?: 0) - penalty
                return -1
            }
            return 1
        }

        val d = otherweight - myweight
        val i = Random.nextInt(5 * d)

        if (i == 1) return 1 // won even though odds were against

        val penalty = obj.pointsTaken + d * 5
        players[ins]?.myPoints = (players[ins]?.myPoints ?: 0) - penalty
        return -1
    }

    private fun sendToPlayer(playerIndex: Int, message: String) {
        if (playerIndex == 1) {
            hostTerminalCallback?.onNewTerminalDataReceived(message)
        } else {
            sendToClient(playerIndex, message)
        }
    }

    private fun getObj() {
        val objDesired = iobj
        if (objPosition[objDesired] == 0 && objOwner[objDesired] == activePlayerIdx) {
            return
        }

        if (objPosition[objDesired] == myPosition) {
            val obj = objects[objDesired] ?: return
            if (!obj.moveable) {
                myPrintf("It refuses to be moved !\n")
                return
            }

            if (numObjsCarried < maximumObjectsCarried) {
                val od = obj.dependent
                val oi = obj.incompatible

                if (od != 0 && objPosition[od] != 0) {
                    myPrintf("You are not ready.\n")
                    return
                }
                if (oi != 0 && objPosition[oi] == 0) {
                    myPrintf("You are not ready.\n")
                    return
                }

                objOwner[objDesired] = activePlayerIdx
                objPosition[objDesired] = 0
                objTaken[objDesired] = 1
                myPoints += obj.pointsTaken
                numObjsCarried++

                myPrintf("Ok. You got it.\n")
                checkGoal(iverb, objDesired)
            } else {
                myPrintf("You are carrying too much.\n")
            }
            return
        }

        // check if another individual is here. Then check if he has the object;
        for ((p, otherPlayer) in players) {
            if (otherPlayer.use == 1 && otherPlayer.myPosition == myPosition && p != activePlayerIdx) {
                if (objOwner[objDesired] == p) {
                    val obj = objects[objDesired] ?: return
                    if (numObjsCarried >= maximumObjectsCarried) {
                        myPrintf("You are carrying too much.\n")
                        return
                    }

                    val od = obj.dependent
                    val oi = obj.incompatible

                    if (od != 0 && objPosition[od] != 0) {
                        myPrintf("You are not ready.\n")
                        return
                    }
                    if (oi != 0 && objPosition[oi] == 0) {
                        myPrintf("You are not ready.\n")
                        return
                    }

                    val f = fight(activePlayerIdx, p, objDesired)
                    val attacker = players[activePlayerIdx]
                    val defender = players[p]
                    val attName = attacker?.handle?.ifBlank { "a player" } ?: "a player"
                    val defName = defender?.handle?.ifBlank { "the player" } ?: "the player"
                    val attNameCap = attName.replaceFirstChar { it.uppercase() }
                    val defNameCap = defName.replaceFirstChar { it.uppercase() }

                    if (f == -1) {
                        sendToPlayer(activePlayerIdx, "\nYou could not get the ${obj.nickname}. $defNameCap defended from your attempt.\n")
                        sendToPlayer(p, "\nBe advised that $attName tried to take your ${obj.nickname}.\n")
                        return
                    } else if (f == 1) {
                        // award object to activePlayerIdx
                        objPosition[objDesired] = 0
                        objOwner[objDesired] = activePlayerIdx
                        objTaken[objDesired] = activePlayerIdx

                        val attackerPoints = (players[activePlayerIdx]?.myPoints ?: 0) + obj.pointsTaken
                        players[activePlayerIdx]?.myPoints = attackerPoints
                        val attackerCarrying = (players[activePlayerIdx]?.numObjsCarried ?: 0) + 1
                        players[activePlayerIdx]?.numObjsCarried = attackerCarrying

                        // de-award from p
                        val defenderPoints = (players[p]?.myPoints ?: 0) - (obj.pointsTaken + 5)
                        players[p]?.myPoints = defenderPoints
                        val defenderCarrying = (players[p]?.numObjsCarried ?: 0) - 1
                        players[p]?.numObjsCarried = defenderCarrying

                        sendToPlayer(activePlayerIdx, "\nYou took the ${obj.nickname} from $defName.\n")
                        sendToPlayer(p, "\n$attNameCap just took your ${obj.nickname}.\n")

                        checkGoal(iverb, objDesired)
                        return
                    }
                }
            }
        }

        myPrintf("You can't see that here.\n")
    }

    private fun dropObj() {
        if (objOwner[iobj] == activePlayerIdx) {
            myPrintf("Ok. You dropped it.\n")
            objOwner[iobj] = -1
            objPosition[iobj] = myPosition
            numObjsCarried--

            val obj = objects[iobj] ?: return
            if (myPosition == obj.locationToDrop) {
                mySavedPoints += obj.pointsSaved
            }
            checkGoal(iverb, iobj)
        } else {
            myPrintf("How can you drop something you don't even have ?\n")
        }
    }

    private fun dig() {
        val shovel = 5
        if (objPosition[shovel] != 0 && objOwner[shovel] != activePlayerIdx) {
            myPrintf("You start digging with your hands, but soon you end up with a cute sand-castle...but the sea leveled it off.\n")
            return
        }

        if (iobj != 13 && iobj != 18) { // 13 = sand (funhouse coin or island dig), 18 = grave
            myPrintf("What a stupid idea! You can't dig that man!\n")
            return
        }

        if (myPosition == 5 && gameNickName == "island") { // Forest (Lost Island key in forest path or Sandy Shore)
            if (sand < 3) {
                sand++
                if (sand == 3) {
                    myPrintf("You dig, and dig, and dig enough big hole to see a golden key in it.\n")
                    objPosition[2] = myPosition // Golden Key appears
                } else {
                    val sandtxt = arrayOf("", "You dig up a small hole in the sand.", "You dig and the hole in the sand becomes deeper.")
                    myPrintf("%s\n", sandtxt[sand])
                }
            } else {
                myPrintf("You can't dig more deep.\n")
            }
        } else if (myPosition == 16 && gameNickName == "funhouse") {
            myPrintf("As you approach to the grave to dig it, you can hear a demonic voice from the grave. You drop the shovel with fright.\n")
            objPosition[shovel] = myPosition
            numObjsCarried--
        } else {
            myPrintf("You dig up some small holes. Boring.\n")
        }
    }

    private fun openDoor() {
        var done = false
        val p = places[myPosition] ?: return
        var hasLockedExit = false
        for (d in 0 until 12) {
            if (p.m[d] < 0) hasLockedExit = true
        }

        if (!hasLockedExit) {
            myPrintf("This action has no meaning here.\n")
            return
        }

        if (numObjsCarried == 0) {
            myPrintf("You are not ready for this action.\n")
            return
        }

        for (i in 1..objects.size) {
            val obj = objects[i]
            if (objOwner[i] == activePlayerIdx && obj != null && obj.openable) {
                for (d in 0 until 12) {
                    if (p.m[d] < 0 && obj.locationToDrop == (-1 * p.m[d])) {
                        p.m[d] = p.m[d] * (-1)
                        done = true
                    }
                }
            }
        }

        if (done) {
            myPrintf("Done.\n")
        } else {
            myPrintf("You are not ready for this action.\n")
        }
    }

    private fun closeContainer() {
        myPrintf("How can you close that ?\n")
    }

    private fun readText() {
        for (i in 1..objects.size) {
            val obj = objects[i]
            if (obj != null && (objPosition[i] == myPosition || objOwner[i] == activePlayerIdx) && obj.readable && argv.getOrNull(1).equals(obj.nickname, ignoreCase = true)) {
                myPrintf("It reads\n'%s'\n", obj.text)
                return
            }
        }
        myPrintf("You can't read anything here.\n")
    }

    private fun startAction() {
        val objDesired = iobj
        val obj = objects[objDesired]
        if (obj != null && (objPosition[objDesired] == myPosition || objOwner[objDesired] == activePlayerIdx)) {
            if (!obj.startable) {
                myPrintf("You cannot do that to a %s.\n", obj.nickname)
            } else {
                checkGoal(iverb, objDesired)
            }
        } else {
            myPrintf("Where can you see that?\n")
        }
    }

    private fun turnAction() {
        val o = places[myPosition]?.obj_dep ?: 0
        if (o == 0 || objects[o]?.turnable != true) {
            myPrintf("I can't quite do this.\n")
            return
        }

        if (objOwner[o] != activePlayerIdx) {
            myPrintf("I can't quite do this.\n")
            return
        }

        var turnOn = true
        if (argc >= 3) {
            val op = argv[1].lowercase()
            val op2 = argv[2].lowercase()
            if (op == "off" || op2 == "off") turnOn = false
        } else {
            // Toggle or default ON
            if (objStatus[o] == 0) turnOn = false // If ON, turn OFF
        }

        if (turnOn) {
            objStatus[o] = 0 // ON
            myPrintf("OK.\n")
            describeRoom()
        } else {
            objStatus[o] = 1 // OFF
            myPrintf("OK.\n")
            describeRoom()
        }
    }

    private fun getGoalType(goal: Goal): Int {
        for (v in goal.verb) {
            if (v == 1) return 1 // start -> GOAL_LOCATION
            if (v == 2) return 2 // kill -> GOAL_PERSON
            if (v == 3 || v == 4) return 3 // get / drop -> GOAL_OBJECT
        }
        return 3 // fallback to GOAL_OBJECT
    }

    private fun checkGoal(vrb: Int, obj: Int) {
        val vrbGoal = when (vrb) {
            15 -> 1 // start
            6 -> 3  // get
            7 -> 4  // drop
            else -> 0
        }
        if (vrbGoal == 0) return

        val mg = goalIndex
        if (mg < 1 || mg >= goals.size) return
        val activeGoal = goals[mg]
        val activeGoalType = getGoalType(activeGoal)

        var matchedGoalIndex = -1
        
        when (activeGoalType) {
            1 -> { // GOAL_LOCATION
                // Find a goal g where goal.location == myPosition
                for (i in 1 until goals.size) {
                    if (goals[i].location == myPosition) {
                        matchedGoalIndex = i
                        break
                    }
                }
            }
            3 -> { // GOAL_OBJECT
                // Find a goal g where goal.location == obj
                for (i in 1 until goals.size) {
                    if (goals[i].location == obj) {
                        matchedGoalIndex = i
                        break
                    }
                }
            }
        }

        if (matchedGoalIndex == -1) {
            // No goal associated with this location or object
            return
        }

        val targetGoal = goals[matchedGoalIndex]
        val mygoal = (matchedGoalIndex == mg)

        var metd = true

        // Check dependencies
        var td = 0
        var cd = 0
        for (d in 0 until 16) {
            if (targetGoal.depend[d] != 0) td++
        }
        for (d in 0 until td) {
            val depObj = targetGoal.depend[d]
            if (objOwner[depObj] == activePlayerIdx) cd++
        }
        if (cd < td) metd = false

        // Check incompatibilities
        if (metd) {
            var ti = 0
            var ci = 0
            for (d in 0 until 16) {
                if (targetGoal.incomp[d] != 0) ti++
            }
            for (d in 0 until ti) {
                val incompObj = targetGoal.incomp[d]
                if (objOwner[incompObj] == activePlayerIdx) ci++
            }
            if (ci > 0) metd = false
        }

        // Check location dependency
        if (metd && getGoalType(targetGoal) == 3) { // GOAL_OBJECT check
            val loc = targetGoal.locDep
            if (loc != 0) {
                if (loc < 0) {
                    if ((-1 * loc) == myPosition) metd = false
                } else {
                    if (loc != myPosition) metd = false
                }
            }
            
            // Check verb matching
            var verbMatched = false
            for (v in 0 until 16) {
                if (targetGoal.verb[v] == vrbGoal) verbMatched = true
            }
            if (!verbMatched) metd = false
        }

        if (!metd) {
            if (targetGoal.errorText.isNotBlank()) {
                myPrintf("%s\n", targetGoal.errorText)
            }
            return
        }

        // Goal Met!
        if (!mygoal) {
            // not my goal
            myPrintf("%s\n", targetGoal.noOwnerText)
            myPoints += targetGoal.pointsNoOwner
        } else {
            // my goal
            myPrintf("%s\n", targetGoal.descr)
            myPoints += targetGoal.pointsOwner
        }
        printScore(false)

        if (targetGoal.finish == 1) {
            gameover = 1
            myPrintf("\nCONGRATULATIONS!!! You have completed the game successfully!\n")
        }
    }

    private fun printInventory() {
        myPrintf("You have :\n")
        if (numObjsCarried == 0) {
            myPrintf("Nothing...Except your sanity...\n")
        } else {
            var c = 0
            for (i in 1..objects.size) {
                if (objOwner[i] == activePlayerIdx) {
                    c++
                    myPrintf("%s\n", objects[i]?.name)
                }
            }
            numObjsCarried = c
        }
    }

    private fun printGoal() {
        myPrintf("Your goal is to %s\n", goals[goalIndex].name)
    }

    private fun listPlayers() {
        myPrintf("Current players:\n")
        players.values.sortedBy { it.index }.forEach { p ->
            val status = if (p.online == 1) "Online" else "Sleeping"
            if (p.index == activePlayerIdx) {
                val roomName = places[p.myPosition]?.name ?: "Unknown"
                myPrintf("- %s (you, %s) - Location: %s - Score: %d\n", p.handle, status, roomName, p.myPoints)
            } else {
                myPrintf("- %s (%s)\n", p.handle, status)
            }
        }
    }

    private fun listPlaces() {
        myPrintf("Available locations:\n")
        places.values.sortedBy { it.num }.forEach { p ->
            if (p.name.isNotBlank()) {
                myPrintf("- %s\n", p.name)
            }
        }
    }

    private fun printScore(silent: Boolean) {
        val totalScore = myPoints + mySavedPoints + myPlacePoints - numMovements
        myPrintf("You have accumulated %d points from objects you collected.\n", myPoints)
        myPrintf("You have accumulated %d points from objects you saved.\n", mySavedPoints)
        myPrintf("You have accumulated %d points from places you discovered.\n", myPlacePoints)
        myPrintf("You have lost %d points for the movements you made.\n", numMovements)
        myPrintf("Your score is %d.\n", totalScore)
    }

    private fun startNewGame() {
        com.funhouse.shared.common.utils.saveTextToFile("$gameFolder/${saveFile}.gameengsav", "")
        
        myPosition = gameStarts[Random.nextInt(gameStarts.size)]
        goalIndex = Random.nextInt(totalGoals) + 1
        myPoints = 0
        mySavedPoints = 0
        myPlacePoints = 0
        numMovements = 0
        numObjsCarried = 0
        gameover = 0
        helped = 0
        sand = 0
        placeVisited.fill(0)
        placeVisited[myPosition] = 1

        objects.values.forEach {
            objPosition[it.num] = it.location
            objStatus[it.num] = it.status
            objOwner[it.num] = -1
            objTaken[it.num] = 0
        }

        myPrintf("\nNew game started!\n")
        describeRoom()
    }

    private fun saveGame(silent: Boolean = false) {
        val filename = if (argv.size > 1) "${saveFile}_${argv[1]}.json" else "${saveFile}.json"
        try {
            val state = GameSaveState(
                objPosition = objPosition.toList(),
                objOwner = objOwner.toList(),
                objStatus = objStatus.toList(),
                objTaken = objTaken.toList(),
                placesStatus = places.mapValues { it.value.status },
                placesDisc = places.mapValues { it.value.disc },
                placesM = places.mapValues { it.value.m.toList() },
                players = players.values.toList()
            )
            com.funhouse.shared.common.utils.saveTextToFile("$gameFolder/$filename", Json.encodeToString(state))
            if (!silent) {
                myPrintf("Game saved successfully (JSON).\n")
            }
        } catch (e: Exception) {
            GcLog.e("Failed to save JSON game", e)
            if (!silent) {
                myPrintf("Error saving game.\n")
            }
        }
    }

    private fun restoreGame(silent: Boolean = false): Boolean {
        val filename = if (argv.size > 1) "${saveFile}_${argv[1]}.json" else "${saveFile}.json"
        val savedText = com.funhouse.shared.common.utils.readTextFromFile("$gameFolder/$filename")
        if (savedText == null) {
            val legacyFilename = if (argv.size > 1) "${saveFile}_${argv[1]}.gameengsav" else "${saveFile}.gameengsav"
            val legacyText = com.funhouse.shared.common.utils.readTextFromFile("$gameFolder/$legacyFilename")
            if (legacyText != null) {
                return restoreLegacyGame(legacyText, silent)
            }
            if (!silent) {
                myPrintf("No saved game file found.\n")
            }
            return false
        }

        try {
            val state = Json.decodeFromString<GameSaveState>(savedText)
            for (i in 0 until minOf(state.objPosition.size, objPosition.size)) {
                objPosition[i] = state.objPosition[i]
                objOwner[i] = state.objOwner[i]
                objStatus[i] = state.objStatus[i]
                objTaken[i] = state.objTaken[i]
            }
            state.placesStatus.forEach { (num, status) -> places[num]?.status = status }
            state.placesDisc.forEach { (num, disc) -> places[num]?.disc = disc }
            state.placesM.forEach { (num, exits) ->
                val p = places[num]
                if (p != null) {
                    for (d in 0 until minOf(exits.size, p.m.size)) {
                        p.m[d] = exits[d]
                    }
                }
            }
            players.clear()
            state.players.forEach { players[it.index] = it }

            if (!silent) {
                myPrintf("Game restored successfully.\n")
                describeRoom()
            }
            return true
        } catch (e: Exception) {
            GcLog.e("Failed to restore JSON game", e)
            if (!silent) {
                myPrintf("Error restoring game.\n")
            }
            return false
        }
    }

    private fun restoreLegacyGame(content: String, silent: Boolean): Boolean {
        try {
            content.replace("\r\n", "\n").split("\n").forEach { line ->
                val tokens = line.split("=")
                if (tokens.size >= 2) {
                    val key = tokens[0].trim()
                    val valStr = tokens[1].trim()
                    when (key) {
                        "position" -> myPosition = valStr.toIntOrNull() ?: 1
                        "points" -> myPoints = valStr.toIntOrNull() ?: 0
                        "saved" -> mySavedPoints = valStr.toIntOrNull() ?: 0
                        "places" -> myPlacePoints = valStr.toIntOrNull() ?: 0
                        "movements" -> numMovements = valStr.toIntOrNull() ?: 0
                        "carried" -> numObjsCarried = valStr.toIntOrNull() ?: 0
                        "gameover" -> gameover = valStr.toIntOrNull() ?: 0
                        "helped" -> helped = valStr.toIntOrNull() ?: 0
                        "goal" -> goalIndex = valStr.toIntOrNull() ?: 1
                        "alertlost" -> alertedLost = valStr.toIntOrNull() ?: 0
                        "plcvisit" -> {
                            val parts = valStr.split(" ")
                            if (parts.size >= 2) {
                                val idx = parts[0].toIntOrNull() ?: 0
                                val v = parts[1].toIntOrNull() ?: 0
                                if (idx in 1 until placeVisited.size) {
                                    placeVisited[idx] = v
                                }
                            }
                        }
                        "object" -> {
                            val parts = valStr.split(" ")
                            if (parts.size >= 5) {
                                val idx = parts[0].toIntOrNull() ?: 0
                                if (idx in 1..objects.size) {
                                    objPosition[idx] = parts[1].toIntOrNull() ?: 0
                                    objStatus[idx] = parts[2].toIntOrNull() ?: 0
                                    objTaken[idx] = parts[3].toIntOrNull() ?: 0
                                    objOwner[idx] = parts[4].toIntOrNull() ?: -1
                                }
                            }
                        }
                        "plcstat" -> {
                            val parts = valStr.split(" ")
                            if (parts.size >= 3) {
                                val idx = parts[0].toIntOrNull() ?: 0
                                val status = parts[1].toIntOrNull() ?: 0
                                val disc = parts[2].toIntOrNull() ?: 0
                                val p = places[idx]
                                if (p != null) {
                                    p.status = status
                                    p.disc = disc
                                }
                            }
                        }
                        "plcmov" -> {
                            val parts = valStr.split(" ")
                            if (parts.size >= 3) {
                                val idx = parts[0].toIntOrNull() ?: 0
                                val dir = parts[1].toIntOrNull() ?: 0
                                val dest = parts[2].toIntOrNull() ?: 0
                                val p = places[idx]
                                if (p != null && dir in 0 until 12) {
                                    p.m[dir] = dest
                                }
                            }
                        }
                    }
                }
            }
            if (!silent) {
                myPrintf("Game restored successfully (Legacy).\n")
                describeRoom()
            }
            return true
        } catch (e: Exception) {
            GcLog.e("Failed to restore legacy game", e)
            return false
        }
    }
}

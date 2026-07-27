package com.funhouse.wander_engine

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.TerminalDataCallback
import jni.WanderKotlin
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class WanderEngineTest {

    @BeforeTest
    fun setUp() {
        AppData.packageFolder = "../shared/common/src/commonMain/composeResources/files"
    }

    @Test
    fun testWanderAldebaranInitialization() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Test About",
            gameNickName = "wandera3",
            callbackParam = callback
        )
        engine.start("wandera3")
        assertTrue(receivedOutput.contains("Welcome to a Wander adventure game!"), "Output should contain wander greetings")

        receivedOutput = ""
        engine.sendCommand("about")
        assertTrue(receivedOutput.contains("Test About"), "Output should contain about text")
    }

    @Test
    fun testWanderCastleInitialization() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Wander Castle Test",
            gameNickName = "wandercastle",
            callbackParam = callback
        )
        engine.start("wandercastle")
        assertTrue(receivedOutput.contains("Welcome to a Wander adventure game!"), "Output should contain wander greetings")

        receivedOutput = ""
        engine.sendCommand("look")
        assertTrue(receivedOutput.isNotEmpty(), "Output should not be empty after look command")
    }

    @Test
    fun testWanderLibraryInitialization() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Wander Library Test",
            gameNickName = "wanderlibrary",
            callbackParam = callback
        )
        engine.start("wanderlibrary")
        assertTrue(receivedOutput.contains("Welcome to a Wander adventure game!"), "Output should contain wander greetings")
    }

    @Test
    fun testWanderLogicalOpsInitialization() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Wander Logic Test",
            gameNickName = "wandertut",
            callbackParam = callback
        )
        engine.start("wandertut")
        assertTrue(receivedOutput.contains("Welcome to a Wander adventure game!"), "Output should contain wander greetings")
    }

    @Test
    fun testWanderAldebaranBaseline() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Test About",
            gameNickName = "wandera3",
            callbackParam = callback
        )
        
        // Start the game
        engine.start("wandera3")
        val startOutput = receivedOutput
        receivedOutput = ""

        // 1. Send look
        engine.sendCommand("look")
        val look1Output = receivedOutput
        receivedOutput = ""

        // 2. Send take credit card
        engine.sendCommand("take credit card")
        val takeCardOutput = receivedOutput
        receivedOutput = ""

        // 3. Send south
        engine.sendCommand("south")
        val southOutput = receivedOutput
        receivedOutput = ""

        // 4. Send look
        engine.sendCommand("look")
        val look2Output = receivedOutput
        receivedOutput = ""

        // 5. Send insert credit card
        engine.sendCommand("insert credit card")
        val insertCardOutput = receivedOutput
        receivedOutput = ""

        // Dump outputs to see what is failing
        println("=== START OUTPUT ===")
        println(startOutput)
        println("=== LOOK 1 OUTPUT ===")
        println(look1Output)
        println("=== TAKE CARD OUTPUT ===")
        println(takeCardOutput)
        println("=== SOUTH OUTPUT ===")
        println(southOutput)
        println("=== LOOK 2 OUTPUT ===")
        println(look2Output)
        println("=== INSERT CARD OUTPUT ===")
        println(insertCardOutput)

        // Assert baseline expectations:
        assertTrue(startOutput.contains("You have been sent to Aldebaran III"), "Start output should contain the intro text")
        assertTrue(startOutput.contains("You're in the Aldebaran III spaceport"), "Start output should contain the location description")
        assertTrue(startOutput.contains("There is a credit card here"), "Start output should notice the credit card")

        assertTrue(look1Output.contains("You're in the Aldebaran III spaceport"), "Look 1 output should contain the location description")

        assertTrue(takeCardOutput.contains("Done"), "Take card output should say Done")
        assertTrue(takeCardOutput.contains("credits left"), "Take card output should mention credits")

        assertTrue(southOutput.contains("You are in the tiny waiting room for the spaceport"), "South output should describe the waiting room")

        assertTrue(look2Output.contains("You are in the tiny waiting room for the spaceport"), "Look 2 output should describe the waiting room")

        assertTrue(insertCardOutput.contains("The machine lights up"), "Insert card output should light up the machine")
        assertTrue(insertCardOutput.contains("blue button --- \"Cigarettes"), "Insert card output should describe the lit buttons")
    }

    @Test
    fun testWanderLogicOpsBaseline() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Test About",
            gameNickName = "wandertut",
            callbackParam = callback
        )
        
        engine.start("wandertut")
        val startOutput = receivedOutput
        receivedOutput = ""

        engine.sendCommand("110")
        val r1Output = receivedOutput
        receivedOutput = ""

        engine.sendCommand("1")
        val r2Output = receivedOutput
        receivedOutput = ""

        engine.sendCommand("100")
        val r3Output = receivedOutput
        receivedOutput = ""

        // Dump outputs to see what is failing
        println("=== LOGIC OPS START OUTPUT ===")
        println(startOutput)
        println("=== R1 OUTPUT ===")
        println(r1Output)
        println("=== R2 OUTPUT ===")
        println(r2Output)
        println("=== R3 OUTPUT ===")
        println(r3Output)

        assertTrue(startOutput.contains("You are about to play with, (learn about), logical bit operations."), "Start output should contain intro")
        assertTrue(startOutput.contains("The next DECIMAL number is \"6\", what would the next BINARY number be?"), "Start output should contain decimal question")

        assertTrue(r1Output.contains("Right on!"), "R1 output should contain right on")
        assertTrue(r1Output.contains("What would 1 & 1 be?"), "R1 output should contain AND question")

        assertTrue(r2Output.contains("You got it!"), "R2 output should contain you got it")
        assertTrue(r2Output.contains("What would you get if you ANDed together 110 and 101?"), "R2 output should contain AND bigger numbers question")

        assertTrue(r3Output.contains("Great!  You got it."), "R3 output should contain great you got it")
        assertTrue(r3Output.contains("What does 1 | 0 = ?"), "R3 output should contain OR question")
    }

    @Test
    fun testWanderLibraryBaseline() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Test About",
            gameNickName = "wanderlibrary",
            callbackParam = callback
        )
        
        engine.start("wanderlibrary")
        val startOutput = receivedOutput
        receivedOutput = ""

        engine.sendCommand("take sack")
        val takeSackOutput = receivedOutput
        receivedOutput = ""

        engine.sendCommand("up")
        val upOutput = receivedOutput
        receivedOutput = ""

        engine.sendCommand("inventory")
        val invOutput = receivedOutput
        receivedOutput = ""

        println("=== LIBRARY START OUTPUT ===")
        println(startOutput)
        println("=== TAKE SACK OUTPUT ===")
        println(takeSackOutput)
        println("=== UP OUTPUT ===")
        println(upOutput)
        println("=== INVENTORY OUTPUT ===")
        println(invOutput)

        assertTrue(startOutput.contains("Through the wonder of Wander, you are going to explore"), "Start output should contain library intro")
        assertTrue(startOutput.contains("There is a leather sack here."), "Start output should contain leather sack")

        assertTrue(takeSackOutput.contains("Done"), "Take sack output should say Done")

        assertTrue(upOutput.contains("You climb up the steps and into the building."), "Up output should contain climbing text")
        assertTrue(upOutput.contains("You're inside a large vaulted lobby."), "Up output should describe the lobby")

        assertTrue(invOutput.contains("You are carrying a leather sack"), "Inventory output should list sack")
    }

    @Test
    fun testWanderCastleBaseline() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = WanderKotlin(
            aboutText = "Test About",
            gameNickName = "wandercastle",
            callbackParam = callback
        )
        
        engine.start("wandercastle")
        val startOutput = receivedOutput
        receivedOutput = ""

        engine.sendCommand("north")
        val north1Output = receivedOutput
        receivedOutput = ""

        engine.sendCommand("north")
        val north2Output = receivedOutput
        receivedOutput = ""

        engine.sendCommand("look")
        val lookOutput = receivedOutput
        receivedOutput = ""

        engine.sendCommand("west")
        val westOutput = receivedOutput
        receivedOutput = ""

        println("=== CASTLE START OUTPUT ===")
        println(startOutput)
        println("=== NORTH 1 OUTPUT ===")
        println(north1Output)
        println("=== NORTH 2 OUTPUT ===")
        println(north2Output)
        println("=== LOOK OUTPUT ===")
        println(lookOutput)
        println("=== WEST OUTPUT ===")
        println(westOutput)

        assertTrue(startOutput.contains("Seated late one evening in front of the television"), "Start output should contain intro")
        assertTrue(startOutput.contains("You're at a crossroads in a shallow valley"), "Start output should contain crossroads")

        assertTrue(north1Output.contains("You're on a sandy beach bordering a large lake"), "North 1 output should contain beach")

        assertTrue(north2Output.contains("You swim out a little ways, get tired, and swim back."), "North 2 output should contain swim out")
        assertTrue(north2Output.contains("Sandy beach south of Swan Lake"), "North 2 output should contain beach name")

        assertTrue(lookOutput.contains("You're on a sandy beach bordering a large lake"), "Look output should contain beach desc")

        assertTrue(westOutput.contains("You're on the bank of a raging river flowing north."), "West output should contain river bank")
        assertTrue(westOutput.contains("There is a boat here."), "West output should notice boat")
    }
}

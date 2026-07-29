package com.funhouse.funhouse_engine

import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.TerminalDataCallback
import jni.GengameKotlin
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class FunHouseEngineTest {

    @BeforeTest
    fun setUp() {
        AppData.packageFolder = "../shared/common/src/commonMain/composeResources/files"
    }

    private fun waitForOutput(condition: () -> Boolean, timeoutMs: Long = 2000) {
        val start = java.lang.System.currentTimeMillis()
        while (!condition()) {
            if (java.lang.System.currentTimeMillis() - start > timeoutMs) {
                break
            }
            java.lang.Thread.sleep(50)
        }
    }

    @Test
    fun testHelpCommand() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = GengameKotlin()
        engine.isUnitTest = true
        engine.registerTerminalCallback(callback)
        engine.start("funhouse", isMultiplayer = false)

        // Wait for instructions prompt
        waitForOutput({ receivedOutput.contains("Would you like instructions?") })
        
        // Answer "no" to bypass instructions prompt
        receivedOutput = ""
        engine.sendCommand("no")
        
        // Wait for room description
        waitForOutput({ receivedOutput.contains("You are") })
        
        // Now send the actual "help" command
        receivedOutput = ""
        engine.sendCommand("help")
        
        // Wait for help response to be fully printed
        waitForOutput({ receivedOutput.contains("PLACES") })

        println("Help Output: $receivedOutput")
        assertTrue(receivedOutput.contains("Available commands:"), "Help output should contain available commands label")
        assertTrue(receivedOutput.contains("PLACES"), "Help output should list the PLACES command")
        assertTrue(receivedOutput.contains("HELP"), "Help output should list the HELP command")
        engine.stopNetwork()
    }

    @Test
    fun testPlacesCommand() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine = GengameKotlin()
        engine.isUnitTest = true
        engine.registerTerminalCallback(callback)
        engine.start("funhouse", isMultiplayer = false)

        // Wait for instructions prompt
        waitForOutput({ receivedOutput.contains("Would you like instructions?") })
        
        // Answer "no" to bypass instructions prompt
        receivedOutput = ""
        engine.sendCommand("no")
        
        // Wait for room description
        waitForOutput({ receivedOutput.contains("You are") })

        // Send "places" command
        receivedOutput = ""
        engine.sendCommand("places")
        
        // Wait for places response to be fully printed
        waitForOutput({ receivedOutput.contains("Makeshift Klown Kart Racing Chamber") })

        println("Places Output: $receivedOutput")
        assertTrue(receivedOutput.contains("Available locations:"), "Places output should contain available locations label")
        assertTrue(receivedOutput.contains("Hall of Optical Illusions"), "Places output should list Hall of Optical Illusions")
        assertTrue(receivedOutput.contains("Makeshift Klown Kart Racing Chamber"), "Places output should list Makeshift Klown Kart Racing Chamber")
        engine.stopNetwork()
    }

    @Test
    fun testSaveAndRestoreMultiplayer() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine1 = GengameKotlin()
        engine1.isUnitTest = true
        engine1.networkPort = 8093
        engine1.registerTerminalCallback(callback)
        engine1.start("funhouse", isMultiplayer = true)

        // Wait for instructions prompt
        waitForOutput({ receivedOutput.contains("Would you like instructions?") })
        engine1.sendCommand("no")
        waitForOutput({ receivedOutput.contains("You are") })

        // Manually inject a second player into engine1's players map
        val player2 = com.funhouse.feature.funhouseenginekotlin.models.PlayerInstance(
            index = 2,
            use = 1,
            online = 1,
            handle = "Player2",
            gender = "Wanderer",
            description = "A helper player",
            myPosition = 2
        )
        engine1.players[2] = player2

        // Send save command
        receivedOutput = ""
        engine1.sendCommand("save")
        waitForOutput({ receivedOutput.contains("Game saved successfully") })
        println("Save Output: $receivedOutput")
        assertTrue(receivedOutput.contains("Game saved successfully"), "Save should succeed")

        // Stop engine1
        engine1.stopNetwork()
        // Wait a brief moment for port to release
        java.lang.Thread.sleep(1000)

        // Create engine2
        val engine2 = GengameKotlin()
        engine2.isUnitTest = true
        engine2.networkPort = 8093
        var restoreOutput = ""
        engine2.registerTerminalCallback(object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                restoreOutput += data
            }
        })
        engine2.start("funhouse", isMultiplayer = true)

        // Wait for instructions prompt
        waitForOutput({ restoreOutput.contains("Would you like instructions?") })
        restoreOutput = ""
        engine2.sendCommand("no")
        waitForOutput({ restoreOutput.contains("You are") })

        // Send restore command
        restoreOutput = ""
        engine2.sendCommand("restore")
        waitForOutput({ restoreOutput.contains("Game restored successfully") || restoreOutput.contains("Error restoring") })
        println("Restore Output: $restoreOutput")

        assertTrue(restoreOutput.contains("Game restored successfully"), "Restore should succeed")
        assertTrue(engine2.players.containsKey(2), "Player 2 should be restored")
        engine2.stopNetwork()

        // Test restore of a non-existent backup file to check error output
        var nonExistentOutput = ""
        val engine3 = GengameKotlin()
        engine3.isUnitTest = true
        engine3.registerTerminalCallback(object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                nonExistentOutput += data
            }
        })
        engine3.start("funhouse", isMultiplayer = false)
        waitForOutput({ nonExistentOutput.contains("Would you like instructions?") })
        nonExistentOutput = ""
        engine3.sendCommand("no")
        waitForOutput({ nonExistentOutput.contains("You are") })

        nonExistentOutput = ""
        engine3.sendCommand("restore nonexistent")
        waitForOutput({ nonExistentOutput.contains("Error restoring game") })
        println("Non-existent Restore Output: $nonExistentOutput")
        assertTrue(nonExistentOutput.contains("Error restoring game"), "Error restoring game should be printed")
        assertTrue(nonExistentOutput.contains("No saved game file found"), "No saved game file found should be printed")
        engine3.stopNetwork()
    }

    @Test
    fun testSaveAndRestoreSingleplayer() {
        var receivedOutput = ""
        val callback = object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                receivedOutput += data
            }
        }
        val engine1 = GengameKotlin()
        engine1.isUnitTest = true
        engine1.registerTerminalCallback(callback)
        engine1.start("funhouse", isMultiplayer = false)

        // Wait for instructions prompt
        waitForOutput({ receivedOutput.contains("Would you like instructions?") })
        engine1.sendCommand("no")
        waitForOutput({ receivedOutput.contains("You are") })

        // Send save command
        receivedOutput = ""
        engine1.sendCommand("save")
        waitForOutput({ receivedOutput.contains("Game saved successfully") })
        println("Save Output: $receivedOutput")
        assertTrue(receivedOutput.contains("Game saved successfully"), "Save should succeed")
        engine1.stopNetwork()

        // Create engine2
        val engine2 = GengameKotlin()
        engine2.isUnitTest = true
        var restoreOutput = ""
        engine2.registerTerminalCallback(object : TerminalDataCallback {
            override fun onNewTerminalDataReceived(data: String) {
                restoreOutput += data
            }
        })
        engine2.start("funhouse", isMultiplayer = false)

        // Wait for instructions prompt
        waitForOutput({ restoreOutput.contains("Would you like instructions?") })
        restoreOutput = ""
        engine2.sendCommand("no")
        waitForOutput({ restoreOutput.contains("You are") })

        // Send restore command
        restoreOutput = ""
        engine2.sendCommand("restore")
        waitForOutput({ restoreOutput.contains("Game restored successfully") || restoreOutput.contains("Error restoring") })
        println("Restore Output: $restoreOutput")

        assertTrue(restoreOutput.contains("Game restored successfully"), "Restore should succeed")
        engine2.stopNetwork()
    }
}


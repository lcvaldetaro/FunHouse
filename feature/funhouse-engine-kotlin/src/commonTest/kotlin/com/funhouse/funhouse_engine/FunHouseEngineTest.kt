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
        
        // Wait for help response
        waitForOutput({ receivedOutput.contains("Available commands:") })

        println("Help Output: $receivedOutput")
        assertTrue(receivedOutput.contains("Available commands:"), "Help output should contain available commands label")
        assertTrue(receivedOutput.contains("PLACES"), "Help output should list the PLACES command")
        assertTrue(receivedOutput.contains("HELP"), "Help output should list the HELP command")
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
        
        // Wait for places response
        waitForOutput({ receivedOutput.contains("Available locations:") })

        println("Places Output: $receivedOutput")
        assertTrue(receivedOutput.contains("Available locations:"), "Places output should contain available locations label")
        assertTrue(receivedOutput.contains("Hall of Optical Illusions"), "Places output should list Hall of Optical Illusions")
        assertTrue(receivedOutput.contains("Makeshift Klown Kart Racing Chamber"), "Places output should list Makeshift Klown Kart Racing Chamber")
    }
}

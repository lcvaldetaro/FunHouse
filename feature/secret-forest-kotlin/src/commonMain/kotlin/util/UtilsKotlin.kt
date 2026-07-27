package secretforestkotlin.utils


import com.funhouse.shared.common.models.Direction
import com.funhouse.shared.common.models.DownloadableFile
import com.funhouse.shared.common.models.Game
import com.funhouse.shared.common.models.LocalGame
import com.funhouse.shared.common.utils.installFile

fun installFiles() {
    try {
        installFile("secretforest.md")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val defaultAbout = """
Copyright (c) 2018 Easter <ethinethin@gmail.com>
 Converted to Kotlin by Valdetaro Consulting, LLC in 2026.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
   may be used to endorse or promote products derived from this software without
   specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 """.trimIndent()

val defaultForestDirections: List<Direction> =
    listOf(
        Direction("N", "north"),
        Direction("S", "south"),
        Direction("W", "west"),
        Direction("E", "east"),
        Direction("NE", "ne"),
        Direction("NW", "nw"),
        Direction("SW", "sw"),
        Direction("SE", "se"),
        Direction("Yes", "yes"),
        Direction("No", "no"),
        Direction("Up", "up"),
        Direction("Down", "down"),
        Direction("In", "in"),
        Direction("Out", "out"),
        Direction("Look", "look"),
        Direction("Inv", "inventory"),
        Direction("Help", "help"),
        Direction("about", "about"),
    )


val secretForestGame = Game(
    nickName = "secretforest",
    echo = false,
    composableTextGame = true,
    gameClass = jni.SecretForestKotlin(),
    library = "",
    title = "Secret in the Forest",
    menuTitle = "Forest",
    version = "1.0",
    directions = defaultForestDirections,
    helpFile = DownloadableFile("secretforest.md"),
    about = defaultAbout,
    directionColumns = 6,
    localGame = LocalGame(
        icon = 0,
        image = 0,
    )
)

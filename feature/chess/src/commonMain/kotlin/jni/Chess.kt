package jni

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.encodeToString
import com.funhouse.shared.common.AppData
import com.funhouse.shared.common.jni.*
import com.funhouse.shared.common.models.currentSettings
import club.gepetto.GcLog
import com.funhouse.shared.common.AppData.gameFolder
import java.io.File

class Chess: BaseKotlinGame() {
    init {
        if (packageFolder.isNotEmpty()) {
            val folder = File(packageFolder, gameFolder)
            folder.mkdir()
        }
    }

     companion object {
        private val packageFolder = AppData.packageFolder
        private val key = if (currentSettings.currentGame != null) currentSettings.currentGame!!.nickName else "chess"
        private const val MOVELOGTAG = "-movelog"

        fun saveGame(chessBoard: List<List<ChessPiece?>>, currentPlayer: ChessPlayer, moveLog: List<String>) {
            var stringMap = "${encodeToString(currentPlayer)}\n"

            chessBoard.forEach{ row ->
                var r = 0
                row.forEach { piece ->
                    val node = if (piece != null) encodeToString(piece) else "null"
                    if (r < 7)
                        stringMap = "$stringMap$node|"
                    else
                        stringMap = "$stringMap$node"
                    r++
                }
                stringMap = "$stringMap\n"
            }

            updateStringMap(key, stringMap)

            stringMap = ""
            moveLog.forEach { stringMap = "${it}\n" }
            updateStringMap("${key}$MOVELOGTAG", stringMap)
        }

        fun saveResult(againstGepetto: Boolean, depth: Int, gameStatus: ChessGameStatus, gamePlayer: ChessPlayer) {
            val result = when {
                againstGepetto && gameStatus == ChessGameStatus.CHECKMATE && gamePlayer == ChessPlayer.BLACK -> 1 // Won
                againstGepetto && gameStatus == ChessGameStatus.CHECKMATE && gamePlayer == ChessPlayer.WHITE -> 2 // Lost
                againstGepetto && gameStatus == ChessGameStatus.STALEMATE -> 3 // Tied
                else -> 0 // do not compute wallet
            }

            if (result > 0) {
                val oldValue = getWalletValue(key)

                val gameFinishedValue = when(result) {
                    1 -> if (depth == 0) 10f else if (depth == 3) 1000f else depth * 5000f // won
                    2 -> if (depth == 0) -100f else if (depth == 3) -10f else depth * -2f // lost
                    3 -> if (depth == 0) 0f else if (depth == 3) 500f else depth * 2500f // tied
                    else -> 0f
                }

                updateWallet(key, "${oldValue + gameFinishedValue}")
            }
            // reset game
            saveGame(chessInitialBoard, ChessPlayer.WHITE, emptyList())
        }

        fun restoreBoard() : List<List<ChessPiece?>> {
            try {
                val stringMap = getStringMap(key)
                GcLog.d("restoreBoard = $stringMap")
                val rows = stringMap.split("\n")
                val board: MutableList<List<ChessPiece?>> = mutableListOf()

                var r = 0
                rows.forEachIndexed { index, row ->
                    if (index > 0 && row.isNotEmpty()) {
                        val word = row.split("|")
                        val rowData: MutableList<ChessPiece?> = mutableListOf()
                        for (c in 0..7) {
                            GcLog.d("Processing $r:$c '${word[c]}'")
                            val chessPiece: ChessPiece? =
                                if (word[c] == "null") null else Json.decodeFromString<ChessPiece>(
                                    word[c]
                                )
                            rowData.add(chessPiece)
                        }
                        board.add(rowData)
                        r++
                    }
                }

                return board
            }
            catch (e: Exception) {
                e.printStackTrace()
                return chessInitialBoard
            }
        }

        fun restoreCurrentPlayer() : ChessPlayer? {
            try {
                val stringMap = getStringMap(key)
                GcLog.d("restoreCurrentPlayer = $stringMap")
                val rows = stringMap.split("\n")
                return Json.decodeFromString(rows[0])
            }
            catch (e: Exception) {
                return null
            }
        }

        fun  restoreMoveLog() : List<String> {
            val stringMap = getStringMap("${key}$MOVELOGTAG")
            GcLog.d("restoreMoveLog = $stringMap")
            val lines = stringMap.split("\n")
            val newLog : MutableList<String> = mutableListOf()
            lines.forEach { newLog.add(it) }

            return newLog
        }

        fun getGameWalletValue() = getWalletValue(key)
    }
}

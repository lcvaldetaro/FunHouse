package jni
import com.funhouse.shared.common.utils.Preview
import androidx.compose.runtime.Composable

import com.funhouse.shared.common.generated.resources.Res as CommonR
import com.funhouse.shared.common.generated.resources.underconstruction
import com.funhouse.shared.common.generated.resources.backarrow
import com.funhouse.shared.common.generated.resources.ic_profile
import com.funhouse.shared.common.generated.resources.call_spk_on
import com.funhouse.shared.common.generated.resources.speaker_off
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import com.funhouse.shared.common.generated.resources.dejavusans

import com.funhouse.shared.common.stringResource
import com.funhouse.shared.common.R
import com.funhouse.shared.common.getString

import java.io.Serializable as JavaSerializable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import club.gepetto.composeutils.scaleRatio

import com.funhouse.shared.common.generated.resources.Res
import com.funhouse.shared.common.generated.resources.poweroff
import com.funhouse.shared.common.generated.resources.settingsbtn
import com.funhouse.shared.common.SettingsBanner
import jni.Chess.Companion.saveGame
import club.gepetto.GcLog
import jni.ChessAI.getAllLegalMoves
import kotlinx.serialization.Serializable
import com.funhouse.shared.common.models.currentSettings

@Serializable
enum class ChessGameStatus { ONGOING, CHECKMATE, STALEMATE }

@Serializable
enum class ChessPlayer { WHITE, BLACK }

@Serializable
data class ChessPiece(val player: ChessPlayer, val type: String) : JavaSerializable

@Serializable
object ChessPieces {
    const val BLACK_ROOK = "\u265C"
    const val BLACK_KNIGHT = "\u265E"
    const val BLACK_BISHOP = "\u265D"
    const val BLACK_QUEEN = "\u265B"
    const val BLACK_KING = "\u265A"
    const val BLACK_PAWN = "\u265F"
    const val WHITE_ROOK = "\u2656"
    const val WHITE_KNIGHT = "\u2658"
    const val WHITE_BISHOP = "\u2657"
    const val WHITE_QUEEN = "\u2655"
    const val WHITE_KING = "\u2654"
    const val WHITE_PAWN = "\u2659"
}

private var SQUARE_SIZE  = 40.dp
private var CHESSPIECE_FONT_SIZE = 30.sp

val chessInitialBoard = listOf(
    listOf(ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_ROOK), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_KNIGHT), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_BISHOP), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_QUEEN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_KING), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_BISHOP), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_KNIGHT), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_ROOK)),
    listOf(ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN), ChessPiece(ChessPlayer.BLACK, ChessPieces.BLACK_PAWN)),
    listOf(null, null, null, null, null, null, null, null),
    listOf(null, null, null, null, null, null, null, null),
    listOf(null, null, null, null, null, null, null, null),
    listOf(null, null, null, null, null, null, null, null),
    listOf(ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_PAWN)),
    listOf(ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_ROOK), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_KNIGHT), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_BISHOP), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_QUEEN), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_KING), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_BISHOP), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_KNIGHT), ChessPiece(ChessPlayer.WHITE, ChessPieces.WHITE_ROOK)),
)

private object ChessColors {
    val LightSquare = Color.White
    val DarkSquare = Color(0xFFD2B48C)
    val KingInCheck = Color.Red
    val SelectedPiece = Color.Yellow
}

private val CHESS_TEXT_COLOR = Color.White

val chessDifficultyTextRes = listOf(R.string.chess_easy, R.string.chess_moderate, R.string.chess_hard)
val chessDifficultyDepth = listOf(0, 3, 4)

@Composable
fun ChessView(
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {}
) {
    var againstGepetto by remember { mutableStateOf(true)}
    var depth by remember { mutableStateOf(0)}
    var showSettings by remember { mutableStateOf(false)}
    var usingVoice by remember { mutableStateOf(currentSettings.usingVoice) }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val scaleRatio = scaleRatio()

        Icon(
            painter = painterResource(Res.drawable.poweroff),
            contentDescription = "Exit",
            tint = Color.White,
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
                .clickable { onExit() }
        )

        ChessGame(
            modifier = Modifier.align(Alignment.Center),
            scaleRatio = if (SQUARE_SIZE.value * scaleRatio > (maxWidth - 8.dp).value) 1f else scaleRatio,
            isGepettoOpponent = againstGepetto,
            depth = depth
        )

        Text(
            text = if (againstGepetto) stringResource(R.string.playing_against_gepetto) else stringResource(R.string.playing_against_friend),
            fontSize = 24.sp,
            color = CHESS_TEXT_COLOR,
            modifier = Modifier.align(Alignment.TopCenter).padding(12.dp)
        )

        Column (modifier = Modifier.align(Alignment.BottomCenter), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (d in 0..2)
                  Button(onClick = { depth = chessDifficultyDepth[d] }) { Text(stringResource(chessDifficultyTextRes[d])) }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Button(onClick = {
                    againstGepetto = !againstGepetto
                    Chess.saveGame(chessInitialBoard, ChessPlayer.WHITE, emptyList())
                }) {
                    Text(
                        text = if (againstGepetto) stringResource(R.string.play_against_friend) else stringResource(R.string.play_against_gepetto)
                    )
                }
            }
        }
        Icon(
            painter = painterResource(Res.drawable.settingsbtn),
            contentDescription = "Settings",
            tint = Color.White,
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
                .align(Alignment.BottomStart)
                .clickable{ showSettings = !showSettings }
        )

        if (showSettings)
            SettingsBanner(Chess.getGameWalletValue(), usingVoice, onExit = { showSettings = false }) { usingVoice = it}
    }
}
@Composable
fun Chessboard(
    board: List<List<ChessPiece?>>,
    onSquareClick: (row: Int, col: Int) -> Unit,
    selectedPiece: Pair<Int, Int>?,
    isKingInCheck: Boolean,
    kingPosition: Pair<Int, Int>?,
    modifier: Modifier = Modifier,
) {
    val chessFontFamily = FontFamily(Font(Res.font.dejavusans))
    Column (modifier) {
        board.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { colIndex, piece ->
                    val isSelected = selectedPiece?.first == rowIndex && selectedPiece?.second == colIndex
                    val isKing = kingPosition?.first == rowIndex && kingPosition?.second == colIndex
                    val color = if ((rowIndex + colIndex) % 2 == 0) ChessColors.LightSquare else ChessColors.DarkSquare
                    val finalColor = if (isKing && isKingInCheck) ChessColors.KingInCheck else if (isSelected) ChessColors.SelectedPiece else color

                    Box(
                        modifier = Modifier
                            .size(SQUARE_SIZE)
                            .background(finalColor)
                            .clickable { onSquareClick(rowIndex, colIndex) },
                        contentAlignment = Alignment.Center
                    ) {
                        piece?.let {
                            Text(text = it.type, fontSize = CHESSPIECE_FONT_SIZE, fontFamily = chessFontFamily)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChessGame(
    modifier: Modifier = Modifier,
    scaleRatio: Float = 1f,
    isGepettoOpponent: Boolean = false,
    depth: Int = 3,
) {
    var board by rememberSaveable { mutableStateOf(chessInitialBoard) }
    var currentPlayer by rememberSaveable { mutableStateOf(ChessPlayer.WHITE) }
    var selectedPiece by rememberSaveable { mutableStateOf<Pair<Int, Int>?>(null) }
    var gameStatus by rememberSaveable { mutableStateOf(ChessGameStatus.ONGOING) }
    var isCheck by rememberSaveable { mutableStateOf(false) }
    var kingPosition by rememberSaveable { mutableStateOf<Pair<Int, Int>?>(null) }
    var moveLog by rememberSaveable { mutableStateOf(listOf<String>()) }
    var scaled by rememberSaveable { mutableStateOf(false)}
    var againstGepetto by rememberSaveable { mutableStateOf(isGepettoOpponent) }
    var title by remember { mutableStateOf(" ")}

    if (moveLog.isEmpty() && Chess.restoreMoveLog().isNotEmpty()) {
        board = Chess.restoreBoard()
        moveLog = Chess.restoreMoveLog()
        currentPlayer = Chess.restoreCurrentPlayer() ?: ChessPlayer.WHITE
    }

    fun resetGame() {
        board = chessInitialBoard
        currentPlayer = ChessPlayer.WHITE
        selectedPiece = null
        gameStatus = ChessGameStatus.ONGOING
        isCheck = false
        kingPosition = null
        moveLog = emptyList()
        Chess.saveGame(board, currentPlayer, moveLog)
    }

    if (againstGepetto != isGepettoOpponent) {
        resetGame()
        againstGepetto = isGepettoOpponent
    }

    if (!scaled && scaleRatio != 1f) {
        SQUARE_SIZE = SQUARE_SIZE * scaleRatio
        CHESSPIECE_FONT_SIZE = CHESSPIECE_FONT_SIZE * scaleRatio
        scaled = true
    }

    fun toAlgebraic(row: Int, col: Int): String {
        val file = ('a' + col).toString()
        val rank = (8 - row).toString()
        return "$file$rank"
    }

    fun getPieceInitial(piece: ChessPiece): String {
        return when (piece.type) {
            ChessPieces.WHITE_PAWN, ChessPieces.BLACK_PAWN -> ""
            ChessPieces.WHITE_ROOK, ChessPieces.BLACK_ROOK -> "R"
            ChessPieces.WHITE_KNIGHT, ChessPieces.BLACK_KNIGHT -> "N"
            ChessPieces.WHITE_BISHOP, ChessPieces.BLACK_BISHOP -> "B"
            ChessPieces.WHITE_QUEEN, ChessPieces.BLACK_QUEEN -> "Q"
            ChessPieces.WHITE_KING, ChessPieces.BLACK_KING -> "K"
            else -> ""
        }
    }

    fun getStandardNotation(
        from: Pair<Int, Int>,
        to: Pair<Int, Int>,
        piece: ChessPiece,
        isCapture: Boolean,
        isCheck: Boolean,
        isCheckmate: Boolean,
        promotedTo: ChessPiece? = null
    ): String {
        val pieceInitial = getPieceInitial(piece)
        val fromStr = toAlgebraic(from.first, from.second)
        val toStr = toAlgebraic(to.first, to.second)
        val captureStr = if (isCapture) "x" else ""
        val checkStr = if (isCheckmate) "#" else if (isCheck) "+" else ""
        val promotionStr = if (promotedTo != null) "=${getPieceInitial(promotedTo)}" else ""

        return if (pieceInitial.isEmpty()) { // Pawn move
            if (isCapture) {
                "${fromStr.first()}$captureStr$toStr$promotionStr$checkStr"
            } else {
                "$toStr$promotionStr$checkStr"
            }
        } else {
            "$pieceInitial$captureStr$toStr$checkStr"
        }
    }

    fun applyMove(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        val (fromRow, fromCol) = from
        val (toRow, toCol) = to

        val newBoard = board.map { it.toMutableList() }.toMutableList()
        val movedPiece = newBoard[fromRow][fromCol]!!
        val targetPiece = newBoard[toRow][toCol]
        val isCapture = targetPiece != null

        newBoard[toRow][toCol] = movedPiece
        newBoard[fromRow][fromCol] = null

        var promotedPiece: ChessPiece? = null
        if ((movedPiece.type == ChessPieces.BLACK_PAWN || movedPiece.type == ChessPieces.WHITE_PAWN) && (toRow == 0 || toRow == 7)) {
            promotedPiece = ChessPiece(movedPiece.player, if (movedPiece.player == ChessPlayer.WHITE) ChessPieces.WHITE_QUEEN else ChessPieces.BLACK_QUEEN)
            newBoard[toRow][toCol] = promotedPiece
        }

        val nextPlayer = if (currentPlayer == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
        val isCheckAfterMove = ChessAI.isKingInCheck(newBoard, nextPlayer)
        val hasLegalMoves = ChessAI.hasLegalMoves(newBoard, nextPlayer)
        val isCheckmate = isCheckAfterMove && !hasLegalMoves

        val moveNotation = getStandardNotation(
            from = from,
            to = to,
            piece = movedPiece,
            isCapture = isCapture,
            isCheck = isCheckAfterMove,
            isCheckmate = isCheckmate,
            promotedTo = promotedPiece
        )
        val playerColor = if (currentPlayer == ChessPlayer.WHITE) "white" else "black"
        moveLog = moveLog + "$playerColor: $moveNotation"
        GcLog.d("move: $playerColor: $moveNotation")

        board = newBoard
        currentPlayer = nextPlayer
        isCheck = isCheckAfterMove

        Chess.saveGame(board, currentPlayer, moveLog)

        if (!hasLegalMoves) {
            gameStatus = if (isCheck) ChessGameStatus.CHECKMATE else ChessGameStatus.STALEMATE
            Chess.saveResult(againstGepetto, depth, gameStatus, currentPlayer)
        }
        GcLog.d("Game Status: check: $isCheck ${gameStatus}")
    }

    fun makeGepettoMove() {
        if (depth == 0) {
            val legalMoves = getAllLegalMoves(board, currentPlayer)
            if (legalMoves.isNotEmpty()) {
                val move = legalMoves.random()
                applyMove(move.first, move.second)
            }
        }
        else {
            val bestMove = ChessAI.findBestMove(board, currentPlayer, depth)
            applyMove(bestMove.first, bestMove.second)
        }
    }

    fun handleSquareClick(row: Int, col: Int) {
        if (gameStatus != ChessGameStatus.ONGOING || (isGepettoOpponent && currentPlayer == ChessPlayer.BLACK)) return

        selectedPiece?.let { selPiece ->
            val (selectedRow, selectedCol) = selPiece
            if (row == selectedRow && col == selectedCol) {
                selectedPiece = null
            } else {
                if (ChessAI.isValidMove(board, selPiece, row to col)) {
                    if (ChessAI.isKingInCheckAfterMove(board, currentPlayer, selPiece, row to col)) {
                        return
                    }
                    applyMove(selPiece, row to col)
                    if (isGepettoOpponent && gameStatus == ChessGameStatus.ONGOING) {
                        selectedPiece = null
                        makeGepettoMove()
                    }
                }
            }
        } ?: run {
            val piece = board[row][col]
            if (piece != null && piece.player == currentPlayer) {
                selectedPiece = row to col
            }
        }
    }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val difficultyIndex = chessDifficultyDepth.indexOf(depth)
        val statusResId = when (gameStatus) {
            ChessGameStatus.ONGOING -> R.string.chess_status_ongoing
            ChessGameStatus.CHECKMATE -> R.string.chess_status_checkmate
            ChessGameStatus.STALEMATE -> R.string.chess_status_stalemate
        }
        title = stringResource(R.string.chess_status_level, stringResource(statusResId), stringResource(chessDifficultyTextRes[difficultyIndex]))
        Text(
            text = title,
            color = CHESS_TEXT_COLOR,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        Chessboard(
            board = board,
            onSquareClick = ::handleSquareClick,
            selectedPiece = selectedPiece,
            isKingInCheck = isCheck,
            kingPosition = kingPosition,
        )

        when (gameStatus) {
            ChessGameStatus.CHECKMATE -> Text(
                stringResource(R.string.chess_checkmate, if (currentPlayer == ChessPlayer.WHITE) stringResource(R.string.chess_black) else stringResource(R.string.chess_white)), color = CHESS_TEXT_COLOR
            )
            ChessGameStatus.STALEMATE -> Text(stringResource(R.string.chess_stalemate), color = CHESS_TEXT_COLOR)
            else -> {}
        }

        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(onClick = { resetGame() }) { Text(stringResource(R.string.new_game)) }
        }
    }
}


@Composable
fun ChessGamePreview() {
    ChessView(Modifier.background(Color.Black))
}

@Composable
private fun GcImage(
    imageResource: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    androidx.compose.foundation.Image(
        painter = org.jetbrains.compose.resources.painterResource(imageResource),
        contentDescription = null,
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        contentScale = contentScale
    )
}

@Composable
private fun GcImage(
    imageResource: Int?,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    // No-op box for dummy / layout placeholder integer resources
    androidx.compose.foundation.layout.Box(modifier)
}

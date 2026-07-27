package jni

// Minimax with Alpha-Beta Pruning Algorithm

// This object will contain all the AI logic for Gepetto
object ChessAI {

    // Piece values for evaluation
    private const val PAWN_VALUE = 10
    private const val KNIGHT_VALUE = 30
    private const val BISHOP_VALUE = 30
    private const val ROOK_VALUE = 50
    private const val QUEEN_VALUE = 90
    private const val KING_VALUE = 900 // King value is high as losing it means game over

    // --- Game Logic Functions (Moved from ChessGame) ---

    fun isValidMove(board: List<List<ChessPiece?>>, from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
        val (fromRow, fromCol) = from
        val (toRow, toCol) = to
        val piece = board[fromRow][fromCol]
        val targetPiece = board[toRow][toCol]

        if (piece == null) {
            return false
        }

        // A piece cannot capture a piece of the same color
        if (targetPiece != null && targetPiece.player == piece.player) {
            return false
        }

        fun isPathClear(start: Pair<Int, Int>, end: Pair<Int, Int>): Boolean {
            val (startRow, startCol) = start
            val (endRow, endCol) = end
            val rowDiff = endRow - startRow
            val colDiff = endCol - startCol

            val rowStep = if (rowDiff != 0) rowDiff / Math.abs(rowDiff) else 0
            val colStep = if (colDiff != 0) colDiff / Math.abs(colDiff) else 0

            var currentRow = startRow + rowStep
            var currentCol = startCol + colStep

            while (currentRow != endRow || currentCol != endCol) {
                if (board[currentRow][currentCol] != null) {
                    return false
                }
                currentRow += rowStep
                currentCol += colStep
            }
            return true
        }

        when (piece.type) {
            ChessPieces.BLACK_PAWN, ChessPieces.WHITE_PAWN -> { // Pawn
                val forwardDirection = if (piece.player == ChessPlayer.WHITE) -1 else 1
                val startRow = if (piece.player == ChessPlayer.WHITE) 6 else 1

                // Move forward
                if (fromCol == toCol) {
                    // One square forward
                    if (toRow == fromRow + forwardDirection && board[toRow][toCol] == null) {
                        return true
                    }
                    // Two squares forward from start
                    if (fromRow == startRow && toRow == fromRow + 2 * forwardDirection && board[toRow][toCol] == null && board[fromRow + forwardDirection][toCol] == null) {
                        return true
                    }
                }
                // Capture
                if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + forwardDirection) {
                    if (targetPiece != null && targetPiece.player != piece.player) {
                        return true
                    }
                }
            }
            ChessPieces.BLACK_ROOK, ChessPieces.WHITE_ROOK -> { // Rook
                if (fromRow == toRow || fromCol == toCol) {
                    return isPathClear(from, to)
                }
            }
            ChessPieces.BLACK_KNIGHT, ChessPieces.WHITE_KNIGHT -> { // Knight
                val rowDiff = Math.abs(fromRow - toRow)
                val colDiff = Math.abs(fromCol - toCol)
                return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
            }
            ChessPieces.BLACK_BISHOP, ChessPieces.WHITE_BISHOP -> { // Bishop
                if (Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) {
                    return isPathClear(from, to)
                }
            }
            ChessPieces.BLACK_QUEEN, ChessPieces.WHITE_QUEEN -> { // Queen
                if (fromRow == toRow || fromCol == toCol || Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) {
                    return isPathClear(from, to)
                }
            }
            ChessPieces.BLACK_KING, ChessPieces.WHITE_KING -> { // King
                val rowDiff = Math.abs(fromRow - toRow)
                val colDiff = Math.abs(fromCol - toCol)
                return rowDiff <= 1 && colDiff <= 1
            }
        }

        return false
    }

    fun isKingInCheck(board: List<List<ChessPiece?>>, player: ChessPlayer): Boolean {
        val kingPos = board.flatMapIndexed { r, row ->
            row.mapIndexedNotNull { c, piece ->
                if (piece != null && piece.player == player && (piece.type == ChessPieces.BLACK_KING || piece.type == ChessPieces.WHITE_KING)) {
                    r to c
                } else {
                    null
                }
            }
        }.firstOrNull() ?: return false

        val opponentPlayer = if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE

        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.player == opponentPlayer) {
                    if (isValidMove(board, r to c, kingPos)) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun hasLegalMoves(board: List<List<ChessPiece?>>, player: ChessPlayer): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.player == player) {
                    for (toRow in 0..7) {
                        for (toCol in 0..7) {
                            if (isValidMove(board, r to c, toRow to toCol)) {
                                val newBoard = board.map { it.toMutableList() }.toMutableList()
                                newBoard[toRow][toCol] = newBoard[r][c]
                                newBoard[r][c] = null
                                if (!isKingInCheck(newBoard, player)) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    fun getAllLegalMoves(board: List<List<ChessPiece?>>, player: ChessPlayer): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val legalMoves = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.player == player) {
                    for (toRow in 0..7) {
                        for (toCol in 0..7) {
                            val from = r to c
                            val to = toRow to toCol
                            if (isValidMove(board, from, to)) {
                                val newBoard = board.map { it.toMutableList() }.toMutableList()
                                newBoard[toRow][toCol] = newBoard[r][c]
                                newBoard[r][c] = null
                                if (!isKingInCheck(newBoard, player)) {
                                    legalMoves.add(from to to)
                                }
                            }
                        }
                    }
                }
            }
        }
        return legalMoves
    }

    fun isKingInCheckAfterMove(board: List<List<ChessPiece?>>, player: ChessPlayer, from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
        val newBoard = board.map { it.toMutableList() }.toMutableList()
        newBoard[to.first][to.second] = newBoard[from.first][from.second]
        newBoard[from.first][from.second] = null
        return isKingInCheck(newBoard, player)
    }

    // --- AI Specific Functions ---

    private fun getPieceValue(piece: ChessPiece?): Int {
        return when (piece?.type) {
            ChessPieces.WHITE_PAWN, ChessPieces.BLACK_PAWN -> PAWN_VALUE
            ChessPieces.WHITE_KNIGHT, ChessPieces.BLACK_KNIGHT -> KNIGHT_VALUE
            ChessPieces.WHITE_BISHOP, ChessPieces.BLACK_BISHOP -> BISHOP_VALUE
            ChessPieces.WHITE_ROOK, ChessPieces.BLACK_ROOK -> ROOK_VALUE
            ChessPieces.WHITE_QUEEN, ChessPieces.BLACK_QUEEN -> QUEEN_VALUE
            ChessPieces.WHITE_KING, ChessPieces.BLACK_KING -> KING_VALUE
            else -> 0
        }
    }

    fun evaluateBoard(board: List<List<ChessPiece?>>, player: ChessPlayer): Int {
        var score = 0
        board.forEach { row ->
            row.forEach { piece ->
                if (piece != null) {
                    score += if (piece.player == player) getPieceValue(piece) else -getPieceValue(piece)
                }
            }
        }
        return score
    }

    fun minimax(
        board: List<List<ChessPiece?>>,
        depth: Int,
        isMaximizingPlayer: Boolean,
        player: ChessPlayer,
        alpha: Int,
        beta: Int
    ): Int {
        if (depth == 0) {
            return evaluateBoard(board, player)
        }

        val legalMoves = getAllLegalMoves(board, player)

        if (legalMoves.isEmpty()) {
            return if (isKingInCheck(board, player)) {
                // Checkmate
                if (isMaximizingPlayer) -KING_VALUE else KING_VALUE
            } else {
                // Stalemate
                0
            }
        }

        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizingPlayer) {
            var maxEval = Int.MIN_VALUE
            for (move in legalMoves) {
                val newBoard = board.map { it.toMutableList() }.toMutableList()
                newBoard[move.second.first][move.second.second] = newBoard[move.first.first][move.first.second]
                newBoard[move.first.first][move.first.second] = null

                val eval = minimax(newBoard, depth - 1, false, if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE, currentAlpha, currentBeta)
                maxEval = Math.max(maxEval, eval)
                currentAlpha = Math.max(currentAlpha, eval)
                if (currentBeta <= currentAlpha) {
                    break // Alpha-beta pruning
                }
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in legalMoves) {
                val newBoard = board.map { it.toMutableList() }.toMutableList()
                newBoard[move.second.first][move.second.second] = newBoard[move.first.first][move.first.second]
                newBoard[move.first.first][move.first.second] = null

                val eval = minimax(newBoard, depth - 1, true, if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE, currentAlpha, currentBeta)
                minEval = Math.min(minEval, eval)
                currentBeta = Math.min(currentBeta, eval)
                if (currentBeta <= currentAlpha) {
                    break // Alpha-beta pruning
                }
            }
            return minEval
        }
    }

    fun findBestMove(board: List<List<ChessPiece?>>, player: ChessPlayer, depth: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        var bestMove: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
        var bestValue = Int.MIN_VALUE

        val legalMoves = getAllLegalMoves(board, player)
        if (legalMoves.isEmpty()) {
            return (0 to 0) to (0 to 0) // Should not happen if game is ongoing
        }

        for (move in legalMoves) {
            val newBoard = board.map { it.toMutableList() }.toMutableList()
            newBoard[move.second.first][move.second.second] = newBoard[move.first.first][move.first.second]
            newBoard[move.first.first][move.first.second] = null

            val moveValue = minimax(newBoard, depth - 1, false, if (player == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE, Int.MIN_VALUE, Int.MAX_VALUE)

            if (moveValue > bestValue) {
                bestValue = moveValue
                bestMove = move
            }
        }
        return bestMove ?: legalMoves.random() // Fallback to random if no best move found (shouldn't happen)
    }
}

package connectfour

const val FOUR_CONSECUTIVE = 4

enum class Orientation {
    HORIZONTAL, VERTICAL, UP_RIGHT, UP_LEFT
}

class Board() {
    private var row = 6
    private var column = 7
    private var player1 = ""
    private var player2 = ""
    private var player1Win = 0
    private var player2Win = 0
    private var player1Turn = true
    private val boardSpace = mutableListOf<MutableList<Char>>()
    private var colMove = 0
    private var rowMove = 0
    private var noOfGames = 0
    fun initPlayersName() {
        println("Connect Four")
        println("""How to play:
1. setting player's name
2. setting board dimension <row>x<column> ex. 6x7
3. setting number of match ex. 3
4. player move <column> ex. 1
5. first player to connect 4 piece horizontally, vertically or diagonally will win

        """.trimMargin())
        println("First player's name:")
        player1 = readln()
        println("Second player's name:")
        player2 = readln()
    }

    fun initBoardDimension() {
        while (true) {
            try {
                var invalid = false
                println(
                    "Set the board dimensions (Rows x Columns)\n" +
                            "Press Enter for default (6 x 7)"
                )
                var input = readln().lowercase()
                if (input.isNotEmpty()) {
                    val dimension = input.replace(Regex("\\s"), "").split("x")
                    if (dimension.size != 2)
                        throw Exception()
                    if (dimension[0].toInt() !in 5..9) {
                        println("Board rows should be from 5 to 9")
                        invalid = true
                    }
                    if (dimension[1].toInt() !in 5..9) {
                        println("Board columns should be from 5 to 9")
                        invalid = true
                    }
                    if (invalid)
                        continue
                    row = dimension[0].toInt()
                    column = dimension[1].toInt()
                }
                while (true) {
                    try {
                        println(
                            "Do you want to play single or multiple games?\n" +
                                    "For a single game, input 1 or press Enter\n" +
                                    "Input a number of games:"
                        )
                        input = readln()
                        noOfGames = if (input.isEmpty())
                            1
                        else
                            input.toInt()
                        if (noOfGames == 0)
                            throw Exception()
                        else
                            break
                    } catch (e: Exception) {
                        println("Invalid input")
                    }
                }
                break
            } catch (e: Exception) {
                println("Invalid input")
            }
        }
        println("$player1 VS $player2")
        println("$row X $column board")
        repeat(row) {
            boardSpace.add(MutableList(column) { ' ' })
        }
        if (noOfGames == 1) {
            println("Single game")
            printBoard()
        } else
            println("Total $noOfGames games")
    }

    private fun clearBoard() {
        for (r in 0 until row) {
            for (c in 0 until column) {
                boardSpace[r][c] = ' '
            }
        }
    }

    private fun printBoard() {
        println(" ${List(column) { it + 1 }.joinToString(" ")} ")
        repeat(row) {
            println("|${boardSpace[it].joinToString("|")}|")
        }
        println(List(column * 2 + 1) { "=" }.joinToString(""))
    }

    fun startGame() {
        repeat(noOfGames) {
            if (noOfGames != 1) {
                println("Game #${it + 1}")
                printBoard()
            }
            while (true) {
                try {
                    println("${if (player1Turn) player1 else player2}'s turn:")
                    val input = readln()
                    if (input == "end")
                        break
                    val col = input.toInt()
                    if (col !in 1..column) {
                        println("The column number is out of range (1 - $column)")
                        continue
                    }
                    var isFull = true
                    for (i in row - 1 downTo 0) {
                        if (boardSpace[i][col - 1] == ' ') {
                            boardSpace[i][col - 1] = if (player1Turn) 'o' else '*'
                            colMove = col - 1
                            rowMove = i
                            isFull = false
                            break
                        }
                    }
                    if (isFull)
                        println("Column $col is full")
                    else {
                        printBoard()
                        if (winOrDraw())
                            break
                        player1Turn = !player1Turn
                    }
                } catch (e: Exception) {
                    println("Incorrect column number")
                }
            }
            if (noOfGames != 1)
                println(
                    """Score
$player1: $player1Win $player2: $player2Win"""
                )
            player1Turn = !player1Turn
            if (it == noOfGames - 1)
                print("Game over!")
            clearBoard()
        }
    }

    private fun winOrDraw(): Boolean {
        return if (winAdjacent(Orientation.HORIZONTAL) || winAdjacent(Orientation.VERTICAL) || winDiagonal(Orientation.UP_RIGHT) || winDiagonal(
                Orientation.UP_LEFT
            )
        ) {
            println("Player ${if (player1Turn) player1 else player2} won")
            if (player1Turn) player1Win += 2 else player2Win += 2
            true
        } else if (isDraw()) {
            println("It is a draw")
            player1Win++
            player2Win++
            true
        } else
            false
    }

    private fun isDraw(): Boolean {
        for (r in 0 until row) {
            for (c in 0 until column) {
                if (boardSpace[r][c] == ' ')
                    return false
            }
        }
        return true
    }

    private fun winAdjacent(orientation: Orientation): Boolean {
        val pieceToCount = if (player1Turn) 'o' else '*'
        var consecutiveCount = 0
        repeat(if (orientation == Orientation.HORIZONTAL) column else row) {
            if ((if (orientation == Orientation.HORIZONTAL) boardSpace[rowMove][it] else boardSpace[it][colMove]) == pieceToCount)
                ++consecutiveCount
            else
                consecutiveCount = 0
            if (consecutiveCount == FOUR_CONSECUTIVE)
                return true
        }
        return false
    }

    private fun winDiagonal(orientation: Orientation): Boolean {
        val pieceToCount = if (player1Turn) 'o' else '*'
        var consecutiveCount = 0
        var startRow = rowMove
        var startCol = colMove
        while (true) {
            if (startRow == 0 || startCol == if (orientation == Orientation.UP_RIGHT) column - 1 else 0)
                break
            else {
                if (orientation == Orientation.UP_RIGHT) {
                    startRow--
                    startCol++
                } else {
                    startRow--
                    startCol--
                }
            }
        }
        for (i in startRow until row) {
            if (boardSpace[i][startCol] == pieceToCount)
                ++consecutiveCount
            else
                consecutiveCount = 0
            if (consecutiveCount == FOUR_CONSECUTIVE)
                return true
            if (i == row - 1 || startCol == if (orientation == Orientation.UP_RIGHT) 0 else column - 1)
                break
            else {
                if (orientation == Orientation.UP_RIGHT) startCol-- else startCol++
            }
        }
        return false
    }
}

fun main() {
    val board = Board()
    board.initPlayersName()
    board.initBoardDimension()
    board.startGame()
}
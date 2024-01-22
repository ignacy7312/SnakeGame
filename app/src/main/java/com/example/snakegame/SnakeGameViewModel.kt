package com.example.snakegame

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SnakeGameViewModel(): ViewModel(
) {

    private val _state = MutableStateFlow(SnakeGameState())
    val state = _state.asStateFlow()


    fun onEvent(event: SnakeGameEvent) {
        when (event) {
            SnakeGameEvent.StartGame -> { // rozpoczęcie gry
                _state.update { it.copy(gameState = GameState.STARTED) } // aktualizacja stanu
                viewModelScope.launch {
                    while (state.value.gameState == GameState.STARTED) {
                        val delayMillis = when (state.value.snake.size) { // im dłuższy wąż tym trudniej
                            in 1..5 -> 120L
                            in 6..10 -> 110L
                            else -> 100L
                        }
                        delay(delayMillis)
                        _state.value = updateGame(state.value)
                    }
                }
            }

            SnakeGameEvent.PauseGame -> { // zaktualizuj stan na PAUSED
                _state.update { it.copy(gameState = GameState.PAUSED) }
            }

            SnakeGameEvent.ResetGame -> { //graj od nowa
                _state.value = SnakeGameState()
            }

            SnakeGameEvent.ResizeGame -> { // zmień rozmiar planszy - nie działa idealnie!
                _state.update {it.copy(xAxisGridSize = 15, yAxisGridSize = 20)}
            }

            is SnakeGameEvent.UpdateDirection -> { // zmiana kierunku węża
                updateDirection(event.offset, event.canvasWidth)
            }
        }
    }

    private fun updateDirection(offset: Offset, canvasWidth: Int) {
        if (!state.value.isGameOver) { // jeżeli gra w toku
            val cellSize = canvasWidth / state.value.xAxisGridSize
            val tapX = (offset.x / cellSize).toInt()
            val tapY = (offset.y / cellSize).toInt()
            val head = state.value.snake.first()

            _state.update {
                it.copy(
                    direction = when (state.value.direction) {
                        Direction.UP, Direction.DOWN -> {
                            if (tapX < head.x) Direction.LEFT else Direction.RIGHT
                        }

                        Direction.LEFT, Direction.RIGHT -> {
                            if (tapY < head.y) Direction.UP else Direction.DOWN
                        }
                    }
                )
            }
        }
    }

    private fun updateGame(currentGame: SnakeGameState): SnakeGameState {
        if (currentGame.isGameOver) {
            return currentGame
        }
        currentGame.score = currentGame.snake.size - 1 // przypisanie score
        val head = currentGame.snake.first() // pierwsza komórka to głowa węża
        val xAxisGridSize = currentGame.xAxisGridSize
        val yAxisGridSize = currentGame.yAxisGridSize

        //Aktualizacja ruchu węża
        val newHead = when (currentGame.direction) {
            Direction.UP -> Coordinate(x = head.x, y = (head.y - 1))
            Direction.DOWN -> Coordinate(x = head.x, y = (head.y + 1))
            Direction.LEFT -> Coordinate(x = head.x - 1, y = (head.y))
            Direction.RIGHT -> Coordinate(x = head.x + 1, y = (head.y))
        }

        //Sprawdzenie czy wąż nie zderzył się ze sobą/nie wyszedł z pola gry
        if (
            currentGame.snake.contains(newHead) ||
            !isWithinBounds(newHead, xAxisGridSize, yAxisGridSize)
        ) {
            return currentGame.copy(isGameOver = true)
        }

        //Sprawdzenie czy wąż zjadł - jeżeli tak to generacja nowego położenia jedzenia, jeżeli nie to zostaje stare
        var newSnake = mutableListOf(newHead) + currentGame.snake
        val newFood = if (newHead == currentGame.food) SnakeGameState.generateRandomFoodCoordinate(currentGame.xAxisGridSize, currentGame.yAxisGridSize)
        else currentGame.food

        //Aktualizacja długości węża
        if (newHead != currentGame.food) {
            newSnake = newSnake.toMutableList()
            newSnake.removeAt(newSnake.size - 1)
        }
        return currentGame.copy(snake = newSnake, food = newFood)
    }

    private fun isWithinBounds(
        coordinate: Coordinate,
        xAxisGridSize: Int,
        yAxisGridSize: Int
    ): Boolean {
        return coordinate.x in 1 until xAxisGridSize - 1
                && coordinate.y in 1 until yAxisGridSize - 1
    }
}







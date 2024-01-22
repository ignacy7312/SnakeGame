package com.example.snakegame

import kotlin.random.Random

data class SnakeGameState( // stałe + zmienne podczas gry
    val xAxisGridSize: Int = 20,
    val yAxisGridSize: Int = 30,
    val direction: Direction = Direction.RIGHT, // 4 możliwe kierunki
    val snake: List<Coordinate> = listOf(Coordinate(x = 5, y = 5)), // 2 koordynaty, defaultowo pozycja głowy
    val food: Coordinate = generateRandomFoodCoordinate(xAxisGridSize, yAxisGridSize),
    val isGameOver: Boolean = false,
    val gameState: GameState = GameState.IDLE,
    var score : Int = 0,
    var highScore : Int = 69
) {
    companion object {
        fun generateRandomFoodCoordinate(xMax : Int, yMax : Int): Coordinate { // generacja randomowego położenia jabłka
            return Coordinate(
                x = Random.nextInt(from = 1, until = xMax - 1),
                y = Random.nextInt(from = 1, until = yMax - 1)
            )
        }
    }
}

enum class GameState {
    IDLE,
    STARTED,
    PAUSED
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

data class Coordinate(
    val x: Int,
    val y: Int
)
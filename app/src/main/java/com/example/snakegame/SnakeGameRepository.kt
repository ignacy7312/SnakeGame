package com.example.snakegame

class SnakeGameRepository(private val snakeGameDao: HighScoreDao) {

    suspend fun updateHS(score: Int) {
        snakeGameDao.updateHighScore(HighScore(score))
    }

}
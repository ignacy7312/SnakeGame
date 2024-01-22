package com.example.snakegame

sealed interface HighScoreEvent{
    object SaveHighScore: HighScoreEvent
    data class SetHighScore(val highscore: Int): HighScoreEvent
    data class GetHighScore(val placeholder: Int): HighScoreEvent
    object ShowDialog: HighScoreEvent
}
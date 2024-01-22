package com.example.snakegame

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface HighScoreDao {
    @Query("SELECT * from high_score_table LIMIT 1")
    suspend fun getHighScore() : HighScore

    @Insert
    suspend fun insertHighScore(highScore: HighScore)

    @Update
    suspend fun updateHighScore(highScore: HighScore)

}
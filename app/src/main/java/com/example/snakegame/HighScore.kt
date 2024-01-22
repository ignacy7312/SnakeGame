package com.example.snakegame

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_score_table")
data class HighScore(
    var score: Int = 0,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
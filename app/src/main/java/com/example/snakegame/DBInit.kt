package com.example.snakegame

import android.app.Application
import androidx.room.Room

class App : Application() {
    val database: HighScoreDatabase by lazy {
        Room.databaseBuilder(
            this,
            HighScoreDatabase::class.java, "high_score_database"
        ).build()
    }
}
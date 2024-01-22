package com.example.snakegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.snakegame.ui.theme.SnakeGameTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            HighScoreDatabase::class.java,
            "hs.db"
        ).build()
    }
    private val viewModelhs by viewModels<HighScoreViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun<T : ViewModel> create(modelClass: Class <T>): T {
                    return HighScoreViewModel(db.highScoreDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGameTheme {
                val viewModel = viewModel<SnakeGameViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                SnakeGameScreen(
                    state = state,
                    onEvent = viewModel::onEvent,
                    highScoreViewModel = viewModelhs
                )
            }
        }
    }
}


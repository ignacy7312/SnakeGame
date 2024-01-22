package com.example.snakegame

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HighScoreViewModel(
    private val dao: HighScoreDao
    ): ViewModel()
{
        private val _state = MutableStateFlow(HighScoreState())

    var hs : HighScore? = HighScore(0)

    fun onEvent(event: HighScoreEvent) : Int?{

        when(event){
            is HighScoreEvent.SetHighScore -> {

                viewModelScope.launch {
                    hs = dao.getHighScore()
                }

                if (hs == null){
                    hs = HighScore(0)
                }
                viewModelScope.launch {
                    dao.insertHighScore(HighScore(event.highscore))
                }
                if (event.highscore > hs!!.score){
                    viewModelScope.launch {
                        dao.updateHighScore(HighScore(event.highscore))
                    }
                    return event.highscore
                }
                return hs!!.score
            }
            is HighScoreEvent.GetHighScore -> {

                viewModelScope.launch {
                    hs = dao.getHighScore()
                }

                return hs?.score
            }
            else -> {}
        }
        return 0
    }
}
package com.example.snakegame

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snakegame.ui.theme.Citrine
import com.example.snakegame.ui.theme.Custard
import com.example.snakegame.ui.theme.PurpleGrey40
import com.example.snakegame.ui.theme.RoyalBlue

@Composable
fun SnakeGameScreen(
    state: SnakeGameState,
    onEvent: (SnakeGameEvent) -> Unit,
    highScoreViewModel: HighScoreViewModel
) {

    var highScore = 0
    //var highScore: Int? = null
    val placeholder : Int?= highScoreViewModel.onEvent(HighScoreEvent.GetHighScore(0))
    if ( placeholder == null ){
            state.highScore = 0
        } else {
        state.highScore = placeholder
    }
    //Log.d("TUTAJ", "$state.score, $state.highScore")


    //przypisanie odpowiednich obrazków
    val foodImageBitmap = ImageBitmap.imageResource(id = R.drawable.japko)
    val snakeHeadImageBitmap = when (state.direction) {
        Direction.RIGHT -> ImageBitmap.imageResource(id = R.drawable.h)
        Direction.LEFT -> ImageBitmap.imageResource(id = R.drawable.h)
        Direction.UP -> ImageBitmap.imageResource(id = R.drawable.hu)
        Direction.DOWN -> ImageBitmap.imageResource(id = R.drawable.h)
    }

    val context = LocalContext.current
    val foodSoundMP = remember { MediaPlayer.create(context, R.raw.food) }  //przypisanie dźwięków
    val gameOverSoundMP = remember { MediaPlayer.create(context, R.raw.gameover) }

    LaunchedEffect(key1 = state.snake.size) {
        if (state.snake.size != 1) {
            foodSoundMP?.start()
        }
    }

    LaunchedEffect(key1 = state.isGameOver) {
        if (state.isGameOver) {
            gameOverSoundMP?.start()
            highScoreViewModel.onEvent(HighScoreEvent.SetHighScore(state.score))
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Card(   //card na score
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(14.dp),
                    text = "wynik: ${state.score}, najelpszy: ${state.highScore}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth() //zajmuje cala szerokosc ekarnu telefonu
                    .aspectRatio(ratio = 2 / 3f)
                    .pointerInput(state.gameState) { //zbieranie dotyku
                        if (state.gameState != GameState.STARTED) {
                            return@pointerInput
                        }
                        detectTapGestures { offset ->
                            onEvent(SnakeGameEvent.UpdateDirection(offset, size.width))
                        }
                    }
            ) {
                val cellSize = size.width / 20 // rozmiar pola = rozmiar canvas/ilość pól
                drawGameBoard( // rysowanie pola gry
                    cellSize = cellSize,
                    cellColor = PurpleGrey40,
                    borderCellColor = Color.White,
                    gridWidth = state.xAxisGridSize,
                    gridHeight = state.yAxisGridSize
                )
                drawApple( //rysowanie obrazka
                    foodImage = foodImageBitmap,
                    cellSize = cellSize.toInt(),
                    coordinate = state.food
                )
                drawSnake( //rysowanie weza
                    snakeHeadImage = snakeHeadImageBitmap,
                    cellSize = cellSize,
                    snake = state.snake
                )
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(SnakeGameEvent.ResetGame) },
                    enabled = state.gameState == GameState.PAUSED || state.isGameOver
                ) {
                    Text(text = if (state.isGameOver) "resetuj" else "nowa gra ")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when (state.gameState) {
                            GameState.IDLE, GameState.PAUSED -> onEvent(SnakeGameEvent.StartGame)
                            GameState.STARTED -> onEvent(SnakeGameEvent.PauseGame)
                        }
                    },
                    enabled = !state.isGameOver
                ) {
                    Text(
                        text = when (state.gameState) {
                            GameState.IDLE -> "graj"
                            GameState.STARTED -> "pauza"
                            GameState.PAUSED -> "wznów"
                        }
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(SnakeGameEvent.ResizeGame) },
                    enabled = state.gameState == GameState.IDLE
                ) {
                    Text(text = "resize!")
                }
            }
        }
        AnimatedVisibility(visible = state.isGameOver) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "PORAŻKA",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

private fun DrawScope.drawGameBoard( //rysowanie pola gry
    cellSize: Float,
    cellColor: Color,
    borderCellColor: Color,
    gridWidth: Int,
    gridHeight: Int
) {
    for (i in 0 until gridWidth) { //klasyczne rysowanie pola - iteracja po x i y
        for (j in 0 until gridHeight) {
            // sprawdzenie odpowiednich warunków
            val isBorderCell = i == 0 || j == 0 || i == gridWidth - 1 || j == gridHeight - 1
            drawRect(
                color = if (isBorderCell) borderCellColor // border
                else if ((i + j) % 2 == 0) cellColor // co drugie pole ma inny kolor
                else cellColor.copy(alpha = 0.5f),
                topLeft = Offset(x = i * cellSize, y = j * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

private fun DrawScope.drawApple( //rysowanie jedzenia
    foodImage: ImageBitmap,
    cellSize: Int,
    coordinate: Coordinate
) {
    drawImage(
        image = foodImage,
        dstOffset = IntOffset(
            x = (coordinate.x * cellSize), //odpowiednie przesunięcie
            y = (coordinate.y * cellSize)
        ),
        dstSize = IntSize(cellSize, cellSize)
    )
}

private fun DrawScope.drawSnake(
    snakeHeadImage: ImageBitmap,
    cellSize: Float,
    snake: List<Coordinate>
) {
    val cellSizeInt = cellSize.toInt()
    snake.forEachIndexed { index, coordinate -> //dla każdej części weżą
        if (index == 0) {
            drawImage(  //jeżeli głowa to rysuj obrazek
                image = snakeHeadImage,
                dstOffset = IntOffset(
                    x = (coordinate.x * cellSizeInt),
                    y = (coordinate.y * cellSizeInt)
                ),
                dstSize = IntSize(cellSizeInt, cellSizeInt)
            )
        } else {
            drawRect( // jeżeli nie głowa to rysuj zielony kwadrat
                color = Citrine,
                topLeft = Offset(coordinate.x * cellSize, coordinate.y * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
}









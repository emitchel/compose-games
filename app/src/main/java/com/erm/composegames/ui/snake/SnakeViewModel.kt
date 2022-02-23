package com.erm.composegames.ui.snake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SnakeViewModel : ViewModel() {
    //TODO rig up
    private val _uiState = MutableStateFlow<SnakeUiState>(SnakeUiState.Pregame)
    val uiState: StateFlow<SnakeUiState>
        get() = _uiState.asStateFlow()

    private val _snakeState = MutableStateFlow(SnakeState())
    val snakeState: StateFlow<SnakeState>
        get() = _snakeState.asStateFlow()

    //TODO rig up via settings
    private val _lastRequestedDirection = MutableStateFlow(SnakeDirection.RIGHT) //TODO randomize
    fun requestDirection(direction: SnakeDirection) {
        _lastRequestedDirection.value = direction
    }

    //TODO rig up via settings
    private val _stepsPerSecond = MutableStateFlow(1)
    fun updateStepsPerSecond(perSecond: Int) {
        _stepsPerSecond.value = perSecond
    }

    init {
        //TODO have this be triggered by action instead...
        start()
    }

    private var snakeMovingJob: Job? = null
    fun start() {
        //TODO rig up countdown

        //TODO randomize starting food position
        _uiState.value = SnakeUiState.Playing(SnakeFoodPosition(7, 2))


        snakeMovingJob = viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay((1 / _stepsPerSecond.value) * 1000L)
                progressUi()
            }
        }
    }

    private fun progressUi() {
        (_uiState.value as? SnakeUiState.Playing)?.let {
            val newState = _snakeState.value.progress(
                _lastRequestedDirection.value,
                it.foodPosition
            )

            _snakeState.value = newState

            when (newState.moveResult) {
                SnakeMoveResult.OK -> {
                    //no op
                }
                SnakeMoveResult.ATE -> {
                    //TODO make noise?
                }
                SnakeMoveResult.OB, SnakeMoveResult.CLASH -> {
                    _uiState.value =
                        SnakeUiState.GameOver(_snakeState.value.bodyPositions.size)
                    snakeMovingJob?.cancel()
                }
            }
        }
    }


    /**
     *
     * Progress the snake state to the next position
     *
     * Check [SnakeState.moveResult] to determine what the UI should do
     */
    private fun SnakeState.progress(
        requestedDirection: SnakeDirection,
        foodPosition: SnakeFoodPosition,
        xMax: Int = 9,
        yMax: Int = 9
    ): SnakeState = run {

        //Don't update direction if it's the opposite
        val directionAdjustedFor =
            if (direction.isOpposite(requestedDirection)) direction else requestedDirection

        val head = bodyPositions.first()
        val nextPosition = when (directionAdjustedFor) {
            SnakeDirection.UP -> head.copy(x = head.x, y = head.y + 1)
            SnakeDirection.DOWN -> head.copy(x = head.x, y = head.y - 1)
            SnakeDirection.LEFT -> head.copy(x = head.x - 1, y = head.y)
            SnakeDirection.RIGHT -> head.copy(x = head.x + 1, y = head.y)
        }

        //Move body right away
        val newBody = (listOf(nextPosition) + bodyPositions).toMutableList()

        //If the LAST STATE wasn't ATE then remove the tail before evaluating next result
        if (moveResult != SnakeMoveResult.ATE) {
            newBody.removeLast()
        }

        //Snake can't move out of bounds
        if (nextPosition.x > xMax || nextPosition.y > yMax || listOf(
                nextPosition.x,
                nextPosition.y
            ).any { it < 0 }
        ) return copy(
            bodyPositions = newBody,
            direction = directionAdjustedFor,
            moveResult = SnakeMoveResult.OB
        )

        //Snake clashed with food, set result to ATE
        if (nextPosition.x == foodPosition.x && nextPosition.y == foodPosition.y)
            return copy(
                bodyPositions = newBody,
                direction = directionAdjustedFor,
                moveResult = SnakeMoveResult.ATE
            )

        //Snake clashed with itself, set result to CLASH
        if (bodyPositions.contains(nextPosition))
            return copy(
                bodyPositions = newBody,
                direction = directionAdjustedFor,
                moveResult = SnakeMoveResult.CLASH
            )

        //Normal move otherwise
        return copy(
            bodyPositions = newBody,
            direction = directionAdjustedFor,
            moveResult = SnakeMoveResult.OK
        )
    }
}

sealed class SnakeUiState {
    object Pregame : SnakeUiState()
    data class CountDown(val countdownText: String) : SnakeUiState()
    data class Playing(val foodPosition: SnakeFoodPosition) : SnakeUiState()
    data class GameOver(val score: Int) : SnakeUiState()
}

data class SnakeState(
    val direction: SnakeDirection = SnakeDirection.RIGHT,
    val bodyPositions: List<SnakeBodyPosition> = SnakeBodyPosition.getBoringDefault(),
    val moveResult: SnakeMoveResult = SnakeMoveResult.OK
)

enum class SnakeDirection {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(newDirection: SnakeDirection) =
        (this == RIGHT && newDirection == LEFT) ||
                (this == LEFT && newDirection == RIGHT) ||
                (this == UP && newDirection == DOWN) ||
                (this == DOWN && newDirection == UP)
}

enum class SnakeMoveResult {
    OK, //Moved in a valid position
    ATE, //Ate a fruit
    OB, //Moved out of bounds
    CLASH //Clashed with a part of the snak
}

data class SnakeBodyPosition(
    val x: Int,
    val y: Int
) {
    companion object {
        fun getBoringDefault() = //Moving Right!
            listOf(SnakeBodyPosition(4, 5), SnakeBodyPosition(3, 5), SnakeBodyPosition(2, 5))
    }
}

data class SnakeFoodPosition(val x: Int, val y: Int)
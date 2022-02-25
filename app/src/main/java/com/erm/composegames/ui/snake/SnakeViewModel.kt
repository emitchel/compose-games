package com.erm.composegames.ui.snake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SnakeViewModel(gridSize: Int = 10, stepsPerSecond: Int = 1, val snakeStartingSize: Int = 3) :
    ViewModel() {

    //TODO rig up via settings
    private val _gridSize = MutableStateFlow(gridSize)
    val gridSize: StateFlow<Int>
        get() = _gridSize.asStateFlow()

    //TODO rig up via settings
    private val _stepsPerSecond = MutableStateFlow(stepsPerSecond)
    fun updateStepsPerSecond(perSecond: Int) {
        _stepsPerSecond.value = perSecond
    }

    //TODO rig up
    private val _uiState = MutableStateFlow<SnakeUiState>(SnakeUiState.Pregame)
    val uiState: StateFlow<SnakeUiState>
        get() = _uiState.asStateFlow()

    private val _snakeState =
        MutableStateFlow(SnakeState.randomizeStartingPosition(snakeStartingSize, gridSize))
    val snakeState: StateFlow<SnakeState>
        get() = _snakeState.asStateFlow()

    //TODO rig up via settings
    private val _lastRequestedDirection = MutableStateFlow(snakeState.value.direction)
    fun requestDirection(direction: Direction) {
        _lastRequestedDirection.value = direction
    }

    fun start(reset: Boolean = false) = viewModelScope.launch {
        if (reset) _snakeState.value =
            SnakeState.randomizeStartingPosition(snakeStartingSize, gridSize.value)

        countDownToBegin()
        createNewFoodPosition()
        begingSnakeMovingJob()
    }

    private var snakeMovingJob: Job? = null
    private fun begingSnakeMovingJob() {
        snakeMovingJob = viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay((1 / _stepsPerSecond.value) * 1000L)
                progressUi()
            }
        }
    }

    private suspend fun countDownToBegin() {
        listOf("3", "2", "1", "Go!").forEach { text ->
            _uiState.value = SnakeUiState.CountDown(text)
            delay(1000)
        }
    }

    private fun createNewFoodPosition() {
        _uiState.value = SnakeUiState.Playing(Position.random(gridSize.value.also {
            Timber.d("Created new food position $it")
        }))
    }

    private fun progressUi() {
        Timber.d("Attempting to progress snake, current state ${_uiState.value}")
        (_uiState.value as? SnakeUiState.Playing)?.let {
            Timber.d("...Progressing")

            val newState = _snakeState.value.progress(
                _lastRequestedDirection.value,
                it.foodPosition
            )

            _snakeState.value = newState

            when (newState.moveResult) {
                SnakeMoveResult.OK -> {
                    //no op
                    Timber.d("Ok snake move")
                }
                SnakeMoveResult.ATE -> {
                    //TODO make noise? vibrate?
                    Timber.d("CRONCH!!")
                    createNewFoodPosition()
                }
                SnakeMoveResult.OB, SnakeMoveResult.CLASH -> {
                    Timber.d("GAME OVER (${newState.moveResult})")
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
        requestedDirection: Direction,
        foodPosition: Position,
        xMax: Int = 9,
        yMax: Int = 9
    ): SnakeState = run {

        //Don't update direction if it's the opposite
        val directionAdjustedFor =
            if (direction.isOpposite(requestedDirection)) direction else requestedDirection
        Timber.d("Moving $directionAdjustedFor")

        val head = bodyPositions.first()
        val nextPosition = when (directionAdjustedFor) {
            Direction.UP -> head.copy(x = head.x, y = head.y - 1)
            Direction.DOWN -> head.copy(x = head.x, y = head.y + 1)
            Direction.LEFT -> head.copy(x = head.x - 1, y = head.y)
            Direction.RIGHT -> head.copy(x = head.x + 1, y = head.y)
        }

        //Move body right away
        val newBody = (listOf(nextPosition) + bodyPositions).toMutableList()

        //If the LAST STATE wasn't ATE then remove the tail before evaluating next result
        if (moveResult != SnakeMoveResult.ATE) {
            Timber.d("Snake didn't eat, removing trailing tail!")
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

    companion object Factory {
        class Create(
            private val gridSize: Int,
            private val stepsPerSecond: Int,
            private val snakeStartingSize: Int
        ) :
            ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                SnakeViewModel(gridSize, stepsPerSecond, snakeStartingSize) as T
        }
    }
}
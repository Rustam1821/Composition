package com.example.composition.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase
import kotlin.concurrent.timer

class GameViewModel : ViewModel() {

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    private val _seconds = MutableLiveData<Int>()
    val seconds: LiveData<Int> = _seconds

    private val _gameFinished = MutableLiveData<GameResult>()
    val gameFinished: LiveData<GameResult> = _gameFinished

    private val _rightAnswersCount = MutableLiveData<Int>(0)
    val rightAnswersCount: LiveData<Int> = _rightAnswersCount

    private val _minRightAnswers = MutableLiveData<Int>(0)
    val minRightAnswers: LiveData<Int> = _minRightAnswers

    private val _rightAnswersPercent = MutableLiveData(0)
    val rightAnswersPercent: LiveData<Int> = _rightAnswersPercent


    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private var answersCount = 0
    private lateinit var gameSettings: GameSettings


    fun launchNextQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    fun getGameSettings(level: Level) {
        gameSettings = getGameSettingsUseCase(level)
        _seconds.value = gameSettings.gameTimeSeconds
        _minRightAnswers.value = gameSettings.rightAnswersMinCount
        launchTimer()
    }

    fun userAnswered(answer: Int) {
        answersCount++
        val rightAnswer = question.value?.let {
            it.sum - it.visibleNumber
        }
        if (answer == rightAnswer) {
            _rightAnswersCount.value = _rightAnswersCount.value?.plus(1)
        }
        _rightAnswersPercent.value = _rightAnswersCount.value?.times(100)?.div(answersCount)
        launchNextQuestion()
        println("---> right answers: $rightAnswersCount")
    }

    private fun launchTimer() {
        var secondsLeft: Int = _seconds.value ?: throw RuntimeException("Seconds are null")
        timer(initialDelay = 100L, period = 1000L) {
            if (secondsLeft > 0) {
                _seconds.postValue(secondsLeft--)
            } else {
                finishGame()
            }
        }
    }

    private fun finishGame() {
        val isWinner = (rightAnswersPercent.value?.compareTo(gameSettings.rightAnswersMinPercent) ?: -1) >= 0
        _gameFinished.postValue(
            GameResult(
                winner = isWinner,
                gameSettings = this.gameSettings,
                rightAnswersCount = this._rightAnswersCount.value ?: -1,
                questionsCount = answersCount
            )
        )
    }

}
package com.example.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase


class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String> = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    private val _rightAnswersPercent = MutableLiveData<Int>()
    val rightAnswersPercent: LiveData<Int> = _rightAnswersPercent

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String> = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean> = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean> = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int> = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult> = _gameResult

    private val context = application
    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null
    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    private lateinit var gameSettings: GameSettings
    private lateinit var level: Level

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    fun startGame(level: Level) {
        getGameSettings(level)
        startTimer()
        generateQuestion()
        updateProgress()
    }

    private fun getGameSettings(level: Level) {
        this.level = level
        gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.rightAnswersMinPercent
    }

    fun chooseAnswer(answer: Int) {
        checkAnswer(answer)
        updateProgress()
        generateQuestion()
    }

    private fun checkAnswer(answer: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (answer == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRightAnswers()
        _rightAnswersPercent.value = percent
        _progressAnswers.value = String.format(
            context.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.rightAnswersMinCount
        )
        _enoughCount.value = countOfRightAnswers >= gameSettings.rightAnswersMinCount
        _enoughPercent.value = percent >= gameSettings.rightAnswersMinPercent
    }

    private fun calculatePercentOfRightAnswers(): Int {
        return if (countOfQuestions == 0) {
            0
        } else {
            (countOfRightAnswers / countOfQuestions.toDouble() * 100).toInt()
        }
    }


    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeSeconds * MILLIS_IN_SECONDS, MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTE
        val leftSeconds = seconds % SECONDS_IN_MINUTE
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            winner = enoughCount.value == true && enoughPercent.value == true,
            gameSettings = gameSettings,
            rightAnswersCount = countOfRightAnswers,
            questionsCount = countOfQuestions
        )
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTE = 60
    }

}
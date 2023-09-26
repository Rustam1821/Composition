package com.example.composition.domain.entity

data class GameResults(
    val winner: Boolean,
    val rightAnswersCount: Int,
    val questionsCount: Int,
    val gameSettings: GameSettings,
)

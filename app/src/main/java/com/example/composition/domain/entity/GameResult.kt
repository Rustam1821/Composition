package com.example.composition.domain.entity

import java.io.Serializable

data class GameResult(
    val winner: Boolean,
    val rightAnswersCount: Int,
    val questionsCount: Int,
    val gameSettings: GameSettings,
) : Serializable

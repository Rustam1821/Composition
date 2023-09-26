package com.example.composition.domain.entity

data class GameSettings(
    val maxSumValue: Int,
    val rightAnswersMinCount: Int,
    val rightAnswersMinPercent: Int,
    val gameTimeSeconds: Int,
)

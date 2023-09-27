package com.example.composition.domain.entity

import java.io.Serializable

data class GameSettings(
    val maxSumValue: Int,
    val rightAnswersMinCount: Int,
    val rightAnswersMinPercent: Int,
    val gameTimeSeconds: Int,
) : Serializable

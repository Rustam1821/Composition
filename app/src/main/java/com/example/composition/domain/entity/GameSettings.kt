package com.example.composition.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSettings(
    val maxSumValue: Int,
    val rightAnswersMinCount: Int,
    val rightAnswersMinPercent: Int,
    val gameTimeSeconds: Int,
) : Parcelable {
    val rightAnswersMinCountString = rightAnswersMinCount.toString()
    val rightAnswersMinPercentString = rightAnswersMinPercent.toString()
}

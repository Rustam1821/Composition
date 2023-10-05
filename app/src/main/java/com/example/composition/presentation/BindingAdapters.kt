package com.example.composition.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entity.GameResult

interface OnOptionClickListener {
    fun onClick(option: Int)
}

@BindingAdapter("requiredAnswers")
fun bindRequiredAnswers(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_score),
        count
    )
}

@BindingAdapter("scoreAnswers")
fun bindScore(textView: TextView, score: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.score_answers),
        score
    )
}

@BindingAdapter("requiredPercentage")
fun bindRequiredPercentage(textView: TextView, percentage: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_percentage),
        percentage
    )
}

@BindingAdapter("scorePercentage")
fun bindScorePercentage(textView: TextView, gameResult: GameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.score_percentage),
        gameResult.rightAnswerPercent
    )
}

@BindingAdapter("emojiResult")
fun bindEmojiResult(imageView: ImageView, winner: Boolean) {
    imageView.setImageResource(getSmileResId(winner))
}

@BindingAdapter("numberAsText")
fun bindNumberAsText(textView: TextView, number: Int) {
    textView.text = number.toString()
}


@BindingAdapter("enoughCount")
fun bindEnoughCount(textView: TextView, enoughCount: Boolean) {
    val color = getColorByState(enoughCount, textView.context)
    textView.setTextColor(color)
}

@BindingAdapter("enoughPercent")
fun bindEnoughPercent(progressBar: ProgressBar, enoughPercent: Boolean) {
    val colorId = getColorByState(enoughPercent, progressBar.context)
    progressBar.progressTintList = ColorStateList.valueOf(colorId)
}

@BindingAdapter("onOptionClickListener")
fun bindOnOptionClickListener(textView: TextView, opOptionClickListener: OnOptionClickListener) {
    textView.setOnClickListener {
        opOptionClickListener.onClick(textView.text.toString().toInt())
    }
}


private fun getSmileResId(winner: Boolean): Int {
    return if (winner) R.drawable.ic_smile else R.drawable.ic_sad
}

private fun getColorByState(goodState: Boolean?, context: Context): Int {
    val colorId = if (goodState == true) android.R.color.holo_green_light else android.R.color.holo_red_light
    return ContextCompat.getColor(context, colorId)
}
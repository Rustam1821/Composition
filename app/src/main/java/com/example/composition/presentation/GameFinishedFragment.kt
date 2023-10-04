package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding

class GameFinishedFragment : Fragment() {

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")

    private val args by navArgs<GameFinishedFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
        bindViews()
    }

    private fun bindViews() {
        with(binding) {
            emojiResult.setImageResource(getSmileResId())
            tvRequiredAnswers.text = String.format(
                getString(R.string.required_score), args.gameResult.gameSettings.rightAnswersMinCount
            )
            tvScoreAnswers.text = String.format(
                getString(R.string.score_answers), args.gameResult.rightAnswersCount
            )
            tvRequiredPercentage.text = String.format(
                getString(R.string.required_percentage), args.gameResult.gameSettings.rightAnswersMinPercent
            )
            tvScorePercentage.text = String.format(
                getString(R.string.score_percentage), args.gameResult.rightAnswerPercent
            )
        }
    }

    private fun getSmileResId(): Int {
        return if (args.gameResult.winner) R.drawable.ic_smile else R.drawable.ic_sad
    }

    private fun setUpClickListeners() {
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }
}
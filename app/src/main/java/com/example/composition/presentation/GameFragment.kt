package com.example.composition.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import java.lang.Integer.parseInt

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    private lateinit var viewModel: GameViewModel

    private lateinit var level: Level

    private var minRightAnswers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        viewModel.getGameSettings(level)
        viewModel.launchNextQuestion()

        setUpListeners()
        observeViewModel()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
        Log.e("--->", "level is: $level")
    }

    private fun observeViewModel() {
        viewModel.seconds.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it.toString()
        }
        viewModel.question.observe(viewLifecycleOwner) {
            updateViews(it)
        }
        viewModel.rightAnswersCount.observe(viewLifecycleOwner) {
            binding.tvAnswersProgress.text =
                getString(R.string.progress_answers, it.toString(), minRightAnswers.toString())
        }
        viewModel.rightAnswersPercent.observe(viewLifecycleOwner) {
            binding.progressBar.progress = it
        }
        viewModel.minRightAnswers.observe(viewLifecycleOwner) {
            minRightAnswers = it
            binding.tvAnswersProgress.text = getString(R.string.progress_answers, "0", it.toString())
        }
        viewModel.gameFinished.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(
                it
            )
        }
    }

    private fun updateViews(question: Question) {
        with(binding) {
            val options = listOf(
                tvOption1, tvOption2, tvOption3, tvOption4, tvOption5, tvOption6
            )
            tvSum.text = question.sum.toString()
            tvLeftNumber.text = question.visibleNumber.toString()
            options.forEachIndexed { index, textView ->
                textView.text = question.options[index].toString()
            }
        }
    }

    private fun setUpListeners() {
        with(binding) {
            val options = listOf(
                tvOption1, tvOption2, tvOption3, tvOption4, tvOption5, tvOption6
            )
            options.forEach { textView ->
                textView.setOnClickListener {
                    viewModel.userAnswered(parseInt(textView.text.toString()))
                }
            }
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    companion object {

        const val NAME = "GameFragment"

        private const val KEY_LEVEL = "level"
        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }

}
package com.example.learnahead_prototyp.Goal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.databinding.FragmentGoalBinding
import com.example.learnahead_prototyp.databinding.FragmentGoalTestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalTestFragment : Fragment() {

    val TAG: String = "FragmentGoalTestBinding"
    lateinit var binding: FragmentGoalTestBinding
    val viewModel: GoalViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getGoals()
        viewModel.goal.observe(viewLifecycleOwner) {
            it.forEach{
                Log.e(TAG, it.toString())
            }
        }
    }
}
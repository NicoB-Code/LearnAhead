package com.example.learnahead_prototyp.Goal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.databinding.FragmentGoalListingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalListingFragment : Fragment() {

    val TAG: String = "GoalListingFragment"
    lateinit var binding: FragmentGoalListingBinding
    val viewModel: GoalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalListingBinding.inflate(layoutInflater)
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
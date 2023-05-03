package com.example.learnahead_prototyp.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.databinding.FragmentGoalDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalDetailFragment : Fragment() {

    val TAG: String = "GoalDetailFragment"
    lateinit var binding: FragmentGoalDetailBinding
    val viewModel: GoalViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
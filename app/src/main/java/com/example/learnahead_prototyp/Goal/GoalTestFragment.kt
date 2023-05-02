package com.example.learnahead_prototyp.Goal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.databinding.FragmentGoalBinding
import com.example.learnahead_prototyp.databinding.FragmentGoalTestBinding

class GoalTestFragment : Fragment() {

    val TAG: String = "FragmentGoalTestBinding"
    lateinit var binding: FragmentGoalTestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
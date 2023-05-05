package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentGoalDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class GoalDetailFragment : Fragment() {

    val TAG: String = "GoalDetailFragment"
    lateinit var binding: FragmentGoalDetailBinding
    val viewModel: GoalViewModel by viewModels()
    var isEdit = false
    var objGoal: Goal? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UpdateUI()
        binding.button.setOnClickListener {
            if(isEdit) {
                updateGoal()
            } else {
                createGoal()
            }
        }
    }

    private fun createGoal() {
        if (validation()) {
            viewModel.addGoal(
                Goal(
                    id = "",
                    description = binding.goalDescription.text.toString(),
                    endDate = Date()
                )
            )
        }
        viewModel.addGoal.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                    binding.button.text = ""
                }
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Create"
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Create"
                    toast(state.data)
                }
            }
        }
    }

    private fun updateGoal() {
        if (validation()) {
            viewModel.updateGoal(
                Goal(
                    id = objGoal?.id ?: "",
                    description = binding.goalDescription.text.toString(),
                    endDate = Date()
                )
            )
        }
        viewModel.updateGoal.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                    binding.button.text = ""
                }
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Update"
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Update"
                    toast(state.data)
                }
            }
        }
    }
    private fun UpdateUI() {
        val type = arguments?.getString("type", null)
        type?.let {
            when(it) {
                "view" -> {
                    isEdit = false
                    binding.goalDescription.isEnabled = true
                    objGoal = arguments?.getParcelable("goal")
                    binding.goalDescription.setText(objGoal?.description)
                    binding.button.hide()
                }
                "create" -> {
                    isEdit = false
                    binding.button.setText("Create")
                }
                "edit" -> {
                    isEdit = true
                    objGoal = arguments?.getParcelable("goal")
                    binding.goalDescription.setText(objGoal?.description)
                    binding.button.setText("Update")
                }
            }
        }
    }
    private fun validation(): Boolean {
        var isValid = true

        if(binding.goalDescription.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("Enter description")
        }

        return  isValid
    }
}
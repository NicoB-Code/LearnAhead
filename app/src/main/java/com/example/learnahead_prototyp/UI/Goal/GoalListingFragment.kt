package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentGoalListingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalListingFragment : Fragment() {

    val TAG: String = "GoalListingFragment"
    lateinit var binding: FragmentGoalListingBinding
    val viewModel: GoalViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    var deletePosition: Int = -1
    var list: MutableList<Goal> = arrayListOf()
    val adapter by lazy {
        GoalListingAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(R.id.action_goalListingFragment_to_goalDetailFragment, Bundle().apply {
                    putString("type", "view")
                    putParcelable("goal", item)
                })
            },
            onEditClicked = { pos, item ->
                findNavController().navigate(R.id.action_goalListingFragment_to_goalDetailFragment, Bundle().apply {
                    putString("type", "edit")
                    putParcelable("goal", item)
                })
            },
            onDeleteClicked = { pos, item ->
                deletePosition = pos
                viewModel.deleteGoal(item)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalListingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Observer()
        binding.recyclerView.adapter = adapter
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_goalListingFragment_to_goalDetailFragment, Bundle().apply {
                putString("type", "create")
            })
        }
        viewModel.getGoals()

        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_goalListingFragment_to_loginFragment)
            }
        }
    }

    private fun Observer() {
        viewModel.goal.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    list = state.data.toMutableList()
                    adapter.updateList(list)
                }
            }
        }
        viewModel.deleteGoal.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    toast(state.data)
                    if(deletePosition != -1) {
                        list.removeAt(deletePosition)
                        adapter.updateList(list)
                    }
                }
            }
        }
    }
}
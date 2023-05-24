package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentLearningCategoryInnerViewBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [LearningCategoryListFragment] ist für die Anzeige der Liste der Lernkategorien zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einer Lernkategorie anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class LearningCategoryInnerViewFragment : Fragment() {

    private var currentUser: User? = null

    val TAG: String = "LearningCategoryListFragment"

    lateinit var binding: FragmentLearningCategoryInnerViewBinding
    private val learnCategoryViewModel: LearnCategoryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val summaryViewModel: SummaryViewModel by viewModels()
    private var deletePosition: Int = -1
    var list: MutableList<Summary> = arrayListOf()
    private var currentLearningCategory: LearningCategory? = null

    private val adapter by lazy {
        LearningCategoryInnerViewAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_learningCategoryListFragment_to_learningCategoryInnerViewFragment,
                    Bundle().apply {
                        putString("type", "view")
                        putParcelable("learning_category", item)
                    })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningCategoryInnerViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        setLocalCurrentUser()
        updateUI()
        setEventListener()

        // Set the adapter to the RecyclerView
        binding.recyclerView.adapter = adapter
    }

    private fun updateUI() {
        currentLearningCategory = arguments?.getParcelable("learning_category")
        if (currentLearningCategory != null)
            binding.learningGoalMenuHeaderLabel.text = currentLearningCategory!!.name
    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    private fun setEventListener() {
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_homeFragment) }

        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_goalListingFragment) }

        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_learningCategoryListFragment) }

        binding.logout.setOnClickListener { authViewModel.logout { findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_loginFragment) } }

        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_learningCategoryListFragment) }

    }

    private fun observer() {
        summaryViewModel.summary.observe(viewLifecycleOwner) { state ->
            when (state) {
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

        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    currentUser = state.data
                    if (currentLearningCategory != null)
                        summaryViewModel.getSummaries(currentUser, currentLearningCategory!!)
                }
            }
        }
    }
}

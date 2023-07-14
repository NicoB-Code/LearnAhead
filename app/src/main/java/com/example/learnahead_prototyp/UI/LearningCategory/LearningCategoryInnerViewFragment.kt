package com.example.learnahead_prototyp.UI.LearningCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.Summary.SummaryViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.getRandomLerntipp
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentLearningCategoryInnerViewBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [LearningCategoryInnerViewFragment] ist für die Anzeige der Details einer Lernkategorie zuständig.
 * Es zeigt eine Liste der Zusammenfassungen der Lernziele an, die zu dieser Lernkategorie gehören.
 * Diese Klasse ist mit [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class LearningCategoryInnerViewFragment : Fragment() {

    // Deklaration der Variablen
    private var currentUser: User? = null
    private lateinit var binding: FragmentLearningCategoryInnerViewBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private var currentLearningCategory: LearningCategory? = null

    /**
     * Erstellt die View-Hierarchie des Fragments.
     * @param inflater Der LayoutInflater, der verwendet wird, um die View-Hierarchie aufzubauen.
     * @param container Der ViewGroup, in die die View-Hierarchie eingefügt wird.
     * @param savedInstanceState Das Bundle, das den Zustand des Fragments enthält.
     * @return Die View-Hierarchie des Fragments.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningCategoryInnerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Wird aufgerufen, wenn die View-Hierarchie des Fragments erstellt wurde.
     * @param view Die View, die das Fragment darstellt.
     * @param savedInstanceState Das Bundle, das den Zustand des Fragments enthält.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setzt die Event-Listener, beobachtet die LiveData-Objekte und aktualisiert die UI
        setEventListener()
        observer()
        setLocalCurrentUser()
        updateUI()
    }

    /**
     * Aktualisiert die UI des Fragments.
     */
    private fun updateUI() {
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.learningGoalMenuHeaderLabel.text = currentLearningCategory?.name
        binding.learningTipOfTheDay.text = getRandomLerntipp()
    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Setzt die Event-Listener für die Buttons und Views des Fragments.
     */
    private fun setEventListener() {
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_homeFragment)
        }

        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_goalListingFragment)
        }

        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_learningCategoryListFragment)
        }

        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_loginFragment)
            }
        }

        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_learningCategoryListFragment)
        }

        binding.buttonTestsAndQuestions.setOnClickListener {
            if (binding.buttonQuestions.visibility != View.VISIBLE && binding.buttonTests.visibility != View.VISIBLE) {
                binding.buttonSummaries.visibility = View.GONE
                binding.buttonTestsAndQuestions.visibility = View.GONE
                binding.buttonQuestions.visibility = View.VISIBLE
                binding.buttonTests.visibility = View.VISIBLE

                val layoutParams =
                    binding.rectangleLearningTipBox.layoutParams as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.BELOW, binding.buttonTests.id)
                binding.rectangleLearningTipBox.layoutParams = layoutParams
            }
        }

        binding.buttonTests.setOnClickListener {
            val selectedLearningCategory = currentLearningCategory
            learnCategoryViewModel.setCurrentSelectedLearningCategory(selectedLearningCategory)
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_testListingFragment)
        }

        binding.buttonQuestions.setOnClickListener {
            val selectedLearningCategory = currentLearningCategory
            learnCategoryViewModel.setCurrentSelectedLearningCategory(selectedLearningCategory)
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_questionListingFragment)
        }

        binding.buttonSummaries.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_summaryFragment,
                Bundle().apply {
                    putString("type", "view")
                    putParcelable("learning_category", currentLearningCategory)
                })
        }
    }

    /**
     * Beobachtet die LiveData-Objekte der ViewModels und aktualisiert die UI entsprechend.
     */
    private fun observer() {
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Failure -> {
                    toast(state.error)
                    View.GONE
                }
                is UiState.Success -> {
                    currentUser = state.data
                    currentLearningCategory?.let {
                        summaryViewModel.getSummaries(currentUser, it)
                    }
                    View.GONE
                }
            }
        }
    }
}

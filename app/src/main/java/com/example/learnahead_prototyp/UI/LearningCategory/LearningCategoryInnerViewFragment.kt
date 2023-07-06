package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.util.Log
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
        // Inflate the layout for this fragment
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
        setLocalCurrentUser()
        updateUI()
    }

    /**
     * Aktualisiert die UI des Fragments.
     */
    private fun updateUI() {
        // Holt die Lernkategorie aus den Argumenten und setzt den Text des Labels
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.learningGoalMenuHeaderLabel.text = currentLearningCategory?.name
    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank.
     */
    private fun setLocalCurrentUser() {
        // Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable currentUser
        authViewModel.getSession()
    }

    /**
     * Setzt die Event-Listener für die Buttons und Views des Fragments.
     */
    private fun setEventListener() {
        // Setzt den Event-Listener für den Home-Button
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_homeFragment)
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.buttonLearningGoals.setOnClickListener {
            //findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_goalListingFragment)
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_summaryFragment)

        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_learningCategoryListFragment)
        }

        binding.buttonSummaries.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_summaryFragment,
                Bundle().apply {
                    putString("type","view")
                    putParcelable("learning_category", currentLearningCategory)
                })
        }
    }


}
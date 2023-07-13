package com.example.learnahead_prototyp.UI.Summary

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.Goal.SummaryViewModel
import com.example.learnahead_prototyp.databinding.FragmentInnerSummaryBinding
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.Data.Model.Summary
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.R
import androidx.core.widget.doAfterTextChanged
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import io.noties.markwon.Markwon


@AndroidEntryPoint
class innerSummaryFragment : Fragment() {

    private var currentUser: User? = null
    private var currentLearningCategory: LearningCategory?= null
    lateinit var binding: FragmentInnerSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var currentSummary: Summary? = null
    private var markdownInput: String = ""

    /**
     * Erstellt die View und gibt sie zurück.
     * @param inflater Der LayoutInflater, der verwendet wird, um die View zu erstellen.
     * @param container Der ViewGroup-Container, der die View enthält.
     * @param savedInstanceState Das Bundle-Objekt, das den zuletzt gespeicherten Zustand enthält.
     * @return Die erstellte View.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInnerSummaryBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Initialisiert die View und setzt die Listener auf die Schaltflächen. Ruft die UpdateUI()-Methode auf,
     * um die View mit den vorhandenen Daten zu aktualisieren.
     * @param view Die erstellte View.
     * @param savedInstanceState Das Bundle-Objekt, das den zuletzt gespeicherten Zustand enthält.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentSummary = arguments?.getParcelable("summary")
        setLocalCurrentUser()
        setEventListener()
        updateUI()
        observer()
    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    private fun updateUI() {
        // Holt die Lernkategorie aus den Argumenten und setzt den Text des Labels
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.learningGoalMenuHeaderLabel.text = currentLearningCategory?.name
        if(currentSummary?.content != null) {
            binding.markdownEditText.setText(currentSummary?.content)
        } else {
            binding.markdownEditText.setText("# Hello Zusammenfassung")
        }
    }



    private fun observer() {
        // Eine Beobachtung auf viewModel.updateLearningCategory ausführen
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.progressBar.hide()
                    currentUser = state.data
                }
            }
        }
        summaryViewModel.updateSummary.observe(viewLifecycleOwner) {state ->
            when (state) {
                is UiState.Loading -> {
                    // Fortschrittsanzeige anzeigen
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    // Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    if(state.data != null && currentUser != null) {
                        // Die neue Zusammenfassung dem User hinzufügen
                        var foundSummaryIndex: Int = 0
                        for (category in currentUser!!.learningCategories){
                            foundSummaryIndex = category!!.summaries.indexOfFirst { it.id == state.data.id }
                        }
                        currentLearningCategory?.summaries?.set(foundSummaryIndex, state.data)
                        val foundIndex =
                            currentUser!!.learningCategories.indexOfFirst { it.id == currentLearningCategory?.id }
                        if (foundIndex != -1) {
                            currentUser!!.learningCategories[foundIndex] = currentLearningCategory!!
                        }
                        // Den User in der DB updaten
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(R.id.action_innerSummaryFragment_to_summaryPreviewFragment,
                            Bundle().apply {
                                putParcelable("summary", currentSummary)
                                putParcelable("learning_category", currentLearningCategory)
                            })
                        toast("Die Zusammenfassung konnte erfolgreich aktualisiert werden")
                    }
                    else {
                        toast("Die Zusammenfassung konnte nicht aktualisiert werden")
                    }

                }
            }
        }
    }


    /**
     * Erstellt alle notwendigen EventListener für das Fragment
     */
    private fun setEventListener() {
        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_goalListingFragment) }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_innerSummaryFragment_to_loginFragment)
            }
        }

        binding.buttonPreview.setOnClickListener {
            currentSummary?.content = binding.markdownEditText.text.toString()
            currentSummary?.let { summary -> summaryViewModel.updateSummary(summary) }
        }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_summaryFragment,
            Bundle().apply {
                putParcelable("summary", currentSummary)
                putParcelable("learning_category", currentLearningCategory)
            })
        }
    }


}
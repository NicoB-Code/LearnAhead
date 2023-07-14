package com.example.learnahead_prototyp.UI.LearningCategory.Summary

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
import com.example.learnahead_prototyp.databinding.FragmentInnerSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher


/**
 * Ein Fragment, das die innere Zusammenfassung darstellt.
 * Es ermöglicht Benutzern das Anzeigen und Bearbeiten von Zusammenfassungen einer Lernkategorie.
 * Benutzer können den Inhalt der Zusammenfassung bearbeiten und Änderungen speichern.
 */
@AndroidEntryPoint
class InnerSummaryFragment : Fragment() {

    private var currentUser: User? = null
    private var currentLearningCategory: LearningCategory? = null
    lateinit var binding: FragmentInnerSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var currentSummary: Summary? = null

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
     * Initialisiert die View und setzt die Listener auf die Schaltflächen.
     * Ruft die updateUI()-Methode auf, um die View mit den vorhandenen Daten zu aktualisieren.
     * Ruft die observer()-Methode auf, um die Observer zu initialisieren.
     * Ruft die initEditor()-Methode auf, um den Texteditor zu initialisieren.
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
        initEditor()
    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Aktualisiert die Benutzeroberfläche basierend auf den vorhandenen Daten.
     * Setzt den Text des Labels mit dem Namen der aktuellen Lernkategorie.
     * Setzt den Text des Texteditors mit dem Inhalt der aktuellen Zusammenfassung,
     * oder einen Standardtext, wenn keine Zusammenfassung vorhanden ist.
     */
    private fun updateUI() {
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.learningGoalMenuHeaderLabel.text = currentLearningCategory?.name
        if (currentSummary?.content != null) {
            binding.markdownEditText.setText(currentSummary?.content)
        } else {
            binding.markdownEditText.setText("# Hello Zusammenfassung")
        }
    }

    /**
     * Initialisiert den Texteditor für die Bearbeitung des Zusammenfassungsinhalts.
     */
    private fun initEditor() {
        val markwon = context?.let { Markwon.create(it) }
        val editor = markwon?.let { MarkwonEditor.create(it) }
        binding.markdownEditText.addTextChangedListener(
            editor?.let {
                MarkwonEditorTextWatcher.withProcess(
                    editor
                )
            }
        )
    }

    /**
     * Initialisiert die Observer für die Beobachtung des aktuellen Benutzers und der Zusammenfassungsaktualisierung.
     * Der Observer für currentUser überwacht den Zustand des aktuellen Benutzers und führt entsprechende Aktionen aus.
     * Der Observer für updateSummary überwacht den Zustand der Aktualisierung einer Zusammenfassung und führt entsprechende Aktionen aus.
     */
    private fun observer() {
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
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
                    currentUser = state.data
                }
            }
        }

        summaryViewModel.updateSummary.observe(viewLifecycleOwner) { state ->
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
                    if (state.data != null && currentUser != null) {
                        // Die neue Zusammenfassung dem Benutzer hinzufügen
                        var foundSummaryIndex: Int = 0
                        for (category in currentUser!!.learningCategories) {
                            foundSummaryIndex =
                                category!!.summaries.indexOfFirst { it.id == state.data.id }
                        }
                        currentLearningCategory?.summaries?.set(foundSummaryIndex, state.data)
                        val foundIndex =
                            currentUser!!.learningCategories.indexOfFirst { it.id == currentLearningCategory?.id }
                        if (foundIndex != -1) {
                            currentUser!!.learningCategories[foundIndex] = currentLearningCategory!!
                        }
                        // Den Benutzer in der Datenbank aktualisieren
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(
                            R.id.action_innerSummaryFragment_to_summaryPreviewFragment,
                            Bundle().apply {
                                putParcelable("summary", currentSummary)
                                putParcelable("learning_category", currentLearningCategory)
                            }
                        )
                        toast("Die Zusammenfassung konnte erfolgreich aktualisiert werden")
                    } else {
                        toast("Die Zusammenfassung konnte nicht aktualisiert werden")
                    }
                }
            }
        }
    }

    /**
     * Erstellt alle notwendigen EventListener für das Fragment.
     * Setzt die Listener für die Schaltflächen und führt die entsprechenden Aktionen aus.
     */
    private fun setEventListener() {
        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_goalListingFragment) }

        // Klick Listener zum Ausloggen des Benutzers
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_innerSummaryFragment_to_loginFragment)
            }
        }

        // Klick Listener zum Speichern der Zusammenfassung
        binding.buttonPreview.setOnClickListener {
            currentSummary?.content = binding.markdownEditText.text.toString()
            currentSummary?.let { summary -> summaryViewModel.updateSummary(summary) }
        }

        // Klick Listener zum Zurücknavigieren zur Zusammenfassungsliste
        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_summaryFragment,
            Bundle().apply {
                putParcelable("summary", currentSummary)
                putParcelable("learning_category", currentLearningCategory)
            })
        }
    }
}

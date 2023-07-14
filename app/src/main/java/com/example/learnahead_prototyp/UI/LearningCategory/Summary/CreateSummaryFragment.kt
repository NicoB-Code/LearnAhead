package com.example.learnahead_prototyp.UI.LearningCategory.Summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
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
import com.example.learnahead_prototyp.databinding.FragmentCreateSummaryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateSummaryFragment : Fragment() {

    private var currentUser: User? = null
    private var currentLearningCategory: LearningCategory? = null
    lateinit var binding: FragmentCreateSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var isEdit = false
    private var objSummary: Summary? = null

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
        binding = FragmentCreateSummaryBinding.inflate(layoutInflater)
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
        observer()
        setLocalCurrentUser()
        updateUI()
        setEventListeners()
    }

    /**
     * Ruft die Session des aktuellen Benutzers ab und speichert sie lokal.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }


    /**
     * Aktiviert oder deaktiviert die Benutzeroberfläche der Lernkategorien, indem die `isEnabled`-Eigenschaften
     * der betreffenden Ansichtselemente auf den übergebenen Wert festgelegt werden. Wenn `isDisable` auf
     * `true` gesetzt ist, wird die Benutzeroberfläche deaktiviert, andernfalls wird sie aktiviert.
     * @param isDisable Ein boolescher Wert, der angibt, ob die Benutzeroberfläche deaktiviert werden soll
     *                  (`true`) oder nicht (`false`). Der Standardwert ist `false`.
     */
    private fun isMakeEnableUI(isDisable: Boolean) {
        binding.summaryNameEditText.isEnabled = isDisable
    }

    /**
     * Erstellt alle notwendigen EventListener für das Fragment.
     */
    private fun setEventListeners() {
        binding.saveButton.setOnClickListener {
            createSummary()
        }

        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_createSummaryFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.learningCategoriesButton.setOnClickListener { findNavController().navigate(R.id.action_createSummaryFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.learningGoalsButton.setOnClickListener { findNavController().navigate(R.id.action_createSummaryFragment_to_goalListingFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.backIcon.setOnClickListener { findNavController().navigate(
            R.id.action_createSummaryFragment_to_summaryFragment,
            Bundle().apply {
                putString("type", "view")
                putParcelable("learning_category", currentLearningCategory)
            })
        }

        // Event Listener wenn sich der Titel verändert
        binding.summaryNameEditText.doAfterTextChanged {
            updateButtonVisibility()
        }
    }

    /**
     * Überprüft, ob der Name ausgefüllt ist.
     * Wenn das Feld ausgefüllt ist, soll der Button zum Speichern sichtbar sein.
     * Ist das Feld nicht ausgefüllt, soll der Speichern-Button nicht sichtbar sein.
     */
    private fun updateButtonVisibility() {
        val title = binding.summaryNameEditText.text.toString().trim()

        if (title.isNotEmpty())
            binding.saveButton.show()
        else
            binding.saveButton.hide()
    }

    /**
     * Aktualisiert die Benutzeroberfläche basierend auf dem Typ des übergebenen Arguments.
     */
    private fun updateUI() {
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.summaryMenuHeaderLabel.text = currentLearningCategory?.name
        binding.summaryNameEditText.setText("")
        binding.saveButton.hide()
        binding.editButton.hide()
        //binding.delete.hide()
        isMakeEnableUI(true)
        binding.summaryNameEditText.requestFocus()
    }

    /**
     * Überprüft, ob die Zusammenfassung korrekt eingegeben wurde oder nicht.
     * @return Boolean
     */
    private fun validation(): Boolean {
        // Überprüfen, ob der Name der Zusammenfassung leer oder null ist
        if (binding.summaryNameEditText.text.isNullOrEmpty()) {
            // Wenn ja, eine Toast-Nachricht ausgeben und false zurückgeben
            toast("Geben Sie einen Namen ein")
            return false
        }
        // Andernfalls true zurückgeben
        return true
    }

    /**
     * Erstellt eine Zusammenfassung, indem es eine Beobachtung auf viewModel.addSummary ausführt und je nach Zustand der Beobachtung
     * verschiedene Aktionen durchführt, wie z.B. Anzeigen oder Ausblenden des Fortschrittsindikators und Anzeigen einer Toast-Nachricht
     * in Abhängigkeit vom Zustand. Wenn die Eingabevalidierung erfolgreich ist, wird die Zusammenfassung an viewModel.addSummary übergeben.
     */
    private fun createSummary() {
        // Wenn die Eingabevalidierung erfolgreich ist, die Zusammenfassung an viewModel.addSummary übergeben
        if (validation()) {
            summaryViewModel.addSummary(getSummary())
        }
    }

    /**
     * Erstellt eine Zusammenfassung basierend auf den aktuellen Eingaben und gibt sie zurück.
     * @return Die erstellte Zusammenfassung.
     */
    private fun getSummary(): Summary {
        // Erstellt ein Summary-Objekt mit den aktuellen Eingaben
        return Summary(
            id = objSummary?.id ?: "",
            name = binding.summaryNameEditText.text.toString(),
        )
    }

    /**
     * Initialisiert die Observer für die Beobachtung von addSummary und currentUser.
     * Der Observer für addSummary überwacht den Zustand der Hinzufügung einer Zusammenfassung.
     * Je nach Zustand werden verschiedene Aktionen ausgeführt, wie das Anzeigen oder Ausblenden des Fortschrittsindikators
     * und das Anzeigen einer Toast-Nachricht. Wenn die Hinzufügung erfolgreich ist, wird die Zusammenfassung zur aktuellen
     * Lernkategorie hinzugefügt und der Benutzer in der Datenbank aktualisiert.
     *
     * Der Observer für currentUser überwacht den Zustand des aktuellen Benutzers.
     * Je nach Zustand werden verschiedene Aktionen ausgeführt, wie das Anzeigen oder Ausblenden des Fortschrittsindikators
     * und das Anzeigen einer Toast-Nachricht. Wenn der Zustand erfolgreich ist, wird der aktuelle Benutzer lokal gespeichert.
     */
    private fun observer() {
        summaryViewModel.addSummary.observe(viewLifecycleOwner) { state ->
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
                    if (state.data != null && currentUser != null) {
                        // Die neue Zusammenfassung zur Lernkategorie hinzufügen
                        currentLearningCategory?.summaries?.add(state.data)
                        val foundIndex =
                            currentUser!!.learningCategories.indexOfFirst { it.id == currentLearningCategory?.id }
                        if (foundIndex != -1) {
                            currentUser!!.learningCategories[foundIndex] = currentLearningCategory!!
                        }
                        // Den Benutzer in der Datenbank aktualisieren
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(
                            R.id.action_createSummaryFragment_to_summaryFragment,
                            Bundle().apply {
                                putString("type", "view")
                                putParcelable("learning_category", currentLearningCategory)
                            }
                        )
                        toast("Die Zusammenfassung wurde erfolgreich erstellt")
                    } else {
                        toast("Die Zusammenfassung konnte nicht erstellt werden")
                    }
                }
            }
        }

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
    }
}

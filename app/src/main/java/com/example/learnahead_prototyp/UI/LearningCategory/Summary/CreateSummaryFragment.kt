package com.example.learnahead_prototyp.UI.LearningCategory.Summary

import android.os.Bundle
import android.util.Log
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
    private var currentLearningCategory: LearningCategory?= null
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
        setEventListener()
    }

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
        binding.textSummaryName.isEnabled = isDisable
    }

    /**
     * Erstellt alle notwendigen EventListener für das Fragment
     */
    private fun setEventListener() {
        binding.saveButton.setOnClickListener {
            createSummary()
        }

        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_createSummaryFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_createSummaryFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_createSummaryFragment_to_goalListingFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_createSummaryFragment_to_summaryFragment,
            Bundle().apply {
                putString("type","view")
                putParcelable("learning_category", currentLearningCategory)
            })
        }

        // Event Listener wenn sich der Titel verändert
        binding.textSummaryName.doAfterTextChanged {
            Log.d("Test", "Hallo")
            updateButtonVisibility()
        }
    }

    /**
     * Überprüft ob der Name ausgefüllt ist.
     * Wenn das Feld ausgefüllt ist, dann soll der Button zum Speichern sichtbar sein.
     * Ist das Feld nicht ausgefüllt, dann soll der Speichern Button nicht sichtbar sein.
     */
    private fun updateButtonVisibility() {
        val title = binding.textSummaryName.text.toString().trim()

        if (title.isNotEmpty())
            binding.saveButton.show()
        else
            binding.saveButton.hide()
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
     * Methode zum Aktualisieren der Benutzeroberfläche basierend auf dem Typ des übergebenen Arguments.
     * @return Nothing
     */
    private fun updateUI() {
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.createSummaryMenuHeaderLabel.text = currentLearningCategory?.name
        binding.textSummaryName.setText("")
        binding.saveButton.hide()
        binding.editButton.hide()
        //binding.delete.hide()
        isMakeEnableUI(true)
        binding.textSummaryName.requestFocus()
    }

    /**
     * Methode zur Überprüfung, ob die Zusammenfassung korrekt eingegeben wurde oder nicht.
     * @return Boolean
     */
    private fun validation(): Boolean {
        // Überprüfen, ob der Lernkategorie Name leer oder null ist
        if (binding.textSummaryName.text.isNullOrEmpty()) {
            // Wenn ja, eine Toast-Meldung ausgeben und false zurückgeben
            toast("Enter name")
            return false
        }
        // Andernfalls true zurückgeben
        return true
    }

    /**
     * Methode zum Erstellen einer neuen Lernkategorie oder Aktualisieren einer vorhandenen Lernkategorie.
     * @return Nothing
     */
    private fun getSummary(): Summary {
        // Lernkategorie-Objekt wird erstellt
        return Summary(
            id = objSummary?.id ?: "",
            name = binding.textSummaryName.text.toString(),
        )
    }

    private fun observer() {
        // Eine Beobachtung auf viewModel.addSummary ausführen
        summaryViewModel.addSummary.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    if(state.data != null && currentUser != null) {
                        // Die neue Zusammenfassung dem User hinzufügen
                        currentLearningCategory?.summaries?.add(state.data)
                        val foundIndex = currentUser!!.learningCategories.indexOfFirst { it.id == currentLearningCategory?.id}
                        if(foundIndex != -1) {
                            currentUser!!.learningCategories[foundIndex] = currentLearningCategory!!
                        }
                        // Den User in der DB updaten
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(R.id.action_createSummaryFragment_to_summaryFragment,
                            Bundle().apply {
                                putString("type","view")
                                putParcelable("learning_category", currentLearningCategory)
                            })
                        toast("Die Zusammenfassung konnte erfolgreich erstellt werden")
                    }
                    else {
                        toast("Die Zusammenfassung konnte nicht erstellt werden")
                    }
                }
            }
        }

        // Eine Beobachtung auf viewModel.updateLearningCategory ausführen
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    currentUser = state.data
                }
            }
        }
    }
}
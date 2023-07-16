package com.example.learnahead_prototyp.UI.LearningCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentLearnCategoryDetailBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dies ist die Detailansicht einer Lernkategorie in der App. Sie zeigt Informationen wie den Lernkategorie-Namen.
 * Diese Klasse ist verantwortlich für die Erstellung einer neuen Lernkategorie oder die Aktualisierung einer vorhandenen Lernkategorie.
 * Die Entscheidung zwischen Erstellung und Aktualisierung erfolgt anhand des Werts der Variable isEdit.
 * Wenn isEdit true ist, wird die Lernkategorie aktualisiert, andernfalls wird eine neue Lernkategorie erstellt.
 *
 * @constructor Erstellt eine neue Instanz des LearningCategoryDetailFragment.
 */
@AndroidEntryPoint
class LearnCategoryDetailFragment : Fragment() {

    private var currentUser: User? = null

    // Ein Tag zur Identifizierung des Lernkategorie-Detail-Fragments für Logging-Zwecke.
    private val TAG: String = "LearningCategoryDetailFragment"

    // Das FragmentLearnCategoryDetailBinding-Objekt, das die View-Bindings enthält.
    private lateinit var binding: FragmentLearnCategoryDetailBinding

    // Das LearnCategoryViewModel-Objekt, das die Geschäftslogik enthält und die Daten für die Lernkategorie liefert.
    private val learnCategoryViewModel: LearnCategoryViewModel by viewModels()

    // Das AuthViewModel-Objekt, das für die Authentifizierung des Benutzers verantwortlich ist.
    private val authViewModel: AuthViewModel by viewModels()

    // Eine Flagge, die angibt, ob die Lernkategorie bearbeitet wird (true) oder ob sie neu erstellt wird (false).
    private var isEdit = false

    // Die Lernkategorie, die bearbeitet wird.
    private var objLearningCategory: LearningCategory? = null

    /**
     * Erstellt die View und gibt sie zurück.
     *
     * @param inflater Der LayoutInflater, der verwendet wird, um die View zu erstellen.
     * @param container Der ViewGroup-Container, der die View enthält.
     * @param savedInstanceState Das Bundle-Objekt, das den zuletzt gespeicherten Zustand enthält.
     * @return Die erstellte View.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearnCategoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Initialisiert die View und setzt die Listener auf die Schaltflächen. Ruft die updateUI()-Methode auf,
     * um die View mit den vorhandenen Daten zu aktualisieren.
     *
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

    /**
     * Diese Funktion setzt den aktuellen Benutzer lokal.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Erstellt alle notwendigen EventListener für das Fragment.
     */
    private fun setEventListener() {
        binding.saveIcon.setOnClickListener {
            if (isEdit)
                updateLearningCategory()
            else
                createLearningCategory()
        }

        binding.editIcon.setOnClickListener {
            isMakeEnableUI(true)
            isEdit = true
            binding.editIcon.hide()
            binding.textLearningCategoryName.requestFocus()
        }

        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.learningCategoriesButton.setOnClickListener { findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.learningGoalsButton.setOnClickListener { findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_goalListingFragment) }

        // Klick Listener zum Zurücknavigieren
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        // Event Listener wenn sich der Titel verändert
        binding.textLearningCategoryName.doAfterTextChanged {
            updateButtonVisibility()
        }
    }

    /**
     * Aktiviert oder deaktiviert die Benutzeroberfläche der Lernkategorien, indem die `isEnabled`-Eigenschaften
     * der betreffenden Ansichtselemente auf den übergebenen Wert festgelegt werden.
     * Wenn `isDisable` auf `true` gesetzt ist, wird die Benutzeroberfläche deaktiviert, andernfalls wird sie aktiviert.
     *
     * @param isDisable Ein boolescher Wert, der angibt, ob die Benutzeroberfläche deaktiviert werden soll
     *                  (`true`) oder nicht (`false`). Der Standardwert ist `false`.
     */
    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.textLearningCategoryName.isEnabled = isDisable
    }

    /**
     * Beobachtet die LiveData-Variablen `addLearningCategory` und `updateLearningCategory` des [ViewModel]-Objekts und reagiert
     * entsprechend, wenn sich der Wert der Variablen ändert.
     * Zeigt eine Fortschrittsanzeige an, zeigt Fehlermeldungen an oder zeigt Erfolgsmeldungen an.
     */
    private fun observer() {
        learnCategoryViewModel.addLearningCategory.observe(viewLifecycleOwner) { state ->
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
                    if (state.data != null && currentUser != null) {
                        currentUser!!.learningCategories.add(state.data)
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_learningCategoryListFragment)
                        toast("Die Lernkategorie konnte erfolgreich erstellt werden")
                    } else {
                        toast("Die Lernkategorie konnte nicht erstellt werden")
                    }
                }
            }
        }

        learnCategoryViewModel.updateLearningCategory.observe(viewLifecycleOwner) { state ->
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
                    if (state.data != null && currentUser != null) {
                        val indexOfCurrentObject = currentUser!!.learningCategories.indexOfFirst { it.id == state.data.id }
                        if (indexOfCurrentObject != -1) {
                            currentUser!!.learningCategories[indexOfCurrentObject] = state.data
                        } else {
                            currentUser!!.learningCategories.add(state.data)
                        }
                        authViewModel.updateUserInfo(currentUser!!)
                        toast("Die Lernkategorie konnte erfolgreich aktualisiert werden")
                    } else {
                        toast("Die Lernkategorie konnte nicht aktualisiert werden")
                    }
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
                }
            }
        }
    }

    /**
     * Aktualisiert die Sichtbarkeit des Speichern-Buttons basierend auf dem Inhalt des Namensfelds.
     * Wenn das Feld ausgefüllt ist, wird der Speichern-Button sichtbar, ansonsten wird er ausgeblendet.
     */
    private fun updateButtonVisibility() {
        val title = binding.textLearningCategoryName.text.toString().trim()

        if (title.isNotEmpty())
            binding.saveIcon.show()
        else
            binding.saveIcon.hide()
    }

    /**
     * Erstellt eine Lernkategorie, indem eine Beobachtung auf viewModel.addLearningCategory ausgeführt wird.
     * Zeigt je nach Zustand der Beobachtung verschiedene Aktionen an, z. B. Fortschrittsanzeige,
     * Fehlermeldung oder Erfolgsmeldung. Wenn die Eingabevalidierung erfolgreich ist,
     * wird die Lernkategorie an viewModel.addLearningCategory übergeben.
     */
    private fun createLearningCategory() {
        if (validation()) {
            learnCategoryViewModel.addLearningCategory(getLearningCategory())
        }
    }

    /**
     * Aktualisiert eine Lernkategorie, indem eine Beobachtung auf viewModel.updateLearningCategory ausgeführt wird.
     * Zeigt je nach Zustand der Beobachtung verschiedene Aktionen an, z. B. Fortschrittsanzeige,
     * Fehlermeldung oder Erfolgsmeldung. Wenn die Eingabevalidierung erfolgreich ist,
     * wird die Lernkategorie an viewModel.updateLearningCategory übergeben.
     */
    private fun updateLearningCategory() {
        if (validation()) {
            learnCategoryViewModel.updateLearningCategory(getLearningCategory())
        }
    }

    /**
     * Aktualisiert die Benutzeroberfläche basierend auf dem übergebenen Argumenttyp.
     */
    private fun updateUI() {
        objLearningCategory = arguments?.getParcelable("learning_category")

        objLearningCategory?.let { learningCategory ->
            binding.textLearningCategoryName.setText(learningCategory.name)
            binding.saveIcon.hide()
            binding.editIcon.show()
            isMakeEnableUI()
        } ?: run {
            binding.textLearningCategoryName.setText("")
            binding.saveIcon.hide()
            binding.editIcon.hide()
            isMakeEnableUI(true)
            binding.textLearningCategoryName.requestFocus()
        }
    }

    /**
     * Überprüft, ob die Eingabevalidierung für die Lernkategorie erfolgreich ist.
     *
     * @return Gibt true zurück, wenn die Eingabevalidierung erfolgreich ist, sonst false.
     */
    private fun validation(): Boolean {
        if (binding.textLearningCategoryName.text.isNullOrEmpty()) {
            binding.textLearningCategoryName.error = "Geben Sie einen Namen ein"
            return false
        }
        return true
    }

    /**
     * Erstellt eine Lernkategorie oder aktualisiert eine vorhandene Lernkategorie.
     *
     * @return Das erstellte Lernkategorie-Objekt.
     */
    private fun getLearningCategory(): LearningCategory {
        return LearningCategory(
            id = objLearningCategory?.id ?: "",
            name = binding.textLearningCategoryName.text.toString()
        )
    }
}

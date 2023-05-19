package com.example.learnahead_prototyp.UI.Goal

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
 * Dies ist die Detailansicht einer Lernkategorie auf der App. Es zeigt Informationen wie Lernkategorie-Name.
 * Diese Klasse ist verantwortlich für die Erstellung
 * einer neuen Lernkategorie oder die Aktualisierung einer vorhandenen Lernkategorie . Die Entscheidung zwischen Erstellung und Aktualisierung
 * erfolgt anhand des Wertes der Variable isEdit. Wenn isEdit true ist, wird die Lernkategorie aktualisiert, andernfalls wird
 * eine neue Lernkategorie erstellt.
 * @constructor Erstellt eine neue Instanz des LearningCategoryDetailFragment.
 */
@AndroidEntryPoint
class LearnCategoryDetailFragment : Fragment() {

    private var currentUser: User? = null

    // Ein Tag zur Identifizierung des Lernkategorie-Detail-Fragments für Logging-Zwecke.
    val TAG: String = "LearningCategoryDetailFragment"

    // Das FragmentLearnCategoryDetailBinding-Objekt, das die View-Bindings enthält.
    lateinit var binding: FragmentLearnCategoryDetailBinding

    // Das LearnCategoryViewModel-Objekt, das die Geschäftslogik enthält und die Daten für die Lernkategorie liefert.
    private val learnCategoryViewModel: LearnCategoryViewModel by viewModels()

    // Das AuthViewModel-Objekt, das für die Authentifizierung des Benutzers verantwortlich ist.
    private val authViewModel: AuthViewModel by viewModels()

    // Eine Flagge, die angibt, ob die Lernkategorie bearbeitet wird (true) oder ob es neu erstellt wird (false).
    private var isEdit = false

    // Die Lernkategorie, die bearbeitet wird.
    private var objLearningCategory: LearningCategory? = null


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
        binding = FragmentLearnCategoryDetailBinding.inflate(layoutInflater)
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
     * Erstellt alle notwendigen EventListener für das Fragment
     */
    private fun setEventListener() {
        binding.saveButton.setOnClickListener {
            if (isEdit)
                updateLearningCategory()
            else
                createLearningCategory()
        }

        binding.editButton.setOnClickListener {
            isMakeEnableUI(true)
            isEdit = true
            binding.editButton.hide()
            binding.textLearningCategoryName.requestFocus()
        }

        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_goalListingFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
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
     * der betreffenden Ansichtselemente auf den übergebenen Wert festgelegt werden. Wenn `isDisable` auf
     * `true` gesetzt ist, wird die Benutzeroberfläche deaktiviert, andernfalls wird sie aktiviert.
     * @param isDisable Ein boolescher Wert, der angibt, ob die Benutzeroberfläche deaktiviert werden soll
     *                  (`true`) oder nicht (`false`). Der Standardwert ist `false`.
     */
    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.textLearningCategoryName.isEnabled = isDisable
    }

    /**
     * Beobachtet die LiveData-Variablen `addLearningCategory` und `updateLearningCategory` des [ViewModel]-Objekts und reagiert
     * entsprechend, wenn sich der Wert der Variablen ändert. Zeigt eine Fortschrittsanzeige an, zeigt
     * Fehlermeldungen an oder zeigt Erfolgsmeldungen an.
     */
    private fun observer() {
        // Eine Beobachtung auf viewModel.addLearningCategory ausführen
        learnCategoryViewModel.addLearningCategory.observe(viewLifecycleOwner) { state ->
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
                        // Die neue Lernkategorie dem User hinzufügen
                        currentUser!!.learningCategories.add(state.data)
                        // Den User in der DB updaten
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(R.id.action_learnCategoryDetailFragment_to_learningCategoryListFragment)
                        toast("Die Lernkategorie konnte erfolgreich erstellt werden")
                    }
                    else {
                        toast("Die Lernkategorie konnte nicht erstellt werden")
                    }
                }
            }
        }

        // Eine Beobachtung auf viewModel.updateLearningCategory ausführen
        learnCategoryViewModel.updateLearningCategory.observe(viewLifecycleOwner) { state ->
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
                        val indexOfCurrentObject = currentUser!!.learningCategories.indexOfFirst { it.id == state.data.id }
                        if (indexOfCurrentObject != -1) {
                            currentUser!!.learningCategories[indexOfCurrentObject] = state.data
                        } else {
                            currentUser!!.learningCategories.add(state.data)
                        }

                        // Den User in der DB updaten
                        authViewModel.updateUserInfo(currentUser!!)
                        toast("Die Lernkategorie konnte erfolgreich geupdated werden")
                    }
                    else {
                        toast("Die Lernkategorie konnte nicht geupdated werden")
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

    /**
     * Überprüft ob der Name ausgefüllt ist.
     * Wenn das Feld ausgefüllt ist, dann soll der Button zum Speichern sichtbar sein.
     * Ist das Feld nicht ausgefüllt, dann soll der Speichern Button nicht sichtbar sein.
     */
    private fun updateButtonVisibility() {
        val title = binding.textLearningCategoryName.text.toString().trim()

        if (title.isNotEmpty())
            binding.saveButton.show()
        else
            binding.saveButton.hide()
    }

    /**
     * Erstellt eine Lernkategorie, indem es eine Beobachtung auf viewModel.addLearningCategory ausführt und je nach Zustand der Beobachtung
     * verschiedene Aktionen durchführt, wie z.B. Anzeigen oder Ausblenden des Fortschrittsindikators und Anzeigen einer Toast-Nachricht
     * in Abhängigkeit vom Zustand. Wenn die Eingabevalidierung erfolgreich ist, wird die Lernkategorie an viewModel.addLearningCategory übergeben.
     */
    private fun createLearningCategory() {
        // Wenn die Eingabevalidierung erfolgreich ist, die Lernkategorie an viewModel.addLearningCategory übergeben
        if (validation()) {
            learnCategoryViewModel.addLearningCategory(getLearningCategory())
        }
    }

    /**
     * Aktualisiert eine Lernkategorie, indem es eine Beobachtung auf viewModel.updateLearningCategory ausführt und je nach Zustand der Beobachtung
     * verschiedene Aktionen durchführt, wie z.B. Anzeigen oder Ausblenden des Fortschrittsindikators und Anzeigen einer Toast-Nachricht
     * in Abhängigkeit vom Zustand. Wenn die Eingabevalidierung erfolgreich ist, wird die Lernkategorie an viewModel.updateLearningCategory übergeben.
     */
    private fun updateLearningCategory() {
        // Wenn die Eingabevalidierung erfolgreich ist, die Lernkategorie an viewModel.updateLearningCategory übergeben
        if (validation()) {
            learnCategoryViewModel.updateLearningCategory(getLearningCategory())
        }
    }

    /**
     * Methode zum Aktualisieren der Benutzeroberfläche basierend auf dem Typ des übergebenen Arguments.
     * @return Nothing
     */
    private fun updateUI() {
        objLearningCategory = arguments?.getParcelable("learning_category")
        // Wenn eine Lernkategorie existiert z.B. wenn es editiert wird, dann werden die Daten der Lernkategorie geladen
        objLearningCategory?.let { learningCategory ->
            binding.textLearningCategoryName.setText(learningCategory.name)
            binding.saveButton.hide()
            binding.editButton.show()
            //binding.delete.show()
            isMakeEnableUI()
        } ?: run {
            // Standardmaske für Lernkategorie erstellen
            binding.textLearningCategoryName.setText("")
            binding.saveButton.hide()
            binding.editButton.hide()
            //binding.delete.hide()
            isMakeEnableUI(true)
            binding.textLearningCategoryName.requestFocus()
        }
    }

    /**
     * Methode zur Überprüfung, ob die Lernkategorie korrekt eingegeben wurde oder nicht.
     * @return Boolean
     */
    private fun validation(): Boolean {
        // Überprüfen, ob der Lernkategorie Name leer oder null ist
        if (binding.textLearningCategoryName.text.isNullOrEmpty()) {
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
    private fun getLearningCategory(): LearningCategory {
        // Lernkategorie-Objekt wird erstellt
        return LearningCategory(
            id = objLearningCategory?.id ?: "",
            name = binding.textLearningCategoryName.text.toString(),
        )
    }
}
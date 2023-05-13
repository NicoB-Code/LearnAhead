package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
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
import com.example.learnahead_prototyp.databinding.FragmentGoalDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dies ist die Detailansicht einer Ziels auf der App. Es zeigt Informationen wie Zielname, Beschreibung,
 * Start- und Enddatum und den aktuellen Fortschritt des Ziels. Diese Klasse ist verantwortlich für die Erstellung
 * eines neuen Ziels oder die Aktualisierung eines vorhandenen Ziels. Die Entscheidung zwischen Erstellung und Aktualisierung
 * erfolgt anhand des Wertes der Variable isEdit. Wenn isEdit true ist, wird das Ziel aktualisiert, andernfalls wird
 * ein neues Ziel erstellt.
 * @constructor Erstellt eine neue Instanz des GoalDetailFragment.
 */
@AndroidEntryPoint
class GoalDetailFragment : Fragment() {

    // Ein Tag zur Identifizierung des Ziel-Detail-Fragments für Logging-Zwecke.
    val TAG: String = "GoalDetailFragment"

    // Das FragmentGoalDetailBinding-Objekt, das die View-Bindings enthält.
    lateinit var binding: FragmentGoalDetailBinding

    // Das GoalViewModel-Objekt, das die Geschäftslogik enthält und die Daten für das Ziel liefert.
    val viewModel: GoalViewModel by viewModels()

    // Das AuthViewModel-Objekt, das für die Authentifizierung des Benutzers verantwortlich ist.
    val authViewModel: AuthViewModel by viewModels()

    // Eine Flagge, die angibt, ob das Ziel bearbeitet wird (true) oder ob es neu erstellt wird (false).
    var isEdit = false

    // Das Ziel, das bearbeitet wird.
    var objGoal: Goal? = null

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
    ): View? {
        binding = FragmentGoalDetailBinding.inflate(layoutInflater)
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
        // Updaten der UI
        UpdateUI()
        binding.buttonLearningGoalCreate.setOnClickListener {
            if (isEdit) {
                updateGoal()
            } else {
                createGoal()
            }
        }

        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(
                R.id.action_goalDetailFragment_to_homeFragment,
                Bundle().apply {
                    putString("type", "create")
                })
        }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(
                R.id.action_goalDetailFragment_to_learningCategoryListFragment,
                Bundle().apply {
                    putString("type", "create")
                })
        }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(
                R.id.action_goalDetailFragment_to_goalListingFragment,
                Bundle().apply {
                    putString("type", "create")
                })
        }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.backIcon.setOnClickListener {
            findNavController().navigate(
                R.id.action_goalDetailFragment_to_goalListingFragment,
                Bundle().apply {
                    putString("type", "create")
                })
        }

        // Event Listener wenn sich der Titel verändert
        binding.textLearningGoalTitle.doAfterTextChanged {
            updateButtonVisibility()
        }

        // Event Listener wenn sich der Start Datum verändert
        binding.textLearningGoalStartDate.doAfterTextChanged {
            updateButtonVisibility()
        }

        // Event Listener wenn sich der End Datum verändert
        binding.textLearningGoalEndDate.doAfterTextChanged {
            updateButtonVisibility()
        }

        // Event Listener wenn sich der Beschreibung verändert
        binding.textGoalDescription.doAfterTextChanged {
            updateButtonVisibility()
        }
    }

    /**
     * Überprüft ob der Titel, Start, End Datum und Beschreibung ausgefüllt sind.
     * Wenn alle Felder ausgefüllt, dann soll der Button zum Speichern sichtbar sein.
     * Ist eines der Felder nicht ausgefüllt, dann soll der Speichern Button nicht sichtbar sein.
     */
    fun updateButtonVisibility() {
        val title = binding.textLearningGoalTitle.text.toString().trim()
        val startDate = binding.textLearningGoalStartDate.text.toString().trim()
        val endDate = binding.textLearningGoalEndDate.text.toString().trim()
        val description = binding.textGoalDescription.text.toString().trim()

        if (title.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty() && description.isNotEmpty()) {
            binding.buttonLearningGoalCreate.show()
        } else {
            binding.buttonLearningGoalCreate.hide()
        }
    }

    /**
     * Erstellt ein Ziel, indem es eine Beobachtung auf viewModel.addGoal ausführt und je nach Zustand der Beobachtung
     * verschiedene Aktionen durchführt, wie z.B. Anzeigen oder Ausblenden des Fortschrittsindikators und Anzeigen einer Toast-Nachricht
     * in Abhängigkeit vom Zustand. Wenn die Eingabevalidierung erfolgreich ist, wird das Ziel an viewModel.addGoal übergeben.
     */
    private fun createGoal() {
        // Eine Beobachtung auf viewModel.addGoal ausführen
        viewModel.addGoal.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen und Button-Text löschen
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                    binding.buttonLearningGoalCreate.text = ""
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden, Button-Text auf "Create" setzen und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.buttonLearningGoalCreate.text = "Create"
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden, Button-Text auf "Create" setzen und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.buttonLearningGoalCreate.text = "Create"
                    toast(state.data)
                }
            }
        }
        // Wenn die Eingabevalidierung erfolgreich ist, das Ziel an viewModel.addGoal übergeben
        if (validation()) {
            viewModel.addGoal(getGoal())
        }
    }

    /**
     * Aktualisiert ein Ziel, indem es eine Beobachtung auf viewModel.updateGoal ausführt und je nach Zustand der Beobachtung
     * verschiedene Aktionen durchführt, wie z.B. Anzeigen oder Ausblenden des Fortschrittsindikators und Anzeigen einer Toast-Nachricht
     * in Abhängigkeit vom Zustand. Wenn die Eingabevalidierung erfolgreich ist, wird das Ziel an viewModel.updateGoal übergeben.
     */
    private fun updateGoal() {
        // Wenn die Eingabevalidierung erfolgreich ist, das Ziel an viewModel.updateGoal übergeben
        if (validation()) {
            viewModel.updateGoal(getGoal())
        }
        // Eine Beobachtung auf viewModel.updateGoal ausführen
        viewModel.updateGoal.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen und Button-Text löschen
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                    binding.buttonLearningGoalCreate.text = ""
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden, Button-Text auf "Update" setzen und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.buttonLearningGoalCreate.text = "Update"
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden, Button-Text auf "Update" setzen und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.buttonLearningGoalCreate.text = "Update"
                    toast(state.data)
                }
            }
        }
    }

    /**
     * Methode zum Aktualisieren der Benutzeroberfläche basierend auf dem Typ des übergebenen Arguments.
     * @return Nothing
     */
    private fun UpdateUI() {
        // das type-Argument wird aus den Fragment-Argumenten extrahiert
        val type = arguments?.getString("type", null)

        type?.let {
            when (it) {
                // falls "view" übergeben wurde
                "view" -> {
                    isEdit = false
                    // das Ziel-Textfeld wird aktiviert und der Zielbeschreibungstext wird gesetzt
                    binding.textGoalDescription.isEnabled = true
                    objGoal = arguments?.getParcelable("goal")
                    binding.textGoalDescription.setText(objGoal?.description)
                    // der "button" wird ausgeblendet
                    binding.buttonLearningGoalCreate.hide()
                }
                // falls "create" übergeben wurde
                "create" -> {
                    isEdit = false
                    // der Button-Text wird auf "Create" gesetzt
                    binding.buttonLearningGoalCreate.setText("Create")
                }
                // falls "edit" übergeben wurde
                "edit" -> {
                    isEdit = true
                    objGoal = arguments?.getParcelable("goal")
                    // der Zielbeschreibungstext wird gesetzt und der Button-Text wird auf "Update" gesetzt
                    binding.textGoalDescription.setText(objGoal?.description)
                    binding.buttonLearningGoalCreate.setText("Update")
                }
            }
        }
    }

    /**
     * Methode zur Überprüfung, ob das Ziel korrekt eingegeben wurde oder nicht.
     * @return Boolean
     */
    private fun validation(): Boolean {
        // Überprüfen, ob der Lernzielname leer oder null ist
        if (binding.textLearningGoalTitle.text.isNullOrEmpty()) {
            // Wenn ja, eine Toast-Meldung ausgeben und false zurückgeben
            toast("Enter title")
            return false
        }

        // halbfunktionsfähige Datumsvalidierung
        dateValidation(binding.textLearningGoalStartDate)
        dateValidation(binding.textLearningGoalEndDate)

        // Überprüfen, ob die Beschreibung leer oder null ist
        if (binding.textGoalDescription.text.isNullOrEmpty()) {
            // Wenn ja, eine Toast-Meldung ausgeben und false zurückgeben
            toast("Enter description")
            return false
        }
        // Andernfalls true zurückgeben
        return true
    }

    private fun dateValidation(textLearningGoalStartDate: EditText): Date {
        // Holen Sie die Benutzereingabe aus dem EditText
        val userInput = textLearningGoalStartDate.text.toString()

        // Definieren Sie das erwartete Datumsformat (dd.MM.yyyy)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)

        // Setzen Sie isLenient auf false, um sicherzustellen, dass das Format streng ist
        dateFormat.isLenient = false

        try {
            // Versuchen Sie, die Benutzereingabe als Datum zu parsen
            val date = dateFormat.parse(userInput)

            // Überprüfen Sie, ob das geparste Datum der Benutzereingabe entspricht
            if (date != null && dateFormat.format(date) == userInput) {
                // Die Benutzereingabe ist ein gültiges Datum
                return date
            } else {
                // Die Benutzereingabe ist kein gültiges Datum
                val errorMessage = "Das eingegebene Datum ist ungültig."
                textLearningGoalStartDate.error = errorMessage
                return Date()
            }
        } catch (e: ParseException) {
            // Die Benutzereingabe ist kein gültiges Datum
            val errorMessage = "Das eingegebene Datum ist ungültig."
            textLearningGoalStartDate.error = errorMessage
        }
        return Date()
    }

    /**
     * Methode zum Erstellen eines neuen Ziels oder Aktualisieren eines vorhandenen Ziels.
     *
     * @return Nothing
     */
    private fun getGoal(): Goal {
        // Ziel-Objekt wird erstellt
        return Goal(
            id = objGoal?.id ?: "",
            title = binding.textLearningGoalTitle.text.toString(),
            description = binding.textGoalDescription.text.toString(),
            startDate = dateValidation(binding.textLearningGoalStartDate),
            endDate = dateValidation(binding.textLearningGoalEndDate)
        ).apply {
            // Session-Daten werden aktualisiert
            authViewModel.getSession {
                this.user_id = it?.id ?: ""
            }
        }
    }
}
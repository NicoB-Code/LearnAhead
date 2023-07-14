package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.Question.CustomSpinnerAdapter
import com.example.learnahead_prototyp.Util.DatePickerFragment
import com.example.learnahead_prototyp.Util.GoalStatus
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentGoalDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
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


    private val currentDate: Calendar = Calendar.getInstance()
    private var year = currentDate[Calendar.YEAR]
    private var month = currentDate[Calendar.MONTH]
    private var day = currentDate[Calendar.DAY_OF_MONTH]

    private var currentUser: User? = null

    // Ein Tag zur Identifizierung des Ziel-Detail-Fragments für Logging-Zwecke.
    val TAG: String = "GoalDetailFragment"

    // Das FragmentGoalDetailBinding-Objekt, das die View-Bindings enthält.
    lateinit var binding: FragmentGoalDetailBinding

    // Das GoalViewModel-Objekt, das die Geschäftslogik enthält und die Daten für das Ziel liefert.
    private val goalViewModel: GoalViewModel by viewModels()

    // Das AuthViewModel-Objekt, das für die Authentifizierung des Benutzers verantwortlich ist.
    private val authViewModel: AuthViewModel by viewModels()

    // Eine Flagge, die angibt, ob das Ziel bearbeitet wird (true) oder ob es neu erstellt wird (false).
    private var isEdit = false

    // Das Ziel, das bearbeitet wird.
    private var objGoal: Goal? = null

    private var isInitialSelection = false // Flag to track initial selection

    private var learningCategoriesList: MutableList<LearningCategory> = mutableListOf()


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
        observer()
        setLocalCurrentUser()
        updateUI()
        setEventListener()
    }

    private fun populateDropdown() {
        val dropdownItems = learningCategoriesList.map { it.name } // Extrahiere die Kategorienamen aus der Liste

        val adapter = CustomSpinnerAdapter(requireContext(), R.layout.spinner_dropdown_item, dropdownItems)
        binding.dropdownElement.adapter = adapter
    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    private fun showDatePickerDialog(textLearningGoalDate: TextView) {
        val datePicker = DatePickerFragment {day, month, year -> onDateSelected(textLearningGoalDate, day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")
    }
    private fun onDateSelected(textLearningGoalDate: TextView, day: Int, month: Int, year: Int) {
        if(day < 10){
            textLearningGoalDate.text = "0$day.0${month + 1}.$year"
        } else {
            textLearningGoalDate.text = "$day.0${month + 1}.$year"
        }
    }

    /**
     * Erstellt alle notwendigen EventListener für das Fragment
     */
    private fun setEventListener() {
        binding.saveButton.setOnClickListener {
            if (isEdit)
                updateGoal()
            else
                createGoal()

            isInitialSelection = false
        }

        binding.textLearningGoalStartDate.setOnClickListener {
            showDatePickerDialog(binding.textLearningGoalStartDate)
        }

        binding.textLearningGoalEndDate.setOnClickListener {
            showDatePickerDialog(binding.textLearningGoalEndDate)
        }

        binding.editButton.setOnClickListener {
            isMakeEnableUI(true)
            isEdit = true
            binding.editButton.hide()
            binding.textLearningGoalName.requestFocus()
        }

        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_goalDetailFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_goalDetailFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_goalDetailFragment_to_goalListingFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }


        // Event Listener wenn sich der Titel verändert
        binding.textLearningGoalName.doAfterTextChanged {
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

        // Event Listener when the dropdown selection changes
        binding.dropdownElement.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isInitialSelection) {
                    updateButtonVisibility()
                }
                isInitialSelection = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }


    /**
     * Aktiviert oder deaktiviert die Benutzeroberfläche der Lernziele, indem die `isEnabled`-Eigenschaften
     * der betreffenden Ansichtselemente auf den übergebenen Wert festgelegt werden. Wenn `isDisable` auf
     * `true` gesetzt ist, wird die Benutzeroberfläche deaktiviert, andernfalls wird sie aktiviert.
     * @param isDisable Ein boolescher Wert, der angibt, ob die Benutzeroberfläche deaktiviert werden soll
     *                  (`true`) oder nicht (`false`). Der Standardwert ist `false`.
     */
    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.textLearningGoalName.isEnabled = isDisable
        binding.textLearningGoalStartDate.isEnabled = isDisable


        binding.textLearningGoalEndDate.isEnabled = isDisable
        binding.textGoalDescription.isEnabled = isDisable
        binding.dropdownElement.isEnabled = isDisable
    }

    /**
     * Beobachtet die LiveData-Variablen `addGoal` und `updateGoal` des [ViewModel]-Objekts und reagiert
     * entsprechend, wenn sich der Wert der Variablen ändert. Zeigt eine Fortschrittsanzeige an, zeigt
     * Fehlermeldungen an oder zeigt Erfolgsmeldungen an.
     */
    private fun observer() {
        // Eine Beobachtung auf viewModel.addGoal ausführen
        goalViewModel.addGoal.observe(viewLifecycleOwner) { state ->
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
                        // Das neue Lernziel in dem User hinzufügen
                        currentUser!!.goals.add(state.data)

                        val selectedCategory = learningCategoriesList[binding.dropdownElement.selectedItemPosition]
                        selectedCategory.relatedLearningGoal = state.data

                        // Den User in der DB updaten
                        currentUser?.let {
                            it.learningCategories[learningCategoriesList.indexOf(selectedCategory)] = selectedCategory
                            authViewModel.updateUserInfo(it)
                        }

                        findNavController().navigate(R.id.action_goalDetailFragment_to_goalListingFragment)
                        toast("Das Lernziel konnte erfolgreich erstellt werden")
                    }
                    else {
                        toast("Das Lernziel konnte nicht erstellt werden")
                    }
                }
            }
        }

        // Eine Beobachtung auf viewModel.updateGoal ausführen
        goalViewModel.updateGoal.observe(viewLifecycleOwner) { state ->
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
                        val indexOfCurrentObject = currentUser!!.goals.indexOfFirst { it.id == state.data.id }
                        if (indexOfCurrentObject != -1) {
                            currentUser!!.goals[indexOfCurrentObject] = state.data
                        } else {
                            currentUser!!.goals.add(state.data)
                        }

                        val selectedCategory = learningCategoriesList[binding.dropdownElement.selectedItemPosition]
                        val oldCategory = learningCategoriesList.find { it.relatedLearningGoal?.id == state.data.id }

                        // Remove the goal from the old learning category
                        oldCategory?.relatedLearningGoal = null

                        // Set the goal in the new learning category
                        selectedCategory.relatedLearningGoal = state.data

                        // Update the learning categories in the user object and update it in the database
                        currentUser?.let {
                            val oldCategoryIndex = learningCategoriesList.indexOf(oldCategory)
                            val selectedCategoryIndex = learningCategoriesList.indexOf(selectedCategory)

                            if (oldCategoryIndex != -1) {
                                if (oldCategory != null) {
                                    it.learningCategories[oldCategoryIndex] = oldCategory
                                }
                            }
                            if (selectedCategoryIndex != -1) {
                                it.learningCategories[selectedCategoryIndex] = selectedCategory
                            }

                            authViewModel.updateUserInfo(it)
                        }
                        toast("Das Lernziel konnte erfolgreich geupdated werden")
                    }
                    else {
                        toast("Das Lernziel konnte nicht geupdated werden")
                    }
                }
            }
        }
        // Eine Beobachtung auf viewModel.updateGoal ausführen
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
                    learningCategoriesList = currentUser!!.learningCategories
                    populateDropdown()
                    updateUI()
                    binding.saveButton.hide()
                }
            }
        }
    }

    /**
     * Überprüft ob der Titel, Start, End Datum und Beschreibung ausgefüllt sind.
     * Wenn alle Felder ausgefüllt, dann soll der Button zum Speichern sichtbar sein.
     * Ist eines der Felder nicht ausgefüllt, dann soll der Speichern Button nicht sichtbar sein.
     */
    private fun updateButtonVisibility() {
        val title = binding.textLearningGoalName.text.toString().trim()
        val startDateString = binding.textLearningGoalStartDate.text.toString()
        val endDateString = binding.textLearningGoalEndDate.text.toString()
        val description = binding.textGoalDescription.text.toString().trim()

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val startDate: Date = dateFormat.parse(startDateString)
        val endDate: Date = dateFormat.parse(endDateString)

        if (isInitialSelection && title.isNotEmpty() && startDateString.trim().isNotEmpty() && endDateString.trim().isNotEmpty() && description.isNotEmpty()) {
            if(!startDate.after(endDate) || !endDate.before(startDate)){
                binding.saveButton.show()
            } else {
                binding.saveButton.hide()
            }
        } else {
            binding.saveButton.hide()
        }
    }

    /**
     * Erstellt ein Ziel, indem es eine Beobachtung auf viewModel.addGoal ausführt und je nach Zustand der Beobachtung
     * verschiedene Aktionen durchführt, wie z.B. Anzeigen oder Ausblenden des Fortschrittsindikators und Anzeigen einer Toast-Nachricht
     * in Abhängigkeit vom Zustand. Wenn die Eingabevalidierung erfolgreich ist, wird das Ziel an viewModel.addGoal übergeben.
     */
    private fun createGoal() {
        // Wenn die Eingabevalidierung erfolgreich ist, das Ziel an viewModel.addGoal übergeben
        if (validation()) {
            goalViewModel.addGoal(getGoal())
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
            goalViewModel.updateGoal(getGoal())
        }
    }

    /**
     * Methode zum Aktualisieren der Benutzeroberfläche basierend auf dem Typ des übergebenen Arguments.
     * @return Nothing
     */
    private fun updateUI() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        objGoal = arguments?.getParcelable("goal")
        // Wenn ein Lernziel existiert z.B. wenn es editiert wird, dann werden die Daten des Lernziels geladen
        objGoal?.let { goal ->
            binding.textLearningGoalName.setText(goal.name)
            binding.textLearningGoalStartDate.setText(dateFormat.format(goal.startDate))
            binding.textLearningGoalEndDate.setText(dateFormat.format(goal.endDate))
            binding.textGoalDescription.setText(goal.description)
            binding.saveButton.hide()
            binding.editButton.show()

            // Find the related learning category by goal ID
            val relatedCategory = learningCategoriesList.find { it.relatedLearningGoal?.id == goal.id }

            // Set the dropdown selection to the related learning category if found
            relatedCategory?.let { category ->
                val selectedIndex = learningCategoriesList.indexOf(category)
                binding.dropdownElement.setSelection(selectedIndex)
            }
            //binding.delete.show()
            isMakeEnableUI()
        } ?: run {
            // Standardmaske für Lernziel erstellen
            binding.textLearningGoalName.setText("")
            binding.textLearningGoalStartDate.setText(dateFormat.format(Date()))
            binding.textLearningGoalEndDate.setText(dateFormat.format(Date()))
            binding.textGoalDescription.setText("")
            binding.saveButton.hide()
            binding.editButton.hide()
            //binding.delete.hide()
            isMakeEnableUI(true)
            binding.textLearningGoalName.requestFocus()
        }
    }

    /**
     * Methode zur Überprüfung, ob das Ziel korrekt eingegeben wurde oder nicht.
     * @return Boolean
     */
    private fun validation(): Boolean {
        // Überprüfen, ob der Lernzielname leer oder null ist
        if (binding.textLearningGoalName.text.isNullOrEmpty()) {
            // Wenn ja, eine Toast-Meldung ausgeben und false zurückgeben
            toast("Titel eingeben")
            return false
        }

        // halbfunktionsfähige Datumsvalidierung
        dateValidation(binding.textLearningGoalStartDate)
        dateValidation(binding.textLearningGoalEndDate)

        // Überprüfen, ob die Beschreibung leer oder null ist
        if (binding.textGoalDescription.text.isNullOrEmpty()) {
            // Wenn ja, eine Toast-Meldung ausgeben und false zurückgeben
            toast("Beschreibung eingeben")
            return false
        }
        // Andernfalls true zurückgeben
        return true
    }

    private fun dateValidation(textLearningGoalStartDate: TextView): Date {
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
            name = binding.textLearningGoalName.text.toString(),
            description = binding.textGoalDescription.text.toString(),
            startDate = dateValidation(binding.textLearningGoalStartDate),
            endDate = dateValidation(binding.textLearningGoalEndDate),
            lastLearned = objGoal?.lastLearned,
            status = GoalStatus.ToDo
        )
    }
}
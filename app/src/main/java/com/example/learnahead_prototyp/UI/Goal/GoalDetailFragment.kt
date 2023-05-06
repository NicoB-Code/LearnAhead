package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentGoalDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

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
        binding.button.setOnClickListener {
            if (isEdit) {
                updateGoal()
            } else {
                createGoal()
            }
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
                    binding.button.text = ""
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden, Button-Text auf "Create" setzen und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Create"
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden, Button-Text auf "Create" setzen und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Create"
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
                    binding.button.text = ""
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden, Button-Text auf "Update" setzen und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Update"
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden, Button-Text auf "Update" setzen und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.button.text = "Update"
                    toast(state.data)
                }
            }
        }
    }

    private fun UpdateUI() {
        val type = arguments?.getString("type", null)
        type?.let {
            when (it) {
                "view" -> {
                    isEdit = false
                    binding.goalDescription.isEnabled = true
                    objGoal = arguments?.getParcelable("goal")
                    binding.goalDescription.setText(objGoal?.description)
                    binding.button.hide()
                }

                "create" -> {
                    isEdit = false
                    binding.button.setText("Create")
                }

                "edit" -> {
                    isEdit = true
                    objGoal = arguments?.getParcelable("goal")
                    binding.goalDescription.setText(objGoal?.description)
                    binding.button.setText("Update")
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.goalDescription.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("Enter description")
        }

        return isValid
    }

    private fun getGoal(): Goal {
        return Goal(
            id = objGoal?.id ?: "",
            description = binding.goalDescription.text.toString(),
            date = Date()
        ).apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }
}
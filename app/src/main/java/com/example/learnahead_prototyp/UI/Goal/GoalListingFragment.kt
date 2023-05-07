package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.learnahead_prototyp.databinding.FragmentGoalListingBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [GoalListingFragment] ist für die Anzeige der Liste der Ziele zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einem Ziel anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class GoalListingFragment : Fragment() {

    // Konstante für das Logging-Tag
    val TAG: String = "GoalListingFragment"

    // Deklaration der benötigten Variablen
    lateinit var binding: FragmentGoalListingBinding
    val viewModel: GoalViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    var deletePosition: Int = -1
    var list: MutableList<Goal> = arrayListOf()

    // Initialisierung des Adapters mit den entsprechenden Click-Callbacks
    val adapter by lazy {
        GoalListingAdapter(
            onItemClicked = { pos, item ->
                // Navigation zum Ziel-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_goalListingFragment_to_goalDetailFragment,
                    Bundle().apply {
                        putString("type", "view")
                        putParcelable("goal", item)
                    })
            },
            onEditClicked = { pos, item ->
                // Navigation zum Ziel-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_goalListingFragment_to_goalDetailFragment,
                    Bundle().apply {
                        putString("type", "edit")
                        putParcelable("goal", item)
                    })
            },
            onDeleteClicked = { pos, item ->
                // Speichern der zu löschenden Position und Löschen des Ziels über das ViewModel
                deletePosition = pos
                viewModel.deleteGoal(item)
            }
        )
    }


    /**
     * Erzeugt die View-Hierarchie für das Fragment, indem das entsprechende Binding Layout aufgeblasen wird.
     * @param inflater Das [LayoutInflater]-Objekt, das verwendet wird, um das Layout aufzublasen.
     * @param container Die übergeordnete [ViewGroup], an die die View angehängt werden soll.
     * @param savedInstanceState Das [Bundle]-Objekt, das den Zustand des Fragments enthält.
     * @return Die erzeugte [View]-Instanz.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Das Binding-Objekt für das Fragment-Layout wird initialisiert.
        binding = FragmentGoalListingBinding.inflate(layoutInflater)
        // Die erzeugte View-Instanz wird zurückgegeben.
        return binding.root
    }


    /**
     * Diese Funktion initialisiert die View und registriert alle Observer, die auf Veränderungen in den ViewModel-Objekten achten.
     * @param view Die View der Fragment-Klasse
     * @param savedInstanceState Der gespeicherte Zustand des Fragments
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Registrieren des Observer
        Observer()

        // Setzen des Adapters auf die RecyclerView
        binding.recyclerView.adapter = adapter

        // Klick-Listener für den "Create"-Button, welcher zur "GoalDetailFragment" navigiert.
        binding.button.setOnClickListener {
            findNavController().navigate(
                R.id.action_goalListingFragment_to_goalDetailFragment,
                Bundle().apply {
                    putString("type", "create")
                })
        }

        // Klick-Listener für den "Logout"-Button, welcher den Benutzer ausloggt und zur "LoginFragment" navigiert.
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_goalListingFragment_to_loginFragment)
            }
        }

        // Abrufen der aktuellen Benutzersitzung, um die zugehörigen Ziele abzurufen.
        authViewModel.getSession {
            viewModel.getGoals(it)
        }
    }

    /**
     * Diese Funktion initialisiert alle Observer, welche die ViewModel-Objekte auf Veränderungen überwachen.
     */
    private fun Observer() {
        // Observer für "goal"-Objekt im "viewModel". Dieser überwacht alle Änderungen in der Liste der Benutzerziele.
        viewModel.goal.observe(viewLifecycleOwner) { state ->
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
                    // Fortschrittsanzeige ausblenden und Liste der Benutzerziele aktualisieren
                    binding.progressBar.hide()
                    list = state.data.toMutableList()
                    adapter.updateList(list)
                }
            }
        }

        // Observer für "deleteGoal"-Objekt im "viewModel". Dieser überwacht alle Änderungen beim Löschen von Benutzerzielen.
        viewModel.deleteGoal.observe(viewLifecycleOwner) { state ->
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
                    // Fortschrittsanzeige ausblenden, Erfolgsmeldung anzeigen und Ziel aus der Liste entfernen
                    binding.progressBar.hide()
                    toast(state.data)
                    if (deletePosition != -1) {
                        list.removeAt(deletePosition)
                        adapter.updateList(list)
                    }
                }
            }
        }
    }
}
package com.example.learnahead.UI.Goal

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead.Data.Model.Goal
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.User
import com.example.learnahead.R
import com.example.learnahead.UI.Auth.AuthViewModel
import com.example.learnahead.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead.Util.UiState
import com.example.learnahead.Util.hide
import com.example.learnahead.Util.show
import com.example.learnahead.Util.toast
import com.example.learnahead.databinding.FragmentGoalListingBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [GoalListingFragment] ist für die Anzeige der Liste der Ziele zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einem Ziel anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class GoalListingFragment : Fragment() {

    private lateinit var relatedCategory: LearningCategory
    private lateinit var learningCategoriesList: MutableList<LearningCategory>
    private var currentUser: User? = null

    // Konstante für das Logging-Tag
    private val TAG: String = "GoalListingFragment"

    // Deklaration der benötigten Variablen
    private lateinit var binding: FragmentGoalListingBinding
    private val viewModel: GoalViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private var deletePosition: Int = -1
    private var goalList: MutableList<Goal> = mutableListOf()

    // Initialisierung des Adapters mit den entsprechenden Click-Callbacks
    private val adapter by lazy {
        GoalListingAdapter(
            onItemClicked = { pos, item ->
                // Navigation zum Ziel-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_goalListingFragment_to_goalDetailFragment,
                    Bundle().apply {
                        putParcelable("goal", item)
                    }
                )
            },
            onEditClicked = { pos, item ->
                // Navigation zum Ziel-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_goalListingFragment_to_goalDetailFragment,
                    Bundle().apply {
                        putParcelable("goal", item)
                    }
                )
            },
            onDeleteClicked = { pos, item ->
                // Speichern der zu löschenden Position und Löschen des Ziels über das ViewModel

                // Finde die zugehörige LearningCategory des Ziels
                relatedCategory = learningCategoriesList.find { it.relatedLearningGoal?.id == item.id }!!

                // Setze das relatedLearningGoal auf null, wenn die LearningCategory gefunden wurde
                relatedCategory.relatedLearningGoal = null
                deletePosition = pos
                viewModel.deleteGoal(item)
                updateUserObject(item, true)
            }
        )
    }

    /**
     * Aktualisiert das Benutzerobjekt mit den Zielen, nachdem ein Ziel gelöscht wurde.
     * @param goal Das gelöschte Ziel.
     * @param deleteGoal Ein Flag, das angibt, ob das Ziel gelöscht werden soll.
     */
    private fun updateUserObject(goal: Goal, deleteGoal: Boolean) {
        if (deleteGoal && currentUser != null)
            currentUser!!.goals.remove(goal)

        currentUser?.let { authViewModel.updateUserInfo(it) }
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
    ): View {
        // Das Binding-Objekt für das Fragment-Layout wird initialisiert.
        binding = FragmentGoalListingBinding.inflate(inflater, container, false)
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
        setupObservers()
        setLocalCurrentUser()
        setEventListeners()
    }

    /**
     * Ruft den aktuellen Benutzer aus der AuthViewModel ab.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Initialisiert die Event-Listener für die Buttons und setzt den Adapter für die RecyclerView.
     */
    private fun setEventListeners() {
        // Setzen des Adapters auf die RecyclerView
        binding.learningGoalsRecyclerView.adapter = adapter

        // Klick-Listener für den "Create"-Button, welcher zur "GoalDetailFragment" navigiert.
        binding.addLearningGoalButton.setOnClickListener {
            findNavController().navigate(R.id.action_goalListingFragment_to_goalDetailFragment)
        }

        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_goalListingFragment_to_homeFragment)
        }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.learningCategoriesButton.setOnClickListener {
            findNavController().navigate(R.id.action_goalListingFragment_to_learningCategoryListFragment)
        }

        // Klick-Listener für den "Logout"-Button, welcher den Benutzer ausloggt und zur "LoginFragment" navigiert.
        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Ausloggen")
                    .setMessage("Möchtest du dich wirklich ausloggen?")
                    .setPositiveButton("Ja") { _, _ ->
                        findNavController().navigate(R.id.action_goalListingFragment_to_loginFragment)
                    }
                    .setNegativeButton("Nein", null)
                    .show()
            }
        }

        // Klick-Listener für den "Profile"-Button
        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.action_goalListingFragment_to_profileFragment)
        }
    }

    /**
     * Initialisiert alle Observer, welche die ViewModel-Objekte auf Veränderungen überwachen.
     */
    private fun setupObservers() {

        // Observer für "learningCategoriesList"-Objekt im "learnCategoryViewModel". Überwacht Änderungen in der Liste der Lernkategorien.
        learnCategoryViewModel.learningCategories.observe(viewLifecycleOwner) { state ->
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
                    // Fortschrittsanzeige ausblenden und Liste der Lernkategorien aktualisieren
                    binding.progressBar.hide()
                    learningCategoriesList = state.data.toMutableList()
                }
            }
        }
        // Observer für "goalList"-Objekt im "viewModel". Überwacht Änderungen in der Liste der Benutzerziele.
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
                    goalList = state.data.toMutableList()
                    adapter.updateList(goalList)
                }
            }
        }

        // Observer für "deleteGoalResult"-Objekt im "viewModel". Überwacht Änderungen beim Löschen von Benutzerzielen.
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
                    //toast(state.data)
                    if (deletePosition != -1 && deletePosition < goalList.size) {
                        goalList.removeAt(deletePosition)
                        adapter.updateList(goalList)
                        // Das Lernziel wurde gelöscht, somit deletePosition wieder zurücksetzen
                        deletePosition = -1

                        // Den User in der DB updaten
                        currentUser?.let {
                            it.learningCategories[learningCategoriesList.indexOf(relatedCategory)] = relatedCategory
                            authViewModel.updateUserInfo(it)
                        }
                    }

                }
            }
        }

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
                    // Fortschrittsanzeige ausblenden, Erfolgsmeldung anzeigen und Ziel aus der Liste entfernen
                    binding.progressBar.hide()
                    currentUser = state.data
                    viewModel.getGoals(currentUser)
                }
            }
        }
    }
}

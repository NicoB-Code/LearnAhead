package com.example.learnahead.UI.LearningCategory

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.User
import com.example.learnahead.R
import com.example.learnahead.UI.Auth.AuthViewModel
import com.example.learnahead.Util.UiState
import com.example.learnahead.Util.hide
import com.example.learnahead.Util.show
import com.example.learnahead.Util.toast
import com.example.learnahead.databinding.FragmentLearningCategoryListBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [LearningCategoryListFragment] ist für die Anzeige der Liste der Lernkategorien zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einer Lernkategorie anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class LearningCategoryListFragment : Fragment() {

    private var currentUser: User? = null

    // Konstante für das Logging-Tag
    private val TAG: String = "LearningCategoryListFragment"

    // Deklaration der benötigten Variablen
    lateinit var binding: FragmentLearningCategoryListBinding
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private var deletePosition: Int = -1
    private var list: MutableList<LearningCategory> = arrayListOf()

    // Initialisierung des Adapters mit den entsprechenden Click-Callbacks
    private val adapter by lazy {
        LearningCategoryListingAdapter(
            onItemClicked = { item ->
                // Navigation zum Lernkategorie-Detail-Fragment mit Parameter-Übergabe
                learnCategoryViewModel.setCurrentSelectedLearningCategory(item)
                findNavController().navigate(
                    R.id.action_learningCategoryListFragment_to_learningCategoryInnerViewFragment)
            },
            onDeleteClicked = { pos, item ->
                // Speichern der zu löschenden Position und Löschen der Lernkategorie über das ViewModel
                deletePosition = pos
                learnCategoryViewModel.deleteLearningCategory(item)
                updateUserObject(item, true)
            }
        )
    }

    /**
     * Aktualisiert das Benutzerobjekt, indem eine Lernkategorie hinzugefügt oder entfernt wird.
     *
     * @param learningCategory Die Lernkategorie, die hinzugefügt oder entfernt werden soll.
     * @param deleteLearningCategory Gibt an, ob die Lernkategorie gelöscht werden soll.
     */
    private fun updateUserObject(learningCategory: LearningCategory, deleteLearningCategory: Boolean) {
        if (deleteLearningCategory && currentUser != null) {
            currentUser!!.learningCategories.remove(learningCategory)
        }

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
        binding = FragmentLearningCategoryListBinding.inflate(layoutInflater)
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

        observer()
        setLocalCurrentUser()
        setEventListener()

        // Setzen des Adapters auf die RecyclerView
        binding.learningCategoriesRecyclerView.adapter = adapter
    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    // Alle Event-Listener aufsetzen
    private fun setEventListener() {
        // Klick-Listener für den "Create"-Button, welcher zur "LearningCategoryDetailFragment" navigiert.
        binding.addLearningCategoryButton.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_learnCategoryDetailFragment) }

        // Klick-Listener für den "LearningCategory"-Button, welcher den Benutzer zum "HomeFragment" navigiert.
        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_homeFragment)}

        // Klick-Listener für den "LearningCategory"-Button, welcher den Benutzer zur "LearningCategoryListFragment" navigiert.
        binding.learningGoalsButton.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_goalListingFragment) }

        // Klick-Listener für den "Logout"-Button, welcher den Benutzer ausloggt und zur "LoginFragment" navigiert.
        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Ausloggen")
                    .setMessage("Möchtest du dich wirklich ausloggen?")
                    .setPositiveButton("Ja") { _, _ ->
                        findNavController().navigate(R.id.action_learningCategoryListFragment_to_loginFragment)
                    }
                    .setNegativeButton("Nein", null)
                    .show()
            }
        }

        // Klick-Listener für den "Profil"-Button
        binding.profileIcon.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_profileFragment)}
    }

    /**
     * Diese Funktion initialisiert alle Observer, welche die ViewModel-Objekte auf Veränderungen überwachen.
     */
    private fun observer() {
        // Observer für "learningCategory"-Objekt im "viewModel". Dieser überwacht alle Änderungen in der Liste der Lernkategorien.
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
                    // Fortschrittsanzeige ausblenden und Liste der Benutzerziele aktualisieren
                    binding.progressBar.hide()
                    list = state.data.toMutableList()
                    adapter.updateList(list)
                }
            }
        }

        // Observer für "deleteLearningCategory"-Objekt im "viewModel". Dieser überwacht alle Änderungen beim Löschen von Lernkategorien.
        learnCategoryViewModel.deleteLearningCategory.observe(viewLifecycleOwner) { state ->
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
                        // Die Lernkategorie wurde gelöscht, somit deletePosition wieder zurücksetzen
                        deletePosition = -1
                    }
                }
            }
        }

        // Observer für "deleteLearningCategory"-Objekt im "viewModel". Dieser überwacht alle Änderungen beim Löschen von Lernkategorien.
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
                    this.currentUser = state.data
                    learnCategoryViewModel.getLearningCategories(this.currentUser)
                }
            }
        }
    }
}
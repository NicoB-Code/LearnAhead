package com.example.learnahead_prototyp.UI.Goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearningCategoryListingAdapter
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentLearningCategoryListBinding
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
    val TAG: String = "LearningCategoryListFragment"

    // Deklaration der benötigten Variablen
    lateinit var binding: FragmentLearningCategoryListBinding
    private val learnCategoryViewModel: LearnCategoryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var deletePosition: Int = -1
    var list: MutableList<LearningCategory> = arrayListOf()

    // Initialisierung des Adapters mit den entsprechenden Click-Callbacks
    private val adapter by lazy {
        LearningCategoryListingAdapter(
            onItemClicked = { pos, item ->
                // Navigation zum Lernkategorie-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_learningCategoryListFragment_to_learningCategoryInnerViewFragment,
                    Bundle().apply {
                        putString("type", "view")
                        putParcelable("learning_category", item)
                    })
            },
            onDeleteClicked = { pos, item ->
                // Speichern der zu löschenden Position und Löschen der Lernkategorie über das ViewModel
                deletePosition = pos
                learnCategoryViewModel.deleteLearningCategory(item)
                updateUserObject(item, true)
            }
        )
    }

    private fun updateUserObject(learningCategory: LearningCategory, deleteLearningCategory: Boolean) {
        if (deleteLearningCategory && currentUser != null)
            currentUser!!.learningCategories.remove(learningCategory)

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
        binding.recyclerView.adapter = adapter
    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    // Alle Event-Listener aufsetzen
    private fun setEventListener() {
        // Klick-Listener für den "Create"-Button, welcher zur "LearningCategoryDetailFragment" navigiert.
        binding.buttonAdd.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_learnCategoryDetailFragment) }

        // Klick-Listener für den "LearningCategory"-Button, welcher den Benutzer zum "HomeFragment" navigiert.
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_homeFragment)}

        // Klick-Listener für den "LearningCategory"-Button, welcher den Benutzer zur "LearningCategoryListFragment" navigiert.
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_goalListingFragment) }

        // Klick-Listener für den "Logout"-Button, welcher den Benutzer ausloggt und zur "LoginFragment" navigiert.
        binding.logout.setOnClickListener { authViewModel.logout { findNavController().navigate(R.id.action_learningCategoryListFragment_to_loginFragment) } }

        // Klick-Listener für den "Profil"-Button
        binding.profile.setOnClickListener { findNavController().navigate(R.id.action_learningCategoryListFragment_to_profileFragment)}
    }

    /**
     * Diese Funktion initialisiert alle Observer, welche die ViewModel-Objekte auf Veränderungen überwachen.
     */
    private fun observer() {
        // Observer für "learningCategory"-Objekt im "viewModel". Dieser überwacht alle Änderungen in der Liste der Lernkategorien.
        learnCategoryViewModel.learningCategory.observe(viewLifecycleOwner) { state ->
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
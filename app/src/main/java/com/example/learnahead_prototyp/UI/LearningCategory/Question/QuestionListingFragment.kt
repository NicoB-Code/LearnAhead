package com.example.learnahead_prototyp.UI.LearningCategory.Question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentQuestionListingBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [LearningCategoryListFragment] ist für die Anzeige der Liste der Lernkategorien zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einer Lernkategorie anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class QuestionListingFragment : Fragment() {

    private var currentUser: User? = null

    // Konstante für das Logging-Tag
    val TAG: String = "LearningCategoryListFragment"

    // Deklaration der benötigten Variablen
    lateinit var binding: FragmentQuestionListingBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private var deletePosition: Int = -1
    var list: MutableList<Question> = arrayListOf()

    private val adapter by lazy {
        QuestionListingAdapter(
            onItemClicked = { pos, item ->
                // Navigation zum Lernkategorie-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_questionListingFragment_to_questionDetailFragment,
                    Bundle().apply {
                        putString("type", "view")
                        putParcelable("question", item)
                    })
            },
            onDeleteClicked = { pos, item ->
                // Speichern der zu löschenden Position und Löschen der Lernkategorie über das ViewModel
                deletePosition = pos
                questionViewModel.deleteQuestion(item)
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
    ): View {
        // Das Binding-Objekt für das Fragment-Layout wird initialisiert.
        binding = FragmentQuestionListingBinding.inflate(layoutInflater)
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

        setEventListener()
        observer()
        setLocalCurrentUser()
        updateUI()

        // Setzen des Adapters auf die RecyclerView
        binding.recyclerView.adapter = adapter
    }

    private fun setLocalCurrentUser() {
        // Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable currentUser
        authViewModel.getSession()
    }
    private fun observer() {
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
                    questionViewModel.getQuestions(currentUser, learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                }
            }
        }

        // Observer für "learningCategory"-Objekt im "viewModel". Dieser überwacht alle Änderungen in der Liste der Lernkategorien.
        questionViewModel.question.observe(viewLifecycleOwner) { state ->
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
    }

    private fun updateUI() {
        // Retrieve the selected learning category from the shared view model
        val selectedLearningCategoryName = learnCategoryViewModel.currentSelectedLearningCategory.value?.name ?: ""

        // Set the text of the learning_goal_menu_header_label TextView
        binding.learningGoalMenuHeaderLabel.text = "$selectedLearningCategoryName / Fragen"
    }

    private fun setEventListener() {
        // Setzt den Event-Listener für den Home-Button
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_homeFragment)
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_goalListingFragment)
        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_questionListingFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_learningCategoryInnerViewFragment)
        }

        binding.buttonSaveQuestion.setOnClickListener {
                findNavController().navigate(R.id.action_questionListingFragment_to_questionDetailFragment)
        }
    }
}
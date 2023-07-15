package com.example.learnahead_prototyp.UI.LearningCategory.Question

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
 * Das [QuestionListingFragment] ist für die Anzeige der Liste der Fragen zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einer Frage anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class QuestionListingFragment : Fragment() {

    // Konstante für das Logging-Tag
    private val TAG: String = "QuestionListingFragment"

    // Deklaration der benötigten Variablen
    private lateinit var binding: FragmentQuestionListingBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private var deletePosition: Int = -1
    private var list: MutableList<Question> = arrayListOf()
    private var currentUser: User? = null
    private var searchQuery: String = ""

    private val adapter by lazy {
        QuestionListingAdapter(
            onItemClicked = { pos, item ->
                // Navigation zum Frage-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_questionListingFragment_to_questionDetailFragment,
                    Bundle().apply {
                        putString("type", "view")
                        putParcelable("question", item)
                    }
                )
            },
            onDeleteClicked = { pos, item ->
                // Speichern der zu löschenden Position und Löschen der Frage über das ViewModel
                deletePosition = pos
                questionViewModel.deleteQuestion(item)
            }
        )
    }

    /**
     * Erzeugt die View-Hierarchie für das Fragment, indem das entsprechende Binding Layout aufgeblasen wird.
     *
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
        binding = FragmentQuestionListingBinding.inflate(inflater, container, false)
        // Die erzeugte View-Instanz wird zurückgegeben.
        return binding.root
    }


    /**
     * Diese Funktion initialisiert die View und registriert alle Observer, die auf Veränderungen in den ViewModel-Objekten achten.
     *
     * @param view Die View der Fragment-Klasse
     * @param savedInstanceState Der gespeicherte Zustand des Fragments
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListeners()
        observe()
        setLocalCurrentUser()
        updateUI()

        // Setzen des Adapters auf die RecyclerView
        binding.questionsRecyclerView.adapter = adapter
    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable currentUser.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Registriert die Observer für Datenänderungen in den ViewModels.
     */
    private fun observe() {
        // Observer für das "currentUser"-Objekt im "authViewModel". Dieser überwacht alle Änderungen beim Abrufen des aktuellen Benutzers.
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
                    // Fortschrittsanzeige ausblenden, Erfolgsmeldung anzeigen und Fragen für die ausgewählte Lernkategorie abrufen
                    binding.progressBar.hide()
                    currentUser = state.data
                    questionViewModel.getQuestions(currentUser, learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                }
            }
        }

        // Observer für das "question"-Objekt im "questionViewModel". Dieser überwacht alle Änderungen in der Liste der Fragen.
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
                    // Fortschrittsanzeige ausblenden und Liste der Fragen aktualisieren
                    binding.progressBar.hide()
                    list = state.data.toMutableList()
                    adapter.updateList(list)
                }
            }
        }
    }

    /**
     * Aktualisiert die UI-Elemente basierend auf den Daten in den ViewModels.
     */
    private fun updateUI() {
        // Die ausgewählte Lernkategorie aus dem Shared ViewModel abrufen
        val selectedLearningCategoryName = learnCategoryViewModel.currentSelectedLearningCategory.value?.name ?: ""

        // Den Text des learningGoalMenuHeaderLabel TextViews setzen
        binding.headerLabel.text = "$selectedLearningCategoryName / Fragen"
    }

    /**
     * Führt eine Suche in der Frage-Liste basierend auf der eingegebenen Suchanfrage aus und zeigt die entsprechenden Ergebnisse an.
     */
    private fun performSearch() {
        val filteredList = if (searchQuery.isEmpty()) {
            // Wenn die Suchanfrage leer ist, werden alle Elemente angezeigt
            list.toMutableList()
        } else {
            // Filtert die Liste basierend auf der Suchanfrage
            list.filter { question ->
                question.tags.any { tag ->
                    tag.name.contains(searchQuery, ignoreCase = true)
                }
            }.toMutableList()
        }

        // Aktualisiert den Adapter mit der gefilterten Liste
        adapter.updateList(filteredList)
    }

    /**
     * Setzt die Event-Listener für die UI-Elemente.
     */
    private fun setEventListeners() {
        // Setzt die Suchfunktion auf
        binding.searchBarQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nicht benötigt
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Aktualisiert die Suchanfrage
                searchQuery = s.toString()

                // Führt die Suche aus
                performSearch()
            }

            override fun afterTextChanged(s: Editable?) {
                // Nicht benötigt
            }
        })

    // Setzt den Event-Listener für den Home-Button
        binding.homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_homeFragment)
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.learningGoalsButton.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_goalListingFragment)
        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.learningCategoriesButton.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_questionListingFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_learningCategoryInnerViewFragment)
        }

        // Setzt den Event-Listener für den Save Question-Button
        binding.addQuestionButton.setOnClickListener {
            findNavController().navigate(R.id.action_questionListingFragment_to_questionDetailFragment)
        }
    }
}

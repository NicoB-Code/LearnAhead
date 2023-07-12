package com.example.learnahead_prototyp.UI.LearningCategory.Test

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.Tag
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.Question.CustomSpinnerAdapter
import com.example.learnahead_prototyp.UI.LearningCategory.Question.QuestionViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentQuestionDetailBinding
import com.example.learnahead_prototyp.databinding.FragmentTestDetailBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [LearningCategoryListFragment] ist für die Anzeige der Liste der Lernkategorien zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einer Lernkategorie anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class TestDetailFragment : Fragment() {

    private var currentUser: User? = null

    // Konstante für das Logging-Tag
    val TAG: String = "LearningCategoryListFragment"

    // Deklaration der benötigten Variablen
    lateinit var binding: FragmentTestDetailBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val MAX_TAGS = 2
    private val isEdit: Boolean = false
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private var deletePosition: Int = -1
    private val tagsListString: MutableList<String> = mutableListOf()

    var list: MutableList<LearningCategory> = arrayListOf()

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
        binding = FragmentTestDetailBinding.inflate(layoutInflater)
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
        populateDropdown()
        updateUI()
    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    private fun observer() {
        // Eine Beobachtung auf viewModel.updateLearningCategory ausführen
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.progressBar.hide()
                    currentUser = state.data
                }
            }
        }
    }

    private fun populateDropdown() {
        val dropdownItems = listOf("Karteikarte - Umdrehen", "Weitere Fragen Arten folgen.") // Replace with your dropdown items

        val adapter = CustomSpinnerAdapter(requireContext(), R.layout.spinner_dropdown_item, dropdownItems)
        binding.dropdownElement.adapter = adapter
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
            findNavController().navigate(R.id.action_testDetailFragment_to_homeFragment)
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_testDetailFragment_to_goalListingFragment)
        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_testDetailFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_testDetailFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_testDetailFragment_to_testListingFragment)
        }

        binding.buttonSaveQuestion.setOnClickListener{
            if(isEdit)
                updateQuestion()
            else
                createQuestion()
        }
    }

    private fun updateQuestion() {

    }

    private fun createQuestion() {
        if (validation()) {
            //questionViewModel.addQuestion(getQuestion())
        }
    }

    private fun validation(): Boolean {
        return true
    }

}
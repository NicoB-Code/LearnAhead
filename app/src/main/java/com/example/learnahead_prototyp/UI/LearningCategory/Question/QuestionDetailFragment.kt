package com.example.learnahead_prototyp.UI.LearningCategory.Question

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
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentQuestionDetailBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [QuestionDetailFragment] ist für die Anzeige und Bearbeitung einer einzelnen Frage zuständig.
 * Es bietet Funktionen zum Erstellen, Aktualisieren und Validieren von Fragen und verwaltet die zugehörigen ViewModel-Objekte.
 * Diese Klasse ist mit [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class QuestionDetailFragment : Fragment() {

    private var currentUser: User? = null

    // Konstante für das Logging-Tag
    val TAG: String = "QuestionDetailFragment"

    // Deklaration der benötigten Variablen
    lateinit var binding: FragmentQuestionDetailBinding
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
        binding = FragmentQuestionDetailBinding.inflate(layoutInflater)
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

        observeViewModels()
        setLocalCurrentUser()
        setEventListeners()
        populateDropdown()
        updateUI()
    }

    /**
     * Setzt den aktuellen Benutzer lokal, indem die Sitzung über das AuthViewModel abgerufen wird.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Beobachtet die ViewModel-Objekte und reagiert auf Änderungen, indem entsprechende Aktionen ausgeführt werden.
     */
    private fun observeViewModels() {
        // Eine Beobachtung auf viewModel.addQuestion ausführen
        questionViewModel.addQuestion.observe(viewLifecycleOwner) { state ->
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
                    if (state.data != null && currentUser != null) {
                        learnCategoryViewModel.currentSelectedLearningCategory.value!!.questions.add(state.data)
                        learnCategoryViewModel.updateLearningCategory(learnCategoryViewModel.currentSelectedLearningCategory.value!!)

                        // Die neue Lernkategorie dem User hinzufügen
                        val indexOfCurrentObject =
                            currentUser!!.learningCategories.indexOfFirst { it.id == learnCategoryViewModel.currentSelectedLearningCategory.value!!.id }
                        if (indexOfCurrentObject != -1) {
                            currentUser!!.learningCategories[indexOfCurrentObject] = learnCategoryViewModel.currentSelectedLearningCategory.value!!
                        } else {
                            currentUser!!.learningCategories.add(learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                        }
                        // Den User in der DB updaten
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(R.id.action_questionDetailFragment_to_questionListingFragment)
                        toast("Die Lernkategorie konnte erfolgreich erstellt werden")
                    } else {
                        toast("Die Lernkategorie konnte nicht erstellt werden")
                    }
                }
            }
        }

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

    /**
     * Füllt das Dropdown-Menü mit den Dropdown-Elementen.
     */
    private fun populateDropdown() {
        val dropdownItems = listOf("Karteikarte - Umdrehen", "Weitere Fragen Arten folgen.") // Mit Ihren Dropdown-Elementen ersetzen

        val adapter = CustomSpinnerAdapter(requireContext(), R.layout.spinner_dropdown_item, dropdownItems)
        binding.dropdownElement.adapter = adapter
    }

    /**
     * Aktualisiert die Benutzeroberfläche basierend auf den aktuellen Werten im ViewModel.
     */
    private fun updateUI() {
        // Die ausgewählte Lernkategorie aus dem Shared ViewModel abrufen
        val selectedLearningCategoryName = learnCategoryViewModel.currentSelectedLearningCategory.value?.name ?: ""

        // Den Text des learning_goal_menu_header_label TextViews setzen
        binding.learningGoalMenuHeaderLabel.text = "$selectedLearningCategoryName / Fragen"
    }

    /**
     * Setzt die Event-Listener für verschiedene UI-Elemente.
     */
    private fun setEventListeners() {
        // Setzt den Event-Listener für den Home-Button
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_questionDetailFragment_to_homeFragment)
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_questionDetailFragment_to_goalListingFragment)
        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_questionDetailFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_questionDetailFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_questionDetailFragment_to_questionListingFragment)
        }

        binding.buttonSaveQuestion.setOnClickListener {
            if (isEdit)
                updateQuestion()
            else
                createQuestion()
        }

        binding.addTags.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val enteredText = binding.addTags.text.toString()
                if (enteredText.isNotBlank()) {
                    addTag(enteredText)
                    binding.addTags.text.clear()
                }
                true
            } else {
                false
            }
        }
    }

    /**
     * Aktualisiert die Frage mit den geänderten Werten.
     */
    private fun updateQuestion() {
        // TODO: Implementieren Sie die Logik zum Aktualisieren der Frage
    }

    /**
     * Erstellt eine neue Frage basierend auf den eingegebenen Werten.
     */
    private fun createQuestion() {
        if (validation()) {
            questionViewModel.addQuestion(getQuestion())
        }
    }

    /**
     * Gibt eine [Question] mit den eingegebenen Werten zurück.
     */
    private fun getQuestion(): Question {
        val tagsList = mutableListOf<Tag>()
        tagsListString.forEach { tagName ->
            val tag = Tag(
                name = tagName
            )
            tagsList.add(tag)
        }
        // Bei Erweiterung muss type noch hinzugefügt werden, da aktuell nur ein Fragen Typ existiert
        return Question(
            id = questionViewModel.currentQuestion.value?.id ?: "",
            question = binding.questionContent.text.toString(),
            answer = binding.answerContent.text.toString(),
            tags = tagsList
        )
    }

    /**
     * Führt die Validierung der eingegebenen Werte durch und gibt `true` zurück, wenn die Validierung erfolgreich ist, andernfalls `false`.
     */
    private fun validation(): Boolean {
        return true
    }

    /**
     * Fügt der Liste der Tags einen neuen Tag hinzu.
     *
     * @param tag Der hinzuzufügende Tag.
     */
    private fun addTag(tag: String) {
        if (tagsListString.size >= MAX_TAGS) {
            // Maximum limit reached, show a notification
            Toast.makeText(context, "Maximum limit of $MAX_TAGS tags reached.", Toast.LENGTH_SHORT).show()
            return
        }

        if (tagsListString.contains(tag)) {
            // Duplicate tag, show a notification
            Toast.makeText(context, "Tag '$tag' already exists.", Toast.LENGTH_SHORT).show()
            return
        }

        tagsListString.add(tag)

        val tagView = layoutInflater.inflate(R.layout.tag_item, null)
        val tagText = tagView.findViewById<TextView>(R.id.tag_text)
        val deleteTag = tagView.findViewById<ImageView>(R.id.delete_tag)

        tagText.text = tag
        deleteTag.setOnClickListener {
            removeTag(tagView.id)
        }

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        if (binding.tagsContainer.childCount > 0) {
            val previousTag = binding.tagsContainer.getChildAt(binding.tagsContainer.childCount - 1)
            layoutParams.addRule(RelativeLayout.BELOW, previousTag.id)

            val previousTagParams = previousTag.layoutParams as RelativeLayout.LayoutParams
            val previousTagRight = previousTagParams.leftMargin + previousTag.width

            val currentLineWidth = calculateCurrentLineWidth(binding.tagsContainer)
            val containerWidth = binding.tagsContainer.width

            if (containerWidth - currentLineWidth < tagView.measuredWidth) {
                layoutParams.addRule(RelativeLayout.BELOW, previousTag.id)
            } else {
                layoutParams.addRule(RelativeLayout.ALIGN_TOP, previousTag.id)
                layoutParams.leftMargin = previousTagRight + layoutParams.marginStart
            }
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
        }

        tagView.layoutParams = layoutParams

        val newTagId = View.generateViewId()
        tagView.id = newTagId

        tagView.visibility = View.VISIBLE // Set visibility to visible

        binding.tagsContainer.addView(tagView)
    }

    /**
     * Entfernt den angegebenen Tag aus der Liste der Tags.
     *
     * @param tagId Die ID des Tags, der entfernt werden soll.
     */
    private fun removeTag(tagId: Int) {
        val tagToRemove = binding.tagsContainer.findViewById<View>(tagId)
        val tagText = tagToRemove.findViewById<TextView>(R.id.tag_text)
        val tagValue = tagText.text.toString()

        tagsListString.remove(tagValue)
        binding.tagsContainer.removeView(tagToRemove)

        // Shift the remaining tags to the left
        val childCount = binding.tagsContainer.childCount

        for (i in 0 until childCount) {
            val tagView = binding.tagsContainer.getChildAt(i)

            // Update the left margin of the tag to shift it to the left
            val layoutParams = tagView.layoutParams as RelativeLayout.LayoutParams

            if (i > 0) {
                val previousTag = binding.tagsContainer.getChildAt(i - 1)
                val previousTagParams = previousTag.layoutParams as RelativeLayout.LayoutParams
                layoutParams.leftMargin = previousTagParams.leftMargin + previousTag.width
            } else {
                layoutParams.leftMargin = 0
            }

            // Apply the updated layout parameters to the tag view
            tagView.layoutParams = layoutParams
        }
    }

    /**
     * Berechnet die aktuelle Breite der Tags in der TagsContainer-ViewGroup.
     *
     * @param tagsContainer Die ViewGroup, die die Tags enthält.
     * @return Die aktuelle Breite der Tags in Pixeln.
     */
    private fun calculateCurrentLineWidth(tagsContainer: ViewGroup): Int {
        var currentLineWidth = 0
        var lastLineStartIndex = -1

        for (i in 0 until tagsContainer.childCount) {
            val tagView = tagsContainer.getChildAt(i)
            val tagParams = tagView.layoutParams as RelativeLayout.LayoutParams
            val tagWidth = tagView.width + tagParams.leftMargin + tagParams.rightMargin

            val newLine = currentLineWidth + tagWidth > tagsContainer.width
            if (newLine) {
                lastLineStartIndex = i
                break
            }

            currentLineWidth += tagWidth
        }

        // Calculate the total width of tags in the last line
        var totalWidth = 0
        if (lastLineStartIndex >= 0) {
            for (i in lastLineStartIndex until tagsContainer.childCount) {
                val tagView = tagsContainer.getChildAt(i)
                val tagParams = tagView.layoutParams as RelativeLayout.LayoutParams
                totalWidth += tagView.width + tagParams.leftMargin + tagParams.rightMargin
            }
        }

        return totalWidth
    }
}
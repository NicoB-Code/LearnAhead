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
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.Test
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.Question.CustomSpinnerAdapter
import com.example.learnahead_prototyp.UI.LearningCategory.Question.QuestionListingAdapter
import com.example.learnahead_prototyp.UI.LearningCategory.Question.QuestionViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentTestDetailBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [TestDetailFragment] ist für die Anzeige der Testdetails zuständig und bietet die Möglichkeit,
 * einen Test zu erstellen oder zu aktualisieren. Diese Klasse ist mit [AndroidEntryPoint] annotiert,
 * um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class TestDetailFragment : Fragment() {

    private var currentUser: User? = null

    /**
     * Das Logging-Tag für das Fragment.
     */
    private val TAG: String = "TestDetailFragment"

    // Deklaration der benötigten Variablen
    private lateinit var binding: FragmentTestDetailBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val isEdit: Boolean = false
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val testViewModel: TestViewModel by activityViewModels()
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private val tagsListString: MutableList<String> = mutableListOf()
    private var questionsToAddToTheTest: MutableList<Question> = mutableListOf()
    private var dropdownItems: MutableList<Question> = mutableListOf()
    private val tagsList: MutableList<String> = mutableListOf()
    private val adapter by lazy {
        QuestionListingAdapter(
            onItemClicked = { pos, item -> toast("Frage wurde hinzugefügt/entfernt")
            },
            onDeleteClicked = { pos, item ->
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
        binding = FragmentTestDetailBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Initialisiert die View und registriert alle Observer, die auf Veränderungen in den ViewModel-Objekten achten.
     * @param view Die View des Fragments.
     * @param savedInstanceState Der gespeicherte Zustand des Fragments.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        setLocalCurrentUser()
        setEventListener()
        populateDropdown()
        updateUI()

        binding.recyclerView.adapter = adapter
    }

    /**
     * Holt den aktuellen Benutzer aus dem [AuthViewModel].
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Registriert Observer für Änderungen in [TestViewModel] und [AuthViewModel].
     */
    private fun observer() {
        testViewModel.addTest.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    if (state.data != null && currentUser != null) {
                        learnCategoryViewModel.currentSelectedLearningCategory.value!!.tests.add(state.data)
                        learnCategoryViewModel.updateLearningCategory(learnCategoryViewModel.currentSelectedLearningCategory.value!!)

                        val indexOfCurrentObject = currentUser!!.learningCategories.indexOfFirst { it.id == learnCategoryViewModel.currentSelectedLearningCategory.value!!.id }
                        if (indexOfCurrentObject != -1) {
                            currentUser!!.learningCategories[indexOfCurrentObject] = learnCategoryViewModel.currentSelectedLearningCategory.value!!
                        } else {
                            currentUser!!.learningCategories.add(learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                        }
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(R.id.action_testDetailFragment_to_testListingFragment)
                        toast("Der Test konnte erfolgreich erstellt werden")
                    } else {
                        toast("Der Test konnte nicht erstellt werden")
                    }
                }
            }
        }

        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    currentUser = state.data
                }
            }
        }
    }

    private fun getQuestionDropdownItems(): MutableList<Question> {
        val currentLearningCategory = learnCategoryViewModel.currentSelectedLearningCategory.value

        // Erstellt eine Set-Version der Tags-Liste, um Duplikate zu entfernen
        val allTags = tagsList.toSet()

        // Filtert die Fragen der Lernkategorie basierend auf den ausgewählten Tags
        return currentLearningCategory?.questions?.filterNot { question ->
            question.tags.any { questionTag ->
                allTags.contains(questionTag.name)
            }
        }?.toMutableList()!!
    }

    private fun addCurrentManualQuestion() {
        var selectedQuestion = binding.dropdownElementManualQuestion.selectedItemPosition
        questionsToAddToTheTest.add(dropdownItems[selectedQuestion])
        toast("Frage manuell hinzugefügt")
    }

    /**
     * Füllt das Dropdown-Menü mit Elementen.
     */
    private fun populateDropdown() {
        dropdownItems = getQuestionDropdownItems()
        val dropdownItemsStrings = dropdownItems.map { it.question }

        //val dropdownItems = listOf("Karteikarte - Umdrehen", "Weitere Fragen Arten folgen.")
        val adapter = CustomSpinnerAdapter(requireContext(), R.layout.spinner_dropdown_item, dropdownItemsStrings)
        binding.dropdownElementManualQuestion.adapter = adapter
    }

    /**
     * Fügt einen Tag zur Liste der ausgewählten Tags hinzu.
     * @param tag Der hinzuzufügende Tag.
     */
    private fun addTag(tag: String) {
        // Holt die aktuell ausgewählte Lernkategorie aus dem ViewModel
        val currentLearningCategory = learnCategoryViewModel.currentSelectedLearningCategory.value

        // Überprüft, ob es eine Frage mit dem angegebenen Tag in der Lernkategorie gibt
        val hasQuestionWithTag = currentLearningCategory?.questions?.any { question ->
            question.tags.any { questionTag ->
                questionTag.name == tag
            }
        } ?: false

        // Wenn es keine Frage mit dem Tag gibt, wird eine Meldung angezeigt und die Funktion beendet
        if (!hasQuestionWithTag) {
            Toast.makeText(context, "Es gibt keine Frage mit dem Tag '$tag'.", Toast.LENGTH_SHORT).show()
            return
        }

        // Überprüft, ob der Tag bereits hinzugefügt wurde
        if (tagsList.contains(tag)) {
            Toast.makeText(context, "Du hast bereits die Fragen mit dem Tag '$tag' hinzugefügt.", Toast.LENGTH_SHORT).show()
            return
        }

        // Fügt den Tag zur Liste der ausgewählten Tags hinzu
        tagsList.add(tag)

        // Erstellt eine Set-Version der Tags-Liste, um Duplikate zu entfernen
        val allTags = tagsList.toSet()

        // Filtert die Fragen der Lernkategorie basierend auf den ausgewählten Tags
        questionsToAddToTheTest = currentLearningCategory?.questions?.filter { question ->
            question.tags.any { questionTag ->
                allTags.contains(questionTag.name)
            }
        }?.toMutableList()!!

        // Aktualisiert die RecyclerView-Liste der Fragen
        adapter.updateList(questionsToAddToTheTest)

        // Erstellt die Ansicht für den hinzugefügten Tag
        val tagView = layoutInflater.inflate(R.layout.tag_item_for_tests, null)
        val tagText = tagView.findViewById<TextView>(R.id.tag_text)
        val deleteTag = tagView.findViewById<ImageView>(R.id.delete_tag)

        // Setzt den Text des Tags
        tagText.text = tag

        // Fügt einen Klick-Listener hinzu, um den Tag zu entfernen
        deleteTag.setOnClickListener {
            removeTag(tagView)
        }

        // Definiert die Layout-Parameter für die Ansicht des Tags
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        // Wenn bereits andere Tags vorhanden sind, wird der aktuelle Tag darunter angeordnet
        if (binding.tagsContainer.childCount > 0) {
            val previousTag = binding.tagsContainer.getChildAt(binding.tagsContainer.childCount - 1)
            layoutParams.addRule(RelativeLayout.BELOW, previousTag.id)
            layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.tag_margin_top)
        } else {
            // Wenn es keine anderen Tags gibt, wird der aktuelle Tag am Anfang ausgerichtet
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
        }

        // Setzt die Layout-Parameter für die Ansicht des Tags
        tagView.layoutParams = layoutParams

        // Fügt die Tag-Ansicht zur Container-View hinzu
        binding.tagsContainer.addView(tagView)

        // Aktualisiert die Höhe des Containers basierend auf der Anzahl der Tags und deren Größe
        val containerHeight = binding.tagsContainer.height + tagView.height + resources.getDimensionPixelSize(R.dimen.tag_margin_top)
        val maxContainerHeight = resources.getDimensionPixelSize(R.dimen.max_tag_container_height)
        val tagsContainerLayoutParams = binding.tagsContainer.layoutParams
        tagsContainerLayoutParams.height = containerHeight.coerceAtMost(maxContainerHeight)
        binding.tagsContainer.layoutParams = tagsContainerLayoutParams

        // Scrollt zum Ende des Containers, um den neuen Tag anzuzeigen
        binding.tagsScrollView.post {
            binding.tagsScrollView.fullScroll(View.FOCUS_DOWN)
        }

        // Aktualisiert die Höhe des ScrollViews basierend auf der Anzahl der Tags
        val scrollViewLayoutParams = binding.tagsScrollView.layoutParams
        scrollViewLayoutParams.height = if (tagsList.size == 1) {
            resources.getDimensionPixelSize(R.dimen.tag_scroll_view_height_single_tag)
        } else {
            resources.getDimensionPixelSize(R.dimen.tag_scroll_view_height_multiple_tags)
        }
        binding.tagsScrollView.layoutParams = scrollViewLayoutParams
        populateDropdown()
    }

    /**
     * Entfernt einen Tag aus der Liste der ausgewählten Tags.
     * @param tagView Die Ansicht des Tags, die entfernt werden soll.
     */
    private fun removeTag(tagView: View) {
        val tagText = tagView.findViewById<TextView>(R.id.tag_text)
        val tagValue = tagText.text.toString()

        // Entfernt den Tag aus der Liste
        tagsList.remove(tagValue)

        // Entfernt die Tag-Ansicht aus dem Container
        binding.tagsContainer.removeView(tagView)

        // Aktualisiert die Fragenliste, indem die Fragen, die den entfernten Tag enthalten, entfernt werden
        questionsToAddToTheTest = questionsToAddToTheTest.filterNot { question ->
            question.tags.any { it.name == tagValue }
        }.toMutableList()

        // Aktualisiert die RecyclerView-Liste der Fragen
        adapter.updateList(questionsToAddToTheTest)

        // Aktualisiert die Höhe des Containers basierend auf der Anzahl der verbleibenden Tags und deren Größe
        val containerHeight = binding.tagsContainer.height - tagView.height - resources.getDimensionPixelSize(R.dimen.tag_margin_top)
        val tagsContainerLayoutParams = binding.tagsContainer.layoutParams
        tagsContainerLayoutParams.height = containerHeight
        binding.tagsContainer.layoutParams = tagsContainerLayoutParams

        // Aktualisiert die Höhe des ScrollViews basierend auf der Anzahl der verbleibenden Tags
        val scrollViewLayoutParams = binding.tagsScrollView.layoutParams
        scrollViewLayoutParams.height = if (tagsList.size == 1) {
            resources.getDimensionPixelSize(R.dimen.tag_scroll_view_height_single_tag)
        } else {
            if (tagsList.size == 0) {
                resources.getDimensionPixelSize(R.dimen.tag_scroll_view_height_no_tag)
            } else {
                resources.getDimensionPixelSize(R.dimen.tag_scroll_view_height_multiple_tags)
            }
        }
        binding.tagsScrollView.layoutParams = scrollViewLayoutParams
    }



    /**
     * Aktualisiert die Benutzeroberfläche des Fragments.
     */
    private fun updateUI() {
        val selectedLearningCategoryName = learnCategoryViewModel.currentSelectedLearningCategory.value?.name ?: ""
        binding.learningGoalMenuHeaderLabel.text = "$selectedLearningCategoryName / Fragen"
    }

    /**
     * Registriert Event-Listener für die verschiedenen UI-Elemente.
     */
    private fun setEventListener() {
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

        binding.checkboxManuallyAddQuestions.setOnCheckedChangeListener { _, isChecked ->
            binding.dropdownElementManualQuestion.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.buttonAddManualQuestion.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_testDetailFragment_to_homeFragment)
        }

        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_testDetailFragment_to_goalListingFragment)
        }

        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_testDetailFragment_to_learningCategoryListFragment)
        }

        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_testDetailFragment_to_loginFragment)
            }
        }

        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_testDetailFragment_to_testListingFragment)
        }

        binding.buttonAddManualQuestion.setOnClickListener{
            addCurrentManualQuestion()
        }

        binding.buttonSaveTest.setOnClickListener {
            if (isEdit)
                updateTest()
            else
                createTest()
        }
    }

    /**
     * Aktualisiert den Test.
     */
    private fun updateTest() {
        // Implement updateTest functionality
    }

    /**
     * Erstellt einen neuen Test.
     */
    private fun createTest() {
        if (validation()) {
            testViewModel.addTest(getTest())
        }
    }

    /**
     * Ruft die Informationen für den Test ab.
     * @return Der erstellte [Test].
     */
    private fun getTest(): Test {
        return Test(
            id = testViewModel.currentTest.value?.id ?: "",
            name = binding.testTitle.text.toString(),
            questions = questionsToAddToTheTest
        )
    }

    /**
     * Überprüft die Validität des erstellten Tests.
     * @return [true], wenn der Test gültig ist, ansonsten [false].
     */
    private fun validation(): Boolean {
        // Implement validation logic
        return true
    }
}

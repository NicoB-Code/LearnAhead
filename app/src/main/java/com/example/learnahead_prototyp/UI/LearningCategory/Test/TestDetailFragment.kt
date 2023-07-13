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
    private val isEdit: Boolean = false
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val testViewModel: TestViewModel by activityViewModels()
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private val tagsListString: MutableList<String> = mutableListOf()
    private var questionsToAddToTheTest: MutableList<Question> = mutableListOf()
    private val tagsList: MutableList<String> = mutableListOf()
    private val adapter by lazy {
        QuestionListingAdapter(
            onItemClicked = { pos, item ->

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

        binding.recyclerView.adapter = adapter
    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    private fun observer() {
        // Eine Beobachtung auf viewModel.addLearningCategory ausführen
        testViewModel.addTest.observe(viewLifecycleOwner) { state ->
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
                    if(state.data != null && currentUser != null) {
                        learnCategoryViewModel.currentSelectedLearningCategory.value!!.tests.add(state.data)
                        learnCategoryViewModel.updateLearningCategory(learnCategoryViewModel.currentSelectedLearningCategory.value!!)

                        // Die neue Lernkategorie dem User hinzufügen
                        val indexOfCurrentObject = currentUser!!.learningCategories.indexOfFirst { it.id == learnCategoryViewModel.currentSelectedLearningCategory.value!!.id }
                        if (indexOfCurrentObject != -1) {
                            currentUser!!.learningCategories[indexOfCurrentObject] = learnCategoryViewModel.currentSelectedLearningCategory.value!!
                        } else {
                            currentUser!!.learningCategories.add(learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                        }
                        // Den User in der DB updaten
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(R.id.action_testDetailFragment_to_testListingFragment)
                        toast("Der Test konnte erfolgreich erstellt werden")
                    }
                    else {
                        toast("Der Test konnte nicht erstellt werden")
                    }
                }
            }
        }

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
        binding.dropdownElementManualQuestion.adapter = adapter
    }

    private fun addTag(tag: String) {
        val currentLearningCategory = learnCategoryViewModel.currentSelectedLearningCategory.value
        val hasQuestionWithTag = currentLearningCategory?.questions?.any { question ->
            question.tags.any { questionTag ->
                questionTag.name == tag
            }
        } ?: false

        if (!hasQuestionWithTag) {
            // No question with the tag exists
            Toast.makeText(context, "Es gibt keine Frage mit dem Tag '$tag'.", Toast.LENGTH_SHORT).show()
            return
        }

        if (tagsList.contains(tag)) {
            // Duplicate tag, show a notification
            Toast.makeText(context, "Du hast bereits die Fragen mit dem Tag '$tag' hinzugefügt.", Toast.LENGTH_SHORT).show()
            return
        }

        tagsList.add(tag)

        val allTags = tagsList.toSet() // Convert the tagsList to a Set to remove duplicates

        questionsToAddToTheTest = currentLearningCategory?.questions?.filter { question ->
            question.tags.any { questionTag ->
                allTags.contains(questionTag.name)
            }
        }?.toMutableList()!!

        adapter.updateList(questionsToAddToTheTest)

        val tagView = layoutInflater.inflate(R.layout.tag_item_for_tests, null)
        val tagText = tagView.findViewById<TextView>(R.id.tag_text)
        val deleteTag = tagView.findViewById<ImageView>(R.id.delete_tag)

        tagText.text = tag
        deleteTag.setOnClickListener {
            removeTag(tagView)
        }

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        if (binding.tagsContainer.childCount > 0) {
            val previousTag = binding.tagsContainer.getChildAt(binding.tagsContainer.childCount - 1)
            layoutParams.addRule(RelativeLayout.BELOW, previousTag.id)

            // Adjust the top margin to create spacing between tags
            layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.tag_margin_top)
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
        }

        tagView.layoutParams = layoutParams

        binding.tagsContainer.addView(tagView)

        // Update the position of add_tags EditText
        val containerHeight = binding.tagsContainer.height + tagView.height + resources.getDimensionPixelSize(R.dimen.tag_margin_top)
        val maxContainerHeight = resources.getDimensionPixelSize(R.dimen.max_tag_container_height)
        val tagsContainerLayoutParams = binding.tagsContainer.layoutParams
        tagsContainerLayoutParams.height = containerHeight.coerceAtMost(maxContainerHeight)
        binding.tagsContainer.layoutParams = tagsContainerLayoutParams

        // Scroll to the bottom of the tagsContainer
        binding.tagsScrollView.post {
            binding.tagsScrollView.fullScroll(View.FOCUS_DOWN)
        }

        // Update the height of the scrollView
        val scrollViewLayoutParams = binding.tagsScrollView.layoutParams
        scrollViewLayoutParams.height = if (tagsList.size == 1) {
            resources.getDimensionPixelSize(R.dimen.tag_scroll_view_height_single_tag)
        } else {
            resources.getDimensionPixelSize(R.dimen.tag_scroll_view_height_multiple_tags)
        }
        binding.tagsScrollView.layoutParams = scrollViewLayoutParams
    }


    private fun removeTag(tagView: View) {
        val tagText = tagView.findViewById<TextView>(R.id.tag_text)
        val tagValue = tagText.text.toString()

        tagsList.remove(tagValue)
        binding.tagsContainer.removeView(tagView)
    }

    private fun updateUI() {
        // Retrieve the selected learning category from the shared view model
        val selectedLearningCategoryName = learnCategoryViewModel.currentSelectedLearningCategory.value?.name ?: ""

        // Set the text of the learning_goal_menu_header_label TextView
        binding.learningGoalMenuHeaderLabel.text = "$selectedLearningCategoryName / Fragen"
    }

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

        // Inside onViewCreated() method
        binding.checkboxManuallyAddQuestions.setOnCheckedChangeListener { _, isChecked ->
            binding.dropdownElementManualQuestion.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

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

        binding.buttonSaveTest.setOnClickListener{
            if(isEdit)
                updateTest()
            else
                createTest()
        }
    }

    private fun updateTest() {

    }

    private fun createTest() {
        if (validation()) {
            testViewModel.addTest(getTest())
        }
    }

    private fun getTest(): Test {
        return Test(
            id = testViewModel.currentTest.value?.id ?: "",
            name = binding.testTitle.text.toString(),
            questions = questionsToAddToTheTest
        )
    }

    private fun validation(): Boolean {
        return true
    }

}
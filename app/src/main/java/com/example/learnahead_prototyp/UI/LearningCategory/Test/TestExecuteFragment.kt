package com.example.learnahead_prototyp.UI.LearningCategory.Test

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.Question.QuestionViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentTestExecuteBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [LearningCategoryListFragment] ist für die Anzeige der Liste der Lernkategorien zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einer Lernkategorie anzuzeigen. Diese Klasse ist mit
 * [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class TestExecuteFragment : Fragment() {

    private var currentUser: User? = null

    // Konstante für das Logging-Tag
    val TAG: String = "LearningCategoryListFragment"

    // Deklaration der benötigten Variablen
    private lateinit var cardView: CardView
    private lateinit var linearLayout: LinearLayout
    private lateinit var flipAnimation: AnimatorSet
    lateinit var binding: FragmentTestExecuteBinding
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val testViewModel: TestViewModel by activityViewModels()
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private lateinit var currentQuestion: Question
    private var isShowingQuestion = true
    private var questionsToAnswer: MutableList<Question> = arrayListOf()
    var questionsAfterAnswering: MutableList<Question> = arrayListOf()


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
        binding = FragmentTestExecuteBinding.inflate(layoutInflater)
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

        questionsToAnswer = testViewModel.currentTest.value!!.questions
        initAnimation(view)
        setRandomQuestion()
        setEventListener()
        observer()
        setLocalCurrentUser()
    }

    @SuppressLint("ResourceType")
    private fun initAnimation(view: View) {
        // Initialize views
        cardView = view.findViewById(R.id.card_view)
        linearLayout = view.findViewById(R.id.index_card_box)

        // Load flip animation from XML
        flipAnimation = AnimatorInflater.loadAnimator(requireContext(), R.anim.flip_animation) as AnimatorSet

        // Set click listener for the LinearLayout
        linearLayout.setOnClickListener {
            flipCard()
        }
    }

    private fun setRandomQuestion() {
        // Check if the questions list is not empty
        if (!questionsToAnswer.isNullOrEmpty()) {
            isShowingQuestion = true
            // Get a random question index
            val randomQuestionIndex = (0 until questionsToAnswer.size).random()

            // Get the random question
            val randomQuestion = questionsToAnswer[randomQuestionIndex]

            // Set the question text in the TextView
            questionViewModel.setCurrentQuestion(randomQuestion)
            binding.addQuestionsByTagsLabel.text = questionViewModel.currentQuestion.value!!.question
        } else {
            // No more questions remaining
            testViewModel.currentTest.value!!.questions = questionsAfterAnswering
            learnCategoryViewModel.currentSelectedLearningCategory.value?.let { currentLearningCategory ->
                val indexOfTest = currentLearningCategory.tests.indexOfFirst { it.id == testViewModel.currentTest.value?.id }
                if (indexOfTest != -1) {
                    currentLearningCategory.tests[indexOfTest] = testViewModel.currentTest.value!!
                    learnCategoryViewModel.updateLearningCategory(currentLearningCategory)
                }
            }

            // Die neue Lernkategorie dem User hinzufügen
            val indexOfCurrentObject = currentUser!!.learningCategories.indexOfFirst { it.id == learnCategoryViewModel.currentSelectedLearningCategory.value!!.id }
            if (indexOfCurrentObject != -1) {
                currentUser!!.learningCategories[indexOfCurrentObject] = learnCategoryViewModel.currentSelectedLearningCategory.value!!
            } else {
                currentUser!!.learningCategories.add(learnCategoryViewModel.currentSelectedLearningCategory.value!!)
            }
            // Den User in der DB updaten
            authViewModel.updateUserInfo(currentUser!!)

            binding.addQuestionsByTagsLabel.text = "Test beendet"
            toast("Test beendet")
            findNavController().navigate(R.id.action_testExecuteFragment_to_homeFragment)
        }
    }

    private fun flipCard() {
        if (isShowingQuestion) {
            // Show the answer
            binding.addQuestionsByTagsLabel.text = questionViewModel.currentQuestion.value!!.answer
        } else {
            // Show the question
            binding.addQuestionsByTagsLabel.text = questionViewModel.currentQuestion.value!!.question
        }
        // Create ObjectAnimator for rotationY property
        val anim1 = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f)
        anim1.interpolator = AccelerateDecelerateInterpolator()
        anim1.duration = 250

        // Create ObjectAnimator for rotationY property for flip back
        val anim2 = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f)
        anim2.interpolator = AccelerateDecelerateInterpolator()
        anim2.duration = 250
        anim2.startDelay = 250

        // Combine the animations into AnimatorSet
        flipAnimation = AnimatorSet()
        flipAnimation.playSequentially(anim1, anim2)

        // Start the flip animation
        flipAnimation.start()

        // Perform actions when the card is flipped
        // For example, show the answer or perform other actions here
        isShowingQuestion = !isShowingQuestion
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
                    testViewModel.getTest(currentUser, learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                }
            }
        }
    }
    private fun setLocalCurrentUser() {
        // Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable currentUser
        authViewModel.getSession()
    }

    @SuppressLint("SetTextI18n")
    private fun setEventListener() {
        // Setzt den Event-Listener für den Home-Button
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_homeFragment)
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_goalListingFragment)
        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_testExecuteFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_testListingFragment)
        }

        binding.buttonCorrect.setOnClickListener {
            questionViewModel.currentQuestion.value!!.lastTest = true
            questionViewModel.currentQuestion.value!!.wrongCounter = 0
            questionViewModel.currentQuestion.value?.let { currentQuestion ->
                questionsAfterAnswering.add(currentQuestion)
                questionsToAnswer.remove(currentQuestion)
            }

            // Set a new random question
            setRandomQuestion()
        }

        binding.buttonFalse.setOnClickListener {
            questionViewModel.currentQuestion.value!!.lastTest = false
            questionViewModel.currentQuestion.value!!.wrongCounter += 1
            questionViewModel.currentQuestion.value?.let { currentQuestion ->
                questionsAfterAnswering.add(currentQuestion)
                questionsToAnswer.remove(currentQuestion)
            }

            // Set a new random question
            setRandomQuestion()
        }
    }
}
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
 * Ein Fragment, das den Testablauf für eine Lernkategorie durchführt. Das Fragment zeigt Fragen an,
 * ermöglicht das Beantworten der Fragen und zeigt die Ergebnisse an. Es verwendet ViewModels für die
 * Kommunikation mit anderen Komponenten und nutzt die Hilt Injection für die Abhängigkeiten.
 *
 * @constructor Erzeugt eine Instanz des TestExecuteFragment.
 */
@AndroidEntryPoint
class TestExecuteFragment : Fragment() {

    private var currentUser: User? = null

    // Das Tag für das Logging

    val TAG: String = "TestListingFragment"

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
        binding = FragmentTestExecuteBinding.inflate(layoutInflater)
        // Die erzeugte View-Instanz wird zurückgegeben.
        return binding.root
    }

    /**
     * Diese Funktion initialisiert die View und registriert alle Observer, die auf Veränderungen in den ViewModel-Objekten achten.
     *
     * @param view Die View der Fragment-Klasse.
     * @param savedInstanceState Der gespeicherte Zustand des Fragments.
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

    /**
     * Initialisiert die Animation für das Kartenflipping.
     *
     * @param view Die View des Fragments.
     */
    @SuppressLint("ResourceType")
    private fun initAnimation(view: View) {
        // Initialisiere die Views
        cardView = view.findViewById(R.id.card_view)
        linearLayout = view.findViewById(R.id.index_card_box)

        // Lade die Flip-Animation aus XML
        flipAnimation = AnimatorInflater.loadAnimator(requireContext(), R.anim.flip_animation) as AnimatorSet

        // Setze den Click-Listener für das LinearLayout
        linearLayout.setOnClickListener {
            flipCard()
        }
    }

    /**
     * Wählt eine zufällige Frage aus der Liste der zu beantwortenden Fragen aus und zeigt diese an.
     * Wenn keine Fragen mehr vorhanden sind, werden die Ergebnisse angezeigt und der Test beendet.
     */
    private fun setRandomQuestion() {
        // Überprüfe, ob die Liste der Fragen nicht leer ist
        if (!questionsToAnswer.isNullOrEmpty()) {
            isShowingQuestion = true
            // Wähle einen zufälligen Index für die Frage aus
            val randomQuestionIndex = (0 until questionsToAnswer.size).random()

            // Hole die zufällige Frage
            val randomQuestion = questionsToAnswer[randomQuestionIndex]

            // Setze den Fragetext im TextView
            questionViewModel.setCurrentQuestion(randomQuestion)
            binding.addQuestionsByTagsLabel.text = questionViewModel.currentQuestion.value!!.question
        } else {
            // Keine Fragen mehr übrig
            testViewModel.currentTest.value!!.questions = questionsAfterAnswering
            learnCategoryViewModel.currentSelectedLearningCategory.value?.let { currentLearningCategory ->
                val indexOfTest = currentLearningCategory.tests.indexOfFirst { it.id == testViewModel.currentTest.value?.id }
                if (indexOfTest != -1) {
                    currentLearningCategory.tests[indexOfTest] = testViewModel.currentTest.value!!
                    learnCategoryViewModel.updateLearningCategory(currentLearningCategory)
                }
            }

            // Füge die aktualisierte Lernkategorie dem Benutzer hinzu
            val indexOfCurrentObject = currentUser!!.learningCategories.indexOfFirst { it.id == learnCategoryViewModel.currentSelectedLearningCategory.value!!.id }
            if (indexOfCurrentObject != -1) {
                currentUser!!.learningCategories[indexOfCurrentObject] = learnCategoryViewModel.currentSelectedLearningCategory.value!!
            } else {
                currentUser!!.learningCategories.add(learnCategoryViewModel.currentSelectedLearningCategory.value!!)
            }
            // Aktualisiere den Benutzer in der Datenbank
            authViewModel.updateUserInfo(currentUser!!)

            binding.addQuestionsByTagsLabel.text = "Test beendet"
            toast("Test beendet")
            findNavController().navigate(R.id.action_testExecuteFragment_to_homeFragment)
        }
    }

    /**
     * Führt das Kartenflipping durch. Zeigt entweder die Frage oder die Antwort der aktuellen Karte an.
     */
    private fun flipCard() {
        if (isShowingQuestion) {
            // Zeige die Antwort an
            binding.addQuestionsByTagsLabel.text = questionViewModel.currentQuestion.value!!.answer
        } else {
            // Zeige die Frage an
            binding.addQuestionsByTagsLabel.text = questionViewModel.currentQuestion.value!!.question
        }
        // Erzeuge ObjectAnimator für die rotationY-Eigenschaft
        val anim1 = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f)
        anim1.interpolator = AccelerateDecelerateInterpolator()
        anim1.duration = 250

        // Erzeuge ObjectAnimator für die rotationY-Eigenschaft für das Zurückflipping
        val anim2 = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f)
        anim2.interpolator = AccelerateDecelerateInterpolator()
        anim2.duration = 250
        anim2.startDelay = 250

        // Kombiniere die Animationen zu einem AnimatorSet
        flipAnimation = AnimatorSet()
        flipAnimation.playSequentially(anim1, anim2)

        // Starte die Flip-Animation
        flipAnimation.start()

        // Führe Aktionen aus, wenn die Karte umgedreht wird
        // Zum Beispiel die Antwort anzeigen oder andere Aktionen durchführen
        isShowingQuestion = !isShowingQuestion
    }

    /**
     * Registriert einen Observer, der auf Änderungen des aktuellen Benutzers achtet.
     * Wird verwendet, um den aktuellen Benutzer aus der Datenbank abzurufen und zu speichern.
     */
    private fun observer() {
        // Observer für das "currentUser"-Objekt im "authViewModel". Überwacht alle Änderungen beim Abrufen des aktuellen Benutzers.
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Zeige den Fortschrittsbalken an
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    // Verberge den Fortschrittsbalken und zeige eine Fehlermeldung an
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    // Verberge den Fortschrittsbalken, zeige eine Erfolgsmeldung an und hole den Test für die Lernkategorie des Benutzers
                    binding.progressBar.hide()
                    this.currentUser = state.data
                    testViewModel.getTests(currentUser, learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                }
            }
        }
    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable [currentUser].
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Setzt die Event-Listener für die verschiedenen Buttons und Symbole in der Benutzeroberfläche.
     */
    @SuppressLint("SetTextI18n")
    private fun setEventListener() {
        // Event-Listener für den Home-Button
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_homeFragment)
        }

        // Event-Listener für den Learning Goals-Button
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_goalListingFragment)
        }

        // Event-Listener für den Learning Categories-Button
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_learningCategoryListFragment)
        }

        // Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_testExecuteFragment_to_loginFragment)
            }
        }

        // Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_testExecuteFragment_to_testListingFragment)
        }

        // Event-Listener für den "Richtig"-Button
        binding.buttonCorrect.setOnClickListener {
            questionViewModel.currentQuestion.value!!.lastTest = true
            questionViewModel.currentQuestion.value!!.wrongCounter = 0
            questionViewModel.currentQuestion.value?.let { currentQuestion ->
                questionsAfterAnswering.add(currentQuestion)
                questionsToAnswer.remove(currentQuestion)
            }

            // Setze eine neue zufällige Frage
            setRandomQuestion()
        }

        // Event-Listener für den "Falsch"-Button
        binding.buttonFalse.setOnClickListener {
            questionViewModel.currentQuestion.value!!.lastTest = false
            questionViewModel.currentQuestion.value!!.wrongCounter += 1
            questionViewModel.currentQuestion.value?.let { currentQuestion ->
                questionsAfterAnswering.add(currentQuestion)
                questionsToAnswer.remove(currentQuestion)
            }

            // Setze eine neue zufällige Frage
            setRandomQuestion()
        }
    }
}
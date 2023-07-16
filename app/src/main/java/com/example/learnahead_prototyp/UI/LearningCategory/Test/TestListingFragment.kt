package com.example.learnahead_prototyp.UI.LearningCategory.Test

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.Test
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentTestListingBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das [TestListingFragment] ist für die Anzeige der Liste der Tests zuständig und bietet auch die Möglichkeit,
 * diese zu bearbeiten, zu löschen oder detaillierte Informationen zu einem Test anzuzeigen.
 * Diese Klasse ist mit [AndroidEntryPoint] annotiert, um die Injection von [ViewModel]s zu ermöglichen.
 */
@AndroidEntryPoint
class TestListingFragment : Fragment() {

    private var currentUser: User? = null

    /**
     * Konstante für das Logging-Tag.
     */
    private val TAG: String = "TestListingFragment"

    // Deklaration der benötigten Variablen
    private lateinit var binding: FragmentTestListingBinding
    private val learnCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val testViewModel: TestViewModel by activityViewModels()
    private var testList: MutableList<Test> = arrayListOf()
    private var deletePosition: Int = -1

    private val adapter: TestListingAdapter by lazy {
        TestListingAdapter(
            onItemClicked = { pos, item ->
                testViewModel.setCurrentTest(item)
                findNavController().navigate(
                    R.id.action_testListingFragment_to_testExecuteFragment,
                    Bundle().apply {
                        putParcelable("test", item)
                    }
                )
            },
            onDeleteClicked = { pos, item ->
                // Speichern der zu löschenden Position und Löschen des Tests über das ViewModel
                deletePosition = pos
                testViewModel.deleteTest(item)
                updateUserObject(item, true)

            },
            onEditClicked = { pos, item ->
                testViewModel.setCurrentTest(item)
                findNavController().navigate(
                    R.id.action_testListingFragment_to_testDetailFragment
                )
            }
        )
    }

    private fun updateUserObject(test: Test, deleteTest: Boolean) {
        if (deleteTest && currentUser != null) {
            learnCategoryViewModel.currentSelectedLearningCategory.value?.tests?.remove(test)
            val foundIndex =
                currentUser!!.learningCategories.indexOfFirst { it.id == learnCategoryViewModel.currentSelectedLearningCategory.value?.id }
            if (foundIndex != -1) {
                currentUser!!.learningCategories[foundIndex] = learnCategoryViewModel.currentSelectedLearningCategory.value!!
            }
            currentUser?.let { authViewModel.updateUserInfo(it) }
            toast("Test erfolgreich gelöscht")
        }
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
        binding = FragmentTestListingBinding.inflate(inflater, container, false)
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

        setEventListener()
        observeViewModels()
        setLocalCurrentUser()
        updateUI()

        binding.testsRecyclerView.adapter = adapter
    }

    /**
     * Diese Funktion beobachtet die Änderungen in den ViewModels.
     */
    private fun observeViewModels() {
        // Observer für das "currentUser"-Objekt im "authViewModel". Überwacht Änderungen am aktuellen Benutzer.
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
                    // Fortschrittsanzeige ausblenden, Erfolgsmeldung anzeigen und Tests abrufen
                    binding.progressBar.hide()
                    currentUser = state.data
                    testViewModel.getTests(currentUser, learnCategoryViewModel.currentSelectedLearningCategory.value!!)
                }
            }
        }


        testViewModel.deleteTest.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Zeige den Fortschrittsbalken an
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    // Verberge den Fortschrittsbalken und zeige die Fehlermeldung an
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    if (deletePosition != -1) {
                        testList.removeAt(deletePosition)
                        adapter.updateList(testList)
                        deletePosition = -1
                    }
                }
            }
        }

        // Observer für das "tests"-Objekt im "testViewModel". Überwacht Änderungen in der Liste der Tests.
        testViewModel.tests.observe(viewLifecycleOwner) { state ->
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
                    // Fortschrittsanzeige ausblenden und Testliste aktualisieren
                    binding.progressBar.hide()
                    testList = state.data.toMutableList()
                    adapter.updateList(testList)
                }
            }
        }
    }

    /**
     * Diese Funktion setzt den aktuellen Benutzer lokal.
     */
    private fun setLocalCurrentUser() {
        // Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable currentUser
        authViewModel.getSession()
    }

    /**
     * Diese Funktion aktualisiert die UI-Komponenten.
     */
    private fun updateUI() {
        // Ruft den Namen der ausgewählten Lernkategorie aus dem gemeinsam genutzten ViewModel ab
        val selectedLearningCategoryName = learnCategoryViewModel.currentSelectedLearningCategory.value?.name ?: ""

        // Setzt den Text des TextViews "testListingMenuHeaderLabel"
        binding.testListingMenuHeaderLabel.text = "$selectedLearningCategoryName / Tests"
    }

    /**
     * Diese Funktion weist den Event-Listenern die entsprechenden Aktionen zu.
     */
    @SuppressLint("SetTextI18n")
    private fun setEventListener() {
        // Setzt den Event-Listener für den Home-Button
        binding.homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_testListingFragment_to_homeFragment)
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.learningGoalsButton.setOnClickListener {
            findNavController().navigate(R.id.action_testListingFragment_to_goalListingFragment)
        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.learningCategoriesButton.setOnClickListener {
            findNavController().navigate(R.id.action_testListingFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_testListingFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_testListingFragment_to_learningCategoryInnerViewFragment)
        }

        // Setzt den Event-Listener für den "Create Test"-Button
        binding.createTestButton.setOnClickListener {
            findNavController().navigate(R.id.action_testListingFragment_to_testDetailFragment)
        }
    }
}

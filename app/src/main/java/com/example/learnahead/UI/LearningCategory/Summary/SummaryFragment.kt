package com.example.learnahead.UI.LearningCategory.Summary

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.Summary
import com.example.learnahead.Data.Model.User
import com.example.learnahead.R
import com.example.learnahead.UI.Auth.AuthViewModel
import com.example.learnahead.Util.UiState
import com.example.learnahead.Util.hide
import com.example.learnahead.Util.show
import com.example.learnahead.Util.toast
import com.example.learnahead.databinding.FragmentSummaryBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Ein einfaches [Fragment]-Unterklasse.
 * Verwenden Sie die [SummaryFragment.newInstance]-Methode, um eine Instanz dieses Fragments zu erstellen.
 */
@AndroidEntryPoint
class SummaryFragment : Fragment() {
    private var currentUser: User? = null
    private lateinit var binding: FragmentSummaryBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val summaryViewModel: SummaryViewModel by viewModels()
    private var currentLearningCategory: LearningCategory? = null
    private var deletePosition: Int = -1
    val TAG: String = "SummaryFragment"
    var list: MutableList<Summary> = arrayListOf()

    private val adapter by lazy {
        SummaryAdapter(
            onItemClicked = { item ->
                findNavController().navigate(
                    R.id.action_summaryFragment_to_innerSummaryFragment,
                    Bundle().apply {
                        putString("type", "view")
                        putParcelable("summary", item)
                        putParcelable("learning_category", currentLearningCategory)
                        putBoolean("markdown", true)
                    }
                )
            },
            onDeleteClicked = { pos, item ->
                deletePosition = pos
                summaryViewModel.deleteSummary(item)
                updateUserObject(item, true)
            }
        )
    }

    /**
     * Erstellt die View-Hierarchie des Fragments.
     * @param inflater Der LayoutInflater, der verwendet wird, um die View-Hierarchie aufzubauen.
     * @param container Der ViewGroup, in die die View-Hierarchie eingefügt wird.
     * @param savedInstanceState Das Bundle, das den Zustand des Fragments enthält.
     * @return Die View-Hierarchie des Fragments.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Die Layout-Datei für dieses Fragment aufblasen
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Wird aufgerufen, wenn die View-Hierarchie des Fragments erstellt wurde.
     * @param view Die View, die das Fragment darstellt.
     * @param savedInstanceState Das Bundle, das den Zustand des Fragments enthält.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Event-Listener einstellen, LiveData-Objekte beobachten und UI aktualisieren
        observer()
        setLocalCurrentUser()
        setEventListener()
        updateUI()

        // Adapter für das RecyclerView setzen
        binding.summariesRecyclerView.adapter = adapter
    }

    /**
     * Aktualisiert das Benutzerobjekt.
     * @param summary Das Summary-Objekt, das aktualisiert wurde.
     * @param deleteSummary Gibt an, ob das Summary-Objekt gelöscht wurde.
     */
    private fun updateUserObject(summary: Summary, deleteSummary: Boolean) {
        if (deleteSummary && currentUser != null) {
            currentLearningCategory?.summaries?.remove(summary)
            val foundIndex =
                currentUser!!.learningCategories.indexOfFirst { it.id == currentLearningCategory?.id }
            if (foundIndex != -1) {
                currentUser!!.learningCategories[foundIndex] = currentLearningCategory!!
            }
            currentUser?.let { authViewModel.updateUserInfo(it) }
        }
    }

    /**
     * Aktualisiert die Benutzeroberfläche des Fragments.
     */
    private fun updateUI() {
        // Lernkategorie aus den Argumenten erhalten und den Text des Labels setzen
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.headerLabel.text = currentLearningCategory?.name
    }

    /**
     * Den aktuellen Benutzer aus der Datenbank abrufen.
     */
    private fun setLocalCurrentUser() {
        // Aktuellen Benutzer aus der Datenbank abrufen und in der Variable "currentUser" speichern
        authViewModel.getSession()
    }

    /**
     * Event-Listener für Buttons und Views des Fragments einstellen.
     */
    private fun setEventListener() {
        // Event-Listener für den Home-Button einstellen
        binding.homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_homeFragment)
        }

        binding.addSummaryButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_summaryFragment_to_createSummaryFragment,
                Bundle().apply {
                    putString("type", "view")
                    putParcelable("learning_category", currentLearningCategory)
                })
        }

        // Event-Listener für den Learning Goals-Button einstellen
        binding.learningGoalsButton.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_goalListingFragment)
        }

        // Event-Listener für den Learning Categories-Button einstellen
        binding.learningCategoriesButton.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_learningCategoryListFragment)
        }

        // Event-Listener für den Logout-Button einstellen
        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Ausloggen")
                    .setMessage("Möchtest du dich wirklich ausloggen?")
                    .setPositiveButton("Ja") { _, _ ->
                        findNavController().navigate(R.id.action_summaryFragment_to_loginFragment)
                    }
                    .setNegativeButton("Nein", null)
                    .show()
            }
        }

        // Event-Listener für das Back-Icon einstellen
        binding.backIcon.setOnClickListener {
            findNavController().navigate(
                R.id.action_summaryFragment_to_learningCategoryInnerViewFragment,
                Bundle().apply {
                    putString("type", "view")
                    putParcelable("learning_category", currentLearningCategory)
                })
        }
    }

    /**
     * Beobachtet die LiveData-Objekte der ViewModels und aktualisiert die Benutzeroberfläche entsprechend.
     */
    private fun observer() {
        // Beobachtet die LiveData-Objekte des SummaryViewModels und aktualisiert die Benutzeroberfläche entsprechend
        summaryViewModel.summary.observe(viewLifecycleOwner) { state ->
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
                    list = state.data.toMutableList()
                    adapter.updateList(list)
                }
            }
        }
        summaryViewModel.deleteSummary.observe(viewLifecycleOwner) { state ->
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
                        list.removeAt(deletePosition)
                        adapter.updateList(list)
                        deletePosition = -1
                    }
                }
            }
        }

        // Beobachtet die LiveData-Objekte des AuthViewModels und aktualisiert die Benutzeroberfläche entsprechend
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Failure -> {
                    toast(state.error)
                    View.GONE
                }
                is UiState.Success -> {
                    currentUser = state.data
                    currentLearningCategory?.let { summaryViewModel.getSummaries(currentUser, it) }
                    View.GONE
                }
            }
        }
    }
}

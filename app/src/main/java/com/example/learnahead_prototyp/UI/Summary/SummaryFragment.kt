package com.example.learnahead_prototyp.UI.Summary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.databinding.FragmentSummaryBinding
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.Goal.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast


/**
 * A simple [Fragment] subclass.
 * Use the [SummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SummaryFragment : Fragment() {
    private var currentUser: User? = null
    private lateinit var binding: FragmentSummaryBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val summaryViewModel: SummaryViewModel by viewModels()
    private var currentLearningCategory: LearningCategory?= null
    private var deletePosition: Int = -1
    val TAG: String = "SummaryFragment"
    var list: MutableList<Summary> = arrayListOf()

    private val adapter by lazy {
        SummaryAdapter(
            onItemClicked = { pos, item ->
            findNavController().navigate(
                R.id.action_summaryFragment_to_innerSummaryFragment,
                Bundle().apply {
                    putString("type","view")
                    putParcelable("summary", item)
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
        // Inflate the layout for this fragment
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

        // Setzt die Event-Listener, beobachtet die LiveData-Objekte und aktualisiert die UI
        observer()
        setLocalCurrentUser()
        setEventListener()
        updateUI()


        // Setzt den Adapter für das RecyclerView
        binding.recyclerSummaryView.adapter = adapter
    }


    /**
     * Aktualisiert die UI des Fragments.
     */
    private fun updateUI() {
        // Holt die Lernkategorie aus den Argumenten und setzt den Text des Labels
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.learningGoalMenuHeaderLabel.text = currentLearningCategory?.name

    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank.
     */
    private fun setLocalCurrentUser() {
        // Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable currentUser
        authViewModel.getSession()
    }

    /**
     * Setzt die Event-Listener für die Buttons und Views des Fragments.
     */
    private fun setEventListener() {
        // Setzt den Event-Listener für den Home-Button
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_learningCategoryInnerViewFragment_to_homeFragment)
        }

        binding.buttonAddSummary.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_createSummaryFragment,
                Bundle().apply {
                    putString("type","view")
                    putParcelable("learning_category", currentLearningCategory)
                })
        }

        // Setzt den Event-Listener für den Learning Goals-Button
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_goalListingFragment)
        }

        // Setzt den Event-Listener für den Learning Categories-Button
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_learningCategoryListFragment)
        }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_summaryFragment_to_loginFragment)
            }
        }

        // Setzt den Event-Listener für das Back-Icon
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_learningCategoryInnerViewFragment,
                Bundle().apply {
                    putString("type","view")
                    putParcelable("learning_category", currentLearningCategory)
                })
        }

    }

    /**
     * Beobachtet die LiveData-Objekte der ViewModels und aktualisiert die UI entsprechend.
     */
    private fun observer() {
        // Beobachtet die LiveData-Objekte des SummaryViewModels und aktualisiert die UI entsprechend
        summaryViewModel.summary.observe(viewLifecycleOwner) { state ->
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
                    Log.d("Success", "Haalahlihihl")
                    binding.progressBar.hide()
                    list = state.data.toMutableList()
                    Log.d("SummaryAdapter", "List Size: ${list.size}")
                    adapter.updateList(list)
                }
            }
        }
        summaryViewModel.deleteSummary.observe(viewLifecycleOwner) { state ->
            when(state) {
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
                    binding.progressBar.hide()
                    toast(state.data)
                    if (deletePosition != -1) {
                        list.removeAt(deletePosition)
                        Log.d("SummaryAdapter", "List Size: ${list.size}")
                        adapter.updateList(list)
                        deletePosition = -1
                    }
                }
                }
            }

        // Beobachtet die LiveData-Objekte des AuthViewModels und aktualisiert die UI entsprechend
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Failure -> {
                    toast(state.error)
                    View.GONE
                }
                is UiState.Success -> {
                    Log.d(TAG,"Success")
                    currentUser = state.data
                    currentLearningCategory?.let { summaryViewModel.getSummaries(currentUser, it) }
                    View.GONE
                }
            }
        }
    }
}
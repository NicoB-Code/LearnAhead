package com.example.learnahead.UI.Home

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.User
import com.example.learnahead.R
import com.example.learnahead.UI.Auth.AuthViewModel
import com.example.learnahead.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead.Util.UiState
import com.example.learnahead.Util.hide
import com.example.learnahead.Util.show
import com.example.learnahead.Util.toast
import com.example.learnahead.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Ein Fragment für den Home-Bildschirm der App.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val learningCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adapter by lazy {
        HomeAdapter(this::onItemClicked)
    }
    var pointsBefore = 0
    private var currentUser: User? = null
    private var learningCategoryList: MutableList<LearningCategory> = mutableListOf()

    /**
     * Erstellt die View des Fragments.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setLocalCurrentUser()
        setupEventListeners()
        currentUser?.let { authViewModel.updateUserInfo(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("App schließen")
                    .setMessage("Möchten Sie die App wirklich beenden?")
                    .setPositiveButton("Ja") { _, _ ->
                        requireActivity().finish()
                    }
                    .setNegativeButton("Nein", null)
                    .show()
            }
        })
    }

    /**
     * Ruft die aktuelle Benutzersitzung ab.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }



    /**
     * Setzt die Event-Listener für die Buttons und RecyclerView.
     */
    private fun setupEventListeners() {
        binding.todaysLearningGoalsRecyclerView.adapter = adapter

        binding.learningCategoriesButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_learningCategoryListFragment)
        }

        binding.learningGoalsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_goalListingFragment)
        }

        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Ausloggen")
                    .setMessage("Möchtest du dich wirklich ausloggen?")
                    .setPositiveButton("Ja") { _, _ ->
                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                    }
                    .setNegativeButton("Nein", null)
                    .show()
            }
        }
        // Klick Listener für den Info Knopf
        binding.infoIcon.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Information")
                .setMessage("Willkommen bei LearnAhead!\n" +
                        "Hierbei handelt es sich um eine mobile Lern-App von zwei Studenten." +
                        "Hier auf dem Home-Screen werden dir deine heutigen Lerneinheiten angezeigt.\n" +
                        "Wenn du noch keine hast erstell doch mal welche, indem du unten in der Leiste auf Lernziele klickst.\n" +
                        "Unter Lernkategorien kannst du dir eine eigene Lernkategorie erstellen.\n" +
                        "In Lernkategorien gibt es Zusammenfassungen um Lerninhalte zu kondensieren.\n" +
                        "Bei Fragen/Tests kannst du Tests erstellen welche Fragen enthalten. Probier dich doch einfach mal durch!")
                .setPositiveButton("Danke!") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

        }

        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    /**
     * Setzt die Observer für die Datenaktualisierungen.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        learningCategoryViewModel.learningCategories.observe(viewLifecycleOwner) { state ->
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
                    learningCategoryList = state.data.toMutableList()
                    val today = LocalDate.now()

                    val filteredList = state.data.filter { learningCategory ->
                        val goal = learningCategory.relatedLearningGoal
                        val startDate = goal?.startDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                        val endDate = goal?.endDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

                        if (startDate != null && endDate != null && today >= startDate) {
                            val daysBetweenDates = ChronoUnit.DAYS.between(startDate, endDate)
                            val daysSinceStart = ChronoUnit.DAYS.between(startDate, today)

                            val interval = if (daysBetweenDates < 10) {
                                1 // Set interval to 1 if daysBetweenDates is less than 10
                            } else {
                                ((daysBetweenDates.toDouble() + 1) / 10).toInt() // Calculate the interval
                            }

                            val learningDayIndex = if (interval > 0) {
                                (daysSinceStart / interval) + 1
                            } else {
                                0
                            }

                            val nextLearningDay = startDate.plusDays((learningDayIndex - 1) * interval)

                            today == nextLearningDay && today != startDate
                        } else {
                            false
                        }
                    }.toMutableList()
                    val todaysLearningGoal = state.data.filter { learningCategory ->
                        val goal = learningCategory.relatedLearningGoal
                        val startDate = goal?.startDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

                        startDate == today
                    }.toMutableList()
                    filteredList.addAll(todaysLearningGoal)

                    val sortedList = filteredList.sortedBy { it.relatedLearningGoal?.endDate }.toMutableList()

                    adapter.updateList(sortedList)
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
                    pointsBefore = currentUser?.currentPoints!!
                    learningCategoryViewModel.getLearningCategories(currentUser)
                }
            }
        }

        authViewModel.updateUserInfo.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Failure -> {
                    toast(state.error)
                    View.GONE
                }
                is UiState.Success -> {
                    val pointsNow =currentUser?.currentPoints!!
                    if(pointsBefore != pointsNow ){
                        val changedPoints = pointsNow- pointsBefore
                        toast("Du hast gerade $changedPoints Punkte für deinen Login erhalten!")
                    }
                    View.GONE
                }
            }
        }
    }

    /**
     * Handler für den Klick auf ein Item in der RecyclerView.
     */
    private fun onItemClicked(item: LearningCategory) {
        learningCategoryViewModel.setCurrentSelectedLearningCategory(item)
        findNavController().navigate(
            R.id.action_homeFragment_to_learningCategoryInnerViewFragment,
        )
    }
}

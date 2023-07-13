package com.example.learnahead_prototyp.UI.Home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.LearningCategory.LearnCategoryViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var currentUser: User? = null

    lateinit var binding: FragmentHomeBinding
    private val learningCategoryViewModel: LearnCategoryViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    var list: MutableList<LearningCategory> = arrayListOf()

    // Initialisierung des Adapters mit den entsprechenden Click-Callbacks
    val adapter by lazy {
        HomeAdapter(
            onItemClicked = { pos, item ->
                // Navigation zum Ziel-Detail-Fragment mit Parameter-Übergabe
                findNavController().navigate(
                    R.id.action_goalListingFragment_to_goalDetailFragment,
                    Bundle().apply {
                        putParcelable("goal", item)
                    })
            }
        )
    }

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

        observer()
        setLocalCurrentUser()
        // Wir updaten den User um zu überprüfen ob er sich einen Login Bonus verdient hat
        currentUser?.let { authViewModel.updateUserInfo(it) }
        setEventListener()


    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    private fun setEventListener() {
        binding.recyclerView.adapter = adapter

        // Klick Listener zum Weiterleiten auf den Lern Kategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernziele Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_goalListingFragment) }

        // Klick-Listener für den "Logout"-Button, welcher den Benutzer ausloggt und zur "LoginFragment" navigiert.
        binding.logout.setOnClickListener { authViewModel.logout { findNavController().navigate(R.id.action_homeFragment_to_loginFragment) } }

        binding.profile.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observer() {
        // Observer für "goal"-Objekt im "viewModel". Dieser überwacht alle Änderungen in der Liste der Benutzerziele.
        learningCategoryViewModel.learningCategory.observe(viewLifecycleOwner) { state ->
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
                    // Hide progress bar and update the list of user goals
                    binding.progressBar.hide()
                    list = state.data.toMutableList()
                    val today = LocalDate.now()

                    val filteredList = if (list.isNotEmpty()) {
                        // Filter the learning categories based on the criteria
                        list.filter { learningCategory ->
                            val goal = learningCategory.relatedLearningGoal
                            val startDate = goal?.startDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                            val endDate = goal?.endDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

                            if (startDate != null && endDate != null) {
                                val daysBetweenDates = ChronoUnit.DAYS.between(startDate, endDate)

                                val interval = if (daysBetweenDates != 0L) {
                                    Math.round(daysBetweenDates.toFloat() / 10).toLong() // Calculate the interval
                                } else {
                                    1L // Set a default interval value of 1 if daysBetweenDates is zero
                                }

                                // Check if today is within the interval for learning
                                val daysSinceStart = ChronoUnit.DAYS.between(startDate, today)
                                val daysRemaining = ChronoUnit.DAYS.between(today, endDate)

                                // Calculate the number of learning days within the interval
                                val learningDays = if (daysRemaining > 0) {
                                    (daysBetweenDates - daysRemaining) / interval
                                } else {
                                    (daysBetweenDates - 1) / interval
                                }

                                val isLearningDay = daysSinceStart % interval <= learningDays
                                isLearningDay
                            } else {
                                false
                            }
                        }.toMutableList()
                    } else {
                        // Empty list, no need to filter
                        mutableListOf()
                    }


                    adapter.updateList(filteredList)
                }
            }
        }

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
                    learningCategoryViewModel.getLearningCategories(this.currentUser)
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
                    toast("Du hast für deinen Login gerade $state.data erhalten.")
                    View.GONE
                }
            }
        }
    }
}
package com.example.learnahead_prototyp.UI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private var currentUser: User? = null
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun setLocalCurrentUser() {
        // Holt den aktuellen Benutzer aus der Datenbank und speichert ihn in der Variable currentUser
        authViewModel.getSession()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        setLocalCurrentUser()
        currentUser?.let { authViewModel.updateUserInfo(it) }
        // Klick Listener zum Weiterleiten auf den Lern Kategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernziele Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_goalListingFragment) }

        // Klick-Listener für den "Logout"-Button, welcher den Benutzer ausloggt und zur "LoginFragment" navigiert.
        binding.logout.setOnClickListener { authViewModel.logout { findNavController().navigate(R.id.action_homeFragment_to_loginFragment) } }

        binding.profile.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }
    }

    private fun observer() {
        // Beobachtet die LiveData-Objekte des SummaryViewModels und aktualisiert die UI entsprechend


        // Beobachtet die LiveData-Objekte des AuthViewModels und aktualisiert die UI entsprechend
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Failure -> {
                    toast(state.error)
                    View.GONE
                }
                is UiState.Success -> {
                    currentUser = state.data
                    View.GONE
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
                    toast("Du hast für deinen Login gerade $state erhalten.")
                    View.GONE
                }
            }
        }
    }
}
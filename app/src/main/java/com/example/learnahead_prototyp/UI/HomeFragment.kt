package com.example.learnahead_prototyp.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Klick Listener zum Weiterleiten auf den Lern Kategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernziele Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_goalListingFragment) }

        // Klick-Listener für den "Logout"-Button, welcher den Benutzer ausloggt und zur "LoginFragment" navigiert.
        binding.logout.setOnClickListener { authViewModel.logout { findNavController().navigate(R.id.action_homeFragment_to_loginFragment) } }

        binding.profile.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }
    }
}
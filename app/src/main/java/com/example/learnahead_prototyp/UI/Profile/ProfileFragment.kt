package com.example.learnahead_prototyp.UI.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das Profilfragment zeigt das Profil des aktuellen Benutzers an.
 * Hier kann der Benutzer sein Profilbild ändern und seine Lernfortschritte anzeigen.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var currentUser: User
    private val viewModelAuth: AuthViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()

    /**
     * Der GalleryLauncher wird verwendet, um ein Bild aus der Galerie auszuwählen.
     * Wenn ein Bild ausgewählt wurde, wird es an die Funktion [viewModelProfile.onUploadSingleFile] übergeben.
     */
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let {
                viewModelProfile.onUploadSingleFile(it, currentUser)
                loadImageFromUrl(currentUser.profileImageUrl)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        setLocalCurrentUser()
        updateUI()
    }

    /**
     * Funktion zum Laden des aktuellen Benutzers aus der lokalen Sitzung.
     */
    private fun setLocalCurrentUser() {
        viewModelAuth.getSession()
    }

    /**
     * Funktion zum Aktualisieren der Benutzeroberfläche.
     * Hier werden die Klick-Listener für die Schaltflächen und das Profilbild festgelegt.
     */
    private fun updateUI() {
        binding.apply {
            buttonHome.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }

            buttonLearningCategories.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_learningCategoryListFragment)
            }

            buttonLearningGoals.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_goalListingFragment)
            }

            backIcon.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }

            profilePic.setOnClickListener {
                galleryLauncher.launch("image/*")
            }
        }
    }

    /**
     * Funktion zum Beobachten von Änderungen im ViewModel.
     * Hier wird der Zustand der Datei-Uploads und des aktuellen Benutzers beobachtet und die Benutzeroberfläche entsprechend aktualisiert.
     */
    private fun observer() {
        viewModelProfile.fileUris.observe(viewLifecycleOwner) { state ->
            binding.btnProgressAr.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Success -> {
                    viewModelAuth.storeSession(currentUser)
                    toast(state.data)
                    View.GONE
                }
                is UiState.Failure -> {
                    toast(state.error)
                    View.GONE
                }
            }
        }

        viewModelAuth.currentUser.observe(viewLifecycleOwner) { state ->
            binding.btnProgressAr.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Success -> {
                    currentUser = state.data
                    binding.apply {
                        usernameDisplay.text = currentUser.username
                        passwordDisplay.text = currentUser.password
                        emailDisplay.text = currentUser.email
                        learningStreakDisplay.text = currentUser.learningStreak.toString()
                        achievedGoalsDisplay.text = currentUser.achievedGoals.toString()
                    }
                    loadImageFromUrl(currentUser.profileImageUrl)
                    View.GONE
                }
                is UiState.Failure -> {
                    toast(state.error)
                    View.GONE
                }
            }
        }
    }

    /**
     * Funktion zum Laden eines Bildes aus einer URL in das Profilbild.
     * @param imageUrl Die URL des Bildes, das geladen werden soll.
     */
    private fun loadImageFromUrl(imageUrl: String) {
        Log.d(TAG, "loading imageURL into profilepic- $imageUrl")
        context?.let {
            Glide.with(it)
                .load(imageUrl)
                .apply(RequestOptions().override(300, 300).placeholder(R.drawable.profile_image_placeholder).centerCrop())
                .into(binding.profilePic)
        }
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}
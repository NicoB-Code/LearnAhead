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
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das Profilfragment zeigt das Profil des aktuellen Benutzers an.
 * Hier kann der Benutzer sein Profilbild ändern und seine Lernfortschritte anzeigen.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // Binding-Objekt für die Layout-Datei des Fragments
    private lateinit var binding: FragmentProfileBinding

    // Aktueller Benutzer
    private lateinit var currentUser: User

    // ViewModels für die Authentifizierung und das Profil
    private val viewModelAuth: AuthViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()

    /**
     * Der GalleryLauncher wird verwendet, um ein Bild aus der Galerie auszuwählen.
     * Wenn ein Bild ausgewählt wurde, wird es an die Funktion [viewModelProfile.onUploadSingleFile] übergeben.
     */
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let {
                // Das ausgewählte Bild wird an die Funktion onUploadSingleFile() übergeben
                viewModelProfile.onUploadSingleFile(it, currentUser)
                // Das Profilbild wird aktualisiert
                loadImageFromUrl(currentUser.profileImageUrl)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Das Layout aufblasen und das Binding-Objekt initialisieren
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Beobachter und UI-Update-Funktionen initialisieren
        observer()
        setLocalCurrentUser()
        updateUI()
    }

    /**
     * Funktion zum Laden des aktuellen Benutzers aus der lokalen Sitzung.
     */
    private fun setLocalCurrentUser() {
        // Die Funktion getSession() des AuthViewModels wird aufgerufen, um den aktuellen Benutzer aus der lokalen Sitzung zu laden
        viewModelAuth.getSession()
    }

    /**
     * Funktion zum Aktualisieren der Benutzeroberfläche.
     * Hier werden die Klick-Listener für die Schaltflächen und das Profilbild festgelegt.
     */
    private fun updateUI() {
        binding.apply {
            // Klick-Listener für die Schaltflächen festlegen
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

            // Klick-Listener für das Profilbild festlegen
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
                    // Die aktuelle Sitzung wird im AuthViewModel gespeichert
                    viewModelAuth.storeSession(currentUser)
                    // Eine Toast-Nachricht wird angezeigt, um den Benutzer über den erfolgreichen Upload zu informieren
                    toast(state.data)
                    View.GONE
                }
                is UiState.Failure -> {
                    // Eine Toast-Nachricht wird angezeigt, um den Benutzer über den fehlgeschlagenen Upload zu informieren
                    toast(state.error)
                    View.GONE
                }
            }
        }

        viewModelAuth.currentUser.observe(viewLifecycleOwner) { state ->
            binding.btnProgressAr.visibility = when (state) {
                is UiState.Loading -> View.VISIBLE
                is UiState.Success -> {
                    // Der aktuelle Benutzer wird aus dem ViewModel geladen
                    currentUser = state.data
                    binding.apply {
                        // Die Benutzerdaten werden in die Benutzeroberfläche eingefügt
                        usernameDisplay.text = currentUser.username
                        passwordDisplay.text = currentUser.password
                        emailDisplay.text = currentUser.email
                        learningStreakDisplay.text = currentUser.learningStreak.toString()
                        achievedGoalsDisplay.text = currentUser.achievedGoals.toString()
                    }
                    // Das Profilbild wird aus der URL des Benutzers geladen
                    loadImageFromUrl(currentUser.profileImageUrl)
                    View.GONE
                }
                is UiState.Failure -> {
                    // Eine Toast-Nachricht wird angezeigt, um den Benutzer über den fehlgeschlagenen Ladevorgang zu informieren
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
            // Das Bild wird mit Glide aus der URL geladen und in das Profilbild eingefügt
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
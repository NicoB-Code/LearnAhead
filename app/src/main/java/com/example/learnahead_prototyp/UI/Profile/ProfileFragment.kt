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

// Constant for Android Systems Intent mechanism to launch the gallery app
private const val PICK_IMAGE_REQUEST = 123


/**
 * Das ist die ProfileFragment-Klasse, die für die Anmeldung von Benutzern zuständig ist.
 * Sie ist mit dem AndroidEntryPoint-Annotation versehen, um sicherzustellen, dass die erforderlichen
 * Abhängigkeiten im Fragment eingesetzt werden.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var currentUser: User? = null

    // Konstante für das Logging-Tag
    val TAG: String = "ProfileFragment"

    // Viewmodel-Objekte, um die Geschäftslogiken von AuthViewModel und ProfileViewModel zu nutzen
    private val viewModelAuth: AuthViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()

    // Binding-Objekt für die Layout-Datei "fragment_login.xml"
    lateinit var binding: FragmentProfileBinding

    // Aufrufen der handy-internen Gallerie
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            // hole einen User, damit wir seine Daten verarbeiten können
            if (result != null && currentUser != null)
                viewModelProfile.onUploadSingleFile(result, currentUser!!)
            else
                Log.e(TAG, "Error in galleryLauncer")
        }

    // wird ausgeführt, wenn die Benutzeroberfläche erstellt wird
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Diese Funktion lädt ein Bild per URL in das Profilbild
     * @param imageUrl String der in das Profilbild geladen werden soll.
     */
    private fun loadImageFromUrl(imageUrl: String) {
        Log.d(TAG, "loading imageURL into profilepic- $imageUrl")
        context?.let {
            Glide.with(it)
                .load(imageUrl)
                // override the image if its size does not match our requirements and crop if not a square
                .apply(RequestOptions().override(300,300).placeholder(R.drawable.profile_image_placeholder).centerCrop())
                .into(binding.profilePic)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Eine Beobachtung auf viewModel.addGoal ausführen
        observer()
        setLocalCurrentUser()
        updateUI()
    }

    private fun setLocalCurrentUser() {
        viewModelAuth.getSession()
    }

    private fun updateUI() {
        // Hole User und aktualisiere die angezeigten Informationen
        Log.e(TAG, "UpdateUI currentUser holen")

        // Handler für Button Click
        binding.profilePic.setOnClickListener {
            // Wenn Button gedrückt, wähle Bild aus der Gallerie aus
            galleryLauncher.launch("image/*")

            // Wenn Bild geholt, dann aktualisiere Session und lade Bild neu
            if(currentUser != null)
                loadImageFromUrl(currentUser!!.profileImageUrl)
        }
        // Setze Listener für Buttons für Navigation
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_homeFragment) }

        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_learningCategoryListFragment) }

        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_goalListingFragment) }

        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_homeFragment) }
    }

    private fun observer() {
        viewModelProfile.fileUris.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    // if profileURL was succesfully updated, make sure that our user is actually the user we want
                    // because if the user has been updated, we need to store a new session
                    viewModelAuth.storeSession(currentUser!!)
                    binding.btnProgressAr.hide()
                    toast(state.data)
                }
            }
        }

        viewModelAuth.currentUser.observe(viewLifecycleOwner) { state ->
            // Zustand des Ladevorgangs - Fortschrittsanzeige anzeigen
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }
                // Fehlerzustand - Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }
                // Erfolgszustand - Fortschrittsanzeige ausblenden und Erfolgsmeldung anzeigen
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    currentUser = state.data
                    binding.usernameDisplay.text = currentUser!!.username
                    binding.passwordDisplay.text = currentUser!!.password
                    binding.emailDisplay.text = currentUser!!.email
                    binding.learningStreakDisplay.text = currentUser!!.learningStreak.toString()
                    binding.achievedGoalsDisplay.text = currentUser!!.achievedGoals.toString()
                    loadImageFromUrl(currentUser!!.profileImageUrl)
                }
            }
        }
    }
}
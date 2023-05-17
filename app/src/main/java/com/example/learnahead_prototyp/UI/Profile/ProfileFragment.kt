package com.example.learnahead_prototyp.UI.Profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Constant for Android Systems Intent mechanism to launch the gallery app
private const val PICK_IMAGE_REQUEST = 123


/**
 * Das ist die ProfileFragment-Klasse, die für die Anmeldung von Benutzern zuständig ist.
 * Sie ist mit dem AndroidEntryPoint-Annotation versehen, um sicherzustellen, dass die erforderlichen
 * Abhängigkeiten im Fragment eingesetzt werden.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // Konstante für das Logging-Tag
    val TAG: String = "ProfileFragment"

    // Viewmodel-Objekte, um die Geschäftslogiken von AuthViewModel und ProfileViewModel zu nutzen
    val viewModelAuth: AuthViewModel by viewModels()
    val viewModelProfile: ProfileViewModel by viewModels()
    // Binding-Objekt für die Layout-Datei "fragment_login.xml"
    lateinit var binding: FragmentProfileBinding

    // Aufrufen der handy-internen Gallerie
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
        // hole einen User, damit wir seine Daten verarbeiten können
        viewModelAuth.getSession { user ->
            if(result != null) {
                if (user != null) {
                    // um sicherzugehen, dass User in App und Datenbank übereinstimmen, muss
                    // hier die aktuelle Session in den internen Speicher geschrieben werden
                    viewModelAuth.storeSession(user){userNew ->
                        if(userNew != null){
                            // beginne upload von ausgewähltem Bild
                            viewModelProfile.onUploadSingleFile(result, userNew) { state ->
                                when (state) {
                                    is UiState.Loading -> {
                                        Log.d(TAG, "Loading...")
                                    }
                                    is UiState.Failure -> {
                                        Log.e(TAG, "Error while trying to upload image")
                                    }
                                    is UiState.Success -> {
                                        Log.d(TAG, "Success!")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // wird ausgeführt, wenn die Benutzeroberfläche erstellt wird
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        // Hole User und aktualisiere die angezeigten Informationen
        viewModelAuth.getSession { user ->
            if (user != null) {
                viewModelAuth.storeSession(user) { userNew ->
                    if (userNew != null) {
                        binding.usernameDisplay.text = userNew.username
                        binding.emailDisplay.text = userNew.email
                        binding.learnstreakDisplay.text = userNew.learningStreak.toString()
                        loadImageFromUrl(userNew.profileImageUrl)
                    }
                }
            }
        }
        // Handler für Button Click
        binding.profilePic.setOnClickListener {
            // Wenn Button gedrückt, wähle Bild aus der Gallerie aus
            galleryLauncher.launch("image/*")
            // Wenn Bild geholt, dann aktualisiere Session und lade Bild neu
            viewModelAuth.getSession { user ->
                if (user != null) {
                    viewModelAuth.storeSession(user) { userNew ->
                        if (userNew != null) {
                            loadImageFromUrl(userNew.profileImageUrl)
                        }
                    }
                }
            }
        }
        // Setze Listener für Buttons für Navigation
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
        binding.buttonLearningCategories.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_learningCategoryListFragment)
        }
        binding.buttonLearningGoals.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_goalListingFragment)
        }
        binding.backIcon.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
        return binding.root
    }

    /**
     * Diese Funktion lädt ein Bild per URL in das Profilbild
     * @param imageUrl String der in das Profilbild geladen werden soll.
     */
    fun loadImageFromUrl(imageUrl: String) {
        Log.d(TAG, "loading imageURL into profilepic- $imageUrl")
        context?.let {
            Glide.with(it)
                .load(imageUrl)
                .into(binding.profilePic)
        }

    }
}
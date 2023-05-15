package com.example.learnahead_prototyp.UI.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * Das ist die ProfileFragment-Klasse, die für die Anmeldung von Benutzern zuständig ist.
 * Sie ist mit dem AndroidEntryPoint-Annotation versehen, um sicherzustellen, dass die erforderlichen
 * Abhängigkeiten im Fragment eingesetzt werden.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // Konstante für das Logging-Tag
    val TAG: String = "ProfileFragment"

    // Binding-Objekt für die Layout-Datei "fragment_login.xml"
    lateinit var binding: FragmentProfileBinding

    // Viewmodel-Objekt, um die Geschäftslogik von AuthViewModel zu nutzen
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        val usernameTextView = binding.usernameDisplay
        val emailTextView = binding.eMailDisplay
        val passwordTextview = binding.passwordDisplay
        val learnstreakTextView = binding.learningStreakDisplay
        val learngoalsTextView = binding.learningGoalsDisplay

        viewModel.getSession { user ->
            if (user != null) {
                usernameTextView.text = user.username
                emailTextView.text = user.email
                learnstreakTextView.text = user.learningStreak.toString()
            }
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}
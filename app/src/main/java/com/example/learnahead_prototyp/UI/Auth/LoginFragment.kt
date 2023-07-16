package com.example.learnahead_prototyp.UI.Auth

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.isValidEmail
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Das LoginFragment ist für die Anmeldung von Benutzern zuständig.
 * Es ist mit der AndroidEntryPoint-Annotation versehen, um sicherzustellen, dass die erforderlichen Abhängigkeiten im Fragment verwendet werden.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    // TAG für Debugging-Zwecke
    private val TAG: String = "LoginFragment"

    // Binding-Objekt für das Layout "fragment_login.xml"
    private lateinit var binding: FragmentLoginBinding

    // ViewModel-Objekt für Authentifizierungsaktionen
    private val viewModel: AuthViewModel by viewModels()

    /**
     * Die onCreateView-Methode wird aufgerufen, um das Fragment-Layout zu erstellen und zurückzugeben.
     * Hier wird das Binding-Objekt für "fragment_login.xml" initialisiert und zurückgegeben.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Die onViewCreated-Methode wird aufgerufen, sobald die View für das Fragment erstellt wurde.
     * Hier werden UI-Elemente initialisiert, wie z.B. Klicklistener für den Login-Button, das Passwort-Sichtbarkeits-Icon und die Navigations-Labels.
     * Außerdem wird der Observer für die LiveData-Objekte aufgerufen, um auf ViewModel-Änderungen zu reagieren.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListeners()
        observeLiveData()
    }

    /**
     * Initialisiert die Klicklistener für den Login-Button, das Passwort-Sichtbarkeits-Icon und die Navigations-Labels.
     */
    private fun setEventListeners() {
        binding.loginButton.setOnClickListener {
            if (validateInput()) {
                viewModel.login(
                    email = binding.textEmail.text.toString(),
                    password = binding.textPassword.text.toString()
                )
            }
        }

        binding.textForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.textRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.textPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = binding.textPassword.compoundDrawablesRelative[2]
                if (event.rawX >= binding.textPassword.right - drawable.bounds.width()) {
                    togglePasswordVisibility()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    /**
     * Schaltet die Sichtbarkeit des Passwortfeldes um.
     */
    private fun togglePasswordVisibility() {
        val passwordEditText = binding.textPassword

        if (passwordEditText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.textPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.password_icon, 0, R.drawable.show_password_icon, 0
            )
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.textPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.password_icon, 0, R.drawable.show_password_icon, 0
            )
        }
    }

    /**
     * Setzt den Observer für die LiveData-Objekte im ViewModel.
     */
    private fun observeLiveData() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loginButton.setText("")
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.loginButton.setText("Login")
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.loginButton.setText("Login")
                    binding.progressBar.hide()
                    toast(state.data)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Failure -> {
                    toast(state.error)
                }
                is UiState.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                else -> Unit
            }
        }
    }

    /**
     * Validiert die Benutzereingaben.
     * Gibt True zurück, wenn die Eingaben gültig sind, sonst False.
     */
    private fun validateInput(): Boolean {
        val email = binding.textEmail.text.toString().trim()
        val password = binding.textPassword.text.toString()

        if (email.isEmpty()) {
            binding.textEmail.error = getString(R.string.enter_email)
            return false
        } else if (!email.isValidEmail()) {
            binding.textEmail.error = getString(R.string.invalid_email)
            return false
        }

        if (password.isEmpty()) {
            binding.textPassword.error = getString(R.string.enter_password)
            return false
        } else if (password.length < 8) {
            binding.textPassword.error = getString(R.string.invalid_password)
            return false
        }

        return true
    }

    /**
     * Die onStart-Methode wird aufgerufen, wenn das Fragment sichtbar wird.
     * Hier wird die getSession-Methode des ViewModels aufgerufen, um zu überprüfen, ob der Benutzer bereits angemeldet ist.
     * Wenn ja, wird zur Zielfragment "homeFragment" navigiert.
     */
    override fun onStart() {
        super.onStart()
        viewModel.getSession()
    }
}
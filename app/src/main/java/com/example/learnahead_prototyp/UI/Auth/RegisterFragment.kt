package com.example.learnahead_prototyp.UI.Auth

import android.annotation.SuppressLint
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
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.isValidEmail
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * Das RegisterFragment ist für die Registrierung eines neuen Benutzers zuständig.
 * Es initialisiert das Binding-Objekt für das Layout "fragment_register.xml" und nutzt das ViewModel "AuthViewModel", um die Geschäftslogik der Authentifizierung zu verwenden.
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    // Debugging-Tag für das Fragment
    private val TAG: String = "RegisterFragment"

    // Binding-Objekt für das Layout "fragment_register.xml"
    private lateinit var binding: FragmentRegisterBinding

    // ViewModel-Objekt, um die Geschäftslogik von AuthViewModel zu verwenden
    private val viewModel: AuthViewModel by viewModels()

    /**
     * Die onCreateView-Methode wird aufgerufen, um das Fragment-Layout zu erstellen und zurückzugeben.
     * Hier wird das Binding-Objekt für "fragment_register.xml" initialisiert und zurückgegeben.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Bezugnahme auf die Ansichten
        val passwordEditText = binding.textPassword

        // Klicklistener auf dem Passwort-Icon setzen
        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.password_icon, 0, R.drawable.show_password_icon, 0
        )
        passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Das drawable-Objekt abrufen, das das Passwort-Icon auf der rechten Seite des EditText repräsentiert
                val drawable = passwordEditText.compoundDrawablesRelative[2]

                // Überprüfen, ob das Touch-Event innerhalb der Grenzen des Passwort-Icons aufgetreten ist
                if (event.rawX >= passwordEditText.right - drawable.bounds.width()) {
                    // Passwort-Sichtbarkeit umschalten
                    if (passwordEditText.transformationMethod == PasswordTransformationMethod.getInstance()) {
                        // Passwort anzeigen
                        passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    } else {
                        // Passwort verstecken
                        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                    }
                    true // Das Touch-Event verbrauchen
                } else {
                    false // Das Touch-Event nicht verbrauchen
                }
            } else {
                false // Andere Touch-Events nicht verbrauchen
            }
        }
        return binding.root
    }

    /**
     * Die onViewCreated-Methode wird aufgerufen, sobald die View für das Fragment erstellt wurde.
     * Hier werden die Click-Listener für den Registrieren-Button und die Navigationselemente festgelegt und der Observer für das ViewModel aufgerufen, um auf ViewModel-Änderungen zu reagieren.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListeners()
        observeViewModel()
    }

    /**
     * Legt die Click-Listener für den Registrieren-Button und die Navigationselemente fest.
     */
    private fun setEventListeners() {
        // Click-Listener für den Registrieren-Button festlegen
        binding.signUpButton.setOnClickListener {
            // Überprüfen, ob die Benutzereingaben gültig sind
            if (validateInput()) {
                // Das ViewModel aufrufen, um einen neuen Benutzer zu registrieren
                viewModel.register(
                    email = binding.editTextEmail.text.toString(),
                    password = binding.textPassword.text.toString(),
                    user = getUserObj()
                )
            }
        }

        // Click-Listener für die Navigationselemente festlegen
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    /**
     * Beobachtet das ViewModel auf Änderungen des Registrierungs-UI-Status und aktualisiert die UI entsprechend.
     * Zeigt eine Fortschrittsanzeige während des Ladevorgangs an, zeigt eine Toast-Nachricht bei Fehlern an
     * und navigiert zum Hauptbildschirm-Fragment, wenn die Registrierung erfolgreich abgeschlossen wurde.
     */
    private fun observeViewModel() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.signUpButton.setText("")
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.signUpButton.setText(getString(R.string.register_verb))
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.signUpButton.setText(getString(R.string.register_verb))
                    binding.progressBar.hide()
                    toast(state.data)
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }
            }
        }
    }

    /**
     * Erstellt ein neues User-Objekt mit den Benutzereingaben aus den Fragment-Views und gibt es zurück.
     * @return Das neu erstellte User-Objekt
     */
    private fun getUserObj(): User {
        return User(
            id = "",
            username = binding.textUsername.text.toString(),
            email = binding.editTextEmail.text.toString(),
        )
    }

    /**
     * Überprüft die Benutzereingaben aus den Fragment-Views und gibt zurück, ob alle Eingaben gültig sind.
     * Zeigt Toast-Nachrichten an, wenn eine Eingabe ungültig ist.
     * @return True, wenn alle Eingaben gültig sind, sonst False
     */
    private fun validateInput(): Boolean {
        var isValid = true

        if (binding.textUsername.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enterUsername))
        }

        if (binding.editTextEmail.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.editTextEmail.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.textPassword.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {


            if (binding.textPassword.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }
}
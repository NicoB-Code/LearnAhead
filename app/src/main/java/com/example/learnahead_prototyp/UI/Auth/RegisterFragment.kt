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
 * Dies ist das Fragment für die Registrierung eines neuen Benutzers.
 * Es initialisiert das Binding-Objekt für das Layout-File "fragment_register.xml" und nutzt das ViewModel "AuthViewModel", um die Geschäftslogik der Authentifizierung zu nutzen.
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    // Debugging-Tag für das Fragment
    val TAG: String = "RegisterFragment"

    // Binding-Objekt für die Layout-Datei "fragment_register.xml"
    lateinit var binding: FragmentRegisterBinding

    // Viewmodel-Objekt, um die Geschäftslogik von AuthViewModel zu nutzen
    val viewModel: AuthViewModel by viewModels()

    /**
     * Die onCreateView-Methode wird aufgerufen, um das Fragment-Layout aufzubauen und zurückzugeben.
     * Hier wird das Binding-Objekt für "fragment_register.xml" initialisiert und zurückgegeben.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Bezugnahme auf die Ansichten
        val passwordEditText = binding.editTextPassword

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

        // Set click listener on the register button
        binding.buttonSignUp.setOnClickListener {
            // Perform registration logic here
        }
        return binding.root
    }

    /**
     * Die onViewCreated-Methode wird aufgerufen, sobald die View erstellt wurde.
     * Hier wird der Click-Listener für die Registrieren-Button gesetzt und die "observer"-Funktion aufgerufen, um auf ViewModel-Änderungen zu reagieren.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ruft die "observer"-Funktion auf, um auf ViewModel-Änderungen zu reagieren
        observer()

        // Setzt den Click-Listener für die Registrieren-Button
        binding.buttonSignUp.setOnClickListener {
            // Überprüft, ob die Eingaben des Benutzers gültig sind
            if (validation()) {
                // Ruft die "register"-Funktion des ViewModels auf, um einen neuen Benutzer zu registrieren
                viewModel.register(
                    email = binding.editTextEmail.text.toString(),
                    password = binding.editTextPassword.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }

    /**
     * Beobachtet den AuthViewModel auf Änderungen des Registrierungs-UI-Status und aktualisiert die UI entsprechend.
     * Zeigt eine Fortschrittsanzeige während des Ladevorgangs an, zeigt eine Toast-Nachricht bei Fehlern an
     * und navigiert zur Hauptbildschirm-Fragment, wenn die Registrierung erfolgreich abgeschlossen wurde.
     */
    fun observer() {
        // Beobachtet den ViewModel-Status auf Änderungen
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                // Zeigt Fortschrittsanzeige während des Ladevorgangs an
                is UiState.Loading -> {
                    binding.buttonSignUp.setText("")
                    binding.registerProgress.show()
                }
                // Zeigt Toast-Nachricht bei Fehlern an
                is UiState.Failure -> {
                    binding.buttonSignUp.setText(getString(R.string.register_verb))
                    binding.registerProgress.hide()
                    toast(state.error)
                }
                // Navigiert zur Hauptbildschirm-Fragment, wenn Registrierung erfolgreich abgeschlossen wurde
                is UiState.Success -> {
                    binding.buttonSignUp.setText(getString(R.string.register_verb))
                    binding.registerProgress.hide()
                    toast(state.data)
                    findNavController().navigate(R.id.action_registerFragment_to_goalListingFragment)
                }

                else -> {}
            }
        }
    }

    /**
     * Erstellt ein neues User-Objekt mit den Eingabedaten des Benutzers aus den Fragment-Views und gibt es zurück.
     * @return Das neu erstellte User-Objekt
     */
    fun getUserObj(): User {
        return User(
            id = "",
            username = binding.editTextUsername.text.toString(),
            email = binding.editTextEmail.text.toString(),
        )
    }


    /**
     * Validiert die Eingabedaten des Benutzers aus den Fragment-Views und gibt zurück, ob alle Eingaben gültig sind.
     * Zeigt Toast-Nachrichten an, wenn eine Eingabe ungültig ist.
     * @return True, wenn alle Eingaben gültig sind, andernfalls False
     */
    fun validation(): Boolean {
        var isValid = true

        if (binding.editTextUsername.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_first_name))
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
        if (binding.editTextPassword.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            if (binding.editTextPassword.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }
}
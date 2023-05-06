package com.example.learnahead_prototyp.UI.Auth

import android.os.Bundle
import android.view.LayoutInflater
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
 * Das ist die LoginFragment-Klasse, die für die Anmeldung von Benutzern zuständig ist.
 * Sie ist mit dem AndroidEntryPoint-Annotation versehen, um sicherzustellen, dass die erforderlichen
 * Abhängigkeiten im Fragment eingesetzt werden.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    // TAG für Debugging-Zwecke
    val TAG: String = "RegisterFragment"

    // Binding-Objekt für die Layout-Datei "fragment_login.xml"
    lateinit var binding: FragmentLoginBinding

    // Viewmodel-Objekt, um die Geschäftslogik von AuthViewModel zu nutzen
    val viewModel: AuthViewModel by viewModels()

    /**
     * Die onCreateView-Methode wird aufgerufen, um das Fragment-Layout aufzubauen und zurückzugeben.
     * Hier wird das Binding-Objekt für "fragment_login.xml" initialisiert und zurückgegeben.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Die onViewCreated-Methode wird aufgerufen, sobald die View erstellt wurde.
     * Hier werden die UI-Elemente initialisiert, wie zum Beispiel die Click-Listener für die Login-, Passwort-Vergessen- und Registrieren-Buttons.
     * Außerdem wird die "observer"-Funktion aufgerufen, um auf ViewModel-Änderungen zu reagieren.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ruft die "observer"-Funktion auf, um auf ViewModel-Änderungen zu reagieren
        observer()

        // Setzt den Click-Listener für die Login-Button
        binding.loginBtn.setOnClickListener {
            // Überprüft, ob die Eingaben des Benutzers gültig sind
            if (validation()) {
                // Ruft die "login"-Funktion des ViewModels auf, um den Benutzer anzumelden
                viewModel.login(
                    email = binding.emailEt.text.toString(),
                    password = binding.passEt.text.toString()
                )
            }
        }

        // Setzt den Click-Listener für die Passwort vergessen-Label
        binding.forgotPassLabel.setOnClickListener {
            // Navigiert zum ForgotPasswordFragment
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        // Setzt den Click-Listener für die Registrieren-Label
        binding.registerLabel.setOnClickListener {
            // Navigiert zum RegisterFragment
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    /**
     * Die Methode "observer" wird genutzt, um auf Änderungen im "login" LiveData-Objekt des ViewModels zu reagieren.
     * Je nach Zustand des LiveData-Objekts wird die UI entsprechend aktualisiert.
     */
    fun observer(){
        viewModel.login.observe(viewLifecycleOwner) { state ->
            // Überprüft den Status des UI-States und aktualisiert die UI entsprechend
            when(state){
                is UiState.Loading -> {
                    // Zeigt den Fortschrittsbalken an und deaktiviert den Login-Button
                    binding.loginBtn.setText("")
                    binding.loginProgress.show()
                }
                is UiState.Failure -> {
                    // Aktiviert den Login-Button, versteckt den Fortschrittsbalken und zeigt eine Toast-Nachricht mit der Fehlermeldung an
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    // Aktiviert den Login-Button, versteckt den Fortschrittsbalken und zeigt eine Toast-Nachricht mit der Erfolgsmeldung an
                    // Navigiert zum "GoalListingFragment"
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.data)
                    findNavController().navigate(R.id.action_loginFragment_to_goalListingFragment)
                }
            }
        }
    }

    /**
     * Die Methode "validation" wird genutzt, um zu überprüfen, ob die Benutzereingaben gültig sind.
     * Wenn die Eingaben ungültig sind, wird eine Toast-Nachricht angezeigt und die Methode gibt "false" zurück.
     * Wenn die Eingaben gültig sind, gibt die Methode "true" zurück.
     */
    fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()){
            // Wenn das E-Mail-Feld leer ist, ist die Eingabe ungültig und eine Toast-Nachricht wird angezeigt
            isValid = false
            toast(getString(R.string.enter_email))
        }else{
            if (!binding.emailEt.text.toString().isValidEmail()){
                // Wenn die E-Mail-Adresse ungültig ist, ist die Eingabe ungültig und eine Toast-Nachricht wird angezeigt
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.passEt.text.isNullOrEmpty()){
            // Wenn das Passwort-Feld leer ist, ist die Eingabe ungültig und eine Toast-Nachricht wird angezeigt
            isValid = false
            toast(getString(R.string.enter_password))
        }else{
            if (binding.passEt.text.toString().length < 8){
                // Wenn das Passwort weniger als 8 Zeichen enthält, ist die Eingabe ungültig und eine Toast-Nachricht wird angezeigt
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

    /**
     * Die "onStart" Methode wird aufgerufen, sobald das Fragment sichtbar wird.
     * Hier wird die Methode "getSession" des ViewModels aufgerufen, um zu überprüfen, ob der Benutzer bereits angemeldet ist.
     * Wenn ja, wird direkt zum Ziel-Fragment "goalListingFragment" navigiert.
     */
    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if(user != null) {
                // Wenn ein Benutzer angemeldet ist, navigiert es zum "GoalListingFragment"
                findNavController().navigate(R.id.action_loginFragment_to_goalListingFragment)
            }
        }
    }
}
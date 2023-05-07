package com.example.learnahead_prototyp.UI.Auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.isValidEmail
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * Ein Fragment zur Anzeige des Formulars zum Zurücksetzen des Passworts
 */
@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    // Ein Tag zur Identifizierung des Fragments in den Logs
    val TAG: String = "ForgotPasswordFragment"

    // Das Binding-Objekt zum Zugriff auf die Ansichten des Fragments
    lateinit var binding: FragmentForgotPasswordBinding

    // Das ViewModel-Objekt zum Durchführen von Authentifizierungsaktionen
    val viewModel: AuthViewModel by viewModels()

    /**
     * Erstellt die Benutzeroberfläche für das Fragment.
     * @param inflater Layout-Inflater, der verwendet wird, um das Layout aufzublasen
     * @param container Die Elternansicht, in die das Fragment eingefügt wird
     * @param savedInstanceState Wenn die Aktivität vom System zerstört und neu erstellt wird, enthält dieses Bundle die zuletzt gespeicherten Zustände der Ansichten.
     * @return Die aufgeblasene View für das Fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Das Binding-Objekt wird initialisiert, um auf die Ansichten des Fragments zugreifen zu können
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Wird aufgerufen, sobald die View für das Fragment erstellt wurde.
     * Fügt den Klicklistener für den "Passwort vergessen"-Button hinzu und ruft die Funktion zum Beobachten von LiveData-Objekten auf.
     * @param view Die erstellte View für das Fragment.
     * @param savedInstanceState Wenn die Aktivität vom System zerstört und neu erstellt wird, enthält dieses Bundle die zuletzt gespeicherten Zustände der Ansichten.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisierung der Beobachter für die verschiedenen Authentifizierungsaktionen
        observer()

        // Fügt einen Klicklistener zum Zurücksetzen-Button hinzu
        binding.forgotPassBtn.setOnClickListener {
            // Überprüft die Eingabevalidierung des Benutzers
            if (validation()) {
                // Ruft die "forgotPassword" -Funktion des ViewModels auf, um das Passwort zurückzusetzen
                viewModel.forgotPassword(binding.emailEt.text.toString())
            }
        }
    }

    /**
     * Diese Funktion ist ein Beobachter für den LiveData "forgotPassword" im ViewModel "viewModel".
     * Sobald sich der Zustand des LiveData ändert, wird diese Funktion aufgerufen und reagiert entsprechend.
     * @param viewLifecycleOwner LifecycleOwner des Views, der den Observer besitzt.
     */
    private fun observer() {
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Wenn der Zustand "Loading" ist, ändert die Funktion die Textanzeige und zeigt den Fortschrittsbalken an
                    binding.forgotPassBtn.setText("")
                    binding.forgotPassProgress.show()
                }

                is UiState.Failure -> {
                    // Wenn der Zustand "Failure" ist, ändert die Funktion die Textanzeige, versteckt den Fortschrittsbalken und zeigt eine Fehlermeldung an.
                    binding.forgotPassBtn.setText("Send")
                    binding.forgotPassProgress.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    // Wenn der Zustand "Success" ist, ändert die Funktion die Textanzeige, versteckt den Fortschrittsbalken und zeigt eine Erfolgsmeldung an.
                    binding.forgotPassBtn.setText("Send")
                    binding.forgotPassProgress.hide()
                    toast(state.data)
                }
            }
        }
    }

    /**
     * Diese Funktion validiert die E-Mail-Adresse, die vom Benutzer in das E-Mail-Feld eingegeben wurde.
     * Wenn die E-Mail-Adresse ungültig ist, zeigt die Funktion eine Fehlermeldung an und gibt "false" zurück.
     * Andernfalls gibt sie "true" zurück.
     * @return Boolean - true, wenn die E-Mail-Adresse gültig ist, false, wenn sie ungültig ist.
     */
    fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()) {
            // Wenn das E-Mail-Feld leer ist oder null, ist die E-Mail-Adresse ungültig. Die Funktion zeigt eine Fehlermeldung an und gibt "false" zurück.
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.emailEt.text.toString().isValidEmail()) {
                // Wenn die E-Mail-Adresse ungültig ist, zeigt die Funktion eine Fehlermeldung an und gibt "false" zurück.
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }

        return isValid
    }
}
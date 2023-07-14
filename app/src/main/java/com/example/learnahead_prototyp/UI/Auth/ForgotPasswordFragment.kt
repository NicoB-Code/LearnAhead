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
 * Fragment zur Anzeige des Passwort-Wiederherstellungsformulars.
 */
@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    // Tag zur Identifizierung des Fragments in den Logs
    private val TAG: String = "ForgotPasswordFragment"

    // Binding-Objekt zum Zugriff auf die Fragment-Views
    private lateinit var binding: FragmentForgotPasswordBinding

    // ViewModel-Objekt für Authentifizierungsaktionen
    private val viewModel: AuthViewModel by viewModels()

    /**
     * Erstellt die Benutzeroberfläche für das Fragment.
     * @param inflater Layout-Inflater zum Aufblasen des Layouts
     * @param container Elternansicht, in die das Fragment eingefügt wird
     * @param savedInstanceState Bundle mit den zuletzt gespeicherten Ansichtszuständen, wenn die Aktivität zerstört und wiederhergestellt wurde
     * @return Die aufgeblasene View für das Fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Wird aufgerufen, sobald die View für das Fragment erstellt wurde.
     * Fügt den Klicklistener für den "Passwort vergessen"-Button hinzu und setzt den Observer für LiveData-Objekte.
     * @param view Die erstellte View für das Fragment.
     * @param savedInstanceState Bundle mit den zuletzt gespeicherten Ansichtszuständen, wenn die Aktivität zerstört und wiederhergestellt wurde.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListener()
        observeLiveData()
    }

    /**
     * Fügt den Klicklistener für den Zurücksetzen-Button hinzu.
     */
    private fun setEventListener() {
        binding.buttonForgotPassword.setOnClickListener {
            if (validation()) {
                viewModel.forgotPassword(binding.editTextEmail.text.toString())
            }
        }
    }

    /**
     * Setzt den Observer für den LiveData "forgotPassword" im ViewModel.
     */
    private fun observeLiveData() {
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.buttonForgotPassword.setText("")
                    binding.forgetPasswordProgress.show()
                }
                is UiState.Failure -> {
                    binding.buttonForgotPassword.setText("Send")
                    binding.forgetPasswordProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.buttonForgotPassword.setText("Send")
                    binding.forgetPasswordProgress.hide()
                    toast(state.data)
                }
            }
        }
    }

    /**
     * Validiert die eingegebene E-Mail-Adresse.
     * Zeigt eine Fehlermeldung an, wenn die E-Mail-Adresse ungültig ist.
     * @return True, wenn die E-Mail-Adresse gültig ist, ansonsten False.
     */
    private fun validation(): Boolean {
        val email = binding.editTextEmail.text.toString().trim()

        return if (email.isEmpty()) {
            toast(getString(R.string.enter_email))
            false
        } else if (!email.isValidEmail()) {
            toast(getString(R.string.invalid_email))
            false
        } else {
            true
        }
    }
}

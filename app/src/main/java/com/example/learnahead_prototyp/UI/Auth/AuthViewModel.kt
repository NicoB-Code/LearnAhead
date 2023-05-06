package com.example.learnahead_prototyp.UI.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.IAuthRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Diese Klasse ist ein ViewModel, das für den Authentifizierungsprozess zuständig ist und
 * Daten zwischen dem Repository und der UI-Schicht vermittelt. Diese Klasse wird mithilfe
 * von Hilt injeziert und erhält eine Instanz des IAuthRepository-Interfaces, um auf
 * Authentifizierungsfunktionen zugreifen zu können.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: IAuthRepository
): ViewModel() {

    /**
     * Diese Eigenschaft ist eine LiveData-Instanz, die den aktuellen Status des Registrierungsprozesses
     * in der UI-Schicht widerspiegelt.
     */
    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    /**
     * Diese Eigenschaft ist eine LiveData-Instanz, die den aktuellen Status des Anmeldevorgangs
     * in der UI-Schicht widerspiegelt.
     */
    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    /**
     * Diese Eigenschaft ist eine LiveData-Instanz, die den aktuellen Status des Passwort-Wiederherstellungsvorgangs
     * in der UI-Schicht widerspiegelt.
     */
    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    /**
     * Diese Methode ruft die registerUser-Funktion im IAuthRepository auf, um einen neuen Benutzer
     * zu registrieren. Sie aktualisiert den Status der _register-LiveData-Eigenschaft, um den Fortschritt
     * in der UI-Schicht widerzuspiegeln.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param user Ein Objekt, das den Benutzer darstellt.
     */
    fun register(
        email: String,
        password: String,
        user: User
    ) {
        _register.value = UiState.Loading
        repository.registerUser(
            email = email,
            password = password,
            user = user
        ) { _register.value = it }
    }

    /**
     * Diese Methode ruft die loginUser-Funktion im IAuthRepository auf, um einen Benutzer anzumelden.
     * Sie aktualisiert den Status der _login-LiveData-Eigenschaft, um den Fortschritt
     * in der UI-Schicht widerzuspiegeln.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     */
    fun login(
        email: String,
        password: String
    ) {
        _login.value = UiState.Loading
        repository.loginUser(
            email,
            password
        ){
            _login.value = it
        }
    }

    /**
     * Diese Funktion löst einen Passwort-Wiederherstellungsprozess aus, indem sie eine Email an die angegebene Email-Adresse sendet.
     * @param email Die Email-Adresse des Benutzers, dessen Passwort wiederhergestellt werden soll.
     */
    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(email){
            _forgotPassword.value = it
        }
    }

    /**
     * Diese Funktion meldet den aktuellen Benutzer ab.
     * @param result Callback-Funktion, die aufgerufen wird, wenn der Logout-Prozess abgeschlossen ist.
     */
    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    /**
     * Diese Funktion ruft die aktuelle Benutzersitzung ab.
     * @param result Callback-Funktion, die aufgerufen wird, wenn die Benutzersitzung abgerufen wurde.
     * Die zurückgegebene [User] Instanz enthält Informationen über den aktuellen Benutzer oder ist null, wenn kein Benutzer angemeldet ist.
     */
    fun getSession(result: (User?) -> Unit) {
        repository.getSession(result)
    }

}
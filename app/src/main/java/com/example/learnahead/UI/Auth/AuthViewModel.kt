package com.example.learnahead.UI.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead.Data.Model.User
import com.example.learnahead.Data.Repository.IAuthRepository
import com.example.learnahead.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel-Klasse für den Authentifizierungsprozess.
 * Verwaltet die Kommunikation zwischen Repository und UI-Schicht.
 * Wird mithilfe von Hilt injiziert und erhält eine Instanz des IAuthRepository-Interfaces, um auf Authentifizierungsfunktionen zuzugreifen.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: IAuthRepository
) : ViewModel() {

    /**
     * LiveData-Instanz, die den aktuellen Status des Registrierungsprozesses in der UI-Schicht widerspiegelt.
     */
    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _currentUser = MutableLiveData<UiState<User>>()
    val currentUser: LiveData<UiState<User>>
        get() = _currentUser

    /**
     * LiveData-Instanz, die den aktuellen Status des Anmeldevorgangs in der UI-Schicht widerspiegelt.
     */
    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    /**
     * LiveData-Instanz, die den aktuellen Status des Passwort-Wiederherstellungsvorgangs in der UI-Schicht widerspiegelt.
     */
    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword

    private val _updateUserInfo = MutableLiveData<UiState<String>>()
    val updateUserInfo: LiveData<UiState<String>>
        get() = _updateUserInfo

    /**
     * Registriert einen neuen Benutzer.
     * Aktualisiert den Status der _register-LiveData-Eigenschaft, um den Fortschritt in der UI-Schicht widerzuspiegeln.
     * @param email E-Mail-Adresse des Benutzers.
     * @param password Passwort des Benutzers.
     * @param user Benutzerobjekt.
     */
    fun register(email: String, password: String, user: User) {
        _register.value = UiState.Loading
        repository.registerUser(email = email, password = password, user = user) { _register.value = it }
    }

    /**
     * Meldet einen Benutzer an.
     * Aktualisiert den Status der _login-LiveData-Eigenschaft, um den Fortschritt in der UI-Schicht widerzuspiegeln.
     * @param email E-Mail-Adresse des Benutzers.
     * @param password Passwort des Benutzers.
     */
    fun login(email: String, password: String) {
        _login.value = UiState.Loading
        repository.loginUser(email, password) { _login.value = it }
    }

    /**
     * Löst einen Passwort-Wiederherstellungsprozess aus, indem eine E-Mail an die angegebene E-Mail-Adresse gesendet wird.
     * Aktualisiert den Status der _forgotPassword-LiveData-Eigenschaft, um den Fortschritt in der UI-Schicht widerzuspiegeln.
     * @param email E-Mail-Adresse des Benutzers, dessen Passwort wiederhergestellt werden soll.
     */
    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(email) { _forgotPassword.value = it }
    }

    /**
     * Meldet den aktuellen Benutzer ab.
     * @param result Callback-Funktion, die aufgerufen wird, wenn der Logout-Prozess abgeschlossen ist.
     */
    fun logout(result: () -> Unit) {
        repository.logout(result)
    }

    /**
     * Ruft die aktuelle Benutzersitzung ab.
     * @param result Callback-Funktion, die aufgerufen wird, wenn die Benutzersitzung abgerufen wurde.
     * Das zurückgegebene [User]-Objekt enthält Informationen über den aktuellen Benutzer oder ist null, wenn kein Benutzer angemeldet ist.
     */
    fun getSession() {
        repository.getSession() { _currentUser.value = it }
    }

    /**
     * Speichert die Benutzersitzung.
     * @param user Benutzerobjekt.
     */
    fun storeSession(user: User) {
        repository.storeSession(user.id) { _currentUser.value = it }
    }

    /**
     * Aktualisiert die Benutzerinformationen.
     * Aktualisiert den Status der _updateUserInfo-LiveData-Eigenschaft, um den Fortschritt in der UI-Schicht widerzuspiegeln.
     * @param user Benutzerobjekt.
     */
    fun updateUserInfo(user: User) {
        _updateUserInfo.value = UiState.Loading
        repository.updateUserInfo(user) { _updateUserInfo.value = it }
        storeSession(user)
    }

}
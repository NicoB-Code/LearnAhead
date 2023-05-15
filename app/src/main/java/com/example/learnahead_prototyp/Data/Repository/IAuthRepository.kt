package com.example.learnahead_prototyp.Data.Repository

import android.net.Uri
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

/**
 * Das Interface IAuthRepository definiert Methoden, die den Zugriff auf die Datenbank für die Authentifizierung von Benutzern ermöglichen.
 */
interface IAuthRepository {
    /**
     * Diese Methode registriert einen Benutzer mit der angegebenen E-Mail-Adresse, dem Passwort und Benutzerdaten.
     * Das Ergebnis wird in einem Rückruf zurückgegeben, der einen [UiState] -Parameter erwartet.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param user Die zu registrierenden Benutzerdaten.
     * @param result Die Rückruffunktion zum Bearbeiten des Ergebnisses.
     */
    fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit)

    /**
     * Diese Methode aktualisiert die Informationen des angegebenen Benutzers.
     * Das Ergebnis wird in einem Rückruf zurückgegeben, der einen [UiState] -Parameter erwartet.
     * @param user Die zu aktualisierenden Benutzerdaten.
     * @param result Die Rückruffunktion zum Bearbeiten des Ergebnisses.
     */
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)

    /**
     * Diese Methode loggt einen Benutzer mit der angegebenen E-Mail-Adresse und dem Passwort ein.
     * Das Ergebnis wird in einem Rückruf zurückgegeben, der einen [UiState] -Parameter erwartet.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param result Die Rückruffunktion zum Bearbeiten des Ergebnisses.
     */
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)

    /**
     * Diese Methode sendet eine E-Mail zur Zurücksetzung des Passworts an die angegebene E-Mail-Adresse.
     * Das Ergebnis wird in einem Rückruf zurückgegeben, der einen [UiState] -Parameter erwartet.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param result Die Rückruffunktion zum Bearbeiten des Ergebnisses.
     */
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)

    /**
     * Diese Methode loggt den aktuell angemeldeten Benutzer aus.
     * Das Ergebnis wird in einem Rückruf zurückgegeben, der keinen Parameter erwartet.
     * @param result Die Rückruffunktion zum Bearbeiten des Ergebnisses.
     */
    fun logout(result: () -> Unit)

    /**
     * Diese Methode speichert die angegebenen Sitzungsinformationen.
     * Das Ergebnis wird in einem Rückruf zurückgegeben, der einen [User]-Parameter erwartet.
     * @param it Die zu speichernden Sitzungsinformationen.
     * @param result Die Rückruffunktion zum Bearbeiten des Ergebnisses.
     */
    fun storeSession(it: String, result: (User?) -> Unit)

    /**
     * This method retrieves the stored session information.
     * The result is returned in a callback that takes a [User] parameter.
     * @param result The callback function to handle the result.
     */
    fun getSession(result: (User?) -> Unit)

    suspend fun singleImageUpload(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
}
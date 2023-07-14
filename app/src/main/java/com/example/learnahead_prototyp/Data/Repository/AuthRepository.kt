package com.example.learnahead_prototyp.Data.Repository

import android.content.SharedPreferences
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.SharedPrefConstants
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.Date

/**
 * AuthRepository ist eine Implementierung des IAuthRepository-Interfaces.
 * Sie handhabt Authentifizierungs- und Benutzerdatenoperationen für die Anwendung.
 *
 * @property auth FirebaseAuth-Objekt für die Authentifizierung mit Firebase
 * @property database FirebaseFirestore-Objekt für die Datenbankkommunikation
 * @property appPreferences SharedPreferences-Objekt für den Zugriff auf lokale Einstellungen
 * @property gson Gson-Objekt für die JSON-Serialisierung und Deserialisierung
 */
class AuthRepository(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
) : IAuthRepository {

    /**
     * Registriert einen Benutzer mit E-Mail-Adresse und Passwort und aktualisiert die Benutzerdaten.
     *
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param user Die Benutzerdaten, die aktualisiert werden sollen.
     * @param result Die Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     */
    override fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit) {
        // Benutzer mit E-Mail-Adresse und Passwort registrieren
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // Benutzer-ID setzen
                user.id = authResult.user?.uid ?: ""
                // Benutzerdaten aktualisieren
                updateUserInfo(user) { state ->
                    when (state) {
                        is UiState.Success -> {
                            // Sitzung speichern
                            storeSession(id = authResult.user?.uid ?: "") {
                                result.invoke(UiState.Success("Benutzer wurde erfolgreich registriert!"))
                            }
                        }
                        is UiState.Failure -> result.invoke(UiState.Failure(state.error))
                        else -> {}
                    }
                }
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthWeakPasswordException ->
                        result.invoke(UiState.Failure("Registrierung fehlgeschlagen. Das Passwort muss mindestens 6 Zeichen lang sein."))
                    is FirebaseAuthInvalidCredentialsException ->
                        result.invoke(UiState.Failure("Registrierung fehlgeschlagen. Ungültige E-Mail-Adresse eingegeben."))
                    is FirebaseAuthUserCollisionException ->
                        result.invoke(UiState.Failure("Registrierung fehlgeschlagen. E-Mail-Adresse bereits registriert."))
                    else ->
                        result.invoke(UiState.Failure(exception.message))
                }
            }
    }

    /**
     * Aktualisiert die Benutzerdaten in der Firestore-Datenbank.
     *
     * @param user Die Benutzerdaten, die aktualisiert werden sollen.
     * @param result Die Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     */
    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        // Dokumentreferenz für den Benutzer in der Firestore-Datenbank abrufen
        val document = database.collection(FireStoreCollection.USER).document(user.id)

        // Überprüfen, ob ein neuer Tag begonnen hat
        val currentDate = Date()
        if (user.lastLogin.date != currentDate.date) {
            // ist seit dem login 1 Tag vergangen?
            if((user.lastLogin.date + 1) == currentDate.date){
                user.learningStreak++
                // lernstreak gecapped bei zehn
                if (user.learningStreak > 10) {
                    user.currentPoints += 100
                } else {
                    user.currentPoints += (10 * user.learningStreak)
                }
            }else {
                // Mehr als 1 Tag weg: Lernserie zurücksetzen und 10 Punkte vergeben
                user.learningStreak = 1
                user.currentPoints += 10
            }
        }
        user.lastLogin = currentDate

        // Benutzerdaten in der Datenbank aktualisieren
        document.set(user)
            .addOnSuccessListener {
                // Erfolgsstatus an den Aufrufer zurückgeben
                result.invoke(UiState.Success("Benutzer wurde erfolgreich aktualisiert."))
            }
            .addOnFailureListener {
                // Fehlerstatus an den Aufrufer zurückgeben
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    /**
     * Meldet einen Benutzer mit der angegebenen E-Mail-Adresse und dem Passwort an.
     *
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param result Die Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     */
    override fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit) {
        // Benutzer mit E-Mail-Adresse und Passwort anmelden
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Anmeldung erfolgreich?
                if (task.isSuccessful) {
                    // Sitzung speichern
                    storeSession(id = task.result.user?.uid ?: "") {
                        result.invoke(UiState.Success("Anmeldung erfolgreich!"))
                    }
                }
            }.addOnFailureListener {
                // Fehler: Anmeldung fehlgeschlagen
                result.invoke(UiState.Failure("Anmeldung fehlgeschlagen. Überprüfe E-Mail und Passwort."))
            }
    }

    /**
     * Beantragt das Zurücksetzen des Passworts für einen Benutzer per E-Mail.
     *
     * @param email Die E-Mail-Adresse des Benutzers, für den das Passwort zurückgesetzt werden soll.
     * @param result Die Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     */
    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        // Überprüfen, ob die E-Mail-Adresse eine gültige Syntax hat
        if (!isValidEmail(email)) {
            result.invoke(UiState.Failure("Ungültige E-Mail-Adresse."))
            return
        }
        // E-Mail zum Zurücksetzen des Passworts senden
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Erfolgreich?
                if (task.isSuccessful) {
                    result.invoke(UiState.Success("E-Mail zum Zurücksetzen des Passworts wurde gesendet."))
                } else {
                    result.invoke(UiState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Fehler bei der Authentifizierung. Überprüfe die E-Mail-Adresse."))
            }
    }

    /**
     * Überprüft, ob eine E-Mail-Adresse eine gültige Syntax hat.
     *
     * @param email Die zu überprüfende E-Mail-Adresse.
     * @return true, wenn die E-Mail-Adresse eine gültige Syntax hat, andernfalls false.
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Meldet den Benutzer von der Anwendung ab.
     *
     * @param result Die Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     */
    override fun logout(result: () -> Unit) {
        // Benutzer bei Firebase abmelden
        auth.signOut()
        // Sitzungsdaten aus den Anwendungseinstellungen entfernen
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        // Leeres Ergebnis an den Aufrufer zurückgeben
        result.invoke()
    }

    /**
     * Speichert die Sitzungsdetails eines Benutzers im lokalen Speicher.
     *
     * @param id Die ID des Benutzers, für den die Sitzung gespeichert werden soll.
     * @param result Die Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     */
    override fun storeSession(id: String, result: (UiState<User>) -> Unit) {
        // Firestore-Sammlung "USER" abrufen
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnSuccessListener { document ->
                // Benutzerobjekt aus dem Firestore-Dokument erhalten
                val user = document.toObject(User::class.java)
                if (user != null) {
                    // Sitzungsdetails des Benutzers im lokalen Speicher speichern
                    appPreferences.edit()
                        .putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result.invoke(UiState.Success(user))
                }
            }
    }

    /**
     * Ruft die gespeicherten Sitzungsdaten des aktuell angemeldeten Benutzers ab.
     *
     * @param result Die Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Wenn eine gültige Sitzung vorhanden ist, gibt die Funktion das Benutzerobjekt zurück, andernfalls null.
     */
    override fun getSession(result: (UiState<User>) -> Unit) {
        // Gespeicherte Sitzungsdaten des Benutzers aus dem lokalen Speicher abrufen
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        // Wenn keine Sitzungsdaten vorhanden sind, null zurückgeben
        if (userStr == null) {
            result.invoke(UiState.Failure("Keine Benutzersitzung gefunden."))
        } else {
            // JSON-Zeichenfolge in Benutzerobjekt konvertieren
            val user = gson.fromJson(userStr, User::class.java)
            result.invoke(UiState.Success(user))
        }
    }
}
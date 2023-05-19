package com.example.learnahead_prototyp.Data.Repository

import android.content.SharedPreferences
import android.util.Log
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

// Die Klasse AuthRepository implementiert das Interface IAuthRepository.
class AuthRepository(
    val auth: FirebaseAuth, // Objekt für Firebase Authentifizierung
    val database: FirebaseFirestore, // FirebaseFirestore-Objekt
    val appPreferences: SharedPreferences, // SharedPreferences-Objekt
    val gson: Gson // Gson-Objekt
) : IAuthRepository {

    val TAG: String = "AuthRepository"

    /**
     * Diese Funktion registriert einen Benutzer mit seiner E-Mail-Adresse und seinem Passwort
     * und aktualisiert anschließend seine Benutzerdaten. Das Ergebnis der Registrierung wird
     * über die übergebene Funktion an den Aufrufer zurückgegeben.
     * @param email die E-Mail-Adresse des Benutzers
     * @param password das Passwort des Benutzers
     * @param user die Benutzerdaten, die aktualisiert werden sollen
     * @param result eine Funktion zur Rückgabe des Ergebnisses an den Aufrufer
     * @return Diese Funktion gibt kein Ergebnis zurück, sondern ruft die übergebene Funktion "result"
     * auf, um das Ergebnis an den Aufrufer zurückzugeben.
     */
    override fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit) {
        // Neuen Benutzer mit Email und Passwort erstellen
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // ID des Benutzers setzen
                user.id = authResult.user?.uid ?: ""
                // Benutzerdaten aktualisieren
                updateUserInfo(user) { state ->
                    when (state) {
                        is UiState.Success -> {
                            // Session speichern
                            storeSession(id = authResult.user?.uid ?: "") {
                                result.invoke(UiState.Success("User register successfully!"))
                            }
                        }

                        is UiState.Failure -> result.invoke(UiState.Failure(state.error))
                        else -> {}
                    }
                }
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthWeakPasswordException -> result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
                    is FirebaseAuthInvalidCredentialsException -> result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                    is FirebaseAuthUserCollisionException -> result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                    else -> result.invoke(UiState.Failure(exception.message))
                }
            }
    }

    /**
     * Die Funktion updateUserInfo aktualisiert die Benutzerinformationen in der Firebase Firestore-Datenbank.
     * @param user Die Benutzerdaten, die aktualisiert werden sollen.
     * @param result Eine Lambda-Funktion, die aufgerufen wird, wenn die Aktualisierung in der Datenbank abgeschlossen ist.
     *               Der Parameter der Funktion gibt an, ob die Aktualisierung erfolgreich war oder nicht.
     * @return Es wird kein Wert zurückgegeben.
     */
    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        // Eine Referenz auf das Dokument des Benutzers in der Firebase Firestore-Datenbank abrufen
        val document = database.collection(FireStoreCollection.USER).document(user.id)

        // Das Benutzerobjekt in der Datenbank aktualisieren
        document
            .set(user)
            .addOnSuccessListener {
                // Bei erfolgreicher Aktualisierung den Erfolgsstatus im UiState-Objekt setzen und die result-Funktion aufrufen
                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                // Bei einem Fehler den Fehlerstatus im UiState-Objekt setzen und die result-Funktion aufrufen
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }


    /**
     * Meldet einen Benutzer mit der angegebenen E-Mail-Adresse und dem Passwort an.
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     *               Das Ergebnis enthält entweder eine Erfolgsmeldung oder eine Fehlermeldung.
     *               Die Erfolgsmeldung gibt an, dass der Benutzer erfolgreich angemeldet wurde.
     *               Die Fehlermeldung gibt an, dass die Anmeldung fehlgeschlagen ist.
     */
    override fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit) {
        // Benutzer mit Email und Passwort anmelden
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Anmeldung erfolgreich?
                if (task.isSuccessful) {
                    // Session speichern
                    storeSession(id = task.result.user?.uid ?: "") {
                        result.invoke(UiState.Success("Login successfully!"))
                    }
                }
            }.addOnFailureListener {
                // Fehler: Anmeldung fehlgeschlagen
                result.invoke(UiState.Failure("Authentication failed, Check email and password"))
            }
    }

    /**
     * Funktion, um das Zurücksetzen des Passworts für den angegebenen Benutzer per E-Mail zu beantragen.
     * @param email E-Mail-Adresse des Benutzers, für den das Passwort zurückgesetzt werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Nachricht, die den Status beschreibt.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Zurücksetzen des Passworts erfolgreich war oder nicht.
     *
     * Vor dem Zurücksetzen des Passworts wird die E-Mail-Adresse des Benutzers validiert, um sicherzustellen,
     * dass sie eine gültige Syntax hat. Wenn die E-Mail-Adresse ungültig ist, wird ein Fehler zurückgegeben.
     */
    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        // Überprüft, ob die E-Mail-Adresse eine gültige Syntax hat
        if (!isValidEmail(email)) {
            // Gibt ein fehlgeschlagenes Ergebnis mit einer Fehlermeldung für ungültige E-Mail-Adressen an den Aufrufer zurück
            result.invoke(UiState.Failure("Invalid email address"))
            return
        }
        // Sendet eine E-Mail zum Zurücksetzen des Passworts an die angegebene E-Mail-Adresse
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Erfolgreich?
                if (task.isSuccessful) {
                    // Gibt ein erfolgreiches Ergebnis an den Aufrufer zurück
                    result.invoke(UiState.Success("Email has been sent"))
                } else {
                    // Gibt ein fehlgeschlagenes Ergebnis mit der Fehlermeldung an den Aufrufer zurück
                    result.invoke(UiState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email"))
            }
    }

    /**
     * Funktion zur Überprüfung, ob eine E-Mail-Adresse eine gültige Syntax hat.
     * @param email E-Mail-Adresse, die überprüft werden soll.
     * @return true, wenn die E-Mail-Adresse eine gültige Syntax hat, andernfalls false.
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    /**
     * Funktion, um das Ausloggen des Benutzers aus der Anwendung durchzuführen.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein leeres UiState-Objekt und dient nur zur Bestätigung des erfolgreichen Ausloggens.
     */
    override fun logout(result: () -> Unit) {
        // Meldet den Benutzer bei Firebase ab
        auth.signOut()
        // Entfernt die Sitzungsdaten aus dem Local Storage der Anwendung
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        // Gibt ein leeres Ergebnis an den Aufrufer zurück, um das erfolgreiche Ausloggen zu bestätigen
        result.invoke()
    }

    /**
     * Funktion zum Speichern der Sitzungsdetails eines Benutzers im lokalen Speicher
     * @param id ID des Benutzers, dessen Sitzung gespeichert werden soll
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein User-Objekt, das die Details des Benutzers enthält, oder null, wenn das Speichern der Sitzung fehlgeschlagen ist.
     */
    override fun storeSession(id: String, result: (UiState<User>) -> Unit) {
        // Zugriff auf die Firestore-Sammlung "USER"
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnSuccessListener { document ->
                // Holt das User-Objekt aus dem Firestore-Dokument
                val user = document.toObject(User::class.java)
                if (user != null) {
                    // Fügt die Sitzungsdetails des Benutzers im lokalen Speicher hinzu
                    appPreferences.edit()
                        .putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result.invoke(UiState.Success(user))
                }
            }
    }


    /**
     * Funktion zum Abrufen der gespeicherten Sitzungsdaten des aktuell angemeldeten Benutzers.
     * @param result Funktion zum Rückgabewert des Ergebnisses an den Aufrufer.
     * Wenn eine gültige Sitzung vorhanden ist, gibt die Funktion das Benutzerobjekt zurück, andernfalls wird null zurückgegeben.
     */
    override fun getSession(result: ((UiState<User>)) -> Unit) {
        // Holt die gespeicherten Sitzungsdaten des Benutzers aus dem lokalen Speicher
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        // Wenn keine gespeicherten Sitzungsdaten vorhanden sind, gib null zurück
        if (user_str == null) {
            Log.e(TAG,"ES WURDE KEINE USER SESSION GEFUNDEN")
        } else {
            // Konvertiert die JSON-Zeichenfolge in ein Benutzerobjekt
            val user = gson.fromJson(user_str, User::class.java)
            // Gibt das Benutzerobjekt zurück
            result.invoke(UiState.Success(user))
        }
    }
}
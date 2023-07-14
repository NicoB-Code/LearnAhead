package com.example.learnahead_prototyp.Data.Repository

import android.net.Uri
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

/**
 * Das Interface IProfileRepository definiert Methoden, die Interaktionen mit dem Profil ermöglichen.
 */
interface IProfileRepository {

    /**
     * Lädt ein Profilbild hoch.
     *
     * @param imageUri Die URI des hochzuladenden Bildes.
     * @param user Der Benutzer, dem das Profilbild zugeordnet ist.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Profilbild erfolgreich hochgeladen wurde oder nicht.
     */
    fun uploadImage(imageUri: Uri, user: User, result: (UiState<String>) -> Unit)
}
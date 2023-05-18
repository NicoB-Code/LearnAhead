package com.example.learnahead_prototyp.Data.Repository

import android.net.Uri
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState


/**
 * Das Interface IProfileRepository definiert Methoden, die Interaktion mit dem Profil ermöglichen.
 */
interface IProfileRepository {
    // DocString siehe Implementierung

    /**
     * Image Upload
     * bitte noch kommentieren @Nico
     */
     fun uploadImage(imageUri: Uri, user: User, result: (UiState<String>) -> Unit)
}
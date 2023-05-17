package com.example.learnahead_prototyp.Data.Repository

import android.net.Uri
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState
import java.io.File


/**
 * Das Interface IProfileRepository definiert Methoden, die Interaktion mit dem Profil ermöglichen.
 */
interface IProfileRepository {
    // DocString siehe Implementierung
    suspend fun uploadImage(imageUri: Uri, user: User, onResult: (UiState<Uri>) -> Unit)
}
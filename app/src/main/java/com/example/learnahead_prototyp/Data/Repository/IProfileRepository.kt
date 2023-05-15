package com.example.learnahead_prototyp.Data.Repository

import android.net.Uri
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState
import java.io.File


interface IProfileRepository {
    suspend fun uploadImage(imageUri: Uri, user: User, onResult: (UiState<Uri>) -> Unit)
}
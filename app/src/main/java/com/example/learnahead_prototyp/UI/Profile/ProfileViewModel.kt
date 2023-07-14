package com.example.learnahead_prototyp.UI.Profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.IProfileRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Das [ProfileViewModel] sorgt für den Informationsaustausch zwischen
 * dem Repository und der UI. Diese Klasse wird mithilfe
 * von Hilt injiziert und erhält eine Instanz des IProfileRepository-Interfaces, um auf
 * Profil-Funktionen zugreifen zu können.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    val repository: IProfileRepository
) : ViewModel() {

    val TAG: String = "ProfileViewModel"

    /**
     * MutableLiveData, das den aktuellen Zustand der Datei-URI des Benutzerprofils enthält.
     */
    private val _fileUris = MutableLiveData<UiState<String>>()
    val fileUris: LiveData<UiState<String>>
        get() = _fileUris

    /**
     * Ruft das Repository auf, um ein neues Profilbild für den angegebenen Benutzer hochzuladen, und aktualisiert das _fileUris MutableLiveData.
     * @param fileUris Die URI des neuen Bildes.
     * @param user Der Benutzer, für den die Dateien abgerufen werden sollen.
     */
    fun onUploadSingleFile(fileUris: Uri, user: User){
        _fileUris.value = UiState.Loading
        repository.uploadImage(fileUris, user) { _fileUris.value = it}
    }
}
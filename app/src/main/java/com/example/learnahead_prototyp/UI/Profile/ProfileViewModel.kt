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
 * Diese Klasse sorgt für den Informationsaustausch zwischen
 * Repository und der UI. Diese Klasse wird mithilfe
 * von Hilt injeziert und erhält eine Instanz des IProfileRepository-Interfaces, um auf
 * Profil-Funktionen zugreifen zu können.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    val repository: IProfileRepository
) : ViewModel() {

    val TAG: String = "ProfileViewModel"

    private val _fileUris = MutableLiveData<UiState<String>>()
    val fileUris: LiveData<UiState<String>>
        get() = _fileUris


    fun onUploadSingleFile(fileUris: Uri, user: User){
        _fileUris.value = UiState.Loading
        repository.uploadImage(fileUris, user) { _fileUris.value = it}
    }
}
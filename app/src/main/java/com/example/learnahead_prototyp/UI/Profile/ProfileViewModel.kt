package com.example.learnahead_prototyp.UI.Profile

import android.net.Uri
import android.util.Log
import com.example.learnahead_prototyp.Util.UiState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.IProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val repository: IProfileRepository
) : ViewModel() {

    val TAG: String = "ProfileViewModel"

    fun onUploadSingleFile(fileUris: Uri, user: User, onResult: (UiState<Uri>) -> Unit){
        onResult.invoke(UiState.Loading)
        Log.d(TAG ,"This is the ViewModel - URI $fileUris")
        viewModelScope.launch {
            repository.uploadImage(fileUris, user, onResult)
        }
    }

}
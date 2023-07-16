package com.example.learnahead.UI.LearningCategory.Test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.Test
import com.example.learnahead.Data.Model.User
import com.example.learnahead.Data.Repository.ITestRepository
import com.example.learnahead.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HiltViewModel-Klasse, die das ViewModel für die Test-Funktion repräsentiert.
 *
 * @param repository Eine Implementierung von ITestRepository für die Test-Funktion.
 */
@HiltViewModel
class TestViewModel @Inject constructor(
    val repository: ITestRepository
) : ViewModel() {

    /**
     * MutableLiveData, das den aktuellen Zustand der Liste von Tests enthält.
     */
    private val _tests = MutableLiveData<UiState<List<Test>>>()
    val tests: LiveData<UiState<List<Test>>>
        get() = _tests

    /**
     * MutableLiveData, das den aktuellen Test enthält.
     */
    private val _currentTest = MutableLiveData<Test?>()
    val currentTest: LiveData<Test?>
        get() = _currentTest

    fun setCurrentTest(test: Test?) {
        _currentTest.value = test
    }

    /**
     * MutableLiveData, das den aktuellen Zustand beim Hinzufügen eines neuen Tests enthält.
     */
    private val _addTest = MutableLiveData<UiState<Test?>>()
    val addTest: LiveData<UiState<Test?>>
        get() = _addTest

    /**
     * MutableLiveData, das den aktuellen Zustand beim Aktualisieren eines Tests enthält.
     */
    private val _updateTest = MutableLiveData<UiState<Test?>>()
    val updateTest: LiveData<UiState<Test?>>
        get() = _updateTest

    /**
     * MutableLiveData, das den aktuellen Zustand beim Löschen eines Tests enthält.
     */
    private val _deleteTest = MutableLiveData<UiState<String>>()
    val deleteTest: LiveData<UiState<String>>
        get() = _deleteTest

    /**
     * Ruft das Repository auf, um die Liste der Tests für den angegebenen Benutzer und die angegebene Lernkategorie abzurufen, und aktualisiert das _tests MutableLiveData.
     *
     * @param user Der Benutzer, für den die Tests abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, für die die Tests abgerufen werden sollen.
     */
    fun getTests(user: User?, learningCategory: LearningCategory) {
        _tests.value = UiState.Loading
        repository.getTests(user, learningCategory) { _tests.value = it }
    }

    /**
     * Ruft das Repository auf, um einen neuen Test hinzuzufügen, und aktualisiert das _addTest MutableLiveData.
     *
     * @param test Der Test, der hinzugefügt werden soll.
     */
    fun addTest(test: Test) {
        _addTest.value = UiState.Loading
        repository.addTest(test) { _addTest.value = it }
    }

    /**
     * Ruft das Repository auf, um einen Test zu aktualisieren, und aktualisiert das _updateTest MutableLiveData.
     *
     * @param test Der Test, der aktualisiert werden soll.
     */
    fun updateTest(test: Test) {
        _updateTest.value = UiState.Loading
        repository.updateTest(test) { _updateTest.value = it }
    }

    /**
     * Ruft das Repository auf, um einen Test zu löschen, und aktualisiert das _deleteTest MutableLiveData.
     *
     * @param test Der Test, der gelöscht werden soll.
     */
    fun deleteTest(test: Test) {
        _deleteTest.value = UiState.Loading
        repository.deleteTest(test) { _deleteTest.value = it }
    }
}

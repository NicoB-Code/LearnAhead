package com.example.learnahead.UI.LearningCategory.Question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.Question
import com.example.learnahead.Data.Model.User
import com.example.learnahead.Data.Repository.IQuestionRepository
import com.example.learnahead.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HiltViewModel-Klasse, die das ViewModel für die Frage-Funktion repräsentiert.
 * @param repository Eine Implementierung von IQuestionRepository für die Frage-Funktion.
 */
@HiltViewModel
class QuestionViewModel @Inject constructor(
    val repository: IQuestionRepository
) : ViewModel() {

    /**
     * MutableLiveData, das den aktuellen Zustand der Liste der Fragen enthält.
     */
    private val _questions = MutableLiveData<UiState<List<Question>>>()
    val question: LiveData<UiState<List<Question>>>
        get() = _questions

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?>
        get() = _currentQuestion

    /**
     * Legt die aktuelle Frage fest.
     * @param question Die Frage, die festgelegt werden soll.
     */
    fun setCurrentQuestion(question: Question?) {
        _currentQuestion.value = question
    }

    /**
     * MutableLiveData, das den aktuellen Zustand des Hinzufügens einer neuen Frage enthält.
     */
    private val _addQuestion = MutableLiveData<UiState<Question?>>()
    val addQuestion: LiveData<UiState<Question?>>
        get() = _addQuestion

    /**
     * MutableLiveData, das den aktuellen Zustand der Aktualisierung einer Frage enthält.
     */
    private val _updateQuestion = MutableLiveData<UiState<Question?>>()
    val updateQuestion: LiveData<UiState<Question?>>
        get() = _updateQuestion

    /**
     * MutableLiveData, das den aktuellen Zustand des Löschens einer Frage enthält.
     */
    private val _deleteQuestion = MutableLiveData<UiState<String>>()
    val deleteQuestion: LiveData<UiState<String>>
        get() = _deleteQuestion

    /**
     * Ruft das Repository auf, um die Liste der Fragen für den angegebenen Benutzer abzurufen,
     * und aktualisiert das _questions MutableLiveData.
     * @param user Der Benutzer, für den die Fragen abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, zu der die Fragen gehören sollen.
     */
    fun getQuestions(user: User?, learningCategory: LearningCategory) {
        _questions.value = UiState.Loading
        repository.getQuestions(user, learningCategory) { _questions.value = it }
    }

    /**
     * Ruft das Repository auf, um eine neue Frage hinzuzufügen, und aktualisiert das _addQuestion MutableLiveData.
     * @param question Die Frage, die hinzugefügt werden soll.
     */
    fun addQuestion(question: Question) {
        _addQuestion.value = UiState.Loading
        repository.addQuestion(question) { _addQuestion.value = it }
    }

    /**
     * Ruft das Repository auf, um eine Frage zu aktualisieren, und aktualisiert das _updateQuestion MutableLiveData.
     * @param question Die Frage, die aktualisiert werden soll.
     */
    fun updateQuestion(question: Question) {
        _updateQuestion.value = UiState.Loading
        repository.updateQuestion(question) { _updateQuestion.value = it }
    }

    /**
     * Ruft das Repository auf, um eine Frage zu löschen, und aktualisiert das _deleteQuestion MutableLiveData.
     * @param question Die Frage, die gelöscht werden soll.
     */
    fun deleteQuestion(question: Question) {
        _deleteQuestion.value = UiState.Loading
        repository.deleteQuestion(question) { _deleteQuestion.value = it }
    }
}

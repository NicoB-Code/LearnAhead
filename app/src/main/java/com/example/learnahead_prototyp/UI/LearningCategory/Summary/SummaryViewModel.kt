package com.example.learnahead_prototyp.UI.LearningCategory.Summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.ISummaryRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel-Klasse für die Zusammenfassungsansicht.
 * @param repository Eine Implementierung von ISummaryRepository für die Zusammenfassungsansicht.
 */
@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: ISummaryRepository
) : ViewModel() {

    /**
     * MutableLiveData, das den aktuellen Zustand der Liste der Zusammenfassungen enthält.
     */
    private val _summaries = MutableLiveData<UiState<List<Summary>>>()
    val summary: LiveData<UiState<List<Summary>>>
        get() = _summaries

    private val _currentLearningCategory = MutableLiveData<UiState<LearningCategory>>()
    val currentLearningCategory: LiveData<UiState<LearningCategory>>
        get() = _currentLearningCategory

    /**
     * MutableLiveData, das den aktuellen Zustand des Hinzufügens einer neuen Zusammenfassung enthält.
     */
    private val _addSummary = MutableLiveData<UiState<Summary?>>()
    val addSummary: LiveData<UiState<Summary?>>
        get() = _addSummary

    /**
     * MutableLiveData, das den aktuellen Zustand der Aktualisierung einer Zusammenfassung enthält.
     */
    private val _updateSummary = MutableLiveData<UiState<Summary?>>()
    val updateSummary: LiveData<UiState<Summary?>>
        get() = _updateSummary

    /**
     * MutableLiveData, das den aktuellen Zustand des Löschens einer Zusammenfassung enthält.
     */
    private val _deleteSummary = MutableLiveData<UiState<String>>()
    val deleteSummary: LiveData<UiState<String>>
        get() = _deleteSummary

    /**
     * Ruft das Repository auf, um die Liste der Zusammenfassungen für den gegebenen Benutzer und die gegebene Lernkategorie abzurufen
     * und aktualisiert das _summaries MutableLiveData.
     * @param user Der Benutzer, für den die Zusammenfassungen abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, für die die Zusammenfassungen abgerufen werden sollen.
     */
    fun getSummaries(user: User?, learningCategory: LearningCategory) {
        _summaries.value = UiState.Loading
        repository.getSummaries(user, learningCategory) { _summaries.value = it }
    }

    /**
     * Ruft das Repository auf, um eine neue Zusammenfassung hinzuzufügen und aktualisiert das _addSummary MutableLiveData.
     * @param summary Die hinzuzufügende Zusammenfassung.
     */
    fun addSummary(summary: Summary) {
        _addSummary.value = UiState.Loading
        repository.addSummary(summary) { _addSummary.value = it }
    }

    /**
     * Ruft das Repository auf, um eine Zusammenfassung zu löschen und aktualisiert das _deleteSummary MutableLiveData.
     * @param summary Die zu löschende Zusammenfassung.
     */
    fun deleteSummary(summary: Summary) {
        _deleteSummary.value = UiState.Loading
        repository.deleteSummary(summary) { _deleteSummary.value = it }
    }

    /**
     * Ruft das Repository auf, um eine Zusammenfassung zu aktualisieren und aktualisiert das _updateSummary MutableLiveData.
     * @param summary Die zu aktualisierende Zusammenfassung.
     */
    fun updateSummary(summary: Summary){
        _updateSummary.value = UiState.Loading
        repository.updateSummary(summary) { _updateSummary.value = it}
    }
}
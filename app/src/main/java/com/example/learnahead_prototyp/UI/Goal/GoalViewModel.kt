package com.example.learnahead_prototyp.UI.Goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Data.Repository.IGoalRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * HiltViewModel class that represents the ViewModel for the Goals feature.
 * @param repository an implementation of IGoalRepository for the Goals feature.
 */
@HiltViewModel
class GoalViewModel @Inject constructor(
    val repository: IGoalRepository
) : ViewModel() {

    /**
     * MutableLiveData that holds the current state of the list of Goals.
     */
    private val _goals = MutableLiveData<UiState<List<Goal>>>()
    val goal: LiveData<UiState<List<Goal>>>
        get() = _goals

    /**
     * MutableLiveData that holds the current state of adding a new Goal.
     */
    private val _addGoal = MutableLiveData<UiState<Goal?>>()
    val addGoal: LiveData<UiState<Goal?>>
        get() = _addGoal

    /**
     * MutableLiveData that holds the current state of updating a Goal.
     */
    private val _updateGoal = MutableLiveData<UiState<Goal?>>()
    val updateGoal: LiveData<UiState<Goal?>>
        get() = _updateGoal

    /**
     * MutableLiveData that holds the current state of deleting a Goal.
     */
    private val _deleteGoal = MutableLiveData<UiState<String>>()
    val deleteGoal: LiveData<UiState<String>>
        get() = _deleteGoal

    /**
     * Calls the repository to get the list of Goals for the given User and updates the _goals MutableLiveData.
     * @param user the User for which to retrieve the Goals.
     */
    fun getGoals(user: User?) {
        _goals.value = UiState.Loading
        repository.getGoals(user) { _goals.value = it }
    }

    /**
     * Calls the repository to add a new Goal and updates the _addGoal MutableLiveData.
     * @param goal the Goal to add.
     */
    fun addGoal(goal: Goal) {
        _addGoal.value = UiState.Loading
        repository.addGoal(goal) { _addGoal.value = it }
    }

    /**
     * Calls the repository to update a Goal and updates the _updateGoal MutableLiveData.
     * @param goal the Goal to update.
     */
    fun updateGoal(goal: Goal) {
        _updateGoal.value = UiState.Loading
        repository.updateGoal(goal) { _updateGoal.value = it }
    }

    /**
     * Calls the repository to delete a Goal and updates the _deleteGoal MutableLiveData.
     * @param goal the Goal to delete.
     */
    fun deleteGoal(goal: Goal) {
        _deleteGoal.value = UiState.Loading
        repository.deleteGoal(goal) { _deleteGoal.value = it }
    }
}
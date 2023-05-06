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

@HiltViewModel
class GoalViewModel @Inject constructor(
    val repository: IGoalRepository
) : ViewModel() {

    private val _goals = MutableLiveData<UiState<List<Goal>>>()
    val goal: LiveData<UiState<List<Goal>>>
        get() = _goals

    private val _addGoal = MutableLiveData<UiState<String>>()
    val addGoal: LiveData<UiState<String>>
        get() = _addGoal

    private val _updateGoal = MutableLiveData<UiState<String>>()
    val updateGoal: LiveData<UiState<String>>
        get() = _updateGoal

    private val _deleteGoal = MutableLiveData<UiState<String>>()
    val deleteGoal: LiveData<UiState<String>>
        get() = _deleteGoal

    fun getGoals(user: User?) {
        _goals.value = UiState.Loading
        repository.getGoals(user) { _goals.value = it }
    }

    fun addGoal(goal: Goal) {
        _addGoal.value = UiState.Loading
        repository.addGoal(goal) { _addGoal.value = it }
    }

    fun updateGoal(goal: Goal) {
        _updateGoal.value = UiState.Loading
        repository.updateGoal(goal) { _updateGoal.value = it }
    }

    fun deleteGoal(goal: Goal) {
        _deleteGoal.value = UiState.Loading
        repository.deleteGoal(goal) { _deleteGoal.value = it }
    }
}
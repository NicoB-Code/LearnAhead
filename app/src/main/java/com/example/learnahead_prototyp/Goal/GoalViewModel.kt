package com.example.learnahead_prototyp.Goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Data.Repository.IGoalRepository
import com.example.learnahead_prototyp.Util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    val repository: IGoalRepository
): ViewModel()  {

    private val _goals = MutableLiveData<UiState<List<Goal>>>()
    val goal: LiveData<UiState<List<Goal>>>
        get() = _goals

    private val _addGoal = MutableLiveData<UiState<String>>()
    val addGoal: LiveData<UiState<String>>
        get() = _addGoal

    fun getGoals() {
        _goals.value = UiState.Loading
        repository.getGoals { _goals.value = it }
    }

    fun addGoal(goal: Goal) {
        _addGoal.value = UiState.Loading
        repository.addGoal(goal) { _addGoal.value = it }
    }
}
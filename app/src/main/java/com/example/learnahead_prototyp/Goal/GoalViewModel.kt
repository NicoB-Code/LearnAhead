package com.example.learnahead_prototyp.Goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Data.Repository.GoalRepository
import com.example.learnahead_prototyp.Data.Repository.IGoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    val repository: IGoalsRepository
): ViewModel()  {

    private val _goals = MutableLiveData<List<Goal>>()
    val goal: LiveData<List<Goal>>
        get() = _goals

    fun getGoals() {
        _goals.value = repository.getGoals()
    }
}
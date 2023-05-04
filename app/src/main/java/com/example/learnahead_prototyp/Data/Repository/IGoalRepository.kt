package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Util.UiState

interface IGoalRepository {
    fun getGoals(result: (UiState<List<Goal>>) -> Unit)
    fun addGoal(goal: Goal, result: (UiState<String>) -> Unit)
    fun updateGoal(goal: Goal, result: (UiState<String>) -> Unit)
    fun deleteGoal(goal: Goal, result: (UiState<String>) -> Unit)
}
package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Util.UiState

interface IGoalRepository {
    fun getGoals(): UiState<List<Goal>>
}
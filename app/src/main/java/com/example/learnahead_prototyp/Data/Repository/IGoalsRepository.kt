package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal

interface IGoalsRepository {
    fun getGoals(): List<Goal>
}
package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class GoalRepository (
    val database: FirebaseFirestore): IGoalRepository {
    override fun getGoals(): UiState<List<Goal>> {
        val data = arrayListOf(
            Goal(
                id="fdasf",
                description =  "Goal 1",
                beginDate =  Date(),
                endDate = Date()
            ),
            Goal(
                id="fdasf",
                description =  "Goal 2",
                beginDate =  Date(),
                endDate = Date()
            ),
            Goal(
                id="fdasf",
                description =  "Goal 3",
                beginDate =  Date(),
                endDate = Date()
            )
        )

        if(data.isNullOrEmpty()){
            return UiState.Failure("Data is empty")
        } else {
            return UiState.Success(data)
        }

    }
}
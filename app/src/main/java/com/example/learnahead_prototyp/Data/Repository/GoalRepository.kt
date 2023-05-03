package com.example.learnahead_prototyp.Data.Repository

import android.app.DatePickerDialog
import com.example.learnahead_prototyp.Data.Model.Goal
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class GoalRepository (
    val database: FirebaseFirestore): IGoalsRepository {
    override fun getGoals(): List<Goal> {
        return arrayListOf(
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
    }
}
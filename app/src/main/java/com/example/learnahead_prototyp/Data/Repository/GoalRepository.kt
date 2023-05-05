package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

class GoalRepository (
    val database: FirebaseFirestore): IGoalRepository {
    override fun getGoals(result: (UiState<List<Goal>>) -> Unit) {
        database.collection(FireStoreCollection.GOAL)
            .get()
            .addOnSuccessListener {
                val goals = arrayListOf<Goal>()
                for (document in it) {
                    val goal = document.toObject(Goal::class.java)
                    goals.add(goal)
                }
                result.invoke(
                    UiState.Success(goals)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override fun addGoal(goal: Goal, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.GOAL).document()
        goal.id = document.id
        document
            .set(goal)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Goal has been created successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateGoal(goal: Goal, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.GOAL).document(goal.id)
        document
            .set(goal)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Goal has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun deleteGoal(goal: Goal, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.GOAL).document(goal.id)
        document
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Goal has been deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
}
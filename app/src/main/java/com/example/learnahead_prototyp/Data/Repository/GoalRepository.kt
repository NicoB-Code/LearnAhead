package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.FireStoreDocumentField
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GoalRepository (
    val database: FirebaseFirestore): IGoalRepository {
    override fun getGoals(user: User?, result: (UiState<List<Goal>>) -> Unit) {
        // Wenn man order By nutzen will muss man ein Index in Firebase erstellen. Fehler würde im LogCat stehen
        // Falls wir es abändern bzgl. mehr Variablen
        database.collection(FireStoreCollection.GOAL)
            .whereEqualTo(FireStoreDocumentField.USER_ID, user?.id)
                // WICHTIG!!! INDEX MUSS IN DER ZUKUNFT GEÄNDERT WERDEN!!!!!!!!!!!
            .orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
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
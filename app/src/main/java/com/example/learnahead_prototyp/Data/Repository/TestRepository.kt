package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Test
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Klasse, die die Schnittstelle IGoalRepository implementiert und Methoden enthält, um Ziele aus der Datenbank zu holen.
 * @param database Die Firebase Firestore-Datenbank-Instanz, auf die zugegriffen werden soll.
 */
class TestRepository(
    val database: FirebaseFirestore
) : ITestRepository {
    override fun getTests(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Test>>) -> Unit) {
        // Überprüfen, ob learningCategory null ist
        if (learningCategory == null) {
            result(UiState.Failure("learningCategory is null"))
            return
        }

        // Überprüfen, ob user null ist
        if (user == null) {
            result(UiState.Failure("user is null"))
            return
        }

        // Dokumentreferenz des Benutzers holen
        val userDocumentRef = database.collection(FireStoreCollection.USER).document(user.id)

        // Benutzerdokument abrufen
        val learningCategoriesTask = userDocumentRef.get().continueWith { task ->
            task.result?.toObject(User::class.java)?.learningCategories
        }

        // Tests für die Ziel-Lernkategorie holen
        val testTask = learningCategoriesTask.continueWith { task ->
            val learningCategories = task.result
            if (learningCategories.isNullOrEmpty()) {
                throw Exception("Learning categories are null or empty")
            }
            val targetLearningCategory = learningCategories.find { it.id == learningCategory.id }
                ?: throw Exception("Learning category not found")
            targetLearningCategory.tests
        }

        // Ergebnis zurückgeben
        testTask.addOnSuccessListener { tests ->
            result(UiState.Success(tests))
        }.addOnFailureListener { exception ->
            result(UiState.Failure(exception.localizedMessage))
        }
    }

    override fun addTest(test: Test, result: (UiState<Test?>) -> Unit) {
        val document = database.collection(FireStoreCollection.TEST).document()
            test.id = document.id
            document.set(test).addOnSuccessListener {
                result(UiState.Success(test))
            }.addOnFailureListener {
                result(UiState.Failure(it.localizedMessage))
            }
        }

    override fun updateTest(test: Test, result: (UiState<Test?>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteTest(test: Test, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }
}
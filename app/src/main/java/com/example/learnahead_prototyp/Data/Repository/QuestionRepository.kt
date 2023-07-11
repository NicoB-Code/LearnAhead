package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Klasse, die die Schnittstelle IGoalRepository implementiert und Methoden enthält, um Ziele aus der Datenbank zu holen.
 * @param database Die Firebase Firestore-Datenbank-Instanz, auf die zugegriffen werden soll.
 */
class QuestionRepository(
    val database: FirebaseFirestore
) : IQuestionRepository {

    override fun getQuestions(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Question>>) -> Unit) {
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

        // Zusammenfassungen für die Ziel-Lernkategorie holen
        val questionTask = learningCategoriesTask.continueWith { task ->
            val learningCategories = task.result
            if (learningCategories.isNullOrEmpty()) {
                throw Exception("Learning categories are null or empty")
            }
            val targetLearningCategory = learningCategories.find { it.id == learningCategory.id }
                ?: throw Exception("Learning category not found")
            targetLearningCategory.questions
        }

        // Ergebnis zurückgeben
        questionTask.addOnSuccessListener { questions ->
            result(UiState.Success(questions))
        }.addOnFailureListener { exception ->
            result(UiState.Failure(exception.localizedMessage))
        }
    }

    override fun addQuestion(question: Question, result: (UiState<Question?>) -> Unit) {
        // Save the tags to the database
        val tagsCollection = database.collection(FireStoreCollection.TAG)
        val batch = database.batch()
        question.tags.forEach { tag ->
            val tagRef = tagsCollection.document()
            tag.id = tagRef.id
            batch.set(tagRef, tag)
        }
        batch.commit().addOnSuccessListener {
            // Add the question to the database
            val document = database.collection(FireStoreCollection.QUESTION).document()
            question.id = document.id
            document.set(question).addOnSuccessListener {
                result(UiState.Success(question))
            }.addOnFailureListener {
                result(UiState.Failure(it.localizedMessage))
            }
        }.addOnFailureListener {
            result(UiState.Failure(it.localizedMessage))
        }
    }

    override fun updateQuestion(question: Question, result: (UiState<Question?>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteQuestion(question: Question, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }
}
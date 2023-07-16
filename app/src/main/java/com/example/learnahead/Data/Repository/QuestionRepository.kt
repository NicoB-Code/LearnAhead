package com.example.learnahead.Data.Repository

import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.Question
import com.example.learnahead.Data.Model.User
import com.example.learnahead.Util.FireStoreCollection
import com.example.learnahead.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Eine Klasse, die das IQuestionRepository-Interface implementiert und Methoden enthält, um Fragen aus der Datenbank abzurufen, hinzuzufügen, zu aktualisieren und zu löschen.
 *
 * @param database Die Firebase Firestore-Datenbankinstanz, auf die zugegriffen werden soll.
 */
class QuestionRepository(
    private val database: FirebaseFirestore
) : IQuestionRepository {

    /**
     * Funktion zum Abrufen der Fragen aus der Datenbank für einen bestimmten Benutzer und eine bestimmte Lernkategorie.
     *
     * @param user Der Benutzer, für den die Fragen abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, für die die Fragen abgerufen werden sollen.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Liste von Fragen oder eine Fehlermeldung enthält.
     */
    override fun getQuestions(
        user: User?,
        learningCategory: LearningCategory?,
        result: (UiState<List<Question>>) -> Unit
    ) {
        // Überprüfen, ob learningCategory null ist
        if (learningCategory == null) {
            result(UiState.Failure("learningCategory ist null"))
            return
        }

        // Überprüfen, ob user null ist
        if (user == null) {
            result(UiState.Failure("user ist null"))
            return
        }

        // Dokumentreferenz des Benutzers abrufen
        val userDocumentRef = database.collection(FireStoreCollection.USER).document(user.id)

        // Benutzerdokument abrufen
        userDocumentRef.get()
            .addOnSuccessListener { userDocument ->
                val userObject = userDocument.toObject(User::class.java)
                val learningCategories = userObject?.learningCategories

                if (learningCategories.isNullOrEmpty()) {
                    result(UiState.Failure("Lernkategorien sind null oder leer"))
                    return@addOnSuccessListener
                }

                val targetLearningCategory =
                    learningCategories.find { it.id == learningCategory.id }

                if (targetLearningCategory == null) {
                    result(UiState.Failure("Lernkategorie nicht gefunden"))
                    return@addOnSuccessListener
                }

                result(UiState.Success(targetLearningCategory.questions))
            }
            .addOnFailureListener { exception ->
                result(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Funktion zum Hinzufügen einer neuen Frage in die Datenbank.
     *
     * @param question Die Frage, die hinzugefügt werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie die hinzugefügte Frage oder eine Fehlermeldung enthält.
     */
    override fun addQuestion(question: Question, result: (UiState<Question?>) -> Unit) {
        // Tags in die Datenbank speichern
        val tagsCollection = database.collection(FireStoreCollection.TAG)
        val batch = database.batch()

        question.tags.forEach { tag ->
            val tagRef = tagsCollection.document()
            tag.id = tagRef.id
            batch.set(tagRef, tag)
        }

        batch.commit()
            .addOnSuccessListener {
                // Frage in die Datenbank hinzufügen
                val document = database.collection(FireStoreCollection.QUESTION).document()
                question.id = document.id
                document.set(question)
                    .addOnSuccessListener {
                        result(UiState.Success(question))
                    }
                    .addOnFailureListener { exception ->
                        result(UiState.Failure(exception.localizedMessage))
                    }
            }
            .addOnFailureListener { exception ->
                result(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Funktion zum Aktualisieren einer vorhandenen Frage in der Datenbank.
     *
     * @param question Die Frage, die aktualisiert werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie die aktualisierte Frage oder eine Fehlermeldung enthält.
     */
    override fun updateQuestion(question: Question, result: (UiState<Question?>) -> Unit) {
        // Dokument des Tests in der Datenbank abrufen
        val document = database.collection(FireStoreCollection.TEST).document(question.id)

        // Test in der Datenbank aktualisieren
        document.set(question)
            .addOnSuccessListener {
                result(UiState.Success(question))
            }
            .addOnFailureListener { exception ->
                // Fehlermeldung an den Aufrufer zurückgeben
                result(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Funktion zum Löschen einer Frage aus der Datenbank.
     *
     * @param question Die Frage, die gelöscht werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Erfolgsmeldung oder eine Fehlermeldung enthält.
     */
    override fun deleteQuestion(question: Question, result: (UiState<String>) -> Unit) {
        // Zugriff auf das Test-Dokument in der Datenbank basierend auf der Test-ID
        val document = database.collection(FireStoreCollection.TEST).document(question.id)

        // Test-Dokument aus der Datenbank löschen
        document.delete()
            .addOnSuccessListener {
                // Erfolgsmeldung an den Aufrufer zurückgeben
                result(UiState.Success("Question wurde erfolgreich gelöscht"))
            }
            .addOnFailureListener { exception ->
                // Fehlermeldung an den Aufrufer zurückgeben
                result(UiState.Failure(exception.localizedMessage))
            }
    }
}

package com.example.learnahead.DI

import android.content.SharedPreferences
import com.example.learnahead.Data.Repository.AuthRepository
import com.example.learnahead.Data.Repository.GoalRepository
import com.example.learnahead.Data.Repository.IAuthRepository
import com.example.learnahead.Data.Repository.IGoalRepository
import com.example.learnahead.Data.Repository.ILearnCategoryRepository
import com.example.learnahead.Data.Repository.IProfileRepository
import com.example.learnahead.Data.Repository.IQuestionRepository
import com.example.learnahead.Data.Repository.ISummaryRepository
import com.example.learnahead.Data.Repository.ITestRepository
import com.example.learnahead.Data.Repository.LearningCategoryRepository
import com.example.learnahead.Data.Repository.ProfileRepository
import com.example.learnahead.Data.Repository.QuestionRepository
import com.example.learnahead.Data.Repository.SummaryRepository
import com.example.learnahead.Data.Repository.TestRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Die Klasse RepositoryModule enthält Funktionen, um die Repositories des Projekts mit Dagger Hilt zu konfigurieren und bereitzustellen.
 * Sie ist mit @InstallIn(SingletonComponent::class) annotiert, was bedeutet, dass die darin definierten Abhängigkeiten während der gesamten Lebensdauer der Anwendung verfügbar sind.
 * Die @Provides-Annotation wird verwendet, um Methoden in einem Modul zu markieren, die Abhängigkeiten bereitstellen.
 * Die Methoden werden vom Hilt Dependency Injection Framework verwendet, um die Abhängigkeiten bei Bedarf zu instanziieren.
 */
@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    /**
     * Funktion zur Bereitstellung eines IGoalRepository-Objekts, das Zugriff auf die Ziele in der Firebase-Datenbank bietet.
     *
     * @param database Die Instanz der Firebase Firestore-Datenbank, auf die zugegriffen werden soll.
     * @return Das IGoalRepository-Objekt, das für den Zugriff auf die Ziele in der Datenbank verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideGoalRepository(
        database: FirebaseFirestore
    ): IGoalRepository {
        return GoalRepository(database)
    }

    /**
     * Funktion zur Bereitstellung eines ISummaryRepository-Objekts, das Zugriff auf die Zusammenfassungen in der Firebase-Datenbank bietet.
     *
     * @param database Die Instanz der Firebase Firestore-Datenbank, auf die zugegriffen werden soll.
     * @return Das ISummaryRepository-Objekt, das für den Zugriff auf die Zusammenfassungen in der Datenbank verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideSummaryRepository(
        database: FirebaseFirestore
    ): ISummaryRepository {
        return SummaryRepository(database)
    }

    /**
     * Funktion zur Bereitstellung eines ILearnCategoryRepository-Objekts, das Zugriff auf die Lernkategorien in der Firebase-Datenbank bietet.
     *
     * @param database Die Instanz der Firebase Firestore-Datenbank, auf die zugegriffen werden soll.
     * @return Das ILearnCategoryRepository-Objekt, das für den Zugriff auf die Lernkategorien in der Datenbank verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideLearningCategoryRepository(
        database: FirebaseFirestore
    ): ILearnCategoryRepository {
        return LearningCategoryRepository(database)
    }

    /**
     * Funktion zur Bereitstellung eines IAuthRepository-Objekts, das Zugriff auf die Firebase-Authentifizierung und die Benutzerdaten in der Firebase-Datenbank bietet.
     *
     * @param database Die Instanz der Firebase Firestore-Datenbank, auf die zugegriffen werden soll.
     * @param auth Die Instanz der Firebase Authentifizierung, auf die zugegriffen werden soll.
     * @param appPreferences Das SharedPreferences-Objekt, das für die lokale Datenspeicherung verwendet werden soll.
     * @param gson Das Gson-Objekt, das für die Serialisierung und Deserialisierung von JSON-Daten verwendet werden soll.
     * @return Das IAuthRepository-Objekt, das für den Zugriff auf die Authentifizierung und die Benutzerdaten in der Datenbank verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): IAuthRepository {
        return AuthRepository(auth, database, appPreferences, gson)
    }

    /**
     * Funktion zur Bereitstellung eines IProfileRepository-Objekts, das Zugriff auf das Benutzerprofil und die Profilbilder in der Firebase-Datenbank bietet.
     *
     * @param database Die Instanz der Firebase Firestore-Datenbank, auf die zugegriffen werden soll.
     * @param storageReference Die StorageReference für den Firebase Storage.
     * @return Das IProfileRepository-Objekt, das für den Zugriff auf das Benutzerprofil und die Profilbilder in der Datenbank verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideProfileRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): IProfileRepository {
        return ProfileRepository(database, storageReference)
    }

    /**
     * Funktion zur Bereitstellung eines IQuestionRepository-Objekts, das Zugriff auf die Fragen in der Firebase-Datenbank bietet.
     *
     * @param database Die Instanz der Firebase Firestore-Datenbank, auf die zugegriffen werden soll.
     * @return Das IQuestionRepository-Objekt, das für den Zugriff auf die Fragen in der Datenbank verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideQuestionRepository(
        database: FirebaseFirestore
    ): IQuestionRepository {
        return QuestionRepository(database)
    }

    /**
     * Funktion zur Bereitstellung eines ITestRepository-Objekts, das Zugriff auf die Tests in der Firebase-Datenbank bietet.
     *
     * @param database Die Instanz der Firebase Firestore-Datenbank, auf die zugegriffen werden soll.
     * @return Das ITestRepository-Objekt, das für den Zugriff auf die Tests in der Datenbank verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideTestRepository(
        database: FirebaseFirestore
    ): ITestRepository {
        return TestRepository(database)
    }
}

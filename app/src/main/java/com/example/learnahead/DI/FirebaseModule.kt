package com.example.learnahead.DI

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Die Klasse FirebaseModule enthält Funktionen zur Konfiguration und Bereitstellung von Firebase-Abhängigkeiten mit Dagger Hilt.
 * Sie ist mit @InstallIn(SingletonComponent::class) annotiert, was bedeutet, dass die darin definierten Abhängigkeiten während der gesamten Lebensdauer der Anwendung verfügbar sind.
 * Die @Provides-Annotation wird verwendet, um Methoden in einem Modul zu markieren, die Abhängigkeiten bereitstellen.
 * Die Methoden werden vom Hilt Dependency Injection Framework verwendet, um die Abhängigkeiten bei Bedarf zu instanziieren.
 */
@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    /**
     * Funktion zur Bereitstellung einer Instanz von FirebaseDatabase.
     * @return Das FirebaseDatabase-Objekt.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    /**
     * Funktion zur Bereitstellung einer Instanz von FirebaseFirestore.
     * @return Das FirebaseFirestore-Objekt.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Funktion zur Bereitstellung einer Instanz von FirebaseAuth.
     * @return Das FirebaseAuth-Objekt.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
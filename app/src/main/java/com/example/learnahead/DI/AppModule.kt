package com.example.learnahead.DI

import android.content.Context
import android.content.SharedPreferences
import com.example.learnahead.Util.SharedPrefConstants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Die Klasse AppModule enthält Funktionen, um die Abhängigkeiten des Projekts mit Dagger Hilt zu konfigurieren und bereitzustellen.
 * Sie ist mit @InstallIn(SingletonComponent::class) annotiert, was bedeutet, dass die darin definierten Abhängigkeiten während der gesamten Lebensdauer der Anwendung verfügbar sind.
 * Die @Provides-Annotation wird verwendet, um Methoden in einem Modul zu markieren, die Abhängigkeiten bereitstellen.
 * Die Methoden werden vom Hilt Dependency Injection Framework verwendet, um die Abhängigkeiten bei Bedarf zu instanziieren.
 */
@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    /**
     * Funktion zur Bereitstellung eines SharedPreferences-Objekts für die lokale Datenspeicherung.
     *
     * @param context Der Anwendungscontext.
     * @return Das SharedPreferences-Objekt, das für die lokale Datenspeicherung verwendet werden soll.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            SharedPrefConstants.LOCAL_SHARED_PREF,
            Context.MODE_PRIVATE
        )
    }

    /**
     * Funktion zur Bereitstellung eines StorageReference-Objekts für die Firebase Storage-Referenz.
     *
     * @return Das StorageReference-Objekt für den Firebase Storage.
     */
    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference {
        // Ein Instanz des StorageReference-Objekts zurückgeben
        return FirebaseStorage.getInstance().reference
    }

    /**
     * Funktion zur Bereitstellung eines Gson-Objekts für die Serialisierung und Deserialisierung von JSON-Daten.
     *
     * @return Das Gson-Objekt.
     * Das Objekt wird als Singleton mit @Singleton annotiert, um sicherzustellen, dass nur eine Instanz davon erstellt wird.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}

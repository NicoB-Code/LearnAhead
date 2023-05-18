package com.example.learnahead_prototyp.Util

/**
 * Eine abstrakte Klasse, die den verschiedenen Zuständen darstellt, in denen sich ein UI befinden kann.
 *
 * @param T der Typ der Daten, die der Zustand beinhalten kann.
 */
sealed class UiState<out T> {

    /**
     * Ein Zustand, der angibt, dass die Daten noch geladen werden.
     */
    object Loading : UiState<Nothing>()

    /**
     * Ein Zustand, der angibt, dass die Daten erfolgreich geladen wurden.
     * @property data die Daten, die erfolgreich geladen wurden.
     */
    data class Success<out T>(val data: T) : UiState<T>()

    /**
     * Ein Zustand, der angibt, dass ein Fehler aufgetreten ist.
     * @property error die Fehlermeldung, die den aufgetretenen Fehler beschreibt.
     */
    data class Failure(val error: String?) : UiState<Nothing>()
}

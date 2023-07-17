package com.example.learnahead

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.AndroidEntryPoint

/**
 * Die Hauptaktivität der Anwendung.
 * Sie dient als Einstiegspunkt und enthält das Layout der Benutzeroberfläche.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "DocSnippets"

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Hier wird das Layout der Benutzeroberfläche gesetzt.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
package com.example.learnahead_prototyp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Die Anwendungsklasse der App.
 * Sie wird beim Start der App initialisiert und dient als zentraler Punkt für die Konfiguration von Hilt.
 */
@HiltAndroidApp
class MyApplication : Application() {
}
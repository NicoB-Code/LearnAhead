package com.example.learnahead_prototyp.Util

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Versteckt die View, indem die visibility auf View.GONE gesetzt wird.
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Zeigt die View, indem die visibility auf View.VISIBLE gesetzt wird.
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Zeigt einen Toast mit einer Nachricht [msg] für die Dauer Toast.LENGTH_LONG an.
 * Erfordert ein Fragment und greift auf dessen Context zu.
 */
fun Fragment.toast(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}

/**
 * Überprüft, ob der String ein gültiges E-Mail-Format hat.
 * @return True, wenn der String ein gültiges E-Mail-Format hat, ansonsten False.
 */
fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

package com.example.learnahead.Util

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.util.Calendar
import kotlin.random.Random

/**
 * Versteckt die View, indem die Sichtbarkeit auf View.GONE gesetzt wird.
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Zeigt die View, indem die Sichtbarkeit auf View.VISIBLE gesetzt wird.
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Zeigt einen Toast mit einer Nachricht [msg] für die Dauer Toast.LENGTH_LONG an.
 * Erfordert ein Fragment und greift auf dessen Context zu.
 * @param msg Die anzuzeigende Nachricht.
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

/**
 * Gibt einen zufälligen Lerntipp aus der Liste der Lerntipps zurück.
 * @return Ein zufälliger Lerntipp.
 */
fun getRandomLerntipp(): String {
    return Lerntipps.LERNTIPPS_LISTE[Random.nextInt(Lerntipps.LERNTIPPS_LISTE.size)]
}

/**
 * Ein Dialogfragment für den DatePicker.
 * @param listener Ein Lambda-Ausdruck, der aufgerufen wird, wenn ein Datum ausgewählt wurde.
 */
class DatePickerFragment(val listener: (day: Int, month: Int, year: Int) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        listener(day, month, year)
    }
}
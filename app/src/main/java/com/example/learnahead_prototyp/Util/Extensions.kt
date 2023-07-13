package com.example.learnahead_prototyp.Util

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.provider.OpenableColumns
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Reader

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

fun View.showKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    requestFocus()
}

fun View.hideKeyboard() =
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)



suspend fun Uri.getName(context: Context): String {
    var fileName: String? = null
    try {
        if ("content" == scheme) {
            withContext(Dispatchers.IO) {
                context.contentResolver.query(
                    this@getName,
                    null,
                    null,
                    null,
                    null
                )?.use {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    it.moveToFirst()
                    fileName = it.getString(nameIndex)
                }
            }
        } else if ("file" == scheme) {
            fileName = lastPathSegment
        }
    } catch (ignored: Exception) {
        ignored.printStackTrace()
    }
    return fileName ?: "Untitled.md"
}

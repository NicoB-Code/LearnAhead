package com.example.learnahead_prototyp.UI.Markdown


import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicBoolean
import android.net.Uri
import android.preference.PreferenceManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.Dispatchers
import java.io.FileInputStream
import java.io.Reader
import java.io.File


const val PREF_KEY_AUTOSAVE_URI = "autosave.uri"


class MarkdownViewModel(val timber: Timber.Tree = Timber.asTree()) : ViewModel(){
    val fileName = MutableLiveData<String?>("Untitled.md")
    val markdownUpdates = MutableLiveData<String>()
    val editorActions = MutableLiveData<EditorAction>()
    val uri = MutableLiveData<Uri?>()
    private val isDirty = AtomicBoolean(false)
    private val saveMutex = Mutex()


    fun updateMarkdown(markdown: String?) {
        this.markdownUpdates.postValue(markdown ?: "")
        isDirty.set(true)
    }

    suspend fun load(
        context: Context,
        uri: Uri?,
        sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    ): Boolean {
        if (uri == null) {
            timber.i("No URI provided to load, attempting to load last autosaved file")
            sharedPrefs.getString(PREF_KEY_AUTOSAVE_URI, null)
                ?.let {
                    Timber.d("Using uri from shared preferences: $it")
                    return load(context, Uri.parse(it), sharedPrefs)
                } ?: return false
        }
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openFileDescriptor(uri, "r")?.use {
                    val fileInput = FileInputStream(it.fileDescriptor)
                    val fileName = uri.getName(context)
                    val content = fileInput.reader().use(Reader::readText)
                    if (content.isBlank()) {
                        // If we don't get anything back, then we can assume that reading the file failed
                        timber.i("Ignoring load for empty file $fileName from $fileInput")
                        return@withContext false
                    }
                    editorActions.postValue(EditorAction.Load(content))
                    markdownUpdates.postValue(content)
                    this@MarkdownViewModel.fileName.postValue(fileName)
                    this@MarkdownViewModel.uri.postValue(uri)
                    timber.i("Loaded file $fileName from $fileInput")
                    timber.v("File contents:\n$content")
                    isDirty.set(false)
                    timber.i("Persisting autosave uri in shared prefs: $uri")
                    sharedPrefs.edit()
                        .putString(PREF_KEY_AUTOSAVE_URI, uri.toString())
                        .apply()
                    true
                } ?: run {
                    timber.w("Open file descriptor returned null for uri: $uri")
                    false
                }
            } catch (e: Exception) {
                timber.e(e, "Failed to open file descriptor for uri: $uri")
                false
            }
        }
    }
    sealed class EditorAction {
        val consumed = AtomicBoolean(false)

        data class Load(val markdown: String) : EditorAction()
    }
}
package com.example.learnahead_prototyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val TAG = "DocSnippets"
    lateinit var mUsername: String
    lateinit var mPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        val anmeldeButton: Button = findViewById(R.id.anmeldenButton)
        anmeldeButton.setOnClickListener{
            val userInput: EditText = findViewById(R.id.benutzer_e_)
            val passInput: EditText = findViewById(R.id.passwort)
            // we get the input for the login which needs to be checked
            mUsername = userInput.text.toString()
            mPassword = passInput.text.toString()

            myViewModel.validateUserCredentials(mUsername, mPassword) { isValid ->
                if (isValid) {
                    Log.d("MainActivity","Authentification successful")
                } else {
                    Log.d("MainActivity","Authentification failed")
                }
            }
        }*/
    }
}

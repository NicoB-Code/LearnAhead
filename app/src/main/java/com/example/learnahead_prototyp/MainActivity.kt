package com.example.learnahead_prototyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        val myTextView: TextView = findViewById(R.id.myTextView)
        myTextView.text = myViewModel.myModel.myData

        val myButton: Button = findViewById(R.id.myButton)
        myButton.setOnClickListener {
            myViewModel.updateMyData("New Data")
            myTextView.text = myViewModel.myModel.myData
            setContentView(R.layout.login)
        }
    }
}

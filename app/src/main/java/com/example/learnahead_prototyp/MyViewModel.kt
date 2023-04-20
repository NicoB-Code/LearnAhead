package com.example.learnahead_prototyp

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    public var myModel = MyModel()

    fun updateMyData(newData: String) {
        myModel.myData = newData
    }
    fun validateUserCredentials(username: String, password: String, onComplete: (Boolean) -> Unit) {
        myModel.checkUserCredentials(username, password, onComplete)
    }
}
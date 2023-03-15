package com.example.learnahead_prototyp

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    public var myModel = MyModel()

    fun updateMyData(newData: String) {
        myModel.myData = newData
    }
}
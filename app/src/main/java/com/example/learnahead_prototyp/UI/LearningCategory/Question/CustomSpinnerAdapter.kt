package com.example.learnahead_prototyp.UI.LearningCategory.Question

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.learnahead_prototyp.R

class CustomSpinnerAdapter(context: Context, resource: Int, items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false)

        val text = view.findViewById<TextView>(R.id.dropdown_text)
        val icon = view.findViewById<ImageView>(R.id.dropdown_icon)

        text.text = getItem(position)
        icon.setImageResource(R.drawable.arrow_down_icon)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false)

        val text = view.findViewById<TextView>(R.id.dropdown_text)
        val icon = view.findViewById<ImageView>(R.id.dropdown_icon)

        text.text = getItem(position)
        icon.setImageResource(R.drawable.arrow_down_icon)

        return view
    }
}




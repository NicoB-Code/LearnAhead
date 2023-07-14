package com.example.learnahead_prototyp.UI.LearningCategory.Question

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.learnahead_prototyp.R

/**
 * Ein benutzerdefinierter Spinner-Adapter, der ein Dropdown-Menü für eine Liste von Strings in Android erstellt.
 *
 * @param context Der Kontext der Anwendung.
 * @param resource Die ID der Layout-Ressource für jedes Element im Dropdown-Menü.
 * @param items Die Liste von Strings, die im Dropdown-Menü angezeigt werden sollen.
 */
class CustomSpinnerAdapter(context: Context, resource: Int, items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

    private val layoutInflater = LayoutInflater.from(context)

    /**
     * Gibt die View zurück, die das ausgewählte Element im Spinner darstellt.
     *
     * @param position Die Position des ausgewählten Elements.
     * @param convertView Die konvertierte View, die wiederverwendet werden kann.
     * @param parent Die übergeordnete Ansicht, zu der die View hinzugefügt werden soll.
     * @return Die View, die das ausgewählte Element darstellt.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false)

        val text = view.findViewById<TextView>(R.id.dropdown_text)
        val icon = view.findViewById<ImageView>(R.id.dropdown_icon)

        text.text = getItem(position)
        icon.setImageResource(R.drawable.arrow_down_icon)

        return view
    }

    /**
     * Gibt die View zurück, die jedes Element im Dropdown-Menü darstellt.
     *
     * @param position Die Position des Elements im Dropdown-Menü.
     * @param convertView Die konvertierte View, die wiederverwendet werden kann.
     * @param parent Die übergeordnete Ansicht, zu der die View hinzugefügt werden soll.
     * @return Die View, die das Element im Dropdown-Menü darstellt.
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false)

        val text = view.findViewById<TextView>(R.id.dropdown_text)
        val icon = view.findViewById<ImageView>(R.id.dropdown_icon)

        text.text = getItem(position)
        icon.setImageResource(R.drawable.arrow_down_icon)

        return view
    }
}

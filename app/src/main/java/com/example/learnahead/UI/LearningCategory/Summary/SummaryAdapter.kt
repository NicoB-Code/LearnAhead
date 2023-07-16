package com.example.learnahead.UI.LearningCategory.Summary

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead.Data.Model.Summary
import com.example.learnahead.databinding.InnerViewSummaryBinding

/**
 * Ein Adapter zur Darstellung von Zusammenfassungen.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Zusammenfassung geklickt wird.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Löschen-Schaltfläche einer Zusammenfassung geklickt wird.
 */
class SummaryAdapter(
    val onItemClicked: (Summary) -> Unit,
    val onDeleteClicked: (Int, Summary) -> Unit
) : RecyclerView.Adapter<SummaryAdapter.MyViewHolder>() {

    private var list: MutableList<Summary> = arrayListOf()
    private lateinit var resources: Resources

    /**
     * Erstellt eine neue Instanz von MyViewHolder.
     * Erstellt das Binding-Objekt für das RecyclerView-Element.
     * @param parent Die Eltern-ViewGroup, in der das View-Objekt angezeigt wird.
     * @param viewType Der Typ des angezeigten Views.
     * @return Die erstellte Instanz von MyViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = InnerViewSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        resources = parent.context.resources // Ressourcen aus dem übergeordneten Kontext abrufen
        return MyViewHolder(binding)
    }

    /**
     * Bindet die Daten eines [Summary]-Objekts an das RecyclerView-Element.
     * Ruft die bind-Methode von MyViewHolder auf und übergibt das [Summary]-Objekt.
     * @param holder Die Instanz von MyViewHolder, die das RecyclerView-Element repräsentiert.
     * @param position Die Position des Elements in der Liste.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        Log.d("SummaryAdapter", "Bound")
    }

    /**
     * Aktualisiert die Liste der Zusammenfassungen mit den übergebenen Daten.
     * Benachrichtigt den Adapter, dass sich die Daten geändert haben und die Ansicht aktualisiert werden muss.
     * @param list Die Liste der Zusammenfassungen.
     */
    fun updateList(list: MutableList<Summary>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Gibt die Anzahl der Elemente in der Liste zurück.
     * @return Die Anzahl der Elemente in der Liste.
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Eine innere Klasse, die als ViewHolder für das RecyclerView-Element dient.
     * @param binding Das Binding-Objekt, das die View-Elemente enthält.
     */
    inner class MyViewHolder(private val binding: InnerViewSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten eines [Summary]-Objekts an die View-Elemente.
         * Setzt den Text des TextViews auf den Namen der Zusammenfassung.
         * Setzt den Klicklistener für das Layout der inneren Zusammenfassungsansicht.
         * Ruft die onItemClicked-Funktion mit der Position und dem [Summary]-Objekt auf.
         * Setzt den Klicklistener für die Löschen-Schaltfläche der Zusammenfassung.
         * Ruft die onDeleteClicked-Funktion mit der Position und dem [Summary]-Objekt auf.
         * @param item Das [Summary]-Objekt, das an die View gebunden wird.
         */
        fun bind(item: Summary) {
            binding.textSummaryName.text = item.name

            // Setzt den Klicklistener für das Layout der inneren Zusammenfassungsansicht
            binding.innerViewSummaryLayout.setOnClickListener {
                onItemClicked.invoke(item)
            }

            // Setzt den Klicklistener für die Löschen-Schaltfläche der Zusammenfassung
            binding.deleteIcon.setOnClickListener {
                onDeleteClicked.invoke(bindingAdapterPosition, item)
            }
        }
    }
}

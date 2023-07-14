package com.example.learnahead_prototyp.UI.LearningCategory

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.databinding.FragmentLearningCategoryInnerViewBinding

/**
 * Ein Adapter zur Darstellung von Zielen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Lernkategorie geklickt wird.
 *                      Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 */
class LearningCategoryInnerViewAdapter(
    private val onItemClicked: (Int, Summary) -> Unit
) : RecyclerView.Adapter<LearningCategoryInnerViewAdapter.MyViewHolder>() {

    private var list: MutableList<Summary> = mutableListOf()
    private lateinit var resources: Resources

    /**
     * Erstellt einen ViewHolder, der das Layout für ein Ziel repräsentiert.
     *
     * @param parent Die übergeordnete Ansichtsgruppe.
     * @param viewType Der Typ des View-Typs.
     * @return Der erstellte ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = FragmentLearningCategoryInnerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        resources = parent.context.resources
        return MyViewHolder(binding)
    }

    /**
     * Bindet die Daten eines Ziels an einen ViewHolder.
     *
     * @param holder Der ViewHolder, an den die Daten gebunden werden sollen.
     * @param position Die Position des Ziels in der Liste.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (list.isNotEmpty()) {
            val item = list[position]
            holder.bind(item)
        }
    }

    /**
     * Aktualisiert die Liste der Ziele.
     *
     * @param list Die neue Liste der Ziele.
     */
    fun updateList(list: List<Summary>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Gibt die Anzahl der Ziele in der Liste zurück.
     *
     * @return Die Anzahl der Ziele.
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * ViewHolder-Klasse, die das Layout für ein Ziel repräsentiert.
     *
     * @param binding Das Binding-Objekt für das Layout.
     */
    inner class MyViewHolder(private val binding: FragmentLearningCategoryInnerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten eines [Summary]-Objekts an die Ansicht.
         *
         * @param item Das [Summary]-Objekt, das an die Ansicht gebunden werden soll.
         */
        fun bind(item: Summary) {
            // Die Daten an die Ansicht binden
            // Beispiel: binding.textViewTitle.text = item.title

            // Einen Klicklistener für das Element festlegen
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val summary = list[position]
                    onItemClicked(position, summary)
                }
            }
        }
    }
}

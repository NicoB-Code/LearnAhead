package com.example.learnahead.UI.LearningCategory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.databinding.ItemCategoryLayoutBinding

/**
 * Ein Adapter zur Darstellung von Lernkategorien in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Lernkategorie geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Löschen" einer Lernkategorie geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 */
class LearningCategoryListingAdapter(
    private val onItemClicked: (LearningCategory) -> Unit,
    private val onDeleteClicked: (Int, LearningCategory) -> Unit
) : RecyclerView.Adapter<LearningCategoryListingAdapter.MyViewHolder>() {

    /**
     * Die Liste der Lernkategorien, die in der RecyclerView angezeigt werden soll.
     */
    private var list: MutableList<LearningCategory> = mutableListOf()

    /**
     * Erstellt einen neuen ViewHolder für jedes Element in der RecyclerView.
     *
     * @param parent Die übergeordnete Ansicht, zu der die neue Ansicht hinzugefügt werden soll.
     * @param viewType Der Typ der Ansicht.
     * @return Der erstellte ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemCategoryLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    /**
     * Bindet die Daten an den ViewHolder.
     *
     * @param holder Der ViewHolder, an den die Daten gebunden werden sollen.
     * @param position Der Index der Lernkategorie in der Liste.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    /**
     * Gibt die Anzahl der Elemente in der Liste zurück.
     *
     * @return Die Anzahl der Elemente in der Liste.
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * Aktualisiert die Liste der Lernkategorien und benachrichtigt den Adapter über die Änderungen.
     *
     * @param list Die neue Liste der Lernkategorien.
     */
    fun updateList(list: MutableList<LearningCategory>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Entfernt die Lernkategorie an der angegebenen Position aus der Liste und benachrichtigt den Adapter über die Änderungen.
     *
     * @param position Die Position der zu entfernenden Lernkategorie.
     */
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * ViewHolder-Klasse für jedes einzelne Element in der RecyclerView.
     *
     * @param binding Das Binding-Objekt für das Layoutelement.
     */
    inner class MyViewHolder(private val binding: ItemCategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten der Lernkategorie an die Ansichtselemente des ViewHolders.
         *
         * @param item Die Lernkategorie, die an die Ansichtselemente gebunden werden soll.
         */
        fun bind(item: LearningCategory) {
            binding.cardViewTextLearningCategoryName.text = item.name
            binding.delete.setOnClickListener {
                onDeleteClicked.invoke(bindingAdapterPosition, item)
            }
            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(item)
            }
        }
    }
}

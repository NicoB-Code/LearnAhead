package com.example.learnahead_prototyp.UI.LearningCategory.Test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Test
import com.example.learnahead_prototyp.databinding.ItemTestLayoutBinding
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Ein Adapter zur Darstellung von Zielen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Lernkategorie geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 * @param onEditClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Bearbeiten" einer Lernkategorie geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Löschen" einer geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 */
class TestListingAdapter(
    val onItemClicked: (Int, Test) -> Unit,
    val onDeleteClicked: (Int, Test) -> Unit
) : RecyclerView.Adapter<TestListingAdapter.MyViewHolder>() {

    /**
     * Eine Liste von Lernkategorien, die in der RecyclerView angezeigt werden soll.
     */
    private var list: MutableList<Test> = arrayListOf()

    /**
     * Erstellt eine neue View für jede Lernkategorie in der Liste.
     * @param parent Die übergeordnete Ansicht, zu der die neue Ansicht hinzugefügt werden soll.
     * @param viewType Der Typ der Ansicht.
     * @return Eine neue Instanz von [MyViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemTestLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    /**
     * Bindet die Daten an die View in [holder].
     * @param holder Der [MyViewHolder], an den die Daten gebunden werden sollen.
     * @param position Der Index der Lernkategorie in der Liste.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    /**
     * Aktualisiert die Liste von Lernkategorien und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     * @param list Die neue Liste von Lernkategorien.
     */
    fun updateList(list: MutableList<Test>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Entfernt die Lernkategorie an der angegebenen Position aus der Liste und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     * @param position Der Index der Lernkategorie, die entfernt werden soll.
     */
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    /**
     * Gibt die Anzahl der Elemente in der Liste zurück.
     * @return Anzahl der Elemente in der Liste.
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * ViewHolder-Klasse für jedes einzelne Element in der RecyclerView.
     * @param binding Verweis auf das Layoutbinding für das Element.
     */
    inner class MyViewHolder(val binding: ItemTestLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten des übergebenen LearningCategory-Objekts an die Ansichtselemente des ViewHolders.
         * @param item Das LearningCategory-Objekt, das an die Ansichtselemente gebunden werden soll.
         */
        fun bind(item: Test) {
            binding.test.text = item.name

            val lastModifiedDate = item.modifiedDate // Assuming item.modifiedDate is of type Date
            val currentDate = Date() // Current date

            val diffInMillis = currentDate.time - lastModifiedDate.time
            val diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)

            val text = when {
                diffInDays == 0L -> "heute"
                diffInDays == 1L -> "gestern"
                else -> "vor $diffInDays Tagen"
            }

            val displayText = "zuletzt aktualisiert $text"
            binding.modifiedDate.text = displayText

            binding.delete.setOnClickListener {
                onDeleteClicked.invoke(
                    bindingAdapterPosition,
                    item
                )
            }
            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(
                    bindingAdapterPosition,
                    item
                )
            }
        }
    }
}
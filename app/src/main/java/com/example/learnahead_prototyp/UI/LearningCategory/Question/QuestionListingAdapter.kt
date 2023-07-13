package com.example.learnahead_prototyp.UI.LearningCategory.Question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.databinding.ItemQuestionLayoutBinding

/**
 * Ein Adapter zur Darstellung von Zielen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Lernkategorie geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 * @param onEditClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Bearbeiten" einer Lernkategorie geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Löschen" einer geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 */
class QuestionListingAdapter(
    val onItemClicked: (Int, Question) -> Unit,
    val onDeleteClicked: (Int, Question) -> Unit
) : RecyclerView.Adapter<QuestionListingAdapter.MyViewHolder>() {

    /**
     * Eine Liste von Lernkategorien, die in der RecyclerView angezeigt werden soll.
     */
    private var list: MutableList<Question> = arrayListOf()

    /**
     * Erstellt eine neue View für jede Lernkategorie in der Liste.
     * @param parent Die übergeordnete Ansicht, zu der die neue Ansicht hinzugefügt werden soll.
     * @param viewType Der Typ der Ansicht.
     * @return Eine neue Instanz von [MyViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemQuestionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    fun updateList(list: MutableList<Question>) {
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
    inner class MyViewHolder(val binding: ItemQuestionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten des übergebenen LearningCategory-Objekts an die Ansichtselemente des ViewHolders.
         * @param item Das LearningCategory-Objekt, das an die Ansichtselemente gebunden werden soll.
         */
        fun bind(item: Question) {
            binding.tags.text = item.tags.joinToString(" | ") { it.name }
            binding.question.text = item.question
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
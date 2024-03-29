package com.example.learnahead.UI.LearningCategory.Question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead.Data.Model.Question
import com.example.learnahead.databinding.ItemQuestionLayoutBinding

/**
 * Ein Adapter zur Darstellung von Fragen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Frage geklickt wird. Der Index der Frage in der Liste und die Frage selbst werden übergeben.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Löschen" einer Frage geklickt wird. Der Index der Frage in der Liste und die Frage selbst werden übergeben.
 */
class QuestionListingAdapter(
    val onDeleteClicked: (Int, Question) -> Unit,
    val onEditClicked: (Question) -> Unit
) : RecyclerView.Adapter<QuestionListingAdapter.MyViewHolder>() {

    /**
     * Eine Liste von Fragen, die in der RecyclerView angezeigt werden soll.
     */
    private var list: MutableList<Question> = arrayListOf()

    /**
     * Indicates whether the adapter is in edit mode or not.
     */
    var isEditMode: Boolean = true // Set to true by default, change it based on your requirements


    /**
     * Erstellt eine neue View für jede Frage in der Liste.
     *
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
     *
     * @param holder Der [MyViewHolder], an den die Daten gebunden werden sollen.
     * @param position Der Index der Frage in der Liste.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        // Control the visibility of the edit icon based on the edit mode
        if (isEditMode) {
            holder.binding.edit.visibility = View.VISIBLE
        } else {
            holder.binding.edit.visibility = View.GONE
        }
    }

    /**
     * Aktualisiert die Liste von Fragen und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     *
     * @param list Die neue Liste von Fragen.
     */
    fun updateList(list: MutableList<Question>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Entfernt die Frage an der angegebenen Position aus der Liste und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     *
     * @param position Der Index der Frage, die entfernt werden soll.
     */
    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    /**
     * Gibt die Anzahl der Elemente in der Liste zurück.
     *
     * @return Anzahl der Elemente in der Liste.
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * ViewHolder-Klasse für jedes einzelne Element in der RecyclerView.
     *
     * @param binding Verweis auf das Layoutbinding für das Element.
     */
    inner class MyViewHolder(val binding: ItemQuestionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten des übergebenen [Question]-Objekts an die Ansichtselemente des ViewHolders.
         *
         * @param item Das [Question]-Objekt, das an die Ansichtselemente gebunden werden soll.
         */
        fun bind(item: Question) {
            binding.textTags.text = item.tags.joinToString(" | ") { it.name }
            binding.textShownQuestion.text = item.question
            binding.deleteIcon.setOnClickListener {
                onDeleteClicked.invoke(
                    bindingAdapterPosition,
                    item
                )
            }
            binding.edit.setOnClickListener {
                onEditClicked.invoke(
                    item
                )
            }
        }
    }
}
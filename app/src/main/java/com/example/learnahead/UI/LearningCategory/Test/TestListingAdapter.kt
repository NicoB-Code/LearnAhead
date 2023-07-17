package com.example.learnahead.UI.LearningCategory.Test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead.Data.Model.Test
import com.example.learnahead.databinding.ItemTestLayoutBinding
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Ein Adapter zur Darstellung von Tests in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf einen Test geklickt wird. Der Index des Tests in der Liste und der Test selbst werden übergeben.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Löschen" eines Tests geklickt wird. Der Index des Tests in der Liste und der Test selbst werden übergeben.
 */
class TestListingAdapter(
    val onItemClicked: (Test) -> Unit,
    val onDeleteClicked: (Int, Test) -> Unit,
    val onEditClicked:(Test) -> Unit
) : RecyclerView.Adapter<TestListingAdapter.MyViewHolder>() {

    /**
     * Eine Liste von Tests, die in der RecyclerView angezeigt werden soll.
     */
    private var testList: MutableList<Test> = arrayListOf()

    /**
     * Erstellt eine neue View für jeden Test in der Liste.
     *
     * @param parent Die übergeordnete Ansicht, zu der die neue Ansicht hinzugefügt werden soll.
     * @param viewType Der Typ der Ansicht.
     * @return Eine neue Instanz von [MyViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemTestLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    /**
     * Bindet die Daten an die View in [holder].
     *
     * @param holder Der [MyViewHolder], an den die Daten gebunden werden sollen.
     * @param position Der Index des Tests in der Liste.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val test = testList[position]
        holder.bind(test)
    }

    /**
     * Aktualisiert die Liste von Tests und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     *
     * @param list Die neue Liste von Tests.
     */
    fun updateList(list: MutableList<Test>) {
        testList = list
        notifyDataSetChanged()
    }

    /**
     * Entfernt den Test an der angegebenen Position aus der Liste und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     *
     * @param position Der Index des Tests, der entfernt werden soll.
     */
    fun removeItem(position: Int) {
        testList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Gibt die Anzahl der Elemente in der Liste zurück.
     *
     * @return Anzahl der Elemente in der Liste.
     */
    override fun getItemCount(): Int {
        return testList.size
    }

    /**
     * ViewHolder-Klasse für jedes einzelne Element in der RecyclerView.
     *
     * @param binding Verweis auf das Layoutbinding für das Element.
     */
    inner class MyViewHolder(private val binding: ItemTestLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten des übergebenen Test-Objekts an die Ansichtselemente des ViewHolders.
         *
         * @param test Das Test-Objekt, das an die Ansichtselemente gebunden werden soll.
         */
        fun bind(test: Test) {
            binding.test.text = test.name

            val lastModifiedDate = test.modifiedDate
            val currentDate = Date()

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
                onDeleteClicked.invoke(bindingAdapterPosition, test)
            }

            binding.edit.setOnClickListener{
                onEditClicked.invoke(test)
            }

            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(test)
            }
        }
    }
}

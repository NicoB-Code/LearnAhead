package com.example.learnahead_prototyp.UI.Goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.databinding.ItemGoalLayoutBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


/**
 * Ein Adapter zur Darstellung von Zielen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf ein Ziel geklickt wird. Der Index des Ziels in der Liste und das Ziel selbst werden übergeben.
 * @param onEditClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Bearbeiten" eines Ziels geklickt wird. Der Index des Ziels in der Liste und das Ziel selbst werden übergeben.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Löschen" eines Ziels geklickt wird. Der Index des Ziels in der Liste und das Ziel selbst werden übergeben.
 */
class GoalListingAdapter(
    val onItemClicked: (Int, Goal) -> Unit,
    val onEditClicked: (Int, Goal) -> Unit,
    val onDeleteClicked: (Int, Goal) -> Unit
) : RecyclerView.Adapter<GoalListingAdapter.MyViewHolder>() {

    /**
     * Eine Liste von Zielen, die in der RecyclerView angezeigt werden soll.
     */
    private var list: MutableList<Goal> = arrayListOf()

    /**
     * Erstellt eine neue View für jedes Ziel in der Liste.
     * @param parent Die übergeordnete Ansicht, zu der die neue Ansicht hinzugefügt werden soll.
     * @param viewType Der Typ der Ansicht.
     * @return Eine neue Instanz von [MyViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemGoalLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    /**
     * Bindet die Daten an die View in [holder].
     * @param holder Der [MyViewHolder], an den die Daten gebunden werden sollen.
     * @param position Der Index des Ziel in der Liste.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    /**
     * Aktualisiert die Liste von Zielen und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     * @param list Die neue Liste von Zielen.
     */
    fun updateList(list: MutableList<Goal>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Entfernt das Ziel an der angegebenen Position aus der Liste und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     * @param position Der Index des Ziels, das entfernt werden soll.
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
    inner class MyViewHolder(val binding: ItemGoalLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten des übergebenen Goal-Objekts an die Ansichtselemente des ViewHolders.
         * @param goal Das Goal-Objekt, das an die Ansichtselemente gebunden werden soll.
         */
        fun bind(goal: Goal) {
            binding.cardViewTextGoalTitle.text = goal.title

            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            binding.cardViewGoalDate.text = dateFormat.format(goal.startDate) + " - " + dateFormat.format(goal.endDate)

            // calculate the number of days between the two dates
            val daysBetweenDates = getDaysBetweenDates(goal.startDate, goal.endDate)
            binding.cardViewDateDaysCalculated.text = "$daysBetweenDates Tage"
            binding.delete.setOnClickListener {
                onDeleteClicked.invoke(
                    bindingAdapterPosition,
                    goal
                )
            }
            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(
                    bindingAdapterPosition,
                    goal
                )
            }
        }

        private fun getDaysBetweenDates(startDate: Date, endDate: Date): Int {
            val startCalendar = Calendar.getInstance().apply { time = startDate }
            val endCalendar = Calendar.getInstance().apply { time = endDate }
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)
            startCalendar.set(Calendar.MILLISECOND, 0)
            endCalendar.set(Calendar.HOUR_OF_DAY, 0)
            endCalendar.set(Calendar.MINUTE, 0)
            endCalendar.set(Calendar.SECOND, 0)
            endCalendar.set(Calendar.MILLISECOND, 0)
            val diff = endCalendar.timeInMillis - startCalendar.timeInMillis
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
        }
    }
}
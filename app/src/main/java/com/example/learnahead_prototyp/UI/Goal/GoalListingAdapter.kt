package com.example.learnahead_prototyp.UI.Goal

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.databinding.ItemGoalLayoutBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Ein Adapter zur Darstellung von Zielen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf ein Ziel geklickt wird. Der Index des Ziels in der Liste und das Ziel selbst werden übergeben.
 * @param onEditClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Bearbeiten" eines Ziels geklickt wird. Der Index des Ziels in der Liste und das Ziel selbst werden übergeben.
 * @param onDeleteClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf die Schaltfläche "Löschen" eines Ziels geklickt wird. Der Index des Ziels in der Liste und das Ziel selbst werden übergeben.
 */
class GoalListingAdapter(
    private val onItemClicked: (Int, Goal) -> Unit,
    private val onEditClicked: (Int, Goal) -> Unit,
    private val onDeleteClicked: (Int, Goal) -> Unit
) : RecyclerView.Adapter<GoalListingAdapter.MyViewHolder>() {

    /**
     * Eine Liste von Zielen, die in der RecyclerView angezeigt werden soll.
     */
    private var goalList: MutableList<Goal> = mutableListOf()

    /**
     * Erstellt eine neue View für jedes Ziel in der Liste.
     * @param parent Die übergeordnete Ansicht, zu der die neue Ansicht hinzugefügt werden soll.
     * @param viewType Der Typ der Ansicht.
     * @return Eine neue Instanz von [MyViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemGoalLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    /**
     * Bindet die Daten an die View in [holder].
     * @param holder Der [MyViewHolder], an den die Daten gebunden werden sollen.
     * @param position Der Index des Ziels in der Liste.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val goal = goalList[position]
        holder.bind(goal)
    }

    /**
     * Aktualisiert die Liste von Zielen und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     * @param list Die neue Liste von Zielen.
     */
    fun updateList(list: List<Goal>) {
        goalList.clear()
        goalList.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Entfernt das Ziel an der angegebenen Position aus der Liste und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     * @param position Der Index des Ziels, das entfernt werden soll.
     */
    fun removeItem(position: Int) {
        goalList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Gibt die Anzahl der Elemente in der Liste zurück.
     * @return Anzahl der Elemente in der Liste.
     */
    override fun getItemCount(): Int {
        return goalList.size
    }

    /**
     * ViewHolder-Klasse für jedes einzelne Element in der RecyclerView.
     * @param binding Verweis auf das Layoutbinding für das Element.
     */
    inner class MyViewHolder(private val binding: ItemGoalLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bindet die Daten des übergebenen Goal-Objekts an die Ansichtselemente des ViewHolders.
         * @param goal Das Goal-Objekt, das an die Ansichtselemente gebunden werden soll.
         */
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(goal: Goal) {
            binding.cardViewTextGoalName.text = goal.name

            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            binding.cardViewGoalDate.text =
                "${dateFormat.format(goal.startDate)} - ${dateFormat.format(goal.endDate)}"

            // Get the current date (today)
            val currentDate = LocalDate.now()
            // Parse the formatted endDate string back to a LocalDate object
            val newEndDate = dateFormat.parse(dateFormat.format(goal.endDate))
            val endDate = newEndDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            // Calculate the number of days between the current date and the endDate
            val daysBetweenDates = ChronoUnit.DAYS.between(currentDate, endDate).toInt()

            val daysText = if (daysBetweenDates == 1) "Tag" else "Tage"

            if (daysBetweenDates > 0)
                binding.cardViewDateDaysCalculated.text = "endet in $daysBetweenDates $daysText"
            else
                binding.cardViewDateDaysCalculated.text = "Lernziel abgelaufen"

            binding.delete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClicked.invoke(position, goal)
                }
            }

            binding.itemLayout.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked.invoke(position, goal)
                }
            }
        }
    }
}

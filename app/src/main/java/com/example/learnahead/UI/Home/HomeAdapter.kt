package com.example.learnahead.UI.Home

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.databinding.ItemTodaysGoalLayoutBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit


/**
 * Ein Adapter zur Darstellung von Zielen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf ein Ziel geklickt wird. Der Index des Ziels in der Liste und das Ziel selbst werden übergeben.
 */
class HomeAdapter(
    val onItemClicked: (LearningCategory) -> Unit,
) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    /**
     * Eine Liste von Zielen, die in der RecyclerView angezeigt werden soll.
     */
    private var list: MutableList<LearningCategory> = mutableListOf()

    /**
     * Erstellt eine neue View für jedes Ziel in der Liste.
     * @param parent Die übergeordnete Ansicht, zu der die neue Ansicht hinzugefügt werden soll.
     * @param viewType Der Typ der Ansicht.
     * @return Eine neue Instanz von [MyViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemTodaysGoalLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    /**
     * Bindet die Daten an die View in [holder].
     * @param holder Der [MyViewHolder], an den die Daten gebunden werden sollen.
     * @param position Der Index des Ziel in der Liste.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    /**
     * Aktualisiert die Liste von Zielen und benachrichtigt den Adapter, dass sich die Daten geändert haben.
     * @param list Die neue Liste von Zielen.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<LearningCategory>) {
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
    inner class MyViewHolder(val binding: ItemTodaysGoalLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(learningCategory: LearningCategory) {
            if (learningCategory.relatedLearningGoal != null) {
                val goal = learningCategory.relatedLearningGoal

                binding.textTodaysGoalsName.text = learningCategory.name

                val dateFormat = SimpleDateFormat("dd.MM.yyyy")
                val startDate = goal?.startDate
                val endDate = goal?.endDate

                if (endDate != null) {
                    binding.textLearningGoalEndDate.text = "Ziel Datum: " + dateFormat.format(endDate)

                    val currentDate = LocalDate.now()
                    val newEndDate = dateFormat.parse(dateFormat.format(endDate))
                    val endDateLocalDate =
                        newEndDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

                    if (endDateLocalDate != null) {
                        val daysBetweenDates =
                            ChronoUnit.DAYS.between(currentDate, endDateLocalDate).toInt()

                        if (daysBetweenDates == 0) {
                            binding.textLearningGoalLeftDaysCalculated.text = "endet heute"
                            binding.continueOrStartLearningGoalButton.text = "Beginnen"
                        } else if (daysBetweenDates < 0) {
                            binding.textLearningGoalLeftDaysCalculated.text = "Lernziel abgelaufen"
                            binding.continueOrStartLearningGoalButton.text = "Weiter machen"
                        } else {
                            binding.textLearningGoalLeftDaysCalculated.text =
                                "endet in $daysBetweenDates Tag(en)"

                            if (startDate != null) {
                                val startLocalDate = startDate.toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate()

                                if (startLocalDate == currentDate) {
                                    binding.continueOrStartLearningGoalButton.text = "Beginnen"
                                } else {
                                    binding.continueOrStartLearningGoalButton.text = "Weiter machen"
                                }
                            } else {
                                binding.continueOrStartLearningGoalButton.text = "Weiter machen"
                            }
                        }
                    }
                }

                binding.continueOrStartLearningGoalButton.setOnClickListener {
                    onItemClicked.invoke(
                        learningCategory
                    )
                }
            }
        }
    }
}

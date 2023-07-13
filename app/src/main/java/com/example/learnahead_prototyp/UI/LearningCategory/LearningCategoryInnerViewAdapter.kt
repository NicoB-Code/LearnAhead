package com.example.learnahead_prototyp.UI.LearningCategory

import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.R
import com.example.learnahead_prototyp.databinding.LearningCategoryInnerViewSummaryBinding

/**
 * Ein Adapter zur Darstellung von Zielen in einer RecyclerView.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Lernkategorie geklickt wird. Der Index der Lernkategorie in der Liste und die Lernkategorie selbst werden übergeben.
 */
class LearningCategoryInnerViewAdapter(
    val onItemClicked: (Int, Summary) -> Unit
) : RecyclerView.Adapter<LearningCategoryInnerViewAdapter.MyViewHolder>() {

    private var list: MutableList<Summary> = arrayListOf()
    private lateinit var resources: Resources

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LearningCategoryInnerViewSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        resources = parent.context.resources // Get resources from the parent context
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (list.isNotEmpty()) {
            val item = list[position]
            holder.bind(item)
        }
    }

    fun updateList(list: MutableList<Summary>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return 1 // Return 1 to indicate a single view
    }

    inner class MyViewHolder(private val binding: LearningCategoryInnerViewSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the data of a [Summary] item to the view.
         *
         * @param item The [Summary] object to bind.
         */
        fun bind(item: Summary) {
            val maxItems = 3 // Maximum number of items to display
            val remainingCount = list.size - maxItems

            // Create a list of formatted items, including the index and name
            val formattedItems = list.take(maxItems).mapIndexed { index, it ->
                "${index + 1}. ${it.name}"
            }.toMutableList()

            // Add the remaining count text if there are more items than the maximum
            if (remainingCount > 0) {
                val remainingText = resources.getString(R.string.remaining_summary_items, remainingCount)
                formattedItems.add(remainingText)
            }

            val itemListBuilder = SpannableStringBuilder()
            val blackSpan = ForegroundColorSpan(Color.BLACK)
            val greySpan = ForegroundColorSpan(ContextCompat.getColor(binding.itemListText.context, R.color.special_grey))

            // Iterate through the formatted items and apply the appropriate text color span
            for ((index, formattedItem) in formattedItems.withIndex()) {
                val spannableItem = SpannableString(formattedItem)
                val textColorSpan = if (index < maxItems) blackSpan else greySpan
                spannableItem.setSpan(textColorSpan, 0, formattedItem.length, 0)
                itemListBuilder.append(spannableItem)
                itemListBuilder.append("\n")
            }

            // Set the formatted item list to the item list text view
            binding.itemListText.text = itemListBuilder

            // Set the click listener for the inner view summary layout
            binding.innerViewSummaryLayout.setOnClickListener {
                onItemClicked.invoke(bindingAdapterPosition, item)
            }
        }

    }
}

package com.example.learnahead_prototyp.UI.LearningCategory.Summary

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.databinding.InnerViewSummaryBinding

/**
 * Ein Adapter zur Darstellung von Zusammenfassungen.
 *
 * @param onItemClicked Eine Lambda-Funktion, die aufgerufen wird, wenn auf eine Zusammenfassung geklickt wird.
 */
class SummaryAdapter(
    val onItemClicked: (Int, Summary) -> Unit,
    val onDeleteClicked: (Int, Summary) ->Unit
) : RecyclerView.Adapter<SummaryAdapter.MyViewHolder>() {

    private var list: MutableList<Summary> = arrayListOf()
    private lateinit var resources: Resources

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = InnerViewSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        resources = parent.context.resources // Get resources from the parent context
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = list[position]
            holder.bind(item)
            Log.d("SummaryAdapter", "Bound")
    }

    fun updateList(list: MutableList<Summary>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: InnerViewSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the data of a [Summary] item to the view.
         *
         * @param item The [Summary] object to bind.
         */
        fun bind(item: Summary) {
            binding.itemListText.text = item.name

            // Set the click listener for the inner view summary layout
            binding.innerViewSummaryLayout.setOnClickListener {
                onItemClicked.invoke(bindingAdapterPosition, item)
            }
            binding.delete.setOnClickListener {
                onDeleteClicked.invoke(bindingAdapterPosition, item)
            }
        }

    }
}
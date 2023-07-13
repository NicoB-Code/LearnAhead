package com.example.learnahead_prototyp.UI.LearningCategory

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.databinding.FragmentLearningCategoryInnerViewBinding

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
        val binding = FragmentLearningCategoryInnerViewBinding.inflate(
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
            //holder.bind(item)
        }
    }

    fun updateList(list: MutableList<Summary>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return 1 // Return 1 to indicate a single view
    }

    inner class MyViewHolder(private val binding: FragmentLearningCategoryInnerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the data of a [Summary] item to the view.
         *
         * @param item The [Summary] object to bind.
         */
    }
}

package com.example.learnahead_prototyp.Goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.databinding.ItemGoalLayoutBinding

class GoalListingAdapter(
    val onItemClicked: (Int, Goal) -> Unit,
    val onEditClicked: (Int, Goal) -> Unit,
    val onDeleteClicked: (Int,Goal) -> Unit
) : RecyclerView.Adapter<GoalListingAdapter.MyViewHolder>() {

    private var list: MutableList<Goal> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemGoalLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<Goal>){
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val binding: ItemGoalLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Goal){
            binding.goalIdValue.setText(item.id)
            binding.msg.setText(item.description)
            binding.edit.setOnClickListener { onEditClicked.invoke(bindingAdapterPosition,item) }
            binding.delete.setOnClickListener { onDeleteClicked.invoke(bindingAdapterPosition,item) }
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(bindingAdapterPosition,item) }
        }
    }
}
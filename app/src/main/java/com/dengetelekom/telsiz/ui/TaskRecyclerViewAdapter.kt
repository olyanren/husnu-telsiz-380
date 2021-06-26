package com.dengetelekom.telsiz.ui

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.dengetelekom.telsiz.databinding.FragmentTaskBinding
import com.dengetelekom.telsiz.models.TaskModel

import com.dengetelekom.telsiz.ui.placeholder.PlaceholderContent.PlaceholderItem


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TaskRecyclerViewAdapter(
    private val values: List<TaskModel>, val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val index = position + 1;

        holder.name.text = item.name

        holder.title.text = "TUR - ${item.startDate}"
        holder.state.text = if(item.completedDate==null|| item.completedDate=="") "YAPILMADI" else "YAPILDI"
        holder.gecerlilik.text = index.toString() + "/" + values.size.toString()
        holder.bind(item, itemClickListener)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.textTitle

        val state: TextView = binding.textState

        val gecerlilik: TextView = binding.textGecerlilik
        val name: TextView = binding.textName
        private val btnStartTask: Button = binding.btnStartTask


        override fun toString(): String {
            return super.toString() + " '" + title.text + "'"
        }

        fun bind(item: TaskModel, itemClickListener: OnItemClickListener) {
            btnStartTask.setOnClickListener { itemClickListener.onImageStartClicked(item) }

        }
    }

    interface OnItemClickListener {
        fun onImageStartClicked(item: TaskModel?)

    }

}
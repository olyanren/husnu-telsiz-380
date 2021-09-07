package com.dengetelekom.telsiz.ui

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.dengetelekom.telsiz.R
import com.dengetelekom.telsiz.databinding.FragmentNotificationBinding

import com.dengetelekom.telsiz.databinding.FragmentTaskBinding
import com.dengetelekom.telsiz.models.NotificationModel
import com.dengetelekom.telsiz.models.TaskModel

import com.dengetelekom.telsiz.ui.placeholder.PlaceholderContent.PlaceholderItem


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class NotificationViewAdapter(
    private val values: List<NotificationModel>, val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NotificationViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.name.text = item.title
        holder.title.text = "${item.title}"
        holder.gecerlilik.text = item.createdAt
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.textTitle
        val gecerlilik: TextView = binding.textGecerlilik
        val name: TextView = binding.textName

        override fun toString(): String {
            return super.toString() + " '" + title.text + "'"
        }


    }

    interface OnItemClickListener {
        fun onImageStartClicked(item: TaskModel?)

    }

}
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
import com.dengetelekom.telsiz.helpers.DateConverter
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
        holder.content.text = item.title
        holder.title.text = item.content
        holder.createdAt.text = DateConverter(item.createdAt).date()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.textTitle
        val createdAt: TextView = binding.textCreatedAt
        val content: TextView = binding.textContent

        override fun toString(): String {
            return super.toString() + " '" + title.text + "'"
        }


    }

    interface OnItemClickListener {
        fun onImageStartClicked(item: TaskModel?)

    }

}
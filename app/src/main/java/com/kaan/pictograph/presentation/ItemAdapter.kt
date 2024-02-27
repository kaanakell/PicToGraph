package com.kaan.pictograph.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kaan.pictograph.R
import com.kaan.pictograph.databinding.ListItemSensorBinding


class ItemAdapter(val listener:ISensor) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    var list : List<Item> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ListItemSensorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = list[position]
            binding.textView.text = item.id
            binding.root.setOnClickListener {
                item.isSelected = !item.isSelected
                listener.onItemClick(item)
                notifyDataSetChanged()
            }
            if (item.isSelected){
                binding.textView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.black))
            }else {
                binding.textView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.list_grey))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemSensorBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
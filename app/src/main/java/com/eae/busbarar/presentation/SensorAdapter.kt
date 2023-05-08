package com.eae.busbarar.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ListItemSensorBinding


class SensorAdapter(val listener:ISensor) : RecyclerView.Adapter<SensorAdapter.ViewHolder>() {

    var list : List<SensorItem> = listOf()
        set(value) {
            var temp : List<SensorItem> = listOf()
            for(item in value) {
                if (temp.contains(item)){

                }else {
                    temp = temp + listOf(item)
                }
            }
            field = temp
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ListItemSensorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = list[position]
            binding.textView.text = item.sensorId
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
package com.eae.busbarar.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ListItemSensorBinding


class SensorAdapter(val listener:ISensor) : RecyclerView.Adapter<SensorAdapter.ViewHolder>() {

    var sensorClicks : ArrayList<String> = arrayListOf()

    var list : List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ListItemSensorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = list[position]
            binding.textView.text = item
            binding.root.setOnClickListener {
                if (sensorClicks.contains(item)){
                    sensorClicks.remove(item)
                }else{
                    sensorClicks.add(item)
                }
                listener.onItemClick(item)
                notifyItemChanged(position)
            }
            if (sensorClicks.contains(item)){
                binding.textView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.purple_200))
            }else {
                binding.textView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.transparent))
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
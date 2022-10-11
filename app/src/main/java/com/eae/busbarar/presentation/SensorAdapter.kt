package com.eae.busbarar.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eae.busbarar.databinding.ListItemSensorBinding


class SensorAdapter(val listener:ISensor) : RecyclerView.Adapter<SensorAdapter.ViewHolder>() {

    var list : List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(private val binding: ListItemSensorBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.textView.text = list[position]
            binding.root.setOnClickListener {
                listener.onItemClick(list[position])
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